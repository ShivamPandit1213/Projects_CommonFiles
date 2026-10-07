# PAIMANA — Jenkins, Git & Automation Command Reference

A single reference for the local toolchain: Jenkins startup, Git repository setup and
recovery, remote management, and the Playwright test CLI.

Commands assume **Windows `cmd`** unless marked otherwise. In `cmd`, comments start with
`::` at the beginning of a line; `#` is **not** a comment and is passed to the command.
Examples use the repositories `ShivamPandit1213/PAIMANA_Dev` and
`ShivamPandit1213/PAIMANA_PlaywrightMavenJavaSelenium`.

---

## Table of Contents

**Environment**

1. [Version Compatibility](#1-version-compatibility)
2. [Jenkins](#2-jenkins)

**Git — Setup & Daily Use**

3. [Creating a New GitHub Repository](#3-creating-a-new-github-repository)
4. [Full Setup — Every New Project](#4-full-setup--every-new-project)
5. [Verification Commands](#5-verification-commands)
6. [Day-to-Day Workflow](#6-day-to-day-workflow)
7. [Quick Reference — Command Purpose Table](#7-quick-reference--command-purpose-table)
8. [Inspecting Repository History](#8-inspecting-repository-history)
9. [Staging & Atomic Commits](#9-staging--atomic-commits)
10. [Managing Remotes](#10-managing-remotes)

**Git — Undoing & Recovery**

11. [Undoing & Reverting](#11-undoing--reverting)
12. [Working Tree & Cleanup](#12-working-tree--cleanup)
13. [Emergency Recovery (`git reflog`)](#13-emergency-recovery-git-reflog)
14. [Troubleshooting — Hijacked Parent Remote](#14-troubleshooting--hijacked-parent-remote)
15. [Deleting a GitHub Repository](#15-deleting-a-github-repository)
16. [Recovery Path — Wrong Content in History](#16-recovery-path--wrong-content-in-history)
17. [Isolating One Project from a Shared Parent Folder](#17-isolating-one-project-from-a-shared-parent-folder)

**Test Automation CLI**

18. [Playwright CLI](#18-playwright-cli)
19. [Playwright Projects](#19-playwright-projects) — Paimana_Dev, PlaywrightBasic1, PAIMANA_Playwright_1.1
20. [Playwright Troubleshooting](#20-playwright-troubleshooting)
21. [Everyday Commands](#21-everyday-commands)

Every section ends with a **⬆ Back to top** link.

---

## 1. Version Compatibility

Jenkins is the binding constraint on JDK choice — everything else in the stack
accepts a wider range, so pin to what Jenkins supports.

| Tool | Supported JDK | Note |
|---|---|---|
| **Jenkins** | **17, 21 only** | The binding constraint — pick the JDK here first |
| Appium | 11+ | Any recent version, but use JDK 21 for Jenkins compatibility |
| Selenium | 11+ | — |
| TestNG | 11+ | — |

**Practical rule:** install **JDK 21** and point everything at it. That satisfies
Jenkins and every other tool in the list simultaneously.

[⬆ Back to top](#table-of-contents)

---

## 2. Jenkins

### Start Jenkins with JDK 21

**start-jenkins.bat**

```bat
@echo off
:: Hide the commands themselves; show only messages and Jenkins output
echo Starting Jenkins with JDK 21...

:: Use JDK 21 for this window only (Jenkins supports 17 and 21)
set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.12"

:: Go to the folder holding jenkins.war (/d also switches drive if needed)
cd /d C:\Users\shiva\OneDrive\Jenkins

:: Start Jenkins on http://localhost:8080/ (first launch prints the admin password)
"%JAVA_HOME%\bin\java" -jar jenkins.war

:: Keep the window open after Jenkins stops, so you can read any error
pause
```

Double-click to run. Watch the console for the initial admin password on first launch.

### Start Jenkins so Playwright HTML reports display

Jenkins blocks JavaScript in archived HTML, so the published Playwright report opens as a
blank page. For a personal Jenkins, start it with the Content Security Policy relaxed:

```bat
:: Same start command, plus CSP="" so published HTML reports may run JavaScript
"%JAVA_HOME%\bin\java" -Dhudson.model.DirectoryBrowserSupport.CSP="" -jar jenkins.war
```

Replace the `java` line in `start-jenkins.bat` with this one. On a shared Jenkins, prefer a
Resource Root URL instead (see `JenkinsJobTypesSetup.md`, Part E.4).

### Fixing `Jenkinsfile.txt` → `Jenkinsfile`

Windows often saves `Jenkinsfile` as `Jenkinsfile.txt`. Jenkins requires the exact
filename with no extension.

```cmd
:: Go to the project that holds the Jenkinsfile
cd C:\Users\shiva\OneDrive\JavaSelenium\paimana_1point1

:: Rename: remove the hidden .txt extension
ren Jenkinsfile.txt Jenkinsfile

:: VERIFY: must list "Jenkinsfile" with nothing after it
dir Jenkinsfile
```

`dir` should show `Jenkinsfile` with nothing after it — confirms the rename worked.

View the file content directly in CMD:

```cmd
:: Print the file's content in the console
type Jenkinsfile
```

Commit it:

```cmd
:: Stage the renamed file
git add Jenkinsfile

:: Save it in a commit
git commit -m "Add Jenkinsfile"

:: Upload, so Jenkins can read it from GitHub
git push
```

> **Tip:** turn on **File name extensions** in File Explorer (View tab). Without it,
> `Jenkinsfile.txt` displays as `Jenkinsfile` and the problem is invisible.

[⬆ Back to top](#table-of-contents)

---

## 3. Creating a New GitHub Repository

Go to **github.com → New repository**

| Field | Value |
|---|---|
| Repository name | `paimana_1point1` |
| Visibility | **Private** |
| Add README | Off |
| Add .gitignore | None |
| Add license | None |

Leave README / .gitignore / license off whenever you already have local commits —
GitHub creating its own initial commit causes a conflict on first push.

[⬆ Back to top](#table-of-contents)

---

## 4. Full Setup — Every New Project

Run this exact sequence any time you start a new project and want it as its own GitHub repo.
Lines starting with `::` are comments; `cmd` skips them, so you can paste the whole block.

```cmd
:: 1. Enter the exact project folder, never a parent folder
cd <project-folder>

:: 2. Create a NEW, separate .git in this folder
git init

:: 3. VERIFY: .git exists here, not inherited from a parent folder
dir /a:h .git

:: 4. Stage only this project's files
git add .

:: 5. VERIFY: paths must read src/..., pom.xml, package.json with NO ../ prefix
::    If you see ../ you are in the wrong repo: stop and check step 1
git status

:: 6. Save the first commit locally
git commit -m "Initial commit"

:: 7. Link this repo to the EMPTY GitHub repo you created (Section 3)
git remote add origin <url-from-GitHub>

:: 8. Name the branch main (use master instead to match an older repo)
git branch -M main

:: 9. Upload the commit and set upstream tracking, so later a plain "git push" works
git push -u origin main
```

**Using `master` instead**, for consistency with an older repo, replace steps 8 and 9:

```cmd
:: 8. Name the branch master
git branch -M master

:: 9. Upload and set upstream tracking for master
git push -u origin master
```

Not sure which branch name you're on? This prints it, with `*` next to the current one:

```cmd
:: List local branches; * marks the current one
git branch
```

After step 9, run the checks in [Section 5](#5-verification-commands) to confirm
everything actually landed.

[⬆ Back to top](#table-of-contents)

---

## 5. Verification Commands

### Before every commit / push

```cmd
:: Lists files here: confirms you're in the right folder
dir

:: Prints the repo root: must be THIS project's path, not a parent's
git rev-parse --show-toplevel

:: Confirms a .git folder physically exists here
dir /a:h .git

:: Shows which GitHub URL this repo pushes to (prints nothing if not linked yet)
git remote -v

:: Shows the current branch name (main or master), marked with *
git branch

:: Shows staged / unstaged / untracked files: paths must have no ../
git status

:: Commit history, one line per commit
git log --oneline

:: Exact line changes not yet staged
git diff

:: Every file Git tracks: the ground truth of what a push will contain
git ls-files
```

### After every push

Don't assume a push worked just because no error appeared.

```cmd
:: Is a remote linked? Expect YOUR GitHub URL on both (fetch) and (push) lines,
:: not empty and not someone else's repo
git remote -v

:: Was the commit saved? Expect your commit message at the top
git log --oneline

:: Is upstream tracking set? Expect "Your branch is up to date with 'origin/main'"
git status

:: Did it actually land? Refresh the repo page in the browser:
:: the file list must match your project folder, with nothing from other projects
```

If `git remote -v` shows a URL that isn't yours, or `git status` doesn't mention
`origin/main`, **stop** — something is misconfigured and pushing further makes it worse.
See [Section 14](#14-troubleshooting--hijacked-parent-remote).

[⬆ Back to top](#table-of-contents)

---

## 6. Day-to-Day Workflow

Once `origin` and upstream tracking are set, every future change is three commands:

```cmd
:: Stage every change in this project
git add .

:: Save the staged changes as one commit, describing what changed
git commit -m "what changed"

:: Upload new commits to the branch this one tracks on GitHub
git push
```

No `-u origin main` needed again — upstream is already remembered.

A push uploads **commits** only. Uncommitted changes stay on your PC however often you
push, so always `git add` and `git commit` first.

### Push: which command, when

| Command | When | What happens |
|---|---|---|
| `git push` | Every day, once the branch is linked (after its first `-u` push). | Uploads new commits of the current branch. On an unlinked branch it stops with `fatal: The current branch main has no upstream branch` and uploads nothing. |
| `git push -u origin main` | The **first** push of a branch: a new repository, or a new local branch. | Uploads **and** links the branch (`-u` = `--set-upstream`). Once per branch, not per repository. |
| `git push --set-upstream origin main` | Same situation — the long spelling Git prints in its error. | Identical to `-u`. |
| `git push origin main` | A one-off push you don't want to link. | Uploads, but plain `git push` keeps failing. Prefer `-u` once. |
| `git push --dry-run` | Before a push you're unsure about. | Shows what would be uploaded; uploads nothing. |
| `git branch -vv` | To know whether plain `git push` will work. | Lists each branch with its upstream, e.g. `main ... [origin/main]`. No bracket = not linked → use `-u`. |
| `git config --global push.autoSetupRemote true` | Once per PC, to never type `-u`. | Plain `git push` links new branches automatically. |
| `git push --force-with-lease` | Only after rewriting already-pushed commits (`--amend`, rebase), on **your own** branch. | Replaces the remote branch but refuses if someone else pushed meanwhile. Never on a shared `main`; never plain `--force`. |

On the first push to a repository a browser sign-in window may open: use the account
that owns the repository.

### Useful extras

| Command | Purpose |
|---|---|
| `git status --short` | Compact status: `M` modified, `D` deleted, `??` new. |
| `git add -A` | Stage everything, including deletions. **Not** `git add ..` — two dots is the parent folder. |
| `git log --oneline -5` | Last five commits. |
| `git pull` | Bring down changes from GitHub. |

[⬆ Back to top](#table-of-contents)

---

## 7. Quick Reference — Command Purpose Table

| When | Command |
|---|---|
| Check current folder contents | `dir` |
| Enter a folder | `cd <path>` |
| Start a new repo here | `git init` |
| Confirm repo is isolated (not inherited) | `git rev-parse --show-toplevel` |
| Confirm `.git` physically exists | `dir /a:h .git` |
| Stage all changes | `git add .` |
| Stage one specific file | `git add "filename.ext"` |
| Stage multiple specific files | `git add "file1.ts" "file2.ts"` |
| Check what's staged / unstaged | `git status` |
| Commit staged changes | `git commit -m "message"` |
| Link to GitHub | `git remote add origin <url>` |
| Check which remote(s) are linked | `git remote -v` |
| Remove a remote | `git remote remove origin` |
| Rename branch | `git branch -M main` (or `master`) |
| Check current branch | `git branch` |
| Push and set tracking (first time) | `git push -u origin main` |
| Push (after tracking is set) | `git push` |
| View commit history | `git log --oneline` |
| View exact changes before commit | `git diff` |
| List all tracked files | `git ls-files` |
| Rename a file (Windows) | `ren OldName NewName` |
| View a file's content in CMD | `type filename` |

### Staging a single file — why quote the filename

Quotes matter whenever a filename has spaces or special characters — common in this
project set (e.g. `"Coolections and Arrays"`). Without quotes, CMD treats each
space-separated word as a *separate* argument and Git tries to stage files that don't exist.

```cmd
:: Simple name: quotes optional, but harmless
git add "Jenkinsfile"

:: Name with dots: quotes keep it as one argument
git add "playwright.config.js"

:: Full path: quotes protect any spaces in folder names
git add "src/main/java/com/paimana/pages/HomePage.java"
```

Quotes are optional for simple filenames with no spaces, but using them every time
removes the guesswork.

[⬆ Back to top](#table-of-contents)

---

## 8. Inspecting Repository History

| Command | Output / Action | When It Works | Limitations & Gotchas |
|---|---|---|---|
| `git status` | Branch tracking, staged files, unstaged changes, untracked files. | Always. | Does not display commit history. |
| `git log --oneline` | Condensed single-line log of commits reachable from the active branch. | Always. | Output can be long without flags like `-n <number>`. |
| `git log origin/main..HEAD --oneline` | Only local commits not yet pushed to the remote. | When tracking a remote branch. | Prints nothing if local and remote are in sync. Run `git fetch` first, or `origin/main` may be stale. |
| `git rev-list --count HEAD` | Total number of commits reachable from `HEAD`. | Always. | Includes commits merged in from other branches, not just ones authored here. |
| `git diff` | Exact line-level changes not yet staged. | Always. | Shows nothing once changes are staged — use `git diff --staged` for those. |
| `git ls-files` | Every file Git currently tracks. | Always. | Ground truth for what a push will contain. |

[⬆ Back to top](#table-of-contents)

---

## 9. Staging & Atomic Commits

Stage and commit individual files without capturing unrelated working directory changes:

```cmd
:: 1. Check current status
git status

:: 2. Unstage anything accidentally staged (modifications are preserved)
git restore --staged <file-path>

:: 3. Stage only the targeted file(s)
git add <file-path>

:: 4. Create an atomic commit
git commit -m "Your descriptive commit message"
```

One commit should describe one change. If the commit message needs the word "and",
it's usually two commits.

[⬆ Back to top](#table-of-contents)

---

## 10. Managing Remotes

### Add & connect

| Purpose | Command | Example |
|---|---|---|
| Add a remote | `git remote add <name> <url>` | `git remote add origin https://github.com/ShivamPandit1213/PAIMANA_Dev.git` |
| Add a second remote | `git remote add <name> <url>` | `git remote add backup https://github.com/ShivamPandit1213/PAIMANA_Backup.git` |
| Push and set upstream | `git push -u <remote> <branch>` | `git push -u origin master` |
| Push to a specific remote | `git push <remote> <branch>` | `git push backup master` |

### Inspect — offline (local config only)

| Purpose | Command | Example output |
|---|---|---|
| List remote names | `git remote` | `origin` |
| List names + URLs | `git remote -v` | `origin  https://github.com/ShivamPandit1213/PAIMANA_Dev.git (fetch)` |
| Count remotes | `git remote \| find /c /v ""` | `1` |
| All remote config keys | `git config --get-regexp "^remote\."` | `remote.origin.url https://github.com/...` |
| Filter full config | `git config --list \| find "remote"` | `branch.master.remote=origin` |
| Get one remote's URL | `git remote get-url <name>` | `https://github.com/ShivamPandit1213/PAIMANA_Dev.git` |
| Branch → upstream mapping | `git branch -vv` | `* master 56e865f [origin/master] Initial commit` |
| Which remote a branch tracks | `git config --get branch.<branch>.remote` | `origin` |

### Inspect — online (contacts the server)

| Purpose | Command | Example output |
|---|---|---|
| Full remote details | `git remote show <name>` | `HEAD branch: master` … `(local out of date)` |
| List server refs | `git ls-remote <name>` | `1142e4c…  refs/heads/master` |
| Branch heads only | `git ls-remote --heads <name>` | `1142e4c…  refs/heads/master` |
| Update stale tracking refs | `git remote update` | fetches all remotes |
| Drop deleted remote branches | `git remote prune <name>` | `* [pruned] origin/old-branch` |

### Modify

| Purpose | Command | Example |
|---|---|---|
| Rename a remote | `git remote rename <old> <new>` | `git remote rename backup mirror` |
| Change a remote's URL | `git remote set-url <name> <url>` | `git remote set-url mirror https://github.com/ShivamPandit1213/PAIMANA_Mirror.git` |
| Separate push URL | `git remote set-url --push <name> <url>` | `git remote set-url --push origin https://github.com/ShivamPandit1213/Fork.git` |

### Remove

| Purpose | Command | Note |
|---|---|---|
| Remove a remote | `git remote remove <name>` | `git remote remove mirror` |
| Same, older spelling | `git remote rm <name>` | Identical behaviour |
| Verify removal | `git remote -v` | Only `origin` remains |

### Re-pointing a repo at the correct URL

Used as a set whenever a repo is linked to the wrong GitHub URL.

```cmd
:: 1. Check what's linked: prints the (fetch) and (push) URL, or nothing if none
git remote -v

:: 2. Unlink that URL: local commits are untouched, GitHub is unchanged
git remote remove origin

:: 3. Link the correct GitHub repository
git remote add origin <correct-url>

:: VERIFY: must now show the correct URL
git remote -v
```

`git remote set-url origin <url>` does steps 2 and 3 in one command and is the better
choice when a remote named `origin` already exists.

Full walkthrough of *when* this is needed: [Section 14](#14-troubleshooting--hijacked-parent-remote).

### Key behaviours

- **`git remote add` never touches the network.** A wrong or non-existent URL is
  accepted silently and only fails on the first `push` or `fetch`.
- **`git remote -v` prints two lines per remote** (`fetch` and `push`).
  Two lines means *one* remote, not two.
- **`git remote remove` is local only.** It deletes the config entry and
  `refs/remotes/<name>/*`, and changes nothing on GitHub.
- **Removing `origin` orphans any branch tracking it.** A bare `git push` then fails
  with `No configured push destination` until you re-add it and push with `-u`.
- **Rename before remove.** After `git remote rename backup mirror`, the name `backup`
  no longer exists; `git remote remove backup` returns `error: No such remote: 'backup'`.

### Common errors

| Error | Cause | Fix |
|---|---|---|
| `error: remote origin already exists.` | `git remote add origin` run twice | Use `git remote set-url origin <url>` to change it |
| `remote: Repository not found.` | URL points at a repo that does not exist | Create it on GitHub, or correct the URL |
| `error: No such remote: '<name>'` | Remote was renamed or already removed | Check `git remote -v` for the current name |
| `src refspec master does not match any` | No commits exist yet on that branch | Commit first, then push |
| `fatal: 'origin' does not appear to be a git repository` | No remote named `origin` is configured | `git remote add origin <url>` |
| `Failed to connect to github.com port 443` | No network path to GitHub (network, VPN, firewall or proxy) | `Test-NetConnection github.com -Port 443` in PowerShell; if a proxy is needed, `git config --global http.proxy http://HOST:PORT` |
| `fatal: The current branch main has no upstream branch` | First push of this branch; it isn't linked yet. Committing doesn't change that. | `git push -u origin main` (once) |
| `Updates were rejected because the remote contains work that you do not have` | The GitHub repo was created with a README / .gitignore, or someone else pushed. | `git pull --rebase origin main`, then `git push` |
| `remote: Permission denied` / `403` on push | Windows saved a different GitHub login. | Control Panel → Credential Manager → Windows Credentials → remove the `github.com` entry, push again and sign in as the repository owner. |
| `fatal: ..: '..' is outside repository` | Typed `git add ..` (parent folder). | `git add .` or `git add -A` |
| `warning: ... LF will be replaced by CRLF` | Harmless line-ending conversion on Windows. | Ignore, or add `.gitattributes` with `* text=auto eol=lf`, run `git add --renormalize .` and commit. |
| Pushed, but GitHub shows old files | The changes were never committed. | `git status --short` → `git add -A` → `git commit` → `git push` |

### Worked example

```cmd
cd C:\Users\shiva\PAIMANA_Dev

:: --- Baseline ---
:: List remotes with their URLs (two lines = one remote: fetch + push)
git remote -v
:: Count remotes (prints a number, e.g. 1)
git remote | find /c /v ""

:: --- Connect a second remote ---
:: Add "backup" (no network check: a wrong URL only fails on push/fetch)
git remote add backup https://github.com/ShivamPandit1213/PAIMANA_Backup.git
:: VERIFY: origin and backup both listed
git remote -v

:: --- Inspect ---
:: Every remote.* setting from the local config (offline)
git config --get-regexp "^remote\."
:: Full details of origin from GitHub: HEAD branch, tracked branches (online)
git remote show origin
:: Branch heads on GitHub with their commit hashes (online)
git ls-remote --heads origin
:: Local branches with the remote branch each one tracks
git branch -vv

:: --- Modify ---
:: Rename backup to mirror (the name "backup" no longer exists afterwards)
git remote rename backup mirror
:: Point mirror at a different URL
git remote set-url mirror https://github.com/ShivamPandit1213/PAIMANA_Mirror.git
:: VERIFY: prints the new URL
git remote get-url mirror

:: --- Remove ---
:: Delete the mirror remote (local only; nothing changes on GitHub)
git remote remove mirror
:: VERIFY: only origin remains
git remote -v
```

[⬆ Back to top](#table-of-contents)

---

## 11. Undoing & Reverting

### Reset modes compared

| Mode | Action | State of modified files | Risk |
|---|---|---|---|
| `--soft` | Moves `HEAD` back to the target commit. | Kept in the staging area (green). | Low |
| `--mixed` *(default)* | Moves `HEAD` back to the target commit. | Kept in the working directory (red / unstaged). | Low |
| `--hard` | Moves `HEAD` back to the target commit. | Discarded. | **High** |

> **On `--hard`:** *committed* work is still recoverable via `git reflog`
> ([Section 13](#13-emergency-recovery-git-reflog)) until Git garbage-collects it.
> *Uncommitted* work is gone for good.

### Common undo scenarios

| Goal | Command | Gotchas |
|---|---|---|
| Undo last commit, keep files staged | `git reset --soft HEAD~1` | Changes are ready to recommit immediately. |
| Undo last commit, unstage files | `git reset HEAD~1` | Changes stay in the folder but must be re-added. |
| Erase the last N commits | `git reset --hard HEAD~N` | **Dangerous:** destroys all uncommitted work in the folder. |
| Overwrite local branch to match remote | `git fetch origin`, then `git reset --hard origin/<branch>` | Discards all local commits that have not been pushed. |
| Overwrite remote history | `git push origin <branch> --force-with-lease` | **Dangerous:** erases commits on the server. Never on a shared branch. |

> **Prefer `--force-with-lease` over `--force`.** It aborts the push if someone else
> updated the branch since your last fetch, so you can't silently overwrite their work.

### Safest option — revert with a new commit

Keeps history intact. Safe even if others have already pulled the bad commit, and the
only correct choice on a shared branch.

```cmd
:: Create a NEW commit that undoes the latest one (history is kept)
git revert HEAD

:: Upload the undo commit: safe on shared branches
git push
```

To revert a specific older commit rather than the latest:

```cmd
:: Find the hash of the commit to undo
git log --oneline

:: Create a new commit that undoes that one commit
git revert <commit-hash>

:: Upload the undo commit
git push
```

### Rewrite history — reset, then force-push

Only when certain nobody else has pulled the bad commits.

```cmd
:: Find the hash of the last GOOD commit
git log --oneline

:: DANGER: move the branch back there and discard everything after it,
:: including uncommitted changes in the folder
git reset --hard <commit-hash-to-go-back-to>

:: Overwrite GitHub's branch; refuses if someone else pushed meanwhile
git push --force-with-lease
```

### Remove files/folders from tracking without deleting them locally

Use when a commit accidentally included the wrong folder — e.g. a sibling project
nested inside. This is the most common cause of the "wrong content" problem in this doc.

```cmd
:: Stop tracking the folder/file; it stays on disk (-r = include subfolders)
git rm -r --cached <folder-or-file>

:: Record the removal
git commit -m "Remove folder from tracking"

:: Remove it from GitHub too (your local copy is untouched)
git push

:: Next: add the path to .gitignore, or "git add ." will pick it up again
```

Files remain on disk; Git simply stops tracking them. Add the path to `.gitignore`
afterwards or it will reappear as untracked on the next `git add .`.

[⬆ Back to top](#table-of-contents)

---

## 12. Working Tree & Cleanup

| Command | Action | Best use case | Risk |
|---|---|---|---|
| `git restore <file>` | Discards unstaged modifications in the working tree. | Resetting one modified file back to its last committed state. | **High:** erases uncommitted changes permanently. |
| `git restore .` | Same, for everything in the current folder. | Abandoning all local edits since the last commit. | **High** |
| `git clean -fd` | Deletes untracked files (`-f`) and directories (`-d`). | Wiping build output or stray generated files. | **High:** bypasses the Recycle Bin. |
| `git stash -u` | Shelves modified and untracked (`-u`) files. | Switching branches with half-finished work. | Low: restorable with `git stash pop`. |
| `git stash pop` | Reapplies the most recent stash and drops it. | Resuming work after returning to a branch. | Medium: can conflict if the branch moved. |

> **Dry-run first:** `git clean -nd` lists what *would* be deleted without deleting
> anything. `git clean` skips files matched by `.gitignore` unless you add `-x`.

> **If `git stash pop` hits a conflict**, the stash is *not* dropped — resolve the
> conflict, then remove it manually with `git stash drop`.

> **`git checkout -- .`** is the older spelling of `git restore .`. Both still work;
> `restore` is the current, clearer form.

[⬆ Back to top](#table-of-contents)

---

## 13. Emergency Recovery (`git reflog`)

If a commit is accidentally deleted — for example by a mistaken `git reset --hard` —
Git keeps an internal log of `HEAD` movements that acts as a safety net.

**1. View recent `HEAD` movements with their SHA hashes**

```cmd
:: List recent HEAD movements, newest first, each with its commit hash
git reflog
```

Example output:

```text
b080ba4 HEAD@{0}: reset: moving to HEAD~1
31e88d2 HEAD@{1}: commit: Refactor Jenkinsfile
f09dd0b HEAD@{2}: commit: update mvn version details
```

**2. Inspect a candidate before committing to it**

```cmd
:: Show that commit's message and changes, to confirm it's the right one
git show 31e88d2
```

**3. Restore the state from before the mistake**

```cmd
:: Move the branch back to that commit (discards uncommitted changes)
git reset --hard 31e88d2
```

**4. Push the recovered state** (see caveat below)

```cmd
:: Overwrite GitHub's branch with the recovered state (only your own branch)
git push origin <branch-name> --force-with-lease
```

**Caveats**

- The reflog is **local and per-clone**. It does not exist in a fresh clone, and
  unreachable commits are garbage-collected eventually (roughly 30 days by default).
- To look around a lost commit without moving your branch, check it out detached:
  `git checkout <commit-hash>`.
- Step 4 rewrites remote history if the branch was already pushed — same shared-branch
  caution as [Section 11](#11-undoing--reverting).

[⬆ Back to top](#table-of-contents)

---

## 14. Troubleshooting — Hijacked Parent Remote

**Symptom:** you run `git push` inside a project subfolder, but GitHub shows files from
*other* projects too — or `git status` inside your project shows paths prefixed with `../`.

**Cause:** the subfolder never had its own `.git`. Git walked up the directory tree,
found a `.git` in a *parent* folder, and every command has been operating on the parent
repo — which may contain many unrelated projects.

**Check first — is this your problem?**

```cmd
:: Go to the project that's misbehaving
cd <project-folder>

:: Prints the repo root: if it shows the PARENT folder, that's the problem
git rev-parse --show-toplevel
```

If this prints the *parent* folder's path instead of your project's own path, that's the cause.

**Fix — clean the parent, then give the project its own repo:**

```cmd
:: Go to the parent folder that owns the stray .git
cd <parent-folder>

:: See which GitHub URL the parent pushes to
git remote -v

:: Unlink it, so nothing can be pushed from the parent by accident
git remote remove origin

:: VERIFY: must print nothing now
git remote -v
```

`git remote -v` should now print **nothing** — confirms it's cleared. Then set the
project folder up properly using [Section 4](#4-full-setup--every-new-project).

[⬆ Back to top](#table-of-contents)

---

## 15. Deleting a GitHub Repository

Use when a repo was created by mistake or ended up with the wrong content.

1. Open the repo on GitHub → **Settings** (top nav).
2. Scroll to the bottom → **Danger Zone**.
3. Click **Delete this repository**.
4. Type the full name to confirm: `owner/repo-name`.
5. Click **I understand the consequences, delete this repository**.

There is no undo. Only delete a repo you're certain you want gone — if in doubt, make it
private instead.

[⬆ Back to top](#table-of-contents)

---

## 16. Recovery Path — Wrong Content in History

If a repo's commit *history* already contains the wrong files (rather than just the
working directory), delete-and-recreate is simpler than surgical fixes.

First, on github.com:

1. Delete the repo: **Settings → Danger Zone → Delete this repository** ([Section 15](#15-deleting-a-github-repository)).
2. Create a new **empty** repo with the same name: no README, .gitignore or license.

Then in `cmd`:

```cmd
:: 3. Go to the project folder
cd <project-folder>

:: 4. VERIFY: the folder has its own .git, separate from any parent
dir /a:h .git

:: 5. Stage the project's files
git add .

:: 6. VERIFY: no ../ paths may appear
git status

:: 7. Save the first commit
git commit -m "Initial commit"

:: 8. Link to the NEW empty repo
git remote add origin <new-repo-url>

:: 9. Name the branch (or master)
git branch -M main

:: 10. Upload and set upstream tracking
git push -u origin main
```

[⬆ Back to top](#table-of-contents)

---

## 17. Isolating One Project from a Shared Parent Folder

Use when a parent folder (e.g. `JavaSelenium`) has an old `.git` that every subfolder has
been inheriting from, and you want **only one specific subfolder** — such as
`PAIMANA_PlaywrightMavenJavaSelenium` — to have its own clean, independent repo.

**Symptom:** `git status` in the parent lists dozens of unrelated project folders, and
`git rev-parse --show-toplevel` from inside your target subfolder prints the *parent's* path.

### Step 1 — Remove everything else from the parent repo

Three different fixes layered together — each solves a different part of the problem, and
skipping any one leaves the mess half-solved.

| Option | What it undoes | What it leaves behind | Use when |
|---|---|---|---|
| **Unstage** — `git restore --staged <folder>` | Removes files from the "about to commit" list | Files are still **tracked** — Git keeps watching them and will re-stage on the next change | A folder shows under "Changes to be committed" and you don't want it committed |
| **Remove** — `git rm -r --cached <folder>` | Stops Git tracking the folder entirely | Files stay safely on disk, but move to the "untracked" list | You want Git to stop watching a folder for good, without deleting anything |
| **Ignore** — add to `.gitignore` | Stops untracked folders reappearing in `git status` | Nothing — this is the permanent fix | You never want this folder tracked again, by accident or otherwise |

None of the three alone is sufficient — **use all three, in this order**:

```cmd
cd C:\Users\shiva\OneDrive\JavaSelenium

:: 1. Unstage first (undo the pending commit)
git restore --staged paimana-automation_1.1
git restore --staged paimana_1point1

:: 2. Remove from tracking (stop watching them, keep files on disk)
git rm -r --cached paimana-automation_1.1
git rm -r --cached paimana_1point1

:: 3. Ignore permanently (stop them resurfacing, ever)
::    add every other folder name to .gitignore, as below
```

For folders already listed as "Untracked files" in `git status`, skip Unstage and Remove
(they were never tracked) and go straight to Ignore.

Create or edit `.gitignore` in `JavaSelenium` and list every folder except the one you're keeping:

```gitignore
paimana-automation_1.1/
paimana_1point1/
PAIMANA_Cucumber/
PAIMANA_Cucumber_1.1/
PAIMANA_PlaywrightJavaSelenium/
PAIMANA_PlaywrightTypeScript/
Appium/
Cypress/
Playwright/
Playwright_TestNG/
# ... add every other project folder here
```

Do **not** add `PAIMANA_PlaywrightMavenJavaSelenium/` — that's the one folder you want tracked.

Confirm the parent has no dangling remote pointing at the wrong GitHub repo:

```cmd
:: Does the parent still push somewhere? Should print nothing
git remote -v
```

Should print nothing. If it shows a URL, remove it:

```cmd
:: Unlink the parent from that GitHub URL
git remote remove origin
```

Commit the cleanup:

```cmd
:: Stage only the updated .gitignore
git add .gitignore

:: Save the cleanup as one commit
git commit -m "Ignore unrelated project folders, keep only PAIMANA_PlaywrightMavenJavaSelenium"
```

The parent repo isn't being deleted here — just cleared of everything else, or left
unused going forward.

**Confirm it worked:**

```cmd
:: VERIFY: only .gitignore and the target folder may be listed
git status
```

Should now show only `.gitignore` and your target folder — nothing else.

### Step 2 — Give the target project its own repo

```cmd
:: Go to the project that needs its own repo
cd C:\Users\shiva\OneDrive\JavaSelenium\PAIMANA_PlaywrightMavenJavaSelenium

:: Has it got its own .git? "File Not Found" = not yet
dir /a:h .git
```

If this says **File Not Found**, the folder has no repo of its own yet — proceed.

```cmd
:: Create the project's own repository
git init

:: VERIFY: .git now exists here
dir /a:h .git

:: VERIFY: must print THIS project's path, not the parent's
git rev-parse --show-toplevel
```

`git rev-parse` must now print **this project's own path**, not the parent's. That is the
check that confirms isolation actually worked.

### Step 3 — Stage, verify, commit

```cmd
:: Stage all project files
git add .

:: VERIFY: paths start with src/, suites/, pom.xml, with no ../ anywhere
git status
```

Every path must start with `src/`, `suites/`, `pom.xml`, etc. — **no `../`** anywhere.
If `../` appears, `git init` didn't run in the right folder — go back to Step 2.

```cmd
:: Save the first commit of the new repo
git commit -m "Initial commit: PAIMANA Playwright Java Maven project"
```

### Step 4 — Delete the old GitHub repo, create a fresh one

The existing repo has the wrong content baked into its history — delete and recreate
rather than fixing in place.

1. github.com → open the repo → **Settings** → **Danger Zone** → **Delete this repository** → type the name to confirm.
2. **New repository** → same name → Private → no README / gitignore / license.

### Step 5 — Link and push

```cmd
:: Link to the freshly created, empty GitHub repo
git remote add origin https://github.com/ShivamPandit1213/PAIMANA_PlaywrightMavenJavaSelenium.git

:: VERIFY: the URL above on (fetch) and (push)
git remote -v

:: Name the branch master
git branch -M master

:: Upload and set upstream tracking
git push -u origin master
```

### Step 6 — Verify

```cmd
:: The initial commit should be listed
git log --oneline

:: Expect "Your branch is up to date with 'origin/master'"
git status
```

`git status` should say `Your branch is up to date with 'origin/master'`. Refresh the
GitHub page — the file list should show **only** `src`, `suites`, `pom.xml`, `README.md`,
`.classpath`, `.project`, `.settings`, `.gitignore`.

### Summary: the whole sequence

```cmd
:: ===== In the PARENT folder =====
cd C:\Users\shiva\OneDrive\JavaSelenium

:: 1. See what's staged; unstage each unwanted folder
git status
git restore --staged <folder>

:: 2. Stop tracking each unwanted folder (files stay on disk)
git rm -r --cached <folder>

:: 3. Add every unwanted folder to .gitignore (edit the file), then commit it
git add .gitignore
git commit -m "Ignore unrelated project folders"

:: 4. Unlink the parent from GitHub if it prints a URL
git remote -v
git remote remove origin

:: ===== In the TARGET project folder =====
cd C:\Users\shiva\OneDrive\JavaSelenium\PAIMANA_PlaywrightMavenJavaSelenium

:: 5. Confirm no repo exists yet ("File Not Found")
dir /a:h .git

:: 6. Create the project's own repo
git init

:: 7. VERIFY: prints this project's own path
git rev-parse --show-toplevel

:: 8. Stage and VERIFY: no ../ paths
git add .
git status

:: 9. First commit
git commit -m "Initial commit"

:: 10. On github.com: delete the old repo, create a new empty one (same name)

:: 11. Link, name the branch, upload
git remote add origin <url>
git branch -M master
git push -u origin master
```

### Avoiding this for every future project

Unstage / Remove / Ignore are **repair tools** — used once, to fix a mistake that already
happened. They are not an ongoing workflow. A `.gitignore` in the parent folder has no
effect on a subfolder that already has its own `.git` — Git stops looking at parent
folders the moment it finds one in the current directory.

**The rule that prevents needing this section again:** the moment a new project folder is
created, before writing any code —

```cmd
:: Go to the brand-new project folder, before writing any code
cd <new-project-folder>

:: Give it its own repository immediately
git init

:: VERIFY: must print this folder's own path, not a parent's
git rev-parse --show-toplevel
```

If that last command prints the new folder's own path (not the parent's), the project is
fully isolated, and Unstage / Remove / Ignore will never be needed for it again.

[⬆ Back to top](#table-of-contents)

---

## 18. Playwright CLI

Generic Playwright commands that work in any of the Playwright projects. For each
project's own npm scripts, folder and settings, see [Section 19](#19-playwright-projects).

### Setting environment variables: `cmd` vs PowerShell

Several commands below set a variable for the run. The syntax depends on the shell:

| Action | `cmd` | PowerShell |
|---|---|---|
| Set for this window | `set HEADLESS=true` | `$env:HEADLESS='true'` |
| Show the value | `echo %HEADLESS%` | `$env:HEADLESS` |
| Remove it | `set HEADLESS=` | `Remove-Item Env:HEADLESS` |
| Set and run in one line | `set BASE_URL=https://other-env.example && npm test` | `$env:BASE_URL='https://other-env.example'; npm test` |
| Any shell, one run only | `npx cross-env HEADLESS=true playwright test` | same |

A variable set this way stays until the window is closed. A leftover value is the usual
reason tests unexpectedly show or hide windows: print it, then remove it.

### Setup and upgrade

| Command | Purpose |
|---|---|
| `npm ci` | Install exactly what `package-lock.json` says. Use after cloning, switching branches, or when `node_modules` looks broken. Fails if `package.json` and the lock file disagree. |
| `npm install` | Resolve versions afresh and rewrite `package-lock.json`. Only when changing dependencies on purpose (`npm install -D <package>`). |
| `npx playwright install` | Download browsers matching the installed Playwright version into `C:\Users\<you>\AppData\Local\ms-playwright` (shared by all projects). Once per machine and after **every** Playwright upgrade. Prints nothing when already present. |
| `npx playwright install chromium` | Download one browser only. |
| `npx playwright install --list` | Show which browser builds are on this machine. |
| `npx playwright install --with-deps` | Browsers plus OS libraries (Linux/CI only). |
| `npx playwright install-deps` | Only the OS libraries (Linux/CI). |
| `npm outdated` | Which packages have newer versions. |
| `npm install -D @playwright/test@latest` then `npx playwright install` | Upgrade Playwright. Always re-run `install`: browser builds are tied to the package version. |
| `npx playwright clear-cache` | Clear Playwright's compile/test cache (**not** the browsers). Try it when an edit to a test or config seems ignored. |
| `npx playwright uninstall` | Remove the downloaded browsers (about 1 GB), not the package. |
| `rmdir /s /q node_modules && npm ci` (cmd)<br>`Remove-Item -Recurse -Force node_modules; npm ci` (PowerShell) | Full reset of a broken `node_modules`. |
| `npm init playwright@latest` | Scaffold a new Playwright project. |
| `npx playwright --version` | Print the installed version; an error means dependencies are missing. |
| `npx playwright --help` | Full command list. |

### Run

| Command | Purpose |
|---|---|
| `npx playwright test` | Run all tests in all projects (browsers). |
| `npx playwright test --project=chromium` | One browser — fastest feedback. |
| `npx playwright test --project=chromium --project=firefox` | Several browsers. `webkit` is Safari's engine. |
| `npx playwright test --workers=1` | One test at a time: when parallel tests interfere, or the PC struggles. |
| `npx playwright test --retries=2` | Retry failures. A test that passes on retry is reported as *flaky*, not *failed*. |
| `npx playwright test --repeat-each=5` | Run every test 5 times to prove a green test is stable. |
| `npx playwright test --last-failed` | Re-run only the tests that failed last time. |
| `npx playwright test --headed` | Show browser windows for one run. Projects with a `test:headed` script should use that instead (it also maximizes windows). |
| `npx playwright show-report` | Open the HTML report from the last run. |

Flags stack, e.g. `npx playwright test tests/ui/home.spec.js --project=chromium --grep @smoke`.

### Filter: run less

| Command | Purpose |
|---|---|
| `npx playwright test <file>` | One spec file, e.g. `tests/ui/home.spec.js`. |
| `npx playwright test <file>:<line>` | One test, by the line number of its `test(...)` call. |
| `npx playwright test -g "home page loads"` | By test title (substring match). |
| `npx playwright test --grep @smoke` | By tag. A tag exists only if a test declares it: `test('...', { tag: '@smoke' }, async ({ page }) => ...)`. |
| `npx playwright test --grep-invert @slow` | Everything except a tag. |
| `npx playwright test --only-changed` | Only tests affected by uncommitted Git changes. |
| `npx playwright test --shard=1/4` | Split the suite across machines. |
| `npx playwright test --list` | Show which tests **would** run. First thing to try when a filter matches nothing or you see `No tests found`. |

### Debug a failing test

| Command | Purpose |
|---|---|
| `npx playwright test <file> --project=chromium --headed` | Narrow down: one browser, one file, visible. Via npm: `npm run test:chromium -- <file> --headed` (the `--` passes the rest to Playwright). |
| `npx playwright test --debug` | Playwright Inspector: pause before every action and step through. Best for "why does it not click / find it". |
| `npx playwright test --ui` | UI mode: pick tests, watch them, time-travel through each step with DOM and network. Best while writing tests. |
| `npx playwright test --trace on` | Record a trace for every test (configs usually record one only on the first retry). |
| `npx playwright show-trace test-results/<test-folder>/trace.zip` | Open a trace: every action, network call, console line and a screenshot per step. Without a path it opens a file picker. |
| `npx playwright test --timeout=120000` | More time per test (ms) for one run. |
| `npx playwright test --max-failures=1` (or `-x`) | Stop at the first failure. |
| `npx playwright test --reporter=line` | Compact output; `list`, `dot`, `html`, `json`, `junit` also available. |
| `npx playwright test --update-snapshots` (or `-u`) | Regenerate visual/snapshot baselines. |
| `npx playwright test -c <file>` | Use a specific config file. |
| `set DEBUG=pw:api` (cmd) / `$env:DEBUG='pw:api'` (PowerShell), then run | Verbose log of every Playwright call — when a run hangs. Remove the variable afterwards. |

### Where a failed run leaves evidence

| Location | What is there |
|---|---|
| `test-results/<test-name>/test-failed-1.png` | Screenshot at the moment of failure. |
| `test-results/<test-name>/error-context.md` | The page's accessibility tree at that moment — read it to find the right locator. |
| `test-results/<test-name>/trace.zip` | On a retry, or always with `--trace on`. |
| `playwright-report/index.html` | The HTML report (`npx playwright show-report`). |

Both folders are rewritten on every run and are git-ignored. Copy a file out before re-running to keep it.

### Record and explore

| Command | Purpose |
|---|---|
| `npx playwright codegen <url>` | Opens a browser and writes code for every click and fill. Use it to get locators for a new page object, then move them into `pages/` rather than keeping the generated test as-is. |
| `npx playwright codegen` | Same, starting from a blank page. |
| `npx playwright open <url>` | A browser with the Playwright inspector attached and no code generation — for exploring a page and trying locators. |

[⬆ Back to top](#table-of-contents)

---

## 19. Playwright Projects

Run every command from the project's own folder. From `C:\Users\shiva` (or
`C:\WINDOWS\System32`) Playwright scans the whole profile and fails with
`EPERM: operation not permitted, scandir ...\Temp\WinSAT`. You're in the right place when
`dir` lists `package.json` and the `playwright.config` file.

| Project | Folder | Language | Tests |
|---|---|---|---|
| **Paimana_Dev** | `C:\Users\shiva\OneDrive\JavaSelenium\Paimana_Dev` | JavaScript, Node 24 | 2 UI × 3 browsers + 1 API = 7 |
| **PlaywrightBasic1** | `C:\Users\shiva\OneDrive\JavaSelenium\PlaywrightBasic1` | JavaScript, Node 24 | 3 × 3 browsers = 9 |
| **PAIMANA_Playwright_1.1** | `C:\Users\shiva\OneDrive\JavaSelenium\PAIMANA_Playwright_1.1` | TypeScript | 5 × 3 browsers = 15 |

All three test the dev portal `https://iigdev.uatnegd.online` and use `@playwright/test` 1.63.

### Paimana_Dev

| Command | Purpose |
|---|---|
| `npm ci` | Install the exact dependencies from `package-lock.json` |
| `npx playwright install` | Download browsers (first time, and after Playwright upgrades) |
| `npm test` | Run everything: UI tests in every browser, plus API tests |
| `npm run test:headed` | Visible, maximized browser windows (use this in Jenkins) |
| `npm run test:headless` | No browser windows |
| `npm run test:ui` | UI tests only (all browsers) |
| `npm run test:api` | API tests only |
| `npm run test:chromium` | UI tests in one browser — quickest |
| `npm run test:smoke` | Only `@smoke` tests |
| `npm run test:regression` | Only `@regression` tests |
| `npm run test:headed -- --workers=4` | Extra Playwright options go after `--` |
| `npm run report` | Open the last HTML report |
| `npm run check` | Lint + format check (CI runs this first) |
| `npm run lint:fix` | Auto-fix lint problems |
| `npm run format` | Auto-format all files |

> In this project `test:ui` means "UI tests"; it does **not** open Playwright's UI mode.
> For UI mode run `npx playwright test --ui`.

**Settings by environment variable** (also in `.env`, see `.env.example`):

| Variable | Values | Priority | Effect |
|---|---|---|---|
| `HEADLESS` | `true` / `false` | `HEADLESS` > `CI` > visible | `false` = maximized windows even on Jenkins. Anything else stops the run. |
| `WORKERS` | `4` or `50%` | `WORKERS` > `CI` (1) > Playwright's default | Parallel tests. In a Jenkins batch step write `50%%`. |
| `BASE_URL` | URL | `BASE_URL` > dev portal | Point the tests at another environment. |

### PlaywrightBasic1

| Command | Purpose |
|---|---|
| `npm test` | All 9 runs. Windows shown or not per `DEFAULT_HEADLESS` in `playwright.config.js` (currently `false` = visible). |
| `npm run test:headed` | Always visible windows (maximized). |
| `npm run test:headless` | No windows; fastest, use before a commit. |
| `npm run test:chromium` | One browser. |
| `npm run test:smoke` / `npm run test:regression` | By tag. |
| `npm run report` | Open the last HTML report, with the attached full-page screenshot. |
| `npm run check` | Lint + format check. |

**Headless priority** (`resolveHeadless()` in `playwright.config.js`), highest first:

1. **`CI` set** (GitHub Actions) → always headless.
2. **`HEADLESS`** → `true` / `false`, any case; anything else stops the run with `HEADLESS must be 'true' or 'false'`.
3. **`DEFAULT_HEADLESS`** constant at the top of the config.

> Unlike Paimana_Dev, here `CI` wins over `HEADLESS`, so a headed run on Jenkins isn't possible without changing the config.

Repository: `https://github.com/ShivamPandit1213/PlaywrightBasic1`. Its GitHub Actions run (`.github/workflows/playwright.yml`) is always headless, 1 worker, 2 retries, and a committed `test.only` fails the build. Results: repository → **Actions** → the run → `playwright-report` artifact.

**Optional extra scripts** for `package.json` → `"scripts"`:

```json
"test:ui": "playwright test --ui",
"test:debug": "playwright test --debug --project=chromium",
"codegen": "playwright codegen https://iigdev.uatnegd.online/home"
```

### PAIMANA_Playwright_1.1 (TypeScript)

**Mandatory files** — the project won't run without these three:

| File | Purpose |
|---|---|
| `package.json` | Declares `@playwright/test` as a dependency |
| `playwright.config.ts` | Browsers, `testDir`, `baseURL`, reporters, timeouts |
| `tests/*.spec.ts` | The actual tests |

Everything else is supporting code, imported by the specs.

**Project structure**

```text
PAIMANA_Playwright_1.1/
├── fixtures/              custom test fixtures (page-object injection)
├── pages/                 Page Object Model classes (BasePage, HomePage, LoginPage)
├── test-data/             static test data (users.json)
├── tests/                 api.spec.ts, example.spec.ts, home.spec.ts
├── utils/                 shared helpers
├── playwright.config.ts   browsers, baseURL, reporters, timeouts
├── package.json           dependencies and npm scripts
└── tsconfig.json          TypeScript compiler options
```

**First-time setup**

```cmd
:: Go to the project root (not C:\Users\shiva or System32)
cd C:\Users\shiva\OneDrive\JavaSelenium\PAIMANA_Playwright_1.1

:: Install the dependencies listed in package.json
npm install

:: Download the browsers for this Playwright version (once per machine/upgrade)
npx playwright install
```

**Run**

| Command | Purpose |
|---|---|
| `npx playwright test` | All tests, all 3 browsers (headless by default) |
| `npx playwright test --project=chromium` | One browser — fastest |
| `npx playwright test tests/home.spec.ts --project=chromium --headed` | One file, visible browser |
| `npx playwright test --grep @smoke` | By tag: `@smoke`, `@regression`, `@api` |
| `npx playwright test -g "login"` | By test title |
| `npx playwright test --project=chromium --grep @smoke --headed` | Flags combined |
| `npx playwright show-report` | Open the last report |

> **Visible browser:** use `--headed`. The older note `$env:HEADED=1` only works if
> `playwright.config.ts` reads a `HEADED` variable; Playwright itself ignores it.
> Check the config before relying on it.

**Optional `scripts`** for `package.json`:

```json
"scripts": {
  "test": "playwright test",
  "test:ui": "playwright test --ui",
  "test:smoke": "playwright test --grep @smoke"
}
```

Then `npm test` or `npm run test:ui`.

**Real Chrome or Edge:** the `chromium` project is the open-source engine behind Chrome
and Edge, not the browsers themselves. To test the installed browsers, add projects with
`channel: 'chrome'` or `channel: 'msedge'` in `playwright.config.ts`.

[⬆ Back to top](#table-of-contents)

---

## 20. Playwright Troubleshooting

### Diagnosis order

When anything fails, in this order:

1. `dir` — right folder? (`package.json` listed)
2. `npx playwright --version` — package installed?
3. `npx playwright install` — browsers present for this version?
4. `npx playwright test --list` — does the path / filter match?
5. Run one test, one browser, visible (`--project=chromium --headed`), then read `test-results/`.

### Environment

| Symptom | Cause | Fix |
|---|---|---|
| `EPERM: operation not permitted, scandir ...\Temp\WinSAT` | Ran from the home folder or `System32`. | `cd` to the project folder. |
| `npm.ps1 cannot be loaded because running scripts is disabled` | PowerShell execution policy blocks npm. | Once: `Set-ExecutionPolicy -Scope CurrentUser RemoteSigned`, or use `cmd`. |
| `'playwright' is not recognized` / `Cannot find module '@playwright/test'` | `node_modules` missing or incomplete. | `npm ci` |
| `Executable doesn't exist at ...\ms-playwright\...` | Browsers not downloaded for this version. | `npx playwright install` |
| `npm ci`: `package.json and package-lock.json are not in sync` | Lock file drifted. | `npm install` once, then commit `package-lock.json`. |
| `EBUSY` / `EPERM` mid-run | OneDrive syncing `test-results/` while Playwright writes to it. | Pause OneDrive sync, or move the project out of OneDrive. |

### Configuration

| Symptom | Cause | Fix |
|---|---|---|
| `HEADLESS must be 'true' or 'false', got 'yes'` | Typo in the variable. | Set `true` / `false`, or remove the variable. |
| `config.workers must be a number or percentage` | `WORKERS` text passed straight to Playwright. | Convert it in the config (`resolveWorkers()` in Paimana_Dev). |
| `Cannot use import statement outside a module` | `package.json` lost `"type": "module"` (JavaScript projects). | Put it back. |
| `deviceScaleFactor option is not supported with null viewport` | `viewport: null` with a device preset's scale factor. | Add `deviceScaleFactor: undefined`. |
| `Playwright Test did not expect test() to be called here` | Two copies of `@playwright/test`, or a spec importing `test` from two places. | Import from one place; `npm ls @playwright/test` must show one version. |

### The run

| Symptom | Cause | Fix |
|---|---|---|
| `Error: No tests found` | Wrong path, wrong filter, or file not named `*.spec.js` / `*.spec.ts`. | `npx playwright test --list` |
| `Test timeout of 60000ms exceeded` / `page.goto: Timeout 30000ms exceeded` | Portal slow or down, VPN off, a locator that never appears, or (Firefox) a resource that never finishes loading. | Open the URL in a normal browser first; then `--headed` or `--debug`, and read `error-context.md`. For the Firefox font case use `waitUntil: 'domcontentloaded'`. |
| `net::ERR_NAME_NOT_RESOLVED` / `ERR_CONNECTION_REFUSED` | Wrong `BASE_URL`, or no network / VPN. | Print `BASE_URL` and check it. |
| `toHaveURL ... Received: https://.../login` | The portal redirected (session, maintenance, changed route). | Look at the screenshot before touching the test. |
| `Target page, context or browser has been closed` | Browser crashed, usually out of memory with many in parallel. | `--workers=1` |
| CI fails on `test.only` / `forbidOnly` | A `test.only` was committed. | Remove `.only`, commit, push. |
| Fails on GitHub Actions, passes locally | GitHub's servers can't reach `iigdev.uatnegd.online` (private network / VPN). | Run on a machine inside the network, e.g. the local Jenkins. |

[⬆ Back to top](#table-of-contents)

---

## 21. Everyday Commands

**Writing tests** — fast loop, one browser, interactive:

```cmd
:: Open UI mode with one browser: pick tests, watch them, time-travel through steps
npx playwright test --project=chromium --ui
```

**Paimana_Dev** — quick check before pushing (all scripts: [Section 19](#19-playwright-projects)):

```cmd
:: ESLint + Prettier check: the same gate CI runs first
npm run check

:: All tests without windows: fastest full run
npm run test:headless
```

**Daily Git loop**, once upstream is set:

```cmd
:: Stage every change
git add .

:: Save them as one commit
git commit -m "what changed"

:: Upload (upstream already set)
git push
```

**Before pushing anything from a new folder** — the isolation check:

```cmd
:: Must print THIS folder's path, not a parent's
git rev-parse --show-toplevel

:: Must show YOUR repo's URL (or nothing, if not linked yet)
git remote -v

:: Paths must have no ../
git status
```

[⬆ Back to top](#table-of-contents)
