# PAIMANA — Complete Git, Jenkins, Node.js & Playwright Operational Reference

A comprehensive, tabular cheat sheet mapping every command to its exact function, internal mechanism, and situational trigger. Written for **Windows CMD**, a local **Jenkins** server, and a **Node.js Playwright** project.

**Project reference values used below**

| Item | Value |
| :--- | :--- |
| Local project path | `C:\Users\shiva\OneDrive\JavaSelenium\PlaywrightBasic1` |
| GitHub repository | `https://github.com/ShivamPandit1213/PlaywrightBasic1.git` |
| Default branch | `main` |
| Node.js (local and Jenkins) | `v24.21.0` (LTS) |
| npm | `11.19.0` |
| Playwright | `1.63.0` |
| Jenkins URL | `http://localhost:8080` |
| Jenkins NodeJS tool name | `Node24` |
| Jenkins job name | `playwright-tests` (Freestyle) |

---

## 1. Network, DNS & Diagnostics

| Command | Purpose | What It Does Under the Hood | When to Use |
| :--- | :--- | :--- | :--- |
| `ping github.com` | Verify GitHub Reachability | Sends ICMP echo request packets to test routing and name resolution. Note: GitHub may drop ICMP, so timeouts do not always mean GitHub is down. | When Git operations hang or fail with connection timeouts. |
| `curl -I https://github.com` | Verify HTTPS Reachability | Sends an HTTP HEAD request over port 443 (the same path Git uses) and prints the response headers. | When `ping` is inconclusive; a `200` or `301` response confirms HTTPS access works. |
| `ipconfig /flushdns` | Reset DNS Resolver Cache | Clears and refreshes the Windows local DNS name resolution cache. | When receiving `fatal: unable to access ... Could not resolve host: github.com`. |
| `git config --global --get http.proxy` | Inspect Git Proxy | Reads the global `.gitconfig` file and outputs the currently configured proxy address. | When troubleshooting connection issues in corporate or VPN environments. |
| `git config --global --unset http.proxy` | Remove Git Proxy | Deletes the `http.proxy` entry from the global Git configuration. | When switching off an office proxy or fixing invalid proxy routing. |
| `git status` | Inspect Working Tree | Compares the working tree against the index (staging area), the `HEAD` commit, and the upstream branch (ahead/behind count). | Run before and after every staging, committing, pulling, or pushing operation. |
| `git remote -v` | Verify Remote URLs | Prints the fetch/push URLs associated with each remote alias (e.g., `origin`). Empty output means no remote is configured. | Run to confirm the repository is pointing to the correct GitHub URL. |
| `git rev-parse --show-toplevel` | Identify Repository Root | Traverses upward and outputs the absolute path where the active `.git` directory lives. | Run to verify a subproject is completely isolated from any parent folder `.git`. |
| `git log --oneline` | View Concise History | Traverses the commit graph from `HEAD` backward, printing each commit hash and subject line. | Run to confirm local commits were created or to locate hashes for rollbacks. |
| `git log origin/main..main --oneline` | List Unpushed Commits | Shows commits reachable from local `main` but not from the remote-tracking branch `origin/main`. Run `git fetch origin` first. | Run before pushing to see exactly what will be uploaded. Empty output means nothing to push. |
| `git diff` | View Unstaged Changes | Generates a patch representation of modifications in the working directory not yet staged. | Run before `git add` to review line-by-line modifications. |
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
| `git remote remove origin` | Unlink Remote Repository | Deletes the `origin` reference entry from `.git/config`. After this, `git push`/`git pull` fail until a remote is added again. | Only when the repository must be detached from GitHub or re-pointed from scratch. |
| `git branch -M main` | Set Default Branch Name | Renames the current HEAD branch reference to `main`. | Prior to initial push to standardize branch naming with GitHub conventions. |
| `git push -u origin main` | Initial Push & Set Tracking | Transfers packfiles to GitHub and writes `branch.main.remote` and `branch.main.merge` to `.git/config`. | On the first push of a new repository or branch, or after re-adding a removed remote. |
| `git push` | Push Tracked Commits | Uploads local commits on the active branch to its pre-configured upstream branch. | For all standard daily uploads after upstream tracking has been configured. |

---

## 3. Staging, Commits & Rollbacks

