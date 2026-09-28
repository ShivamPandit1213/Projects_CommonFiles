/*
========================================================================
  PlaywrightBasic1  -  Playwright command reference
========================================================================
  Stack     Node 20+  |  @playwright/test 1.63  |  JavaScript (ES modules)
  Tests     3 tests x 3 browsers (chromium, firefox, webkit) = 9 runs
  Folder    C:\Users\shiva\OneDrive\JavaSelenium\PlaywrightBasic1
  Repo      https://github.com/ShivamPandit1213/PlaywrightBasic1
  Portal    https://iigdev.uatnegd.online   (baseURL; override with BASE_URL)
  Updated   2026-09-28

  This file is one Java block comment. javac accepts it and produces
  nothing, Playwright ignores it (it is not a *.spec.js file) and VS Code's
  Java support marks it "non-project file" - that warning is expected.
  Keep it in step with package.json scripts and ProjectInfo.html.

  How to read it:   command
                        what it does and WHEN to reach for it

  Everything runs in PowerShell from the project folder unless stated.
========================================================================


0. THE FIRST CHECK - ARE YOU IN THE RIGHT FOLDER?
------------------------------------------------
    cd C:\Users\shiva\OneDrive\JavaSelenium\PlaywrightBasic1
        Every command below assumes this folder. From C:\Users\shiva you get
        "EPERM: operation not permitted, scandir ...\Temp\WinSAT", because
        Playwright starts scanning your whole profile for tests.

    dir
        You are in the right place when it lists package.json and
        playwright.config.js.

    npx playwright --version
        Prints "Version 1.63.0". If it errors, dependencies are missing
        (section 1).


1. FIRST-TIME SETUP  /  AFTER A FRESH CLONE  /  AFTER AN UPGRADE
----------------------------------------------------------------
    npm ci
        Installs exactly what package-lock.json says. Use after cloning,
        after switching branches, or whenever node_modules looks broken.
        Fails if package.json and the lock file disagree - then run
        "npm install" once and commit the updated package-lock.json.

    npm install
        Resolves versions afresh and rewrites package-lock.json. Use only
        when you change dependencies on purpose (npm install -D <package>).

    npx playwright install
        Downloads the browser builds that match the installed
        @playwright/test version into C:\Users\<you>\AppData\Local\ms-playwright.
        Once per machine, and again after EVERY Playwright upgrade.
        Prints nothing when the browsers are already there.

    npx playwright install chromium
        Chromium only - quicker when firefox/webkit are never run locally.

    npx playwright install --with-deps
        Linux / CI only: also installs OS libraries. Not needed on Windows.

    npx playwright install --list
        Shows which browser builds are present on this machine.


2. RUN
------
    npm test
        = npx playwright test. The everyday run: 3 tests x 3 browsers = 9.
        Window or no window follows DEFAULT_HEADLESS in playwright.config.js.

    npm run test:headed
        Same run, browser windows always visible (Chromium maximized).
        Use it to watch what a test actually does.

    npm run test:headless
        Same run, no windows. Fastest; use before a commit.

    npm run test:chromium
        One browser only. The quickest feedback while writing a test.

    npm run report
        Opens playwright-report/index.html from the last run, with the
        attached full-page screenshot and any traces.

    npx playwright test --project=firefox
    npx playwright test --project=webkit
    npx playwright test --project=chromium --project=firefox
        Pick browsers by hand. webkit is Safari's engine.

    npx playwright test --workers=1
        One test at a time. Use when parallel runs interfere with each
        other or the laptop struggles with several browsers at once.

    npx playwright test --retries=2
        Retry failures. Separates flaky from broken: a test that passes on
        retry is reported as "flaky", not "failed".

    npx playwright test --repeat-each=5
        The opposite check: run every test 5 times to prove a green test
        is stable before trusting it.

    npx playwright test --last-failed
        Re-run only the tests that failed in the previous run.

    Flags stack, e.g.:
    npx playwright test tests/home.spec.js --project=chromium --headed


3. HEADLESS / HEADED - HOW THE DECISION IS MADE
-----------------------------------------------
    Priority, highest first (resolveHeadless() in playwright.config.js):
      1. CI is set (GitHub Actions)  -> always headless, there is no screen
      2. HEADLESS env var            -> 'true' / 'false', any case; anything
                                        else stops the run with
                                        "HEADLESS must be 'true' or 'false'"
      3. DEFAULT_HEADLESS            -> constant at the top of the config
                                        (currently false = windows visible)

    $env:HEADLESS='true'
    $env:HEADLESS='false'
        Applies to this PowerShell window until it is closed or removed.
        The npm scripts test:headed / test:headless set it for one run only.

    $env:HEADLESS
        Print the current value. Blank = not set.

    Remove-Item Env:HEADLESS
        Back to DEFAULT_HEADLESS. Run this when tests unexpectedly show or
        hide windows - a value left over from earlier is the usual reason.

    npx playwright test --headed
        Playwright's own flag: forces windows for one run regardless of the
        config. Fine for a quick look; prefer npm run test:headed, which
        also gives Chromium a maximized window.


4. ANOTHER ENVIRONMENT
----------------------
    $env:BASE_URL='https://other-env.example'; npm test
        Point the same tests at another portal.

    $env:BASE_URL
    Remove-Item Env:BASE_URL
        Check it / go back to the dev portal.


5. FILTER - RUN LESS
--------------------
    npx playwright test tests/home.spec.js
        One file.

    npx playwright test -g "home page loads"
        By test title (substring, case-insensitive).

    npx playwright test --grep @smoke
        By tag. A tag exists only if a test declares it:
        test('home page loads', { tag: '@smoke' }, async ({ page }) => ...)

    npx playwright test --grep-invert @slow
        Everything except a tag.

    npx playwright test --list
        Show which tests WOULD run, without running them. First thing to
        try when a filter matches nothing or you see "No tests found".


6. DEBUG - A TEST FAILS OR MISBEHAVES
-------------------------------------
    npm run test:chromium -- tests/home.spec.js --headed
        Narrow it down: one browser, one file, visible. The "--" passes the
        rest of the line through npm to Playwright.

    npx playwright test --debug
        Playwright Inspector: pause before every action, step through, see
        the locator being used. Best for "why does it not click / find it".

    npx playwright test --ui
        UI mode: pick tests, watch them, time-travel through each step with
        the DOM and network. Best while writing a new test.

    npx playwright test --trace on
        Record a trace for every test (the config records one only on the
        first retry). Then open it:
    npx playwright show-trace test-results/<test-folder>/trace.zip
    npx playwright show-trace
        Trace viewer (with a file picker when no path is given). A trace has
        every action, network call, console line and a screenshot per step:
        the most complete record of a failure, and shareable as one file.

    npx playwright test --timeout=120000
        Give slow pages more time (milliseconds) for one run instead of
        editing the config.

    npx playwright test --reporter=line
        Compact one-line output. Useful in a narrow terminal or when
        pasting a log somewhere.

    $env:DEBUG='pw:api'; npx playwright test --project=chromium
        Verbose log of every Playwright call. Use when a run hangs and you
        need to see the last thing it did. Remove-Item Env:DEBUG afterwards.


7. WHERE A FAILED RUN LEAVES EVIDENCE
-------------------------------------
    test-results/<test-name>/
        test-failed-1.png    screenshot at the moment of failure
        error-context.md     the page's accessibility tree at that moment
                             (when present) - read it to find the right locator
        trace.zip            on a retry, or always with --trace on

    playwright-report/index.html
        The HTML report (npm run report). Attachments such as the full-page
        screenshot from home.spec.js sit under each test.

    Both folders are rewritten on every run and are git-ignored. Copy a
    file out before re-running if you want to keep it.


8. RECORD AND EXPLORE
---------------------
    npx playwright codegen https://iigdev.uatnegd.online/home
        Opens a browser and writes code for every click and fill. Use it
        to get locators for a new page object (e.g. the login form), then
        move them into pages/ rather than keeping the generated test as-is.

    npx playwright open https://iigdev.uatnegd.online/home
        A browser with the Playwright inspector attached and no code
        generation - for exploring a page and trying locators.


9. UPGRADE AND CLEAN UP
-----------------------
    npm outdated
        Which packages have newer versions.

    npm install -D @playwright/test@latest
    npx playwright install
        Upgrade Playwright. ALWAYS re-run install afterwards: the browser
        builds are tied to the package version.

    npx playwright clear-cache
        Clears Playwright's compile/test cache (NOT the browsers). Try it
        when an edit to a test or the config seems to be ignored.

    npx playwright uninstall
        Removes the downloaded browsers (about 1 GB). Not the package.

    Remove-Item -Recurse -Force node_modules; npm ci
        The reset for a broken node_modules.


10. GIT AND CI
--------------
    git status                        what changed, what is untracked
    git add -A                        stage everything
    git commit -m "message"           record it
    git push                          upload (first time: git push -u origin main,
                                      which links local main to GitHub)
    git pull                          bring down changes from GitHub
    git log --oneline -5              last five commits
    git diff                          unstaged changes, line by line
    git restore <file>                throw away local changes to one file

    Remote (the GitHub address this folder pushes to):
    git remote -v                     show it
    git remote add origin <url>       connect, when there is no remote yet
    git remote set-url origin <url>   change it, when one already exists
    git remote remove origin          disconnect
        The repository is https://github.com/ShivamPandit1213/PlaywrightBasic1

    CI  (.github/workflows/playwright.yml)
        Runs on every push and pull request to main: npm ci, install
        browsers, npx playwright test, upload playwright-report/.
        CI=true there, so it is always headless, 1 worker, 2 retries, and a
        committed test.only fails the build (forbidOnly).
        Results: repository -> Actions tab -> the run -> "playwright-report"
        artifact (download, unzip, open index.html).


11. TROUBLESHOOTING  -  symptom, cause, fix
-------------------------------------------
    Diagnosis order when anything fails:
      1. dir                            right folder? (package.json listed)
      2. npx playwright --version       package installed?
      3. npx playwright install         browsers present for this version?
      4. npx playwright test --list     does the path / filter match?
      5. npm run test:chromium -- tests/home.spec.js --headed
                                        watch one test, then read test-results/

    "EPERM: operation not permitted, scandir ...\Temp\WinSAT"
        Ran from the home folder.  cd to the project folder.

    "npm.ps1 cannot be loaded because running scripts is disabled"
        PowerShell execution policy blocks npm. Once, as your user:
        Set-ExecutionPolicy -Scope CurrentUser RemoteSigned

    "'playwright' is not recognized"  /  "Cannot find module '@playwright/test'"
        node_modules missing or incomplete.  npm ci

    "Executable doesn't exist at ...\ms-playwright\chromium-xxxx\..."
    "Looks like Playwright Test or Playwright was just installed or updated"
        Browsers not downloaded for this package version (fresh machine or
        just upgraded).  npx playwright install

    "HEADLESS must be 'true' or 'false', got 'yes'"
        Typo in the variable.  $env:HEADLESS='true'  or  Remove-Item Env:HEADLESS

    "Error: No tests found"
        Wrong path, wrong filter, or the file is not named *.spec.js.
        npx playwright test --list shows what Playwright can see.

    "Cannot use import statement outside a module"
        package.json lost "type": "module". Put it back.

    "Test timeout of 60000ms exceeded"  /  "page.goto: Timeout 30000ms exceeded"
        Portal slow or down, VPN off, or a locator that never appears.
        Open the URL in a normal browser first. Then --headed or --debug,
        and read test-results/<test>/error-context.md for the real page state.

    "net::ERR_NAME_NOT_RESOLVED"  /  "net::ERR_CONNECTION_REFUSED"
        Wrong BASE_URL, or no network / VPN.  $env:BASE_URL shows the value.

    "toHaveURL ... Received: https://.../login"  (or another unexpected page)
        The portal redirected: session, maintenance page, changed route.
        Look at the screenshot before touching the test.

    "EBUSY: resource busy or locked"
        OneDrive is syncing test-results/ while Playwright writes to it.
        Pause OneDrive sync and re-run, or move the project out of OneDrive.

    "deviceScaleFactor option is not supported with null viewport"
        Chromium project config: with viewport: null, deviceScaleFactor
        must be left undefined. Restore  deviceScaleFactor: undefined.

    CI fails on "test.only" / forbidOnly but everything passes locally
        A test.only was committed. Remove .only, commit, push again.

    "Playwright Test did not expect test() to be called here"
        Two copies of @playwright/test, or a spec importing test from both
        '@playwright/test' and fixtures/test.js. Import from one place;
        npm ls @playwright/test must show exactly one version.

    "Target page, context or browser has been closed"
        The browser crashed, usually out of memory with many in parallel.
        npx playwright test --workers=1

    npm ci: "package.json and package-lock.json are not in sync"
        The lock file drifted (package.json edited by hand?).
        npm install once, then commit package-lock.json.


OPTIONAL SHORTCUTS  (add to "scripts" in package.json if not there yet)
-----------------------------------------------------------------------
      "test:smoke":  "playwright test --grep @smoke",
      "test:ui":     "playwright test --ui",
      "test:debug":  "playwright test --debug --project=chromium",
      "codegen":     "playwright codegen https://iigdev.uatnegd.online/home"

    Then: npm run test:smoke, npm run test:ui, npm run test:debug, npm run codegen.
========================================================================
*/
