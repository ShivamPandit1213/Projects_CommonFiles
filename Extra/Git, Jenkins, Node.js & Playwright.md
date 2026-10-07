# PAIMANA — Complete Git, Jenkins, Node.js & Playwright Operational Reference

A comprehensive, tabular cheat sheet mapping every command to its exact function, internal mechanism, and situational trigger. Written for **Windows CMD**, a local **Jenkins** server, and a **Node.js Playwright** project.

**Project reference values used below**

| Item | Value |
| :--- | :--- |
| Local project path | `C:\Users\shiva\OneDrive\JavaSelenium\PlaywrightBasic1` |
| GitHub repository | `https://github.com/ShivamPandit1213/PlaywrightBasic1.git` |
| Default branch | `main` |
| Latest commit | `c5fc942` — "Require Node 24 LTS and align @types/node" |
| Node.js (local and Jenkins) | `v24.21.0` (LTS) |
| npm | `11.19.0` |
| Playwright | `1.63.0` |
| `package.json` engines | `"node": ">=24"` |
| `@types/node` | `^24.0.0` (matches the Node version) |
| Local test result | 9 tests, all passing |
| Jenkins URL | `http://localhost:8080` |
| Jenkins home / workspace | `C:\Users\shiva\.jenkins` / `C:\Users\shiva\.jenkins\workspace\<job>` |
| Jenkins NodeJS tool name | `Node24` |
| Jenkins job — headless | `playwright-tests` (Freestyle) |
| Jenkins job — headed | `PlaywrightBasic1-Headed` (Freestyle) |
| Old Jenkins job | `PlaywrightBasic1` (Maven project — fails with `No such file ... pom.xml`; delete or replace) |

---

## 1. Network, DNS & Diagnostics

| Command | Purpose | What It Does Under the Hood | When to Use |
| :--- | :--- | :--- | :--- |
| `ping github.com` | Verify GitHub Reachability | Sends ICMP echo request packets to test routing and name resolution. Note: GitHub may drop ICMP, so timeouts do not always mean GitHub is down. | When Git operations hang or fail with connection timeouts. |
| `curl -I https://github.com` | Verify HTTPS Reachability | Sends an HTTP HEAD request over port 443 (the same path Git uses) and prints the response headers. | When `ping` is inconclusive; a `200` or `301` response confirms HTTPS access works. |
| `ipconfig /flushdns` | Reset DNS Resolver Cache | Clears and refreshes the Windows local DNS name resolution cache. | When receiving `fatal: unable to access ... Could not resolve host: github.com`. |
| `git config --global --get http.proxy` | Inspect Git Proxy | Reads the global `.gitconfig` file and outputs the currently configured proxy address. | When troubleshooting connection issues in corporate or VPN environments. |
| `git config --global --unset http.proxy` | Remove Git Proxy | Deletes the `http.proxy` entry from the global Git configuration. | When switching off an office proxy or fixing invalid proxy routing. |
| `git status` | Inspect Working Tree | Compares the working tree against the index (staging area), the `HEAD` commit, and the upstream branch (ahead/behind count). "Your branch is up to date with 'origin/main'" appears only once upstream tracking is set. | Run before and after every staging, committing, pulling, or pushing operation. |
| `git remote -v` | Verify Remote URLs | Prints the fetch/push URLs associated with each remote alias (e.g., `origin`). Empty output means no remote is configured. | Run to confirm the repository is pointing to the correct GitHub URL. |
| `git branch` | List Local Branches | Lists local branches and marks the current one with `*`. | To confirm you are on `main` before committing or pushing. |
| `git rev-parse --show-toplevel` | Identify Repository Root | Traverses upward and outputs the absolute path where the active `.git` directory lives. | Run to verify a subproject is completely isolated from any parent folder `.git`. |
| `git log --oneline` | View Concise History | Traverses the commit graph from `HEAD` backward, printing each commit hash and subject line. | Run to confirm local commits were created or to locate hashes for rollbacks. |
| `git log origin/main..main --oneline` | List Unpushed Commits | Shows commits reachable from local `main` but not from the remote-tracking branch `origin/main`. Run `git fetch origin` first. | Run before pushing to see exactly what will be uploaded. Empty output means nothing to push. |
| `git diff` | View Unstaged Changes | Generates a patch representation of modifications in the working directory not yet staged. | Run before `git add` to review line-by-line modifications. |
| `git diff --stat` | Summarize Unstaged Changes | Prints each changed file with a count of inserted/deleted lines (e.g., `package.json | 7 +++++--`). | Quick check that an edit was actually saved before committing. Empty output means no changes. |
| `git ls-files` | Inspect Tracked Index | Dumps the list of all file paths currently recorded in the Git staging index. | Run to verify no unwanted files (`node_modules/`, `target/`, `.class`, `.jar`) are tracked. |
| `git ls-files package-lock.json` | Confirm Lockfile Is Tracked | Prints the path only if the file is in the index; prints nothing if it is untracked. | Run before relying on `npm ci` in Jenkins, which requires a committed `package-lock.json`. |

