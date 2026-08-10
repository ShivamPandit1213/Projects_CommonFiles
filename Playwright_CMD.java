========================================================
PAIMANA_Playwright_1.1
Playwright + TypeScript end-to-end test suite
5 tests across 3 browsers = 15 total runs
========================================================

PowerShell: Browser Launch(Not headless): (PS C:\WINDOWS\System32>)
cd C:\Users\shiva\OneDrive\JavaSelenium\PAIMANA_Playwright_1.1
$env:HEADED=1
npx playwright test tests/home.spec.ts --project=chromium

Browser Launch(headless):
npx playwright test
npx playwright test --project=chromium
    
MANDATORY FILES
---------------
The project will not run without these three:

    package.json           declares @playwright/test as a dependency
    playwright.config.ts   browsers, testDir, baseURL, reporters, timeouts
    tests/*.spec.ts        the actual tests

Everything else is supporting code, imported by the specs.


PROJECT STRUCTURE
-----------------
    fixtures/               Custom test fixtures (page-object injection)
    pages/                  Page Object Model classes (BasePage, HomePage, LoginPage)
    test-data/              Static test data (users.json)
    tests/                  Spec files - api.spec.ts, example.spec.ts, home.spec.ts
    utils/                  Shared helpers
    playwright.config.ts    Browsers, baseURL, reporters, timeouts
    package.json            Dependencies and npm scripts
    tsconfig.json           TypeScript compiler options


FIRST-TIME SETUP
----------------
    cd C:\Users\shiva\OneDrive\JavaSelenium\PAIMANA_Playwright_1.1
    npm install              install dependencies
    npx playwright install   download browsers

IMPORTANT: Run from the project root, not your home folder.
From C:\Users\shiva you will get this error:

    Error: EPERM: operation not permitted, scandir '...\Temp\WinSAT'

You are in the right place when "dir" shows package.json and playwright.config.ts.


THE COMMAND TO REMEMBER
-----------------------
    npx playwright test


RUN
---
    npx playwright test                                       all tests, all 3 browsers
    npx playwright test --project=chromium                    one browser - fastest
    npx playwright test --project=firefox
    npx playwright test --project=webkit                      Safari's engine
    npx playwright test --project=chromium --project=firefox  two browsers


FILTER
------
    npx playwright test tests/home.spec.ts    one file
    npx playwright test --grep @smoke         by tag - @smoke, @regression, @api
    npx playwright test -g "login"            by test title
    npx playwright test --list                list tests, don't run


DEBUG
-----
    npx playwright test --ui         interactive mode - best for writing tests
    npx playwright test --headed     watch the browser
    npx playwright test --debug      step through with inspector


REPORT
------
    npx playwright show-report       open report from last run


COMBINE
-------
Flags stack, which is where the real speed comes from:

    npx playwright test --project=chromium --grep @smoke --headed


OPTIONAL: SHORTER COMMANDS
--------------------------
Add a scripts section to package.json:

    "scripts": {
      "test": "playwright test",
      "test:ui": "playwright test --ui",
      "test:smoke": "playwright test --grep @smoke"
    }

Then run "npm test" or "npm run test:ui" from the project folder.


NOTES
-----
OneDrive
    This project sits inside a synced folder. If you hit EBUSY or EPERM errors
    mid-run, pause OneDrive sync or move the project outside the synced directory.

Browser cache
    Browsers are cached globally at C:\Users\<you>\AppData\Local\ms-playwright,
    not per project. If "npx playwright install" prints nothing, they are
    already downloaded.

chromium is not Chrome
    It is the open-source engine behind Chrome and Edge. For real Chrome or Edge,
    add projects with channel: 'chrome' or channel: 'msedge' in playwright.config.ts.
=======================================================================
Playwright's standalone apps rather than running tests:

npx playwright open                        open a browser with Playwright's inspector attached
npx playwright open https://example.com    open a specific URL
npx playwright codegen                     record clicks and generate test code
npx playwright codegen https://example.com record against a specific site
npx playwright show-trace                  open the trace viewer (file picker)
npx playwright show-trace trace.zip        open a specific trace file

Other CLI commands worth having:

npx playwright install                     download browsers
npx playwright install chromium            download one browser only
npx playwright install-deps                install OS-level dependencies (Linux/CI)
npx playwright --version                   check installed version
npx playwright --help                      full command list
npx playwright clear-cache                 clear the browser cache
