# 🎭 Playwright: Interview Questions & Complete Guide

A practical guide to Playwright with TypeScript, covering common interview questions and a hands-on tutorial that goes from installation to advanced framework patterns.

> **Tip:** Interview answers are collapsible. Try answering each question yourself before expanding it.

---

## 📑 Table of Contents

- [Part 1: Interview Questions](#part-1-interview-questions)
  - [Beginner](#beginner)
  - [Intermediate](#intermediate)
  - [Advanced](#advanced)
- [Part 2: Step-by-Step Guide](#part-2-step-by-step-guide)
  - [Step 1: Setup](#step-1-setup)
  - [Step 2: The Config File](#step-2-the-config-file)
  - [Step 3: Your First Test](#step-3-your-first-test)
  - [Step 4: Locators](#step-4-locators)
  - [Step 5: Actions and Assertions](#step-5-actions-and-assertions)
  - [Step 6: Hooks and Grouping](#step-6-hooks-and-grouping)
  - [Step 7: Page Object Model](#step-7-page-object-model)
  - [Step 8: Custom Fixtures](#step-8-custom-fixtures)
  - [Step 9: Data-Driven Tests](#step-9-data-driven-tests)
  - [Step 10: Authentication With storageState](#step-10-authentication-with-storagestate)
  - [Step 11: Network Mocking and Interception](#step-11-network-mocking-and-interception)
  - [Step 12: API Testing](#step-12-api-testing)
  - [Step 13: Tabs, Iframes, Dialogs and Downloads](#step-13-tabs-iframes-dialogs-and-downloads)
  - [Step 14: Advanced Assertions](#step-14-advanced-assertions)
  - [Step 15: Visual Regression Testing](#step-15-visual-regression-testing)
  - [Step 16: Parallelism, Retries and Sharding](#step-16-parallelism-retries-and-sharding)
  - [Step 17: Debugging and Tracing](#step-17-debugging-and-tracing)
  - [Step 18: Environment Variables](#step-18-environment-variables)
  - [Step 19: CI With GitHub Actions](#step-19-ci-with-github-actions)
- [Recommended Project Structure](#recommended-project-structure)
- [Interview Tips](#interview-tips)
- [Useful Links](#useful-links)

---

## Part 1: Interview Questions

### Beginner

<details>
<summary><b>1. What is Playwright?</b></summary>

Playwright is an open-source end-to-end testing framework from Microsoft. It automates **Chromium, Firefox and WebKit** with a single API, and supports TypeScript/JavaScript, Python, Java and .NET.

</details>

<details>
<summary><b>2. How is Playwright different from Selenium?</b></summary>

Selenium uses the WebDriver protocol. Playwright talks to browsers directly, which makes it faster and less flaky. Playwright also includes several things Selenium doesn't have built in:

- auto-waiting
- a test runner
- parallel execution
- tracing
- network interception
- API testing
- multi-tab and multi-context support

</details>

<details>
<summary><b>3. How does Playwright compare with Cypress?</b></summary>

Cypress runs inside the browser. That limits it with multiple tabs, multiple origins and iframes, and it doesn't support Safari/WebKit. Playwright runs outside the browser, so it handles multiple tabs, origins and browsers. It also runs tests in parallel for free.

</details>

<details>
<summary><b>4. What is auto-waiting?</b></summary>

Before an action, Playwright waits for the element to be **attached, visible, stable, enabled and able to receive events**. This removes most manual sleeps.

</details>

<details>
<summary><b>5. What are locators?</b></summary>

Locators are lazy, retrying references to elements, such as `page.getByRole('button', { name: 'Submit' })`. They are re-resolved every time you use them, so they don't go stale.

</details>

<details>
<summary><b>6. Which locator strategy is recommended?</b></summary>

Prefer user-facing locators, in this order:

1. `getByRole`
2. `getByLabel`
3. `getByPlaceholder`
4. `getByText`
5. `getByTestId`

Use CSS or XPath only as a last resort.

</details>

<details>
<summary><b>7. What is the difference between Browser, BrowserContext and Page?</b></summary>

| Object | Description |
|---|---|
| **Browser** | The browser instance |
| **BrowserContext** | An isolated, incognito-like session with its own cookies and storage |
| **Page** | A single tab inside a context |

</details>

<details>
<summary><b>8. What are web-first assertions?</b></summary>

Assertions like `await expect(locator).toBeVisible()` that **retry** until the condition is met or the timeout expires.

</details>

### Intermediate

<details>
<summary><b>9. What are fixtures?</b></summary>

Fixtures are reusable setup and teardown units that tests receive as arguments. Built-in ones include `page`, `context`, `browser` and `request`. You create your own with `test.extend()`.

</details>

<details>
<summary><b>10. How do you handle authentication efficiently?</b></summary>

Log in once in a setup project and save the session with `storageState`. Then reuse that saved state in every test, so tests skip the login UI. See [Step 10](#step-10-authentication-with-storagestate).

</details>

<details>
<summary><b>11. How do you mock APIs?</b></summary>

Use `page.route()` and respond with `route.fulfill()`, `route.abort()` or `route.continue()`. See [Step 11](#step-11-network-mocking-and-interception).

</details>

<details>
<summary><b>12. How do you handle new tabs or popups?</b></summary>

Start waiting for the event before you trigger it:

```ts
const pagePromise = context.waitForEvent('page');
await page.click('...');
const newPage = await pagePromise;
```

</details>

<details>
<summary><b>13. How do you handle iframes?</b></summary>

Use `page.frameLocator('#frame').getByRole(...)`.

</details>

<details>
<summary><b>14. What is the Page Object Model (POM)?</b></summary>

A design pattern where each page's locators and actions live in a class. It improves reuse and makes tests easier to maintain. See [Step 7](#step-7-page-object-model).

</details>

<details>
<summary><b>15. What are projects in <code>playwright.config.ts</code>?</b></summary>

Named configurations, for example for different browsers, devices or environments. Projects can depend on each other, such as an auth setup project that must run first.

</details>

<details>
<summary><b>16. What is the difference between <code>toBe</code> and <code>toHaveText</code>?</b></summary>

`toBe` is a one-time, non-retrying value check. `toHaveText` is a web-first assertion on a locator, so it retries until the text matches or the timeout expires.

</details>

### Advanced

<details>
<summary><b>17. How does parallelism work?</b></summary>

Test files run in parallel across worker processes. Tests inside a single file run in order, unless you enable `fullyParallel: true` or `test.describe.configure({ mode: 'parallel' })`.

</details>

<details>
<summary><b>18. What is sharding?</b></summary>

Splitting the test suite across machines, for example with `npx playwright test --shard=1/4`.

</details>

<details>
<summary><b>19. How do you debug a failing test in CI?</b></summary>

Turn on `trace: 'on-first-retry'`, download the trace zip and open it with `npx playwright show-trace`. The trace shows the DOM snapshots, network calls and console output for each step.

</details>

<details>
<summary><b>20. How do you reduce flakiness?</b></summary>

- Use web-first assertions and avoid `waitForTimeout`.
- Isolate tests so they don't share state.
- Mock unstable third-party services.
- Use retries, but only as a safety net.

</details>

<details>
<summary><b>21. What are <code>expect.soft</code>, <code>expect.poll</code> and <code>toPass</code>?</b></summary>

- **`expect.soft`** records a failure but lets the test continue.
- **`expect.poll`** retries a non-locator value, such as the result of an API call.
- **`toPass`** retries a whole block of code until it passes.

See [Step 14](#step-14-advanced-assertions).

</details>

<details>
<summary><b>22. How do you do visual testing?</b></summary>

Use `await expect(page).toHaveScreenshot()`, which compares the page against a baseline image. You can set a pixel tolerance with `maxDiffPixels`.

</details>

<details>
<summary><b>23. Can Playwright test APIs?</b></summary>

Yes. The `request` fixture (an `APIRequestContext`) can send GET, POST and other requests, and you can mix API calls with UI steps in the same test. See [Step 12](#step-12-api-testing).

</details>

---

## Part 2: Step-by-Step Guide

### Step 1: Setup

**Prerequisite:** Node.js 18 or later.

```bash
mkdir pw-demo && cd pw-demo
npm init playwright@latest
# Choose: TypeScript, tests folder, GitHub Actions workflow = yes, install browsers = yes
```

The generated project looks like this:

```
pw-demo/
├── tests/
│   └── example.spec.ts
├── playwright.config.ts
├── package.json
└── .github/workflows/playwright.yml
```

**Most-used commands:**

| Command | Purpose |
|---|---|
| `npx playwright test` | Run all tests (headless) |
| `npx playwright test --headed` | Watch the browser |
| `npx playwright test --ui` | Interactive UI mode |
| `npx playwright test --debug` | Step through with the inspector |
| `npx playwright test login.spec.ts` | Run one file |
| `npx playwright test -g "add todo"` | Run tests whose title matches |
| `npx playwright codegen <url>` | Record a test |
| `npx playwright show-report` | Open the HTML report |

### Step 2: The Config File

```ts
// playwright.config.ts
import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  testDir: './tests',
  fullyParallel: true,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 2 : undefined,
  reporter: [['html'], ['list']],
  use: {
    baseURL: 'https://demo.playwright.dev',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
  },
  projects: [
    { name: 'chromium', use: { ...devices['Desktop Chrome'] } },
    { name: 'firefox',  use: { ...devices['Desktop Firefox'] } },
    { name: 'webkit',   use: { ...devices['Desktop Safari'] } },
    { name: 'mobile',   use: { ...devices['Pixel 7'] } },
  ],
});
```

### Step 3: Your First Test

```ts
// tests/first.spec.ts
import { test, expect } from '@playwright/test';

test('homepage has correct title', async ({ page }) => {
  await page.goto('https://playwright.dev/');
  await expect(page).toHaveTitle(/Playwright/);

  await page.getByRole('link', { name: 'Get started' }).click();
  await expect(page.getByRole('heading', { name: 'Installation' })).toBeVisible();
});
```

### Step 4: Locators

```ts
page.getByRole('button', { name: 'Sign in' });
page.getByLabel('Email');
page.getByPlaceholder('What needs to be done?');
page.getByText('Welcome back');
page.getByTestId('submit-btn');           // matches data-testid="submit-btn"
page.locator('#id .class');               // CSS (last resort)

// Chaining and filtering
page.getByRole('listitem').filter({ hasText: 'Milk' }).getByRole('button');
page.getByRole('row').nth(2);
page.getByRole('listitem').first();
```

### Step 5: Actions and Assertions

This example uses Playwright's TodoMVC demo app:

```ts
import { test, expect } from '@playwright/test';

test('add and complete todos', async ({ page }) => {
  await page.goto('/todomvc');

  const input = page.getByPlaceholder('What needs to be done?');
  await input.fill('Buy milk');
  await input.press('Enter');
  await input.fill('Write tests');
  await input.press('Enter');

  const items = page.getByTestId('todo-item');
  await expect(items).toHaveCount(2);
  await expect(items).toHaveText(['Buy milk', 'Write tests']);

  await items.first().getByRole('checkbox').check();
  await expect(items.first()).toHaveClass(/completed/);
});
```

| Common actions | Common assertions |
|---|---|
| `click`, `dblclick`, `hover` | `toBeVisible`, `toBeHidden` |
| `fill`, `press`, `keyboard.press` | `toBeEnabled`, `toHaveValue` |
| `selectOption`, `check` | `toContainText`, `toHaveText` |
| `setInputFiles`, `dragTo` | `toHaveURL`, `toHaveAttribute` |

### Step 6: Hooks and Grouping

```ts
import { test, expect } from '@playwright/test';

test.describe('Todo app', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/todomvc');
  });

  test('shows empty state', async ({ page }) => {
    await expect(page.getByTestId('todo-item')).toHaveCount(0);
  });

  test.skip('not ready yet', async () => {});
  test.fixme('known bug', async () => {});
  test('@smoke tagged test', async ({ page }) => { /* ... */ }); // run with --grep @smoke
});
```

### Step 7: Page Object Model

**The page class:**

```ts
// pages/TodoPage.ts
import { Page, Locator, expect } from '@playwright/test';

export class TodoPage {
  readonly page: Page;
  readonly input: Locator;
  readonly items: Locator;

  constructor(page: Page) {
    this.page = page;
    this.input = page.getByPlaceholder('What needs to be done?');
    this.items = page.getByTestId('todo-item');
  }

  async goto() {
    await this.page.goto('/todomvc');
  }

  async addTodo(text: string) {
    await this.input.fill(text);
    await this.input.press('Enter');
  }

  async expectCount(n: number) {
    await expect(this.items).toHaveCount(n);
  }
}
```

**A test that uses it:**

```ts
// tests/pom.spec.ts
import { test } from '@playwright/test';
import { TodoPage } from '../pages/TodoPage';

test('POM example', async ({ page }) => {
  const todo = new TodoPage(page);
  await todo.goto();
  await todo.addTodo('Learn POM');
  await todo.expectCount(1);
});
```

### Step 8: Custom Fixtures

Fixtures let tests receive page objects directly, so you don't have to construct them in every test.

```ts
// fixtures.ts
import { test as base } from '@playwright/test';
import { TodoPage } from './pages/TodoPage';

type MyFixtures = { todoPage: TodoPage };

export const test = base.extend<MyFixtures>({
  todoPage: async ({ page }, use) => {
    const todoPage = new TodoPage(page);
    await todoPage.goto();      // setup
    await use(todoPage);        // the test runs here
    // teardown goes here if needed
  },
});
export { expect } from '@playwright/test';
```

```ts
// tests/fixture.spec.ts
import { test } from '../fixtures';

test('uses fixture', async ({ todoPage }) => {
  await todoPage.addTodo('Fixture todo');
  await todoPage.expectCount(1);
});
```

### Step 9: Data-Driven Tests

```ts
import { test, expect } from '@playwright/test';

const todos = ['Milk', 'Bread', 'Eggs'];

for (const item of todos) {
  test(`can add ${item}`, async ({ page }) => {
    await page.goto('/todomvc');
    await page.getByPlaceholder('What needs to be done?').fill(item);
    await page.keyboard.press('Enter');
    await expect(page.getByTestId('todo-title')).toHaveText(item);
  });
}
```

### Step 10: Authentication With storageState

The idea is to log in once, save the session to a file, and reuse it. Replace the URL and field labels below with your own app's login page.

```ts
// tests/auth.setup.ts
import { test as setup, expect } from '@playwright/test';

const authFile = 'playwright/.auth/user.json';

setup('authenticate', async ({ page }) => {
  await page.goto('https://your-app.com/login');
  await page.getByLabel('Email').fill(process.env.USER_EMAIL!);
  await page.getByLabel('Password').fill(process.env.USER_PASSWORD!);
  await page.getByRole('button', { name: 'Sign in' }).click();
  await expect(page).toHaveURL(/dashboard/);
  await page.context().storageState({ path: authFile });
});
```

Register the setup as a project that the browser projects depend on:

```ts
// in playwright.config.ts → projects
{ name: 'setup', testMatch: /.*\.setup\.ts/ },
{
  name: 'chromium',
  use: { ...devices['Desktop Chrome'], storageState: 'playwright/.auth/user.json' },
  dependencies: ['setup'],
},
```

> ⚠️ Add `playwright/.auth` to `.gitignore` so the saved session never gets committed.

### Step 11: Network Mocking and Interception

```ts
import { test, expect } from '@playwright/test';

test('mock API response', async ({ page }) => {
  await page.route('**/api/users', async route => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify([{ id: 1, name: 'Mocked User' }]),
    });
  });

  // Block images to speed up the test
  await page.route('**/*.{png,jpg,jpeg}', route => route.abort());

  await page.goto('https://your-app.com/users');
  await expect(page.getByText('Mocked User')).toBeVisible();
});

test('wait for a real response', async ({ page }) => {
  await page.goto('https://your-app.com');
  const responsePromise = page.waitForResponse('**/api/data');
  await page.getByRole('button', { name: 'Load' }).click();
  const response = await responsePromise;
  expect(response.status()).toBe(200);
});
```

### Step 12: API Testing

This example uses JSONPlaceholder, a free fake REST API:

```ts
import { test, expect } from '@playwright/test';

test.describe('API tests', () => {
  test('GET posts', async ({ request }) => {
    const res = await request.get('https://jsonplaceholder.typicode.com/posts/1');
    expect(res.ok()).toBeTruthy();
    const body = await res.json();
    expect(body).toHaveProperty('id', 1);
  });

  test('POST post', async ({ request }) => {
    const res = await request.post('https://jsonplaceholder.typicode.com/posts', {
      data: { title: 'Hello', body: 'World', userId: 1 },
    });
    expect(res.status()).toBe(201);
    expect(await res.json()).toMatchObject({ title: 'Hello' });
  });
});
```

### Step 13: Tabs, Iframes, Dialogs and Downloads

```ts
test('multi-tab, iframe, dialog, download', async ({ page, context }) => {
  // New tab: start waiting before the click that opens it
  const newPagePromise = context.waitForEvent('page');
  await page.getByRole('link', { name: 'Open in new tab' }).click();
  const newPage = await newPagePromise;
  await newPage.waitForLoadState();

  // Iframe
  await page.frameLocator('#payment-frame').getByLabel('Card number').fill('4242...');

  // Alert/confirm dialog: register the handler before triggering it
  page.once('dialog', dialog => dialog.accept());
  await page.getByRole('button', { name: 'Delete' }).click();

  // Download
  const downloadPromise = page.waitForEvent('download');
  await page.getByRole('link', { name: 'Export CSV' }).click();
  const download = await downloadPromise;
  await download.saveAs('downloads/' + download.suggestedFilename());
});
```

### Step 14: Advanced Assertions

```ts
// Soft assertions: the test keeps going after a failure
await expect.soft(page.getByTestId('status')).toHaveText('Active');
await expect.soft(page.getByTestId('count')).toHaveText('5');

// Poll a non-UI value until it matches
await expect.poll(async () => {
  const res = await page.request.get('/api/job/42');
  return (await res.json()).status;
}, { timeout: 30_000 }).toBe('done');

// Retry a whole block until it passes
await expect(async () => {
  const res = await page.request.get('/api/health');
  expect(res.status()).toBe(200);
}).toPass({ timeout: 15_000 });
```

### Step 15: Visual Regression Testing

```ts
test('visual check', async ({ page }) => {
  await page.goto('/todomvc');
  await expect(page).toHaveScreenshot('todo-home.png', { maxDiffPixels: 100 });
  await expect(page.getByRole('heading')).toHaveScreenshot();
});
```

The first run creates the baseline images. When the UI changes on purpose, update them with:

```bash
npx playwright test --update-snapshots
```

### Step 16: Parallelism, Retries and Sharding

```ts
test.describe.configure({ mode: 'parallel' }); // tests in this file run in parallel
// or mode: 'serial' → run in order, and stop at the first failure

test('flaky-ish test', async ({ page }) => {
  test.info().annotations.push({ type: 'issue', description: 'JIRA-123' });
  test.setTimeout(60_000);
  // ...
});
```

```bash
npx playwright test --workers=4
npx playwright test --shard=1/3       # run on machine 1 of 3
npx playwright test --repeat-each=5   # hunt for flakiness
```

### Step 17: Debugging and Tracing

```bash
npx playwright test --ui                          # best for local debugging
npx playwright test --debug                       # inspector with step-over
npx playwright show-trace test-results/.../trace.zip
```

Inside a test, `await page.pause();` stops execution and opens the inspector at that point.

### Step 18: Environment Variables

Install dotenv:

```bash
npm i -D dotenv
```

Load it at the top of your config:

```ts
// top of playwright.config.ts
import dotenv from 'dotenv';
dotenv.config({ path: `.env.${process.env.ENV || 'qa'}` });
// use: { baseURL: process.env.BASE_URL }
```

Run against a specific environment:

```bash
ENV=staging npx playwright test
```

### Step 19: CI With GitHub Actions

```yaml
# .github/workflows/playwright.yml
name: Playwright Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        shard: [1, 2, 3]
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with: { node-version: 20 }
      - run: npm ci
      - run: npx playwright install --with-deps
      - run: npx playwright test --shard=${{ matrix.shard }}/3
      - uses: actions/upload-artifact@v4
        if: always()
        with:
          name: report-${{ matrix.shard }}
          path: playwright-report/
```

---

## Recommended Project Structure

```
pw-framework/
├── tests/
│   ├── auth.setup.ts
│   ├── ui/
│   └── api/
├── pages/            # Page Objects
├── fixtures.ts       # custom fixtures
├── test-data/        # JSON/CSV data
├── utils/            # helpers
├── .env.qa / .env.staging
├── playwright.config.ts
└── .github/workflows/
```

---

## Interview Tips

- Be ready to explain **why** Playwright tests are less flaky: auto-waiting and web-first assertions.
- Know how you would **structure a framework**: POM, fixtures, a setup project for auth, and environment configs.
- Know how you would **debug a CI failure**: traces, screenshots and videos.
- Interviewers often ask you to write a POM class or a `page.route` mock live, so practise [Step 7](#step-7-page-object-model), [Step 8](#step-8-custom-fixtures) and [Step 11](#step-11-network-mocking-and-interception) until you can write them from memory.

---

## Useful Links

- [Official documentation](https://playwright.dev/docs/intro)
- [Locators guide](https://playwright.dev/docs/locators)
- [Best practices](https://playwright.dev/docs/best-practices)
- [Playwright on GitHub](https://github.com/microsoft/playwright)

---

⭐ If this guide helped you, consider starring the repo!