| Command | Purpose | What It Does Under the Hood | When to Use |
| :--- | :--- | :--- | :--- |
| `git add .` | Stage All Modifications | Adds all new, modified, and deleted working tree files (respecting `.gitignore`) to the index. | When bundling all project changes into the next commit snapshot. |
| `git add "filename"` | Stage Specific File | Stages only the designated file path (double quotes protect paths containing spaces). | When committing specific files (e.g., `Jenkinsfile`, `package.json`, `package-lock.json`). |
| `git commit -m "message"` | Create Commit Object | Packages the current staging area into a tree object, writes metadata, and advances `HEAD`. Reports "nothing to commit" if the index matches `HEAD`. | Directly following `git add` to record a permanent change point. |
| `git rm -r --cached <folder>` | Untrack Folder from Git | Removes the directory tree from the Git index while leaving all physical files on disk. | When build or dependency folders (`node_modules/`, `target/`, `.vscode/`, `playwright-report/`) were tracked by accident. |
| `git reset --soft HEAD~1` | Undo Previous Commit | Moves the `HEAD` and branch reference back one commit while leaving all changes staged. | When a commit was made prematurely or needs an updated message (only if not yet pushed). |
| `git revert HEAD` | Safely Revert Public Commit | Appends a brand-new commit that applies the exact inverse diff of the `HEAD` commit. | When an erroneous commit has already been pushed to a remote GitHub branch. |
| `git checkout -- .` | Discard Uncommitted Edits | Overwrites modified tracked files in the working directory with the copies from the index. Modern equivalent: `git restore .` | When local experimental changes need to be discarded completely. |

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

## 5. Node.js & npm Environment

| Command | Purpose | What It Does Under the Hood | When to Use |
| :--- | :--- | :--- | :--- |
| `node -v` | Check Node.js Version | Prints the version of the `node.exe` found first in `PATH`. | After installing or switching Node; expected output: `v24.x.x` (LTS). |
| `npm -v` | Check npm Version | Prints the version of the npm CLI bundled with the active Node install. | Alongside `node -v` to confirm the toolchain. |
| `npx -v` | Check npx Version | Prints the npx version (shipped with npm, so it matches the npm version). | To confirm `npx` is available before running Playwright. |
| `where node` | Locate Node Executable | Searches every folder in `PATH` and lists each `node.exe` found (usually `C:\Program Files\nodejs`). | To find the install path for Jenkins `PATH+NODE`, or to detect duplicate installs. |
| `nvm install 24` | Install Node 24 via nvm-windows | Downloads the latest Node 24.x into the nvm directory without touching other versions. | When keeping several Node versions side by side (requires nvm-windows installed). |
| `nvm use 24` | Switch Active Node Version | Repoints the `nodejs` symlink in `PATH` to the chosen version (run CMD as Administrator). | To switch between Node versions (e.g., `nvm use 25` to go back). |
| `nvm list` | List Installed Node Versions | Prints every Node version managed by nvm and marks the active one. | To confirm which versions are installed. |
| `rmdir /s /q node_modules` | Delete Installed Dependencies | Recursively and silently deletes the `node_modules` folder. | After changing Node versions or when dependencies are corrupted. |
| `npm install` | Install / Update Dependencies | Resolves versions from `package.json` ranges (e.g., `^1.59.1` can resolve to `1.63.0`), installs them, and creates or updates `package-lock.json`. | Locally, when adding packages or when no lockfile exists yet. Commit the resulting `package-lock.json`. |
| `npm ci` | Clean, Reproducible Install | Deletes `node_modules` and installs exactly the versions in `package-lock.json`; fails if the lockfile is missing or out of sync with `package.json`. | In Jenkins/CI builds, so every run uses identical dependency versions. |

**Node version rule:** even-numbered releases (22, 24, 26) become **LTS**; odd-numbered releases (21, 23, 25) are short-lived "Current" versions. Use LTS for Jenkins. Node 26 becomes LTS in October 2026; until then, use **24.x**.

**Installing Node 24 LTS locally (Windows):** Settings → Apps → Installed apps → uninstall Node.js → download the LTS `.msi` (64-bit) from https://nodejs.org → install with defaults → open a **new** CMD window → run `node -v`.

---

## 6. Playwright

