# PAIMANA — Jenkins & GitHub Command Reference

---

## Table of Contents

1. [Jenkins](#1-jenkins)
   - [Start Jenkins with JDK 21](#start-jenkins-with-jdk-21)
   - [Fixing Jenkinsfile.txt → Jenkinsfile](#fixing-jenkinsfiletxt--jenkinsfile)
2. [Creating a New GitHub Repository](#2-creating-a-new-github-repository)
3. [Verify: Remote Linked, Committed, and Pushed Correctly](#3-verify-remote-linked-committed-and-pushed-correctly)
4. [Full Setup — Every New Project, Step by Step](#4-full-setup--every-new-project-step-by-step)
5. [Verification Commands](#5-verification-commands--use-before-every-commitpush)
6. [Day-to-Day Workflow](#6-day-to-day-workflow-after-initial-setup)
7. [Quick Reference — Command Purpose Table](#7-quick-reference--command-purpose-table)
   - [Staging a single file](#staging-a-single-file--why-quote-the-filename)
   - [Managing remotes](#managing-remotes--the-three-commands-together)
8. [Troubleshooting: Fixing a Hijacked Parent Remote](#8-troubleshooting-fixing-a-hijacked-parent-remote)
9. [Undoing / Reverting Pushes](#9-undoing--reverting-pushes)
   - [Remove specific files/folders](#remove-specific-filesfolders-from-tracking-without-deleting-them-locally)
10. [Deleting a GitHub Repository](#10-deleting-a-github-repository)
11. [Recovery Path — Repo Has Wrong Content Baked Into History](#11-recovery-path--repo-has-wrong-content-baked-into-history)
12. [Isolating One Project from a Shared Parent Folder](#12-isolating-one-project-from-a-shared-parent-folder)

Every section below ends with a **⬆ Back to top** link.

---

## 1. Jenkins

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

Windows often saves `Jenkinsfile` as `Jenkinsfile.txt`. Jenkins requires the exact filename with no extension.

```bash
cd C:\Users\shiva\OneDrive\JavaSelenium\paimana_1point1
ren Jenkinsfile.txt Jenkinsfile
dir Jenkinsfile
```

`dir` should show `Jenkinsfile` with nothing after it — confirms the rename worked.

View file content directly in CMD:

```bash
type Jenkinsfile
```

Commit it:

```bash
git add Jenkinsfile
git commit -m "Add Jenkinsfile"
git push
```

[⬆ Back to top](#table-of-contents)

---

## 2. Creating a New GitHub Repository

Go to **github.com → New repository**

| Field | Value |
|---|---|
| Repository name | `paimana_1point1` |
| Visibility | **Private** |
| Add README | Off |
| Add .gitignore | None |
| Add license | None |

Leave README/.gitignore/license off whenever you already have local commits — GitHub creating its own copy causes a conflict on first push.

[⬆ Back to top](#table-of-contents)

---

## 3. Verify: Remote Linked, Committed, and Pushed Correctly

Run these checks any time after a push — don't assume it worked just because no error appeared.

| Check | Command | Expected result |
|---|---|---|
| Is a remote linked? | `git remote -v` | Shows your GitHub URL for both `(fetch)` and `(push)` — **not empty, not someone else's repo** |
| Was the commit saved? | `git log --oneline` | Shows your commit message at the top — confirms it's not still sitting unstaged |
| Is upstream tracking set? | `git status` | Says `Your branch is up to date with 'origin/main'` — confirms the push actually linked this branch to GitHub, not just uploaded once |
| Did everything actually land on GitHub? | Refresh the repo page in your browser | File list matches what's in your project folder — no leftover files from a different project |

If `git remote -v` shows a URL that isn't yours, or `git status` doesn't mention `origin/main`, stop here — something is misconfigured, and pushing further will make it worse. See [Section 8](#8-troubleshooting-fixing-a-hijacked-parent-remote) if the URL points at the wrong repo.

[⬆ Back to top](#table-of-contents)

---

## 4. Full Setup — Every New Project, Step by Step

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

### What each command does and when to use it

| Step | Command | Purpose |
|---|---|---|
| 1 | `cd <project-folder>` | Enter the exact project folder — never a parent folder |
| 2 | `git init` | Creates a **new, separate** `.git` in this folder |
| 3 | `dir /a:h .git` | **Verify** — confirms `.git` exists locally here, not inherited from a parent |
| 4 | `git add .` | Stages only this project's files |
| 5 | `git status` | **Verify** — paths should show as `src/...`, `pom.xml` with **no `../`** prefix. If you see `../`, you're in the wrong repo |
| 6 | `git commit -m "Initial commit"` | Saves the first commit locally |
| 7 | `git remote add origin <url>` | Links the local repo to the empty GitHub repo you created |
| 8 | `git branch -M main` | Sets the branch name to `main` |
| 9 | `git push -u origin main` | Uploads the commit and sets upstream tracking |

**Rename to `master` instead**, if you want consistency with an older repo:

```bash
git branch -M master
git push -u origin master
```

Check your current branch name first if unsure:

```bash
git branch
```

After step 9, run the checks in [Section 3](#3-verify-remote-linked-committed-and-pushed-correctly) above to confirm everything actually landed.

[⬆ Back to top](#table-of-contents)

---

## 5. Verification Commands — Use Before Every Commit/Push

| Command | What it tells you |
|---|---|
| `dir` | Lists files in the current folder — confirms you're in the right place |
| `git rev-parse --show-toplevel` | Prints the actual repo root — should be *this* project's path, not a parent's |
| `dir /a:h .git` | Confirms a `.git` folder physically exists here |
| `git remote -v` | Shows which GitHub URL this repo pushes to — should print nothing if not yet linked |
| `git branch` | Shows current branch name (`main` vs `master`) |
| `git status` | Shows staged/unstaged/untracked files — check paths have no `../` |
| `git log --oneline` | Shows commit history, one line per commit |
| `git diff` | Shows exact line changes before staging |
| `git ls-files` | Lists every file Git is currently tracking — ground truth of what will be pushed |

[⬆ Back to top](#table-of-contents)

---

## 6. Day-to-Day Workflow (After Initial Setup)

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
| Stage one specific file | `git add "filename.ext"` — e.g. `git add "playwright.config.ts"` |
| Stage multiple specific files | `git add "file1.ts" "file2.ts"` |
| Check what's staged/unstaged | `git status` |
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

Quotes matter whenever a filename has spaces or special characters — common in this project set (e.g. `"Coolections and Arrays"`). Without quotes, CMD treats each space-separated word as a *separate* argument and Git tries to stage files that don't exist.

```bash
git add "Jenkinsfile"
git add "playwright.config.ts"
git add "src/main/java/com/paimana/pages/HomePage.java"
```

Quotes are optional for simple filenames with no spaces, but using them every time removes the guesswork.

### Managing remotes — the three commands together

These three are used as a set whenever a repo is linked to the wrong GitHub URL, or you need to check/replace it.

| Step | Command | Result |
|---|---|---|
| 1. Check what's currently linked | `git remote -v` | Prints the URL for `(fetch)` and `(push)` — or nothing, if none is set |
| 2. Remove it | `git remote remove origin` | Unlinks the repo from that URL — local commits are untouched |
| 3. Re-link to the correct URL | `git remote add origin <correct-url>` | Points the repo at the right GitHub repository |

Full walkthrough of when and why to use this sequence: see [Section 8 — Troubleshooting: Fixing a Hijacked Parent Remote](#8-troubleshooting-fixing-a-hijacked-parent-remote).

[⬆ Back to top](#table-of-contents)

---

## 8. Troubleshooting: Fixing a Hijacked Parent Remote

**Symptom:** you run `git push` inside a project subfolder, but GitHub shows files from *other* projects too — or a `git status` inside your project shows paths prefixed with `../`.

**Cause:** the subfolder never had its own `.git`. Git walked up the directory tree and found a `.git` in a *parent* folder instead, so every command has actually been operating on the parent repo — which may contain many unrelated projects.

**Check first — is this your problem?**

```bash
cd <project-folder>
git rev-parse --show-toplevel
```

If this prints the *parent* folder's path instead of your project's own path, you've found the cause.

**Fix — clean the parent, then give the project its own repo:**

```bash
cd <parent-folder>
git remote -v
git remote remove origin
```

`git remote -v` after removal should print **nothing** — confirms it's cleared. Then set up the project folder properly using [Section 4](#4-full-setup--every-new-project-step-by-step) above.

[⬆ Back to top](#table-of-contents)

---

## 9. Undoing / Reverting Pushes

### Remove specific files/folders from tracking without deleting them locally

Use this when a commit accidentally included the wrong folder (e.g. a sibling project nested inside) — the most common cause of the "wrong content" problem in this doc.

```bash
git rm -r --cached <folder-or-file>
git commit -m "Remove folder from tracking"
git push
```

### Safest — revert the last commit with a new commit

Keeps history intact. Safe even if others have already pulled the bad commit.

```bash
git revert HEAD
git push
```

### Rewrite history — reset to an earlier commit, then force-push

Only do this if certain no one else has pulled the bad commits. This overwrites GitHub's history.

```bash
git log --oneline
git reset --hard <commit-hash-to-go-back-to>
git push --force
```

### Undo the last local commit before it's pushed

Uncommits but keeps changes staged, ready to recommit correctly.

```bash
git reset --soft HEAD~1
```

### Discard all local uncommitted changes

```bash
git checkout -- .
```

[⬆ Back to top](#table-of-contents)

---

## 10. Deleting a GitHub Repository

Use when a repo was created by mistake or ended up with the wrong content (e.g. it inherited files from a parent folder's `.git`).

1. Open the repo on GitHub → **Settings** (top nav)
2. Scroll to the bottom → **Danger Zone**
3. Click **Delete this repository**
4. Type the full name to confirm: `owner/repo-name`
5. Click **I understand the consequences, delete this repository**

There is no undo. Only delete a repo you're certain you want gone — if in doubt, make it private instead.

[⬆ Back to top](#table-of-contents)

---

## 11. Recovery Path — Repo Has Wrong Content Baked Into History

If a repo's commit history already contains the wrong files (rather than just the working directory), delete-and-recreate is simpler than surgical fixes.

| Step | Command / Action |
|---|---|
| 1 | Delete the repo on GitHub (Settings → Danger Zone) |
| 2 | Create a new empty repo, same name, no README/gitignore/license |
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

## 12. Isolating One Project from a Shared Parent Folder

Use this when the parent folder (e.g. `JavaSelenium`) has an old `.git` that all subfolders have been inheriting from, and you want **only one specific subfolder** — such as `PAIMANA_PlaywrightMavenJavaSelenium` — to have its own clean, independent repo.

**Symptom:** `git status` inside the parent folder lists dozens of unrelated project folders, and `git rev-parse --show-toplevel` from inside your target subfolder prints the *parent's* path instead of its own.

### Step 1 — Remove everything else from the parent repo first

Before touching your target project, stop the parent repo from tracking every other folder in it. This step actually involves **three different fixes layered together** — each solves a different part of the problem, and skipping any one of them leaves the mess half-solved.

| Option | What it undoes | What it leaves behind | Use when |
|---|---|---|---|
| **Unstage** — `git restore --staged <folder>` | Removes files from the "about to commit" list | Files are still **tracked** — Git still watches them and will offer to re-stage them next time something changes | A folder shows under "Changes to be committed" and you don't want it committed |
| **Remove** — `git rm -r --cached <folder>` | Stops Git from tracking the folder entirely | Files stay safely on disk, but move into the "untracked" list in `git status` | You want Git to stop watching a folder for good, without deleting anything |
| **Ignore** — add to `.gitignore` | Stops untracked folders from ever reappearing in `git status` | Nothing — this is the permanent fix | You never want this folder tracked again, by accident or otherwise |

None of the three alone is sufficient here — **use all three, in this order**:

```bash
cd C:\Users\shiva\OneDrive\JavaSelenium

# 1. Unstage first (undo the pending commit)
git restore --staged paimana-automation_1.1
git restore --staged paimana_1point1

# 2. Remove from tracking (stop watching them, keep files on disk)
git rm -r --cached paimana-automation_1.1
git rm -r --cached paimana_1point1

# 3. Ignore permanently (stop them resurfacing, ever)
# — add all 55 folder names to .gitignore, as listed below
```

For every other folder currently listed as "Untracked files" in `git status`, skip Unstage/Remove (they were never tracked) and go straight to Ignore — add each one to `.gitignore` below.

Create or edit `.gitignore` in `JavaSelenium` and list every folder except the one you're keeping:

```
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
... (add every other project folder here)
```

Do **not** add `PAIMANA_PlaywrightMavenJavaSelenium/` to this list — that's the one folder you want tracked.

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

The parent repo itself is not being deleted here — just cleared of everything except optionally your target project, or left entirely unused going forward.

**Confirm the cleanup worked:**

```bash
git status
```

Should now show only `.gitignore` and your target folder — nothing else.

### Step 2 — Give the target project its own independent repo

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

`git rev-parse` must now print **this project's own path**, not the parent's. This is the check that confirms isolation actually worked.

### Step 3 — Stage, verify, and commit only this project

```bash
git add .
git status
```

Every path listed must start with `src/`, `suites/`, `pom.xml`, etc. — **no `../`** prefix anywhere. If `../` appears, `git init` did not run in the right folder — go back to Step 2.

```bash
git commit -m "Initial commit: PAIMANA Playwright Java Maven project"
```

### Step 4 — Delete the old GitHub repo and create a fresh one

The existing `PAIMANA_PlaywrightMavenJavaSelenium` repo on GitHub has the wrong content baked into its history — delete and recreate rather than trying to fix it in place.

1. github.com → open the repo → **Settings** → **Danger Zone** → **Delete this repository** → type the name to confirm
2. **New repository** → same name → Private → no README/gitignore/license

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

`git status` should say `Your branch is up to date with 'origin/master'`. Refresh the GitHub page — the file list should show **only** `src`, `suites`, `pom.xml`, `README.md`, `.classpath`, `.project`, `.settings`, `.gitignore`.

### Summary table

| Step | Command | Where |
|---|---|---|
| 1 | `git status` → `git restore --staged <folder>` for each unwanted folder | `JavaSelenium` |
| 2 | Add unwanted folders to `.gitignore` | `JavaSelenium` |
| 3 | `git remote -v` → `git remote remove origin` if needed | `JavaSelenium` |
| 4 | `dir /a:h .git` — confirm no repo exists yet | Target project folder |
| 5 | `git init` | Target project folder |
| 6 | `git rev-parse --show-toplevel` — confirm it prints the project's own path | Target project folder |
| 7 | `git add .` → `git status` — confirm no `../` paths | Target project folder |
| 8 | `git commit -m "Initial commit"` | Target project folder |
| 9 | Delete + recreate the GitHub repo | github.com |
| 10 | `git remote add origin <url>` → `git branch -M master` → `git push -u origin master` | Target project folder |

### Avoiding this problem for every future project

Unstage / Remove / Ignore are **repair tools** — used once, to fix a mistake that already happened. They are not meant to be an ongoing workflow. `.gitignore` in the parent folder has no effect on a subfolder that already has its own `.git` — Git stops looking at parent folders the moment it finds one in the current directory.

**The rule that prevents needing this section again:** the moment a new project folder is created, before writing any code:

```bash
cd <new-project-folder>
git init
git rev-parse --show-toplevel
```

If that last command prints the new folder's own path (not the parent's), the project is fully isolated — Unstage, Remove, and Ignore will never be needed for it again.

[⬆ Back to top](#table-of-contents)
