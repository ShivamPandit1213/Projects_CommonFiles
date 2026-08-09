## Getting Started

```bash
npm install              # install dependencies (first time only)
npx playwright install   # download browsers (first time only)
npx playwright test      # run all tests
```

> **Important:** run these from the project root, not your home folder.
> On Windows you'll see `Error: EPERM: operation not permitted, scandir '...\Temp\WinSAT'`
> if you're in `C:\Users\<you>`. Navigate to the project first:
>
> ```bash
> cd C:\Users\shiva\OneDrive\JavaSelenium\PAIMANA_Playwright_1.1
> ```
>
> You're in the right place when `dir` (or `ls`) shows `package.json` and `playwright.config.ts`.

## Running Tests

| Command | What it does |
|---|---|
| `npx playwright test` | Run everything across all browsers |
| `npx playwright test tests/home.spec.ts` | Run a single file |
| `npx playwright test -g "login"` | Run tests matching a title |
| `npx playwright test --grep @smoke` | Run tests by tag |
| `npx playwright test --project=chromium` | Single browser (faster) |
| `npx playwright test --headed` | Watch the browser as it runs |
| `npx playwright test --ui` | Interactive UI mode |
| `npx playwright test --debug` | Step through with the inspector |
| `npx playwright show-report` | Open the HTML report from the last run |

## Project Structure

```
fixtures/       Custom test fixtures (page-object injection)
pages/          Page Object Model classes
test-data/      Static test data (users.json)
tests/          Spec files — the actual tests
utils/          Shared helpers
playwright.config.ts   Browsers, baseURL, reporters, timeouts
```
