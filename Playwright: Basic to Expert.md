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
mkdir pw-demo && cd pw-demo
npm init playwright@latest
# choose: TypeScript, tests folder = tests, add GitHub Actions = yes, install browsers = yes
```

To add it to an existing project:

```bash
npm i -D @playwright/test
npx playwright install --with-deps   # downloads browsers plus OS dependencies
```

### 1.2 Project structure

```text
pw-demo/
├── playwright.config.ts      # runner configuration
├── tests/
│   └── example.spec.ts       # test files end in .spec.ts or .test.ts
├── tests-examples/
├── playwright-report/        # HTML report (generated)
├── test-results/             # traces, screenshots, videos (generated)
└── .github/workflows/playwright.yml
```

### 1.3 Your first test

```ts
// tests/first.spec.ts
import { test, expect } from '@playwright/test';

test('home page has a title and a Get started link', async ({ page }) => {
  await page.goto('https://playwright.dev/');

  await expect(page).toHaveTitle(/Playwright/);

  await page.getByRole('link', { name: 'Get started' }).click();

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
npx playwright codegen https://demo.playwright.dev/todomvc
```

A browser opens next to the Inspector. Every click and keystroke becomes code, using the most resilient locator it can find. Treat the result as a first draft: rename things, add assertions and remove noise.

### 1.6 Using Playwright as a library (no test runner)

```ts
import { chromium } from 'playwright';

(async () => {
  const browser = await chromium.launch({ headless: false });
  const page = await browser.newPage();
  await page.goto('https://example.com');
  await page.screenshot({ path: 'example.png', fullPage: true });
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
// narrow inside a container
const row = page.getByRole('row').filter({ hasText: 'Invoice #1042' });
await row.getByRole('button', { name: 'Pay' }).click();

// filter by a child element
const card = page.locator('.product').filter({ has: page.getByText('In stock') });

// pick from several matches
await page.getByRole('listitem').nth(2).click();
await page.getByRole('listitem').first().click();
await expect(page.getByRole('listitem')).toHaveCount(5);

// either / both
const newBtn = page.getByRole('button', { name: 'New' }).or(page.getByText('Create'));
```

Locators are **strict**. If a locator matches several elements, an action on it fails. This catches ambiguous selectors early.

### 2.2 Actions

```ts
await page.getByLabel('Email').fill('user@test.com');      // clears, then types
await page.getByLabel('Password').pressSequentially('s3cr3t', { delay: 50 }); // key by key
await page.getByRole('checkbox', { name: 'Remember me' }).check();
await page.getByLabel('Country').selectOption('India');
await page.getByRole('button', { name: 'Submit' }).click();
await page.getByText('Menu').hover();
await page.getByText('Row 1').dblclick();
await page.getByText('File').click({ button: 'right' });
await page.keyboard.press('Control+A');
await page.locator('#source').dragTo(page.locator('#target'));
```

### 2.3 Auto-waiting

Before an action, Playwright runs **actionability checks**. The element must be attached, visible, stable (not animating), able to receive events (not covered) and enabled. It retries these checks until the action timeout. That is why `page.waitForTimeout(3000)` is almost never needed. Treat it as a code smell.

### 2.4 Assertions

**Web-first (auto-retrying) assertions** take a locator or page and retry until they pass or hit `expect` timeout (default 5 s):

```ts
await expect(page.getByText('Saved')).toBeVisible();
await expect(page.getByRole('button', { name: 'Pay' })).toBeEnabled();
await expect(page.getByLabel('Name')).toHaveValue('Ravi');
await expect(page.locator('.price')).toHaveText('₹ 499');
await expect(page.locator('.tag')).toHaveText(['New', 'Sale']);   // list
await expect(page.locator('.alert')).toHaveClass(/error/);
await expect(page).toHaveURL(/\/dashboard/);
await expect(page.locator('.toast')).toBeHidden();
```

**Generic (non-retrying) assertions** check plain values once:

```ts
const count = await page.locator('.item').count();
expect(count).toBeGreaterThan(0);
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

test.describe('Cart', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/shop');
  });

  test('adds an item', async ({ page }) => {
    await page.getByRole('button', { name: 'Add to cart' }).first().click();
    await expect(page.getByTestId('cart-count')).toHaveText('1');
  });

  test.skip('coupon flow', async () => {});          // skip
  test.fixme('broken on Safari', async () => {});     // known broken
  test('slow checkout', async ({ page }) => {
    test.slow();                                      // triples timeout
  });
});

test.afterAll(async () => { /* cleanup once per worker */ });
```

Other controls: `test.only` (focus, never commit it), `test.describe.serial` (run in order, stop on first failure), `test.describe.configure({ mode: 'parallel' })`, and tags such as `test('login @smoke', ...)` run with `--grep @smoke`.

### 2.6 Navigation and waiting for events

```ts
await page.goto('/login', { waitUntil: 'domcontentloaded' });
await page.waitForURL('**/dashboard');
const respPromise = page.waitForResponse('**/api/orders');
await page.getByRole('button', { name: 'Place order' }).click();
const resp = await respPromise;          // start waiting BEFORE the click
expect(resp.ok()).toBeTruthy();
```

## Level 3: Intermediate

At this level you structure a suite and handle real-world flows: page objects, saved logins, API calls, mocked networks, frames, tabs and files.

### 3.1 Page Object Model (POM)

```ts
// pages/LoginPage.ts
import { Page, Locator, expect } from '@playwright/test';

export class LoginPage {
  readonly email: Locator;
  readonly password: Locator;
  readonly submit: Locator;
  readonly error: Locator;

  constructor(private readonly page: Page) {
    this.email = page.getByLabel('Email');
    this.password = page.getByLabel('Password');
    this.submit = page.getByRole('button', { name: 'Sign in' });
    this.error = page.getByRole('alert');
  }

  async goto() { await this.page.goto('/login'); }

  async login(email: string, pwd: string) {
    await this.email.fill(email);
    await this.password.fill(pwd);
    await this.submit.click();
  }

  async expectError(msg: string) { await expect(this.error).toHaveText(msg); }
}
```

```ts
// tests/login.spec.ts
import { test } from '@playwright/test';
import { LoginPage } from '../pages/LoginPage';

test('rejects a wrong password', async ({ page }) => {
  const login = new LoginPage(page);
  await login.goto();
  await login.login('user@test.com', 'wrong');
  await login.expectError('Invalid credentials');
});
```

Rule of thumb: page objects hold locators and actions. Tests hold the assertions that express business intent, plus thin helpers like `expectError`.

### 3.2 Authentication: log in once, reuse everywhere

Use a **setup project** that logs in and saves cookies and localStorage to a file. Other projects start already signed in.

```ts
// tests/auth.setup.ts
import { test as setup, expect } from '@playwright/test';
const authFile = 'playwright/.auth/user.json';

setup('authenticate', async ({ page }) => {
  await page.goto('/login');
  await page.getByLabel('Email').fill(process.env.USER_EMAIL!);
  await page.getByLabel('Password').fill(process.env.USER_PASSWORD!);
  await page.getByRole('button', { name: 'Sign in' }).click();
  await expect(page.getByText('Dashboard')).toBeVisible();
  await page.context().storageState({ path: authFile });
});
```

```ts
// playwright.config.ts (projects part)
projects: [
  { name: 'setup', testMatch: /.*\.setup\.ts/ },
  {
    name: 'chromium',
    use: { ...devices['Desktop Chrome'], storageState: 'playwright/.auth/user.json' },
    dependencies: ['setup'],
  },
],
```

Add `playwright/.auth` to `.gitignore`; the file holds live session tokens. For several roles, save one file per role (`admin.json`, `viewer.json`) and use `test.use({ storageState: '...' })` per file or describe block.

### 3.3 API testing

```ts
import { test, expect } from '@playwright/test';

test('create and fetch a project via API', async ({ request }) => {
  const create = await request.post('/api/projects', {
    data: { name: 'Metro Line 3', budgetCr: 1200 },
    headers: { Authorization: `Bearer ${process.env.API_TOKEN}` },
  });
  expect(create.status()).toBe(201);
  const { id } = await create.json();

  const get = await request.get(`/api/projects/${id}`);
  await expect(get).toBeOK();
  expect(await get.json()).toMatchObject({ name: 'Metro Line 3' });
});
```

A common hybrid pattern seeds data through the API, checks it in the UI, then cleans up through the API. It is fast and keeps UI tests focused.

### 3.4 Network interception and mocking

```ts
// mock a response
await page.route('**/api/projects', route =>
  route.fulfill({ status: 200, json: [{ id: 1, name: 'Mocked project' }] }));

// modify a real response
await page.route('**/api/user', async route => {
  const response = await route.fetch();
  const json = await response.json();
  json.role = 'admin';
  await route.fulfill({ response, json });
});

// simulate failures and block noise
await page.route('**/api/report', route => route.fulfill({ status: 500 }));
await page.route('**/*.{png,jpg,woff2}', route => route.abort());

// record and replay with a HAR file
await page.routeFromHAR('hars/api.har', { url: '**/api/**', update: false });
```

Register routes **before** the navigation or action that triggers the request.

### 3.5 Frames, new tabs, popups and dialogs

```ts
// iframe
const frame = page.frameLocator('#payment-iframe');
await frame.getByLabel('Card number').fill('4242 4242 4242 4242');

// new tab opened by a link
const pagePromise = context.waitForEvent('page');
await page.getByRole('link', { name: 'Open report' }).click();
const newTab = await pagePromise;
await newTab.waitForLoadState();
await expect(newTab).toHaveTitle(/Report/);

// popup window
const popupPromise = page.waitForEvent('popup');
await page.getByText('Help').click();
const popup = await popupPromise;

// JS alert / confirm / prompt (auto-dismissed unless handled)
page.once('dialog', d => d.accept());
await page.getByRole('button', { name: 'Delete' }).click();
```

### 3.6 File upload and download

```ts
// upload
await page.getByLabel('Attach file').setInputFiles('fixtures/report.pdf');
await page.getByLabel('Attach file').setInputFiles([]);   // clear

// download
const downloadPromise = page.waitForEvent('download');
await page.getByRole('button', { name: 'Export CSV' }).click();
const download = await downloadPromise;
expect(download.suggestedFilename()).toMatch(/\.csv$/);
await download.saveAs('downloads/' + download.suggestedFilename());
```

### 3.7 Test data and environments

- Keep secrets in environment variables. Load them with `dotenv` in the config; never hard-code them.
- Data-driven tests: loop over an array and create one `test()` per item, giving each a unique title.

```ts
const users = [
  { role: 'admin', canDelete: true },
  { role: 'viewer', canDelete: false },
];
for (const u of users) {
  test(`delete button for ${u.role}`, async ({ page }) => { /* ... */ });
}
```

## Level 4: Advanced

At this level you control how the suite runs: configuration, parallelism, custom fixtures, visual checks, debugging and reporting.

### 4.1 A production-grade playwright.config.ts

```ts
import { defineConfig, devices } from '@playwright/test';
import 'dotenv/config';

export default defineConfig({
  testDir: './tests',
  timeout: 30_000,                       // per test
  expect: { timeout: 5_000 },
  fullyParallel: true,
  forbidOnly: !!process.env.CI,          // fail CI if test.only is committed
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 4 : undefined,
  reporter: [['html', { open: 'never' }], ['list'], ['junit', { outputFile: 'results.xml' }]],
  use: {
    baseURL: process.env.BASE_URL ?? 'http://localhost:3000',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
    actionTimeout: 10_000,
    locale: 'en-IN',
    timezoneId: 'Asia/Kolkata',
  },
  projects: [
    { name: 'setup', testMatch: /.*\.setup\.ts/ },
    { name: 'chromium', use: { ...devices['Desktop Chrome'] }, dependencies: ['setup'] },
    { name: 'firefox',  use: { ...devices['Desktop Firefox'] }, dependencies: ['setup'] },
    { name: 'webkit',   use: { ...devices['Desktop Safari'] }, dependencies: ['setup'] },
    { name: 'mobile',   use: { ...devices['Pixel 7'] }, dependencies: ['setup'] },
  ],
  webServer: {
    command: 'npm run start',
    url: 'http://localhost:3000',
    reuseExistingServer: !process.env.CI,
  },
});
```

### 4.2 Parallelism, workers and sharding

- **Workers** are separate OS processes. By default, files run in parallel and tests inside a file run in order. `fullyParallel: true` also spreads tests within a file across workers.
- Each worker has its own browser. Tests never share a context unless you build that on purpose.
- **Sharding** splits the suite across machines:

```bash
npx playwright test --shard=1/4   # on machine 1
npx playwright test --shard=2/4   # on machine 2 ...
# merge the blob reports afterwards
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

type Fixtures = {
  loginPage: LoginPage;
  dashboard: DashboardPage;
  testProject: { id: number; name: string };
};

export const test = base.extend<Fixtures>({
  loginPage: async ({ page }, use) => { await use(new LoginPage(page)); },
  dashboard: async ({ page }, use) => { await use(new DashboardPage(page)); },

  // setup + teardown around the test
  testProject: async ({ request }, use) => {
    const res = await request.post('/api/projects', { data: { name: `pw-${Date.now()}` } });
    const project = await res.json();
    await use(project);                                   // test runs here
    await request.delete(`/api/projects/${project.id}`);  // cleanup always runs
  },
});
export { expect };
```

```ts
// tests/project.spec.ts
import { test, expect } from '../fixtures';

test('project appears on dashboard', async ({ dashboard, testProject }) => {
  await dashboard.goto();
  await expect(dashboard.projectRow(testProject.name)).toBeVisible();
});
```

Worker-scoped fixtures (`{ scope: 'worker' }`) are created once per worker, for example a database connection. `{ auto: true }` makes a fixture run for every test, for example to capture console errors.

### 4.4 Visual regression testing

```ts
await expect(page).toHaveScreenshot('dashboard.png', {
  fullPage: true,
  maxDiffPixelRatio: 0.01,
  mask: [page.locator('.timestamp'), page.locator('.avatar')],   // hide dynamic parts
  animations: 'disabled',
});
await expect(page.getByTestId('chart')).toHaveScreenshot();      // a single element
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
test.use({
  ...devices['iPhone 15'],
  geolocation: { latitude: 28.6139, longitude: 77.2090 },   // New Delhi
  permissions: ['geolocation'],
  colorScheme: 'dark',
  offline: false,
});
// clock control for time-dependent UI
await page.clock.setFixedTime(new Date('2026-03-31T10:00:00+05:30'));
```

## Level 5: Expert

Expert work is less about API calls and more about building a suite a team can trust: reliable in CI, fast, maintainable and broad in what it covers.

### 5.1 CI/CD with GitHub Actions (sharded)

```yaml
# .github/workflows/playwright.yml
name: e2e
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    container: mcr.microsoft.com/playwright:v1.65.0-noble   # match your package version
    strategy:
      fail-fast: false
      matrix: { shard: [1, 2, 3, 4] }
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with: { node-version: 22 }
      - run: npm ci
      - run: npx playwright test --shard=${{ matrix.shard }}/4 --reporter=blob
        env:
          BASE_URL: ${{ secrets.BASE_URL }}
          USER_PASSWORD: ${{ secrets.USER_PASSWORD }}
      - uses: actions/upload-artifact@v4
        if: always()
        with: { name: blob-${{ matrix.shard }}, path: blob-report, retention-days: 7 }

  merge:
    needs: test
    if: always()
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with: { node-version: 22 }
      - run: npm ci
      - uses: actions/download-artifact@v4
        with: { path: all-blob-reports, pattern: blob-*, merge-multiple: true }
      - run: npx playwright merge-reports --reporter html ./all-blob-reports
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
├── playwright.config.ts
├── fixtures/            # base.extend: page objects, data, API clients
├── pages/               # page objects and components (Header, DataTable)
├── api/                 # typed API clients for seeding and cleanup
├── data/                # factories and builders, not static JSON dumps
├── utils/               # date helpers, env config, custom matchers
├── tests/
│   ├── smoke/           # @smoke: under 5 minutes, runs on every PR
│   ├── regression/      # nightly, all browsers
│   └── api/
└── reporters/           # custom reporter (Slack, dashboard)
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
import AxeBuilder from '@axe-core/playwright';

test('dashboard has no serious a11y violations', async ({ page }) => {
  await page.goto('/dashboard');
  const results = await new AxeBuilder({ page }).withTags(['wcag2a', 'wcag2aa']).analyze();
  expect(results.violations.filter(v => ['serious', 'critical'].includes(v.impact!))).toEqual([]);
});
```

Role-based locators give you basic accessibility coverage for free: if `getByRole('button', { name: 'Save' })` cannot find the button, screen readers probably can't either.

### 5.6 Component testing (experimental)

```ts
// Button.spec.tsx  (@playwright/experimental-ct-react)
import { test, expect } from '@playwright/experimental-ct-react';
import { Button } from './Button';

test('calls onClick', async ({ mount }) => {
  let clicked = false;
  const c = await mount(<Button label="Save" onClick={() => (clicked = true)} />);
  await c.click();
  expect(clicked).toBe(true);
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
