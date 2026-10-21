# Playwright Automation Guide: TypeScript/JavaScript & Java (Maven)

Every command below has a comment (what it does) and a **Purpose** (why you run it). Part 7 is the reference for the PAIMANA_Playwright_1.1 TypeScript project.
Run PowerShell/CMD commands from the project folder unless stated otherwise.

---

## PART 1 — Playwright TypeScript / JavaScript project

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

---

## PART 2 — Playwright Java (Maven) project

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

---

## PART 3 — Headless vs maximized window strategy

### Why maximizing breaks on Jenkins

- **Session 0:** a Jenkins Windows Service runs on an isolated desktop with no real display. Windows are clamped to a virtual 1024x768 or hidden.
- **Interactive mode** (`java -jar jenkins.war`): windows appear on your desktop, but locking Windows (Win + L) suspends rendering and maximize calls fail.
- **Headless has no real window:** `--start-maximized` and OS maximize are ignored. Set an explicit 1920x1080 viewport so the tests behave the same everywhere.

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

---

## PART 4 — Jenkins job commands (Windows batch step)

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

---

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

---

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

---

## PART 7 — PAIMANA_Playwright_1.1 (Playwright + TypeScript) project reference

5 tests across 3 browsers = 15 total runs. Folder: `C:\Users\shiva\OneDrive\JavaSelenium\PAIMANA_Playwright_1.1`

### Mandatory files

The project will not run without these three. Everything else is supporting code imported by the specs.

| File | Purpose |
|---|---|
| `package.json` | Declares `@playwright/test` as a dependency and holds the npm scripts. |
| `playwright.config.ts` | Browsers, `testDir`, `baseURL`, reporters, timeouts. |
| `tests/*.spec.ts` | The actual tests. |

### Project structure

| Folder / file | Purpose |
|---|---|
| `fixtures/` | Custom test fixtures (inject page objects into tests). |
| `pages/` | Page Object Model classes (`BasePage`, `HomePage`, `LoginPage`). |
| `test-data/` | Static test data (`users.json`). |
| `tests/` | Spec files: `api.spec.ts`, `example.spec.ts`, `home.spec.ts`. |
| `utils/` | Shared helpers. |
| `tsconfig.json` | TypeScript compiler options. |

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

### The command to remember

```bash
npx playwright test
# Purpose: runs all tests on all 3 browsers. Everything below is a variation of this command.
```

### Run

```bash
npx playwright test                                       # all tests, all 3 browsers. Purpose: full regression run.
npx playwright test --project=chromium                    # one browser. Purpose: fastest feedback.
npx playwright test --project=firefox                     # Firefox only. Purpose: check Gecko-specific behaviour.
npx playwright test --project=webkit                      # Safari's engine. Purpose: check Safari-like behaviour.
npx playwright test --project=chromium --project=firefox  # two browsers. Purpose: pick any combination.
```

### Filter

```bash
npx playwright test tests/home.spec.ts   # one file. Purpose: run only the tests in that spec.
npx playwright test --grep @smoke        # by tag (@smoke, @regression, @api). Purpose: run a tagged group.
npx playwright test -g "login"           # by test title (substring). Purpose: run tests whose name contains the text.
npx playwright test --list               # list tests, don't run. Purpose: check that a path/filter matches before a long run.
```

### Debug

```bash
npx playwright test --ui      # interactive mode. Purpose: pick tests, watch them and time-travel through steps; best while writing tests.
npx playwright test --headed  # watch the browser. Purpose: see what the test does on screen.
npx playwright test --debug   # step through with the Inspector. Purpose: pause before each action to find why a locator or click fails.
```

### Report

```bash
npx playwright show-report    # open the report from the last run. Purpose: see results, screenshots and traces of failures.
```

### Combine

Flags stack, which is where the speed comes from:

```bash
npx playwright test --project=chromium --grep @smoke --headed
# Purpose: one browser + only smoke tests + visible window = the quickest way to check a change.
```

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

### Standalone apps (not running tests)

```bash
npx playwright open                         # browser with the Inspector attached. Purpose: explore a page and try locators.
npx playwright open https://example.com     # open a specific URL. Purpose: explore that site directly.
npx playwright codegen                      # record clicks and generate test code. Purpose: get locators and a starting test.
npx playwright codegen https://example.com  # record against a specific site. Purpose: start recording on the right page.
npx playwright show-trace                   # trace viewer (file picker). Purpose: inspect a failure step by step.
npx playwright show-trace trace.zip         # open a specific trace file. Purpose: open a trace from a failed run or CI.
```

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

### Notes

| Topic | Note |
|---|---|
| OneDrive | The project sits in a synced folder. On `EBUSY` / `EPERM` errors mid-run, pause OneDrive sync or move the project outside it. |
| Browser cache | Browsers are cached globally in `C:\Users\<you>\AppData\Local\ms-playwright`, not per project. If `npx playwright install` prints nothing, they are already downloaded. |
| chromium is not Chrome | It is the open-source engine behind Chrome and Edge. For real Chrome or Edge, add projects with `channel: 'chrome'` or `channel: 'msedge'` in `playwright.config.ts`. |