---

## 2. Project Isolation, Setup & Initialization

| Command | Purpose | What It Does Under the Hood | When to Use |
| :--- | :--- | :--- | :--- |
| `cd /d <path>` | Switch Directory Across Drives | Changes both the active directory and disk volume drive letter in Windows CMD. | When navigating between project folders across different drives. |
| `rmdir /s /q .git` | Wipe Local Git Repository (CMD) | Recursively and silently deletes the hidden `.git` folder from the file system. All local history is lost. | When resetting an accidental parent repository (e.g., `JavaSelenium`) without touching code. |
| `git init` | Initialize Local Repository | Creates an empty `.git` directory containing `objects`, `refs`, and template configurations. | When setting up a brand-new repository in an isolated project folder. |
| `git remote add origin <url>` | Link Remote Repository | Creates a named pointer `origin` mapping to the specified GitHub repository URL in `.git/config`. | After `git init`, or to restore a remote removed by mistake (e.g., `git remote add origin https://github.com/ShivamPandit1213/PlaywrightBasic1.git`). |
| `git remote set-url origin <url>` | Re-point Existing Remote | Replaces the URL of the existing `origin` entry in one step. | Preferred over remove + add when the repository simply moved or was renamed. |
| `git remote remove origin` | Unlink Remote Repository | Deletes the `origin` reference entry from `.git/config`. After this, `git push`/`git pull` fail until a remote is added again. Not part of the daily routine. | Only when the repository must be detached from GitHub or re-pointed from scratch. |
| `git branch -M main` | Set Default Branch Name | Renames the current HEAD branch reference to `main`. | Prior to initial push to standardize branch naming with GitHub conventions. |
| `git push -u origin main` | Initial Push & Set Tracking | Transfers packfiles to GitHub and writes `branch.main.remote` and `branch.main.merge` to `.git/config` ("branch 'main' set up to track 'origin/main'"). Needed only once per branch. | On the first push of a new repository or branch, or after re-adding a removed remote. |
| `git push` | Push Tracked Commits | Uploads local commits on the active branch to its upstream branch. A successful push prints a range such as `2dd823e..c5fc942  main -> main`; "Everything up-to-date" means there was nothing new to send. | For all standard daily uploads after upstream tracking has been configured. |

---

## 3. Staging, Commits & Rollbacks

| Command | Purpose | What It Does Under the Hood | When to Use |
| :--- | :--- | :--- | :--- |
| `git add .` | Stage All Modifications | Adds all new, modified, and deleted working tree files (respecting `.gitignore`) to the index. | When bundling all project changes into the next commit snapshot. |
| `git add "filename"` | Stage Specific File | Stages only the designated file path (double quotes protect paths containing spaces). | When committing specific files (e.g., `Jenkinsfile`, `package.json`, `package-lock.json`). |
| `git add package.json package-lock.json` | Stage Dependency Files Together | Stages the manifest and lockfile in one step so they stay in sync on GitHub. | After any `package.json` change followed by `npm install`; committing only one breaks `npm ci` in Jenkins. |
| `git commit -m "message"` | Create Commit Object | Packages the current staging area into a tree object, writes metadata, and advances `HEAD`. Reports "nothing to commit, working tree clean" if no file was changed and saved. | Directly following `git add` to record a permanent change point. |
| `git rm -r --cached <folder>` | Untrack Folder from Git | Removes the directory tree from the Git index while leaving all physical files on disk. | When build or dependency folders (`node_modules/`, `target/`, `.vscode/`, `playwright-report/`) were tracked by accident. |
| `git reset --soft HEAD~1` | Undo Previous Commit | Moves the `HEAD` and branch reference back one commit while leaving all changes staged. | When a commit was made prematurely or needs an updated message (only if not yet pushed). |
| `git revert HEAD` | Safely Revert Public Commit | Appends a brand-new commit that applies the exact inverse diff of the `HEAD` commit. | When an erroneous commit has already been pushed to a remote GitHub branch. |
| `git checkout -- .` | Discard Uncommitted Edits | Overwrites modified tracked files in the working directory with the copies from the index. Modern equivalent: `git restore .` | When local experimental changes need to be discarded completely. |

