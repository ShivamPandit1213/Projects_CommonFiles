# Paimana_Dev: Sample Code with Descriptions

A file-by-file reference for the `Paimana_Dev` Playwright project. For each file: **what it is for**, the **code**, and **what each part does**.

- **Part 1** documents the project's settings files exactly as uploaded.
- **Part 2** documents the framework and test files. The `src/` and `tests/` files are shown exactly as uploaded. `test-data/homePage.json` and the two `.github/` files weren't uploaded; they're shown as created during this project, so compare them with your own copy.
- **Part 3** shows a real HTML report from this project and explains how everything fits together.

---

## Contents

- [Project at a Glance](#project-at-a-glance)
- [Part 1: Project Settings Files](#part-1-project-settings-files)
  - [package.json](#packagejson)
  - [playwright.config.js](#playwrightconfigjs)
  - [.env.example](#envexample)
  - [eslint.config.js](#eslintconfigjs)
  - [.prettierrc.json and .prettierignore](#prettierrcjson-and-prettierignore)
  - [.editorconfig](#editorconfig)
  - [.gitattributes](#gitattributes)
  - [.gitignore](#gitignore)
  - [.nvmrc](#nvmrc)
  - [package-lock.json](#package-lockjson)
- [Part 2: Framework and Test Files](#part-2-framework-and-test-files)
  - [src/utils/env.js](#srcutilsenvjs)
  - [src/utils/testData.js](#srcutilstestdatajs)
  - [src/utils/window.js](#srcutilswindowjs)
  - [src/components/Footer.js](#srccomponentsfooterjs)
  - [src/pages/HomePage.js](#srcpageshomepagejs)
  - [src/fixtures/index.js](#srcfixturesindexjs)
  - [test-data/homePage.json](#test-datahomepagejson)
  - [tests/ui/home.spec.js](#testsuihomespecjs)
  - [tests/api/home.api.spec.js](#testsapihomeapispecjs)
  - [.github/workflows/playwright.yml](#githubworkflowsplaywrightyml)
  - [.github/dependabot.yml](#githubdependabotyml)
- [Part 3: How It Fits Together](#part-3-how-it-fits-together)
  - [Sample HTML Report](#sample-html-report-playwright-reportindexhtml)
  - [Run Flow](#run-flow)

---

## Project at a Glance

Playwright tests in JavaScript for the IIG/NEGD PAIMANA **dev** portal (`https://iigdev.uatnegd.online`). The sample tests are read-only: they check the home page, never log in and never submit anything.

| Item | Value |
|---|---|
| Language | JavaScript, ES modules (`"type": "module"`) |
| Runtime | Node.js 24 |
| Test framework | `@playwright/test` 1.63.0 |
| Design | Page Object Model + component objects + custom fixtures |
| Browsers | Chromium, Firefox, WebKit (UI tests); no browser for API tests |
| Code quality | ESLint 10 + `eslint-plugin-playwright`, Prettier 3 |
| CI | GitHub Actions; local Jenkins Freestyle job |

**Folder layout**

```text
Paimana_Dev/
├── .github/
│   ├── workflows/playwright.yml   CI: lint + format, then tests, upload report
│   └── dependabot.yml             weekly dependency-update pull requests
├── src/                           framework code (no tests here)
│   ├── components/Footer.js       component object shared by every page
│   ├── fixtures/index.js          test + expect with page-object fixtures
│   ├── pages/HomePage.js          page object for /home
│   └── utils/
│       ├── env.js                 loads .env into process.env
│       ├── testData.js            reads JSON from test-data/
│       └── window.js              asks Windows to maximize a browser window
├── tests/
│   ├── ui/home.spec.js            browser tests, run in every browser
│   └── api/home.api.spec.js       HTTP tests, no browser, run once
├── test-data/homePage.json        expected title, heading, quick links
├── playwright.config.js           baseURL, headless, workers, browser + api projects
├── eslint.config.js               code checks
├── .prettierrc.json / .prettierignore   formatting
├── .editorconfig / .gitattributes editor settings; LF line endings
├── .nvmrc                         Node version (24)
├── .env.example                   template for the git-ignored .env
├── .gitignore
├── package.json / package-lock.json
└── ProjectInfo.html               project documentation and change history
```

**Where new files go**

| You're adding… | Put it in |
|---|---|
| A new screen | `src/pages/` (e.g. `LoginPage.js`) |
| A part shown on several screens (header, menu, dialog) | `src/components/` |
| Tests for a new feature | a folder under `tests/ui/` (e.g. `tests/ui/login/`) |
| HTTP/API checks | `tests/api/` |
| Expected values or input data | `test-data/` (never secrets) |
| Login state for authenticated tests | a `tests/setup/auth.setup.js` project that saves to `playwright/.auth/` (already git-ignored) |

---

## Part 1: Project Settings Files

### package.json

**Purpose:** names the project, pins the Node version, defines the `npm run …` commands, and lists the development tools.

```json
{
  "name": "paimana-dev",
  "version": "1.0.0",
  "description": "Playwright (JavaScript) tests for the IIG/NEGD PAIMANA dev environment",
  "private": true,
  "type": "module",
  "engines": {
    "node": ">=24"
  },
  "scripts": {
    "test": "playwright test",
    "test:headed": "cross-env HEADLESS=false playwright test",
    "test:headless": "cross-env HEADLESS=true playwright test",
    "test:ui": "playwright test --project=chromium --project=firefox --project=webkit",
    "test:api": "playwright test --project=api",
    "test:chromium": "playwright test --project=chromium",
    "test:smoke": "playwright test --grep @smoke",
    "test:regression": "playwright test --grep @regression",
    "report": "playwright show-report",
    "lint": "eslint .",
    "lint:fix": "eslint . --fix",
    "format": "prettier --write .",
    "format:check": "prettier --check .",
    "check": "npm run lint && npm run format:check"
  },
  "devDependencies": {
    "@eslint/js": "^10.0.1",
    "@playwright/test": "^1.63.0",
    "@types/node": "^24.0.0",
    "cross-env": "^10.1.0",
    "eslint": "^10.12.0",
    "eslint-config-prettier": "^10.1.8",
    "eslint-plugin-playwright": "^2.12.1",
    "globals": "^17.13.0",
    "prettier": "^3.9.9"
  }
}
```

**Top-level fields**

| Field | Value | What it does |
|---|---|---|
| `name` / `version` / `description` | `paimana-dev`, `1.0.0`, … | Identify the project. `name` appears in npm output (`> paimana-dev@1.0.0 test:headed`). |
| `private` | `true` | Stops the project being published to the npm registry by accident. |
| `type` | `"module"` | Every `.js` file uses `import`/`export` (ES modules), not `require`. |
| `engines.node` | `>=24` | Documents the minimum Node version; `npm` warns on older ones. The exact version lives in `.nvmrc`. |

**Scripts:** run with `npm run <name>` (`npm test` is a shortcut for `test`).

| Script | Command | When to use |
|---|---|---|
| `test` | `playwright test` | All tests: UI in every browser plus API. Headless on CI, windows locally. |
| `test:headed` | `cross-env HEADLESS=false playwright test` | Maximized browser windows, even where `CI` is set. **Use this in Jenkins** for visible runs. |
| `test:headless` | `cross-env HEADLESS=true playwright test` | No windows; faster. |
| `test:ui` | `--project=chromium --project=firefox --project=webkit` | UI tests only. |
| `test:api` | `--project=api` | API tests only. |
| `test:chromium` | `--project=chromium` | UI tests in one browser, for quick local runs. |
| `test:smoke` | `--grep @smoke` | Only tests tagged `@smoke`. |
| `test:regression` | `--grep @regression` | Only tests tagged `@regression`. |
| `report` | `playwright show-report` | Opens the last HTML report in a browser. |
| `lint` / `lint:fix` | `eslint .` / `eslint . --fix` | Find (and auto-fix) code mistakes. |
| `format` / `format:check` | `prettier --write .` / `prettier --check .` | Fix / check formatting. |
| `check` | `npm run lint && npm run format:check` | Both checks; CI runs this before the tests. |

> **Why `cross-env`:** setting a variable for one command is written differently in cmd (`set X=1 &&`), PowerShell (`$env:X=1;`) and bash (`X=1`). `cross-env HEADLESS=false` works the same in all of them, so one script serves Windows, Jenkins and Linux CI.

**Passing extra Playwright options through a script:** put them after `--`, e.g. `npm run test:headed -- --workers=4` or `npm run test:chromium -- tests/ui/home.spec.js`.

**devDependencies**

| Package | Purpose |
|---|---|
| `@playwright/test` | The test runner, assertions and browser automation. |
| `@types/node` | Type information for Node, so VS Code can check `// @ts-check` files. |
| `cross-env` | Sets environment variables in npm scripts on any OS. |
| `eslint`, `@eslint/js`, `globals` | ESLint, its recommended rules, and the list of Node's global names. |
| `eslint-plugin-playwright` | Playwright-specific rules (missing `await`, `test.only`, hard waits). |
| `eslint-config-prettier` | Turns off ESLint rules that would conflict with Prettier. |
| `prettier` | Code formatter. |

The `^` in `^1.63.0` allows newer minor/patch versions when the lockfile is regenerated; `npm ci` always installs the exact versions in `package-lock.json`.

---

### playwright.config.js

**Purpose:** the central settings file. `npx playwright test` reads it automatically. It loads `.env`, decides headed/headless and the number of workers, sizes browser windows, and defines the browser and API projects.

The file is shown in five parts below; together they are the whole file.

#### 1. Imports and `.env` loading

```js
// @ts-check

// Settings for every test in tests/. `npx playwright test` reads this file
// automatically. Docs: https://playwright.dev/docs/test-configuration

import { defineConfig, devices } from '@playwright/test';
import { loadEnvFile } from './src/utils/env.js';

// Fill in variables from .env (see .env.example); terminal/CI values win.
loadEnvFile(new URL('.env', import.meta.url));
```

| Line | What it does |
|---|---|
| `// @ts-check` | Lets VS Code type-check this JavaScript file and underline mistakes. |
| `defineConfig` | Wraps the settings object so the editor offers auto-complete for every option. |
| `devices` | Ready-made browser presets such as `'Desktop Chrome'` (user agent, screen size, etc.). |
| `loadEnvFile(new URL('.env', import.meta.url))` | Reads `.env` next to this file into `process.env`, **before** the functions below read `HEADLESS` and `WORKERS`. Variables already set in the terminal or by Jenkins are not overwritten. `import.meta.url` makes the path relative to this file, not to the current folder. |

#### 2. `resolveHeadless()`: headed or headless

```js
/**
 * Headless or visible windows. Priority: HEADLESS env var > CI > visible.
 *
 * HEADLESS wins over CI so a CI server with a desktop (e.g. Jenkins on this
 * PC) can run headed with HEADLESS=false. Use that, not the --headed flag:
 * --headed forces windows open without this config knowing, so they keep the
 * fixed headless page size and are not maximized.
 * @returns {boolean}
 */
function resolveHeadless() {
  const fromEnv = process.env.HEADLESS?.trim().toLowerCase();
  if (fromEnv === 'true') return true;
  if (fromEnv === 'false') return false;
  if (fromEnv) {
    // Fail fast on a typo instead of silently picking a mode.
    throw new Error(`HEADLESS must be 'true' or 'false', got '${process.env.HEADLESS}'`);
  }
  return !!process.env.CI;
}

const HEADLESS = resolveHeadless();
```

| `HEADLESS` | `CI` | Result |
|---|---|---|
| `true` | any | Headless (no windows) |
| `false` | any | Headed (maximized windows) |
| not set | set (GitHub Actions, Jenkins) | Headless |
| not set | not set (your PC) | Headed |
| anything else (e.g. `yes`) | any | Run stops: `HEADLESS must be 'true' or 'false'` |

| Code | Why |
|---|---|
| `?.trim().toLowerCase()` | `?.` avoids an error when the variable isn't set; `trim` and `toLowerCase` accept `" TRUE "`. |
| `throw new Error(...)` | A typo stops the run immediately instead of silently choosing a mode. |
| `!!process.env.CI` | Converts "set to anything" into `true`, "not set" into `false`. |
| `const HEADLESS = …` | Computed once, then used by `windowSettings()` and `use.headless`. |

#### 3. `resolveWorkers()`: how many tests run at once

```js
/**
 * Number of parallel workers. Priority: WORKERS env var > CI (1) > Playwright's default.
 * WORKERS can be a whole number (4) or a percentage of CPU cores (50%).
 *
 * CI on its own means 1 worker, because CI servers are often small shared
 * machines. Jenkins on this PC is not, so set WORKERS there to run in parallel.
 * @returns {number | string | undefined}
 */
function resolveWorkers() {
  const value = process.env.WORKERS?.trim();
  if (!value) return process.env.CI ? 1 : undefined;
  if (/^[1-9]\d*%$/.test(value)) return value; // "50%" stays text
  if (/^[1-9]\d*$/.test(value)) return Number(value); // "4" becomes the number 4
  throw new Error(
    `WORKERS must be a number like 4 or a percentage like 50%, got '${process.env.WORKERS}'`,
  );
}
```

| `WORKERS` | `CI` | Returns | Effect |
|---|---|---|---|
| `4` | any | `4` (number) | 4 tests at a time |
| `50%` | any | `"50%"` (text) | Half the CPU cores |
| not set | set | `1` | One test at a time |
| not set | not set | `undefined` | Playwright's default: half the CPU cores |
| `0`, `0%`, `abc`, `4.5` | any | error | Run stops with a clear message |

| Code | Why |
|---|---|
| `/^[1-9]\d*%$/` | A whole number from 1 upwards followed by `%`. Rejects `0%`. |
| `/^[1-9]\d*$/` | A whole number from 1 upwards. Rejects `0` and decimals. |
| `Number(value)` | Environment variables are always text. Playwright accepts the number `4` or the text `"50%"`, but rejects the text `"4"` (`config.workers must be a number or percentage`). |

> **Jenkins batch steps:** a single `%` is special in `.bat` files, so write `set WORKERS=50%%` for 50 percent. `set WORKERS=50%` arrives as `50`, meaning 50 workers.

#### 4. `windowSettings()`: browser window size

```js
/**
 * Window settings for one browser project.
 *
 * Headed: every window is maximized, so its size comes from the real screen
 * (resolution and Windows display scaling) instead of a hard-coded number.
 * A fixed 1920x1080 page is larger than a 1920x1080 screen at 125% scaling
 * (1536x864 usable) and runs off the edges. viewport: null = page fills the window.
 *   Chromium: maximizes itself (--start-maximized).
 *   Firefox/WebKit: no such option, so Windows maximizes them
 *   (maximizeWindow → src/fixtures/index.js → src/utils/window.js).
 * Headless: there is no window or screen, so use a fixed Full-HD page size.
 *
 * @param {'chromium' | 'firefox' | 'webkit'} browser
 */
function windowSettings(browser) {
  if (HEADLESS) return { viewport: { width: 1920, height: 1080 } };
  const fillWindow = { viewport: null, deviceScaleFactor: undefined }; // scale factor must be unset with viewport null
  if (browser === 'chromium') {
    return { ...fillWindow, launchOptions: { args: ['--start-maximized'] } };
  }
  if (process.platform !== 'win32') return { viewport: { width: 1920, height: 1080 } };
  if (browser === 'firefox') {
    // Firefox on Windows ignores being maximized when several of its windows
    // overlap ("occlusion tracking"), as happens with parallel tests.
    return {
      ...fillWindow,
      maximizeWindow: true,
      launchOptions: {
        firefoxUserPrefs: { 'widget.windows.window_occlusion_tracking.enabled': false },
      },
    };
  }
  return { ...fillWindow, maximizeWindow: true };
}
```

| Situation | Settings returned | Result |
|---|---|---|
| Headless (any browser) | `viewport: 1920×1080` | Same page size every run, so screenshots are comparable. |
| Headed Chromium | `viewport: null` + `--start-maximized` | Chromium maximizes itself; the page fills the window. |
| Headed Firefox/WebKit on Windows | `viewport: null` + `maximizeWindow: true` | The fixture asks Windows to maximize the window (`src/utils/window.js`). |
| Headed Firefox/WebKit on macOS/Linux | `viewport: 1920×1080` | No OS maximize helper there, so a fixed size. |

| Setting | Why |
|---|---|
| `viewport: null` | "No fixed page size": the page takes whatever size the window is. |
| `deviceScaleFactor: undefined` | The device presets set a scale factor; Playwright refuses to start if one is set while `viewport` is `null`. |
| `maximizeWindow: true` | A **custom option** declared in `src/fixtures/index.js`, not a Playwright built-in. |
| `widget.windows.window_occlusion_tracking.enabled: false` | Firefox pauses windows it thinks are hidden behind others; with several parallel windows it then ignored being maximized. |

> **Why not a fixed 1920×1080 when headed:** on a 1920×1080 screen at 125% Windows scaling, a web page only gets about 1536×816 CSS pixels. A 1920×1080 page is 25% larger and runs off the screen.

#### 5. `defineConfig(...)`: the settings themselves

```js
export default defineConfig({
  testDir: './tests',
  fullyParallel: true,
  // Fail the build on CI if test.only was left in the code.
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: resolveWorkers(),
  reporter: [['list'], ['html', { open: 'never' }]],
  timeout: 60_000,

  use: {
    // Every page.goto('/path') is relative to this. Override with BASE_URL.
    baseURL: process.env.BASE_URL ?? 'https://iigdev.uatnegd.online',
    headless: HEADLESS,
    actionTimeout: 30_000,
    navigationTimeout: 30_000,
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
  },

  // UI tests run once per browser; API tests need no browser and run once.
  // Window size per browser: see windowSettings() above.
  projects: [
    {
      name: 'chromium',
      testDir: './tests/ui',
      use: { ...devices['Desktop Chrome'], ...windowSettings('chromium') },
    },
    {
      name: 'firefox',
      testDir: './tests/ui',
      use: { ...devices['Desktop Firefox'], ...windowSettings('firefox') },
    },
    {
      name: 'webkit',
      testDir: './tests/ui',
      use: { ...devices['Desktop Safari'], ...windowSettings('webkit') },
    },
    { name: 'api', testDir: './tests/api' },
  ],
});
```

**Run settings**

| Setting | Value | What it does |
|---|---|---|
| `testDir` | `./tests` | Default test folder (each project narrows it). |
| `fullyParallel` | `true` | Tests inside one file can also run in parallel, not just separate files. |
| `forbidOnly` | `true` on CI | A forgotten `test.only` fails the CI run instead of silently skipping the other tests. |
| `retries` | `2` on CI, `0` locally | Re-runs a failed test on CI to tell real failures from flaky ones; locally you see failures at once. |
| `workers` | `resolveWorkers()` | Parallel test count (section 3). |
| `reporter` | `list` + `html` | `list`: one line per test in the terminal. `html`: writes `playwright-report/`; `open: 'never'` stops it popping up after every failed local run. Jenkins publishes this folder. |
| `timeout` | `60_000` | Maximum 60 s per test (the `_` is just a readable digit separator). |

**`use`: settings shared by every test**

| Setting | Value | What it does |
|---|---|---|
| `baseURL` | `BASE_URL` or the dev portal | `page.goto('/home')` becomes `https://iigdev.uatnegd.online/home`. Point at another environment with `BASE_URL`. `??` only falls back when `BASE_URL` isn't set. |
| `headless` | `HEADLESS` | Window or no window (section 2). |
| `actionTimeout` | `30_000` | Max wait for one click, fill, etc. |
| `navigationTimeout` | `30_000` | Max wait for `page.goto` and navigations. |
| `trace` | `'on-first-retry'` | Records a step-by-step trace when a test is retried, for debugging CI failures. |
| `screenshot` | `'only-on-failure'` | Attaches a screenshot to failed tests in the report. |

**`projects`: what runs where**

| Project | Tests from | Browser | Runs |
|---|---|---|---|
| `chromium` | `tests/ui` | Desktop Chrome preset + window settings | every UI test |
| `firefox` | `tests/ui` | Desktop Firefox preset + window settings | every UI test |
| `webkit` | `tests/ui` | Desktop Safari preset + window settings | every UI test |
| `api` | `tests/api` | none (HTTP only) | every API test, once |

With 2 UI tests and 1 API test, a full run is 2 × 3 + 1 = **7 tests**.

`...devices['Desktop Chrome'], ...windowSettings('chromium')`: the spread `...` merges both objects; later keys win, so the window settings override the preset's fixed viewport.

---

### .env.example

**Purpose:** a template for the local `.env` file. Copy it to `.env` and fill in values; `.env` itself is git-ignored so secrets never reach GitHub.

```ini
# Copy to .env (git-ignored) and adjust. Terminal / CI variables override these.

# Portal under test.
BASE_URL=https://iigdev.uatnegd.online

# true = no browser windows, false = maximized windows. Unset: headless on CI, windows locally.
# Wins over CI, so a CI server with a desktop (Jenkins on this PC) can set HEADLESS=false.
HEADLESS=false

# Parallel workers: a number (4) or a share of CPU cores (50%). Unset: 1 on CI, Playwright's default locally.
# Wins over CI, so Jenkins on this PC can run in parallel.
WORKERS=

# For future login tests. Put real values in .env only, never in this file.
PAIMANA_USERNAME=
PAIMANA_PASSWORD=
```

| Variable | Sample value | Read by | Effect |
|---|---|---|---|
| `BASE_URL` | `https://iigdev.uatnegd.online` | `use.baseURL` | Which environment the tests hit. |
| `HEADLESS` | `false` | `resolveHeadless()` | `true` = no windows, `false` = maximized windows. |
| `WORKERS` | *(empty)* or `4` | `resolveWorkers()` | Parallel tests. Empty counts as not set. |
| `PAIMANA_USERNAME` / `PAIMANA_PASSWORD` | *(empty)* | future login tests | Real values only in `.env` or Jenkins credentials. |

**Priority:** a value set in the terminal or by Jenkins wins over `.env`, which wins over the defaults in the config.

```powershell
Copy-Item .env.example .env     # PowerShell: create your local .env
```

---

### eslint.config.js

**Purpose:** ESLint settings. ESLint finds likely bugs (unused variables, misspelt names, missing `await`). Formatting is left to Prettier.

```js
// @ts-check

// ESLint checks the code for mistakes: `npm run lint` (or `npm run lint:fix`).
// Formatting is Prettier's job, not ESLint's.

import js from '@eslint/js';
import prettier from 'eslint-config-prettier';
import playwright from 'eslint-plugin-playwright';
import globals from 'globals';

export default [
  { ignores: ['node_modules/', 'playwright-report/', 'test-results/', 'blob-report/'] },

  js.configs.recommended,

  {
    languageOptions: {
      ecmaVersion: 'latest',
      sourceType: 'module',
      globals: globals.node,
    },
  },

  // Playwright rules: missing await, forgotten test.only, hard waits, etc.
  {
    ...playwright.configs['flat/recommended'],
    files: ['tests/**/*.js', 'src/fixtures/**/*.js'],
  },

  // Last: switch off rules that would fight with Prettier.
  prettier,
];
```

ESLint applies the array entries in order; later entries can override earlier ones.

| Entry | What it does |
|---|---|
| `ignores` | Never lint installed packages or generated reports. |
| `js.configs.recommended` | ESLint's core rules: unused variables, undefined names, unreachable code, irregular whitespace, etc. |
| `languageOptions` | Latest JavaScript syntax, ES modules, and Node's globals (`process`, `URL`, …) so they aren't reported as undefined. |
| Playwright recommended, limited to `tests/` and `src/fixtures/` | Catches a missing `await` on Playwright calls, a forgotten `test.only`, `page.waitForTimeout()` hard waits, `expect` outside a test, etc. |
| `prettier` (last) | Turns off every ESLint rule about spacing or quotes, so the two tools never disagree. |

> Example of a real catch: an invisible byte-order-mark character typed inside a regex in `env.js` was reported as `no-irregular-whitespace` and replaced with the `\uFEFF` escape.

---

### .prettierrc.json and .prettierignore

**Purpose:** Prettier reformats code to one consistent style, so diffs show real changes only.

```json
{
  "singleQuote": true,
  "printWidth": 100,
  "trailingComma": "all",
  "endOfLine": "lf"
}
```

| Option | Value | Effect |
|---|---|---|
| `singleQuote` | `true` | `'text'` instead of `"text"` (JSON files keep double quotes). |
| `printWidth` | `100` | Wraps lines longer than 100 characters. |
| `trailingComma` | `"all"` | Comma after the last item in multi-line lists, so adding an item changes one line, not two. |
| `endOfLine` | `"lf"` | Unix line endings everywhere; matches `.gitattributes` and `.editorconfig`. |

```gitignore
# Generated or installed
node_modules/
package-lock.json
playwright-report/
test-results/
blob-report/

# Hand-formatted, shares its layout with every ProjectInfo.html in the workspace.
ProjectInfo.html
```

`.prettierignore` lists files Prettier must not touch: installed packages, the npm-generated lockfile, test output, and `ProjectInfo.html`, which is laid out by hand.

---

### .editorconfig

**Purpose:** basic editor settings every editor applies the same way (VS Code needs the *EditorConfig for VS Code* extension).

```ini
# Editor settings shared by everyone who opens this project. https://editorconfig.org
root = true

[*]
charset = utf-8
end_of_line = lf
indent_style = space
indent_size = 2
insert_final_newline = true
trim_trailing_whitespace = true
```

| Setting | Effect |
|---|---|
| `root = true` | Stop looking for `.editorconfig` files in parent folders. |
| `[*]` | Applies to every file. |
| `charset = utf-8` | Save files as UTF-8. |
| `end_of_line = lf` | Unix line endings. |
| `indent_style` / `indent_size` | Two spaces, no tabs (Prettier's default). |
| `insert_final_newline` | Every file ends with a newline. |
| `trim_trailing_whitespace` | Removes spaces at line ends on save. |

---

### .gitattributes

**Purpose:** tells Git how to store line endings.

```gitattributes
# Store text files with LF line endings on every OS, so format:check passes on Windows.
* text=auto eol=lf

*.png binary
*.jpg binary
*.pdf binary
*.zip binary
```

| Line | Effect |
|---|---|
| `* text=auto eol=lf` | Git detects text files and checks them out with LF endings, even on Windows. Without it, Windows checkouts get CRLF and `npm run format:check` fails on every file. |
| `*.png binary` etc. | Never touch line endings in images, PDFs or archives. |

---

### .gitignore

**Purpose:** files Git must never commit. A leading `/` means "only at the project root".

```gitignore
# Installed npm packages; recreated by `npm ci` from package-lock.json.
node_modules/

# Playwright output
/test-results/
/playwright-report/
/blob-report/
/playwright/.cache/
# Saved login state; can contain session cookies, so never commit it.
/playwright/.auth/

# Local settings and secrets (template: .env.example, which IS committed).
.env
.env.*
!.env.example
```

| Entry | Why ignored |
|---|---|
| `node_modules/` | Large and recreated by `npm ci`. |
| `/test-results/` | Screenshots, traces and videos of the last run. |
| `/playwright-report/` | The HTML report; Jenkins archives it per build instead. |
| `/blob-report/` | Partial reports from sharded runs. |
| `/playwright/.cache/` | Playwright's cache. |
| `/playwright/.auth/` | Saved login sessions; contain cookies. |
| `.env`, `.env.*` | Local settings and secrets. |
| `!.env.example` | The `!` re-includes the template, so it **is** committed. |

---

### .nvmrc

**Purpose:** the Node.js version for this project.

```text
24
```

Read by `nvm use` / `fnm use` locally and by `actions/setup-node` (`node-version-file: .nvmrc`) in GitHub Actions, so everyone runs the same major version.

---

### package-lock.json

**Purpose:** generated by npm. Records the **exact** version of every package and sub-package that was installed. Don't edit it by hand; commit it.

| Command | Effect on the lockfile |
|---|---|
| `npm ci` | Installs exactly what the lockfile says; fails if it doesn't match `package.json`. Use in Jenkins and CI. |
| `npm install` | May update the lockfile. Use when adding or upgrading packages. |
| `npm install --save-dev <pkg>` | Adds a package and updates both files. |

Versions locked at the time of writing:

| Package | Locked version |
|---|---|
| `@playwright/test` / `playwright` | 1.63.0 |
| `eslint` | 10.12.0 |
| `prettier` | 3.9.9 |
| `cross-env` | 10.1.0 |

---

## Part 2: Framework and Test Files

### src/utils/env.js

**Purpose:** loads `.env` into `process.env` without the `dotenv` package.

```js
// @ts-check

import { existsSync, readFileSync } from 'node:fs';
import { parseEnv } from 'node:util';

/**
 * Copies variables from a .env file into process.env, if the file exists.
 * Variables already set in the terminal or on CI are kept (??= only fills gaps).
 *
 * @param {URL | string} path location of the .env file
 */
export function loadEnvFile(path) {
  if (!existsSync(path)) return;
  // Strip a UTF-8 byte-order mark: Windows PowerShell 5.1 and Notepad can add
  // one, and it silently corrupts the first variable's name.
  const vars = parseEnv(readFileSync(path, 'utf8').replace(/^\uFEFF/, ''));
  for (const [key, value] of Object.entries(vars)) process.env[key] ??= value;
}
```

| Code | What it does |
|---|---|
| `existsSync(path)` | No `.env` (e.g. on CI) is fine; the function just returns. |
| `parseEnv(...)` | Node 24's built-in `.env` parser. |
| `.replace(/^\uFEFF/, '')` | Removes a byte-order mark that Notepad or PowerShell 5.1 may add, which would otherwise turn `BASE_URL` into an invisible-character name. |
| `process.env[key] ??= value` | Sets the variable only if it isn't already set, so terminal and Jenkins values win. |

---

### src/utils/testData.js

**Purpose:** reads a JSON file from `test-data/`.

```js
// @ts-check

import { readFileSync } from 'node:fs';

/**
 * Reads a JSON file from test-data/ at the project root.
 *
 * @param {string} name file name without folder, e.g. 'homePage.json'
 * @returns {any} the parsed JSON
 */
export function readTestData(name) {
  const file = new URL(`../../test-data/${name}`, import.meta.url);
  return JSON.parse(readFileSync(file, 'utf8'));
}
```

`new URL('../../test-data/…', import.meta.url)` resolves the path from this file's location (`src/utils/`), so it works whatever folder the tests are started from.

---

### src/utils/window.js

**Purpose:** `maximizeOsWindow(page)` maximizes a headed Firefox or WebKit window on Windows, like double-clicking its title bar. Playwright can maximize Chromium (`--start-maximized`), but its Firefox and WebKit builds have no such option, and a web page isn't allowed to resize its own window, so the code asks Windows to do it.

```js
// @ts-check

// The page.evaluate() callbacks below run inside the browser, where these exist.
/* global window, document */

/**
 * Maximizes a test's browser window through Windows itself, like
 * double-clicking the title bar.
 *
 * Why this exists: Playwright can maximize Chromium (--start-maximized), but
 * its Firefox and WebKit builds have no such option, and a web page is not
 * allowed to move or maximize its own window. So on Windows we ask the OS.
 *
 * How it finds the right window: tests run in parallel, so several browser
 * windows can be opening at once, and a brand-new window may not be known to
 * Windows yet. The page therefore gets a unique temporary title; PowerShell
 * waits until the window with that title exists, maximizes exactly that
 * window, and confirms it really is maximized before returning.
 */

import { execFile } from 'node:child_process';
import { randomUUID } from 'node:crypto';
import { promisify } from 'node:util';

const execFileAsync = promisify(execFile);

/** How long PowerShell keeps looking for the window, in seconds. */
const TIMEOUT_SECONDS = 10;

/** Maximize attempts per window, and how long each waits for the page to resize. */
const ATTEMPTS = 3;
const RESIZE_WAIT_MS = 3_000;

/** Exit codes of the PowerShell script, and what they mean. */
const SCRIPT_RESULT = {
  2: 'no window with the marker title appeared',
  3: 'the window was found but did not stay maximized',
};

/**
 * PowerShell that scans every visible top-level window (user32 EnumWindows)
 * for the one whose title contains `marker`, maximizes it (ShowWindow,
 * SW_MAXIMIZE = 3) and checks the result (IsZoomed). Retries until it works
 * or TIMEOUT_SECONDS pass. Exit code 0 = maximized, otherwise see SCRIPT_RESULT.
 * @param {string} marker
 */
function maximizeScript(marker) {
  return `
Add-Type -TypeDefinition @'
using System;
using System.Text;
using System.Runtime.InteropServices;
public static class PlaywrightWindow {
  delegate bool EnumProc(IntPtr hWnd, IntPtr lParam);
  [DllImport("user32.dll")] static extern bool EnumWindows(EnumProc callback, IntPtr lParam);
  [DllImport("user32.dll")] static extern bool IsWindowVisible(IntPtr hWnd);
  [DllImport("user32.dll", CharSet = CharSet.Unicode)] static extern int GetWindowText(IntPtr hWnd, StringBuilder text, int max);
  [DllImport("user32.dll")] public static extern bool ShowWindow(IntPtr hWnd, int nCmdShow);
  [DllImport("user32.dll")] public static extern bool IsZoomed(IntPtr hWnd);
  public static IntPtr Find(string marker) {
    IntPtr found = IntPtr.Zero;
    EnumWindows((hWnd, lParam) => {
      if (!IsWindowVisible(hWnd)) return true;
      var text = new StringBuilder(512);
      GetWindowText(hWnd, text, text.Capacity);
      if (text.ToString().Contains(marker)) { found = hWnd; return false; }
      return true;
    }, IntPtr.Zero);
    return found;
  }
}
'@
$result = 2
$deadline = (Get-Date).AddSeconds(${TIMEOUT_SECONDS})
while ((Get-Date) -lt $deadline) {
  $hWnd = [PlaywrightWindow]::Find('${marker}')
  if ($hWnd -ne [IntPtr]::Zero) {
    $result = 3
    [void][PlaywrightWindow]::ShowWindow($hWnd, 3)
    Start-Sleep -Milliseconds 150
    if ([PlaywrightWindow]::IsZoomed($hWnd)) { exit 0 }
  } else {
    Start-Sleep -Milliseconds 100
  }
}
exit $result
`;
}

/**
 * Maximizes the window showing `page`. Windows only; does nothing elsewhere.
 * Call it on a fresh page, before the test navigates.
 *
 * Never throws: a window that stays un-maximized is cosmetic, so a failure is
 * logged as a warning instead of failing the test.
 *
 * @param {import('@playwright/test').Page} page
 */
export async function maximizeOsWindow(page) {
  if (process.platform !== 'win32') return;

  const marker = `pw-maximize-${randomUUID()}`;
  const widthBefore = await page.evaluate((title) => {
    document.title = title;
    return window.innerWidth;
  }, marker);
  let reason = '';
  try {
    for (let attempt = 1; attempt <= ATTEMPTS; attempt++) {
      try {
        await execFileAsync(
          'powershell.exe',
          [
            '-NoProfile',
            '-NonInteractive',
            '-ExecutionPolicy',
            'Bypass',
            '-Command',
            maximizeScript(marker),
          ],
          { windowsHide: true, timeout: (TIMEOUT_SECONDS + 15) * 1000 },
        );
      } catch (error) {
        // Window not found / not maximized: retrying will not help.
        const code = /** @type {{ code?: number }} */ (error).code;
        reason = (code && SCRIPT_RESULT[/** @type {2 | 3} */ (code)]) || String(error);
        break;
      }
      // Windows has maximized the window, but a busy browser (several tests in
      // parallel) may not resize the page inside it. Check that the page
      // really grew, so the test starts at full size; otherwise try again.
      const resized = await page
        .waitForFunction((width) => window.innerWidth > width, widthBefore, {
          timeout: RESIZE_WAIT_MS,
        })
        .then(() => true)
        .catch(() => false);
      if (resized) return;
      reason = `the page did not resize after ${attempt} attempt(s)`;
    }
    console.warn(`[maximizeOsWindow] window not maximized: ${reason}`);
  } finally {
    await page.evaluate(() => {
      document.title = '';
    });
  }
}
```

**How it works, step by step**

1. **Skip other systems:** `process.platform !== 'win32'` returns at once on macOS/Linux.
2. **Mark the window:** gives the page a unique title such as `pw-maximize-3f2a…` (`randomUUID()`), and records the page's current width. Tests run in parallel, so several browser windows can be open at once; the unique title identifies exactly this one.
3. **Ask Windows to maximize it:** runs a PowerShell script (`execFileAsync('powershell.exe', …)`) that:
   1. compiles a small C# helper calling `user32.dll`;
   2. loops through every visible window (`EnumWindows`) looking for the marker title (`GetWindowText`);
   3. maximizes it (`ShowWindow(hWnd, 3)`, where 3 = `SW_MAXIMIZE`);
   4. confirms it with `IsZoomed`, retrying for up to 10 seconds, because a brand-new window may not be known to Windows yet.
4. **Check the page grew:** `page.waitForFunction(width => window.innerWidth > width, …)` waits up to 3 s. A busy browser may maximize the frame without resizing the page inside it, so up to 3 attempts are made.
5. **Clean up:** the `finally` block clears the temporary title, whatever happened.

**Constants**

| Constant | Value | Meaning |
|---|---|---|
| `TIMEOUT_SECONDS` | `10` | How long PowerShell keeps looking for the window. |
| `ATTEMPTS` | `3` | Maximize attempts when the page doesn't resize. |
| `RESIZE_WAIT_MS` | `3_000` | How long each attempt waits for the page to grow. |
| `SCRIPT_RESULT` | exit codes `2`, `3` | `2` = no window with the marker title appeared; `3` = found but didn't stay maximized. `0` = success. |

**Code details**

| Code | Why |
|---|---|
| `/* global window, document */` | The `page.evaluate()` callbacks run inside the browser, where `window` and `document` exist. This tells ESLint they're defined. |
| `promisify(execFile)` | Turns Node's callback-style `execFile` into one that works with `await`. |
| `-NoProfile -NonInteractive -ExecutionPolicy Bypass` | Starts PowerShell fast, without user profile scripts or prompts, and allows the inline script to run. |
| `windowsHide: true` | No PowerShell console window flashes up. |
| `timeout: (TIMEOUT_SECONDS + 15) * 1000` | Kills PowerShell if it hangs (25 s: the 10 s search plus start-up time for compiling the C# helper). |
| Inner `catch` → `break` | Exit code 2 or 3 means the window can't be found or maximized; retrying won't help. |
| `console.warn(...)` instead of `throw` | **Never fails a test.** An un-maximized window is cosmetic, so it only prints `[maximizeOsWindow] window not maximized: <reason>`. |

**When it's called:** from the `page` fixture in `src/fixtures/index.js`, before the test navigates, when the `maximizeWindow` option is `true`. That's set by `windowSettings()` in `playwright.config.js` for headed Firefox and WebKit on Windows.

**Cost:** each call starts PowerShell and compiles the C# helper, which adds roughly one or more seconds per test. Headless runs and Chromium skip it entirely.

---
### src/components/Footer.js

**Purpose:** a **component object** for the site footer, which appears on every page. Page objects hold an instance, so a footer change is fixed in one place.

```js
// @ts-check

/**
 * Component object for the site footer, shared by every page.
 *
 * Parts that repeat across pages (header, footer, menus, dialogs) get their own
 * class, and page objects hold an instance, so a footer change is fixed once.
 */
export class Footer {
  /**
   * @param {import('@playwright/test').Page} page
   */
  constructor(page) {
    // "contentinfo" is the accessibility role of <footer>.
    this.root = page.getByRole('contentinfo');
    this.quickLinks = this.root.getByRole('list').getByRole('link');
  }
}
```

| Locator | Finds |
|---|---|
| `root` | The `<footer>` element, by its accessibility role `contentinfo`. |
| `quickLinks` | Every link inside the footer's list. |

Locators use **roles**, not CSS classes, so they keep working when the site is restyled.

---

### src/pages/HomePage.js

**Purpose:** the **page object** for `/home`: its address, the elements tests use, and how to open it. Tests never write selectors themselves.

```js
// @ts-check

import { Footer } from '../components/Footer.js';

/**
 * Page object for the portal home page (/home).
 *
 * One class per screen: it owns the URL and locators, so tests never write
 * selectors themselves. Locators use roles and accessible names, not CSS.
 */
export class HomePage {
  /** Path relative to baseURL in playwright.config.js. */
  static PATH = '/home';

  /**
   * @param {import('@playwright/test').Page} page
   */
  constructor(page) {
    this.page = page;
    this.heading = page.getByRole('main').getByRole('heading', { level: 1 });
    this.footer = new Footer(page);
  }

  /**
   * Opens the home page; returns the server's response.
   *
   * Waits for DOMContentLoaded, not the default 'load': a third-party font
   * (cdn.svar.dev roboto/regular.woff) never finishes in Firefox, so 'load'
   * never fires. Assertions wait for their own elements anyway.
   */
  async open() {
    return this.page.goto(HomePage.PATH, { waitUntil: 'domcontentloaded' });
  }
}
```

| Member | What it is |
|---|---|
| `static PATH` | `/home`, joined to `baseURL`. |
| `heading` | The page's `<h1>` inside the main area. |
| `footer` | A `Footer` component object (`homePage.footer.quickLinks`). |
| `open()` | Navigates to the page and returns the HTTP response. |

> **Why `waitUntil: 'domcontentloaded'`:** the portal loads a font that never finishes downloading in Firefox, so the browser's `load` event never fires and the default `page.goto()` times out after 30 s. The page content is ready in about 1.3 s; assertions then wait for each element they check.

**Adding a page:** create `src/pages/LoginPage.js` in the same shape (a `PATH`, locators in the constructor, action methods such as `login(user, pass)`), then add a fixture for it in `src/fixtures/index.js`.

---

### src/fixtures/index.js

**Purpose:** extends Playwright's `test` with ready-made page objects and the window-maximize option. UI tests import `test` and `expect` from here, not from `@playwright/test`.

```js
// @ts-check

/**
 * Custom fixtures. Specs import `test` and `expect` from here, not from
 * '@playwright/test', and receive page objects as arguments:
 *
 *   test('...', async ({ homePage }) => { await homePage.open(); });
 *
 * To add a page object: import it and add one entry to base.extend.
 * Docs: https://playwright.dev/docs/test-fixtures
 */

import { test as base, expect } from '@playwright/test';
import { HomePage } from '../pages/HomePage.js';
import { maximizeOsWindow } from '../utils/window.js';

export const test = base.extend({
  // Option, set per browser project in playwright.config.js: true when the OS
  // must maximize the window (headed Firefox/WebKit on Windows). Chromium
  // maximizes itself with --start-maximized.
  maximizeWindow: [false, { option: true }],

  // Wraps Playwright's own `page`: maximize the new window before the test runs.
  page: async ({ page, maximizeWindow }, use) => {
    if (maximizeWindow) await maximizeOsWindow(page);
    await use(page);
  },

  homePage: async ({ page }, use) => {
    await use(new HomePage(page));
  },
});

export { expect };
```

| Fixture | Kind | What it does |
|---|---|---|
| `maximizeWindow` | option, default `false` | Set to `true` per project by `windowSettings()` in the config. |
| `page` | override of the built-in | Every test gets a fresh browser window; this maximizes it first when the option is on. |
| `homePage` | new fixture | A `HomePage` built on that `page`, handed to any test that lists `homePage` in its arguments. |

`await use(x)` hands `x` to the test and waits until the test finishes; code after it would run as clean-up. Each fixture is created fresh for every test.

**Adding a page object:**

```js
import { LoginPage } from '../pages/LoginPage.js';
// inside base.extend({ ... }):
loginPage: async ({ page }, use) => {
  await use(new LoginPage(page));
},
```

---

### test-data/homePage.json

**Purpose:** the expected values the UI tests check against, kept out of the test code. No secrets in this folder.

```json
{
  "title": "PAIMANA",
  "heading": "Central Repository of Projects",
  "quickLinks": [
    "Home",
    "Contact Us",
    "FAQs",
    "Site Map",
    "Hyperlinking Policy",
    "Privacy Policy",
    "User Manual"
  ]
}
```

| Key | Used for |
|---|---|
| `title` | The browser tab title. |
| `heading` | The page's `<h1>` text. |
| `quickLinks` | The 7 footer links, **in page order**. |

When the portal's text changes, update this file instead of the tests.

---

### tests/ui/home.spec.js

**Purpose:** read-only browser tests for the home page. Run once in each browser project.

```js
// @ts-check

// Read-only UI checks of the portal home page: no login, nothing submitted.

import { test, expect } from '../../src/fixtures/index.js';
import { readTestData } from '../../src/utils/testData.js';

const expected = readTestData('homePage.json');

test.describe('Home page', () => {
  test.beforeEach(async ({ homePage }) => {
    await homePage.open();
  });

  test(
    'loads with the PAIMANA title and heading',
    { tag: '@smoke' },
    async ({ homePage, page }) => {
      await expect(page).toHaveURL(/\/home/);
      await expect(page).toHaveTitle(expected.title);
      await expect(homePage.heading).toHaveText(expected.heading);
    },
  );

  test('footer shows the quick links in order', { tag: '@regression' }, async ({ homePage }) => {
    await expect(homePage.footer.quickLinks).toHaveText(expected.quickLinks);
  });
});
```

| Part | What it does |
|---|---|
| `import { test, expect } from '../../src/fixtures/index.js'` | Uses the custom `test`, so `homePage` is available. |
| `test.describe('Home page', …)` | Groups the tests under one heading in reports. |
| First `test(` split over several lines | Only because the line is longer than Prettier's 100-character limit; it works exactly like the one-line form below it. |
| `test.beforeEach` | Opens the home page before each test. |
| `{ tag: '@smoke' }` / `{ tag: '@regression' }` | Lets `npm run test:smoke` / `test:regression` pick tests. |
| `toHaveURL(/\/home/)` | Catches a redirect to an error or maintenance page. |
| `toHaveTitle` / `toHaveText` | Web-first assertions: they **retry** until the condition holds or the timeout ends, so no manual waits are needed. |
| `toHaveText(expected.quickLinks)` with an array | Checks the number of links, their text and their order in one assertion. |

---

### tests/api/home.api.spec.js

**Purpose:** a plain HTTP check, no browser. Runs once in the `api` project and finishes in milliseconds.

```js
// @ts-check

// API-level checks: plain HTTP requests, no browser, so they run once (the
// "api" project in playwright.config.js) and finish in milliseconds.

import { test, expect } from '@playwright/test';

test('GET /home returns an HTML page', { tag: '@smoke' }, async ({ request }) => {
  const response = await request.get('/home');

  expect(response.status(), 'HTTP status').toBe(200);
  expect(response.headers()['content-type']).toContain('text/html');
});
```

| Part | What it does |
|---|---|
| `import … from '@playwright/test'` | No page objects needed, so the plain `test` is enough. |
| `{ request }` | Playwright's built-in HTTP client; uses `baseURL` like `page.goto`. |
| `expect(…, 'HTTP status')` | The second argument is the message shown if the check fails. |
| `.toBe(200)` / `.toContain('text/html')` | The server answered OK and sent a web page. |

---

### .github/workflows/playwright.yml

**Purpose:** GitHub Actions CI. On every push or pull request to `main`/`master`, checks code quality, then runs all tests on a fresh Linux machine.

```yaml
# Runs the Playwright tests on GitHub's servers after every push or pull request.
name: Playwright Tests

on:
  push:
    branches: [main, master]
  pull_request:
    branches: [main, master]

jobs:
  test:
    timeout-minutes: 60
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v7
      - uses: actions/setup-node@v7
        with:
          node-version-file: .nvmrc
          cache: npm
      - name: Install dependencies
        run: npm ci
      # ESLint + Prettier; fails fast, before spending time on browsers.
      - name: Lint and format check
        run: npm run check
      - name: Install Playwright browsers
        run: npx playwright install --with-deps
      # CI=true is set automatically: headless, retries, one worker.
      - name: Run Playwright tests
        run: npm test
      # Upload the report even when tests failed; that is when it is needed.
      - uses: actions/upload-artifact@v7
        if: ${{ !cancelled() }}
        with:
          name: playwright-report
          path: playwright-report/
          retention-days: 30
```

| Step | What it does |
|---|---|
| `on: push / pull_request` | When the workflow runs. |
| `runs-on: ubuntu-latest` | A fresh Linux machine each run. |
| `actions/checkout` | Downloads the repository. |
| `actions/setup-node` + `node-version-file: .nvmrc` | Installs Node 24; `cache: npm` speeds up later runs. |
| `npm ci` | Exact dependencies from the lockfile. |
| `npm run check` | ESLint + Prettier; a style or lint error stops the run before browsers are downloaded. |
| `npx playwright install --with-deps` | Browsers plus the Linux libraries they need. |
| `npm test` | GitHub sets `CI=true`: headless, 2 retries, 1 worker, `test.only` forbidden. |
| `upload-artifact` with `!cancelled()` | Saves `playwright-report/` for 30 days, even when tests failed. Download it from the run's page. |

---

### .github/dependabot.yml

**Purpose:** Dependabot opens pull requests when newer versions are available, so updates arrive as reviewable changes that CI tests first.

```yaml
# Dependabot opens a pull request when a dependency has a newer version.
# Docs: https://docs.github.com/code-security/dependabot
version: 2
updates:
  - package-ecosystem: npm
    directory: /
    schedule:
      interval: weekly
    # One PR per week for all minor/patch updates.
    groups:
      minor-and-patch:
        update-types: [minor, patch]

  - package-ecosystem: github-actions
    directory: /
    schedule:
      interval: weekly
```

| Entry | What it watches |
|---|---|
| `npm`, weekly, grouped | Packages in `package.json`. Minor and patch updates arrive together in one PR a week; major updates get their own PR. |
| `github-actions`, weekly | Versions of `actions/checkout`, `setup-node`, `upload-artifact`. |

---

## Part 3: How It Fits Together

### Sample HTML Report (playwright-report/index.html)

**Purpose:** the report Playwright's `html` reporter writes after every run. It's **generated**, so it's git-ignored and never edited by hand. Jenkins archives it with *Publish HTML reports*.

It's a single self-contained file (about 520 KB here): the report viewer's JavaScript plus the results, packed as a base64-encoded zip inside the page. That's why it needs JavaScript to show anything, and why it opens blank in Jenkins until the Content Security Policy is relaxed (see `JenkinsJobTypesSetup.md`, Part E.4).

**Results stored in the uploaded report**

| Summary | Value |
|---|---|
| Result | ✅ 7 passed, 0 failed, 0 flaky, 0 skipped |
| Total duration | 36.9 s |
| Workers | 6 (a local run: `CI` not set, no `WORKERS`) |
| Projects | chromium, firefox, webkit, api |

| Spec file | Project | Test | Tag | Duration |
|---|---|---|---|---|
| `ui/home.spec.js` | chromium | loads with the PAIMANA title and heading | `@smoke` | 7.8 s |
| `ui/home.spec.js` | chromium | footer shows the quick links in order | `@regression` | 7.9 s |
| `ui/home.spec.js` | firefox | loads with the PAIMANA title and heading | `@smoke` | 34.2 s |
| `ui/home.spec.js` | firefox | footer shows the quick links in order | `@regression` | 12.4 s |
| `ui/home.spec.js` | webkit | loads with the PAIMANA title and heading | `@smoke` | 7.5 s |
| `ui/home.spec.js` | webkit | footer shows the quick links in order | `@regression` | 9.6 s |
| `api/home.api.spec.js` | api | GET /home returns an HTML page | `@smoke` | 0.26 s |

**Reading it**

- The 2 UI tests ran once in each of the 3 browsers, and the API test once: 2 × 3 + 1 = 7, matching the `projects` in `playwright.config.js`.
- The API test is about 30 times faster than the UI tests, because it starts no browser.
- One Firefox test took **34.2 s**, much longer than the others and over half the 60 s test timeout. The duration includes fixture time. The report doesn't record whether the run was headed; if it was, a likely cause is the window maximize in `window.js` retrying (up to 3 attempts plus a 10 s search) while 6 windows open at once. Firefox's slower start and page load also add time. If it creeps towards 60 s, run with fewer workers (`WORKERS=4`) and watch for a `[maximizeOsWindow] window not maximized` warning in the console.

**Opening it**

| Where | How |
|---|---|
| Locally | `npm run report` (serves `playwright-report/` on a local web server). |
| A copy, e.g. Jenkins's **Zip** download | `npx playwright show-report <unzipped-folder>`. |
| Jenkins | the job's **Playwright Report** link, after relaxing the CSP. |

### Run Flow

**What happens when you run `npm run test:headed`**

1. `cross-env` sets `HEADLESS=false`, then starts `playwright test`.
2. Playwright loads `playwright.config.js`:
   1. `loadEnvFile()` fills in any missing variables from `.env`.
   2. `resolveHeadless()` → `false`; `resolveWorkers()` → `WORKERS`, or 1 on CI, or the default.
   3. `windowSettings()` gives each browser project its window settings.
3. Playwright finds tests: `tests/ui/*.spec.js` for chromium, firefox and webkit; `tests/api/*.spec.js` for api.
4. For each UI test:
   1. The `page` fixture opens a new window and, for Firefox/WebKit on Windows, maximizes it via `window.js`.
   2. The `homePage` fixture wraps that page in a `HomePage`.
   3. `beforeEach` calls `homePage.open()`, then the test's assertions compare the page with `test-data/homePage.json`.
5. The API test sends `GET /home` with no browser.
6. Reporters print one line per test and write `playwright-report/index.html`.

**Environment variables summary**

| Variable | Set by | Default | Used in |
|---|---|---|---|
| `BASE_URL` | `.env`, terminal, Jenkins | dev portal URL | `use.baseURL` |
| `HEADLESS` | `test:headed` / `test:headless`, `.env`, terminal | `CI` set → headless, else headed | `resolveHeadless()` |
| `WORKERS` | `.env`, terminal, Jenkins `set WORKERS=4` | `CI` set → 1, else half the cores | `resolveWorkers()` |
| `CI` | GitHub Actions; the Jenkins build environment | not set locally | headless default, `workers`, `retries`, `forbidOnly` |
| `PAIMANA_USERNAME` / `PAIMANA_PASSWORD` | `.env`, Jenkins credentials binding | empty | future login tests |

**Command cheat sheet**

```bash
npm ci                               # install exact dependencies
npx playwright install               # download browsers (first time / after upgrades)

npm test                             # everything
npm run test:headed                  # maximized windows
npm run test:headless                # no windows
npm run test:chromium                # one browser, quick
npm run test:smoke                   # @smoke only
npm run test:headed -- --workers=4   # extra options after --
npm run report                       # open the last HTML report

npm run check                        # lint + format check
npm run lint:fix                     # auto-fix lint problems
npm run format                       # auto-format
```

**Jenkins build step (Windows batch)**

```bat
set WORKERS=4
call npm ci
call npx playwright install
call npm run test:headed
```

Publish HTML reports: directory `playwright-report`, index page `index.html`. Full setup: see `JenkinsJobTypesSetup.md`.
