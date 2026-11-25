# Playwright: Basic to Expert Guide

Last updated: 25 November 2026

## Table of contents

- [Introduction](#introduction)
- [Level 1: Basics](#level-1-basics)
- [Level 2: Core concepts](#level-2-core-concepts)
- [Level 3: Intermediate](#level-3-intermediate)
- [Level 4: Advanced](#level-4-advanced)
- [Level 5: Expert](#level-5-expert)
- [Interview Q&A](#interview-qa)
- [Cheat sheet and learning path](#cheat-sheet-and-learning-path)

## Introduction

Playwright is Microsoft's open-source framework for end-to-end testing and browser automation. One API drives Chromium (Chrome, Edge), Firefox and WebKit (Safari engine), headed or headless, on Windows, macOS and Linux.

The current release is 1.65.0, published 5 November 2026 ([TestDino release notes](https://testdino.com/blog/playwright-1-65-release)). It requires Node.js 22, 24 or 26; Node.js 20 is no longer tested ([release feed](https://releasebot.io/updates/categories/testing-frameworks)). All examples here use TypeScript with the `@playwright/test` runner. The same concepts apply to the Python, Java and .NET bindings.

**Why teams pick Playwright**

- **Auto-waiting:** actions wait for elements to be visible, stable, enabled and receiving events, which removes most manual sleeps.
- **Web-first assertions:** `expect(locator).toBeVisible()` retries until it passes or times out.
- **Isolation:** every test gets a fresh browser context, like a new incognito profile, created in milliseconds.
- **Built-in tooling:** codegen, UI mode, Trace Viewer, HTML reports, parallel runs, sharding and retries come in the box.
- **Beyond the UI:** API testing, network mocking, multiple tabs, iframes, file transfers and visual comparisons.

**How to use this guide:** work through Levels 1 to 5 in order, running each example. Then test yourself with the Q&A section and keep the cheat sheet nearby.

## Level 1: Basics

At this level you can install Playwright, write a test, run it and record one with codegen.

### 1.1 Install

```bash
# 1. Create an empty folder for the project and move into it
mkdir pw-demo && cd pw-demo

# 2. Run the Playwright setup wizard (creates the config, sample tests and a CI file)
npm init playwright@latest
# The wizard asks a few questions. Suggested answers:
#   Language: TypeScript | Tests folder: tests
#   Add GitHub Actions workflow: yes | Install browsers: yes
```

To add it to an existing project:

```bash
# Add Playwright's test runner as a dev dependency of your existing project
npm i -D @playwright/test

# Download Chromium, Firefox and WebKit, plus the OS libraries they need
npx playwright install --with-deps
```

### 1.2 Project structure

```text
pw-demo/
├── playwright.config.ts      # settings: browsers, timeouts, base URL, reporters
├── tests/                    # your test files live here
│   └── example.spec.ts       # file names must end in .spec.ts or .test.ts
├── tests-examples/           # a bigger sample test you can read and delete
├── playwright-report/        # HTML report, created after a run (don't commit)
├── test-results/             # traces, screenshots, videos of failures (don't commit)
└── .github/workflows/playwright.yml   # ready-made GitHub Actions pipeline
```

### 1.3 Your first test

```ts
// tests/first.spec.ts
// Import `test` (to declare a test) and `expect` (to check results)
import { test, expect } from '@playwright/test';

// test(title, function). The runner hands us a fresh `page` (a new browser tab)
test('home page has a title and a Get started link', async ({ page }) => {
  // Open the website and wait for it to load
  await page.goto('https://playwright.dev/');

  // Check that the browser tab title contains the word "Playwright"
  await expect(page).toHaveTitle(/Playwright/);

  // Find the link the way a user sees it (a link whose text is "Get started") and click it
  await page.getByRole('link', { name: 'Get started' }).click();

  // Check that the next page shows a heading called "Installation"
  // (expect keeps retrying for up to 5 seconds until it appears)
  await expect(page.getByRole('heading', { name: 'Installation' })).toBeVisible();
});
```

Key ideas:

- `test` declares a test; `expect` makes assertions.
- `{ page }` is a **fixture**: the runner creates a fresh page for each test and closes it afterwards.
- Every browser call is asynchronous, so `await` each one. A missing `await` is the most common beginner bug.

### 1.4 Running tests

| Command | What it does |
| --- | --- |
| `npx playwright test` | Run all tests, headless, on every configured browser |
| `npx playwright test first.spec.ts` | Run one file |
| `npx playwright test -g "title"` | Run tests whose title matches |
| `npx playwright test --project=chromium` | Run one browser project |
| `npx playwright test --headed` | Show the browser window |
| `npx playwright test --ui` | Open UI mode: watch, time-travel, pick locators |
| `npx playwright test --debug` | Step through with the Playwright Inspector |
| `npx playwright show-report` | Open the last HTML report |

### 1.5 Record tests with codegen

```bash
# Opens a browser plus the Inspector window.
# Every click and keystroke you make is written out as test code.
npx playwright codegen https://demo.playwright.dev/todomvc
```

A browser opens next to the Inspector. Every click and keystroke becomes code, using the most resilient locator it can find. Treat the result as a first draft: rename things, add assertions and remove noise.

### 1.6 Using Playwright as a library (no test runner)

```ts
// Use the 'playwright' library directly, without the test runner
import { chromium } from 'playwright';

// Wrap the code in an async function so we can use await
(async () => {
  // Start a Chromium browser; headless: false shows the window on screen
  const browser = await chromium.launch({ headless: false });

  // Open a new tab
  const page = await browser.newPage();

  // Go to the website
  await page.goto('https://example.com');

  // Save a screenshot of the whole page, not just the visible part
  await page.screenshot({ path: 'example.png', fullPage: true });

  // Always close the browser, otherwise the script keeps running
  await browser.close();
})();
```

Use this style for scraping, scripts or PDF generation. Use `@playwright/test` for testing.

## Level 2: Core concepts

This level covers what every test depends on: locators, actions, assertions, auto-waiting, fixtures and hooks.

### 2.1 Locators: how to find elements

A locator describes *how* to find an element. Playwright resolves it again every time it is used, so a locator never goes stale. Choose locators in this order of preference:

| Priority | Locator | Example |
| --- | --- | --- |
| 1 | Role (accessibility) | `page.getByRole('button', { name: 'Sign in' })` |
| 2 | Label | `page.getByLabel('Email')` |
| 3 | Placeholder | `page.getByPlaceholder('Search')` |
| 4 | Text | `page.getByText('Welcome back')` |
| 5 | Alt text / title | `page.getByAltText('logo')`, `page.getByTitle('Close')` |
| 6 | Test id | `page.getByTestId('submit-order')` (uses `data-testid`) |
| 7 | CSS / XPath (last resort) | `page.locator('#cart .item')`, `page.locator('//table/tr[2]')` |

Chaining and filtering:

```ts
// Narrow inside a container:
// 1) find all table rows, 2) keep only the row containing "Invoice #1042"
const row = page.getByRole('row').filter({ hasText: 'Invoice #1042' });
// 3) inside that row only, click the "Pay" button
await row.getByRole('button', { name: 'Pay' }).click();

// Filter by a child element: a product card that contains the text "In stock"
const card = page.locator('.product').filter({ has: page.getByText('In stock') });

// Pick one element when several match
await page.getByRole('listitem').nth(2).click();    // the third item (counting starts at 0)
await page.getByRole('listitem').first().click();   // the first item
// Check that exactly 5 list items exist
await expect(page.getByRole('listitem')).toHaveCount(5);

// .or(): match whichever of the two appears (useful when the UI has two versions)
const newBtn = page.getByRole('button', { name: 'New' }).or(page.getByText('Create'));
```

Locators are **strict**. If a locator matches several elements, an action on it fails. This catches ambiguous selectors early.

### 2.2 Actions

```ts
// Type into a text box (clears any existing text first)
await page.getByLabel('Email').fill('user@test.com');
// Type one key at a time, 50 ms apart (for fields that react to each keystroke)
await page.getByLabel('Password').pressSequentially('s3cr3t', { delay: 50 });
// Tick a checkbox (does nothing if it is already ticked)
await page.getByRole('checkbox', { name: 'Remember me' }).check();
// Choose an option from a <select> dropdown
await page.getByLabel('Country').selectOption('India');
// Normal left click
await page.getByRole('button', { name: 'Submit' }).click();
// Move the mouse over an element (opens hover menus)
await page.getByText('Menu').hover();
// Double-click
await page.getByText('Row 1').dblclick();
// Right-click (opens the context menu)
await page.getByText('File').click({ button: 'right' });
// Press a keyboard shortcut (select all)
await page.keyboard.press('Control+A');
// Drag one element and drop it on another
await page.locator('#source').dragTo(page.locator('#target'));
```

### 2.3 Auto-waiting

Before an action, Playwright runs **actionability checks**. The element must be attached, visible, stable (not animating), able to receive events (not covered) and enabled. It retries these checks until the action timeout. That is why `page.waitForTimeout(3000)` is almost never needed. Treat it as a code smell.

### 2.4 Assertions

**Web-first (auto-retrying) assertions** take a locator or page and retry until they pass or hit `expect` timeout (default 5 s):

```ts
// Each of these retries until it passes or the 5-second expect timeout runs out
await expect(page.getByText('Saved')).toBeVisible();                     // element is shown
await expect(page.getByRole('button', { name: 'Pay' })).toBeEnabled();   // button can be clicked
await expect(page.getByLabel('Name')).toHaveValue('Ravi');               // input contains this value
await expect(page.locator('.price')).toHaveText('₹ 499');               // exact visible text
await expect(page.locator('.tag')).toHaveText(['New', 'Sale']);          // several elements, in order
await expect(page.locator('.alert')).toHaveClass(/error/);               // CSS class contains "error"
await expect(page).toHaveURL(/\/dashboard/);                             // address bar contains /dashboard
await expect(page.locator('.toast')).toBeHidden();                       // element is gone or invisible
```

**Generic (non-retrying) assertions** check plain values once:

```ts
// Read a plain value from the page first (this runs once, no retry)
const count = await page.locator('.item').count();
// Then check that plain value with a normal (non-retrying) assertion
expect(count).toBeGreaterThan(0);
// Same idea for an API response object you already have
expect(response.status()).toBe(200);
```

Useful modifiers:

- `expect.soft(...)` records a failure but lets the test continue.
- `expect(...).not.toBeVisible()` negates.
- `expect(locator).toBeVisible({ timeout: 10_000 })` overrides the timeout.
- `expect.poll(async () => fetchStatus()).toBe('done')` retries any async function.

### 2.5 Fixtures, hooks and grouping

Built-in fixtures: `page`, `context`, `browser`, `browserName`, `request` (API client).

```ts
import { test, expect } from '@playwright/test';

// describe() groups related tests under one name in the report
test.describe('Cart', () => {
  // beforeEach runs before EVERY test in this group
  test.beforeEach(async ({ page }) => {
    await page.goto('/shop');   // relative path: uses baseURL from the config
  });

  test('adds an item', async ({ page }) => {
    // Click the first "Add to cart" button on the page
    await page.getByRole('button', { name: 'Add to cart' }).first().click();
    // The cart badge should now show 1
    await expect(page.getByTestId('cart-count')).toHaveText('1');
  });

  test.skip('coupon flow', async () => {});          // skipped: not run at all
  test.fixme('broken on Safari', async () => {});     // marked as known-broken, not run
  test('slow checkout', async ({ page }) => {
    test.slow();                                      // gives this test 3x the normal timeout
  });
});

// afterAll runs once, after all tests in this file finish (per worker process)
test.afterAll(async () => { /* cleanup, e.g. delete test data */ });
```

Other controls: `test.only` (focus, never commit it), `test.describe.serial` (run in order, stop on first failure), `test.describe.configure({ mode: 'parallel' })`, and tags such as `test('login @smoke', ...)` run with `--grep @smoke`.

### 2.6 Navigation and waiting for events

```ts
// Open a page; continue once the HTML is parsed (don't wait for every image)
await page.goto('/login', { waitUntil: 'domcontentloaded' });
// Wait until the address bar changes to the dashboard (** matches anything)
await page.waitForURL('**/dashboard');

// Start listening for the API response BEFORE clicking,
// otherwise a fast response could arrive before we start waiting
const respPromise = page.waitForResponse('**/api/orders');
await page.getByRole('button', { name: 'Place order' }).click();
const resp = await respPromise;          // now wait for it to arrive
expect(resp.ok()).toBeTruthy();          // status code is 200-299
```

## Level 3: Intermediate

At this level you structure a suite and handle real-world flows: page objects, saved logins, API calls, mocked networks, frames, tabs and files.

### 3.1 Page Object Model (POM)

```ts
// pages/LoginPage.ts
// A Page Object: one class that knows how to find and use the login page
import { Page, Locator, expect } from '@playwright/test';

export class LoginPage {
  // Locators for each element we use, declared once
  readonly email: Locator;
  readonly password: Locator;
  readonly submit: Locator;
  readonly error: Locator;

  // The constructor receives the test's page and sets up the locators
  constructor(private readonly page: Page) {
    this.email = page.getByLabel('Email');
    this.password = page.getByLabel('Password');
    this.submit = page.getByRole('button', { name: 'Sign in' });
    this.error = page.getByRole('alert');   // the red error message box
  }

  // Open the login page
  async goto() { await this.page.goto('/login'); }

  // One method for the whole "log in" action, reusable in any test
  async login(email: string, pwd: string) {
    await this.email.fill(email);
    await this.password.fill(pwd);
    await this.submit.click();
  }

  // Small helper: check the error box shows the given message
  async expectError(msg: string) { await expect(this.error).toHaveText(msg); }
}
```

```ts
// tests/login.spec.ts
import { test } from '@playwright/test';
import { LoginPage } from '../pages/LoginPage';   // our page object

test('rejects a wrong password', async ({ page }) => {
  const login = new LoginPage(page);           // wrap the test's page
  await login.goto();                          // open /login
  await login.login('user@test.com', 'wrong'); // try a bad password
  await login.expectError('Invalid credentials'); // the app must show an error
});
```

Rule of thumb: page objects hold locators and actions. Tests hold the assertions that express business intent, plus thin helpers like `expectError`.

### 3.2 Authentication: log in once, reuse everywhere

Use a **setup project** that logs in and saves cookies and localStorage to a file. Other projects start already signed in.

```ts
// tests/auth.setup.ts
// Runs ONCE before the other tests: logs in and saves the session to a file
import { test as setup, expect } from '@playwright/test';
const authFile = 'playwright/.auth/user.json';   // where cookies + localStorage are saved

setup('authenticate', async ({ page }) => {
  await page.goto('/login');
  // Read credentials from environment variables, never hard-code them
  await page.getByLabel('Email').fill(process.env.USER_EMAIL!);
  await page.getByLabel('Password').fill(process.env.USER_PASSWORD!);
  await page.getByRole('button', { name: 'Sign in' }).click();
  // Make sure login really worked before saving
  await expect(page.getByText('Dashboard')).toBeVisible();
  // Save the logged-in state (cookies, localStorage) to the file
  await page.context().storageState({ path: authFile });
});
```

```ts
// playwright.config.ts (projects part)
projects: [
  // 1) The "setup" project runs only files ending in .setup.ts
  { name: 'setup', testMatch: /.*\.setup\.ts/ },
  {
    name: 'chromium',
    use: {
      ...devices['Desktop Chrome'],                 // Chrome window size, user agent, etc.
      storageState: 'playwright/.auth/user.json',   // start every test already logged in
    },
    dependencies: ['setup'],                        // run "setup" first
  },
],
```

Add `playwright/.auth` to `.gitignore`; the file holds live session tokens. For several roles, save one file per role (`admin.json`, `viewer.json`) and use `test.use({ storageState: '...' })` per file or describe block.

### 3.3 API testing

```ts
import { test, expect } from '@playwright/test';

// `request` is a built-in API client: no browser is opened
test('create and fetch a project via API', async ({ request }) => {
  // Send a POST request with a JSON body and an auth header
  const create = await request.post('/api/projects', {
    data: { name: 'Metro Line 3', budgetCr: 1200 },
    headers: { Authorization: `Bearer ${process.env.API_TOKEN}` },
  });
  expect(create.status()).toBe(201);        // 201 = Created
  const { id } = await create.json();       // read the new project's id from the response

  // Fetch the same project back by id
  const get = await request.get(`/api/projects/${id}`);
  await expect(get).toBeOK();               // status is 200-299
  // The response must contain at least these fields (extra fields are allowed)
  expect(await get.json()).toMatchObject({ name: 'Metro Line 3' });
});
```

A common hybrid pattern seeds data through the API, checks it in the UI, then cleans up through the API. It is fast and keeps UI tests focused.

### 3.4 Network interception and mocking

```ts
// MOCK: when the page calls /api/projects, return fake data instead of the real server
await page.route('**/api/projects', route =>
  route.fulfill({ status: 200, json: [{ id: 1, name: 'Mocked project' }] }));

// MODIFY: let the real request go through, then change the response
await page.route('**/api/user', async route => {
  const response = await route.fetch();     // call the real server
  const json = await response.json();
  json.role = 'admin';                      // pretend the user is an admin
  await route.fulfill({ response, json });  // send the edited response to the page
});

// FAIL: simulate a server error to test the app's error screen
await page.route('**/api/report', route => route.fulfill({ status: 500 }));
// BLOCK: skip images and fonts to make tests faster
await page.route('**/*.{png,jpg,woff2}', route => route.abort());

// REPLAY: answer API calls from a recorded HAR file (update: true re-records it)
await page.routeFromHAR('hars/api.har', { url: '**/api/**', update: false });
```

Register routes **before** the navigation or action that triggers the request.

### 3.5 Frames, new tabs, popups and dialogs

```ts
// IFRAME: step inside the payment iframe, then find elements as usual
const frame = page.frameLocator('#payment-iframe');
await frame.getByLabel('Card number').fill('4242 4242 4242 4242');

// NEW TAB: start waiting for the new tab BEFORE clicking the link that opens it
const pagePromise = context.waitForEvent('page');
await page.getByRole('link', { name: 'Open report' }).click();
const newTab = await pagePromise;          // the new tab's Page object
await newTab.waitForLoadState();           // wait for it to finish loading
await expect(newTab).toHaveTitle(/Report/);

// POPUP: same idea for a window opened with window.open()
const popupPromise = page.waitForEvent('popup');
await page.getByText('Help').click();
const popup = await popupPromise;

// DIALOG: alert/confirm/prompt boxes are dismissed automatically unless you handle them.
// Here we register a one-time handler that clicks "OK"
page.once('dialog', d => d.accept());
await page.getByRole('button', { name: 'Delete' }).click();
```

### 3.6 File upload and download

```ts
// UPLOAD: put a local file into an <input type="file">
await page.getByLabel('Attach file').setInputFiles('fixtures/report.pdf');
await page.getByLabel('Attach file').setInputFiles([]);   // remove the selected file

// DOWNLOAD: start waiting first, then click the button that triggers the download
const downloadPromise = page.waitForEvent('download');
await page.getByRole('button', { name: 'Export CSV' }).click();
const download = await downloadPromise;
// Check the file name the server suggested ends in .csv
expect(download.suggestedFilename()).toMatch(/\.csv$/);
// Save the file to the downloads folder so you can inspect it
await download.saveAs('downloads/' + download.suggestedFilename());
```

### 3.7 Test data and environments

- Keep secrets in environment variables. Load them with `dotenv` in the config; never hard-code them.
- Data-driven tests: loop over an array and create one `test()` per item, giving each a unique title.

```ts
// Test data: one entry per case we want to check
const users = [
  { role: 'admin', canDelete: true },
  { role: 'viewer', canDelete: false },
];
// Create one separate test per entry; the title must be unique, so include the role
for (const u of users) {
  test(`delete button for ${u.role}`, async ({ page }) => {
    /* log in as u.role, then check the Delete button matches u.canDelete */
  });
}
```

## Level 4: Advanced

At this level you control how the suite runs: configuration, parallelism, custom fixtures, visual checks, debugging and reporting.

### 4.1 A production-grade playwright.config.ts

```ts
import { defineConfig, devices } from '@playwright/test';
import 'dotenv/config';                  // loads variables from a .env file into process.env

export default defineConfig({
  testDir: './tests',                    // where to look for test files
  timeout: 30_000,                       // max time for one test: 30 s
  expect: { timeout: 5_000 },            // max time one expect() keeps retrying: 5 s
  fullyParallel: true,                   // run tests inside a file in parallel too
  forbidOnly: !!process.env.CI,          // on CI, fail if someone committed test.only
  retries: process.env.CI ? 2 : 0,       // retry failed tests twice on CI, never locally
  workers: process.env.CI ? 4 : undefined, // 4 parallel processes on CI; local = auto
  // Several reports at once: HTML for people, list for the console, JUnit for CI tools
  reporter: [['html', { open: 'never' }], ['list'], ['junit', { outputFile: 'results.xml' }]],

  // Settings shared by every test
  use: {
    baseURL: process.env.BASE_URL ?? 'http://localhost:3000', // page.goto('/x') uses this
    trace: 'on-first-retry',             // record a trace when a test is retried
    screenshot: 'only-on-failure',       // keep screenshots only for failed tests
    video: 'retain-on-failure',          // keep videos only for failed tests
    actionTimeout: 10_000,               // max time for one click/fill: 10 s
    locale: 'en-IN',                     // browser language and number format
    timezoneId: 'Asia/Kolkata',          // browser time zone (IST)
  },

  // Projects = the same tests run in different setups
  projects: [
    { name: 'setup', testMatch: /.*\.setup\.ts/ },  // login step, runs first
    { name: 'chromium', use: { ...devices['Desktop Chrome'] }, dependencies: ['setup'] },
    { name: 'firefox',  use: { ...devices['Desktop Firefox'] }, dependencies: ['setup'] },
    { name: 'webkit',   use: { ...devices['Desktop Safari'] }, dependencies: ['setup'] },
    { name: 'mobile',   use: { ...devices['Pixel 7'] }, dependencies: ['setup'] }, // phone emulation
  ],

  // Start the app automatically before the tests
  webServer: {
    command: 'npm run start',            // how to start the app
    url: 'http://localhost:3000',        // wait until this URL responds
    reuseExistingServer: !process.env.CI, // locally, reuse an app you already started
  },
});
```

### 4.2 Parallelism, workers and sharding

- **Workers** are separate OS processes. By default, files run in parallel and tests inside a file run in order. `fullyParallel: true` also spreads tests within a file across workers.
- Each worker has its own browser. Tests never share a context unless you build that on purpose.
- **Sharding** splits the suite across machines:

```bash
# Split the suite into 4 equal parts; each machine runs one part
npx playwright test --shard=1/4   # on machine 1
npx playwright test --shard=2/4   # on machine 2 (and so on up to 4/4)

# Afterwards, copy every machine's blob report into one folder
# and merge them into a single HTML report
npx playwright merge-reports --reporter html ./all-blob-reports
```

Use `reporter: 'blob'` on the shards so that `merge-reports` can combine them.

### 4.3 Custom fixtures (the expert's main tool)

Fixtures replace most `beforeEach` code. They are lazy, composable, typed and handle their own teardown.

```ts
// fixtures.ts
import { test as base, expect } from '@playwright/test';
import { LoginPage } from './pages/LoginPage';
import { DashboardPage } from './pages/DashboardPage';

// Describe the type of each custom fixture we are adding
type Fixtures = {
  loginPage: LoginPage;
  dashboard: DashboardPage;
  testProject: { id: number; name: string };
};

// base.extend() creates a new `test` that knows about our fixtures
export const test = base.extend<Fixtures>({
  // Page-object fixtures: build the object and hand it to the test with use()
  loginPage: async ({ page }, use) => { await use(new LoginPage(page)); },
  dashboard: async ({ page }, use) => { await use(new DashboardPage(page)); },

  // Data fixture with setup AND teardown in one place
  testProject: async ({ request }, use) => {
    // SETUP: create a project through the API with a unique name
    const res = await request.post('/api/projects', { data: { name: `pw-${Date.now()}` } });
    const project = await res.json();
    await use(project);                                   // the test runs here
    // TEARDOWN: runs after the test, even if the test failed
    await request.delete(`/api/projects/${project.id}`);
  },
});
// Re-export expect so tests import everything from one file
export { expect };
```

```ts
// tests/project.spec.ts
// Import from OUR fixtures file, not from '@playwright/test'
import { test, expect } from '../fixtures';

// Just name the fixtures you need: Playwright creates them (and cleans up) for you
test('project appears on dashboard', async ({ dashboard, testProject }) => {
  await dashboard.goto();
  // testProject was created by the fixture, so we know its name
  await expect(dashboard.projectRow(testProject.name)).toBeVisible();
});
```

Worker-scoped fixtures (`{ scope: 'worker' }`) are created once per worker, for example a database connection. `{ auto: true }` makes a fixture run for every test, for example to capture console errors.

### 4.4 Visual regression testing

```ts
// Compare the page with a saved baseline image called dashboard.png
await expect(page).toHaveScreenshot('dashboard.png', {
  fullPage: true,                 // capture the whole scrollable page
  maxDiffPixelRatio: 0.01,        // allow up to 1% of pixels to differ
  // Cover parts that change on every run so they don't cause false failures
  mask: [page.locator('.timestamp'), page.locator('.avatar')],
  animations: 'disabled',         // stop CSS animations before capturing
});
// Compare just one element instead of the whole page
await expect(page.getByTestId('chart')).toHaveScreenshot();
```

The first run writes the baseline. Update baselines with `npx playwright test --update-snapshots`. Baselines depend on OS and browser, so generate them in the same Docker image CI uses.

For structure rather than pixels, use ARIA snapshots: `await expect(page.getByRole('navigation')).toMatchAriaSnapshot(...)`.

### 4.5 Debugging toolkit

| Tool | When to use | How |
| --- | --- | --- |
| UI mode | Local development; time-travel through every step | `npx playwright test --ui` |
| Inspector | Step line by line, try locators live | `--debug` or `await page.pause()` |
| Trace Viewer | A CI failure you cannot reproduce: DOM snapshots, network, console, source | `npx playwright show-trace trace.zip`, or open it from the HTML report |
| VS Code extension | Run or debug from the editor, pick locators, set breakpoints | "Playwright Test for VS Code" |
| Verbose logs | Find out why an action is waiting | `DEBUG=pw:api npx playwright test` |
| Console / network events | Catch app errors during tests | `page.on('console', ...)`, `page.on('requestfailed', ...)` |

### 4.6 Reporters and test annotations

- Built-in reporters: `list`, `line`, `dot`, `html`, `json`, `junit`, `blob`, `github`.
- Add a custom reporter by implementing `onBegin`, `onTestEnd` and `onEnd`.
- `test.step('Add item to cart', async () => {...})` groups actions into readable steps in reports and traces.
- `testInfo.attach('payload', { body: JSON.stringify(data), contentType: 'application/json' })` attaches evidence to the report.
- `test.info().annotations.push({ type: 'issue', description: 'JIRA-123' })` links a test to a ticket.

### 4.7 Emulation

```ts
// test.use() changes browser settings for the tests in this file or describe block
test.use({
  ...devices['iPhone 15'],                                  // screen size, touch, mobile user agent
  geolocation: { latitude: 28.6139, longitude: 77.2090 },   // pretend to be in New Delhi
  permissions: ['geolocation'],                             // allow location access without a prompt
  colorScheme: 'dark',                                      // dark mode
  offline: false,                                           // set true to test offline behaviour
});

// Freeze the browser clock at a fixed date and time (useful for date-based UI)
await page.clock.setFixedTime(new Date('2026-03-31T10:00:00+05:30'));
```

## Level 5: Expert

Expert work is less about API calls and more about building a suite a team can trust: reliable in CI, fast, maintainable and broad in what it covers.

### 5.1 CI/CD with GitHub Actions (sharded)

```yaml
# .github/workflows/playwright.yml
name: e2e
on: [push, pull_request]            # run on every push and pull request

jobs:
  # JOB 1: run the tests, split across 4 parallel machines
  test:
    runs-on: ubuntu-latest
    # Official image with browsers pre-installed; keep the tag equal to your package version
    container: mcr.microsoft.com/playwright:v1.65.0-noble
    strategy:
      fail-fast: false                # let all shards finish even if one fails
      matrix: { shard: [1, 2, 3, 4] } # creates 4 copies of this job
    steps:
      - uses: actions/checkout@v4     # get the repository code
      - uses: actions/setup-node@v4
        with: { node-version: 22 }
      - run: npm ci                   # install exact versions from package-lock.json
      # Each copy runs its quarter of the suite and writes a "blob" report
      - run: npx playwright test --shard=${{ matrix.shard }}/4 --reporter=blob
        env:                          # secrets come from repo settings, not from code
          BASE_URL: ${{ secrets.BASE_URL }}
          USER_PASSWORD: ${{ secrets.USER_PASSWORD }}
      # Save the blob report even when tests fail (if: always())
      - uses: actions/upload-artifact@v4
        if: always()
        with: { name: blob-${{ matrix.shard }}, path: blob-report, retention-days: 7 }

  # JOB 2: combine the 4 blob reports into one HTML report
  merge:
    needs: test                       # wait for all test shards
    if: always()                      # merge even if some tests failed
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with: { node-version: 22 }
      - run: npm ci
      # Download every blob-* artifact into one folder
      - uses: actions/download-artifact@v4
        with: { path: all-blob-reports, pattern: blob-*, merge-multiple: true }
      - run: npx playwright merge-reports --reporter html ./all-blob-reports
      # Publish the final HTML report so you can download and open it
      - uses: actions/upload-artifact@v4
        with: { name: html-report, path: playwright-report }
```

The same pattern works on GitLab CI, Jenkins or Azure DevOps: use the official Docker image, pass secrets as environment variables, publish JUnit and HTML reports, and keep traces as artifacts. If your company Git is self-hosted (for example a GitLab-based forge), use its native CI file with the same steps.

### 5.2 A flakiness strategy

A flaky test passes and fails on the same code. Root causes, in rough order of frequency:

1. **Shared state:** tests depend on each other's data. Fix: unique data per test (fixtures plus timestamps), API cleanup, no `describe.serial` chains.
2. **Wrong waits:** fixed sleeps or non-retrying checks such as `expect(await loc.isVisible()).toBe(true)`. Fix: web-first assertions, `waitForResponse`, `expect.poll`.
3. **Weak locators:** CSS tied to layout or auto-generated class names. Fix: roles, labels and test ids.
4. **Environment:** slow test servers, third-party scripts, animations. Fix: mock third parties with `page.route`, `animations: 'disabled'`, separate test environments.
5. **Time and randomness:** dates, time zones, A/B flags. Fix: `page.clock`, fixed `timezoneId`, feature-flag overrides.

Process: run with `--repeat-each=20 --workers=4` to reproduce. Read the trace from the first retry. Quarantine with a `@flaky` tag rather than ignoring it, and track the flaky rate as a metric. Retries are a safety net, not a fix; a test that passes only on retry still needs work.

### 5.3 Framework architecture

```text
e2e/
├── playwright.config.ts # one config: projects per browser and environment
├── fixtures/            # base.extend: page objects, test data, API clients for tests
├── pages/               # page objects and shared components (Header, DataTable)
├── api/                 # typed API clients used to create and delete test data
├── data/                # factories that build fresh data (not static JSON dumps)
├── utils/               # date helpers, environment config, custom matchers
├── tests/
│   ├── smoke/           # @smoke: quick checks, under 5 minutes, run on every PR
│   ├── regression/      # full suite, run nightly on all browsers
│   └── api/             # API-only tests (no browser)
└── reporters/           # custom reporter, e.g. post results to Slack or a dashboard
```

Principles:

- Tests read like specs. Mechanics live in fixtures and page objects.
- Each test is independent and can run alone, in any order, in parallel.
- Prefer API setup over UI setup. Test each UI flow once through the UI, then shortcut it everywhere else.
- Add custom matchers with `expect.extend` for domain checks, for example `toHaveStatusBadge('Delayed')`.
- Run lint rules such as `eslint-plugin-playwright` to catch missing `await`, `page.waitForTimeout` and committed `test.only`.

### 5.4 Performance of the suite

- Reuse auth with `storageState`. Logging in through the UI in every test is the biggest waste.
- Block analytics, fonts and images you don't assert on.
- Use `fullyParallel` plus sharding. Balance shards so no single slow file dominates.
- Run a smoke tag on PRs and the full matrix nightly.
- Measure page performance where it matters, for example with `page.evaluate(() => performance.getEntriesByType('navigation'))`.

### 5.5 Accessibility testing

```ts
// Install first: npm i -D @axe-core/playwright
import AxeBuilder from '@axe-core/playwright';

test('dashboard has no serious a11y violations', async ({ page }) => {
  await page.goto('/dashboard');
  // Scan the page against WCAG 2.0 level A and AA rules
  const results = await new AxeBuilder({ page }).withTags(['wcag2a', 'wcag2aa']).analyze();
  // Keep only serious and critical problems; the test fails if any remain
  expect(results.violations.filter(v => ['serious', 'critical'].includes(v.impact!))).toEqual([]);
});
```

Role-based locators give you basic accessibility coverage for free: if `getByRole('button', { name: 'Save' })` cannot find the button, screen readers probably can't either.

### 5.6 Component testing (experimental)

```ts
// Button.spec.tsx  (package: @playwright/experimental-ct-react)
import { test, expect } from '@playwright/experimental-ct-react';
import { Button } from './Button';   // the React component under test

test('calls onClick', async ({ mount }) => {
  let clicked = false;                // will flip to true when the button is clicked
  // mount() renders just this component in a real browser
  const c = await mount(<Button label="Save" onClick={() => (clicked = true)} />);
  await c.click();                    // click the rendered button
  expect(clicked).toBe(true);         // our onClick handler must have run
});
```

This mounts React, Vue or Svelte components in a real browser. It is still experimental; many teams use Vitest browser mode or Storybook tests for this layer instead.

### 5.7 AI agents, MCP and the CLI

- **Playwright MCP** (`npx @playwright/mcp@latest`) lets AI assistants drive a browser through accessibility snapshots instead of screenshots. Use it to explore an app, reproduce bugs or draft tests.
- **playwright-cli** is a token-efficient command-line mode for agents, added in 1.58 ([release notes](https://overctrl-web.vercel.app/r/microsoft/playwright/releases/v1.58.0)).
- Treat AI-generated tests like codegen output: review locators, add meaningful assertions and keep them under your fixtures and conventions.

### 5.8 Keeping up with releases

Playwright ships a minor version about every 4 to 8 weeks. Recent versions added test locks (1.63) and `locator.within()` plus shuffled runs (1.64) ([TestDino](https://testdino.com/blog/playwright-1-64-release)), and 1.65 brought Bun support and 11 breaking changes ([TestDino](https://testdino.com/blog/playwright-1-65-release)). Pin the exact version in `package.json`, keep the Docker image tag in step with it, and read the release notes before each upgrade.

## Interview Q&A

These 32 questions go from basic to scenario-based. Try answering each one aloud before reading the answer.

### Basic

**Q1. What is Playwright and which browsers does it support?** A Node-based (also Python, Java, .NET) framework for end-to-end testing and automation. It drives Chromium, Firefox and WebKit through one API, plus branded Chrome and Edge through `channel`.

**Q2. How is Playwright different from Selenium?** Selenium talks to browsers through WebDriver, one HTTP command at a time. Playwright uses a persistent WebSocket connection with browser-specific protocols, so it is faster and can see network events. Playwright has built-in auto-waiting, web-first assertions, a test runner, parallelism, tracing and network mocking. Selenium needs extra libraries for most of these, but supports more languages and older browsers.

**Q3. How is it different from Cypress?** Cypress runs inside the browser; Playwright controls it from outside. So Playwright handles multiple tabs, multiple origins, iframes and several browsers, including Safari's WebKit, naturally, and it parallelises for free. Cypress has a polished interactive runner, but its parallel runs and some features depend on its paid cloud.

**Q4. What are Browser, BrowserContext and Page?** `Browser` is one browser process. `BrowserContext` is an isolated session inside it, with its own cookies, storage and cache, like an incognito window. `Page` is a single tab in a context. The test runner gives each test a new context, which is cheap and keeps tests isolated.

**Q5. What is auto-waiting?** Before an action, Playwright waits until the element is attached, visible, stable, enabled and not covered by another element. It retries until the action timeout, so manual sleeps are rarely needed.

**Q6. What's the difference between `locator()` and `$()` / `elementHandle`?** A locator is lazy and re-queries the DOM on every use, so it doesn't go stale and works with auto-waiting. An ElementHandle points to one specific node and can go stale. Handles are discouraged; use locators.

**Q7. Which locator should you prefer and why?** `getByRole` with an accessible name first, then `getByLabel`, `getByPlaceholder`, `getByText`, then `getByTestId`. CSS and XPath come last. User-facing locators survive refactors and reflect what users and assistive technology see.

**Q8. `fill()` vs `pressSequentially()`?** `fill()` sets the value in one go after clearing it; it is fast and right for most inputs. `pressSequentially()` types key by key and fires every keyboard event. Use it for autocomplete or input masks that react to each key.

**Q9. What does `npx playwright codegen` do?** It opens a browser and records your actions as test code with suggested locators. It's a starting point, not a finished test.

**Q10. Why do tests fail when you forget `await`?** Playwright calls return Promises. Without `await`, the test moves on before the action or assertion finishes. Assertions may never be checked, and the test may end while the browser is still working. ESLint's `no-floating-promises` rule catches this.

### Intermediate

**Q11. Web-first vs generic assertions?** Web-first assertions (`await expect(locator).toBeVisible()`) retry until a timeout. Generic assertions (`expect(value).toBe(5)`) check once. Writing `expect(await loc.isVisible()).toBe(true)` throws away the retry and causes flakiness.

**Q12. What is strict mode?** An action on a locator that matches more than one element throws an error. It forces precise locators. Use `.first()`, `.nth()` or `.filter()` only when you really mean "one of many".

**Q13. How do you handle authentication efficiently?** Log in once in a setup project, save `storageState` to a JSON file, and point other projects at it with `use.storageState` and `dependencies: ['setup']`. Use one file per role. Never commit the file.

**Q14. How do you mock an API?** Call `page.route(urlPattern, handler)` before the request happens. In the handler, `route.fulfill()` returns fake data, `route.abort()` blocks the request, `route.continue()` changes it, and `route.fetch()` modifies a real response. `routeFromHAR` replays recorded traffic.

**Q15. How do you handle a new tab?** Start waiting first: `const p = context.waitForEvent('page')`, then click, then `const newPage = await p`. If you wait after the click, you may miss the event.

**Q16. How do you work with iframes?** `page.frameLocator('iframe#pay').getByLabel('Card')`. Frame locators chain like normal locators and auto-wait for the frame.

**Q17. What is the Page Object Model and its pros and cons?** A class per page or component holds locators and actions. Pros: reuse, one place to fix a changed selector, readable tests. Cons: it can become a god class, or hide assertions so tests stop saying what they check. Keep page objects small and expose them through fixtures.

**Q18. How do you run tests across browsers and devices?** Define `projects` in the config with `devices['Desktop Firefox']`, `devices['iPhone 15']` and so on. Run all of them or select one with `--project`.

**Q19. How do you do API testing in Playwright?** Use the `request` fixture or `request.newContext()` to send GET, POST, PUT and DELETE, then assert on `status()`, `json()` and `toBeOK()`. It shares cookies with the browser context when you use `page.request`.

**Q20. `beforeEach` vs fixtures?** Hooks run for every test in scope, whether the test needs them or not, and teardown is separate. Fixtures run only when a test asks for them, compose with each other, are typed, and keep setup and teardown together around `use()`. Experts prefer fixtures.

### Advanced

**Q21. Explain workers, `fullyParallel` and sharding.** Workers are parallel processes on one machine. Files are spread across them; `fullyParallel` also spreads tests within a file. Sharding (`--shard=i/n`) splits the suite across machines. The blob reports are then combined with `merge-reports`.

**Q22. Test-scoped vs worker-scoped fixtures?** Test-scoped fixtures are created and torn down for each test, like `page`. Worker-scoped fixtures are created once per worker process and shared by its tests, for example a DB pool or an expensive login. Mutable state in worker fixtures can leak between tests, so use them carefully.

**Q23. How does the Trace Viewer help, and what trace setting would you use in CI?** It records each action with DOM snapshots before and after, network calls, console output, the source line and timing, so you can replay a CI failure offline. `trace: 'on-first-retry'` (or `retain-on-failure`) gives evidence without the cost of tracing every passing test.

**Q24. How do you make visual tests stable?** Generate baselines in the same Docker image as CI. Mask dynamic regions, disable animations and fix fonts, time and locale. Use a small `maxDiffPixelRatio`, and snapshot components rather than whole pages where you can.

**Q25. How do you test time-dependent features?** Use the clock API: `page.clock.setFixedTime()`, `page.clock.install()` with `runFor()`, or `fastForward()`. Set `timezoneId` and `locale` in the config.

**Q26. How do you retry an arbitrary async condition?** `await expect.poll(() => api.getStatus(id), { timeout: 30_000 }).toBe('done')`, or `await expect(async () => { ... }).toPass()` for a whole block.

**Q27. How do you extend `expect` with custom matchers?** Call `expect.extend({ async toHaveBadge(locator, text) { ... return { pass, message } } })`, then export that `expect` from your fixtures file, and add type declarations for TypeScript.

### Scenario-based

**Q28. A test passes locally but fails in CI 1 time in 10. How do you investigate?** Open the trace from the failed run. Reproduce with `--repeat-each=30 --workers=<CI count>`. Look for shared data, race conditions such as a click before a response arrives, non-retrying assertions, animations and CI being slower. Fix the root cause: wait for the specific response or state, use unique data, mock unstable dependencies. Tag it `@flaky` until fixed, and don't just raise retries.

**Q29. Your suite of 1,200 tests takes 90 minutes. How would you cut it to 15?** Measure first, using the HTML report's durations. Replace UI logins with `storageState` and UI data setup with API calls. Block third-party assets. Enable `fullyParallel` and shard across 6 to 8 machines. Split into a PR smoke set and a nightly full matrix. Remove duplicate coverage that unit or API tests already provide.

**Q30. A button only enables after a backend job completes, which can take up to 60 s. How do you wait?** Use `await expect(button).toBeEnabled({ timeout: 60_000 })`, or poll the job API with `expect.poll` and then assert on the UI. Never use `waitForTimeout(60000)`.

**Q31. The app uses OTP or CAPTCHA at login. How do you automate it?** You shouldn't beat these in tests. Ask for a test-environment bypass: a fixed OTP, a test-mode CAPTCHA key or an API that issues a session. Or log in once through a backdoor API and save `storageState`. For real OTP flows, read the code from a test mailbox or SMS API.

**Q32. How would you design a framework for a team of 10 testers across 3 apps?** Use a shared fixtures and utilities package, a page-object layer per app, and typed API clients for data. Use config projects per app, environment and browser, with environment-based config and secrets in the CI vault. Enforce lint rules and code review, tag tests by suite and owner, and run sharded CI with merged HTML reports. Track pass rate, flaky rate and duration on a dashboard, and document conventions in a README.

## Cheat sheet and learning path

### Cheat sheet

| Need | Code |
| --- | --- |
| Go to a page | `await page.goto('/path')` |
| Find by role | `page.getByRole('button', { name: 'Save' })` |
| Find by label / test id | `page.getByLabel('Email')`, `page.getByTestId('id')` |
| Narrow a match | `.filter({ hasText: 'x' })`, `.nth(i)`, `.first()` |
| Type / click / select | `.fill('v')`, `.click()`, `.selectOption('v')` |
| Assert visible / text | `await expect(loc).toBeVisible()`, `.toHaveText('x')` |
| Assert URL / title | `await expect(page).toHaveURL(/x/)`, `.toHaveTitle(/x/)` |
| Wait for an API call | `const r = page.waitForResponse('**/api/x'); await act; await r` |
| Mock an API | `await page.route('**/api/x', r => r.fulfill({ json }))` |
| iframe | `page.frameLocator('#f').getByText('x')` |
| New tab | `const p = context.waitForEvent('page'); await click; await p` |
| Upload / download | `.setInputFiles('f.pdf')`, `page.waitForEvent('download')` |
| Saved login | `use: { storageState: 'auth.json' }` |
| Screenshot compare | `await expect(page).toHaveScreenshot()` |
| Pause for debugging | `await page.pause()` |
| Run in UI mode | `npx playwright test --ui` |
| Reproduce flakiness | `npx playwright test --repeat-each=20` |
| Split across machines | `npx playwright test --shard=1/4` |

### Suggested 6-week learning path

1. **Week 1, basics:** install, codegen, write 10 tests against a demo app such as `demo.playwright.dev/todomvc`, and learn the CLI flags.
2. **Week 2, core:** locators (role first), web-first assertions, hooks and tags. Rewrite your codegen tests by hand.
3. **Week 3, structure:** page objects, `storageState` auth, environment config with `.env`, data-driven tests.
4. **Week 4, beyond the UI:** API testing, `page.route` mocking, frames, tabs, files and dialogs.
5. **Week 5, scale:** custom fixtures, projects across browsers and mobile, Trace Viewer, visual tests, reporters.
6. **Week 6, production:** CI with Docker and sharding, a flakiness playbook, axe accessibility checks, lint rules, and a framework layout you could present in an interview.

Practice project: automate an end-to-end flow on a real app you know, covering login, create, search, edit, export and delete. Include API seeding, one mocked error state and a CI pipeline.

### Sources

- [Playwright docs](https://playwright.dev/docs/intro) and [release notes](https://playwright.dev/docs/release-notes)
- [Playwright 1.65 release guide (TestDino)](https://testdino.com/blog/playwright-1-65-release)
- [Playwright 1.64 release guide (TestDino)](https://testdino.com/blog/playwright-1-64-release)
- [Testing frameworks release feed (Releasebot)](https://releasebot.io/updates/categories/testing-frameworks)
- [Playwright 1.58 release notes](https://overctrl-web.vercel.app/r/microsoft/playwright/releases/v1.58.0)