**What Git does and does not track:** Git records only changes to files inside the repository. Switching the Node version on your PC, installing browsers, or changing Jenkins settings does not create anything to commit. Edit and save a file first, confirm with `git diff --stat`, then commit.

---

## 4. Synchronization & Merge Conflict Resolution

| Command | Purpose | What It Does Under the Hood | When to Use |
| :--- | :--- | :--- | :--- |
| `git fetch origin` | Download Remote References | Fetches commit objects and updates remote-tracking branches (`origin/main`) without merging. | To inspect remote commits before deciding how to integrate them; follow with `git status`. |
| `git pull --no-rebase origin main` | Fetch and Merge | Pulls commits and generates a standard three-way merge commit joining histories. | When the remote has commits you lack (`[rejected] fetch first` error). |
| `git checkout --ours <file>` | Resolve Conflict: Keep Local | Overwrites the conflicted file in the working tree with the stage 2 version (`HEAD`). | During merge conflicts when your local implementation is the one to retain. |
| `git checkout --theirs <file>` | Resolve Conflict: Keep Remote | Overwrites the conflicted file in the working tree with the stage 3 version (incoming). | During merge conflicts when the GitHub/remote version is the one to retain. |
| `git rm <file>` | Resolve Modify/Delete Conflict | Removes the file from both the working directory and the staging index. | When a file was deleted on one side and the deletion should be accepted. |
| `git merge --abort` | Abort Active Merge | Resets the index and working tree back to the pre-merge `HEAD` state. | When merge conflicts are extensive and you need to safely return to a clean baseline. |
| `git stash` | Park Working Directory Changes | Saves uncommitted edits into `refs/stash` and resets the working tree to `HEAD`. | When `git pull` is refused due to uncommitted local edits. |
| `git stash pop` | Reapply Parked Changes | Applies the latest stashed patch on top of the working tree and removes it from the stash stack. | Immediately after completing a `git pull` on a clean tree. |
| `git pull --no-rebase -X ignore-space-at-eol origin main` | Merge Ignoring CRLF Differences | Executes the merge while instructing the merge strategy to disregard end-of-line whitespace mismatches. | When files conflict on every line strictly due to Windows (CRLF) vs Unix (LF) formatting. |
| `git config --global core.autocrlf true` | Normalize Line Endings on Windows | Converts LF to CRLF on checkout and CRLF to LF on commit. This is what produces the harmless "LF will be replaced by CRLF" warning. | Once per Windows machine to keep line endings consistent with GitHub/Linux. |

---

## 5. Node.js, npm & `package.json`

