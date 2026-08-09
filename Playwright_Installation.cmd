========================================================
PLAYWRIGHT - COMPLETE COMMAND GUIDE
========================================================


THE ONE RULE
------------
Every command below must be run from the PROJECT FOLDER, never from
C:\Users\shiva. Check the prompt before you type. It must read:

    C:\Users\shiva\OneDrive\JavaSelenium\PAIMANA_Playwright_1.1>

Shortcut: open the project folder in File Explorer, click the address bar,
type cmd, press Enter. The terminal opens already in that folder.

Do NOT copy the prompt text itself. Type only what comes after the ">".


MANDATORY FILES
---------------
    package.json           declares @playwright/test as a dependency
    playwright.config.ts   browsers, testDir, baseURL, reporters, timeouts
    <testDir>/*.spec.ts    the actual tests

testDir is set in playwright.config.ts and is not always "tests":
    PAIMANA_Playwright_1.1  ->  testDir: './tests'
    playwright-learning     ->  testDir: './playwright'


========================================================
STEP-BY-STEP: FIRST TIME
========================================================

STEP 0 - CHECK PREREQUISITES
    node -v      must be v18 or higher
    npm -v
    npx -v
When: once per machine. If "not recognized", install Node.js LTS from
nodejs.org, then close and reopen CMD.

STEP 1 - GO TO THE PROJECT
    cd C:\Users\shiva\OneDrive\JavaSelenium\PAIMANA_Playwright_1.1
    dir
When: every new terminal session. "dir" must show package.json
and playwright.config.ts.

STEP 2 - INSTALL DEPENDENCIES
    npm install
When: once per project, again after cloning from GitHub or whenever
package.json changes. Creates node_modules/.

STEP 3 - INSTALL BROWSERS
    npx playwright install
When: once per machine, and again if you see "Executable doesn't exist".
Downloads ~500 MB to C:\Users\shiva\AppData\Local\ms-playwright.
Silence means they are already downloaded.

STEP 4 - CHECK VERSION
    npx playwright --version
When: confirming the CLI works, or reporting a bug.

STEP 5 - VERIFY TESTS ARE FOUND
    npx playwright test --list
When: after setup, and after adding new test files. Expect
"Total: 15 tests in 3 files". Zero means testDir is wrong.

STEP 6 - RUN
    npx playwright test

STEP 7 - VIEW RESULTS
    npx playwright show-report
Opens localhost:9323. Ctrl+C then Y to stop the server.


========================================================
RUN
========================================================
    npx playwright test                                       all tests, all 3 browsers
    npx playwright test --project=chromium                    one browser - fastest
    npx playwright test --project=firefox
    npx playwright test --project=webkit                      Safari's engine
    npx playwright test --project=chromium --project=firefox  two browsers

When: --project=chromium for everyday development, it is roughly 3x faster.
Run all three before pushing or releasing.


FILTER
------
    npx playwright test tests/home.spec.ts    one file
    npx playwright test example               partial filename match
    npx playwright test --grep @smoke         by tag - @smoke, @regression, @api
    npx playwright test -g "login"            by test title
    npx playwright test --list                list tests, don't run

When: while fixing one broken test, run only that file. In CI, --grep @smoke
gives a fast gate on every commit and @regression runs nightly.


DEBUG
-----
    npx playwright test --ui         interactive runner - best for writing tests
    npx playwright test --headed     watch the browser
    npx playwright test --debug      step through with inspector

When: --ui is where you should live day to day. --debug when one test fails
and you need to pause on each line. --headed when you just want to see it.


REPORT AND TRACE
----------------
    npx playwright show-report              open report from last run
    npx playwright show-trace               open trace viewer (file picker)
    npx playwright show-trace trace.zip     open a specific trace file

When: show-report after any run. show-trace when a test failed in CI and you
were not watching - the trace has a DOM snapshot timeline, network log, and
console output for every step. Requires trace: 'on-first-retry' in the config.


BROWSER AND RECORDER APPS
-------------------------
    npx playwright open                          browser + inspector, no recording
    npx playwright open https://example.com      open a specific URL
    npx playwright codegen                       record clicks, generate code
    npx playwright codegen https://iigdev.uatnegd.online/home
    npx playwright codegen <url> -o tests/login.spec.ts    write straight to a file
    npx playwright codegen <url> -o codegen-output.ts      safer: scratch file

When: codegen to learn locator syntax or to bootstrap a new test fast. open
to inspect an element's locator without recording.

WARNING: -o OVERWRITES the target file with no confirmation. Record to a
scratch file, then copy the parts you want into your real spec. Add the
scratch file to .gitignore.

Codegen records typing as well as clicking. If your generated code has
.click() but no .fill(), you clicked the field without typing a value.


INSTALL AND MAINTENANCE
-----------------------
    npx playwright install             download all browsers
    npx playwright install chromium    download one browser only
    npx playwright install-deps        OS-level dependencies (Linux / CI)
    npx playwright clear-cache         clear the browser cache
    npx playwright --version           check installed version
    npx playwright --help              full command list


CREATE A NEW PROJECT FROM SCRATCH
---------------------------------
    mkdir playwright-learning && cd playwright-learning
    npm init playwright@latest

Four questions: TypeScript, test folder name, GitHub Actions workflow,
install browsers. Writes package.json, playwright.config.ts, an example
spec, and .github/workflows/playwright.yml.


COMBINE
-------
Flags stack, which is where the real speed comes from:

    npx playwright test --project=chromium --grep @smoke --headed


SHORTER COMMANDS (OPTIONAL)
---------------------------
Add to package.json:

    "scripts": {
      "test": "playwright test",
      "test:ui": "playwright test --ui",
      "test:smoke": "playwright test --grep @smoke"
    }

Then: npm test  /  npm run test:ui


========================================================
COMING FROM SELENIUM
========================================================
    codegen        ~ Selenium IDE (record and generate)
    test --ui      ~ a test runner GUI, no Selenium equivalent
    show-trace     ~ post-mortem debugging, no Selenium equivalent
    pages/         ~ Page Object Model, same concept you already know
    fixtures/      ~ @BeforeMethod setup, but injected not inherited
    playwright.config.ts  ~ testng.xml plus the config half of pom.xml

Playwright prefers getByRole() over XPath. Role-based locators survive
style and layout changes far better.


========================================================
COMMANDS THAT BLOCK THE TERMINAL
========================================================
These four keep running until you close them. Press Ctrl+C (then Y) to
get your prompt back:

    npx playwright open
    npx playwright codegen
    npx playwright show-report
    npx playwright test --ui


========================================================
ERRORS AND FIXES
========================================================

EPERM: operation not permitted, scandir '...\Temp\WinSAT'
    You are in C:\Users\shiva. cd into the project folder.

ENOENT: no such file or directory, open 'C:\Users\shiva\tests\login.spec.ts'
    Same cause - wrong folder, so the tests/ directory does not exist.
    cd into the project first.

'C:\Users\shiva' is not recognized as an internal or external command
    You copied the prompt text along with the command.
    Type only what comes after the ">".

Cannot find module '@playwright/test'
    Run: npm install

Executable doesn't exist at ...ms-playwright\chromium-XXXX
    Run: npx playwright install
    Happens when another project downloaded newer browser builds.

Error: No tests found  /  Total: 0 tests in 0 files
    Wrong folder, or testDir in playwright.config.ts does not match
    the real folder, or files are not named *.spec.ts

'npx' is not recognized
    Node.js not installed, or CMD not reopened after installing.

Timeout 30000ms exceeded
    Site slow or locator wrong. Diagnose with: npx playwright test --debug

EBUSY / EPERM mid-run
    OneDrive syncing during the run. Pause sync or move the project
    outside the synced folder.

Port 9323 already in use
    An old report server is still running. Close it, or:
    npx playwright show-report --port=9324

--config path doubled up
    Do not pass --config when you are already inside the project folder.
    Plain "npx playwright test" is all you need.


========================================================
NOTES
========================================================

OneDrive
    PAIMANA sits inside a synced folder. Sync can lock files mid-run.
    Pause sync or move the project outside OneDrive if errors persist.

Browser cache is global, not per project
    Browsers live in C:\Users\shiva\AppData\Local\ms-playwright.
    If a newer project downloads newer builds, older projects may print
    "Removing unused browser" and need npx playwright install run again.

chromium is not Chrome
    It is the open-source engine behind Chrome and Edge. For real Chrome
    or Edge, add projects with channel: 'chrome' or channel: 'msedge'
    in playwright.config.ts.

Captcha cannot be automated
    The PAIMANA login has a 6-character verification code. It exists to
    block automation. Ask the dev team to disable it on UAT or provide a
    bypass value for test runs.

.first() and .nth(1) in generated code
    These mean more than one element matched. They are fragile - replace
    with a scoped or exact locator by hand after recording.

.gitignore
    node_modules/
    test-results/
    playwright-report/
    blob-report/
    playwright/.cache/
    codegen-output.ts
