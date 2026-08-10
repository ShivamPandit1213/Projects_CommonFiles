========================================================
PLAYWRIGHT - COMPLETE COMMAND REFERENCE
Every command, what it does, and when to use it
========================================================

========================================================
PART 1 - QUICK LOOKUP BY INTENT
========================================================

    I want to run my tests
        npx playwright test

    I want to run tests fast while developing
        npx playwright test --project=chromium

    I want a GUI to run, watch and debug tests
        npx playwright test --ui

    I want to write a NEW test but don't know the locators
        npx playwright codegen <url>

    I want to explore a page and find locators, no recording
        npx playwright open <url>

    A test failed and I need to know why
        npx playwright show-trace

    I want to see results of the last run
        npx playwright show-report

    I want to check what tests exist without running them
        npx playwright test --list


COMING FROM SELENIUM
--------------------
    codegen         = Selenium IDE (record and generate code)
    test --ui       = test runner GUI, no Selenium equivalent
    show-trace      = post-mortem debugging, no Selenium equivalent
    open            = element inspector, similar to browser devtools
    pages/          = Page Object Model, same concept you already know


========================================================
PART 2 - SYSTEM CHECK COMMANDS
========================================================

node -v
    Prints the installed Node.js version. Playwright needs v18 or higher.
    If this fails, Node is not installed or CMD was not reopened after
    installing it. Everything else depends on this working.

npm -v
    Prints the npm version. npm ships with Node, so if node -v worked
    this will too. Used to install project dependencies.

npx -v
    Prints the npx version. npx runs the Playwright CLI out of node_modules
    without installing anything globally. Every playwright command uses it.

npx playwright --version
    Prints the Playwright version in THIS project, e.g. 1.59.1.
    Run it when behaviour looks wrong, or to confirm the CLI is reachable
    at all. Different folders can report different versions.

npx playwright --help
    Lists every available playwright command and flag.
    Use it when you cannot remember a flag name.

dir
    Lists files in the current folder. Use it right after cd to confirm you
    can see package.json and playwright.config.ts. If you cannot, you are in
    the wrong folder and nothing else will work.

cd <path>
    Changes the current folder. THE most important command here - Playwright
    scans whatever folder you are standing in, so being in the wrong place
    causes most errors.


========================================================
PART 3 - SETUP AND INSTALL COMMANDS
========================================================

npm install
    Reads package.json and downloads every dependency into node_modules.
    Run once per project, and again after cloning from GitHub or whenever
    package.json changes. Without it you get "Cannot find module
    '@playwright/test'".

npx playwright install
    Downloads the actual browser binaries - Chromium, Firefox, WebKit,
    about 500 MB - into C:\Users\<you>\AppData\Local\ms-playwright.
    Separate from npm install because browsers are not npm packages.
    Silence means they are already downloaded.

npx playwright install chromium
    Downloads one browser only. Faster and much smaller when you only ever
    test in Chromium, or on a CI machine with limited disk.

npx playwright install-deps
    Installs operating-system level libraries the browsers need.
    Only relevant on Linux and CI runners. Not needed on Windows.

npx playwright clear-cache
    Deletes Playwright's cached data. Use it when browser downloads are
    corrupted or a version mismatch will not resolve any other way.

npm init playwright@latest
    Creates a brand new Playwright project from scratch in the current
    folder. Asks four questions (TypeScript or JavaScript, test folder name,
    GitHub Actions workflow, install browsers) then writes package.json,
    playwright.config.ts, an example spec and the CI workflow.
    Use for a NEW project only - never inside an existing one.


========================================================
PART 4 - MANDATORY FILES
========================================================

A Playwright project will not run without these three:

package.json
    Declares @playwright/test as a dependency and holds npm scripts.
    npm install reads this file.

playwright.config.ts
    Defines which browsers to run (projects), where the tests live
    (testDir), the baseURL, reporters, timeouts and trace settings.
    The runner reads this FIRST. Its location defines the project root.