| Command | Purpose | What It Does Under the Hood | When to Use |
| :--- | :--- | :--- | :--- |
| `node -v` | Check Node.js Version | Prints the version of the `node.exe` found first in `PATH`. | After installing or switching Node; expected output: `v24.x.x` (LTS). |
| `npm -v` | Check npm Version | Prints the version of the npm CLI bundled with the active Node install. | Alongside `node -v` to confirm the toolchain. |
| `npx -v` | Check npx Version | Prints the npx version (shipped with npm, so it matches the npm version). | To confirm `npx` is available before running Playwright. |
| `where node` | Locate Node Executable | Searches every folder in `PATH` and lists each `node.exe` found (usually `C:\Program Files\nodejs`). | To find the install path for Jenkins `PATH+NODE`, or to detect duplicate installs. |
| `nvm install 24` | Install Node 24 via nvm-windows | Downloads the latest Node 24.x into the nvm directory without touching other versions. | When keeping several Node versions side by side (requires nvm-windows installed). |
| `nvm use 24` | Switch Active Node Version | Repoints the `nodejs` symlink in `PATH` to the chosen version (run CMD as Administrator). | To switch between Node versions (e.g., `nvm use 25` to go back). |
| `nvm list` | List Installed Node Versions | Prints every Node version managed by nvm and marks the active one. | To confirm which versions are installed. |
| `notepad package.json` | Edit Project Manifest | Opens `package.json` in Notepad; changes count only after saving with **Ctrl + S**. | When adding `engines`, changing dependency versions, or editing scripts. |
| `node -e "require('./package.json'); console.log('package.json is valid')"` | Validate `package.json` Syntax | Parses the file as JSON; prints the message if valid, or a syntax error (e.g., a missing comma) if not. | Immediately after editing `package.json`. |
| `rmdir /s /q node_modules` | Delete Installed Dependencies | Recursively and silently deletes the `node_modules` folder. | After changing Node versions or when dependencies are corrupted. |
| `npm install` | Install / Update Dependencies | Resolves versions from `package.json` ranges (e.g., `^1.59.1` can resolve to `1.63.0`), installs them, and creates or updates `package-lock.json`. | Locally, after editing dependencies (e.g., `@types/node`) or when no lockfile exists yet. Commit the resulting `package-lock.json`. |
| `npm ci` | Clean, Reproducible Install | Deletes `node_modules` and installs exactly the versions in `package-lock.json`; fails if the lockfile is missing or out of sync with `package.json`. | In Jenkins/CI builds, so every run uses identical dependency versions. |
| `npm run <script>` | Run a `package.json` Script | Executes the command defined under `"scripts"` with `node_modules\.bin` on the PATH. | To use project shortcuts such as `npm run test:headed` (see Section 6). |

**Node version rule:** even-numbered releases (22, 24, 26) become **LTS**; odd-numbered releases (21, 23, 25) are short-lived "Current" versions. Use LTS for Jenkins. Node 26 becomes LTS in October 2026; until then, use **24.x**.

**Installing Node 24 LTS locally (Windows):** Settings → Apps → Installed apps → uninstall Node.js → download the LTS `.msi` (64-bit) from https://nodejs.org → install with defaults → open a **new** CMD window → run `node -v`.

**Current `package.json` (committed in `c5fc942`):**

```json
{
  "name": "playwrightbasic1",
  "version": "1.0.0",
  "description": "Playwright (JavaScript) test that opens the IIG/NEGD dev portal home page",
  "private": true,
  "engines": {
    "node": ">=24"
  },
  "scripts": {
    "test": "playwright test",
    "test:headed": "cross-env HEADLESS=false playwright test",
    "test:headless": "cross-env HEADLESS=true playwright test",
    "test:chromium": "playwright test --project=chromium",
    "report": "playwright show-report"
  },
  "keywords": [],
  "author": "",
  "license": "ISC",
  "type": "module",
  "devDependencies": {
    "@playwright/test": "^1.63.0",
    "@types/node": "^24.0.0",
    "cross-env": "^10.1.0"
  }
}
```

- `"engines"` documents that the project requires Node 24 or newer.
- `@types/node` should match the Node major version you run (24), so editor hints stay accurate.

**Safe `package.json` change workflow:**

```bat
notepad package.json
node -e "require('./package.json'); console.log('package.json is valid')"
npm install
npx playwright test
git diff --stat
git add package.json package-lock.json
git commit -m "Describe the change"
git push
```

---

## 6. Playwright