| Command | Purpose | What It Does Under the Hood | When to Use |
| :--- | :--- | :--- | :--- |
| `npx playwright --version` | Check Playwright Version | Runs the locally installed `@playwright/test` CLI from `node_modules` and prints its version. | After `npm install` / `npm ci` to confirm the version (e.g., `1.63.0`). |
| `npx playwright install` | Download Browsers | Downloads Chromium, Firefox, and WebKit builds matching the installed Playwright version into `%USERPROFILE%\AppData\Local\ms-playwright`. Prints nothing if they are already present. | After installing or upgrading Playwright, and in every Jenkins build (the Jenkins service account has its own profile). |
| `npx playwright install --with-deps` | Download Browsers + OS Libraries | Also installs required system packages via the Linux package manager. | **Linux agents only**; do not use on Windows. |
| `npx playwright test` | Run All Tests | Reads `playwright.config`, discovers test files, runs them (headless by default), and writes `playwright-report/` and `test-results/`. | Locally and as the main Jenkins test step. |
| `npx playwright test --headed` | Run With Visible Browser | Launches browsers in headed mode. | Local debugging only; a Jenkins Windows service cannot show browser windows. |
| `npx playwright test --project=chromium` | Run One Browser Project | Limits the run to the named project defined in `playwright.config`. | For faster runs or isolating browser-specific failures. |
| `npx playwright show-report` | Open HTML Report Locally | Starts a local web server and opens `playwright-report/index.html` in the browser. | After a local run to inspect results, traces, and screenshots. |

---

## 7. Jenkins Setup (UI Configuration)

| Area | Location in Jenkins | Setting / Value | Purpose |
| :--- | :--- | :--- | :--- |
| Plugins | Manage Jenkins → Plugins → Available plugins | Install **NodeJS**, **HTML Publisher**, **Git** | Run npm/npx, publish the Playwright HTML report, clone from GitHub. |
| Node tool | Manage Jenkins → Tools → NodeJS installations → Add NodeJS | Name: `Node24`; ☑ Install automatically; Version: **NodeJS 24.21.0** | Jenkins downloads and manages its own Node LTS, independent of the local install. Hovering over another version in the dropdown does not select it; confirm the box shows 24.21.0 before saving. |
| New job | Dashboard → New Item | Name: `playwright-tests`; type: **Freestyle project** (or **Pipeline**) | Separate job needed because Maven jobs only run `mvn` goals. |
| Source code | Job → Configure → Source Code Management → Git | URL: `https://github.com/ShivamPandit1213/PlaywrightBasic1.git`; Branch: `*/main`; credentials: GitHub username + Personal Access Token (private repos) | Jenkins clones its own copy of the repository for each build. |
| Triggers (optional) | Job → Configure → Build Triggers | Build periodically `H 2 * * *`; Poll SCM `H/15 * * * *`; or GitHub hook trigger (needs a webhook) | Run automatically on a schedule or on push. |
| Node on PATH | Job → Configure → Build Environment | ☑ Provide Node & npm bin/ folder to PATH → `Node24` | Makes `node`, `npm`, and `npx` available to the build step. |
| Build step | Job → Configure → Build Steps → Execute Windows batch command | See Section 8 batch script | Installs dependencies and browsers, then runs tests. |
| Report | Job → Configure → Post-build Actions → Publish HTML reports | Directory: `playwright-report`; Index page: `index.html`; Title: `Playwright Report` | Adds a **Playwright Report** link to the job page. |
| Artifacts (optional) | Job → Configure → Post-build Actions → Archive the artifacts | `playwright-report/**, test-results/**` | Keeps reports, traces, and screenshots per build. |
| Alternative Node PATH | Manage Jenkins → System → Global properties → ☑ Environment variables | Name: `PATH+NODE`; Value: `C:\Program Files\nodejs` | Uses the locally installed Node instead of the NodeJS plugin. |

---

## 8. Jenkins Build Commands & Server