<testDir>/*.spec.ts
    The actual tests. Without them the runner reports 0 tests.

NOTE: testDir is NOT always "tests".
    PAIMANA_Playwright_1.1  ->  testDir: './tests'
    playwright-learning     ->  testDir: './playwright'


SUPPORTING FILES (PAIMANA_Playwright_1.1)
-----------------------------------------
    fixtures/               Custom test fixtures - inject page objects into tests
    pages/                  Page Object Model classes (BasePage, HomePage, LoginPage)
    test-data/              Static test data (users.json)
    utils/                  Shared helper functions
    tsconfig.json           TypeScript compiler options
    node_modules/           Installed dependencies - never commit this
    test-results/           Traces and screenshots from failed runs
    playwright-report/      The HTML report from the last run


========================================================
PART 5 - SETUP, STEP BY STEP
========================================================

STEP 0 - CHECK PREREQUISITES
    node -v
    npm -v
    npx -v

STEP 1 - GO TO THE PROJECT FOLDER   (MANDATORY)
    cd C:\Users\shiva\OneDrive\JavaSelenium\PAIMANA_Playwright_1.1
    dir

    SHORTCUT: open the folder in File Explorer, click the address bar,
    type cmd, press Enter. The terminal opens already in that folder.

STEP 2 - INSTALL DEPENDENCIES
    npm install

STEP 3 - INSTALL BROWSERS
    npx playwright install

STEP 4 - CHECK VERSION
    npx playwright --version

STEP 5 - VERIFY TESTS ARE FOUND
    npx playwright test --list
    Expect: Total: 15 tests in 3 files

STEP 6 - RUN
    npx playwright test

STEP 7 - VIEW RESULTS
    npx playwright show-report


========================================================
PART 6 - RUNNING TESTS
========================================================

npx playwright test
    Runs every test in every configured browser. Your default command.
    Headless by default, parallel across multiple workers.

npx playwright test --project=chromium
    Runs one browser only. Roughly 3x faster. Use this constantly while
    developing; save the full 3-browser run for before you commit.

npx playwright test --project=firefox
    Firefox only. Use when chasing a Firefox-specific failure.

npx playwright test --project=webkit
    WebKit only - Safari's engine. This is how you test Safari behaviour
    from Windows, where Safari itself does not exist.

npx playwright test --project=chromium --project=firefox
    Runs a subset of browsers. The flag can be repeated.

npx playwright test --list
    Lists every test the runner can find, WITHOUT running any of them.
    Use it to confirm new tests are being picked up, to see exact project
    and test names, or to verify you are in the right folder.

npx playwright test --workers=1
    Runs tests one at a time instead of in parallel. Use when tests
    interfere with each other, or to get cleaner output while debugging.

npx playwright test --retries=2
    Retries a failed test up to 2 times. Useful for isolating flaky tests -
    a test that passes on retry is flaky, not broken.

npx playwright test --reporter=list
    Changes the output format. Options include list, line, dot, html, json.
    Use "list" for readable per-test output in the terminal.


FILTERING WHICH TESTS RUN
-------------------------
npx playwright test tests/home.spec.ts
    Runs a single file. Fastest way to iterate on one area.

npx playwright test example
    Runs any file whose path contains "example". Partial match, no path needed.

npx playwright test --grep @smoke
    Runs only tests tagged @smoke. Your project has @smoke, @regression, @api.
    This is how CI pipelines split a 2-minute smoke run from a full regression.

npx playwright test --grep-invert @slow
    Runs everything EXCEPT the matching tag. Use to skip known-slow tests.

npx playwright test -g "login"
    Runs tests whose TITLE contains "login". Matches the test name, not the
    filename.

npx playwright test tests/home.spec.ts:13
    Runs the single test at line 13 of that file. The most precise filter -
    line numbers come from the --list output.


COMBINING FLAGS
---------------
Flags stack. This is where the real speed comes from:

    npx playwright test --project=chromium --grep @smoke --headed
    -> smoke tests only, one browser, visible - a few seconds instead of 20


========================================================
PART 7 - CODEGEN (RECORD TESTS)
========================================================

npx playwright codegen
    Opens a blank browser plus the Playwright Inspector. Every click and
    keystroke is converted to TypeScript in the Inspector panel.
    The equivalent of Selenium IDE, but the generated locators are better.

npx playwright codegen https://playwright.dev
    Starts recording on a specific URL. Best way to learn Playwright's
    locator syntax - click around and read what it writes.

npx playwright codegen https://iigdev.uatnegd.online/home
    Records against PAIMANA.

npx playwright codegen <url> -o tests/login.spec.ts
    Writes the generated code straight to a file as you record.
    WARNING: -o OVERWRITES the file without asking. Must be run from the
    project folder or the path will not exist.

npx playwright codegen <url> -o codegen-output.ts
    SAFER pattern - record to a scratch file, then copy the useful parts into
    a real spec by hand. Add codegen-output.ts to .gitignore.

npx playwright codegen <url> --target=javascript
    Generates JavaScript instead of TypeScript.

npx playwright codegen <url> --browser=firefox
    Records in Firefox instead of Chromium.

npx playwright codegen <url> --device="iPhone 13"
    Records in a mobile viewport with the right user agent. Use for building
    responsive or mobile-specific tests.

npx playwright codegen <url> --save-storage=auth.json
    Saves cookies and localStorage when you close the browser - i.e. saves
    your logged-in session to a file.

npx playwright codegen <url> --load-storage=auth.json
    Starts the browser already logged in, using a previously saved session.
    This is the practical answer to captchas: log in manually ONCE with
    --save-storage, then every later recording and test run reuses it.

CODEGEN TIPS
    - Click AND type. Clicking alone records .click() with no .fill().
    - .first() and .nth(1) in generated code mean several elements matched
      the same name. Replace them with scoped locators - index positions
      break as soon as the page changes.
    - Captchas cannot be automated. Either ask the dev team to disable
      captcha on UAT, or use the --save-storage / --load-storage pattern.
    - Playwright prefers getByRole() over XPath. Role locators survive
      layout and styling changes far better than XPath.


========================================================
PART 8 - UI MODE AND DEBUGGING
========================================================

npx playwright test --ui
    Opens the full graphical test runner: test list, run buttons, step
    timeline, DOM snapshots, network and console tabs, watch mode.
    The best place to WRITE and DEBUG tests. Most people live here.

npx playwright test --headed
    Runs tests in a visible browser instead of headless, so you can watch
    what happens. Slower, but sometimes the fastest way to spot the problem.

npx playwright test --debug
    Opens the Playwright Inspector and pauses before each step, so you can
    step through the test line by line. Use when a test fails and you cannot
    tell which action broke it.

npx playwright test --timeout=60000
    Overrides the per-test timeout in milliseconds. Use when a slow
    environment causes false failures.

npx playwright open
    Opens a browser with the Inspector attached but records nothing.
    Use it purely to explore a page and read off locators.

npx playwright open https://playwright.dev
    Same, starting at a URL.


INSIDE UI MODE
    Play triangle at top of TESTS panel     run everything
    Play button on a test row               run just that test
    Eye icon                                watch mode - re-runs on file save
    Filter box                              type @smoke to filter by tag
    Actions panel                           every step; click one to time-travel
    Locator tab + target icon               hover elements to generate locators
    Projects: chromium                      defaults to one browser; expand to add more


========================================================
PART 9 - REPORTS AND TRACES
========================================================

npx playwright show-report
    Opens the HTML report from the last run at http://localhost:9323.
    Shows pass/fail per test per browser, durations, error messages,
    screenshots and attached traces.

npx playwright show-report --port=9324
    Same, on a different port. Use when 9323 is already occupied by an
    old server you forgot to close.

npx playwright show-trace
    Opens the trace viewer with a file picker.

npx playwright show-trace test-results/<folder>/trace.zip
    Opens a specific trace. A trace contains a full DOM snapshot timeline,
    every network request, console output and a screenshot at every step of
    a failed test. This is the single most powerful debugging tool
    Playwright has - it lets you inspect a failure after the fact, without
    reproducing it.

    Traces are only produced when playwright.config.ts sets something like
    trace: 'on-first-retry'. They appear in test-results/.


========================================================
PART 10 - SHORTER COMMANDS (OPTIONAL)
========================================================

Add a scripts section to package.json:

    "scripts": {
      "test": "playwright test",
      "test:ui": "playwright test --ui",
      "test:smoke": "playwright test --grep @smoke",
      "test:chromium": "playwright test --project=chromium",
      "report": "playwright show-report"
    }

Then:

npm test
    Runs the "test" script. Note: npm test works without "run".

npm run test:ui
    Runs any other script by name. The "run" word is required for these.

Purpose: shorter to type, and it documents for your team which commands
matter on this project.


========================================================
PART 11 - ERRORS AND FIXES
========================================================

EPERM ... scandir '...\Temp\WinSAT'
    You are in C:\Users\shiva. Playwright is scanning Windows system folders
    it cannot read. Fix: cd into the project folder.

ENOENT: no such file or directory, open 'C:\Users\shiva\tests\login.spec.ts'
    Same cause - wrong folder, so tests/ does not exist there.
    Fix: cd into the project first.

'C:\Users\shiva' is not recognized as an internal or external command
    You copied the prompt text along with the command.
    Fix: type only what comes AFTER the > symbol.

Cannot find module '@playwright/test'
    Dependencies not installed. Fix: npm install

Executable doesn't exist at ...ms-playwright\chromium-XXXX
    The browser binary for this Playwright version is missing, usually
    because another project downloaded newer builds.
    Fix: npx playwright install

Error: No tests found  /  Total: 0 tests in 0 files
    Wrong folder, or testDir in playwright.config.ts does not match the real
    folder, or the files are not named *.spec.ts.

'npx' is not recognized
    Node.js not installed, or CMD not reopened after installing it.

Timeout 30000ms exceeded
    The site was slow, or the locator never matched anything.
    Fix: diagnose with npx playwright test --debug

EBUSY / EPERM mid-run
    OneDrive is syncing files while tests write to them.
    Fix: pause OneDrive sync, or move the project outside the synced folder.

Port 9323 already in use
    An old report server is still running.
    Fix: close it, or npx playwright show-report --port=9324

Terminal appears frozen, no prompt returns
    Normal. These commands block until their window is closed:
    open, codegen, show-report, show-trace, test --ui
    Fix: press Ctrl+C, then Y.


========================================================
PART 12 - NOTES
========================================================

OneDrive
    PAIMANA sits inside a synced folder. If you hit EBUSY or EPERM errors
    mid-run, pause OneDrive sync or move the project outside it.

Browser cache is global, not per project
    Browsers live in C:\Users\<you>\AppData\Local\ms-playwright.
    Silence from npx playwright install means they are already there.
    If a newer project downloads newer builds, older projects may print
    "Removing unused browser" and need npx playwright install run again.

chromium is not Chrome
    It is the open-source engine behind Chrome and Edge. For real Chrome or
    Edge, add projects with channel: 'chrome' or channel: 'msedge' in
    playwright.config.ts.

Node version
    Node 25 is current, not LTS. Playwright officially supports 18/20/22/24.
    If you hit an unexplained internal error, try Node 22 LTS.

GitHub Actions
    If .github/workflows/playwright.yml exists, tests run automatically on
    every push once the repo is on GitHub.

.gitignore
    Never commit these:
        node_modules/
        test-results/
        playwright-report/
        blob-report/
        playwright/.cache/


========================================================
THE MINIMUM YOU EVER NEED AGAIN
========================================================

    cd C:\Users\shiva\OneDrive\JavaSelenium\PAIMANA_Playwright_1.1
    npx playwright test
