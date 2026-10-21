# Playwright Automation Guide: TypeScript/JavaScript & Java (Maven)

Every command below has a comment (what it does) and a **Purpose** (why you run it). Parts 7 and 8 are the reference for the PAIMANA_Playwright_1.1 TypeScript project.

---

<a id="contents"></a>
## Contents

Click any line to jump to it.

### Flow tree: what do you want to do?

- **I am starting out / setting up**
  - [Check Node, npm, npx](#part-1-prerequisites)
  - [PAIMANA TypeScript: 7-step setup](#part-8-8-5-setup-step-by-step)
  - [TypeScript project: existing project steps](#part-1-existing-project-step-by-step)
  - [Brand-new TypeScript project](#part-1-brand-new-project)
  - [Java/Maven: prerequisites](#part-2-prerequisites)
  - [Java/Maven: existing project steps](#part-2-existing-project-step-by-step)
  - [Java/Maven: brand-new project](#part-2-brand-new-project)
- **I want to run tests**
  - [The one command to remember](#part-7-the-command-to-remember)
  - [Run by browser](#part-7-run)
  - [Filter by file, tag or title](#part-7-filter)
  - [Retries, workers, timeouts, reporters](#part-8-8-6-more-run-options)
  - [Combine flags](#part-7-combine)
  - [Shorter npm scripts](#part-8-8-10-shorter-commands-npm-scripts)
- **I want the browser visible or invisible**
  - [Headed/headless on this project (HEADED=1)](#part-7-headed-vs-headless-this-project)
  - [Why maximize breaks on Jenkins](#part-3-why-maximizing-breaks-on-jenkins)
  - [Config with HEADLESS and 1920x1080](#part-3-playwright-config-js-ts)
- **I want to record or explore a page**
  - [Codegen: all options and tips](#part-8-8-7-codegen-record-tests)
  - [open, codegen, show-trace overview](#part-7-standalone-apps-not-running-tests)
- **Something failed**
  - [Common errors](#part-6)
  - [More errors and fixes](#part-8-8-11-errors-and-fixes-additional)
  - [Debug: UI mode, headed, inspector](#part-8-8-8-ui-mode-and-debugging)
  - [Reports and traces](#part-8-8-9-reports-and-traces)
  - [Firefox page.goto timeout on Jenkins](#part-6)
- **I am setting up Jenkins / CI**
  - [Headed and headless job commands](#part-4)
  - [Headless vs maximized strategy](#part-3)
- **I just need a command fast**
  - [Lookup by intent](#part-8-8-1-quick-lookup-by-intent)
  - [TypeScript vs Java command mapping](#part-5)
  - [The minimum to run everything](#part-8-8-13-the-minimum-you-ever-need-again)

### Full contents

- **[PART 1 — Playwright TypeScript / JavaScript project](#part-1)**
  - [Prerequisites](#part-1-prerequisites)
  - [Existing project: step by step](#part-1-existing-project-step-by-step)
  - [Brand-new project](#part-1-brand-new-project)
- **[PART 2 — Playwright Java (Maven) project](#part-2)**
  - [Prerequisites](#part-2-prerequisites)
  - [Existing project: step by step](#part-2-existing-project-step-by-step)
  - [Brand-new project](#part-2-brand-new-project)
- **[PART 3 — Headless vs maximized window strategy](#part-3)**
  - [Why maximizing breaks on Jenkins](#part-3-why-maximizing-breaks-on-jenkins)
  - [`playwright.config.js` / `.ts`](#part-3-playwright-config-js-ts)
- **[PART 4 — Jenkins job commands (Windows batch step)](#part-4)**
  - [1. Headed job (visible, maximized browsers)](#part-4-1-headed-job-visible-maximized-browsers)
  - [2. Headless CI job (recommended for production)](#part-4-2-headless-ci-job-recommended-for-production)
- **[PART 5 — Quick command mapping](#part-5)**
- **[PART 6 — Common errors](#part-6)**
- **[PART 7 — PAIMANA_Playwright_1.1 (Playwright + TypeScript) project reference](#part-7)**
  - [Mandatory files](#part-7-mandatory-files)
  - [Project structure](#part-7-project-structure)
  - [First-time setup](#part-7-first-time-setup)
  - [Headed vs headless (this project)](#part-7-headed-vs-headless-this-project)
  - [The command to remember](#part-7-the-command-to-remember)
  - [Run](#part-7-run)
  - [Filter](#part-7-filter)
  - [Debug](#part-7-debug)
  - [Report](#part-7-report)
  - [Combine](#part-7-combine)
  - [Optional: shorter commands](#part-7-optional-shorter-commands)
  - [Standalone apps (not running tests)](#part-7-standalone-apps-not-running-tests)
  - [Other CLI commands worth having](#part-7-other-cli-commands-worth-having)
  - [Notes](#part-7-notes)
- **[PART 8 — PAIMANA deep reference: lookup by intent, codegen, UI mode, traces, errors](#part-8)**
  - [8.1 Quick lookup by intent](#part-8-8-1-quick-lookup-by-intent)
  - [8.2 System check commands](#part-8-8-2-system-check-commands)
  - [8.3 Setup and install commands](#part-8-8-3-setup-and-install-commands)
  - [8.4 Mandatory files: testDir is not always `tests`](#part-8-8-4-mandatory-files-testdir-is-not-always-tests)
  - [8.5 Setup, step by step](#part-8-8-5-setup-step-by-step)
  - [8.6 More run options](#part-8-8-6-more-run-options)
  - [8.7 Codegen (record tests)](#part-8-8-7-codegen-record-tests)
  - [8.8 UI mode and debugging](#part-8-8-8-ui-mode-and-debugging)
  - [8.9 Reports and traces](#part-8-8-9-reports-and-traces)
  - [8.10 Shorter commands (npm scripts)](#part-8-8-10-shorter-commands-npm-scripts)
  - [8.11 Errors and fixes (additional)](#part-8-8-11-errors-and-fixes-additional)
  - [8.12 More notes](#part-8-8-12-more-notes)
  - [8.13 The minimum you ever need again](#part-8-8-13-the-minimum-you-ever-need-again)

---

Run PowerShell/CMD commands from the project folder unless stated otherwise.


[↑ Back to contents](#contents)

---

<a id="part-1"></a>
## PART 1 — Playwright TypeScript / JavaScript project

<a id="part-1-prerequisites"></a>
### Prerequisites

| Tool | Purpose |
|---|---|
| Node.js v18+ (nodejs.org) | Runtime that runs Playwright and npm. Reopen the terminal after installing. |
| npm / npx | Bundled with Node. `npm` installs packages; `npx` runs a package's command. |
| VS Code (recommended) | Editor with the Playwright Test extension. |

```bash
# Print the installed Node.js version
node -v
# Purpose: confirm Node is installed and is v18 or newer.

# Print the npm version
npm -v
# Purpose: confirm the package manager works.

# Print the npx version
npx -v
# Purpose: confirm the package runner works (used for every `npx playwright ...` command).
```

<a id="part-1-existing-project-step-by-step"></a>
### Existing project: step by step

```powershell
# Move into the project root folder
cd C:\Users\shiva\OneDrive\JavaSelenium\PAIMANA_Playwright_1.1
# Purpose: every command assumes this folder. From C:\Users\shiva you get
#          "EPERM ... Temp\WinSAT" because Playwright scans your whole profile.

# List the folder contents
dir
# Purpose: confirm package.json and playwright.config.ts (or .js) are present, i.e. you are in the right place.
```

```bash
# Show changed files and the current branch
git status
# Purpose: see what is modified or untracked before pulling, so nothing is overwritten by surprise.

# Link local 'main' to remote 'origin/main'
git branch --set-upstream-to=origin/main main
# Purpose: tell Git which remote branch 'main' follows, so plain `git pull` / `git push` work.

# Download remote commits and merge them into local main
git pull origin main
# Purpose: get the latest tests and config written by others (or by CI) before you run anything.

# Pull when local and remote histories share no common ancestor
git pull origin main --allow-unrelated-histories
# Purpose: fallback only. Use when Git refuses with "refusing to merge unrelated histories"
#          (for example the GitHub repo was created with a README and the local repo separately).

# Install every library listed in package.json into node_modules
npm install
# Purpose: get project dependencies (@playwright/test, etc.). Use `npm ci` instead on CI for an exact, repeatable install.

# Download the browser binaries (Chromium, Firefox, WebKit)
npx playwright install
# Purpose: Playwright drives its own browser builds. Needed once per machine and after every Playwright upgrade.

# Verify the Playwright version
npx playwright --version
# Purpose: confirm the package is installed and see which version you are on.

# List all discovered tests without running them
npx playwright test --list
# Purpose: check that test paths, filters and projects are picked up (first thing to try on "No tests found").

# Run all tests on all configured browsers
npx playwright test
# Purpose: the main run. Results go to the console, test-results/ and playwright-report/.

# Open the HTML report in a local web server
npx playwright show-report
# Purpose: inspect pass/fail per test, screenshots, attachments and traces for failures.
```

<a id="part-1-brand-new-project"></a>
### Brand-new project

```bash
# Create a folder for the project
mkdir my-playwright-project
# Purpose: a clean location for the new project.

# Enter the folder
cd my-playwright-project
# Purpose: the scaffold command writes into the current folder.

# Scaffold a new Playwright project
npm init playwright@latest
# Purpose: creates package.json, playwright.config, an example test and optional GitHub Actions workflow.
# Answer the prompts: TypeScript -> tests folder name -> GitHub Actions (yes/no) -> install browsers (yes)

# Run the example tests
npx playwright test
# Purpose: confirm the new setup works end to end.
```


[↑ Back to contents](#contents)

---

<a id="part-2"></a>
## PART 2 — Playwright Java (Maven) project

<a id="part-2-prerequisites"></a>
### Prerequisites

| Tool | Purpose |
|---|---|
| JDK 17 or 21 (adoptium.net) | Playwright Java needs Java 8+, but match your other tools (17/21). |
| Apache Maven | Build tool: dependencies, compile, test. Bundled in most IDEs too. |
| Eclipse / IntelliJ (optional) | IDE. The command line works standalone. |

```bash
# Print the Java version
java -version
# Purpose: confirm the JDK is installed and which version the shell uses.

# Print Maven's version and the JDK it is bound to
mvn -version
# Purpose: Maven must report the SAME Java version as `java -version`; if not, set JAVA_HOME.
```

```bat
:: Set JAVA_HOME permanently for your user (close and reopen CMD afterwards)
setx JAVA_HOME "C:\Program Files\Java\jdk-21.0.12"
:: Purpose: make Maven use the JDK you want when it points at an old one.
```

<a id="part-2-existing-project-step-by-step"></a>
### Existing project: step by step

```powershell
# Move into the Java project root
cd C:\Users\shiva\OneDrive\JavaSelenium\paimana-playwright-java
# Purpose: Maven commands only work next to pom.xml.

# List the folder contents
dir
# Purpose: confirm pom.xml is present.
```

```bash
# Sync with the remote repository
git branch --set-upstream-to=origin/main main
# Purpose: link local main to origin/main (only needed once per branch).
git pull origin main
# Purpose: bring down the latest code before building.

# Delete old build output (target/) and compile the sources
mvn clean compile
# Purpose: download dependencies and make sure the code compiles from scratch.

# Install Playwright's browsers (Java projects need this as a separate step; npm is not involved)
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
# Purpose: the Java equivalent of `npx playwright install`. Needs exec-maven-plugin in pom.xml.

# Fallback if exec-maven-plugin is not in pom.xml: write the classpath to a file
mvn dependency:build-classpath -Dmdep.outputFile=cp.txt
# Purpose: collect all dependency jars so Java can run the Playwright CLI directly.

# Fallback, continued: run the Playwright CLI with that classpath
java -cp "target/classes;%cp.txt%" com.microsoft.playwright.CLI install
# Purpose: install browsers without exec-maven-plugin.
# Note: in CMD, %cp.txt% does not read the file. Use: for /f "delims=" %i in (cp.txt) do java -cp "target/classes;%i" com.microsoft.playwright.CLI install

# Run every test class matching the Surefire patterns (e.g. *Test.java)
mvn test
# Purpose: the main run. Reports land in target/surefire-reports/.

# Run one specific test class
mvn test -Dtest=LoginTest
# Purpose: quick feedback on a single class while developing.

# Run a TestNG suite file (if configured in the pom)
mvn test -DsuiteXmlFile=suites/smoke.xml
# Purpose: run a named group of tests (e.g. smoke) defined in XML.
```

Results: `target/surefire-reports/` (open `index.html` if an HTML reporter is configured).

<a id="part-2-brand-new-project"></a>
### Brand-new project

```bash
# Generate a Maven project skeleton
mvn archetype:generate -DgroupId=com.paimana -DartifactId=paimana-playwright-java -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
# Purpose: creates the folder layout and a starter pom.xml without prompts.

# Enter the new project
cd paimana-playwright-java
```

Add inside `<dependencies>` in `pom.xml`:

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

```bash
# Download dependencies and compile
mvn clean compile
# Purpose: confirm the pom is valid and Playwright resolves.

# Install browsers (same command as in the existing-project steps above)
# Purpose: Playwright needs its browser builds before the first test.

# Run your tests
mvn test
```


[↑ Back to contents](#contents)

---

<a id="part-3"></a>
## PART 3 — Headless vs maximized window strategy

<a id="part-3-why-maximizing-breaks-on-jenkins"></a>
### Why maximizing breaks on Jenkins

- **Session 0:** a Jenkins Windows Service runs on an isolated desktop with no real display. Windows are clamped to a virtual 1024x768 or hidden.
- **Interactive mode** (`java -jar jenkins.war`): windows appear on your desktop, but locking Windows (Win + L) suspends rendering and maximize calls fail.
- **Headless has no real window:** `--start-maximized` and OS maximize are ignored. Set an explicit 1920x1080 viewport so the tests behave the same everywhere.

<a id="part-3-playwright-config-js-ts"></a>
### `playwright.config.js` / `.ts`

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
      viewport: null,                      // null = page fills the maximized OS window
      launchOptions: { args: ['--start-maximized'] }, // Chromium flag: maximize on launch
    };
  }

  // Firefox & WebKit do not support --start-maximized: use a fixed size instead
  return {
    viewport: FULL_HD,
    launchOptions: { args: ['--width=1920', '--height=1080'] },
  };
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


[↑ Back to contents](#contents)

---

<a id="part-4"></a>
## PART 4 — Jenkins job commands (Windows batch step)

<a id="part-4-1-headed-job-visible-maximized-browsers"></a>
### 1. Headed job (visible, maximized browsers)

Use when Jenkins was started interactively (`java -jar jenkins.war`). Keep the desktop unlocked while tests run.

```bat
:: Go to the repository on the Jenkins machine
cd C:\Users\shiva\Projects\PlaywrightBasic1
:: Purpose: all later commands need the project folder.

:: Pull the latest committed code from GitHub
git pull origin main
:: Purpose: test the newest version of the scripts.

:: Install exactly what package-lock.json says
call npm ci
:: Purpose: fast, repeatable, clean install. `call` is required so the batch file continues after npm.

:: Make sure the browser binaries exist for this Playwright version
call npx playwright install
:: Purpose: avoids "Executable doesn't exist" errors on a fresh or upgraded agent.

:: Force visible browsers
set HEADLESS=false
:: Purpose: browsers open on the interactive desktop (read by resolveHeadless()).

:: Run the suite
call npx playwright test
:: Purpose: execute all tests; a non-zero exit code marks the Jenkins build as failed.
```

<a id="part-4-2-headless-ci-job-recommended-for-production"></a>
### 2. Headless CI job (recommended for production)

Unaffected by screen locks or remote-desktop disconnects.

```bat
cd C:\Users\shiva\Projects\PlaywrightBasic1
:: Purpose: enter the project folder.

git pull origin main
:: Purpose: fetch the latest test scripts.

call npm ci
:: Purpose: clean, lock-file-exact dependency install.

call npx playwright install
:: Purpose: ensure browsers are present and match the Playwright version.

set HEADLESS=true
:: Purpose: run invisibly with the fixed 1920x1080 viewport.

call npx playwright test
:: Purpose: run the suite; HTML report goes to playwright-report/ for Jenkins' HTML Publisher.
```


[↑ Back to contents](#contents)

---

<a id="part-5"></a>
## PART 5 — Quick command mapping

| Task | TypeScript / Playwright CLI | Java / Maven | Purpose |
|---|---|---|---|
| Pull code | `git pull origin main` | `git pull origin main` | Sync the local branch with GitHub. |
| Install libs | `npm install` or `npm ci` | `mvn clean compile` | Download project dependencies. |
| Install browsers | `npx playwright install` | `mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"` | Download browser binaries. |
| Run all tests | `npx playwright test` | `mvn test` | Execute every test. |
| Run one test | `npx playwright test tests/home.spec.js` | `mvn test -Dtest=LoginTest` | Run a single file or class. |
| Filter by tag | `npx playwright test --grep @smoke` | `mvn test -DsuiteXmlFile=suites/smoke.xml` | Run a tagged group. |
| One browser | `npx playwright test --project=chromium` | `mvn test -Dbrowser=chromium` (only if you wired it) | Run on a single browser. |
| Force headless | `set HEADLESS=true` then `npx playwright test` | `mvn test -Dheadless=true` (only if you wired it) | Background run at 1080p. |
| Force headed | `set HEADLESS=false` then `npx playwright test` | `mvn test -Dheadless=false` (only if you wired it) | Visible browser windows. |
| View report | `npx playwright show-report` | open `target/surefire-reports` | Inspect results. |


[↑ Back to contents](#contents)

---

<a id="part-6"></a>
## PART 6 — Common errors

| Error | Cause and fix |
|---|---|
| `EPERM ... Temp\WinSAT` | Wrong folder. `cd` into the project first. |
| `Cannot find module '@playwright/test'` | Dependencies missing. Run `npm install` (or `npm ci`). |
| `Executable doesn't exist at ms-playwright\...` | Browsers missing. Run `npx playwright install`. |
| `package does not exist` / `cannot find symbol` (Java) | Playwright dependency missing in `pom.xml`, or `mvn clean compile` not run. |
| `BUILD FAILURE ... Failed to delete target` | OneDrive is locking files. Pause sync or move the project out of OneDrive. |
| `'mvn'` or `'npx'` not recognized | Not installed, or the terminal was not reopened after installing. |
| `page.goto: Timeout 30000ms exceeded` on Firefox only (Jenkins run) | Firefox waits for the `load` event, which the slow UAT portal does not fire in 30 s. Use `page.goto(path, { waitUntil: 'domcontentloaded', timeout: 60000 })` in `HomePage.open()`, or set `navigationTimeout: 60000` under `use:` in the config. Also confirm the Jenkins machine can reach `iigdev.uatnegd.online` in Firefox. |


[↑ Back to contents](#contents)

---

<a id="part-7"></a>
## PART 7 — PAIMANA_Playwright_1.1 (Playwright + TypeScript) project reference

5 tests across 3 browsers = 15 total runs. Folder: `C:\Users\shiva\OneDrive\JavaSelenium\PAIMANA_Playwright_1.1`

<a id="part-7-mandatory-files"></a>
### Mandatory files

The project will not run without these three. Everything else is supporting code imported by the specs.

| File | Purpose |
|---|---|
| `package.json` | Declares `@playwright/test` as a dependency and holds the npm scripts. |
| `playwright.config.ts` | Browsers, `testDir`, `baseURL`, reporters, timeouts. |
| `tests/*.spec.ts` | The actual tests. |

<a id="part-7-project-structure"></a>
### Project structure

| Folder / file | Purpose |
|---|---|
| `fixtures/` | Custom test fixtures (inject page objects into tests). |
| `pages/` | Page Object Model classes (`BasePage`, `HomePage`, `LoginPage`). |
| `test-data/` | Static test data (`users.json`). |
| `tests/` | Spec files: `api.spec.ts`, `example.spec.ts`, `home.spec.ts`. |
| `utils/` | Shared helpers. |
| `tsconfig.json` | TypeScript compiler options. |

<a id="part-7-first-time-setup"></a>
### First-time setup

```powershell
# Move into the project root
cd C:\Users\shiva\OneDrive\JavaSelenium\PAIMANA_Playwright_1.1
# Purpose: run from the project root, not your home folder. From C:\Users\shiva you get
#          "EPERM: operation not permitted, scandir '...\Temp\WinSAT'".
#          You are in the right place when `dir` shows package.json and playwright.config.ts.

# Install the project dependencies
npm install
# Purpose: download the libraries listed in package.json into node_modules.

# Download the browsers
npx playwright install
# Purpose: get Chromium, Firefox and WebKit. Prints nothing if they are already cached.
```

<a id="part-7-headed-vs-headless-this-project"></a>
### Headed vs headless (this project)

```powershell
# Visible browser, one test file, one browser
cd C:\Users\shiva\OneDrive\JavaSelenium\PAIMANA_Playwright_1.1
# Purpose: enter the project folder.
$env:HEADED=1
# Purpose: switches the browser window on for this PowerShell window. This project reads HEADED=1,
#          NOT the HEADLESS variable used in the playwright.config.js example in Part 3.
npx playwright test tests/home.spec.ts --project=chromium
# Purpose: run only home.spec.ts on Chromium so you can watch it. Use `Remove-Item Env:HEADED` afterwards to go back to headless.

# Headless (default): no browser window
npx playwright test
# Purpose: run everything on all 3 browsers in the background.
npx playwright test --project=chromium
# Purpose: same, but Chromium only (fastest).
```

<a id="part-7-the-command-to-remember"></a>
### The command to remember

```bash
npx playwright test
# Purpose: runs all tests on all 3 browsers. Everything below is a variation of this command.
```

<a id="part-7-run"></a>
### Run

```bash
npx playwright test                                       # all tests, all 3 browsers. Purpose: full regression run.
npx playwright test --project=chromium                    # one browser. Purpose: fastest feedback.
npx playwright test --project=firefox                     # Firefox only. Purpose: check Gecko-specific behaviour.
npx playwright test --project=webkit                      # Safari's engine. Purpose: check Safari-like behaviour.
npx playwright test --project=chromium --project=firefox  # two browsers. Purpose: pick any combination.
```

<a id="part-7-filter"></a>
### Filter

```bash
npx playwright test tests/home.spec.ts   # one file. Purpose: run only the tests in that spec.
npx playwright test --grep @smoke        # by tag (@smoke, @regression, @api). Purpose: run a tagged group.
npx playwright test -g "login"           # by test title (substring). Purpose: run tests whose name contains the text.
npx playwright test --list               # list tests, don't run. Purpose: check that a path/filter matches before a long run.
```

<a id="part-7-debug"></a>
### Debug

```bash
npx playwright test --ui      # interactive mode. Purpose: pick tests, watch them and time-travel through steps; best while writing tests.
npx playwright test --headed  # watch the browser. Purpose: see what the test does on screen.
npx playwright test --debug   # step through with the Inspector. Purpose: pause before each action to find why a locator or click fails.
```

<a id="part-7-report"></a>
### Report

```bash
npx playwright show-report    # open the report from the last run. Purpose: see results, screenshots and traces of failures.
```

<a id="part-7-combine"></a>
### Combine

Flags stack, which is where the speed comes from:

```bash
npx playwright test --project=chromium --grep @smoke --headed
# Purpose: one browser + only smoke tests + visible window = the quickest way to check a change.
```

<a id="part-7-optional-shorter-commands"></a>
### Optional: shorter commands

Add a `scripts` section to `package.json`:

```json
"scripts": {
  "test": "playwright test",
  "test:ui": "playwright test --ui",
  "test:smoke": "playwright test --grep @smoke"
}
```

Then run `npm test`, `npm run test:ui` or `npm run test:smoke` from the project folder. Purpose: shorter commands you can remember.

<a id="part-7-standalone-apps-not-running-tests"></a>
### Standalone apps (not running tests)

```bash
npx playwright open                         # browser with the Inspector attached. Purpose: explore a page and try locators.
npx playwright open https://example.com     # open a specific URL. Purpose: explore that site directly.
npx playwright codegen                      # record clicks and generate test code. Purpose: get locators and a starting test.
npx playwright codegen https://example.com  # record against a specific site. Purpose: start recording on the right page.
npx playwright show-trace                   # trace viewer (file picker). Purpose: inspect a failure step by step.
npx playwright show-trace trace.zip         # open a specific trace file. Purpose: open a trace from a failed run or CI.
```

<a id="part-7-other-cli-commands-worth-having"></a>
### Other CLI commands worth having

```bash
npx playwright install            # download browsers. Purpose: first setup and after every Playwright upgrade.
npx playwright install chromium   # one browser only. Purpose: smaller, quicker download.
npx playwright install-deps       # OS-level dependencies. Purpose: Linux / CI only; not needed on Windows.
npx playwright --version          # installed version. Purpose: confirm install and check versions.
npx playwright --help             # full command list. Purpose: see every command and option.
npx playwright clear-cache        # clear Playwright's test/compile cache. Purpose: use when an edit to a test or config seems ignored.
                                  # It does NOT delete the downloaded browsers; `npx playwright uninstall` does that.
```

<a id="part-7-notes"></a>
### Notes

| Topic | Note |
|---|---|
| OneDrive | The project sits in a synced folder. On `EBUSY` / `EPERM` errors mid-run, pause OneDrive sync or move the project outside it. |
| Browser cache | Browsers are cached globally in `C:\Users\<you>\AppData\Local\ms-playwright`, not per project. If `npx playwright install` prints nothing, they are already downloaded. |
| chromium is not Chrome | It is the open-source engine behind Chrome and Edge. For real Chrome or Edge, add projects with `channel: 'chrome'` or `channel: 'msedge'` in `playwright.config.ts`. |


[↑ Back to contents](#contents)

---

<a id="part-8"></a>
## PART 8 — PAIMANA deep reference: lookup by intent, codegen, UI mode, traces, errors

Extends Part 7. Commands already explained there are not repeated.

<a id="part-8-8-1-quick-lookup-by-intent"></a>
### 8.1 Quick lookup by intent

| I want to... | Command | Purpose |
|---|---|---|
| Run my tests | `npx playwright test` | Full run on every configured browser. |
| Run fast while developing | `npx playwright test --project=chromium` | One browser, roughly 3x faster. |
| Use a GUI to run, watch and debug | `npx playwright test --ui` | Test list, step timeline, DOM snapshots. |
| Write a NEW test without knowing locators | `npx playwright codegen <url>` | Record clicks and get generated code. |
| Explore a page and find locators, no recording | `npx playwright open <url>` | Inspector attached, nothing recorded. |
| Find out why a test failed | `npx playwright show-trace` | Post-mortem of a failed run. |
| See results of the last run | `npx playwright show-report` | HTML report. |
| Check which tests exist without running them | `npx playwright test --list` | Verify paths and filters. |

**Coming from Selenium**

| Playwright | Selenium equivalent |
|---|---|
| `codegen` | Selenium IDE (record and generate code) |
| `test --ui` | Test runner GUI; no Selenium equivalent |
| `show-trace` | Post-mortem debugging; no Selenium equivalent |
| `open` | Element inspector, similar to browser devtools |
| `pages/` | Page Object Model, same concept |

<a id="part-8-8-2-system-check-commands"></a>
### 8.2 System check commands

```bash
node -v                    # Node.js version. Purpose: Playwright needs v18+. If it fails, Node is missing or CMD was not reopened; everything else depends on it.
npm -v                     # npm version. Purpose: npm ships with Node, so if node -v worked this does too; it installs dependencies.
npx -v                     # npx version. Purpose: runs the Playwright CLI from node_modules without a global install; every playwright command uses it.
npx playwright --version   # Playwright version in THIS project (e.g. 1.59.1). Purpose: confirm the CLI is reachable; different folders can report different versions.
npx playwright --help      # every command and flag. Purpose: look up a flag you forgot.
dir                        # list the current folder. Purpose: after cd, confirm you see package.json and playwright.config.ts; if not, you are in the wrong folder.
cd <path>                  # change folder. Purpose: the most important command here; Playwright scans the folder you stand in, so the wrong one causes most errors.
```

<a id="part-8-8-3-setup-and-install-commands"></a>
### 8.3 Setup and install commands

```bash
npm install                      # read package.json and download dependencies into node_modules.
                                 # Purpose: run once per project, and again after cloning or when package.json changes. Without it: "Cannot find module '@playwright/test'".
npx playwright install           # download Chromium, Firefox and WebKit (about 500 MB) to C:\Users\<you>\AppData\Local\ms-playwright.
                                 # Purpose: separate from npm install because browsers are not npm packages. Silence means they are already downloaded.
npx playwright install chromium  # one browser only. Purpose: faster and much smaller when you only test in Chromium or on a CI machine with little disk.
npx playwright install-deps      # OS-level libraries the browsers need. Purpose: Linux / CI runners only; not needed on Windows.
npx playwright clear-cache       # clear Playwright's test/compile cache. Purpose: use when an edit to a test or config seems ignored.
                                 # It does NOT remove the downloaded browsers; `npx playwright uninstall` (or `install --force` to re-download) handles those.
npm init playwright@latest       # scaffold a brand-new project in the current folder (TypeScript or JavaScript, test folder, GitHub Actions, install browsers).
                                 # Purpose: NEW projects only, never inside an existing one.
```

<a id="part-8-8-4-mandatory-files-testdir-is-not-always-tests"></a>
### 8.4 Mandatory files: testDir is not always `tests`

The runner reads `playwright.config.ts` first and its location defines the project root. Tests live in `<testDir>/*.spec.ts`; without them the runner reports 0 tests.

| Project | `testDir` |
|---|---|
| PAIMANA_Playwright_1.1 | `./tests` |
| playwright-learning | `./playwright` |

Other generated folders: `node_modules/` (installed dependencies, never commit), `test-results/` (traces and screenshots from failed runs), `playwright-report/` (HTML report of the last run).

<a id="part-8-8-5-setup-step-by-step"></a>
### 8.5 Setup, step by step

| Step | Command | Purpose |
|---|---|---|
| 0 | `node -v`, `npm -v`, `npx -v` | Check prerequisites. |
| 1 (mandatory) | `cd C:\Users\shiva\OneDrive\JavaSelenium\PAIMANA_Playwright_1.1` then `dir` | Enter the project. Shortcut: open the folder in File Explorer, click the address bar, type `cmd`, press Enter; the terminal opens already in that folder. |
| 2 | `npm install` | Install dependencies. |
| 3 | `npx playwright install` | Install browsers. |
| 4 | `npx playwright --version` | Check the version. |
| 5 | `npx playwright test --list` | Verify tests are found. Expect `Total: 15 tests in 3 files`. |
| 6 | `npx playwright test` | Run. |
| 7 | `npx playwright show-report` | View results. |

<a id="part-8-8-6-more-run-options"></a>
### 8.6 More run options

```bash
npx playwright test --workers=1        # one test at a time. Purpose: use when tests interfere with each other, or for cleaner output while debugging.
npx playwright test --retries=2        # retry a failed test up to 2 times. Purpose: isolate flaky tests; a test that passes on retry is flaky, not broken.
npx playwright test --reporter=list    # change output format (list, line, dot, html, json). Purpose: readable per-test output in the terminal.
npx playwright test example            # any file whose path contains "example". Purpose: partial match, no full path needed.
npx playwright test --grep-invert @slow  # everything EXCEPT the matching tag. Purpose: skip known-slow tests.
npx playwright test tests/home.spec.ts:13  # the single test at line 13. Purpose: the most precise filter; line numbers come from --list output.
npx playwright test --timeout=60000    # per-test timeout in milliseconds. Purpose: stop a slow environment causing false failures.
```

Smoke tests only, one browser, visible: `npx playwright test --project=chromium --grep @smoke --headed` finishes in a few seconds instead of about 20. Use `@smoke` for the quick CI run and the full suite for regression; save the 3-browser run for before you commit.

<a id="part-8-8-7-codegen-record-tests"></a>
### 8.7 Codegen (record tests)

```bash
npx playwright codegen                                     # blank browser + Inspector; clicks become TypeScript. Purpose: like Selenium IDE, with better locators.
npx playwright codegen https://playwright.dev             # start on a URL. Purpose: best way to learn locator syntax; click around and read the code.
npx playwright codegen https://iigdev.uatnegd.online/home # record against PAIMANA.
npx playwright codegen <url> -o tests/login.spec.ts        # write straight to a file.
                                                           # WARNING: -o OVERWRITES the file without asking; run it from the project folder or the path will not exist.
npx playwright codegen <url> -o codegen-output.ts          # SAFER: record to a scratch file, copy the useful parts into a real spec, add the file to .gitignore.
npx playwright codegen <url> --target=javascript           # JavaScript instead of TypeScript.
npx playwright codegen <url> --browser=firefox             # record in Firefox instead of Chromium.
npx playwright codegen <url> --device="iPhone 13"          # mobile viewport and user agent. Purpose: responsive / mobile tests.
npx playwright codegen <url> --save-storage=auth.json      # save cookies + localStorage when you close the browser. Purpose: store a logged-in session.
npx playwright codegen <url> --load-storage=auth.json      # start already logged in. Purpose: the practical answer to captchas: log in manually ONCE, reuse it afterwards.
```

Tips:
- Click AND type. Clicking alone records `.click()` with no `.fill()`.
- `.first()` and `.nth(1)` in generated code mean several elements matched. Replace them with scoped locators; index positions break when the page changes.
- Captchas cannot be automated. Ask the dev team to disable captcha on UAT, or use the `--save-storage` / `--load-storage` pattern.
- Prefer `getByRole()` over XPath; role locators survive layout and styling changes far better.

<a id="part-8-8-8-ui-mode-and-debugging"></a>
### 8.8 UI mode and debugging

```bash
npx playwright test --ui           # full graphical runner: test list, step timeline, DOM snapshots, network and console tabs, watch mode.
                                   # Purpose: the best place to WRITE and DEBUG tests.
npx playwright test --headed       # visible browser instead of headless. Purpose: watch what happens; slower but often the quickest way to spot a problem.
npx playwright test --debug        # Inspector pauses before each step. Purpose: find which action broke a failing test.
npx playwright open                # Inspector, records nothing. Purpose: explore a page and read off locators.
npx playwright open https://playwright.dev  # same, starting at a URL.
```

Inside UI mode:

| Control | Purpose |
|---|---|
| Play triangle at top of the TESTS panel | Run everything. |
| Play button on a test row | Run just that test. |
| Eye icon | Watch mode: re-runs on file save. |
| Filter box | Type `@smoke` to filter by tag. |
| Actions panel | Lists every step; click one to time-travel. |
| Locator tab + target icon | Hover elements to generate locators. |
| Projects: chromium | Defaults to one browser; expand to add more. |

<a id="part-8-8-9-reports-and-traces"></a>
### 8.9 Reports and traces

```bash
npx playwright show-report                 # open the HTML report of the last run at http://localhost:9323.
                                           # Purpose: pass/fail per test per browser, durations, errors, screenshots, attached traces.
npx playwright show-report --port=9324     # different port. Purpose: use when 9323 is taken by an old server you forgot to close.
npx playwright show-trace                  # trace viewer with a file picker.
npx playwright show-trace test-results/<folder>/trace.zip  # open a specific trace.
                                           # Purpose: DOM snapshot timeline, every network request, console output and a screenshot per step of a failed test; inspect a failure without reproducing it.
```

Traces are only produced when `playwright.config.ts` sets something like `trace: 'on-first-retry'`. They appear in `test-results/`.

<a id="part-8-8-10-shorter-commands-npm-scripts"></a>
### 8.10 Shorter commands (npm scripts)

```json
"scripts": {
  "test": "playwright test",
  "test:ui": "playwright test --ui",
  "test:smoke": "playwright test --grep @smoke",
  "test:chromium": "playwright test --project=chromium",
  "report": "playwright show-report"
}
```

```bash
npm test             # runs the "test" script; "run" is not needed for this one.
npm run test:ui      # runs any other script by name; "run" is required.
```

Purpose: shorter to type, and it documents for your team which commands matter on this project.

<a id="part-8-8-11-errors-and-fixes-additional"></a>
### 8.11 Errors and fixes (additional)

| Error | Cause and fix |
|---|---|
| `ENOENT: no such file or directory, open 'C:\Users\shiva\tests\login.spec.ts'` | Wrong folder, so `tests/` does not exist there. `cd` into the project first. |
| `'C:\Users\shiva' is not recognized as an internal or external command` | You pasted the prompt text with the command. Type only what comes AFTER the `>`. |
| `Executable doesn't exist at ...ms-playwright\chromium-XXXX` | The browser build for this Playwright version is missing, often because another project downloaded newer builds. Run `npx playwright install`. |
| `Error: No tests found` / `Total: 0 tests in 0 files` | Wrong folder, `testDir` does not match the real folder, or files are not named `*.spec.ts`. |
| `Timeout 30000ms exceeded` | Slow site, or the locator never matched. Diagnose with `npx playwright test --debug`; for slow UAT pages also raise `timeout` / `navigationTimeout`. |
| `Port 9323 already in use` | An old report server is running. Close it, or use `npx playwright show-report --port=9324`. |
| Terminal looks frozen, no prompt returns | Normal. `open`, `codegen`, `show-report`, `show-trace` and `test --ui` block until their window is closed. Press Ctrl+C, then Y. |

<a id="part-8-8-12-more-notes"></a>
### 8.12 More notes

| Topic | Note |
|---|---|
| Browser cache is global | If a newer project downloads newer builds, older projects may print "Removing unused browser" and need `npx playwright install` again. |
| Node version | Playwright officially supports Node 18/20/22/24 (your Jenkins agent runs v24). Odd-numbered current releases such as Node 25 are not LTS; on an unexplained internal error try Node 22 LTS. |
| GitHub Actions | If `.github/workflows/playwright.yml` exists, tests run automatically on every push once the repo is on GitHub. |
| `.gitignore` | Never commit `node_modules/`, `test-results/`, `playwright-report/`, `blob-report/`, `playwright/.cache/`. |

<a id="part-8-8-13-the-minimum-you-ever-need-again"></a>
### 8.13 The minimum you ever need again

```powershell
cd C:\Users\shiva\OneDrive\JavaSelenium\PAIMANA_Playwright_1.1
npx playwright test
# Purpose: enter the project and run everything. Everything else in this guide is a variation of these two lines.
```


[↑ Back to contents](#contents)
