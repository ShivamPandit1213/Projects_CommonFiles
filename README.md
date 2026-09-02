# PAIMANA — Jenkins, Git & Automation Command Reference

A single reference for the local toolchain: Jenkins startup, Git repository setup and
recovery, remote management, and the Playwright / Cucumber test CLIs.

Commands assume **Windows `cmd`** unless marked otherwise. Examples use the
repositories `ShivamPandit1213/PAIMANA_Dev` and `ShivamPandit1213/PAIMANA_PlaywrightMavenJavaSelenium`.

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
19. [Cucumber CLI](#19-cucumber-cli)
20. [Everyday Commands](#20-everyday-commands)

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
echo Starting Jenkins with JDK 21...
set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.12"
cd /d C:\Users\shiva\OneDrive\Jenkins
"%JAVA_HOME%\bin\java" -jar jenkins.war
pause
```

Double-click to run. Watch the console for the initial admin password on first launch.

### Fixing `Jenkinsfile.txt` → `Jenkinsfile`

Windows often saves `Jenkinsfile` as `Jenkinsfile.txt`. Jenkins requires the exact
filename with no extension.

```bash
cd C:\Users\shiva\OneDrive\JavaSelenium\paimana_1point1
ren Jenkinsfile.txt Jenkinsfile
dir Jenkinsfile
```

`dir` should show `Jenkinsfile` with nothing after it — confirms the rename worked.

View the file content directly in CMD:

```bash
type Jenkinsfile
```

Commit it:

```bash
git add Jenkinsfile
git commit -m "Add Jenkinsfile"
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

```bash
cd <project-folder>
git init
dir /a:h .git
git add .
git status
git commit -m "Initial commit"
git remote add origin <url-from-GitHub>
git branch -M main
git push -u origin main
```

### What each command does

| Step | Command | Purpose |
|---|---|---|
| 1 | `cd <project-folder>` | Enter the exact project folder — never a parent folder |
| 2 | `git init` | Creates a **new, separate** `.git` in this folder |
| 3 | `dir /a:h .git` | **Verify** — confirms `.git` exists here, not inherited from a parent |
| 4 | `git add .` | Stages only this project's files |
| 5 | `git status` | **Verify** — paths should read `src/...`, `pom.xml` with **no `../`** prefix. If you see `../`, you're in the wrong repo |
| 6 | `git commit -m "Initial commit"` | Saves the first commit locally |
| 7 | `git remote add origin <url>` | Links the local repo to the empty GitHub repo you created |
| 8 | `git branch -M main` | Sets the branch name to `main` |
| 9 | `git push -u origin main` | Uploads the commit and sets upstream tracking |

**Use `master` instead**, for consistency with an older repo:

```bash
git branch -M master
git push -u origin master
```

Check the current branch name first if unsure:

```bash
git branch
```

After step 9, run the checks in [Section 5](#5-verification-commands) to confirm
everything actually landed.

[⬆ Back to top](#table-of-contents)

---

## 5. Verification Commands

### Before every commit / push

| Command | What it tells you |
|---|---|
| `dir` | Lists files in the current folder — confirms you're in the right place |
| `git rev-parse --show-toplevel` | Prints the actual repo root — should be *this* project's path, not a parent's |
| `dir /a:h .git` | Confirms a `.git` folder physically exists here |
| `git remote -v` | Shows which GitHub URL this repo pushes to — prints nothing if not yet linked |
| `git branch` | Shows current branch name (`main` vs `master`) |
| `git status` | Shows staged / unstaged / untracked files — check paths have no `../` |
| `git log --oneline` | Shows commit history, one line per commit |
| `git diff` | Shows exact line changes before staging |
| `git ls-files` | Lists every file Git is currently tracking — ground truth of what will be pushed |

### After every push

Don't assume a push worked just because no error appeared.

| Check | Command | Expected result |
|---|---|---|
| Is a remote linked? | `git remote -v` | Your GitHub URL for both `(fetch)` and `(push)` — **not empty, not someone else's repo** |
| Was the commit saved? | `git log --oneline` | Your commit message at the top — confirms it isn't still unstaged |
| Is upstream tracking set? | `git status` | `Your branch is up to date with 'origin/main'` — confirms the branch is linked, not just uploaded once |
| Did it actually land? | Refresh the repo page in your browser | File list matches your project folder — no leftover files from a different project |

If `git remote -v` shows a URL that isn't yours, or `git status` doesn't mention
`origin/main`, **stop** — something is misconfigured and pushing further makes it worse.
See [Section 14](#14-troubleshooting--hijacked-parent-remote).

[⬆ Back to top](#table-of-contents)

---

## 6. Day-to-Day Workflow

Once `origin` and upstream tracking are set, every future change is three commands:

```bash
git add .
git commit -m "what changed"
git push
```

No `-u origin main` needed again — upstream is already remembered.

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

```bash
git add "Jenkinsfile"
git add "playwright.config.ts"
git add "src/main/java/com/paimana/pages/HomePage.java"
```

Quotes are optional for simple filenames with no spaces, but using them every time
removes the guesswork.

[⬆ Back to top](#table-of-contents)

---

## 8. Inspecting Repository History

| Command | Output / Action | When It Works | Limitations & Gotchas |
| :--- | :--- | :--- | :--- |
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

```bash
# 1. Check current status
git status

# 2. Unstage anything accidentally staged (modifications are preserved)
git restore --staged <file-path>

# 3. Stage only the targeted file(s)
git add <file-path>

# 4. Create an atomic commit
git commit -m "Your descriptive commit message"
```

One commit should describe one change. If the commit message needs the word "and",
it's usually two commits.

[⬆ Back to top](#table-of-contents)

---

## 10. Managing Remotes

### Add & connect

| Purpose | Command | Example |
| --- | --- | --- |
| Add a remote | `git remote add <name> <url>` | `git remote add origin https://github.com/ShivamPandit1213/PAIMANA_Dev.git` |
| Add a second remote | `git remote add <name> <url>` | `git remote add backup https://github.com/ShivamPandit1213/PAIMANA_Backup.git` |
| Push and set upstream | `git push -u <remote> <branch>` | `git push -u origin master` |
| Push to a specific remote | `git push <remote> <branch>` | `git push backup master` |

### Inspect — offline (local config only)

| Purpose | Command | Example output |
| --- | --- | --- |
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
| --- | --- | --- |
| Full remote details | `git remote show <name>` | `HEAD branch: master` … `(local out of date)` |
| List server refs | `git ls-remote <name>` | `1142e4c…  refs/heads/master` |
| Branch heads only | `git ls-remote --heads <name>` | `1142e4c…  refs/heads/master` |
| Update stale tracking refs | `git remote update` | fetches all remotes |
| Drop deleted remote branches | `git remote prune <name>` | `* [pruned] origin/old-branch` |

### Modify

| Purpose | Command | Example |
| --- | --- | --- |
| Rename a remote | `git remote rename <old> <new>` | `git remote rename backup mirror` |
| Change a remote's URL | `git remote set-url <name> <url>` | `git remote set-url mirror https://github.com/ShivamPandit1213/PAIMANA_Mirror.git` |
| Separate push URL | `git remote set-url --push <name> <url>` | `git remote set-url --push origin https://github.com/ShivamPandit1213/Fork.git` |

### Remove

| Purpose | Command | Note |
| --- | --- | --- |
| Remove a remote | `git remote remove <name>` | `git remote remove mirror` |
| Same, older spelling | `git remote rm <name>` | Identical behaviour |
| Verify removal | `git remote -v` | Only `origin` remains |

### Re-pointing a repo at the correct URL

Used as a set whenever a repo is linked to the wrong GitHub URL.

| Step | Command | Result |
|---|---|---|
| 1. Check what's currently linked | `git remote -v` | Prints the URL for `(fetch)` and `(push)` — or nothing if none is set |
| 2. Remove it | `git remote remove origin` | Unlinks the repo from that URL — local commits untouched |
| 3. Re-link to the correct URL | `git remote add origin <correct-url>` | Points the repo at the right GitHub repository |

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
| --- | --- | --- |
| `error: remote origin already exists.` | `git remote add origin` run twice | Use `git remote set-url origin <url>` to change it |
| `remote: Repository not found.` | URL points at a repo that does not exist | Create it on GitHub, or correct the URL |
| `error: No such remote: '<name>'` | Remote was renamed or already removed | Check `git remote -v` for the current name |
| `src refspec master does not match any` | No commits exist yet on that branch | Commit first, then push |
| `fatal: 'origin' does not appear to be a git repository` | No remote named `origin` is configured | `git remote add origin <url>` |

### Worked example

```cmd
cd C:\Users\shiva\PAIMANA_Dev

:: Baseline
git remote -v
git remote | find /c /v ""

:: Connect a second remote
git remote add backup https://github.com/ShivamPandit1213/PAIMANA_Backup.git
git remote -v

:: Inspect
git config --get-regexp "^remote\."
git remote show origin
git ls-remote --heads origin
git branch -vv

:: Modify
git remote rename backup mirror
git remote set-url mirror https://github.com/ShivamPandit1213/PAIMANA_Mirror.git
git remote get-url mirror

:: Remove
git remote remove mirror
git remote -v
```

[⬆ Back to top](#table-of-contents)

---

## 11. Undoing & Reverting

### Reset modes compared

| Mode | Action | State of modified files | Risk |
| :--- | :--- | :--- | :--- |
| `--soft` | Moves `HEAD` back to the target commit. | Kept in the staging area (green). | Low |
| `--mixed` *(default)* | Moves `HEAD` back to the target commit. | Kept in the working directory (red / unstaged). | Low |
| `--hard` | Moves `HEAD` back to the target commit. | Discarded. | **High** |

> **On `--hard`:** *committed* work is still recoverable via `git reflog`
> ([Section 13](#13-emergency-recovery-git-reflog)) until Git garbage-collects it.
> *Uncommitted* work is gone for good.

### Common undo scenarios

| Goal | Command | Gotchas |
| :--- | :--- | :--- |
| Undo last commit, keep files staged | `git reset --soft HEAD~1` | Changes are ready to recommit immediately. |
| Undo last commit, unstage files | `git reset HEAD~1` | Changes stay in the folder but must be re-added. |
| Erase the last N commits | `git reset --hard HEAD~N` | **Dangerous:** destroys all uncommitted work in the folder. |
| Overwrite local branch to match remote | `git fetch origin`<br>`git reset --hard origin/<branch>` | Discards all local commits that have not been pushed. |
| Overwrite remote history | `git push origin <branch> --force-with-lease` | **Dangerous:** erases commits on the server. Never on a shared branch. |

> **Prefer `--force-with-lease` over `--force`.** It aborts the push if someone else
> updated the branch since your last fetch, so you can't silently overwrite their work.

### Safest option — revert with a new commit

Keeps history intact. Safe even if others have already pulled the bad commit, and the
only correct choice on a shared branch.

```bash
git revert HEAD
git push
```

To revert a specific older commit rather than the latest:

```bash
git log --oneline
git revert <commit-hash>
git push
```

### Rewrite history — reset, then force-push

Only when certain nobody else has pulled the bad commits.

```bash
git log --oneline
git reset --hard <commit-hash-to-go-back-to>
git push --force-with-lease
```

### Remove files/folders from tracking without deleting them locally

Use when a commit accidentally included the wrong folder — e.g. a sibling project
nested inside. This is the most common cause of the "wrong content" problem in this doc.

```bash
git rm -r --cached <folder-or-file>
git commit -m "Remove folder from tracking"
git push
```

Files remain on disk; Git simply stops tracking them. Add the path to `.gitignore`
afterwards or it will reappear as untracked on the next `git add .`.

[⬆ Back to top](#table-of-contents)

---

## 12. Working Tree & Cleanup

| Command | Action | Best use case | Risk |
| :--- | :--- | :--- | :--- |
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

```bash
# 1. View recent HEAD movements with their SHA hashes
git reflog

# Example output:
# b080ba4 HEAD@{0}: reset: moving to HEAD~1
# 31e88d2 HEAD@{1}: commit: Refactor Jenkinsfile
# f09dd0b HEAD@{2}: commit: update mvn version details

# 2. Inspect a candidate before committing to it
git show 31e88d2

# 3. Restore the state from before the mistake
git reset --hard 31e88d2

# 4. Push the recovered state (see caveat below)
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

```bash
cd <project-folder>
git rev-parse --show-toplevel
```

If this prints the *parent* folder's path instead of your project's own path, that's the cause.

**Fix — clean the parent, then give the project its own repo:**

```bash
cd <parent-folder>
git remote -v
git remote remove origin
```

`git remote -v` should now print **nothing** — confirms it's cleared. Then set the
project folder up properly using [Section 4](#4-full-setup--every-new-project).

[⬆ Back to top](#table-of-contents)

---

## 15. Deleting a GitHub Repository

Use when a repo was created by mistake or ended up with the wrong content.

1. Open the repo on GitHub → **Settings** (top nav)
2. Scroll to the bottom → **Danger Zone**
3. Click **Delete this repository**
4. Type the full name to confirm: `owner/repo-name`
5. Click **I understand the consequences, delete this repository**

There is no undo. Only delete a repo you're certain you want gone — if in doubt, make it
private instead.

[⬆ Back to top](#table-of-contents)

---

## 16. Recovery Path — Wrong Content in History

If a repo's commit *history* already contains the wrong files (rather than just the
working directory), delete-and-recreate is simpler than surgical fixes.

| Step | Command / Action |
|---|---|
| 1 | Delete the repo on GitHub (Settings → Danger Zone) |
| 2 | Create a new empty repo, same name, no README / gitignore / license |
| 3 | `cd <project-folder>` |
| 4 | `dir /a:h .git` — confirm it has its own `.git`, separate from any parent |
| 5 | `git add .` |
| 6 | `git status` — confirm no `../` paths appear |
| 7 | `git commit -m "Initial commit"` |
| 8 | `git remote add origin <new-repo-url>` |
| 9 | `git branch -M main` (or `master`) |
| 10 | `git push -u origin main` |

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

```bash
cd C:\Users\shiva\OneDrive\JavaSelenium

# 1. Unstage first (undo the pending commit)
git restore --staged paimana-automation_1.1
git restore --staged paimana_1point1

# 2. Remove from tracking (stop watching them, keep files on disk)
git rm -r --cached paimana-automation_1.1
git rm -r --cached paimana_1point1

# 3. Ignore permanently (stop them resurfacing, ever)
#    — add every other folder name to .gitignore, as below
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

```bash
git remote -v
```

Should print nothing. If it shows a URL, remove it:

```bash
git remote remove origin
```

Commit the cleanup:

```bash
git add .gitignore
git commit -m "Ignore unrelated project folders, keep only PAIMANA_PlaywrightMavenJavaSelenium"
```

The parent repo isn't being deleted here — just cleared of everything else, or left
unused going forward.

**Confirm it worked:**

```bash
git status
```

Should now show only `.gitignore` and your target folder — nothing else.

### Step 2 — Give the target project its own repo

```bash
cd C:\Users\shiva\OneDrive\JavaSelenium\PAIMANA_PlaywrightMavenJavaSelenium
dir /a:h .git
```

If this says **File Not Found**, the folder has no repo of its own yet — proceed.

```bash
git init
dir /a:h .git
git rev-parse --show-toplevel
```

`git rev-parse` must now print **this project's own path**, not the parent's. That is the
check that confirms isolation actually worked.

### Step 3 — Stage, verify, commit

```bash
git add .
git status
```

Every path must start with `src/`, `suites/`, `pom.xml`, etc. — **no `../`** anywhere.
If `../` appears, `git init` didn't run in the right folder — go back to Step 2.

```bash
git commit -m "Initial commit: PAIMANA Playwright Java Maven project"
```

### Step 4 — Delete the old GitHub repo, create a fresh one

The existing repo has the wrong content baked into its history — delete and recreate
rather than fixing in place.

1. github.com → open the repo → **Settings** → **Danger Zone** → **Delete this repository** → type the name to confirm
2. **New repository** → same name → Private → no README / gitignore / license

### Step 5 — Link and push

```bash
git remote add origin https://github.com/ShivamPandit1213/PAIMANA_PlaywrightMavenJavaSelenium.git
git remote -v
git branch -M master
git push -u origin master
```

### Step 6 — Verify

```bash
git log --oneline
git status
```

`git status` should say `Your branch is up to date with 'origin/master'`. Refresh the
GitHub page — the file list should show **only** `src`, `suites`, `pom.xml`, `README.md`,
`.classpath`, `.project`, `.settings`, `.gitignore`.

### Summary table

| Step | Command | Where |
|---|---|---|
| 1 | `git status` → `git restore --staged <folder>` for each unwanted folder | `JavaSelenium` |
| 2 | `git rm -r --cached <folder>` for each tracked unwanted folder | `JavaSelenium` |
| 3 | Add unwanted folders to `.gitignore` | `JavaSelenium` |
| 4 | `git remote -v` → `git remote remove origin` if needed | `JavaSelenium` |
| 5 | `dir /a:h .git` — confirm no repo exists yet | Target project folder |
| 6 | `git init` | Target project folder |
| 7 | `git rev-parse --show-toplevel` — confirm it prints the project's own path | Target project folder |
| 8 | `git add .` → `git status` — confirm no `../` paths | Target project folder |
| 9 | `git commit -m "Initial commit"` | Target project folder |
| 10 | Delete + recreate the GitHub repo | github.com |
| 11 | `git remote add origin <url>` → `git branch -M master` → `git push -u origin master` | Target project folder |

### Avoiding this for every future project

Unstage / Remove / Ignore are **repair tools** — used once, to fix a mistake that already
happened. They are not an ongoing workflow. A `.gitignore` in the parent folder has no
effect on a subfolder that already has its own `.git` — Git stops looking at parent
folders the moment it finds one in the current directory.

**The rule that prevents needing this section again:** the moment a new project folder is
created, before writing any code —

```bash
cd <new-project-folder>
git init
git rev-parse --show-toplevel
```

If that last command prints the new folder's own path (not the parent's), the project is
fully isolated, and Unstage / Remove / Ignore will never be needed for it again.

[⬆ Back to top](#table-of-contents)

---

## 18. Playwright CLI

### Core commands

| Command | Purpose |
|---|---|
| `npx playwright test` | Run all tests in the project |
| `npx playwright test <file>` | Run one spec file, e.g. `tests/login.spec.ts` |
| `npx playwright test <file>:<line>` | Run the single test at that line number |
| `npx playwright show-report` | Open the HTML report from the last run |
| `npx playwright codegen <url>` | Record browser actions and generate test code |
| `npx playwright install` | Download the browser binaries |
| `npx playwright install chromium` | Download one browser only |
| `npx playwright install --with-deps` | Install browsers plus OS-level dependencies (Linux/CI) |
| `npx playwright install-deps` | Install only the OS dependencies |
| `npm init playwright@latest` | Scaffold a new Playwright project |
| `npx playwright --version` | Print the installed version |

### Filtering tests

| Flag | Purpose |
|---|---|
| `--grep <pattern>`, `-g` | Run only tests whose title matches, e.g. `-g @smoke` |
| `--grep-invert <pattern>` | Run everything except matches |
| `--project=<name>` | Run one project or browser, e.g. `--project=chromium` |
| `--only-changed` | Run only tests affected by uncommitted git changes |
| `--last-failed` | Re-run only the tests that failed last time |
| `--shard=<n>/<total>` | Split the suite across machines, e.g. `--shard=1/4` |

### Debugging and execution

| Flag | Purpose |
|---|---|
| `--ui` | Interactive UI mode with time-travel and DOM snapshots |
| `--headed` | Show the browser window instead of running headless |
| `--debug` | Launch the Playwright Inspector and step through |
| `--trace=on` | Record a trace for every test (`on-first-retry` is the usual CI setting) |
| `--workers=<n>` | Parallel worker count; `--workers=1` forces serial |
| `--repeat-each=<n>` | Run each test N times — useful for hunting flaky tests |
| `--retries=<n>` | Retry failed tests N times |
| `--max-failures=<n>`, `-x` | Stop after N failures (`-x` stops at the first) |
| `--timeout=<ms>` | Override the per-test timeout |
| `--reporter=<name>` | Reporter: `list`, `line`, `dot`, `html`, `json`, `junit` |
| `--update-snapshots`, `-u` | Regenerate visual and snapshot baselines |
| `--list` | List matching tests without running them |
| `--config=<file>`, `-c` | Use a specific config file |

### Trace viewer

| Command | Purpose |
|---|---|
| `npx playwright show-trace <file.zip>` | Open a recorded trace |
| `npx playwright show-trace` | Open the trace viewer and drop a file in |
| `npx playwright open <url>` | Open a page in a Playwright-controlled browser |

[⬆ Back to top](#table-of-contents)

---

## 19. Cucumber CLI

### Cucumber-JVM (Maven + TestNG)

Cucumber-JVM has no CLI of its own in a Maven project. Drive it through Maven and system
properties.

| Command | Purpose |
|---|---|
| `mvn test` | Run all features via your runner class |
| `mvn test -Dcucumber.filter.tags="@smoke"` | Run scenarios with one tag |
| `mvn test -Dcucumber.filter.tags="@smoke and not @wip"` | Combine tags with `and`, `or`, `not` |
| `mvn test -Dcucumber.filter.name="login"` | Filter by scenario name |
| `mvn test -Dcucumber.features=<path>` | Run one feature file |
| `mvn test -Dcucumber.plugin="pretty,html:target/report.html"` | Set reporters |
| `mvn test -Dcucumber.glue=<package>` | Point at the step-definition package |
| `mvn test -Dcucumber.execution.dry-run=true` | Check step bindings without executing |
| `mvn test -Dcucumber.publish.quiet=true` | Suppress the publish-report banner |
| `mvn test -Dtest=<RunnerClass>` | Run a specific TestNG runner class |

The dry run is the fastest way to find missing step definitions — it prints snippets for
anything unmatched.

### Cucumber-JS (Node)

| Command | Purpose |
|---|---|
| `npx cucumber-js` | Run all features |
| `npx cucumber-js <file>` | Run one feature file |
| `npx cucumber-js --tags "@smoke"` | Filter by tag |
| `npx cucumber-js --tags "@smoke and not @wip"` | Combined tag expression |
| `npx cucumber-js --name "login"` | Filter by scenario name |
| `npx cucumber-js --dry-run` | Validate step bindings without running |
| `npx cucumber-js --format html:report.html` | Choose output format |
| `npx cucumber-js --parallel <n>` | Run N scenarios concurrently |
| `npx cucumber-js --retry <n>` | Retry failing scenarios |
| `npx cucumber-js --fail-fast` | Stop on first failure |
| `npx cucumber-js --require <path>` | Load support and step files |

[⬆ Back to top](#table-of-contents)

---

## 20. Everyday Commands

```bash
# Writing tests: fast loop, one browser, interactive
npx playwright test --project=chromium --ui

# Quick tagged check on the Java suite
mvn test -Dcucumber.filter.tags="@smoke"

# When a step mysteriously does not fire
mvn test -Dcucumber.execution.dry-run=true
```

```bash
# Daily Git loop, once upstream is set
git add .
git commit -m "what changed"
git push
```

```bash
# Before pushing anything from a new folder — the isolation check
git rev-parse --show-toplevel
git remote -v
git status
```

[⬆ Back to top](#table-of-contents)