| Command | Purpose | What It Does Under the Hood | When to Use |
| :--- | :--- | :--- | :--- |
| `npx playwright --version` | Check Playwright Version | Runs the locally installed `@playwright/test` CLI from `node_modules` and prints its version. | After `npm install` / `npm ci` to confirm the version (e.g., `1.63.0`). |
| `npx playwright install` | Download Browsers | Downloads Chromium, Firefox, and WebKit builds matching the installed Playwright version into `%USERPROFILE%\AppData\Local\ms-playwright`. Prints nothing if they are already present. | After installing or upgrading Playwright, and in every Jenkins build (the Jenkins account may have its own profile). |
| `npx playwright install --with-deps` | Download Browsers + OS Libraries | Also installs required system packages via the Linux package manager. | **Linux agents only**; do not use on Windows. |
| `npx playwright test` | Run All Tests | Reads `playwright.config`, discovers test files, runs them (headless by default), and writes `playwright-report/` and `test-results/`. Does **not** set `HEADLESS`. | Locally and as the main Jenkins test step. |
| `npx playwright test --headed` | Force Visible Browser | Launches browsers in headed mode, overriding the `headless` setting in the config. | Local debugging, or a headed Jenkins job when Jenkins runs in your desktop session (Section 9). |
| `npx playwright test --headed --workers=1` | Headed, One Test at a Time | Runs headed with a single worker, so only one browser window is open at a time. | When watching tests run, locally or in the headed Jenkins job. |
| `npx playwright test --project=chromium` | Run One Browser Project | Limits the run to the named project defined in `playwright.config`. | For faster runs or isolating browser-specific failures. |
| `npx playwright test --debug` | Step Through Tests | Opens the Playwright Inspector and pauses so you can step through each action. | Local debugging only, never in Jenkins. |
| `npx playwright show-report` | Open HTML Report Locally | Starts a local web server and opens `playwright-report/index.html` in the browser. | After a local run to inspect results, traces, and screenshots. |
| `npm run test:headed` | Headed via Script | Runs `cross-env HEADLESS=false playwright test`. Works only if the config reads `HEADLESS`. | Local headed runs using the project script. |
| `npm run test:headless` | Headless via Script | Runs `cross-env HEADLESS=true playwright test`. | Local headless runs using the project script. |
| `npm run test:chromium` | Chromium Only via Script | Runs `playwright test --project=chromium`. | Quick single-browser runs. |
| `npm run report` | Open Report via Script | Runs `playwright show-report`. | After a local run. |

**Making the `HEADLESS` variable work:** the npm scripts set `HEADLESS`, but Playwright ignores it unless `playwright.config.js` reads it. In the `use` section:

```js
use: {
  headless: process.env.HEADLESS !== 'false',
  // ...other settings
},
```

This runs headless by default and headed only when `HEADLESS=false`. Make sure no entry under `projects: [...]` sets its own `headless: true`, which would override it. The `--headed` flag always wins over the config.

---

## 7. Jenkins Setup (UI Configuration)

| Area | Location in Jenkins | Setting / Value | Purpose |
| :--- | :--- | :--- | :--- |
| Plugins | Manage Jenkins → Plugins → Available plugins | Install **NodeJS**, **HTML Publisher**, **Git** | Run npm/npx, publish the Playwright HTML report, clone from GitHub. |
| Node tool | Manage Jenkins → Tools → NodeJS installations → Add NodeJS | Name: `Node24`; ☑ Install automatically; Version: **NodeJS 24.21.0** | Jenkins downloads and manages its own Node LTS, independent of the local install. Hovering over another version in the dropdown does not select it; confirm the box shows 24.21.0 before saving. |
| Job type | Dashboard → New Item | **Freestyle project** (or **Pipeline**), never **Maven project** | A Maven job always parses `pom.xml` ("Parsing POMs") and fails on a Node project. The type cannot be changed after creation, so create a new job. |
| Remove old Maven job | Job → Delete Project (left menu) | Old job: `PlaywrightBasic1` | Frees the name if you want to reuse it for the Freestyle job. |
| New job | Dashboard → New Item | Name: `playwright-tests` (headless) or `PlaywrightBasic1-Headed` (headed); type: **Freestyle project** | Separate job needed because Maven jobs only run `mvn` goals. A Freestyle menu shows *Build Steps*; a Maven menu shows *Pre Steps / Build / Post Steps / Build Settings*. |
| Source code | Job → Configure → Source Code Management → Git | URL: `https://github.com/ShivamPandit1213/PlaywrightBasic1.git`; Branch: `*/main`; Credentials: `- none -` (public repo) or GitHub username + Personal Access Token (private repo) | Jenkins clones its own copy of the repository for each build. |
| Parameter (optional) | Job → Configure → General → ☑ This project is parameterized → Add Parameter → Choice Parameter | Name: `HEADLESS`; Choices: `true` and `false` (one per line; the first is the default) | Lets you choose headless/headed per run via **Build with Parameters**. Jenkins passes `HEADLESS` to the build as an environment variable; the config must read it (Section 6). |
| Triggers (optional) | Job → Configure → Triggers | Build periodically `H 2 * * *`; Poll SCM `H/15 * * * *`; or GitHub hook trigger (needs a webhook) | Run automatically on a schedule or on push. |
| Node on PATH | Job → Configure → Environment | ☑ Provide Node & npm bin/ folder to PATH → `Node24` | Makes `node`, `npm`, and `npx` available to the build step. |
| Build step | Job → Configure → Build Steps → Add build step → Execute Windows batch command | See Section 8 batch scripts | Installs dependencies and browsers, then runs tests. |
| Report | Job → Configure → Post-build Actions → Publish HTML reports → Add | HTML directory to archive: `playwright-report`; Index page[s]: `index.html`; Report title: `Playwright Report` | Adds a **Playwright Report** link to the job page. Missing from the list → install the HTML Publisher plugin. |
| Artifacts (optional) | Job → Configure → Post-build Actions → Archive the artifacts | `playwright-report/**, test-results/**` | Keeps reports, traces, and screenshots per build. |
| Alternative Node PATH | Manage Jenkins → System → Global properties → ☑ Environment variables | Name: `PATH+NODE`; Value: `C:\Program Files\nodejs` | Uses the locally installed Node instead of the NodeJS plugin. |

