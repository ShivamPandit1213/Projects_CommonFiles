# Jenkins Job Setup Guide: Playwright Freestyle Job, Step by Step

Follow the steps in order, top to bottom. Each step says **where** to go in Jenkins, **what to enter** (with sample data), **why**, and **how to check** it worked. Reference material for the other Jenkins job types is in the appendices at the end.

Line	            Meaning
TZ=Asia/Kolkata	Times are in IST
0 11 * * *	      Every day, including Saturday and Sunday, at 11:00 AM
30 11 * * 1-5	   Monday to Friday at 11:30 AM

---

## Contents

- [How to Read This Guide](#how-to-read-this-guide)
- [Sample Data Used Throughout](#sample-data-used-throughout)
- [Part A: One-Time Jenkins Setup](#part-a-one-time-jenkins-setup) (Steps 1–6)
- [Part B: Prepare the Project](#part-b-prepare-the-project) (Steps 7–8)
- [Part C: Create the Freestyle Job](#part-c-create-the-freestyle-job) (Steps 9–17): **Freestyle job, worked example**
- [Part D: Run and Verify](#part-d-run-and-verify) (Steps 18–20)
- [Part E: Troubleshooting](#part-e-troubleshooting)
- [Appendix A: Choosing a Job Type](#appendix-a-choosing-a-job-type)
- [Appendix B: Freestyle Project (All Fields)](#appendix-b-freestyle-project-all-fields)
- [Appendix C: Pipeline](#appendix-c-pipeline)
- [Appendix D: Multibranch Pipeline](#appendix-d-multibranch-pipeline)
- [Appendix E: Organization Folder](#appendix-e-organization-folder)
- [Appendix F: Multi-configuration Project (Matrix)](#appendix-f-multi-configuration-project-matrix)
- [Appendix G: Folder](#appendix-g-folder)
- [Appendix H: Cron Schedule Cheat Sheet](#appendix-h-cron-schedule-cheat-sheet)
- [Appendix I: The Same Job as a Pipeline](#appendix-i-the-same-job-as-a-pipeline)

---

## How to Read This Guide

Jenkins enforces very few fields when you click **Save**. Many fields become required only once you switch on a related option, and a job saved with nothing configured runs but does nothing useful. Each field is labelled:

| Label | Meaning |
|---|---|
| ✅ **Mandatory** | Jenkins won't save, or the step can't work, without it. |
| ⚠️ **Conditionally mandatory** | Required once you enable its parent option (e.g. ticking *Use SMTP Authentication* makes *User Name* required). |
| 🔶 **Practically required** | Jenkins saves without it, but the job fails or does nothing useful. |
| ⬜ **Optional** | Improves safety, speed or housekeeping. |

Field names match Jenkins **2.568** on Windows. Labels can differ slightly in other versions or with other plugins.

---

## Sample Data Used Throughout

Replace these with your own values. Everything else in the guide uses them.

| Item | Sample value |
|---|---|
| Jenkins URL | `http://localhost:8080/` |
| Jenkins home folder | `C:\Users\shiva\.jenkins` |
| Job name | `Paimana_Dev` |
| GitHub repository | `https://github.com/<owner>/Paimana_Dev.git` |
| Branch | `master` |
| Credential ID | `github-shivam` |
| Project stack | Node 24, `@playwright/test`, JavaScript |
| Report folder | `playwright-report` |
| Parallel workers | `4` |
| Email recipients | `qa-team@example.com` |
| Mail server | `smtp.gmail.com`, port `587`, TLS |
| Portal under test | `https://iigdev.uatnegd.online` |

---

## Part A: One-Time Jenkins Setup

Do these once per Jenkins installation. Every job after that reuses them.

### Step 1: Check the Machine

**Where:** a PowerShell window on the PC that runs Jenkins.

```powershell
java -version                                   # Java 21 for current Jenkins
node -v                                         # v24.x for this project
git --version                                   # any recent version
Test-NetConnection github.com -Port 443         # TcpTestSucceeded : True
```

| Check | Expected | Why |
|---|---|---|
| `java -version` | `21.x` | Jenkins itself runs on Java. |
| `node -v` | `v24.x` | The build step runs `npm`/`npx`; they must be on `PATH` for the Windows user that starts Jenkins. |
| `git --version` | prints a version | The job checks code out with `git.exe`. |
| `Test-NetConnection` | `TcpTestSucceeded : True` | Jenkins must reach GitHub, or checkout fails (see [Part E](#part-e-troubleshooting)). |

### Step 2: Start Jenkins

**Where:** PowerShell or Command Prompt, in the folder containing `jenkins.war`.

```bat
java -Dhudson.model.DirectoryBrowserSupport.CSP="" -jar jenkins.war
```

| Part | Sample value | Status | Why |
|---|---|---|---|
| `-Dhudson.model.DirectoryBrowserSupport.CSP=""` | empty | ⬜ Optional (needed for Playwright reports) | Jenkins normally blocks JavaScript in archived HTML. The Playwright report is a JavaScript app, so without this it shows a **blank page**. Fine for a personal Jenkins; on a shared one see [Part E → Report](#e4-report-problems). |
| `-jar jenkins.war` | — | ✅ Mandatory | Starts Jenkins on port 8080. |

**Run it in your logged-in desktop session**, not as a Windows service. Headed (visible) browser runs need a desktop; a Jenkins service has none and must run tests headless.

> The console line `Running as SYSTEM` in build logs is Jenkins's internal permission identity, not the Windows account. Builds run as the Windows user who started Jenkins.

**Check:** `http://localhost:8080/` opens the Jenkins dashboard.

### Step 3: Install Plugins

**Where:** **Manage Jenkins → Plugins → Available plugins**. Search, tick, then **Install**.

| Plugin | Status | Gives you |
|---|---|---|
| Git | ✅ Mandatory | *Source Code Management → Git* |
| HTML Publisher | ✅ Mandatory | *Post-build → Publish HTML reports* |
| Mailer | 🔶 Practically required | *Post-build → E-mail Notification* and the SMTP settings |
| Timestamper | ⬜ Optional | *Add timestamps to the Console Output* |
| Build Timeout | ⬜ Optional | *Terminate a build if it's stuck* |
| Workspace Cleanup | ⬜ Optional | *Delete workspace before build starts* |
| Credentials Binding | ⬜ Optional | *Use secret text(s) or file(s)* |

**Check:** **Installed plugins** lists them; restart Jenkins if asked.

### Step 4: Add the GitHub Credential

**Where:** **Manage Jenkins → Credentials → System → Global credentials (unrestricted) → Add Credentials**.

First create a token on GitHub: **Settings → Developer settings → Personal access tokens**. A fine-grained token with **Contents: Read-only** on the repository is enough (or a classic token with the `repo` scope).

| Field | Sample value | Status | Why |
|---|---|---|---|
| Kind | Username with password | ✅ Mandatory | GitHub over HTTPS uses a username + token. |
| Scope | Global | ✅ Mandatory | *Global* = jobs can use it. *System* is only for Jenkins internals. |
| Username | your GitHub username | ⚠️ Conditionally mandatory | Account that owns the token. |
| Password | the personal access token | ⚠️ Conditionally mandatory | GitHub rejects your account password for Git; use the token. |
| ID | `github-shivam` | ⬜ Optional (strongly recommended) | Stable name to pick in jobs. Left blank, Jenkins generates a random UUID. |
| Description | `GitHub PAT for paimana repos` | ⬜ Optional | Shown in dropdowns and build logs. |

**Check:** the credential appears in the list with ID `github-shivam`.

### Step 5: Set Up Email

Skip this step if you won't use e-mail notifications. If you add the E-mail Notification action without it, Jenkins tries a mail server on your own PC and every failed build ends with `Couldn't connect to host, port: localhost, 25`.

**Where:** **Manage Jenkins → System**.

**5a. Jenkins Location**

| Field | Sample value | Status | Why |
|---|---|---|---|
| Jenkins URL | `http://localhost:8080/` | ✅ Mandatory | Used for links in emails. |
| System Admin e-mail address | `jenkins@example.com` (or your Gmail address) | 🔶 Practically required | The "From" address. Many mail servers reject mail without one; Gmail requires it to match the login account. |

**5b. E-mail Notification**

| Field | Sample value | Status | Why |
|---|---|---|---|
| SMTP server | `smtp.gmail.com` | ✅ Mandatory | Mail server Jenkins sends through. Blank means `localhost`. |
| Default user e-mail suffix | `@example.com` | ⬜ Optional | Turns Jenkins user names into addresses. |
| Advanced → Use SMTP Authentication | ✅ ticked | ⚠️ Conditionally mandatory | Gmail requires login. |
| ↳ User Name | `yourname@gmail.com` | ⚠️ Conditionally mandatory | The sending account. |
| ↳ Password | 16-character app password | ⚠️ Conditionally mandatory | Gmail rejects your normal password. Create one at **Google Account → Security → App passwords**; that option only appears once **2-Step Verification** is on. |
| Advanced → Use TLS | ✅ ticked | ⚠️ Conditionally mandatory | Encryption Gmail expects on port 587. |
| Advanced → SMTP Port | `587` | ⚠️ Conditionally mandatory | `587` for TLS, `465` for SSL. |
| Test configuration by sending test e-mail → Test e-mail recipient | `qa-team@example.com` | ⬜ Optional (recommended) | Click **Test configuration** to prove it works before a real build fails. |

Click **Save**.

**Check:** the test shows *Email was successfully sent* and the mail arrives.

### Step 6: Allow the Report's JavaScript (if not done in Step 2)

If you started Jenkins without the `-D...CSP=""` option, set it at runtime. It lasts until Jenkins restarts.

**Where:** **Manage Jenkins → Script Console** (`http://localhost:8080/manage/script`).

1. The **Console** box holds a sample line (`println(Jenkins.instance.pluginManager.plugins)`). Click inside, press **Ctrl+A**, then **Delete**.
2. Paste and click **Run**:

   ```groovy
   System.setProperty("hudson.model.DirectoryBrowserSupport.CSP", "")
   println("CSP is now: [" + System.getProperty("hudson.model.DirectoryBrowserSupport.CSP") + "]")
   ```

**Check:** the result reads `CSP is now: []`.

---

## Part B: Prepare the Project

Jenkins builds whatever is in the Git repository, so the project must already handle running on Jenkins.

### Step 7: Make the Project Jenkins-Ready

**Where:** your project folder, e.g. `C:\Users\shiva\OneDrive\JavaSelenium\Paimana_Dev`.

**7a. `package.json` scripts** (needs `cross-env` in devDependencies)

```json
"scripts": {
  "test": "playwright test",
  "test:headed": "cross-env HEADLESS=false playwright test",
  "test:headless": "cross-env HEADLESS=true playwright test"
}
```

| Script | Why |
|---|---|
| `test:headed` | Visible, maximized browser windows, even when Jenkins sets `CI`. Use this instead of Playwright's `--headed` flag (see below). |
| `test:headless` | No windows; use when nobody watches the run or Jenkins runs as a service. |

**7b. `playwright.config.js`: headless and workers switches**

```js
/** Headless or visible windows. Priority: HEADLESS env var > CI > visible. */
function resolveHeadless() {
  const value = process.env.HEADLESS?.trim().toLowerCase();
  if (value === 'true') return true;
  if (value === 'false') return false;
  if (value) throw new Error(`HEADLESS must be 'true' or 'false', got '${process.env.HEADLESS}'`);
  return !!process.env.CI;
}

/**
 * Number of parallel workers. Priority: WORKERS env var > CI (1) > Playwright's default.
 * WORKERS can be a whole number (4) or a percentage of CPU cores (50%).
 */
function resolveWorkers() {
  const value = process.env.WORKERS?.trim();
  if (!value) return process.env.CI ? 1 : undefined;
  if (/^\d+%$/.test(value)) return value; // "50%" stays text
  if (/^\d+$/.test(value)) return Number(value); // "4" becomes the number 4
  throw new Error(
    `WORKERS must be a number like 4 or a percentage like 50%, got '${process.env.WORKERS}'`,
  );
}

export default defineConfig({
  workers: resolveWorkers(),
  retries: process.env.CI ? 2 : 0,
  reporter: [['list'], ['html', { open: 'never' }]], // writes playwright-report/
  use: { headless: resolveHeadless() /* ...rest unchanged */ },
  // ...rest unchanged
});
```

**Why these are needed on Jenkins**

| Problem without it | Cause |
|---|---|
| Tests run one at a time on Jenkins (`Running 7 tests using 1 worker`) | Playwright's default template has `workers: process.env.CI ? 1 : undefined`, and the Jenkins build has `CI` set. |
| `config.workers must be a number or percentage` | Environment variables are always text. Playwright accepts the number `4` or the text `"50%"`, but not the text `"4"`. `resolveWorkers()` converts it. |
| Headed windows too big, running off the screen | With `CI` set the config assumed headless and used a fixed 1920×1080 page, while `--headed` forced windows open anyway. On a 1920×1080 screen at 125% scaling only 1536×816 is usable. `HEADLESS=false` tells the config, so it maximizes windows to the real screen. |

**7c. Test locally, then push**

```powershell
npm ci
npm run test:headless        # all tests should pass
git add -A
git commit -m "Make project Jenkins-ready"
git push origin master
```

**Check:** the commit appears on GitHub.

### Step 8: Know What the Build Will Run

The job in Part C runs exactly this, in the Jenkins workspace (`C:\Users\shiva\.jenkins\workspace\Paimana_Dev`):

```bat
set WORKERS=4
call npm ci
call npx playwright install
call npm run test:headed
```

| Line | Why |
|---|---|
| `set WORKERS=4` | Four tests run in parallel. Read by `resolveWorkers()`. |
| `call npm ci` | Installs the exact versions from `package-lock.json`, including new ones pushed to Git (e.g. `cross-env`). |
| `call npx playwright install` | Makes sure browser builds match the Playwright version. Quick when already cached. |
| `call npm run test:headed` | Runs all tests in visible, maximized windows. |
| `call` on each npm/npx line | `npm` and `npx` are `.cmd` files; without `call`, Windows ends the batch step after the first one. |

**Batch-file rules that bite:** Jenkins saves the step as a `.bat` file.

- A single `%` starts a variable name, so `set WORKERS=50%` arrives as `50`, which means **50 workers**. Write `set WORKERS=50%%` for half the CPU cores.
- `set` keeps trailing spaces: `set WORKERS=4 ` stores `4 ` (the config's `.trim()` copes, but avoid it).

---

## Part C: Create the Freestyle Job

This part creates a **Freestyle project**, the classic job type configured entirely through Jenkins's web form. Every field below uses the sample data; for the full list of Freestyle fields, including ones not used here, see [Appendix B](#appendix-b-freestyle-project-all-fields).

### Step 9: Create the Job

**Where:** Dashboard → **New Item**.

| Field | Sample value | Status | Why |
|---|---|---|---|
| Enter an item name | `Paimana_Dev` | ✅ Mandatory | Job URL (`/job/Paimana_Dev/`) and workspace folder name. Avoid spaces. |
| Item type | Freestyle project | ✅ Mandatory | UI-configured job, no Jenkinsfile. Can't be changed later. |
| Copy from | *(blank)*, or an existing job | ⬜ Optional | Clones another job's settings. |

Click **OK**. The configure page opens with sections **General, Source Code Management, Triggers, Environment, Build Steps, Post-build Actions** in the left menu.

### Step 10: General

| Field | Sample value | Status | Why |
|---|---|---|---|
| Description | `Playwright UI + API tests for PAIMANA dev` | ⬜ Optional | Explains the job to others. |
| Discard old builds | ✅ ticked | ⬜ Optional (recommended) | Stops logs and reports filling the disk. |
| ↳ Strategy → Max # of builds to keep | `20` | ⚠️ Conditionally mandatory (this or *Days to keep*) | Retention rule. |
| This project is parameterized | ☐ unticked (see variant below) | ⬜ Optional | Asks for inputs when you start a build. |
| Execute concurrent builds if necessary | ☐ unticked | ⬜ Optional | Two builds at once would fight over the same workspace and screen. |
| Advanced → Retry Count | `3` | ⬜ Optional (recommended) | Retries the Git checkout after a brief network blip. |

**Variant: choose headed/headless and workers per build.** Tick *This project is parameterized* and add:

| Parameter | Name | Sample value | Why |
|---|---|---|---|
| Choice Parameter | `RUN_MODE` | Choices (one per line): `headed`, `headless` | First choice is the default. |
| String Parameter | `WORKERS` | Default Value `4` | Becomes an environment variable automatically. |

Then in Step 14 use `call npm run test:%RUN_MODE%` and drop the `set WORKERS=4` line. **Build Now** becomes **Build with Parameters**.

### Step 11: Source Code Management

| Field | Sample value | Status | Why |
|---|---|---|---|
| SCM | Git | 🔶 Practically required | The tests live in GitHub. |
| Repository URL | `https://github.com/<owner>/Paimana_Dev.git` | ⚠️ Conditionally mandatory | Where to fetch the code. |
| Credentials | `github-shivam` (from Step 4) | ⚠️ Conditionally mandatory (private repo) | Authenticates the fetch. |
| Branches to build → Branch Specifier | `*/master` | ⚠️ Conditionally mandatory | Must match the real branch; the default `*/master` fails on repos that use `main`. |
| Repository browser | (Auto) | ⬜ Optional | Links commits to GitHub's web UI. |
| Additional Behaviours | *(none)* | ⬜ Optional | e.g. *Clean before checkout*. |

**Check:** no red error appears under Repository URL after you pick the credential. A red *Failed to connect* message means Jenkins can't reach or log in to the repo.

### Step 12: Triggers

All optional. With none, the job runs only when you click **Build Now**.

| Trigger | Sample value | Why |
|---|---|---|
| Poll SCM → Schedule | `H/15 * * * *` | Checks GitHub every ~15 minutes and builds only if there's a new commit. Works with a local Jenkins. |
| Build periodically → Schedule | `H 2 * * 1-5` | Nightly run on weekdays around 2 AM, even without changes. |
| GitHub hook trigger for GITScm polling | ☐ unticked | Needs GitHub to reach your Jenkins over the internet; `localhost` isn't reachable without a tunnel. |
| Trigger builds remotely → Authentication Token | `paimana-run-7f3k` | Lets a script start the build via a URL. |

Cron format: [Appendix H](#appendix-h-cron-schedule-cheat-sheet).

### Step 13: Environment

| Field | Sample value | Status | Why |
|---|---|---|---|
| Delete workspace before build starts | ☐ unticked | ⬜ Optional | Forces a full clone every build. `npm ci` already gives a clean `node_modules`. |
| Use secret text(s) or file(s) | ☐ (later, for login tests) | ⬜ Optional | Injects secrets as masked environment variables. |
| ↳ Bindings → Username and password (separated) | Username Variable `PAIMANA_USERNAME`, Password Variable `PAIMANA_PASSWORD`, Credentials: a stored portal login | ⚠️ Conditionally mandatory | How future login tests get credentials without putting them in Git. |
| Add timestamps to the Console Output | ✅ ticked | ⬜ Optional | Shows which step is slow. |
| Terminate a build if it's stuck | ✅ ticked | ⬜ Optional (recommended) | Stops a run where a browser hangs. |
| ↳ Time-out strategy → Absolute → Timeout minutes | `30` | ⚠️ Conditionally mandatory | Limit for the whole build. |
| ↳ Time-out actions | Abort the build | ⬜ Optional | Default action. |
| With Ant | ☐ unticked | ⬜ Optional | Not used by Node projects. |

### Step 14: Build Steps

**Where:** **Add build step → Execute Windows batch command**.

| Field | Sample value | Status | Why |
|---|---|---|---|
| Command | see block | ✅ Mandatory | The actual work. Without it the build "succeeds" with no tests run. |
| Advanced → ERRORLEVEL to set build unstable | *(blank)* | ⬜ Optional | Blank: any non-zero exit code (failed tests) fails the build. |

```bat
set WORKERS=4
call npm ci
call npx playwright install
call npm run test:headed
```

Line-by-line reasons are in [Step 8](#step-8-know-what-the-build-will-run).

### Step 15: Post-build Action: Publish HTML Reports

**Where:** **Add post-build action → Publish HTML reports → Add**.

| Field | Sample value | Status | Why |
|---|---|---|---|
| HTML directory to archive | `playwright-report` | 🔶 Practically required | The folder Playwright's `html` reporter writes. Blank archives the whole workspace, including `node_modules`. |
| Index page[s] | `index.html` | ✅ Mandatory | Page opened by the report link. |
| Index page title[s] (Optional) | `Playwright Results` | ⬜ Optional | Tab label inside the report viewer. |
| Report title | `Playwright Report` | ⬜ Optional | Link name in the job's left menu (`/job/Paimana_Dev/Playwright_20Report/`). |

**Publishing options** (click the dropdown):

| Option | Sample value | Status | Why |
|---|---|---|---|
| Keep past HTML reports | ✅ ticked | ⬜ Optional (recommended) | One report per build, so older runs stay viewable. |
| Always link to last build | ☐ unticked | ⬜ Optional | Unticked: the job-level link shows the last *successful* build's report. Tick it to see failed runs' reports from the job page. |
| Allow missing report | ✅ ticked | ⬜ Optional (recommended) | No extra error when tests never ran (e.g. checkout or config failure). |
| Include files | `**/*` | ⬜ Optional | Keep the default; the report needs its `data/` and `trace/` subfolders. |
| Escape underscores in Report Title | ✅ ticked (default) | ⬜ Optional | Leave as is. |
| Number of workers | `0` (default) | ⬜ Optional | Copy threads; `0` is fine for small reports. |

### Step 16: Post-build Action: E-mail Notification

**Where:** **Add post-build action → E-mail Notification**. Needs [Step 5](#step-5-set-up-email).

| Field | Sample value | Status | Why |
|---|---|---|---|
| Recipients | `qa-team@example.com you@example.com` | ⚠️ Conditionally mandatory | Space-separated, not commas. Blank means nobody gets mail. |
| Send e-mail for every unstable build | ✅ ticked (default) | ⬜ Optional | Unticked: only the first unstable build in a row emails. |
| Send separate e-mails to individuals who broke the build | ☐ unticked | ⬜ Optional | Also mails commit authors, but only those who are Jenkins users; others are skipped with `Not sending mail to unregistered user <address>`. |

This action mails only when a build **fails**, becomes **unstable**, or **returns to stable**. Passing builds in a row send nothing.

> For HTML emails, attachments or mail on every build, use **Editable Email Notification** (Email Extension plugin) instead.

### Step 17: Save

Click **Save** (or **Apply** to stay on the page).

**Check:** the job page `http://localhost:8080/job/Paimana_Dev/` shows **Build Now**, **Workspace**, **Configure**, and (after the first build) **Playwright Report**.

---

## Part D: Run and Verify

### Step 18: Run the First Build

1. Click **Build Now** (or **Build with Parameters** → pick values → **Build**).
2. Click the new build number (e.g. **#1**) → **Console Output**.

**Sample of a good console log**, with what each part proves:

```text
Checking out Revision 471f068... (refs/remotes/origin/master)     ← Step 11 works
Commit message: "Make project Jenkins-ready"                      ← latest push was built
C:\Users\shiva\.jenkins\workspace\Paimana_Dev>set WORKERS=4       ← value arrived intact
C:\Users\shiva\.jenkins\workspace\Paimana_Dev>call npm ci
added 89 packages, and audited 90 packages in 12s
> paimana-dev@1.0.0 test:headed
> cross-env HEADLESS=false playwright test
Running 7 tests using 4 workers                                   ← parallel, not 1 worker
  7 passed (20.1s)
[htmlpublisher] Archiving HTML reports...
[htmlpublisher] Archiving at BUILD level ...\playwright-report    ← BUILD = Keep past reports on
Finished: SUCCESS
```

Browser windows open maximized on the screen while the tests run.

### Step 19: Open the Report

1. On the job page (or a specific build's page), click **Playwright Report**.
2. Press **Ctrl+F5** the first time.

**Check:** the Playwright report lists all tests with pass/fail, durations and browsers. A bar with only **Back to Paimana_Dev**, **Playwright Results** and **Zip** and nothing below means Jenkins is blocking the report's JavaScript: do [Step 6](#step-6-allow-the-reports-javascript-if-not-done-in-step-2), then see [E.4](#e4-report-problems).

### Step 20: Change the Tests Later

Jenkins builds GitHub, not your local folder. After any change:

```powershell
npm run test:headless              # check locally first
git add -A
git commit -m "Describe the change"
git push origin master
```

Then **Build Now**, or wait for Poll SCM. The console's `Commit message:` line confirms which commit was built.

---

## Part E: Troubleshooting

**Read the console from the top and fix the first error.** One failure usually causes more errors further down. Example chain from a real build:

1. `config.workers must be a number or percentage` → tests never start.
2. `Build step 'Execute Windows batch command' marked build as failure`.
3. `Specified HTML directory ... playwright-report does not exist` → no report, because no tests ran.
4. `Couldn't connect to host, port: localhost, 25` → the failure email can't be sent (separate issue: Step 5 not done).

Fixing 1 removes 2 and 3; 4 needs Step 5.

### E.1 Checkout Problems

| Console message | Cause | Fix |
|---|---|---|
| `Failed to connect to github.com:443 after 21136 ms: Could not connect to server` | The PC can't reach GitHub: network drop, VPN change, firewall, or a proxy `git.exe` doesn't know. Not a login problem. | See the steps below. |
| `Authentication failed` / `403` | Wrong or expired token, or token lacks access to the repo | Update the credential's password with a new token (Step 4). |
| `Couldn't find any revision to build` | Branch Specifier doesn't match (e.g. `*/master` on a `main` repo) | Fix Step 11. |

**Steps for `Failed to connect`:**

| # | Command / action | Meaning |
|---|---|---|
| 1 | `Test-NetConnection github.com -Port 443` | `True`: the network path is open. |
| 2 | `git ls-remote https://github.com/<owner>/Paimana_Dev.git` | Lists branches: git works now, the failure was temporary. Rebuild. |
| 3 | Open https://www.githubstatus.com | Rules out a GitHub outage. |
| 4 | `netsh winhttp show proxy` | Browsers use the Windows proxy automatically; `git.exe` doesn't. |
| 5 | `git config --global http.proxy http://HOST:PORT` | Applies to Jenkins too (same Windows user). Or set `HTTPS_PROXY` under **Manage Jenkins → System → Global properties → Environment variables**. |
| 6 | Toggle VPN; allow `git.exe` in firewall/antivirus | Build in whichever state makes step 2 work. |

> **Manage Jenkins → Plugins → Advanced → HTTP Proxy** only affects plugin downloads, not Git checkout. **Retry Count** (Step 10) rides out short blips.

### E.2 Build Step Problems

| Console message / symptom | Cause | Fix |
|---|---|---|
| Only `npm ci` runs, then the step ends | Missing `call` before `npm`/`npx` | Prefix each line with `call`. |
| `'npm' is not recognized` | Node isn't on `PATH` for the Windows user running Jenkins | Install Node for that user or add it to `PATH`, then restart Jenkins. |
| `Executable doesn't exist at ...ms-playwright...` | Browsers don't match the Playwright version | Add `call npx playwright install` (Step 14). |
| `config.workers must be a number or percentage` | Config passes `WORKERS` text straight to Playwright | Use `resolveWorkers()` (Step 7b) and push. |
| Console shows `set WORKERS=50` though you typed `50%` | `%` is special in `.bat` files | `set WORKERS=50%%`, or a whole number like `4`. |
| `HEADLESS must be 'true' or 'false'` | Typo in `HEADLESS` | Use exactly `true` or `false`. |

### E.3 Test Run Problems

| Symptom | Cause | Fix |
|---|---|---|
| `Running 7 tests using 1 worker` | `CI` is set on Jenkins and the config's default is 1 worker | `set WORKERS=4` + `resolveWorkers()`, or `call npm run test:headed -- --workers=4` (the `--` passes the option through npm). |
| Windows oversized / off-screen | `--headed` flag used while `CI` is set | Use `npm run test:headed` (`HEADLESS=false`), not `--headed`. |
| Headed browsers never appear / tests hang | Jenkins runs as a Windows service (no desktop) | Use `test:headless`, or start Jenkins from your logged-in session (Step 2). |
| Firefox times out on `page.goto` | A third-party font never finishes, so Firefox's `load` event never fires | Navigate with `waitUntil: 'domcontentloaded'`. |
| Old code is tested | Changes weren't pushed | Step 20. |

**Choosing a worker count**

| Value | When |
|---|---|
| `1` | Tests share data that can't change at the same time (e.g. one test user). |
| `2`–`4` | Jenkins on a desktop PC; browsers share CPU and memory with everything else. |
| `50%%` (in a batch step) | A dedicated build machine. |

### E.4 Report Problems

| Symptom | Cause | Fix |
|---|---|---|
| Report link shows only *Back to …*, *Playwright Results*, *Zip* | Jenkins's Content Security Policy blocks the report's JavaScript | Step 2 (permanent) or Step 6 (until restart), then **Ctrl+F5**. No rebuild needed. |
| `Specified HTML directory '...\playwright-report' does not exist` | Tests didn't run, so no report | Fix the first error; tick **Allow missing report**. |
| Report shows an older run | Last build aborted/failed; the job-level link shows the last successful build | Open the specific build's **Playwright Report**, or tick *Always link to last build*. |
| Whole workspace archived | *HTML directory to archive* blank | Set `playwright-report` (Step 15). |

**Blocked or empty?** A Playwright report keeps its results inside `index.html`, so a blank page doesn't mean missing data.

| Check | Result | Meaning |
|---|---|---|
| Blank report page → **F12 → Console** | Errors like *Refused to execute inline script because it violates the following Content Security Policy directive* | Data is there; CSP blocks it. |
| Job page → **Workspace → playwright-report** | `index.html` of several hundred KB | Report written correctly. A `data` folder appears only when tests saved screenshots/traces. |
| Same place | No folder or tiny `index.html` | Report not written: the run failed to start or was aborted. |
| Build icon is a grey slash | Build aborted | Playwright writes the report only at the end of a run. |

**Other ways to view it**

- **Zip** (top right of the report page) → unzip → `npx playwright show-report <unzipped-folder>`.
- On a shared Jenkins, instead of turning the CSP off, an administrator can set **Manage Jenkins → System → Serve resource files from another domain → Resource Root URL** (a second hostname for the same Jenkins). Check the Jenkins documentation on *Configuring Content Security Policy* first.

> **Security trade-off:** with the CSP off, any HTML a build archives can run scripts while you're logged in. Acceptable on a personal Jenkins building only your own repositories.

### E.5 Email Problems

| Console message | Cause | Fix |
|---|---|---|
| `Couldn't connect to host, port: localhost, 25` | No SMTP server set | Step 5. |
| `535 ... Username and Password not accepted` | Normal Gmail password used | Use an app password (Step 5b). |
| `Not sending mail to unregistered user <address>` | *Send separate e-mails to individuals…* ticked; commit author isn't a Jenkins user | Harmless; untick the option if not needed. |
| No email after a passing build | By design | Mail only on fail, unstable, or back to stable. |

### E.6 Other Job Types

| Problem | Cause | Fix |
|---|---|---|
| Multibranch shows no branches | No Jenkinsfile at Script Path, or a branch filter excludes them | Check the **Scan Multibranch Pipeline Log**. |
| Pipeline job settings keep resetting | Jenkinsfile `options`/`parameters`/`triggers` overwrite UI settings after each run | Change them in the Jenkinsfile. |
| `Scripts not permitted to use method...` | Groovy sandbox blocks a method | Approve under **Manage Jenkins → In-process Script Approval**, or rewrite the step. |
| Job never runs automatically | No trigger, or webhook can't reach Jenkins | Use Poll SCM for a local Jenkins. |

---

## Appendix A: Choosing a Job Type

| Job Type | Best for | Configuration lives in |
|---|---|---|
| **Freestyle Project** | Simple, single-purpose tasks (run a script, trigger a deploy) | Jenkins UI |
| **Pipeline** | Multi-stage CI/CD for one branch of one repo | Jenkinsfile (inline or in repo) |
| **Multibranch Pipeline** | One repo with many branches / pull requests | Jenkinsfile in each branch |
| **Organization Folder** | Every repo in a GitHub org / Bitbucket team / GitLab group | Jenkinsfile in each repo |
| **Multi-configuration (Matrix)** | Running the same build across many combinations (OS × JDK × browser) | Jenkins UI |
| **Folder** | Grouping and organizing other jobs, scoping credentials | Jenkins UI |

**Quick rule:** for new projects, prefer **Pipeline** or **Multibranch Pipeline** so the build definition is version-controlled alongside the code.

---

## Appendix B: Freestyle Project (All Fields)

> For a complete worked example with sample values (Playwright on Windows), follow [Part C](#part-c-create-the-freestyle-job). This appendix lists **every** common Freestyle field, including ones that example doesn't use (Maven/Gradle steps, artifacts, JUnit, downstream jobs).

**What it is:** The classic, UI-driven job type. You configure source code, triggers, build steps and post-build actions through form fields.

**Use when:** You need a quick, simple job such as running a shell script, a scheduled cleanup, or a one-step deployment.

### 1. General

| Field | Status | Purpose |
|---|---|---|
| Description | ⬜ Optional | Explains what the job does. Shown on the job page; helps other team members. |
| Discard old builds | ⬜ Optional (recommended) | Deletes old build records and artifacts to save disk space. |
| ↳ Days to keep builds / Max # of builds to keep | ⚠️ Conditionally mandatory (at least one) | Defines the retention rule. Without a value, nothing is discarded. |
| This project is parameterized | ⬜ Optional | Lets users supply inputs (branch name, environment, version) when starting a build. |
| ↳ Parameter **Name** | ⚠️ Conditionally mandatory | Variable name used in build steps (e.g. `$ENVIRONMENT`). |
| ↳ Default Value / Choices / Description | ⬜ Optional | Pre-filled value, list of allowed values, and help text. |
| Disable this project | ⬜ Optional | Stops new builds from running without deleting the job. |
| Execute concurrent builds if necessary | ⬜ Optional | Allows multiple builds of this job to run at the same time. Leave off if builds share resources. |
| Restrict where this project can be run | ⬜ Optional | Pins the job to specific agents. |
| ↳ Label Expression | ⚠️ Conditionally mandatory | Agent label(s) to run on, e.g. `linux && docker`. |

**Advanced (click "Advanced" under General):**

| Field | Status | Purpose |
|---|---|---|
| Quiet period | ⬜ Optional | Seconds to wait before starting, so several quick commits trigger one build. |
| Retry Count | ⬜ Optional | How many times to retry an SCM checkout that fails. |
| Block build when upstream project is building | ⬜ Optional | Prevents running against half-built dependencies. |
| Use custom workspace → Directory | ⚠️ Conditionally mandatory | Overrides the default workspace path. |

### 2. Source Code Management

| Field | Status | Purpose |
|---|---|---|
| SCM type (None / Git) | ⬜ Optional | Choose **Git** to pull code; **None** for jobs that don't need a repository. |
| ↳ Repository URL | ⚠️ Conditionally mandatory | Where to clone from, e.g. `https://github.com/org/repo.git`. |
| ↳ Credentials | ⚠️ Conditionally mandatory (private repos) | Username/token or SSH key used to access the repository. Public repos can use *- none -*. |
| ↳ Branches to build (Branch Specifier) | ⚠️ Conditionally mandatory | Branch to check out. Default is `*/master`; change to `*/main` if your repo uses `main`. |
| ↳ Repository browser | ⬜ Optional | Adds links from Jenkins changes to your Git web UI. |
| ↳ Additional Behaviours | ⬜ Optional | Extras like *Clean before checkout*, *Shallow clone*, *Checkout to a sub-directory*. |

### 3. Build Triggers

All triggers are optional; without any, the job only runs when started manually.

| Field | Status | Purpose |
|---|---|---|
| Trigger builds remotely | ⬜ Optional | Allows triggering via a URL (e.g. from scripts). |
| ↳ Authentication Token | ⚠️ Conditionally mandatory | Secret token that must be included in the trigger URL. |
| Build after other projects are built | ⬜ Optional | Chains this job after upstream jobs. |
| ↳ Projects to watch | ⚠️ Conditionally mandatory | Names of the upstream jobs. |
| ↳ Trigger only if build is stable / unstable / fails | ⬜ Optional | Controls which upstream result starts this job. |
| Build periodically | ⬜ Optional | Runs on a fixed schedule (nightly builds, cleanups). |
| ↳ Schedule | ⚠️ Conditionally mandatory | Cron expression, e.g. `H 2 * * *` (see [Appendix H](#appendix-h-cron-schedule-cheat-sheet)). |
| GitHub hook trigger for GITScm polling | ⬜ Optional | Builds immediately when GitHub sends a webhook. Requires a webhook configured in GitHub. |
| Poll SCM | ⬜ Optional | Jenkins checks the repo for changes on a schedule and builds only if something changed. |
| ↳ Schedule | ⚠️ Conditionally mandatory | Cron expression for how often to poll, e.g. `H/5 * * * *`. |

### 4. Build Environment

| Field | Status | Purpose |
|---|---|---|
| Delete workspace before build starts | ⬜ Optional | Guarantees a clean build with no leftovers from previous runs. |
| Use secret text(s) or file(s) | ⬜ Optional | Injects credentials as environment variables, masked in logs. |
| ↳ Binding → Variable + Credentials | ⚠️ Conditionally mandatory | Variable name to expose and which stored credential to use. |
| Add timestamps to the Console Output | ⬜ Optional | Prefixes each log line with a time, useful for spotting slow steps. |
| Terminate a build if it's stuck | ⬜ Optional | Aborts builds that hang beyond a time limit. |
| ↳ Time-out strategy + Timeout minutes | ⚠️ Conditionally mandatory | Defines when a build is considered stuck. |

### 5. Build Steps

| Field | Status | Purpose |
|---|---|---|
| Add build step | 🔶 Practically required | The actual work of the job. Without at least one step, the build succeeds but does nothing. |
| ↳ Execute shell → Command | ⚠️ Conditionally mandatory | Shell commands run on Linux/macOS agents. |
| ↳ Execute Windows batch command → Command | ⚠️ Conditionally mandatory | Batch commands run on Windows agents. |
| ↳ Invoke top-level Maven targets → Goals | ⚠️ Conditionally mandatory | Maven goals, e.g. `clean install`. Maven version is also selected here. |
| ↳ Invoke Gradle script → Tasks | ⚠️ Conditionally mandatory | Gradle tasks, e.g. `clean build`. |

Example shell step:

```bash
npm ci
npm test
npm run build
```

### 6. Post-build Actions

| Field | Status | Purpose |
|---|---|---|
| Archive the artifacts | ⬜ Optional | Saves build outputs (JARs, ZIPs, reports) with the build record. |
| ↳ Files to archive | ⚠️ Conditionally mandatory | Ant-style pattern, e.g. `target/*.jar` or `dist/**`. |
| Publish JUnit test result report | ⬜ Optional | Shows test results and trends on the job page. |
| ↳ Test report XMLs | ⚠️ Conditionally mandatory | Path pattern to JUnit XML files, e.g. `**/target/surefire-reports/*.xml`. |
| Build other projects | ⬜ Optional | Triggers downstream jobs after this one. |
| ↳ Projects to build | ⚠️ Conditionally mandatory | Names of downstream jobs. |
| E-mail Notification | ⬜ Optional | Emails people when builds fail or recover. |
| ↳ Recipients | ⚠️ Conditionally mandatory | Space-separated email addresses. |

### 7. Save and Test

1. Click **Save**.
2. Click **Build Now** (or **Build with Parameters** if parameterized).
3. Open the build number → **Console Output** to verify the result.

---

## Appendix C: Pipeline

**What it is:** A job whose entire process (checkout, build, test, deploy) is defined in code using a **Jenkinsfile** written in Groovy-based Pipeline syntax.

**Use when:** You need multi-stage CI/CD for a single branch and want the build logic version-controlled.

### 1. General

Same options as the Freestyle job (Step 10) (Description, Discard old builds, Parameterized, Disable, concurrent builds). All ⬜ Optional. Pipeline-specific additions:

| Field | Status | Purpose |
|---|---|---|
| Do not allow concurrent builds | ⬜ Optional | Prevents overlapping runs. Can also be set in the Jenkinsfile with `options { disableConcurrentBuilds() }`. |
| Pipeline speed/durability override | ⬜ Optional | Trades crash resilience for speed. Leave at default unless you have performance issues. |

> Most General settings can instead be declared inside the Jenkinsfile (`options {}`, `parameters {}`, `triggers {}`). Settings in the Jenkinsfile overwrite the UI after the first run.

### 2. Build Triggers

Same as the Freestyle job (Step 12): *Build periodically*, *Poll SCM*, *GitHub hook trigger*, *Trigger builds remotely*, *Build after other projects*. All ⬜ Optional, with the same conditionally mandatory sub-fields (Schedule, Token, Projects to watch).

### 3. Pipeline Definition (the core section)

| Field | Status | Purpose |
|---|---|---|
| Definition | ✅ Mandatory | Chooses where the pipeline code comes from: **Pipeline script** (typed into Jenkins) or **Pipeline script from SCM** (Jenkinsfile in your repo). |

#### Option A — Pipeline script

| Field | Status | Purpose |
|---|---|---|
| Script | 🔶 Practically required | The pipeline code itself. An empty script fails at runtime. |
| Use Groovy Sandbox | ⬜ Optional (keep enabled) | Runs the script with security restrictions. Disabling it requires administrator script approval. |
| Pipeline Syntax (link) | — | Opens the Snippet Generator, which writes step syntax for you. |

#### Option B — Pipeline script from SCM (recommended)

| Field | Status | Purpose |
|---|---|---|
| SCM | ⚠️ Conditionally mandatory | Source control type, usually **Git**. |
| ↳ Repository URL | ⚠️ Conditionally mandatory | Repo containing the Jenkinsfile. |
| ↳ Credentials | ⚠️ Conditionally mandatory (private repos) | Access to the repository. |
| ↳ Branch Specifier | ⚠️ Conditionally mandatory | Branch to read the Jenkinsfile from (e.g. `*/main`). |
| Script Path | ⚠️ Conditionally mandatory | Path to the Jenkinsfile in the repo. Default `Jenkinsfile`; change if it lives elsewhere (e.g. `ci/Jenkinsfile`). |
| Lightweight checkout | ⬜ Optional | Reads only the Jenkinsfile instead of cloning the whole repo first. Faster; leave enabled. |

### 4. Sample Jenkinsfile

```groovy
pipeline {
    agent any

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    stages {
        stage('Checkout') {
            steps { checkout scm }
        }
        stage('Build') {
            steps { sh 'npm ci && npm run build' }
        }
        stage('Test') {
            steps { sh 'npm test' }
        }
    }

    post {
        always  { junit allowEmptyResults: true, testResults: 'reports/*.xml' }
        failure { echo 'Build failed!' }
    }
}
```

| Jenkinsfile block | Status | Purpose |
|---|---|---|
| `pipeline {}` | ✅ Mandatory | Root of every Declarative Pipeline. |
| `agent` | ✅ Mandatory | Where the pipeline runs (`any`, a label, or a Docker image). |
| `stages {}` with at least one `stage` | ✅ Mandatory | Ordered phases of the build. |
| `steps {}` inside each stage | ✅ Mandatory | Commands executed in that stage. |
| `options`, `environment`, `parameters`, `triggers`, `post` | ⬜ Optional | Settings, variables, inputs, schedules and cleanup/notification actions. |

### 5. Save and Test

Click **Save** → **Build Now**. The **Stage View** on the job page shows each stage's status and duration.

---

## Appendix D: Multibranch Pipeline

**What it is:** A container job that scans a repository, finds every branch (and optionally pull request) containing a Jenkinsfile, and automatically creates a Pipeline job for each one.

**Use when:** Your team works with feature branches and pull requests and you want each one built automatically.

### 1. General

| Field | Status | Purpose |
|---|---|---|
| Display Name | ⬜ Optional | Friendlier name shown in the UI (the item name stays in the URL). |
| Description | ⬜ Optional | Explains the project. |
| Disable | ⬜ Optional | Pauses scanning and building of all branches. |

### 2. Branch Sources

| Field | Status | Purpose |
|---|---|---|
| Add source | 🔶 Practically required | Where to discover branches from: **Git**, **GitHub**, **Bitbucket**, **GitLab**. Without a source, no branches are found. |

**For a GitHub source:**

| Field | Status | Purpose |
|---|---|---|
| Credentials | 🔶 Practically required | GitHub username + personal access token (or GitHub App). Needed for private repos and strongly recommended for public ones to avoid API rate limits. |
| Repository HTTPS URL | ⚠️ Conditionally mandatory | The repository to scan, e.g. `https://github.com/org/repo`. |
| Behaviours → Discover branches | ⬜ Optional (added by default) | Which branches to build (e.g. exclude branches that are also PRs). |
| Behaviours → Discover pull requests from origin | ⬜ Optional (added by default) | Builds PRs opened from branches in the same repo. |
| Behaviours → Discover pull requests from forks | ⬜ Optional (added by default) | Builds PRs from forks; the *Trust* setting controls whose Jenkinsfile changes are trusted. |
| Behaviours → Filter by name (with wildcards) | ⬜ Optional | Include/exclude branches, e.g. include `main develop feature/*`. |
| Property strategy | ⬜ Optional | Applies properties to branches, e.g. suppress automatic builds for certain branches. |

**For a plain Git source:**

| Field | Status | Purpose |
|---|---|---|
| Project Repository | ⚠️ Conditionally mandatory | Repository URL. |
| Credentials | ⚠️ Conditionally mandatory (private repos) | Repository access. |
| Behaviours | ⬜ Optional | Discover branches / tags, filter by name, etc. |

### 3. Build Configuration

| Field | Status | Purpose |
|---|---|---|
| Mode | ✅ Mandatory (default: *by Jenkinsfile*) | How each branch's pipeline is defined. |
| Script Path | ✅ Mandatory (default: `Jenkinsfile`) | Path of the Jenkinsfile in each branch. Only branches containing this file become jobs. |

### 4. Scan Multibranch Pipeline Triggers

| Field | Status | Purpose |
|---|---|---|
| Periodically if not otherwise run | ⬜ Optional (recommended) | Rescans the repo to detect new/deleted branches if webhooks are missed. |
| ↳ Interval | ⚠️ Conditionally mandatory | How often to rescan, e.g. `1 hour` or `1 day`. |

> With webhooks configured, new commits trigger builds instantly. The periodic scan is a safety net.

### 5. Orphaned Item Strategy

| Field | Status | Purpose |
|---|---|---|
| Discard old items | ⬜ Optional (enabled by default) | Removes jobs for branches deleted from the repo. |
| ↳ Days to keep old items / Max # of old items to keep | ⬜ Optional | How long jobs for deleted branches remain. Blank means delete immediately. |

### 6. Other Sections

| Field | Status | Purpose |
|---|---|---|
| Appearance → Icon | ⬜ Optional | Cosmetic. |
| Health metrics | ⬜ Optional | Controls how the folder's weather icon is calculated. |
| Properties → Pipeline Libraries | ⬜ Optional | Shared libraries available to all branches. |

### 7. Save

Clicking **Save** automatically runs **Scan Multibranch Pipeline Now**. Check **Scan Multibranch Pipeline Log** to confirm which branches were found and which were skipped (and why).

---

## Appendix E: Organization Folder

**What it is:** Scans an entire GitHub organization, Bitbucket team/project, or GitLab group, and creates a Multibranch Pipeline for every repository that contains a Jenkinsfile.

**Use when:** You want every repo in an organization onboarded to CI automatically, without creating jobs one by one.

### 1. General

| Field | Status | Purpose |
|---|---|---|
| Display Name / Description | ⬜ Optional | Friendly name and explanation. |

### 2. Projects (Repository Sources)

| Field | Status | Purpose |
|---|---|---|
| Repository Sources | ✅ Mandatory | Platform type: GitHub Organization, Bitbucket Team/Project, or GitLab Group. |

**For GitHub Organization:**

| Field | Status | Purpose |
|---|---|---|
| API endpoint | ⬜ Optional | Leave default for github.com; set for GitHub Enterprise. |
| Credentials | 🔶 Practically required | Token or GitHub App with read access to the org's repos. Anonymous scanning hits rate limits quickly and can't see private repos. |
| Owner | ✅ Mandatory | Organization or user name to scan, e.g. `my-company`. |
| Behaviours → Discover branches / pull requests | ⬜ Optional (added by default) | Same purpose as in Multibranch. |
| Behaviours → Filter by name (with regular expression / wildcards) | ⬜ Optional | Limit which repositories are included. |

### 3. Project Recognizers

| Field | Status | Purpose |
|---|---|---|
| Pipeline Jenkinsfile | ✅ Mandatory (default) | Decides which repos become jobs: only those containing the Jenkinsfile. |
| ↳ Script Path | ✅ Mandatory (default `Jenkinsfile`) | Expected location of the Jenkinsfile in each repo. |

### 4. Scan Organization Triggers

| Field | Status | Purpose |
|---|---|---|
| Periodically if not otherwise run → Interval | ⬜ Optional (recommended) | Rescans the organization to find new or removed repos. |

### 5. Orphaned Item Strategy & Child Strategies

| Field | Status | Purpose |
|---|---|---|
| Orphaned Item Strategy | ⬜ Optional | Removes jobs for deleted repositories. |
| Child Orphaned Item Strategy | ⬜ Optional | Removes jobs for deleted branches inside each repo. |
| Automatic branch project triggering → Branch names to build automatically | ⬜ Optional | Regex for which branches build automatically after scanning (default `.*` = all). |

### 6. Save

Save triggers an organization scan. Review **Scan Organization Log** to see which repos were recognised.

---

## Appendix F: Multi-configuration Project (Matrix)

**What it is:** A UI-configured job that runs the same build steps across every combination of defined variables ("axes"), e.g. 3 JDK versions × 2 operating systems = 6 builds.

**Use when:** You need compatibility testing across platforms, versions or browsers. (In Pipelines, the equivalent is the `matrix {}` directive.)

**Requires:** Matrix Project plugin.

### 1. General, Source Code Management, Build Triggers, Build Environment

Same fields and rules as [Part C](#part-c-create-the-freestyle-job).

### 2. Configuration Matrix (the core section)

| Field | Status | Purpose |
|---|---|---|
| Add axis | 🔶 Practically required | Defines a variable to vary. Without axes, there is no matrix and only one configuration runs. |
| ↳ **User-defined Axis** → Name | ⚠️ Conditionally mandatory | Variable name used in build steps, e.g. `BROWSER`. |
| ↳ **User-defined Axis** → Values | ⚠️ Conditionally mandatory | Space-separated values, e.g. `chrome firefox edge`. |
| ↳ **Agents** / **Label expression** axis → Name + nodes/labels | ⚠️ Conditionally mandatory | Runs each combination on different agents (e.g. `linux`, `windows`). |
| ↳ **JDK** axis | ⚠️ Conditionally mandatory | Picks JDK installations configured under **Tools**. |
| Combination Filter | ⬜ Optional | Groovy expression to skip invalid combinations, e.g. `!(OS == "linux" && BROWSER == "edge")`. |
| Run each configuration sequentially | ⬜ Optional | Runs combinations one after another instead of in parallel (saves resources). |
| Execution Strategy → Touchstone builds | ⬜ Optional | Runs a subset first; if it fails, the remaining combinations are skipped. |

### 3. Build Steps

| Field | Status | Purpose |
|---|---|---|
| Add build step | 🔶 Practically required | Runs once per combination. Reference axis values as variables. |

Example:

```bash
echo "Testing on $BROWSER with Java $JDK_VERSION"
mvn test -Dbrowser=$BROWSER
```

### 4. Post-build Actions

Same as the Freestyle job (Steps 15–16). Test reports are aggregated across all combinations on the parent job page.

### 5. Save and Test

Click **Build Now**. The job page shows a grid with the result of each combination.

---

## Appendix G: Folder

**What it is:** A container used to organize jobs into groups, similar to a directory. It does not build anything itself.

**Use when:** You have many jobs and want to group them by team, product or environment, or restrict credentials and permissions to a subset of jobs.

### 1. Configuration

| Field | Status | Purpose |
|---|---|---|
| Display Name | ⬜ Optional | Friendly name shown in the UI. |
| Description | ⬜ Optional | Explains what the folder contains. |
| Health metrics | ⬜ Optional | How the folder's weather icon summarizes its jobs. |
| Properties → Pipeline Libraries | ⬜ Optional | Shared libraries available only to jobs inside this folder. |

### 2. Folder-scoped Credentials (after saving)

1. Open the folder → **Credentials** → **Folder** store → **Global credentials** → **Add Credentials**.
2. Credentials added here are visible only to jobs inside this folder, which is safer than global credentials.

### 3. Add Jobs

Open the folder and click **New Item**; jobs created there live inside it. Existing jobs can be moved via the job's **Move** option.

---

## Appendix H: Cron Schedule Cheat Sheet

Used by **Build periodically**, **Poll SCM** and the `triggers {}` block. Format:

```
MINUTE  HOUR  DAY-OF-MONTH  MONTH  DAY-OF-WEEK
(0-59)  (0-23)   (1-31)     (1-12)   (0-7, 0 and 7 = Sunday)
```

`H` means "a hashed value" chosen per job, which spreads load so all jobs don't start at the same minute. Prefer `H` over fixed numbers.

| Expression | Meaning |
|---|---|
| `H/15 * * * *` | Every 15 minutes |
| `H * * * *` | Once every hour |
| `H 2 * * *` | Once a day, sometime between 2:00 and 2:59 AM |
| `H 9 * * 1-5` | Weekdays, around 9 AM |
| `H 0 * * 0` | Weekly on Sunday, around midnight |
| `@daily` / `@weekly` / `@midnight` | Shortcut aliases |

---

## Appendix I: The Same Job as a Pipeline

The Freestyle job from Part C, written as a Jenkinsfile. Commit it as `Jenkinsfile` at the repository root, then create a **Pipeline** job ([Appendix C](#appendix-c-pipeline)) with **Definition: Pipeline script from SCM**, the same repository, credential and branch, and **Script Path** `Jenkinsfile`.

```groovy
pipeline {
    agent any

    options {
        timestamps()                                   // Timestamper plugin
        buildDiscarder(logRotator(numToKeepStr: '20'))
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    environment {
        WORKERS = '4'                                  // read by resolveWorkers()
    }

    stages {
        stage('Install') {
            steps {
                bat 'npm ci'
                bat 'npx playwright install'
            }
        }
        stage('Test') {
            steps {
                bat 'npm run test:headed'
            }
        }
    }

    post {
        always {
            publishHTML(target: [
                reportDir            : 'playwright-report',
                reportFiles          : 'index.html',
                reportName           : 'Playwright Report',
                keepAll              : true,
                alwaysLinkToLastBuild: false,
                allowMissing         : true,
            ])
        }
        failure {
            mail to: 'qa-team@example.com',
                 subject: "FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "See ${env.BUILD_URL}console"
        }
    }
}
```

| Freestyle field (Part C) | Jenkinsfile equivalent |
|---|---|
| Discard old builds → 20 | `buildDiscarder(logRotator(numToKeepStr: '20'))` |
| Add timestamps | `timestamps()` |
| Terminate a build if it's stuck → 30 min | `timeout(time: 30, unit: 'MINUTES')` |
| `set WORKERS=4` | `environment { WORKERS = '4' }` |
| Execute Windows batch command | one `bat '...'` per command; `call` isn't needed because each `bat` is its own script |
| Publish HTML reports | `publishHTML(...)` in `post { always { } }` |
| E-mail Notification | `mail` in `post { failure { } }` |

Inside `bat '...'`, the `%%` rule from Step 8 still applies. Checkout happens automatically (`checkout scm`) because the Jenkinsfile comes from Git.

---

*End of guide.*
