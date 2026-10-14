# PlaywrightProjectName — Playwright command reference

| | |
|---|---|
| **Stack** | Node 20+ · `@playwright/test` 1.63 · JavaScript (ES modules) |
| **Tests** | 3 tests × 3 browsers (chromium, firefox, webkit) = 9 runs |
| **Folder** | `C:\Users\shiva\OneDrive\JavaSelenium\PlaywrightProjectName` |
| **Repo** | https://github.com/ShivamPandit1213/PlaywrightProjectName |
| **Portal** | https://iigdev.uatnegd.online (baseURL; override with `BASE_URL`) |
| **Updated** | 2026-10-14 |

Each row is a command and **when** to reach for it. Everything runs in PowerShell from the project folder unless stated. Project history (what changed when) lives in `ProjectInfo.html`, not here.

## Contents

- [0. First check: are you in the right folder?](#0-first-check-are-you-in-the-right-folder)
- [1. Setup: first time, fresh clone, upgrade](#1-setup-first-time-fresh-clone-upgrade)
- [2. Run](#2-run)
- [3. Headless or headed](#3-headless-or-headed)
- [4. Another environment](#4-another-environment)
- [5. Filter: run less](#5-filter-run-less)
- [6. Debug a failing test](#6-debug-a-failing-test)
- [7. Where a failed run leaves evidence](#7-where-a-failed-run-leaves-evidence)
- [8. Record and explore](#8-record-and-explore)
  - [Record a new test, step by step](#record-a-new-test-step-by-step)
  - [Record as a logged-in user](#record-as-a-logged-in-user)
  - [Continue recording from inside a test](#continue-recording-from-inside-a-test)
  - [Codegen options](#codegen-options)
  - [Explore without recording](#explore-without-recording)
- [9. Reports, snapshots and CI-style runs](#9-reports-snapshots-and-ci-style-runs)
- [10. Upgrade and clean up](#10-upgrade-and-clean-up)
- [11. Git and CI](#11-git-and-ci)
  - [Everyday](#everyday)
  - [Push: which command, when](#push-which-command-when)
  - [First push to a new repository](#first-push-to-a-new-repository)
  - [Remote](#remote)
  - [CI](#ci)
- [12. Troubleshooting](#12-troubleshooting)
  - [Diagnosis order](#diagnosis-order)
  - [Environment](#environment)
  - [Configuration](#configuration)
  - [The run](#the-run)
  - [Recording](#recording)
  - [Git](#git)
- [A. Optional shortcuts](#a-optional-shortcuts)
- [B. Getting help on any command](#b-getting-help-on-any-command)

---

## 0. First check: are you in the right folder?

| Command | What it does / when to use it |
|---|---|
| `cd C:\Users\shiva\OneDrive\JavaSelenium\PlaywrightProjectName` | Every command assumes this folder. From `C:\Users\shiva` you get `EPERM: operation not permitted, scandir ...\Temp\WinSAT`, because Playwright starts scanning your whole profile for tests. |
| `dir` | You are in the right place when it lists `package.json` and `playwright.config.js`. |
| `npx playwright --version` | Prints `Version 1.63.0`. If it errors, dependencies are missing → section 1. |

[↑ Contents](#contents)

## 1. Setup: first time, fresh clone, upgrade

| Command | What it does / when to use it |
|---|---|
| `npm ci` | Installs exactly what `package-lock.json` says. Use after cloning, after switching branches, or whenever `node_modules` looks broken. Fails if `package.json` and the lock file disagree — then run `npm install` once and commit the updated lock file. |
| `npm install` | Resolves versions afresh and rewrites `package-lock.json`. Use only when you change dependencies on purpose (`npm install -D <package>`). |
| `npx playwright install` | Downloads the browser builds that match the installed `@playwright/test` version into `C:\Users\<you>\AppData\Local\ms-playwright`. Once per machine, and again after **every** Playwright upgrade. Prints nothing when the browsers are already there. |
| `npx playwright install chromium` | Chromium only — quicker when firefox/webkit are never run locally. |
| `npx playwright install chrome`<br>`npx playwright install msedge` | Branded Google Chrome / Microsoft Edge, for `--channel=chrome` / `--channel=msedge`. Only needed if you test against the real branded browser. |
| `npx playwright install --with-deps` | Linux / CI only: also installs OS libraries. Not needed on Windows. |
| `npx playwright install --only-shell` | Chromium headless shell only, no full browser. Smaller download; CI use. |
| `npx playwright install --list` | Shows which browser builds are present on this machine. |
| `npx playwright install --dry-run` | Shows what would be downloaded and where, without downloading. |
| `npx playwright install --force` | Re-download even if present. Use when a browser folder is corrupted. |

[↑ Contents](#contents)

## 2. Run

| Command | What it does / when to use it |
|---|---|
| `npm test` | = `npx playwright test`. The everyday run: 3 tests × 3 browsers = 9. Window or no window follows `DEFAULT_HEADLESS` in `playwright.config.js`. |
| `npm run test:headed` | Same run, browser windows always visible (Chromium maximized). Use it to watch what a test actually does. |
| `npm run test:headless` | Same run, no windows. Fastest; use before a commit. |
| `npm run test:chromium` | One browser only. The quickest feedback while writing a test. |
| `npm run report` | Opens `playwright-report/index.html` from the last run, with the attached full-page screenshot and any traces. |
| `npx playwright test --project=firefox`<br>`npx playwright test --project=webkit`<br>`npx playwright test --project=chromium --project=firefox` | Pick browsers by hand. webkit is Safari's engine. |
| `npx playwright test --workers=1` | One test at a time. Use when parallel runs interfere with each other or the laptop struggles with several browsers at once. |
| `npx playwright test --workers=50%` | Half the CPU cores. A middle ground on a busy laptop. |
| `npx playwright test --fully-parallel` | Also run tests **inside** one file in parallel (default: files in parallel, tests in a file one after another). |
| `npx playwright test --retries=2` | Retry failures. Separates flaky from broken: a test that passes on retry is reported as *flaky*, not *failed*. |
| `npx playwright test --repeat-each=5` | The opposite check: run every test 5 times to prove a green test is stable before trusting it. |
| `npx playwright test --last-failed` | Re-run only the tests that failed in the previous run. |
| `npx playwright test --only-changed` | Only test files changed since the last commit (uncommitted work). `--only-changed=main` compares against a branch instead. |
| `npx playwright test -x` | Stop at the first failure. Same as `--max-failures=1`. |
| `npx playwright test --max-failures=3` | Stop after 3 failures — saves time when the portal is down and everything will fail. |
| `npx playwright test --global-timeout=600000` | Abort the whole run after 10 minutes (milliseconds). |
| `npx playwright test -c other.config.js` | Use a different config file (`--config`). |

Flags stack, e.g. `npx playwright test tests/home.spec.js --project=chromium --headed`

[↑ Contents](#contents)

## 3. Headless or headed

Priority, highest first (`resolveHeadless()` in `playwright.config.js`):

1. **`CI` is set** (GitHub Actions) → always headless, there is no screen.
2. **`HEADLESS` env var** → `true` / `false`, any case; anything else stops the run with `HEADLESS must be 'true' or 'false'`.
3. **`DEFAULT_HEADLESS`** → constant at the top of the config (currently `false` = windows visible).

| Command | What it does / when to use it |
|---|---|
| `$env:HEADLESS='true'`<br>`$env:HEADLESS='false'` | Applies to this PowerShell window until it is closed or removed. The npm scripts `test:headed` / `test:headless` set it for one run only. |
| `$env:HEADLESS` | Print the current value. Blank = not set. |
| `Remove-Item Env:HEADLESS` | Back to `DEFAULT_HEADLESS`. Run this when tests unexpectedly show or hide windows — a value left over from earlier is the usual reason. |
| `npx playwright test --headed` | Playwright's own flag: forces windows for one run regardless of the config. Fine for a quick look; prefer `npm run test:headed`, which also gives Chromium a maximized window. |

[↑ Contents](#contents)

## 4. Another environment

| Command | What it does / when to use it |
|---|---|
| `$env:BASE_URL='https://other-env.example'; npm test` | Point the same tests at another portal. |
| `$env:BASE_URL` | Check the current value. |
| `Remove-Item Env:BASE_URL` | Back to the dev portal. |

[↑ Contents](#contents)

## 5. Filter: run less

| Command | What it does / when to use it |
|---|---|
| `npx playwright test tests/home.spec.js` | One file. |
| `npx playwright test tests/home.spec.js:12` | One test, by the line number of its `test(...)` call. |
| `npx playwright test home` | Every file whose path contains `home` (it's a pattern, not an exact name). |
| `npx playwright test -g "home page loads"` | By test title (substring, case-insensitive). |
| `npx playwright test --grep @smoke` | By tag. A tag exists only if a test declares it: `test('home page loads', { tag: '@smoke' }, async ({ page }) => ...)` |
| `npx playwright test --grep "@smoke|@login"` | Either tag (regular expression `|` = or). Keep the quotes in PowerShell. |
| `npx playwright test --grep-invert @slow` | Everything except a tag. |
| `npx playwright test --list` | Show which tests **would** run, without running them. First thing to try when a filter matches nothing or you see `No tests found`. |
| `npx playwright test --pass-with-no-tests` | Exit successfully when a filter matches nothing (default is to fail). Useful in scripts. |

[↑ Contents](#contents)

## 6. Debug a failing test

| Command | What it does / when to use it |
|---|---|
| `npm run test:chromium -- tests/home.spec.js --headed` | Narrow it down: one browser, one file, visible. The `--` passes the rest of the line through npm to Playwright. |
| `npx playwright test --debug` | Playwright Inspector: pause before every action, step through, see the locator being used. Best for "why does it not click / find it". |
| `npx playwright test tests/home.spec.js:12 --debug` | Debug just one test, by line number. Much faster than `--debug` on the whole suite. |
| `npx playwright test --ui` | UI mode: pick tests, watch them, time-travel through each step with the DOM and network. Best while writing a new test. |
| `npx playwright test --trace on` | Record a trace for every test (the config records one only on the first retry). |
| `npx playwright test --trace retain-on-failure` | Record for every test but keep the trace only for failures. Good default when hunting an intermittent failure. |
| `npx playwright show-trace test-results/<test-folder>/trace.zip`<br>`npx playwright show-trace` | Trace viewer (with a file picker when no path is given). A trace has every action, network call, console line and a screenshot per step: the most complete record of a failure, and shareable as one file. You can also drop a `trace.zip` onto https://trace.playwright.dev. |
| `npx playwright test --timeout=120000` | Give slow pages more time (milliseconds) for one run instead of editing the config. |
| `npx playwright test --reporter=line` | Compact one-line output. Useful in a narrow terminal or when pasting a log somewhere. |
| `$env:DEBUG='pw:api'; npx playwright test --project=chromium` | Verbose log of every Playwright call. Use when a run hangs and you need to see the last thing it did. `Remove-Item Env:DEBUG` afterwards. |
| `$env:PWDEBUG='console'; npx playwright test --project=chromium --headed` | Adds a `playwright` object to the browser DevTools console: try `playwright.$('text=Login')` or `playwright.inspect(...)` live on the page. `Remove-Item Env:PWDEBUG` afterwards. |

[↑ Contents](#contents)

## 7. Where a failed run leaves evidence

| Location | What is there |
|---|---|
| `test-results/<test-name>/test-failed-1.png` | Screenshot at the moment of failure. |
| `test-results/<test-name>/error-context.md` | The page's accessibility tree at that moment (when present) — read it to find the right locator. |
| `test-results/<test-name>/trace.zip` | On a retry, or always with `--trace on`. |
| `playwright-report/index.html` | The HTML report (`npm run report`). Attachments such as the full-page screenshot from `home.spec.js` sit under each test. |

Both folders are rewritten on every run and are git-ignored. Copy a file out before re-running if you want to keep it.

[↑ Contents](#contents)

## 8. Record and explore

Codegen opens two windows: a **browser** where every click and fill is recorded, and the **Playwright Inspector** showing the generated code live. The generated code is raw material for `pages/` and `tests/`, not a finished test.

### Record a new test, step by step

| # | Command / action | What it does / when to use it |
|---|---|---|
| 1 | `npx playwright install chromium` | Codegen uses Chromium by default. Skip if section 1 is already done. |
| 2 | `npx playwright codegen https://iigdev.uatnegd.online/home` | Start recording on the portal. Add `--ignore-https-errors` if the UAT certificate is rejected. |
| 3 | Click, type, select in the browser | Each action appears in the Inspector as a line of code. Act slowly: wait for each page to load before the next click. |
| 4 | Browser toolbar → **Assert visibility / Assert text / Assert value / Assert snapshot** | Click the assertion, then the element. This is how a recording gets `expect(...)` lines — without them it only proves nothing crashed. |
| 5 | Inspector → **Pick locator**, hover an element | Shows the locator Playwright would use (`getByRole`, `getByLabel`, `getByText`…). Copy it straight into a page object. |
| 6 | Inspector → **Record** button | Pause/resume recording, e.g. to scroll around without generating noise. |
| 7 | Inspector → **Target** dropdown | Switch output language. Keep **Test Runner (JavaScript)** for this project (= `--target=playwright-test`). |
| 8 | Copy the code, **or** start with `npx playwright codegen -o tests/new-flow.spec.js https://iigdev.uatnegd.online/home` | `-o` writes the code to a file as you record. The file is overwritten on every codegen run with the same `-o`. |
| 9 | Close the browser window | Ends the session (and writes `--save-storage` / `--save-har` files, if used). |
| 10 | Clean up the code | Move locators into a page object in `pages/`; replace the full URL with `page.goto('/home')` (uses `baseURL`); credentials from `process.env`, never hard-coded; add a tag (`{ tag: '@smoke' }`); delete redundant clicks. |
| 11 | `npm run test:chromium -- tests/new-flow.spec.js --headed` | Watch it run once in one browser. |
| 12 | `npx playwright test tests/new-flow.spec.js --repeat-each=5` | Prove it is stable before committing, then run it in all 3 browsers with `npm test`. |

### Record as a logged-in user

Log in once, save the session, reuse it so later recordings start on the dashboard.

| # | Command / action | What it does / when to use it |
|---|---|---|
| 1 | Add `/playwright/.auth/` to `.gitignore` | The saved file holds live cookies/tokens. **Never commit it.** Check with `git status --short` — it must not appear. |
| 2 | `npx playwright codegen --save-storage=playwright/.auth/cpse.json https://iigdev.uatnegd.online/home` | Record with saving switched on. |
| 3 | Log in manually with **test** credentials, wait for the landing page (e.g. Dashboard) | The session is captured from whatever state the browser is in at the end. |
| 4 | **Close the browser window** | `cpse.json` is written only on close. Ctrl+C in PowerShell instead of closing can leave it missing or empty. |
| 5 | `dir playwright\.auth` | Confirm `cpse.json` exists and is not 0 bytes. |
| 6 | `npx playwright codegen --load-storage=playwright/.auth/cpse.json https://iigdev.uatnegd.online/home` | Record further flows already logged in. |
| 7 | In a spec: `test.use({ storageState: 'playwright/.auth/cpse.json' });` | Tests in that file start logged in too. Or set `storageState` under `use:` for a project in `playwright.config.js`. |
| 8 | Repeat step 2 when recordings land on the login page again | Sessions expire. For a permanent fix, log in from a setup project (`tests/auth.setup.js` + `dependencies: ['setup']` in the config) so every run creates a fresh session. |

To make one spec ignore a saved session (e.g. login-page tests): `test.use({ storageState: { cookies: [], origins: [] } });`

### Continue recording from inside a test

| Command / action | What it does / when to use it |
|---|---|
| Add `await page.pause();` in the test where recording should start | Stops the test there and opens the Inspector. |
| `npx playwright test tests/home.spec.js --project=chromium --headed` | Runs up to `page.pause()`; then press **Record** in the Inspector and continue by hand. The test's own login/navigation does the setup for you. |
| Remove `page.pause()` before committing | Otherwise headless and CI runs hang until timeout. |

VS Code users: the *Playwright Test for VS Code* extension has **Record new**, **Record at cursor** and **Pick locator** in the Testing sidebar — same codegen, but code lands directly in the open file.

### Codegen options

`npx playwright codegen [options] [url]` — every option below also works with `npx playwright open`.

> **PowerShell:** quote any value containing a comma or space (`"1920,1080"`, `"iPhone 14"`). Unquoted, PowerShell treats the comma as an array separator and Playwright receives the wrong value. Write long commands on one line, or end lines with a backtick `` ` `` (not `\`).

**Output**

| Command | What it does / when to use it |
|---|---|
| `npx playwright codegen -o tests/login.spec.js <url>` | Write the code to a file (`--output`). |
| `npx playwright codegen --target=playwright-test <url>` | Language. Default for this project. Others: `javascript` (plain script, no test runner), `python`, `python-async`, `python-pytest`, `csharp`, `csharp-mstest`, `csharp-nunit`, `java`, `java-junit`. |
| `npx playwright codegen --test-id-attribute=data-qa <url>` | Which attribute `getByTestId` uses, if the portal uses `data-qa` / `data-test` instead of `data-testid`. |

**Browser**

| Command | What it does / when to use it |
|---|---|
| `npx playwright codegen -b firefox <url>` | Engine: `chromium` (`cr`), `firefox` (`ff`), `webkit` (`wk`). |
| `npx playwright codegen --channel=msedge <url>` | Branded browser: `chrome`, `msedge`, `chrome-beta`, `msedge-dev` … (install it first, section 1). |
| `npx playwright codegen --user-data-dir=.\.codegen-profile <url>` | Persistent profile: cookies, extensions and history survive between codegen runs. Git-ignore the folder. |
| `npx playwright codegen --timeout=10000 <url>` | Action timeout in milliseconds. |

**Session and network**

| Command | What it does / when to use it |
|---|---|
| `npx playwright codegen --save-storage=playwright/.auth/cpse.json <url>` | Save cookies + localStorage when the window closes. |
| `npx playwright codegen --load-storage=playwright/.auth/cpse.json <url>` | Start with a saved session. |
| `npx playwright codegen --save-har=network.har <url>` | Record all network traffic to a HAR file (for mocking with `page.routeFromHAR`). |
| `npx playwright codegen --save-har=api.har --save-har-glob="**/api/**" <url>` | Record only matching URLs. |
| `npx playwright codegen --ignore-https-errors <url>` | Accept self-signed / invalid certificates (common on UAT). |
| `npx playwright codegen --proxy-server=http://proxy:3128 <url>` | Route through a proxy. `--proxy-bypass=".internal.local"` for exceptions. |
| `npx playwright codegen --block-service-workers <url>` | Disable service workers, so network requests are visible/recordable. |

**Emulation**

| Command | What it does / when to use it |
|---|---|
| `npx playwright codegen --device="iPhone 14" <url>` | Mobile size, touch and user agent. Device names: `node -e "console.log(Object.keys(require('playwright-core').devices).join('\n'))"` |
| `npx playwright codegen --viewport-size="1920,1080" <url>` | Exact window size. |
| `npx playwright codegen --color-scheme=dark <url>` | `light` or `dark`. |
| `npx playwright codegen --geolocation="28.6139,77.2090" <url>` | Location (New Delhi here); grants the permission automatically. |
| `npx playwright codegen --lang=en-IN <url>` | Browser locale. |
| `npx playwright codegen --timezone="Asia/Kolkata" <url>` | Time zone. |
| `npx playwright codegen --user-agent="MyAgent/1.0" <url>` | Custom user agent. |

**Combined example (one line in PowerShell):**

```powershell
npx playwright codegen --load-storage=playwright/.auth/cpse.json --ignore-https-errors --viewport-size="1920,1080" --timezone="Asia/Kolkata" --lang=en-IN -o tests/new-flow.spec.js https://iigdev.uatnegd.online/home
```

### Explore without recording

| Command | What it does / when to use it |
|---|---|
| `npx playwright open https://iigdev.uatnegd.online/home` | A browser with the Playwright inspector attached and no code generation — for exploring a page and trying locators. |
| `npx playwright open --load-storage=playwright/.auth/cpse.json https://iigdev.uatnegd.online/home` | Explore as the logged-in user. |
| `npx playwright screenshot https://iigdev.uatnegd.online/home home.png` | Quick screenshot from the command line. Add `--full-page`, `--wait-for-timeout=3000`, `--wait-for-selector="h1"` or `--device="iPhone 14"` as needed. |
| `npx playwright pdf https://iigdev.uatnegd.online/home home.pdf` | Save the page as PDF (Chromium only). |

[↑ Contents](#contents)

## 9. Reports, snapshots and CI-style runs

| Command | What it does / when to use it |
|---|---|
| `npx playwright test --reporter=list` | One line per test (default locally). |
| `npx playwright test --reporter=dot` | One character per test. Minimal. |
| `npx playwright test --reporter=html` | HTML report only. |
| `npx playwright test --reporter=json > results.json` | Machine-readable results. |
| `npx playwright test --reporter=junit > results.xml` | JUnit XML for Jenkins / Azure DevOps. |
| `npx playwright test --reporter=list,html` | Several reporters in one run. |
| `npx playwright show-report` | Same as `npm run report`. |
| `npx playwright show-report playwright-report --port 9323` | Pick the port (if 9323 is busy). Add `--host 0.0.0.0` to open it from another device on the network. |
| `npx playwright test --shard=1/3` | Run a third of the suite. Used to split CI across several machines (`2/3`, `3/3` on the others). |
| `npx playwright test --reporter=blob` then `npx playwright merge-reports --reporter=html ./blob-report` | Combine sharded results into one HTML report. |
| `npx playwright test --forbid-only` | Fail if a `test.only` is present — what CI does. Run it locally before pushing. |
| `npx playwright test --fail-on-flaky-tests` | Treat "passed on retry" as a failure. |
| `npx playwright test --output=results` | Write evidence to `results/` instead of `test-results/`. |
| `npx playwright test --quiet` | Hide the tests' own `console.log` output. |
| `npx playwright test --no-deps` | Skip dependency projects (e.g. an auth `setup` project) — only if you add one. |
| `npx playwright test --update-snapshots` | (`-u`) Rewrite screenshot baselines that changed — only after checking the change is intended. `=all`, `=changed`, `=missing`, `=none` control which. |
| `npx playwright test --ignore-snapshots` | Skip screenshot comparisons for one run. |

[↑ Contents](#contents)

## 10. Upgrade and clean up

| Command | What it does / when to use it |
|---|---|
| `npm outdated` | Which packages have newer versions. |
| `npm install -D @playwright/test@latest`<br>`npx playwright install` | Upgrade Playwright. **Always** re-run `install` afterwards: the browser builds are tied to the package version. |
| `npm ls @playwright/test` | Must show exactly one version. Two copies cause `did not expect test() to be called here`. |
| `npx playwright clear-cache` | Clears Playwright's compile/test cache (**not** the browsers). Try it when an edit to a test or the config seems to be ignored. |
| `npx playwright uninstall` | Removes the downloaded browsers for this version (about 1 GB). Not the package. |
| `npx playwright uninstall --all` | Removes browsers for **every** Playwright version on the machine — frees space left by old upgrades. |
| `Remove-Item -Recurse -Force node_modules; npm ci` | The reset for a broken `node_modules`. |
| `Remove-Item -Recurse -Force test-results, playwright-report` | Wipe old evidence and reports. |

[↑ Contents](#contents)

## 11. Git and CI

### Everyday

| Command | What it does / when to use it |
|---|---|
| `git status --short` | What changed: `M` modified, `D` deleted, `??` new. Long form: `git status`. |
| `git add -A` | Stage everything, including deletions. `git add .` does the same from the project root. **Not** `git add ..` — two dots is the parent folder, which is outside the repository. |
| `git commit -m "message"` | Record it. A push uploads **commits**, never loose changes — uncommitted work stays local however often you push. |
| `git push` | Upload the committed work — see [Push](#push-which-command-when) for which form to use. |
| `git pull` | Bring down changes from GitHub. |
| `git log --oneline -5` | Last five commits — is today's work in one? |
| `git diff` | Unstaged changes, line by line. |
| `git restore <file>` | Throw away local changes to one file. |
| `git check-ignore -v playwright/.auth/cpse.json` | Confirms the session file is ignored (prints the `.gitignore` rule). No output = it would be committed — fix `.gitignore` first. |

### Push: which command, when

| Command | When | What happens |
|---|---|---|
| `git push` | Every day, once the branch is linked to GitHub (after its first `-u` push). | Uploads the new commits of the current branch to the branch it tracks. On a branch that is **not** linked yet it stops with `fatal: The current branch main has no upstream branch` and uploads nothing — even right after a successful commit. |
| `git push -u origin main` | The **first** push of a branch: a brand-new repository, or a new local branch GitHub has never seen. | Uploads the branch **and** links it (`-u` = `--set-upstream`), so plain `git push` and `git pull` work from then on. Confirms with `branch 'main' set up to track 'origin/main'`. Once per branch, not once per repository. |
| `git push --set-upstream origin main` | Same situation — this is the long spelling Git prints in the error message. | Identical to `-u`. |
| `git push origin main` | A one-off push you don't want to link, e.g. a throwaway branch. | Uploads, but the branch stays unlinked, so plain `git push` keeps failing. Prefer `-u` once. |
| `git push --dry-run` | Before a push you are unsure about. | Shows what would be uploaded; uploads nothing. |
| `git branch -vv` | To know in advance whether plain `git push` will work. | Lists local branches with the remote branch each one tracks, e.g. `main ... [origin/main]`. No bracket = not linked → use `-u`. |
| `git config --global push.autoSetupRemote true` | Once on this PC, if you'd rather never type `-u`. | Plain `git push` links a new branch automatically (the setting Git itself suggests in the error). |
| `git push --force-with-lease` | Only after you rewrote commits that were already pushed (`git commit --amend`, a rebase), and only on **your own** branch. | Replaces the remote branch with yours, but refuses if someone else pushed in between. Never on a shared `main`; never plain `--force`. |

A browser sign-in window may open on the first push to a repository: use the account that owns it. Every push to `main` also starts the GitHub Actions run (see [CI](#ci)).

### First push to a new repository

In this order:

1. `git remote -v` — connected? Must print `origin` → the repo URL.
2. `git status --short` — anything listed here is **not** going up yet.
3. `git add -A` — stage it all. (Expect `LF will be replaced by CRLF` warnings on Windows; they are harmless — see [Git troubleshooting](#git).)
4. `git commit -m "message"` — commit it. The summary line (`20 files changed, 652 insertions(+), 229 deletions(-)`) is your check that everything was included.
5. `git push -u origin main` — upload and link local `main` to GitHub `main`. From now on plain `git push` is enough.

### Remote

The GitHub address this folder pushes to. The repository is https://github.com/ShivamPandit1213/PlaywrightProjectName.

| Command | What it does | Fails when |
|---|---|---|
| `git remote -v` | Show it. Empty output = no remote at all. | — |
| `git remote add origin <url>` | Connect. | `origin` already exists |
| `git remote set-url origin <url>` | Change. | there is no `origin` |
| `git remote remove origin` | Disconnect. | there is no `origin` |

`remove` and `set-url` print **nothing** on success. After any of them run `git remote -v` instead of repeating the command: a second `remove`, or a `set-url` after a `remove`, only says `No such remote 'origin'`. Names are exact — `origine` is not `origin`; Tab completes them.

### CI

`.github/workflows/playwright.yml` runs on every push and pull request to `main`: `npm ci`, install browsers, `npx playwright test`, upload `playwright-report/`.

- `CI=true` there, so it is always headless, 1 worker, 2 retries, and a committed `test.only` fails the build (`forbidOnly`).
- Results: repository → **Actions** tab → the run → `playwright-report` artifact (download, unzip, open `index.html`).
- Fails on GitHub but passes here? First suspect: GitHub's servers cannot reach `iigdev.uatnegd.online` (private network / VPN).
- Tests that need a login: `playwright/.auth/cpse.json` is not in the repository, so CI has no session. Store credentials as repository **Secrets** (Settings → Secrets and variables → Actions) and log in from a setup project instead.

[↑ Contents](#contents)

## 12. Troubleshooting

### Diagnosis order

When anything fails, in this order:

1. `dir` — right folder? (`package.json` listed)
2. `npx playwright --version` — package installed?
3. `npx playwright install` — browsers present for this version?
4. `npx playwright test --list` — does the path / filter match?
5. `npm run test:chromium -- tests/home.spec.js --headed` — watch one test, then read `test-results/`

### Environment

| Symptom | Cause | Fix |
|---|---|---|
| `EPERM: operation not permitted, scandir ...\Temp\WinSAT` | Ran from the home folder. | `cd` to the project folder. |
| `npm.ps1 cannot be loaded because running scripts is disabled` | PowerShell execution policy blocks npm. | Once, as your user: `Set-ExecutionPolicy -Scope CurrentUser RemoteSigned` |
| `'playwright' is not recognized` / `Cannot find module '@playwright/test'` | `node_modules` missing or incomplete. | `npm ci` |
| `Executable doesn't exist at ...\ms-playwright\chromium-xxxx\...` / `Looks like Playwright Test or Playwright was just installed or updated` | Browsers not downloaded for this package version (fresh machine or just upgraded). | `npx playwright install` |
| `npm ci`: `package.json and package-lock.json are not in sync` | The lock file drifted (`package.json` edited by hand?). | `npm install` once, then commit `package-lock.json`. |
| `EBUSY: resource busy or locked` | OneDrive is syncing `test-results/` while Playwright writes to it. | Pause OneDrive sync and re-run, or move the project out of OneDrive. |

### Configuration

| Symptom | Cause | Fix |
|---|---|---|
| `HEADLESS must be 'true' or 'false', got 'yes'` | Typo in the variable. | `$env:HEADLESS='true'` or `Remove-Item Env:HEADLESS` |
| `Cannot use import statement outside a module` | `package.json` lost `"type": "module"`. | Put it back. |
| `deviceScaleFactor option is not supported with null viewport` | Chromium project config: with `viewport: null`, `deviceScaleFactor` must be left undefined. | Restore `deviceScaleFactor: undefined`. |
| `Playwright Test did not expect test() to be called here` | Two copies of `@playwright/test`, or a spec importing `test` from both `@playwright/test` and a fixtures file. | Import from one place; `npm ls @playwright/test` must show exactly one version. |

### The run

| Symptom | Cause | Fix |
|---|---|---|
| `Error: No tests found` | Wrong path, wrong filter, or the file is not named `*.spec.js`. | `npx playwright test --list` shows what Playwright can see. |
| `Test timeout of 60000ms exceeded` / `page.goto: Timeout 30000ms exceeded` | Portal slow or down, VPN off, or a locator that never appears. | Open the URL in a normal browser first. Then `--headed` or `--debug`, and read `test-results/<test>/error-context.md` for the real page state. |
| `net::ERR_NAME_NOT_RESOLVED` / `net::ERR_CONNECTION_REFUSED` | Wrong `BASE_URL`, or no network / VPN. | `$env:BASE_URL` shows the value. |
| `net::ERR_CERT_AUTHORITY_INVALID` / `SSL` errors | UAT uses a self-signed certificate. | `ignoreHTTPSErrors: true` under `use:` in the config; `--ignore-https-errors` for codegen. |
| `toHaveURL ... Received: https://.../login` (or another unexpected page) | The portal redirected: session, maintenance page, changed route. | Look at the screenshot before touching the test. |
| `Target page, context or browser has been closed` | The browser crashed, usually out of memory with many in parallel. | `npx playwright test --workers=1` |
| Run hangs forever, no failure | A `page.pause()` left in a test. | Search the tests for `page.pause` and remove it. |
| CI fails on `test.only` / `forbidOnly` but everything passes locally | A `test.only` was committed. | Remove `.only`, commit, push again. |

### Recording

| Symptom | Cause | Fix |
|---|---|---|
| `cpse.json` missing or 0 bytes after `--save-storage` | Codegen was stopped with Ctrl+C, or closed before login finished. | Re-run, finish the login, wait for the landing page, then close the **browser window**. |
| `Error reading storage state from playwright/.auth/cpse.json` / `ENOENT` | File not created yet, wrong path, or not present after a fresh clone (it is git-ignored). | `dir playwright\.auth`; if missing, record it again (section 8). |
| `--load-storage` opens the login page anyway | The saved session expired, or the portal keeps the session in `sessionStorage` (not saved). | Re-record with `--save-storage`. If it expires every time, log in from a setup project instead. |
| Wrong viewport / device / location in codegen | An unquoted comma value in PowerShell was split. | Quote it: `--viewport-size="1920,1080"`. |
| Generated code uses brittle selectors (`#ctl00_...`, long CSS) | The element has no role, label or test id. | Use **Pick locator** on a nearby labelled element, or ask the developers for a `data-testid`. |
| Recorded test passes once, fails next time | Recorded data (unique names, dates) or missing waits on slow pages. | Generate data in the test; replace waits with `expect(...)` assertions; check with `--repeat-each=5`. |

### Git

| Symptom | Cause | Fix |
|---|---|---|
| `fatal: ..: '..' is outside repository` | Typed `git add ..` — two dots is the parent folder. | `git add .` or `git add -A` |
| `warning: in the working copy of '...', LF will be replaced by CRLF the next time Git touches it` | Harmless. Git on Windows converts line endings on checkout (`core.autocrlf=true`); the files were written with LF. | Ignore it, or silence it for good: create `.gitattributes` containing `* text=auto eol=lf`, then `git add --renormalize .` and commit. |
| `fatal: The current branch main has no upstream branch` | First push of this branch — it is not linked to GitHub yet. Committing does not change that. | `git push -u origin main` (once); see [Push](#push-which-command-when). |
| `error: No such remote 'origin'` | There is no remote to change or remove — an earlier `remove` already worked. | `git remote -v` to confirm; if empty: `git remote add origin <url>` |
| `error: remote origin already exists` | Already connected. | `git remote -v` shows where. To point it somewhere else use `git remote set-url origin <url>`, not `add`. |
| Pushed, but GitHub shows the old files / today's work is missing | The changes were never committed; a push uploads commits only. | `git status --short` lists them → `git add -A`, `git commit`, `git push` |
| `Updates were rejected because the remote contains work that you do not have` | The GitHub repo was created with a README or .gitignore, or someone else pushed. | `git pull --rebase origin main`, then `git push` |
| `remote: Permission denied` / 403 on push | Windows saved a different GitHub login. | Control Panel → Credential Manager → Windows Credentials → remove the github.com entry, push again and sign in as the repository owner. |
| `cpse.json` shows up in `git status` | `.gitignore` is missing the auth folder. | Add `/playwright/.auth/` to `.gitignore`. If it was already committed: `git rm --cached playwright/.auth/cpse.json`, commit, and log out / change that test password — the session was exposed. |

[↑ Contents](#contents)

## A. Optional shortcuts

Add to `"scripts"` in `package.json` if not there yet:

```json
"test:smoke":     "playwright test --grep @smoke",
"test:ui":        "playwright test --ui",
"test:debug":     "playwright test --debug --project=chromium",
"test:failed":    "playwright test --last-failed",
"codegen":        "playwright codegen https://iigdev.uatnegd.online/home",
"codegen:login":  "playwright codegen --save-storage=playwright/.auth/cpse.json https://iigdev.uatnegd.online/home",
"codegen:authed": "playwright codegen --load-storage=playwright/.auth/cpse.json https://iigdev.uatnegd.online/home"
```

Then: `npm run test:smoke`, `npm run test:ui`, `npm run test:debug`, `npm run test:failed`, `npm run codegen`, `npm run codegen:login`, `npm run codegen:authed`.

[↑ Contents](#contents)

## B. Getting help on any command

The exact options depend on the installed version; these print the authoritative list for 1.63.

| Command | Shows |
|---|---|
| `npx playwright --help` | All top-level commands (`test`, `codegen`, `open`, `install`, `show-report`, `show-trace`, `merge-reports`, `screenshot`, `pdf`, `clear-cache`, …). |
| `npx playwright test --help` | Every test runner option. |
| `npx playwright codegen --help` | Every codegen option. |
| `npx playwright install --help` | Browser install options and browser names. |

Docs: [Codegen](https://playwright.dev/docs/codegen) · [Command line](https://playwright.dev/docs/test-cli) · [Authentication](https://playwright.dev/docs/auth) · [Trace viewer](https://playwright.dev/docs/trace-viewer)

[↑ Contents](#contents)