---

## 8. Jenkins Build Commands & Server

| Command | Purpose | What It Does Under the Hood | When to Use |
| :--- | :--- | :--- | :--- |
| `call node -v` | Log Node Version in Build | Prints the Node version used by the build; `call` returns control to the batch script afterward. | First line of the build step to confirm `v24.21.0` in Console Output. |
| `call npm ci` | Install Locked Dependencies | Runs `npm ci` from the batch step. Without `call`, the batch script exits after the npm command because `npm` is itself a `.cmd` script. | Every Jenkins build (use `call npm install` if no lockfile is committed). |
| `call npx playwright install` | Install Browsers for Jenkins Account | Downloads browsers into the profile of the account running Jenkins. | Every Jenkins build; a no-op when browsers are already present. |
| `call npx playwright test` | Run Tests Headless | Executes the suite headlessly; a non-zero exit code marks the build as failed. | Main test step for the headless job. |
| `call npx playwright test --headed --workers=1` | Run Tests Headed | Forces visible browsers, one test at a time. | Build step for the headed job (Section 9). |
| `ren Jenkinsfile.txt Jenkinsfile` | Remove Windows `.txt` Suffix | Invokes Windows shell renaming to strip the `.txt` extension from the filename. | When Windows text editors save `Jenkinsfile` with an unwanted extension. |
| `type Jenkinsfile` | Read File to Console | Streams the raw contents of `Jenkinsfile` to the CMD terminal. | To verify pipeline logic and stages directly from the command prompt. |
| `mvn clean` | Purge Target Artifacts | Triggers the `maven-clean-plugin` to delete the `target/` directory and compiled `.class` files. | For Maven/Java projects, before committing, to keep build artifacts out of source control. |
| `services.msc` | Check for a Jenkins Windows Service | Opens the Windows Services console; look for a service named **Jenkins**. | To find out whether Jenkins runs as a service (no desktop, no visible browsers) or from CMD. |
| `"%JAVA_HOME%\bin\java" -jar jenkins.war` | Run Jenkins Standalone | Launches Jenkins with its embedded Winstone (Jetty-based) servlet container on port 8080, using the JDK that `JAVA_HOME` points to. Runs inside your desktop session and uses `%USERPROFILE%\.jenkins` as its home. Keep the CMD window open. | When starting Jenkins manually instead of as a service; required for visible headed runs. |
| `System.setProperty("hudson.model.DirectoryBrowserSupport.CSP", "")` | Allow Playwright Report Scripts | Relaxes Jenkins' Content-Security-Policy so the HTML report's JavaScript and CSS load. Run in **Manage Jenkins → Script Console**; resets on restart. | When the Playwright Report opens blank or unstyled. Relaxing CSP reduces protection, so use it on trusted local instances only. |