| Command | Purpose | What It Does Under the Hood | When to Use |
| :--- | :--- | :--- | :--- |
| `call node -v` | Log Node Version in Build | Prints the Node version used by the build; `call` returns control to the batch script afterward. | First line of the build step to confirm `v24.21.0` in Console Output. |
| `call npm ci` | Install Locked Dependencies | Runs `npm ci` from the batch step. Without `call`, the batch script exits after the npm command because `npm` is itself a `.cmd` script. | Every Jenkins build (use `call npm install` if no lockfile is committed). |
| `call npx playwright install` | Install Browsers for Jenkins Account | Downloads browsers into the profile of the account running Jenkins (often Local System), not into `C:\Users\shiva`. | Every Jenkins build; a no-op when browsers are already present. |
| `call npx playwright test` | Run Tests in Jenkins | Executes the suite headlessly; a non-zero exit code marks the build as failed. | Main test step. |
| `ren Jenkinsfile.txt Jenkinsfile` | Remove Windows `.txt` Suffix | Invokes Windows shell renaming to strip the `.txt` extension from the filename. | When Windows text editors save `Jenkinsfile` with an unwanted extension. |
| `type Jenkinsfile` | Read File to Console | Streams the raw contents of `Jenkinsfile` to the CMD terminal. | To verify pipeline logic and stages directly from the command prompt. |
| `mvn clean` | Purge Target Artifacts | Triggers the `maven-clean-plugin` to delete the `target/` directory and compiled `.class` files. | For Maven/Java projects, before committing, to keep build artifacts out of source control. |
| `"%JAVA_HOME%\bin\java" -jar jenkins.war` | Run Jenkins Standalone | Launches Jenkins with its embedded Winstone (Jetty-based) servlet container on port 8080, using the JDK that `JAVA_HOME` points to. The JDK must be a version supported by your Jenkins release. | When starting a local Jenkins instance manually instead of as a Windows service. |
| `System.setProperty("hudson.model.DirectoryBrowserSupport.CSP", "")` | Allow Playwright Report Scripts | Relaxes Jenkins' Content-Security-Policy so the HTML report's JavaScript and CSS load. Run in **Manage Jenkins → Script Console**; resets on restart. | When the Playwright Report opens blank or unstyled. Relaxing CSP reduces protection, so use it on trusted local instances only. |

**Standard Freestyle batch build step (Windows):**

```bat
call node -v
call npm ci
call npx playwright install
call npx playwright test
```

---

## 9. Jenkinsfile — Pipeline Alternative (Windows Agent)

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

On a Linux agent, replace `bat` with `sh` and use `npx playwright install --with-deps`. Inside a Jenkinsfile, `bat` steps do not need `call`.

---

## 10. Recommended `.gitignore` (Playwright Project)

```
node_modules/
test-results/
playwright-report/
blob-report/
playwright/.cache/
```

Always commit `package.json` and `package-lock.json`. Never commit `node_modules/`.

---

## 11. Troubleshooting Quick Reference

| Symptom / Error | Cause | Fix |
| :--- | :--- | :--- |
| `fatal: 'origin' does not appear to be a git repository` | The remote was removed (`git remote remove origin`). | `git remote add origin https://github.com/ShivamPandit1213/PlaywrightBasic1.git`, then `git push -u origin main`. |
| `nothing to commit, working tree clean` | Files already match the last commit. | Normal. Check `git fetch origin` + `git status` to see if a push is still needed. |
| `LF will be replaced by CRLF` | Windows line-ending conversion. | Harmless; ignore, or set `core.autocrlf true`. |
| `'npx' is not recognized` (Jenkins) | Node is not on the Jenkins build PATH. | Enable *Provide Node & npm bin/ folder to PATH*, or set `PATH+NODE`. |
| Build stops after the first `npm` line | Batch step is missing `call`. | Prefix every npm/npx line with `call`. |
| `npm ci` fails: requires `package-lock.json` | Lockfile not committed or out of sync. | Commit `package-lock.json` (`git ls-files package-lock.json` to confirm), or use `call npm install`. |
| Playwright version changed after `npm install` | `package.json` uses a range such as `^1.59.1`. | Expected; commit the updated lockfile so Jenkins uses the same version via `npm ci`. |
| `Executable doesn't exist at ...ms-playwright...` | Browsers not installed for the account running Jenkins. | Keep `call npx playwright install` in the build step. |
| `--with-deps` fails on Windows | Flag installs Linux system packages only. | Use `npx playwright install` without `--with-deps`. |
| Playwright Report opens blank | Jenkins CSP blocks the report's scripts. | Run the `System.setProperty(...CSP...)` line in Script Console (Section 8). |
| No browser window appears in Jenkins | Jenkins service runs in a non-interactive session. | Expected; keep `headless: true` (default) in `playwright.config`. |
| `EBUSY` / `EPERM` / file-locked errors locally | OneDrive syncing `node_modules`. | Move the project to a non-synced folder (e.g., `C:\Projects\PlaywrightBasic1`). |
| Tests pass locally, fail in Jenkins | Different Node/Playwright versions or headed-only config. | Match Node 24 in both places, use `npm ci`, run headless. |

---

## 12. Standard Daily Workflow

```bat
cd /d C:\Users\shiva\OneDrive\JavaSelenium\PlaywrightBasic1
git fetch origin
git status
git pull --no-rebase origin main
npx playwright test
git add .
git commit -m "Describe the change"
git push
```

Then open `http://localhost:8080` → `playwright-tests` → **Build Now** → **Console Output** → **Playwright Report**.
