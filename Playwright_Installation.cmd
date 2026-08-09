Microsoft Windows [Version 10.0.26200.8973]
(c) Microsoft Corporation. All rights reserved.

C:\Users\shiva>mkdir playwright-learning && cd playwright-learning

C:\Users\shiva\playwright-learning>npm init playwright@latest
Need to install the following packages:
create-playwright@1.17.139
Ok to proceed? (y) y

> npx
> create-playwright

Getting started with writing end-to-end tests with Playwright:
Initializing project in '.'
√ Do you want to use TypeScript or JavaScript? · TypeScript
√ Where to put your end-to-end tests? · playwright
√ Add a GitHub Actions workflow? (Y/n) · true
√ Install Playwright browsers (can be done manually via 'npx playwright install')? (Y/n) · true
Initializing NPM project (npm init -y)…
Wrote to C:\Users\shiva\playwright-learning\package.json:

{
  "name": "playwright-learning",
  "version": "1.0.0",
  "description": "",
  "main": "index.js",
  "scripts": {
    "test": "echo \"Error: no test specified\" && exit 1"
  },
  "keywords": [],
  "author": "",
  "license": "ISC",
  "type": "commonjs"
}


Installing Playwright Test (npm install --save-dev @playwright/test)…

added 3 packages, and audited 4 packages in 3s

found 0 vulnerabilities
Installing Types (npm install --save-dev @types/node)…

added 2 packages, and audited 6 packages in 1s

found 0 vulnerabilities
Writing playwright.config.ts.
Writing .github\workflows\playwright.yml.
Writing playwright\example.spec.ts.
Writing package.json.
Downloading browsers (npx playwright install)…
Removing unused browser at C:\Users\shiva\AppData\Local\ms-playwright\chromium-1223
Removing unused browser at C:\Users\shiva\AppData\Local\ms-playwright\chromium_headless_shell-1223
Removing unused browser at C:\Users\shiva\AppData\Local\ms-playwright\firefox-1522
Removing unused browser at C:\Users\shiva\AppData\Local\ms-playwright\webkit-2287
Downloading Chrome for Testing 151.0.7922.34 (playwright chromium v1234) from https://cdn.playwright.dev/builds/cft/151.0.7922.34/win64/chrome-win64.zip
191.8 MiB [====================] 100% 0.0s
Chrome for Testing 151.0.7922.34 (playwright chromium v1234) downloaded to C:\Users\shiva\AppData\Local\ms-playwright\chromium-1234
Downloading Chrome Headless Shell 151.0.7922.34 (playwright chromium-headless-shell v1234) from https://cdn.playwright.dev/builds/cft/151.0.7922.34/win64/chrome-headless-shell-win64.zip
114.5 MiB [====================] 100% 0.0s
Chrome Headless Shell 151.0.7922.34 (playwright chromium-headless-shell v1234) downloaded to C:\Users\shiva\AppData\Local\ms-playwright\chromium_headless_shell-1234
Downloading Firefox 153.0 (playwright firefox v1538) from https://cdn.playwright.dev/dbazure/download/playwright/builds/firefox/1538/firefox-win64.zip
119.9 MiB [====================] 100% 0.0s
Firefox 153.0 (playwright firefox v1538) downloaded to C:\Users\shiva\AppData\Local\ms-playwright\firefox-1538
Downloading WebKit 26.5 (playwright webkit v2336) from https://cdn.playwright.dev/dbazure/download/playwright/builds/webkit/2336/webkit-win64.zip
59.6 MiB [====================] 100% 0.0s
WebKit 26.5 (playwright webkit v2336) downloaded to C:\Users\shiva\AppData\Local\ms-playwright\webkit-2336
✔ Success! Created a Playwright Test project at C:\Users\shiva\playwright-learning

Inside that directory, you can run several commands:

  npx playwright test
    Runs the end-to-end tests.

  npx playwright test --ui
    Starts the interactive UI mode.

  npx playwright test --project=chromium
    Runs the tests only on Desktop Chrome.

  npx playwright test example
    Runs the tests in a specific file.

  npx playwright test --debug
    Runs the tests in debug mode.

  npx playwright codegen
    Auto generate tests with Codegen.

We suggest that you begin by typing:

    npx playwright test

And check out the following files:
  - .\playwright\example.spec.ts - Example end-to-end test
  - .\playwright.config.ts - Playwright Test configuration

Visit https://playwright.dev/docs/intro for more information. ✨

Happy hacking! 🎭

C:\Users\shiva\playwright-learning>
=====================================================================================

STEP 0 — CHECK PREREQUISITES

node -v      must be v18 or higher
npm -v       comes with Node
npx -v

If node is not recognized, Node.js isn't installed. Download the LTS build from nodejs.org, install it, then close and reopen CMD — a new terminal is required to pick up the PATH change.

STEP 1 — GO TO THE PROJECT FOLDER

cd C:\Users\shiva\OneDrive\JavaSelenium\PAIMANA_Playwright_1.1
dir

You must see package.json and playwright.config.ts. This step is mandatory — nearly every error comes from skipping it.

STEP 2 — INSTALL DEPENDENCIES

npm install

Creates node_modules/. Run once per project, and again whenever package.json changes or after cloning from GitHub.

STEP 3 — INSTALL BROWSERS

npx playwright install

Downloads Chromium, Firefox, WebKit (~500 MB) into C:\Users\shiva\AppData\Local\ms-playwright. Silence means they're already there.

STEP 4 — CHECK VERSION

npx playwright --version

Confirms the CLI is working. Compare against the version in package.json if something behaves unexpectedly.

STEP 5 — VERIFY TESTS ARE FOUND

npx playwright test --list

Should print your 15 tests. If it says zero, testDir in the config doesn't match your actual folder.

STEP 6 — RUN

npx playwright test

STEP 7 — VIEW RESULTS

npx playwright show-report

Opens at localhost:9323. Ctrl+C then Y to stop the server.

OPTIONAL — EXPLORE

npx playwright test --ui                     interactive runner
npx playwright open https://playwright.dev   browser + inspector
npx playwright codegen https://playwright.dev  record clicks into code

COMMON ERRORS AND FIXES

EPERM ... scandir '...\Temp\WinSAT'
  You're in C:\Users\shiva. cd into the project folder.

Cannot find module '@playwright/test'
  Run: npm install

Executable doesn't exist at ...ms-playwright\chromium-XXXX
  Run: npx playwright install
  Happens when another project downloaded newer browser builds.

Error: No tests found
  testDir in playwright.config.ts doesn't match the real folder,
  or your files aren't named *.spec.ts

'npx' is not recognized
  Node.js not installed, or CMD not reopened after installing.

Timeout 30000ms exceeded
  Site slow or locator wrong. Diagnose with: npx playwright test --debug

EBUSY / EPERM mid-run
  OneDrive syncing during the run. Pause sync or move the project out.

Port 9323 already in use
  An old report server is running. Close it, or:
  npx playwright show-report --port=9324

THE MINIMUM YOU EVER NEED AGAIN

Once set up, daily work is just two commands:

cd C:\Users\shiva\OneDrive\JavaSelenium\PAIMANA_Playwright_1.1
npx playwright test