**Headless Freestyle build step (`playwright-tests`):**

```bat
call node -v
call npm ci
call npx playwright install
call npx playwright test
```

**Headed Freestyle build step (`PlaywrightBasic1-Headed`):**

```bat
call node -v
call npm ci
call npx playwright install
call npx playwright test --headed --workers=1
```

**Expected successful Console Output:** Git checkout of the latest commit (e.g., `c5fc942`), `v24.21.0`, npm and Playwright install output, `9 passed`, `Finished: SUCCESS`. "Running as SYSTEM" at the top refers to the Jenkins user that started the build, not the Windows account.

---

## 9. Headed Freestyle Job — Step by Step

Headed mode in Jenkins works only if Jenkins runs **inside your logged-in desktop session**. A Windows service has no desktop, so browsers would open invisibly.

| Step | Where | Action |
| :--- | :--- | :--- |
| 1. Stop the service | `Win + R` → `services.msc` | If a **Jenkins** service exists: right-click → **Stop**, then Properties → Startup type **Manual**. If none exists, skip. |
| 2. Start Jenkins from CMD | CMD | `cd /d <folder containing jenkins.war>` then `"%JAVA_HOME%\bin\java" -jar jenkins.war`. Keep the window open; jobs and settings remain in `C:\Users\shiva\.jenkins`. |
| 3. Create the job | Dashboard → New Item | Name `PlaywrightBasic1-Headed` → **Freestyle project** → OK. |
| 4. Source code | Source Code Management → Git | URL `https://github.com/ShivamPandit1213/PlaywrightBasic1.git`; Credentials `- none -`; Branch `*/main`. |
| 5. Node | Environment | ☑ Provide Node & npm bin/ folder to PATH → `Node24`. |
| 6. Build step | Build Steps → Execute Windows batch command | Paste the headed build step from Section 8. |
| 7. Report | Post-build Actions → Publish HTML reports | `playwright-report` / `index.html` / `Playwright Report`. |
| 8. Run | Save → Build Now | Browser windows open on your desktop; check Console Output for `9 passed` and `Finished: SUCCESS`. |

**Tips:** keep the PC unlocked during headed runs, since a locked screen can break headed browsers. Keep the headless job for regular runs and use the headed job only to watch tests. For demos, use `slowMo` in the config or `--debug` locally, not in Jenkins.

**Alternative:** instead of a second job, parameterize one job with a `HEADLESS` Choice Parameter (Section 7) and keep `call npx playwright test` as the build step; this requires the config change in Section 6.

---

## 10. Jenkinsfile — Pipeline Alternative (Windows Agent)

Create a **Pipeline** job → Definition: *Pipeline script from SCM* → Git URL and branch as in Section 7 → Script Path: `Jenkinsfile`. Commit this file to the repository root:

```groovy
pipeline {
    agent any
    tools { nodejs 'Node24' }
    stages {
        stage('Install') {
            steps {
                bat 'node -v'
                bat 'npm ci'
                bat 'npx playwright install'
            }
        }
        stage('Test') {
            steps {
                bat 'npx playwright test'
            }
        }
    }
    post {
        always {
            publishHTML(target: [
                reportDir: 'playwright-report',
                reportFiles: 'index.html',
                reportName: 'Playwright Report',
                keepAll: true,
                alwaysLinkToLastBuild: true,
                allowMissing: true
            ])
        }
    }
}
```

On a Linux agent, replace `bat` with `sh` and use `npx playwright install --with-deps`. Inside a Jenkinsfile, `bat` steps do not need `call`. For a headed pipeline, change the test step to `bat 'npx playwright test --headed --workers=1'` (same desktop-session requirement as Section 9).

---

## 11. Recommended `.gitignore` (Playwright Project)

```
node_modules/
test-results/
playwright-report/
blob-report/
playwright/.cache/
```

Always commit `package.json` and `package-lock.json`. Never commit `node_modules/`. If these folders do not appear in `git status` after a test run, `.gitignore` is working.

**GitHub Actions note:** a `.github/workflows/playwright.yml` file (created by Playwright setup) also runs the tests on GitHub after every push; check the repository's **Actions** tab. Keep it alongside Jenkins, or delete the `.github` folder to use Jenkins only.

---

