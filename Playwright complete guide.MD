# Playwright command reference: PlaywrightProjectName, PAIMANA and Java

| | |
|---|---|
| **Stack** | Node 20+ · `@playwright/test` 1.63 · JavaScript (ES modules) |
| **Tests** | 3 tests × 3 browsers (chromium, firefox, webkit) = 9 runs |
| **Folder** | `C:\Users\shiva\OneDrive\JavaSelenium\PlaywrightProjectName` |
| **Repo** | https://github.com/ShivamPandit1213/PlaywrightProjectName |
| **Portal** | https://iigdev.uatnegd.online (baseURL; override with `BASE_URL`) |
| **Also covers** | PAIMANA_Playwright_1.1 (TypeScript, 15 runs) · paimana-playwright-java (Maven) · Jenkins jobs; see [Projects at a glance](#projects-at-a-glance) |
| **Updated** | 2026-10-21 |

Each row is a command and **when** to reach for it (its purpose). Everything runs in PowerShell from the project folder unless stated; Jenkins steps are batch (`.bat`). Project history (what changed when) lives in `ProjectInfo.html`, not here.

## Contents

Click any line to jump to it.

### Flow tree: what do you want to do?

- **I am starting out**
  - [Am I in the right folder?](#0-first-check-are-you-in-the-right-folder)
  - [Install and set up](#1-setup-first-time-fresh-clone-upgrade)
  - [Which project is which](#projects-at-a-glance)
  - [PAIMANA TypeScript project](#15-paimana-typescript-project)
  - [Java Maven project](#14-java-maven-project)
- **I want to run tests**
  - [Run](#2-run)
  - [Filter: run less](#5-filter-run-less)
  - [Another environment](#4-another-environment)
- **I want the browser visible or invisible**
  - [Headless or headed](#3-headless-or-headed)
  - [Why maximizing fails on Jenkins](#why-maximizing-fails-on-jenkins-and-the-19201080-fallback)
- **Something failed**
  - [Debug a failing test](#6-debug-a-failing-test)
  - [Where a failed run leaves evidence](#7-where-a-failed-run-leaves-evidence)
  - [Inside UI mode](#inside-ui-mode)
  - [Troubleshooting](#12-troubleshooting)
- **I want to record or explore a page**
  - [Record and explore](#8-record-and-explore)
  - [Codegen tips](#codegen-tips)
  - [Record as a logged-in user](#record-as-a-logged-in-user)
- **Reports, upgrades, cleanup**
  - [Reports, snapshots and CI-style runs](#9-reports-snapshots-and-ci-style-runs)
  - [Upgrade and clean up](#10-upgrade-and-clean-up)
- **Git, GitHub Actions and Jenkins**
  - [Git and CI](#11-git-and-ci)
  - [Push: which command, when](#push-which-command-when)
  - [Jenkins jobs](#13-jenkins-jobs)
- **I just need a command fast**
  - [Optional npm shortcuts](#a-optional-shortcuts)
  - [Getting help on any command](#b-getting-help-on-any-command)

### Full contents

- [Projects at a glance](#projects-at-a-glance)
- [0. First check: are you in the right folder?](#0-first-check-are-you-in-the-right-folder)
- [1. Setup: first time, fresh clone, upgrade](#1-setup-first-time-fresh-clone-upgrade)
- [2. Run](#2-run)
- [3. Headless or headed](#3-headless-or-headed)
  - [Why maximizing fails on Jenkins, and the 1920×1080 fallback](#why-maximizing-fails-on-jenkins-and-the-19201080-fallback)
- [4. Another environment](#4-another-environment)
- [5. Filter: run less](#5-filter-run-less)
- [6. Debug a failing test](#6-debug-a-failing-test)
  - [Inside UI mode](#inside-ui-mode)
- [7. Where a failed run leaves evidence](#7-where-a-failed-run-leaves-evidence)
- [8. Record and explore](#8-record-and-explore)
  - [Record a new test, step by step](#record-a-new-test-step-by-step)
  - [Record as a logged-in user](#record-as-a-logged-in-user)
  - [Continue recording from inside a test](#continue-recording-from-inside-a-test)
  - [Codegen options](#codegen-options)
  - [Codegen tips](#codegen-tips)
  - [Explore without recording](#explore-without-recording)
- [9. Reports, snapshots and CI-style runs](#9-reports-snapshots-and-ci-style-runs)
- [10. Upgrade and clean up](#10-upgrade-and-clean-up)
- [11. Git and CI](#11-git-and-ci)
  - [Everyday](#everyday)
  - [Push: which command, when](#push-which-command-when)
  - [First push to a new repository](#first-push-to-a-new-repository)
  - [Remote](#remote)
  - [CI](#ci)
- [12. Troubleshooting](#12-troubleshooting)
  - [Diagnosis order](#diagnosis-order)
  - [Environment](#environment)
  - [Configuration](#configuration)
  - [The run](#the-run)
  - [Recording](#recording)
  - [Git](#git)
- [13. Jenkins jobs](#13-jenkins-jobs)
  - [Jenkins headless job](#jenkins-headless-job)
  - [Jenkins headed job](#jenkins-headed-job)
  - [Jenkins notes](#jenkins-notes)
- [14. Java Maven project](#14-java-maven-project)
  - [Java prerequisites](#java-prerequisites)
  - [Java commands](#java-commands)
  - [New Java project](#new-java-project)
  - [TypeScript or JavaScript vs Java](#typescript-or-javascript-vs-java)
  - [Java errors](#java-errors)
- [15. PAIMANA TypeScript project](#15-paimana-typescript-project)
  - [PAIMANA files and structure](#paimana-files-and-structure)
  - [PAIMANA commands that differ](#paimana-commands-that-differ)
  - [Coming from Selenium](#coming-from-selenium)
  - [PAIMANA notes](#paimana-notes)
- [A. Optional shortcuts](#a-optional-shortcuts)
- [B. Getting help on any command](#b-getting-help-on-any-command)

---

## Projects at a glance

Three projects share this guide. Commands are the same unless a row below says otherwise; examples elsewhere use the `.js` project, and PAIMANA uses `.ts`.

| | PlaywrightProjectName (main) | PAIMANA_Playwright_1.1 | paimana-playwright-java |
|---|---|---|---|
| Language | JavaScript (ES modules) | TypeScript | Java 17/21, Maven, TestNG |
| Folder | `C:\Users\shiva\OneDrive\JavaSelenium\PlaywrightProjectName` | `C:\Users\shiva\OneDrive\JavaSelenium\PAIMANA_Playwright_1.1` | `C:\Users\shiva\OneDrive\JavaSelenium\paimana-playwright-java` |
| Size | 3 tests × 3 browsers = 9 runs | 5 tests × 3 browsers = 15 runs | per test class |
| Config / build file | `playwright.config.js` | `playwright.config.ts` | `pom.xml` |
| Tests live in | `tests/*.spec.js` | `tests/*.spec.ts` (`testDir: './tests'`) | `src/test/java` |
| Show the browser | `HEADLESS=false` or `npm run test:headed` (section 3) | `$env:HEADED=1` | `-Dheadless=false` (only if you wired it) |
| Install dependencies | `npm ci` | `npm install` | `mvn clean compile` |
| Install browsers | `npx playwright install` | `npx playwright install` | `mvn exec:java ... CLI install` (section 14) |
| Run everything | `npm test` | `npx playwright test` | `mvn test` |
| Report | `npm run report` | `npx playwright show-report` | `target/surefire-reports/` |
| CI | GitHub Actions + Jenkins | not stated | not stated |
| Details | sections 0–13 | [section 15](#15-paimana-typescript-project) | [section 14](#14-java-maven-project) |

Differences that look like contradictions but are not:
- **Headed switch:** `HEADLESS` belongs to the main project's config; `HEADED=1` belongs to PAIMANA. They are read by different configs, so setting one has no effect on the other project.
- **Test counts:** 9 runs is the main project, 15 is PAIMANA. `--list` on the wrong one is a quick way to tell you are in the wrong folder.
- **Playwright version:** the main project is on 1.63; the `1.59.1` quoted in the PAIMANA notes is only an example of what `npx playwright --version` prints there.

[↑ Contents](#contents)


## 0. First check: are you in the right folder?

| Command | What it does / when to use it |
|---|---|
| `cd C:\Users\shiva\OneDrive\JavaSelenium\PlaywrightProjectName` | Every command assumes this folder. From `C:\Users\shiva` you get `EPERM: operation not permitted, scandir ...\Temp\WinSAT`, because Playwright starts scanning your whole profile for tests. |
| `dir` | You are in the right place when it lists `package.json` and `playwright.config.js`. |
| `npx playwright --version` | Prints `Version 1.63.0`. If it errors, dependencies are missing → section 1. |
| `node -v`<br>`npm -v`<br>`npx -v` | Versions of Node.js, npm and npx. Node 20+ is expected (18 is end-of-life). If one fails, it is not installed or the terminal was not reopened after installing; nothing else works until these print versions. |
| `npx playwright --help` | Lists every Playwright command and flag; use it when you cannot remember a flag name. |

Shortcut to be in the right folder: open the project in File Explorer, click the address bar, type `cmd` and press Enter; the terminal opens already there.

[↑ Contents](#contents)

## 1. Setup: first time, fresh clone, upgrade

| Command | What it does / when to use it |
|---|---|
| `npm ci` | Installs exactly what `package-lock.json` says. Use after cloning, after switching branches, or whenever `node_modules` looks broken. Fails if `package.json` and the lock file disagree — then run `npm install` once and commit the updated lock file. |
| `npm install` | Resolves versions afresh and rewrites `package-lock.json`. Use only when you change dependencies on purpose (`npm install -D <package>`). |
| `npx playwright install` | Downloads the browser builds that match the installed `@playwright/test` version into `C:\Users\<you>\AppData\Local\ms-playwright`. Once per machine, and again after **every** Playwright upgrade. Prints nothing when the browsers are already there. |
| `npx playwright install chromium` | Chromium only — quicker when firefox/webkit are never run locally. |
| `npx playwright install chrome`<br>`npx playwright install msedge` | Branded Google Chrome / Microsoft Edge, for `--channel=chrome` / `--channel=msedge`. Only needed if you test against the real branded browser. |
| `npx playwright install --with-deps` | Linux / CI only: also installs OS libraries. Not needed on Windows. |
| `npx playwright install --only-shell` | Chromium headless shell only, no full browser. Smaller download; CI use. |
| `npx playwright install --list` | Shows which browser builds are present on this machine. |
| `npx playwright install --dry-run` | Shows what would be downloaded and where, without downloading. |
| `npx playwright install --force` | Re-download even if present. Use when a browser folder is corrupted. |
| `npx playwright install-deps` | OS-level libraries the browsers need. Linux / CI runners only; not needed on Windows. |
| `npm init playwright@latest` | Scaffolds a brand-new project in the current folder (language, test folder, GitHub Actions, install browsers) and writes `package.json`, the config, an example spec and the CI workflow. New projects only, never inside an existing one. |
| `git pull origin main` | Existing project: get the latest tests and config before installing or running anything. |
| `git branch --set-upstream-to=origin/main main` | One-off: link local `main` to `origin/main` so plain `git pull` / `git push` work (see section 11). |
| `git pull origin main --allow-unrelated-histories` | Fallback only, when Git refuses with "refusing to merge unrelated histories" (the GitHub repo and the local repo were created separately). |

**Existing project, in order:** `cd` → `dir` → `git status` → `git pull origin main` → `npm ci` → `npx playwright install` → `npx playwright --version` → `npx playwright test --list` (expect 9 tests here, 15 in PAIMANA) → `npm test` → `npm run report`.

[↑ Contents](#contents)

## 2. Run

| Command | What it does / when to use it |
|---|---|
| `npm test` | = `npx playwright test`. The everyday run: 3 tests × 3 browsers = 9. Window or no window follows `DEFAULT_HEADLESS` in `playwright.config.js`. |
| `npm run test:headed` | Same run, browser windows always visible (Chromium maximized). Use it to watch what a test actually does. |
| `npm run test:headless` | Same run, no windows. Fastest; use before a commit. |
| `npm run test:chromium` | One browser only. The quickest feedback while writing a test. |
| `npm run report` | Opens `playwright-report/index.html` from the last run, with the attached full-page screenshot and any traces. |
| `npx playwright test --project=firefox`<br>`npx playwright test --project=webkit`<br>`npx playwright test --project=chromium --project=firefox` | Pick browsers by hand. webkit is Safari's engine. |
| `npx playwright test --workers=1` | One test at a time. Use when parallel runs interfere with each other or the laptop struggles with several browsers at once. |
| `npx playwright test --workers=50%` | Half the CPU cores. A middle ground on a busy laptop. |
| `npx playwright test --fully-parallel` | Also run tests **inside** one file in parallel (default: files in parallel, tests in a file one after another). |
| `npx playwright test --retries=2` | Retry failures. Separates flaky from broken: a test that passes on retry is reported as *flaky*, not *failed*. |
| `npx playwright test --repeat-each=5` | The opposite check: run every test 5 times to prove a green test is stable before trusting it. |
| `npx playwright test --last-failed` | Re-run only the tests that failed in the previous run. |
| `npx playwright test --only-changed` | Only test files changed since the last commit (uncommitted work). `--only-changed=main` compares against a branch instead. |
| `npx playwright test -x` | Stop at the first failure. Same as `--max-failures=1`. |
| `npx playwright test --max-failures=3` | Stop after 3 failures — saves time when the portal is down and everything will fail. |
| `npx playwright test --global-timeout=600000` | Abort the whole run after 10 minutes (milliseconds). |
| `npx playwright test -c other.config.js` | Use a different config file (`--config`). |

Flags stack, e.g. `npx playwright test tests/home.spec.js --project=chromium --headed`

[↑ Contents](#contents)

## 3. Headless or headed

Priority, highest first (`resolveHeadless()` in `playwright.config.js`):

1. **`CI` is set** (GitHub Actions) → always headless, there is no screen.
2. **`HEADLESS` env var** → `true` / `false`, any case; anything else stops the run with `HEADLESS must be 'true' or 'false'`.
3. **`DEFAULT_HEADLESS`** → constant at the top of the config (currently `false` = windows visible).

| Command | What it does / when to use it |
|---|---|
| `$env:HEADLESS='true'`<br>`$env:HEADLESS='false'` | Applies to this PowerShell window until it is closed or removed. The npm scripts `test:headed` / `test:headless` set it for one run only. |
| `$env:HEADLESS` | Print the current value. Blank = not set. |
| `Remove-Item Env:HEADLESS` | Back to `DEFAULT_HEADLESS`. Run this when tests unexpectedly show or hide windows — a value left over from earlier is the usual reason. |
| `npx playwright test --headed` | Playwright's own flag: forces windows for one run regardless of the config. Fine for a quick look; prefer `npm run test:headed`, which also gives Chromium a maximized window. |
| `$env:HEADED=1` | PAIMANA project only: its own switch for a visible browser (see section 15). Has no effect on this project. |

### Why maximizing fails on Jenkins, and the 1920×1080 fallback

- **Service mode (Session 0):** when Jenkins runs as a Windows Service it has an isolated desktop with no real display; windows are clamped to about 1024×768 or hidden.
- **Interactive mode** (`java -jar jenkins.war`): windows appear on your desktop, but locking Windows (Win + L) suspends rendering and maximize calls fail.
- **Headless has no real window:** `--start-maximized` and OS maximize are ignored, so set an explicit 1920×1080 viewport.

Reference sample (simplified; your real config adds the `DEFAULT_HEADLESS` constant and rejects invalid `HEADLESS` values instead of ignoring them):

```javascript
import { defineConfig, devices } from '@playwright/test';

// Standard full-HD size used for the viewport and screenshots
const FULL_HD = { width: 1920, height: 1080 };

function resolveHeadless() {
  // Read HEADLESS from the environment (Jenkins parameter or local terminal)
  const fromEnv = process.env.HEADLESS?.trim().toLowerCase();

  // 1. Explicit value wins
  if (fromEnv === 'true') return true;
  if (fromEnv === 'false') return false;

  // 2. On CI servers default to headless (there is no screen)
  if (process.env.CI) return true;

  // 3. Local default: headed (visible browser)
  return false;
}

const HEADLESS = resolveHeadless();

function getWindowSettings(browserName) {
  if (HEADLESS) {
    // Headless: fixed 1080p viewport on every browser engine
    return { viewport: FULL_HD, launchOptions: {} };
  }
  if (browserName === 'chromium') {
    return {
      viewport: null,                                  // null = page fills the maximized OS window
      launchOptions: { args: ['--start-maximized'] },  // Chromium flag: maximize on launch
    };
  }
  // Firefox and WebKit do not support --start-maximized: use a fixed size instead
  return { viewport: FULL_HD, launchOptions: { args: ['--width=1920', '--height=1080'] } };
}

export default defineConfig({
  use: {
    headless: HEADLESS,
    trace: 'on-first-retry',        // record a trace only when a test is retried after failing
    screenshot: 'only-on-failure',  // screenshot only for failed tests
  },
  projects: [
    { name: 'chromium', use: { ...devices['Desktop Chrome'],  ...getWindowSettings('chromium') } },
    { name: 'firefox',  use: { ...devices['Desktop Firefox'], ...getWindowSettings('firefox')  } },
    { name: 'webkit',   use: { ...devices['Desktop Safari'],  ...getWindowSettings('webkit')   } },
  ],
});
```

[↑ Contents](#contents)

## 4. Another environment

| Command | What it does / when to use it |
|---|---|
| `$env:BASE_URL='https://other-env.example'; npm test` | Point the same tests at another portal. |
| `$env:BASE_URL` | Check the current value. |
| `Remove-Item Env:BASE_URL` | Back to the dev portal. |

[↑ Contents](#contents)

## 5. Filter: run less

| Command | What it does / when to use it |
|---|---|
| `npx playwright test tests/home.spec.js` | One file. |
| `npx playwright test tests/home.spec.js:12` | One test, by the line number of its `test(...)` call. |
| `npx playwright test home` | Every file whose path contains `home` (it's a pattern, not an exact name). |
| `npx playwright test -g "home page loads"` | By test title (substring, case-insensitive). |
| `npx playwright test --grep @smoke` | By tag. A tag exists only if a test declares it: `test('home page loads', { tag: '@smoke' }, async ({ page }) => ...)` |
| `npx playwright test --grep "@smoke|@login"` | Either tag (regular expression `|` = or). Keep the quotes in PowerShell. |
| `npx playwright test --grep-invert @slow` | Everything except a tag. |
| `npx playwright test --list` | Show which tests **would** run, without running them. First thing to try when a filter matches nothing or you see `No tests found`. |
| `npx playwright test --pass-with-no-tests` | Exit successfully when a filter matches nothing (default is to fail). Useful in scripts. |

[↑ Contents](#contents)

## 6. Debug a failing test

| Command | What it does / when to use it |
|---|---|
| `npm run test:chromium -- tests/home.spec.js --headed` | Narrow it down: one browser, one file, visible. The `--` passes the rest of the line through npm to Playwright. |
| `npx playwright test --debug` | Playwright Inspector: pause before every action, step through, see the locator being used. Best for "why does it not click / find it". |
| `npx playwright test tests/home.spec.js:12 --debug` | Debug just one test, by line number. Much faster than `--debug` on the whole suite. |
| `npx playwright test --ui` | UI mode: pick tests, watch them, time-travel through each step with the DOM and network. Best while writing a new test. |
| `npx playwright test --trace on` | Record a trace for every test (the config records one only on the first retry). |
| `npx playwright test --trace retain-on-failure` | Record for every test but keep the trace only for failures. Good default when hunting an intermittent failure. |
| `npx playwright show-trace test-results/<test-folder>/trace.zip`<br>`npx playwright show-trace` | Trace viewer (with a file picker when no path is given). A trace has every action, network call, console line and a screenshot per step: the most complete record of a failure, and shareable as one file. You can also drop a `trace.zip` onto https://trace.playwright.dev. |
| `npx playwright test --timeout=120000` | Give slow pages more time (milliseconds) for one run instead of editing the config. |
| `npx playwright test --reporter=line` | Compact one-line output. Useful in a narrow terminal or when pasting a log somewhere. |
| `$env:DEBUG='pw:api'; npx playwright test --project=chromium` | Verbose log of every Playwright call. Use when a run hangs and you need to see the last thing it did. `Remove-Item Env:DEBUG` afterwards. |
| `$env:PWDEBUG='console'; npx playwright test --project=chromium --headed` | Adds a `playwright` object to the browser DevTools console: try `playwright.$('text=Login')` or `playwright.inspect(...)` live on the page. `Remove-Item Env:PWDEBUG` afterwards. |

### Inside UI mode

| Control | What it does / when to use it |
|---|---|
| Play triangle at top of the TESTS panel | Run everything. |
| Play button on a test row | Run just that test. |
| Eye icon | Watch mode: re-runs on file save. |
| Filter box | Type `@smoke` to filter by tag. |
| Actions panel | Lists every step; click one to time-travel. |
| Locator tab + target icon | Hover elements to generate locators. |
| Projects: chromium | Defaults to one browser; expand to add more. |

[↑ Contents](#contents)

## 7. Where a failed run leaves evidence

| Location | What is there |
|---|---|
| `test-results/<test-name>/test-failed-1.png` | Screenshot at the moment of failure. |
| `test-results/<test-name>/error-context.md` | The page's accessibility tree at that moment (when present) — read it to find the right locator. |
| `test-results/<test-name>/trace.zip` | On a retry, or always with `--trace on`. |
| `playwright-report/index.html` | The HTML report (`npm run report`). Attachments such as the full-page screenshot from `home.spec.js` sit under each test. |

Both folders are rewritten on every run and are git-ignored. Copy a file out before re-running if you want to keep it.

[↑ Contents](#contents)

## 8. Record and explore

Codegen opens two windows: a **browser** where every click and fill is recorded, and the **Playwright Inspector** showing the generated code live. The generated code is raw material for `pages/` and `tests/`, not a finished test.

### Record a new test, step by step

| # | Command / action | What it does / when to use it |
|---|---|---|
| 1 | `npx playwright install chromium` | Codegen uses Chromium by default. Skip if section 1 is already done. |
| 2 | `npx playwright codegen https://iigdev.uatnegd.online/home` | Start recording on the portal. Add `--ignore-https-errors` if the UAT certificate is rejected. |
| 3 | Click, type, select in the browser | Each action appears in the Inspector as a line of code. Act slowly: wait for each page to load before the next click. |
| 4 | Browser toolbar → **Assert visibility / Assert text / Assert value / Assert snapshot** | Click the assertion, then the element. This is how a recording gets `expect(...)` lines — without them it only proves nothing crashed. |
| 5 | Inspector → **Pick locator**, hover an element | Shows the locator Playwright would use (`getByRole`, `getByLabel`, `getByText`…). Copy it straight into a page object. |
| 6 | Inspector → **Record** button | Pause/resume recording, e.g. to scroll around without generating noise. |
| 7 | Inspector → **Target** dropdown | Switch output language. Keep **Test Runner (JavaScript)** for this project (= `--target=playwright-test`). |
| 8 | Copy the code, **or** start with `npx playwright codegen -o tests/new-flow.spec.js https://iigdev.uatnegd.online/home` | `-o` writes the code to a file as you record. The file is overwritten on every codegen run with the same `-o`. |
| 9 | Close the browser window | Ends the session (and writes `--save-storage` / `--save-har` files, if used). |
| 10 | Clean up the code | Move locators into a page object in `pages/`; replace the full URL with `page.goto('/home')` (uses `baseURL`); credentials from `process.env`, never hard-coded; add a tag (`{ tag: '@smoke' }`); delete redundant clicks. |
| 11 | `npm run test:chromium -- tests/new-flow.spec.js --headed` | Watch it run once in one browser. |
| 12 | `npx playwright test tests/new-flow.spec.js --repeat-each=5` | Prove it is stable before committing, then run it in all 3 browsers with `npm test`. |

### Record as a logged-in user

Log in once, save the session, reuse it so later recordings start on the dashboard.

| # | Command / action | What it does / when to use it |
|---|---|---|
| 1 | Add `/playwright/.auth/` to `.gitignore` | The saved file holds live cookies/tokens. **Never commit it.** Check with `git status --short` — it must not appear. |
| 2 | `npx playwright codegen --save-storage=playwright/.auth/cpse.json https://iigdev.uatnegd.online/home` | Record with saving switched on. |
| 3 | Log in manually with **test** credentials, wait for the landing page (e.g. Dashboard) | The session is captured from whatever state the browser is in at the end. |
| 4 | **Close the browser window** | `cpse.json` is written only on close. Ctrl+C in PowerShell instead of closing can leave it missing or empty. |
| 5 | `dir playwright\.auth` | Confirm `cpse.json` exists and is not 0 bytes. |
| 6 | `npx playwright codegen --load-storage=playwright/.auth/cpse.json https://iigdev.uatnegd.online/home` | Record further flows already logged in. |
| 7 | In a spec: `test.use({ storageState: 'playwright/.auth/cpse.json' });` | Tests in that file start logged in too. Or set `storageState` under `use:` for a project in `playwright.config.js`. |
| 8 | Repeat step 2 when recordings land on the login page again | Sessions expire. For a permanent fix, log in from a setup project (`tests/auth.setup.js` + `dependencies: ['setup']` in the config) so every run creates a fresh session. |

To make one spec ignore a saved session (e.g. login-page tests): `test.use({ storageState: { cookies: [], origins: [] } });`

### Continue recording from inside a test

| Command / action | What it does / when to use it |
|---|---|
| Add `await page.pause();` in the test where recording should start | Stops the test there and opens the Inspector. |
| `npx playwright test tests/home.spec.js --project=chromium --headed` | Runs up to `page.pause()`; then press **Record** in the Inspector and continue by hand. The test's own login/navigation does the setup for you. |
| Remove `page.pause()` before committing | Otherwise headless and CI runs hang until timeout. |

VS Code users: the *Playwright Test for VS Code* extension has **Record new**, **Record at cursor** and **Pick locator** in the Testing sidebar — same codegen, but code lands directly in the open file.

### Codegen options

`npx playwright codegen [options] [url]` — every option below also works with `npx playwright open`.

> **PowerShell:** quote any value containing a comma or space (`"1920,1080"`, `"iPhone 14"`). Unquoted, PowerShell treats the comma as an array separator and Playwright receives the wrong value. Write long commands on one line, or end lines with a backtick `` ` `` (not `\`).

**Output**

| Command | What it does / when to use it |
|---|---|
| `npx playwright codegen -o tests/login.spec.js <url>` | Write the code to a file (`--output`). |
| `npx playwright codegen --target=playwright-test <url>` | Language. Default for this project. Others: `javascript` (plain script, no test runner), `python`, `python-async`, `python-pytest`, `csharp`, `csharp-mstest`, `csharp-nunit`, `java`, `java-junit`. |
| `npx playwright codegen --test-id-attribute=data-qa <url>` | Which attribute `getByTestId` uses, if the portal uses `data-qa` / `data-test` instead of `data-testid`. |

**Browser**

| Command | What it does / when to use it |
|---|---|
| `npx playwright codegen -b firefox <url>` | Engine: `chromium` (`cr`), `firefox` (`ff`), `webkit` (`wk`). |
| `npx playwright codegen --channel=msedge <url>` | Branded browser: `chrome`, `msedge`, `chrome-beta`, `msedge-dev` … (install it first, section 1). |
| `npx playwright codegen --user-data-dir=.\.codegen-profile <url>` | Persistent profile: cookies, extensions and history survive between codegen runs. Git-ignore the folder. |
| `npx playwright codegen --timeout=10000 <url>` | Action timeout in milliseconds. |

**Session and network**

| Command | What it does / when to use it |
|---|---|
| `npx playwright codegen --save-storage=playwright/.auth/cpse.json <url>` | Save cookies + localStorage when the window closes. |
| `npx playwright codegen --load-storage=playwright/.auth/cpse.json <url>` | Start with a saved session. |
| `npx playwright codegen --save-har=network.har <url>` | Record all network traffic to a HAR file (for mocking with `page.routeFromHAR`). |
| `npx playwright codegen --save-har=api.har --save-har-glob="**/api/**" <url>` | Record only matching URLs. |
| `npx playwright codegen --ignore-https-errors <url>` | Accept self-signed / invalid certificates (common on UAT). |
| `npx playwright codegen --proxy-server=http://proxy:3128 <url>` | Route through a proxy. `--proxy-bypass=".internal.local"` for exceptions. |
| `npx playwright codegen --block-service-workers <url>` | Disable service workers, so network requests are visible/recordable. |

**Emulation**

| Command | What it does / when to use it |
|---|---|
| `npx playwright codegen --device="iPhone 14" <url>` | Mobile size, touch and user agent. Device names: `node -e "console.log(Object.keys(require('playwright-core').devices).join('\n'))"` |
| `npx playwright codegen --viewport-size="1920,1080" <url>` | Exact window size. |
| `npx playwright codegen --color-scheme=dark <url>` | `light` or `dark`. |
| `npx playwright codegen --geolocation="28.6139,77.2090" <url>` | Location (New Delhi here); grants the permission automatically. |
| `npx playwright codegen --lang=en-IN <url>` | Browser locale. |
| `npx playwright codegen --timezone="Asia/Kolkata" <url>` | Time zone. |
| `npx playwright codegen --user-agent="MyAgent/1.0" <url>` | Custom user agent. |

**Combined example (one line in PowerShell):**

```powershell
npx playwright codegen --load-storage=playwright/.auth/cpse.json --ignore-https-errors --viewport-size="1920,1080" --timezone="Asia/Kolkata" --lang=en-IN -o tests/new-flow.spec.js https://iigdev.uatnegd.online/home
```

### Codegen tips

- **Click AND type.** Clicking alone records `.click()` with no `.fill()`.
- **`.first()` and `.nth(1)` in generated code** mean several elements matched the same name. Replace them with scoped locators; index positions break as soon as the page changes.
- **Prefer `getByRole()` over XPath.** Role locators survive layout and styling changes far better.
- **Captchas cannot be automated.** Ask the dev team to disable captcha on UAT, or log in manually once with `--save-storage` and reuse it with `--load-storage`.
- **`-o` overwrites the file without asking.** Run it from the project folder or the path will not exist. Safer pattern: record to a scratch file (`-o codegen-output.js`), copy the useful parts into a real spec by hand, and git-ignore the scratch file.

### Explore without recording

| Command | What it does / when to use it |
|---|---|
| `npx playwright open https://iigdev.uatnegd.online/home` | A browser with the Playwright inspector attached and no code generation — for exploring a page and trying locators. |
| `npx playwright open --load-storage=playwright/.auth/cpse.json https://iigdev.uatnegd.online/home` | Explore as the logged-in user. |
| `npx playwright screenshot https://iigdev.uatnegd.online/home home.png` | Quick screenshot from the command line. Add `--full-page`, `--wait-for-timeout=3000`, `--wait-for-selector="h1"` or `--device="iPhone 14"` as needed. |
| `npx playwright pdf https://iigdev.uatnegd.online/home home.pdf` | Save the page as PDF (Chromium only). |

[↑ Contents](#contents)

## 9. Reports, snapshots and CI-style runs

| Command | What it does / when to use it |
|---|---|
| `npx playwright test --reporter=list` | One line per test (default locally). |
| `npx playwright test --reporter=dot` | One character per test. Minimal. |
| `npx playwright test --reporter=html` | HTML report only. |
| `npx playwright test --reporter=json > results.json` | Machine-readable results. |
| `npx playwright test --reporter=junit > results.xml` | JUnit XML for Jenkins / Azure DevOps. |
| `npx playwright test --reporter=list,html` | Several reporters in one run. |
| `npx playwright show-report` | Same as `npm run report`. |
| `npx playwright show-report playwright-report --port 9323` | Pick the port (if 9323 is busy). Add `--host 0.0.0.0` to open it from another device on the network. |
| `npx playwright test --shard=1/3` | Run a third of the suite. Used to split CI across several machines (`2/3`, `3/3` on the others). |
| `npx playwright test --reporter=blob` then `npx playwright merge-reports --reporter=html ./blob-report` | Combine sharded results into one HTML report. |
| `npx playwright test --forbid-only` | Fail if a `test.only` is present — what CI does. Run it locally before pushing. |
| `npx playwright test --fail-on-flaky-tests` | Treat "passed on retry" as a failure. |
| `npx playwright test --output=results` | Write evidence to `results/` instead of `test-results/`. |
| `npx playwright test --quiet` | Hide the tests' own `console.log` output. |
| `npx playwright test --no-deps` | Skip dependency projects (e.g. an auth `setup` project) — only if you add one. |
| `npx playwright test --update-snapshots` | (`-u`) Rewrite screenshot baselines that changed — only after checking the change is intended. `=all`, `=changed`, `=missing`, `=none` control which. |
| `npx playwright test --ignore-snapshots` | Skip screenshot comparisons for one run. |

[↑ Contents](#contents)

## 10. Upgrade and clean up

| Command | What it does / when to use it |
|---|---|
| `npm outdated` | Which packages have newer versions. |
| `npm install -D @playwright/test@latest`<br>`npx playwright install` | Upgrade Playwright. **Always** re-run `install` afterwards: the browser builds are tied to the package version. |
| `npm ls @playwright/test` | Must show exactly one version. Two copies cause `did not expect test() to be called here`. |
| `npx playwright clear-cache` | Clears Playwright's compile/test cache (**not** the browsers). Try it when an edit to a test or the config seems to be ignored. |
| `npx playwright uninstall` | Removes the downloaded browsers for this version (about 1 GB). Not the package. |
| `npx playwright uninstall --all` | Removes browsers for **every** Playwright version on the machine — frees space left by old upgrades. |
| `Remove-Item -Recurse -Force node_modules; npm ci` | The reset for a broken `node_modules`. |
| `Remove-Item -Recurse -Force test-results, playwright-report` | Wipe old evidence and reports. |

[↑ Contents](#contents)

## 11. Git and CI

### Everyday

| Command | What it does / when to use it |
|---|---|
| `git status --short` | What changed: `M` modified, `D` deleted, `??` new. Long form: `git status`. |
| `git add -A` | Stage everything, including deletions. `git add .` does the same from the project root. **Not** `git add ..` — two dots is the parent folder, which is outside the repository. |
| `git commit -m "message"` | Record it. A push uploads **commits**, never loose changes — uncommitted work stays local however often you push. |
| `git push` | Upload the committed work — see [Push](#push-which-command-when) for which form to use. |
| `git pull` | Bring down changes from GitHub. |
| `git log --oneline -5` | Last five commits — is today's work in one? |
| `git diff` | Unstaged changes, line by line. |
| `git restore <file>` | Throw away local changes to one file. |
| `git check-ignore -v playwright/.auth/cpse.json` | Confirms the session file is ignored (prints the `.gitignore` rule). No output = it would be committed — fix `.gitignore` first. |

### Push: which command, when

| Command | When | What happens |
|---|---|---|
| `git push` | Every day, once the branch is linked to GitHub (after its first `-u` push). | Uploads the new commits of the current branch to the branch it tracks. On a branch that is **not** linked yet it stops with `fatal: The current branch main has no upstream branch` and uploads nothing — even right after a successful commit. |
| `git push -u origin main` | The **first** push of a branch: a brand-new repository, or a new local branch GitHub has never seen. | Uploads the branch **and** links it (`-u` = `--set-upstream`), so plain `git push` and `git pull` work from then on. Confirms with `branch 'main' set up to track 'origin/main'`. Once per branch, not once per repository. |
| `git push --set-upstream origin main` | Same situation — this is the long spelling Git prints in the error message. | Identical to `-u`. |
| `git push origin main` | A one-off push you don't want to link, e.g. a throwaway branch. | Uploads, but the branch stays unlinked, so plain `git push` keeps failing. Prefer `-u` once. |
| `git push --dry-run` | Before a push you are unsure about. | Shows what would be uploaded; uploads nothing. |
| `git branch -vv` | To know in advance whether plain `git push` will work. | Lists local branches with the remote branch each one tracks, e.g. `main ... [origin/main]`. No bracket = not linked → use `-u`. |
| `git config --global push.autoSetupRemote true` | Once on this PC, if you'd rather never type `-u`. | Plain `git push` links a new branch automatically (the setting Git itself suggests in the error). |
| `git push --force-with-lease` | Only after you rewrote commits that were already pushed (`git commit --amend`, a rebase), and only on **your own** branch. | Replaces the remote branch with yours, but refuses if someone else pushed in between. Never on a shared `main`; never plain `--force`. |

A browser sign-in window may open on the first push to a repository: use the account that owns it. Every push to `main` also starts the GitHub Actions run (see [CI](#ci)).

### First push to a new repository

In this order:

1. `git remote -v` — connected? Must print `origin` → the repo URL.
2. `git status --short` — anything listed here is **not** going up yet.
3. `git add -A` — stage it all. (Expect `LF will be replaced by CRLF` warnings on Windows; they are harmless — see [Git troubleshooting](#git).)
4. `git commit -m "message"` — commit it. The summary line (`20 files changed, 652 insertions(+), 229 deletions(-)`) is your check that everything was included.
5. `git push -u origin main` — upload and link local `main` to GitHub `main`. From now on plain `git push` is enough.

### Remote

The GitHub address this folder pushes to. The repository is https://github.com/ShivamPandit1213/PlaywrightProjectName.

| Command | What it does | Fails when |
|---|---|---|
| `git remote -v` | Show it. Empty output = no remote at all. | — |
| `git remote add origin <url>` | Connect. | `origin` already exists |
| `git remote set-url origin <url>` | Change. | there is no `origin` |
| `git remote remove origin` | Disconnect. | there is no `origin` |

`remove` and `set-url` print **nothing** on success. After any of them run `git remote -v` instead of repeating the command: a second `remove`, or a `set-url` after a `remove`, only says `No such remote 'origin'`. Names are exact — `origine` is not `origin`; Tab completes them.

### CI

`.github/workflows/playwright.yml` runs on every push and pull request to `main`: `npm ci`, install browsers, `npx playwright test`, upload `playwright-report/`.

- `CI=true` there, so it is always headless, 1 worker, 2 retries, and a committed `test.only` fails the build (`forbidOnly`).
- Results: repository → **Actions** tab → the run → `playwright-report` artifact (download, unzip, open `index.html`).
- Fails on GitHub but passes here? First suspect: GitHub's servers cannot reach `iigdev.uatnegd.online` (private network / VPN).
- Tests that need a login: `playwright/.auth/cpse.json` is not in the repository, so CI has no session. Store credentials as repository **Secrets** (Settings → Secrets and variables → Actions) and log in from a setup project instead.
- Jenkins instead of GitHub Actions: see [13. Jenkins jobs](#13-jenkins-jobs).

[↑ Contents](#contents)

## 12. Troubleshooting

### Diagnosis order

When anything fails, in this order:

1. `dir` — right folder? (`package.json` listed)
2. `npx playwright --version` — package installed?
3. `npx playwright install` — browsers present for this version?
4. `npx playwright test --list` — does the path / filter match?
5. `npm run test:chromium -- tests/home.spec.js --headed` — watch one test, then read `test-results/`

### Environment

| Symptom | Cause | Fix |
|---|---|---|
| `EPERM: operation not permitted, scandir ...\Temp\WinSAT` | Ran from the home folder. | `cd` to the project folder. |
| `npm.ps1 cannot be loaded because running scripts is disabled` | PowerShell execution policy blocks npm. | Once, as your user: `Set-ExecutionPolicy -Scope CurrentUser RemoteSigned` |
| `'playwright' is not recognized` / `Cannot find module '@playwright/test'` | `node_modules` missing or incomplete. | `npm ci` |
| `Executable doesn't exist at ...\ms-playwright\chromium-xxxx\...` / `Looks like Playwright Test or Playwright was just installed or updated` | Browsers not downloaded for this package version (fresh machine or just upgraded). | `npx playwright install` |
| `npm ci`: `package.json and package-lock.json are not in sync` | The lock file drifted (`package.json` edited by hand?). | `npm install` once, then commit `package-lock.json`. |
| `EBUSY: resource busy or locked` | OneDrive is syncing `test-results/` while Playwright writes to it. | Pause OneDrive sync and re-run, or move the project out of OneDrive. |
| `'npx'` / `'node'` / `'mvn'` is not recognized | Not installed, or the terminal was not reopened after installing. | Install it, then close and reopen the terminal. |
| `ENOENT: no such file or directory, open 'C:\Users\shiva\tests\login.spec.ts'` | Same cause as the WinSAT error: wrong folder, so `tests/` does not exist there. | `cd` into the project first. |
| `'C:\Users\shiva' is not recognized as an internal or external command` | The prompt text was pasted along with the command. | Type only what comes after the `>`. |
| Terminal looks frozen, no prompt returns | Normal: `open`, `codegen`, `show-report`, `show-trace` and `test --ui` block until their window is closed. | Close the window, or press Ctrl+C then Y. |
| `Port 9323 already in use` | An old report server is still running. | Close it, or `npx playwright show-report --port 9324`. |

### Configuration

| Symptom | Cause | Fix |
|---|---|---|
| `HEADLESS must be 'true' or 'false', got 'yes'` | Typo in the variable. | `$env:HEADLESS='true'` or `Remove-Item Env:HEADLESS` |
| `Cannot use import statement outside a module` | `package.json` lost `"type": "module"`. | Put it back. |
| `deviceScaleFactor option is not supported with null viewport` | Chromium project config: with `viewport: null`, `deviceScaleFactor` must be left undefined. | Restore `deviceScaleFactor: undefined`. |
| `Playwright Test did not expect test() to be called here` | Two copies of `@playwright/test`, or a spec importing `test` from both `@playwright/test` and a fixtures file. | Import from one place; `npm ls @playwright/test` must show exactly one version. |

### The run

| Symptom | Cause | Fix |
|---|---|---|
| `Error: No tests found` | Wrong path, wrong filter, or the file is not named `*.spec.js`. | `npx playwright test --list` shows what Playwright can see. |
| `Test timeout of 60000ms exceeded` / `page.goto: Timeout 30000ms exceeded` | Portal slow or down, VPN off, or a locator that never appears. | Open the URL in a normal browser first. Then `--headed` or `--debug`, and read `test-results/<test>/error-context.md` for the real page state. |
| `net::ERR_NAME_NOT_RESOLVED` / `net::ERR_CONNECTION_REFUSED` | Wrong `BASE_URL`, or no network / VPN. | `$env:BASE_URL` shows the value. |
| `net::ERR_CERT_AUTHORITY_INVALID` / `SSL` errors | UAT uses a self-signed certificate. | `ignoreHTTPSErrors: true` under `use:` in the config; `--ignore-https-errors` for codegen. |
| `toHaveURL ... Received: https://.../login` (or another unexpected page) | The portal redirected: session, maintenance page, changed route. | Look at the screenshot before touching the test. |
| `Target page, context or browser has been closed` | The browser crashed, usually out of memory with many in parallel. | `npx playwright test --workers=1` |
| Run hangs forever, no failure | A `page.pause()` left in a test. | Search the tests for `page.pause` and remove it. |
| CI fails on `test.only` / `forbidOnly` but everything passes locally | A `test.only` was committed. | Remove `.only`, commit, push again. |
| `page.goto: Timeout 30000ms exceeded` on **Firefox only** (the last Jenkins headed run: chromium and webkit passed, firefox failed all 3 tests) | Firefox waits for the `load` event and the slow UAT portal does not finish loading in 30 s. | In `HomePage.open()` use `this.page.goto(HomePage.PATH, { waitUntil: 'domcontentloaded', timeout: 60000 })`, or set `navigationTimeout: 60000` under `use:`. Also check the Jenkins machine can reach `iigdev.uatnegd.online` in Firefox. |
| Jenkins "headed" job runs without windows | `CI` is set in the job and beats `HEADLESS` (section 3). | Add `set CI=` before `call npx playwright test` (section 13). |
| Browser windows are tiny or missing on Jenkins | Jenkins runs as a Windows Service (Session 0, no real display). | Use the headless job (section 13). |

### Recording

| Symptom | Cause | Fix |
|---|---|---|
| `cpse.json` missing or 0 bytes after `--save-storage` | Codegen was stopped with Ctrl+C, or closed before login finished. | Re-run, finish the login, wait for the landing page, then close the **browser window**. |
| `Error reading storage state from playwright/.auth/cpse.json` / `ENOENT` | File not created yet, wrong path, or not present after a fresh clone (it is git-ignored). | `dir playwright\.auth`; if missing, record it again (section 8). |
| `--load-storage` opens the login page anyway | The saved session expired, or the portal keeps the session in `sessionStorage` (not saved). | Re-record with `--save-storage`. If it expires every time, log in from a setup project instead. |
| Wrong viewport / device / location in codegen | An unquoted comma value in PowerShell was split. | Quote it: `--viewport-size="1920,1080"`. |
| Generated code uses brittle selectors (`#ctl00_...`, long CSS) | The element has no role, label or test id. | Use **Pick locator** on a nearby labelled element, or ask the developers for a `data-testid`. |
| Recorded test passes once, fails next time | Recorded data (unique names, dates) or missing waits on slow pages. | Generate data in the test; replace waits with `expect(...)` assertions; check with `--repeat-each=5`. |

### Git

| Symptom | Cause | Fix |
|---|---|---|
| `fatal: ..: '..' is outside repository` | Typed `git add ..` — two dots is the parent folder. | `git add .` or `git add -A` |
| `warning: in the working copy of '...', LF will be replaced by CRLF the next time Git touches it` | Harmless. Git on Windows converts line endings on checkout (`core.autocrlf=true`); the files were written with LF. | Ignore it, or silence it for good: create `.gitattributes` containing `* text=auto eol=lf`, then `git add --renormalize .` and commit. |
| `fatal: The current branch main has no upstream branch` | First push of this branch — it is not linked to GitHub yet. Committing does not change that. | `git push -u origin main` (once); see [Push](#push-which-command-when). |
| `error: No such remote 'origin'` | There is no remote to change or remove — an earlier `remove` already worked. | `git remote -v` to confirm; if empty: `git remote add origin <url>` |
| `error: remote origin already exists` | Already connected. | `git remote -v` shows where. To point it somewhere else use `git remote set-url origin <url>`, not `add`. |
| Pushed, but GitHub shows the old files / today's work is missing | The changes were never committed; a push uploads commits only. | `git status --short` lists them → `git add -A`, `git commit`, `git push` |
| `Updates were rejected because the remote contains work that you do not have` | The GitHub repo was created with a README or .gitignore, or someone else pushed. | `git pull --rebase origin main`, then `git push` |
| `remote: Permission denied` / 403 on push | Windows saved a different GitHub login. | Control Panel → Credential Manager → Windows Credentials → remove the github.com entry, push again and sign in as the repository owner. |
| `cpse.json` shows up in `git status` | `.gitignore` is missing the auth folder. | Add `/playwright/.auth/` to `.gitignore`. If it was already committed: `git rm --cached playwright/.auth/cpse.json`, commit, and log out / change that test password — the session was exposed. |

[↑ Contents](#contents)

## 13. Jenkins jobs

Two batch-step jobs run the same suite. The headless one is the recommended default; the headed one needs an interactive, unlocked desktop (see [Why maximizing fails on Jenkins](#why-maximizing-fails-on-jenkins-and-the-19201080-fallback)).

### Jenkins headless job

Runs in the background at 1920×1080; unaffected by screen locks or remote-desktop disconnects.

```bat
:: Go to the repository on the Jenkins machine
cd C:\Users\shiva\Projects\PlaywrightBasic1
:: Purpose: every later command needs the project folder.

:: Pull the latest committed code from GitHub
git pull origin main
:: Purpose: test the newest version of the scripts.

:: Install exactly what package-lock.json says
call npm ci
:: Purpose: fast, repeatable, clean install. `call` is required so the batch file continues after npm.

:: Make sure the browser binaries exist for this Playwright version
call npx playwright install
:: Purpose: avoids "Executable doesn't exist" errors on a fresh or upgraded agent.

:: Run browsers invisibly
set HEADLESS=true
:: Purpose: read by resolveHeadless(); fixed 1920x1080 viewport.

:: Run the suite
call npx playwright test
:: Purpose: a non-zero exit code marks the build as failed; the HTML report goes to playwright-report/ for the HTML Publisher.
```

### Jenkins headed job

Use only when Jenkins was started interactively (`java -jar jenkins.war`) and you want visible, maximized windows. Same script, with one line changed:

```bat
set HEADLESS=false
:: Purpose: browsers open on the interactive desktop. Keep the desktop unlocked while tests run.
```

### Jenkins notes

| Topic | Note |
|---|---|
| Folder name | `C:\Users\shiva\Projects\PlaywrightBasic1` and the workspace `PlaywrightBasic1_Headed` are local Jenkins-side names. Renaming the GitHub repository to `PlaywrightProjectName` does not rename them, but the job's Git URL must be updated or the checkout stops working. |
| `CI` beats `HEADLESS` | Section 3 gives `CI` priority over `HEADLESS`. If the job environment sets `CI` (the single worker and 2 retries in your job log suggest it does), the headed job still runs headless. To force windows, add `set CI=` before `call npx playwright test`. |
| Reports | The job archives `playwright-report` with the HTML Publisher plugin (shown as "Playwright_20Report" in the log). |
| Firefox timeouts | The last headed run failed only on Firefox with `page.goto: Timeout 30000ms exceeded`; see the fix in [12. Troubleshooting](#the-run). |

[↑ Contents](#contents)

## 14. Java Maven project

`paimana-playwright-java`: Playwright for Java with TestNG. Java projects have no npm, so browsers are installed through a Maven command.

### Java prerequisites

| Tool | Purpose |
|---|---|
| JDK 17 or 21 (adoptium.net) | Playwright Java needs Java 8+, but match your other tools. |
| Apache Maven | Build tool: dependencies, compile, test. Bundled in most IDEs. |

### Java commands

```bat
java -version
:: Purpose: confirm the JDK and which version the shell uses.

mvn -version
:: Purpose: Maven must report the SAME Java as `java -version`; if not, set JAVA_HOME.

setx JAVA_HOME "C:\Program Files\Java\jdk-21.0.12"
:: Purpose: make Maven use the JDK you want. Close and reopen CMD afterwards.

cd C:\Users\shiva\OneDrive\JavaSelenium\paimana-playwright-java
dir
:: Purpose: Maven only works next to pom.xml; confirm it is listed.

git pull origin main
:: Purpose: latest code before building (link the branch once with `git branch --set-upstream-to=origin/main main`).

mvn clean compile
:: Purpose: delete old build output (target/), download dependencies, check the code compiles from scratch.

mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
:: Purpose: Java equivalent of `npx playwright install`. Needs exec-maven-plugin in pom.xml.

mvn dependency:build-classpath -Dmdep.outputFile=cp.txt
:: Purpose: fallback when exec-maven-plugin is missing: write all dependency jars to cp.txt.

for /f "delims=" %i in (cp.txt) do java -cp "target/classes;%i" com.microsoft.playwright.CLI install
:: Purpose: fallback, continued: run the Playwright CLI with that classpath (in CMD, %cp.txt% would not read the file).

mvn test
:: Purpose: run every test class matching Surefire patterns (*Test.java). Reports: target/surefire-reports/.

mvn test -Dtest=LoginTest
:: Purpose: one test class, for quick feedback.

mvn test -DsuiteXmlFile=suites/smoke.xml
:: Purpose: run a TestNG suite file (a named group such as smoke), if configured in the pom.
```

### New Java project

```bat
mvn archetype:generate -DgroupId=com.paimana -DartifactId=paimana-playwright-java -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
cd paimana-playwright-java
:: Purpose: create the folder layout and a starter pom.xml without prompts.
```

Add inside `<dependencies>` in `pom.xml`, then run `mvn clean compile`, the browser install above, and `mvn test`:

```xml
<!-- Playwright for Java: browser automation API -->
<dependency>
  <groupId>com.microsoft.playwright</groupId>
  <artifactId>playwright</artifactId>
  <version>1.48.0</version>
</dependency>
<!-- TestNG: test framework (runs @Test methods, suites, groups) -->
<dependency>
  <groupId>org.testng</groupId>
  <artifactId>testng</artifactId>
  <version>7.10.2</version>
  <scope>test</scope>
</dependency>
```

### TypeScript or JavaScript vs Java

| Task | Playwright CLI | Maven | Purpose |
|---|---|---|---|
| Install libraries | `npm ci` / `npm install` | `mvn clean compile` | Download project dependencies. |
| Install browsers | `npx playwright install` | `mvn exec:java ... CLI install` | Download browser binaries. |
| Run all tests | `npx playwright test` | `mvn test` | Execute every test. |
| Run one test | `npx playwright test tests/home.spec.js` | `mvn test -Dtest=LoginTest` | One file or class. |
| Filter by tag | `npx playwright test --grep @smoke` | `mvn test -DsuiteXmlFile=suites/smoke.xml` | A tagged group. |
| One browser | `npx playwright test --project=chromium` | `mvn test -Dbrowser=chromium` (only if wired) | Single browser. |
| Headless / headed | `HEADLESS=true` / `false` | `-Dheadless=true` / `false` (only if wired) | Show or hide windows. |
| View report | `npm run report` | open `target/surefire-reports` | Inspect results. |

### Java errors

| Error | Cause and fix |
|---|---|
| `package does not exist` / `cannot find symbol` | Playwright dependency missing from `pom.xml`, or `mvn clean compile` not run. |
| `BUILD FAILURE ... Failed to delete target` | OneDrive is locking files. Pause sync or move the project out of OneDrive. |
| `'mvn'` is not recognized | Maven not installed, or the terminal was not reopened after installing. |
| `mvn -version` shows a different Java than `java -version` | Set `JAVA_HOME` (above) and reopen CMD. |

[↑ Contents](#contents)

## 15. PAIMANA TypeScript project

`PAIMANA_Playwright_1.1`: Playwright + TypeScript end-to-end suite. 5 tests across 3 browsers = 15 runs. Everything in sections 0–12 applies with `.ts` instead of `.js`; this section lists only what is different.

### PAIMANA files and structure

The project will not run without these three; everything else is supporting code imported by the specs.

| File or folder | Purpose |
|---|---|
| `package.json` | Declares `@playwright/test` and holds the npm scripts. |
| `playwright.config.ts` | Browsers, `testDir`, `baseURL`, reporters, timeouts. The runner reads it first, and its location defines the project root. |
| `tests/*.spec.ts` | The tests (`api.spec.ts`, `example.spec.ts`, `home.spec.ts`). Without them the runner reports 0 tests. |
| `fixtures/` | Custom test fixtures (inject page objects into tests). |
| `pages/` | Page Object Model classes (`BasePage`, `HomePage`, `LoginPage`). |
| `test-data/` | Static test data (`users.json`). |
| `utils/` | Shared helpers. |
| `tsconfig.json` | TypeScript compiler options. |
| `node_modules/`, `test-results/`, `playwright-report/` | Generated; never commit. |

`testDir` is not always `tests`: PAIMANA uses `./tests`, but `playwright-learning` uses `./playwright`. If `--list` shows 0 tests, check `testDir` in the config.

### PAIMANA commands that differ

| Command | What it does / when to use it |
|---|---|
| `cd C:\Users\shiva\OneDrive\JavaSelenium\PAIMANA_Playwright_1.1`<br>`$env:HEADED=1`<br>`npx playwright test tests/home.spec.ts --project=chromium` | Visible browser for one file on one browser. `HEADED=1` is PAIMANA's switch (not `HEADLESS`); `Remove-Item Env:HEADED` returns to headless. |
| `npx playwright test` | Headless, all 3 browsers (15 runs). |
| `npx playwright test --list` | Expect `Total: 15 tests in 3 files`. |
| `npx playwright test --grep @smoke` | Tags in this project: `@smoke`, `@regression`, `@api`. |
| `npx playwright test --project=chromium --grep @smoke --headed` | Smoke tests only, one browser, visible: a few seconds instead of about 20. Use it for quick checks; save the 3-browser run for before you commit. |
| `npm test`, `npm run test:ui`, `npm run test:smoke` | Only if the `scripts` section of `package.json` defines them (section A). |

### Coming from Selenium

| Playwright | Selenium equivalent |
|---|---|
| `codegen` | Selenium IDE (record and generate code). |
| `test --ui` | Test runner GUI; no Selenium equivalent. |
| `show-trace` | Post-mortem debugging; no Selenium equivalent. |
| `open` | Element inspector, similar to browser devtools. |
| `pages/` | Page Object Model, same concept. |

### PAIMANA notes

| Topic | Note |
|---|---|
| Browser cache is global | Browsers live in `C:\Users\<you>\AppData\Local\ms-playwright`, not per project. If a newer project downloads newer builds, older projects may print "Removing unused browser" and need `npx playwright install` again. |
| Node version | Playwright officially supports Node 20, 22 and 24 LTS lines (the Jenkins agent runs v24). Odd-numbered releases such as Node 25 are not LTS; on an unexplained internal error try Node 22 LTS. |
| chromium is not Chrome | It is the open-source engine behind Chrome and Edge. For real Chrome or Edge add projects with `channel: 'chrome'` or `channel: 'msedge'` in `playwright.config.ts`. |
| GitHub Actions | If `.github/workflows/playwright.yml` exists, tests run on every push once the repo is on GitHub. |
| The minimum you ever need | `cd C:\Users\shiva\OneDrive\JavaSelenium\PAIMANA_Playwright_1.1` then `npx playwright test`. |

[↑ Contents](#contents)

## A. Optional shortcuts

Add to `"scripts"` in `package.json` if not there yet:

```json
"test:smoke":     "playwright test --grep @smoke",
"test:ui":        "playwright test --ui",
"test:debug":     "playwright test --debug --project=chromium",
"test:failed":    "playwright test --last-failed",
"codegen":        "playwright codegen https://iigdev.uatnegd.online/home",
"codegen:login":  "playwright codegen --save-storage=playwright/.auth/cpse.json https://iigdev.uatnegd.online/home",
"codegen:authed": "playwright codegen --load-storage=playwright/.auth/cpse.json https://iigdev.uatnegd.online/home"
```

Then: `npm run test:smoke`, `npm run test:ui`, `npm run test:debug`, `npm run test:failed`, `npm run codegen`, `npm run codegen:login`, `npm run codegen:authed`.

[↑ Contents](#contents)

## B. Getting help on any command

The exact options depend on the installed version; these print the authoritative list for 1.63.

| Command | Shows |
|---|---|
| `npx playwright --help` | All top-level commands (`test`, `codegen`, `open`, `install`, `show-report`, `show-trace`, `merge-reports`, `screenshot`, `pdf`, `clear-cache`, …). |
| `npx playwright test --help` | Every test runner option. |
| `npx playwright codegen --help` | Every codegen option. |
| `npx playwright install --help` | Browser install options and browser names. |

Docs: [Codegen](https://playwright.dev/docs/codegen) · [Command line](https://playwright.dev/docs/test-cli) · [Authentication](https://playwright.dev/docs/auth) · [Trace viewer](https://playwright.dev/docs/trace-viewer)

[↑ Contents](#contents)