## 12. Troubleshooting Quick Reference

| Symptom / Error | Cause | Fix |
| :--- | :--- | :--- |
| `Parsing POMs` → `ERROR: No such file ...\pom.xml` | The Jenkins job was created as a **Maven project**. | Create a new **Freestyle** job (Section 7); the job type cannot be changed. Delete the old Maven job if you want to reuse its name. |
| `fatal: 'origin' does not appear to be a git repository` | The remote was removed (`git remote remove origin`). | `git remote add origin https://github.com/ShivamPandit1213/PlaywrightBasic1.git`, then `git push -u origin main`. |
| `nothing to commit, working tree clean` | No file was changed and saved (switching Node or Jenkins settings changes nothing in the repo). | Edit and save the file, confirm with `git diff --stat`, then commit. |
| `Everything up-to-date` on push | No new local commits. | Normal if you already pushed; commit first if you expected changes. |
| `LF will be replaced by CRLF` | Windows line-ending conversion. | Harmless; ignore, or set `core.autocrlf true`. |
| `'nade' is not recognized ...` (or any typo) | Mistyped command. | Retype it (`node -v`). |
| `package.json` syntax error | Missing or extra comma after editing. | Fix the JSON; validate with the `node -e "require('./package.json')..."` command. |
| `npm ci` fails: lockfile out of sync | `package.json` was edited without running `npm install`, or only one of the two files was committed. | Run `npm install`, then commit **both** `package.json` and `package-lock.json`. |
| `npm ci` fails: requires `package-lock.json` | Lockfile not committed. | Commit `package-lock.json` (`git ls-files package-lock.json` to confirm), or use `call npm install`. |
| `'npx' is not recognized` (Jenkins) | Node is not on the Jenkins build PATH. | Enable *Provide Node & npm bin/ folder to PATH*, or set `PATH+NODE`. |
| Build stops after the first `npm` line | Batch step is missing `call`. | Prefix every npm/npx line with `call`. |
| Playwright version changed after `npm install` | `package.json` uses a range such as `^1.59.1`. | Expected; commit the updated lockfile so Jenkins uses the same version via `npm ci`. |
| `Executable doesn't exist at ...ms-playwright...` | Browsers not installed for the account running Jenkins. | Keep `call npx playwright install` in the build step. |
| `--with-deps` fails on Windows | Flag installs Linux system packages only. | Use `npx playwright install` without `--with-deps`. |
| **Publish HTML reports** missing from Post-build Actions | HTML Publisher plugin not installed. | Install it from Manage Jenkins → Plugins. |
| Playwright Report opens blank | Jenkins CSP blocks the report's scripts. | Run the `System.setProperty(...CSP...)` line in Script Console (Section 8). |
| `HEADLESS=true/false` has no effect | Config does not read `process.env.HEADLESS`, a project overrides `headless`, or tests were started with `npx playwright test` (which never sets it). | Add `headless: process.env.HEADLESS !== 'false'` to the config; use `npm run test:headed`/`test:headless`, the Jenkins `HEADLESS` parameter, or `--headed`. |
| No browser window appears in Jenkins | Jenkins runs as a Windows service (no desktop). | For headed runs, stop the service and start Jenkins from CMD (Section 9). For regular runs, headless is expected. |
| Tests fail only in headed mode | Screen locked, or too many parallel windows. | Keep the PC unlocked; use `--workers=1`. |
| `EBUSY` / `EPERM` / file-locked errors locally | OneDrive syncing `node_modules`. | Move the project to a non-synced folder (e.g., `C:\Projects\PlaywrightBasic1`). |
| Tests pass locally, fail in Jenkins | Different Node/Playwright versions or headed-only config. | Match Node 24 in both places, use `npm ci`, run headless. |

---

## 13. Standard Daily Workflow

```bat
cd /d C:\Users\shiva\OneDrive\JavaSelenium\PlaywrightBasic1
git fetch origin
git status
git pull --no-rebase origin main
npx playwright test
git diff --stat
git add .
git commit -m "Describe the change"
git push
```

Then open `http://localhost:8080` → `playwright-tests` (headless) or `PlaywrightBasic1-Headed` (headed) → **Build Now** (or **Build with Parameters**) → **Console Output** → **Playwright Report**.
