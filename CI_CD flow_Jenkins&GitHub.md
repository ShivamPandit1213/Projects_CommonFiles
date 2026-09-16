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
   - [Creating a new branch](#creating-a-new-branch--create-switch-then-push)
   - [Checking how many branches the GitHub repo has](#checking-how-many-branches-the-github-repo-has)
8. [Troubleshooting: Fixing a Hijacked Parent Remote](#8-troubleshooting-fixing-a-hijacked-parent-remote)
9. [Undoing / Reverting Pushes](#9-undoing--reverting-pushes)
   - [Remove specific files/folders](#remove-specific-filesfolders-from-tracking-without-deleting-them-locally)
10. [Deleting a GitHub Repository](#10-deleting-a-github-repository)
11. [Recovery Path — Repo Has Wrong Content Baked Into History](#11-recovery-path--repo-has-wrong-content-baked-into-history)
12. [Isolating One Project from a Shared Parent Folder](#12-isolating-one-project-from-a-shared-parent-folder)
13. [Push Rejected — "fetch first"](#13-push-rejected--fetch-first)
   - [Conflict: same file on both sides with different content](#conflict-same-file-on-both-sides-with-different-content)
14. [Merge Conflicts — Other Scenarios and How to Fix Each](#14-merge-conflicts--other-scenarios-and-how-to-fix-each)
   - [Which state am I in?](#which-state-am-i-in)
   - [Quick lookup — message to scenario](#quick-lookup--message-to-scenario)
   - [1 — Both sides edited the same lines](#scenario-1--both-sides-edited-the-same-lines)
   - [2 — Both sides created the same new file](#scenario-2--both-sides-created-the-same-new-file)
   - [3 — Edited on one side, deleted on the other](#scenario-3--edited-on-one-side-deleted-on-the-other)
   - [4 — Binary file](#scenario-4--binary-file-screenshot-excel-jar-pdf)
   - [5 — Whole file conflicts (line endings)](#scenario-5--the-whole-file-conflicts-after-a-one-line-change-line-endings)
   - [6 — Several files conflict at once](#scenario-6--several-files-conflict-at-once)
   - [7 — Pull refused: local changes would be overwritten](#scenario-7--pull-refused-your-local-changes-would-be-overwritten-by-merge)
   - [8 — Conflict during a rebase pull](#scenario-8--conflict-during-a-rebase-pull)
   - [9 — Conflict when merging a branch locally](#scenario-9--conflict-when-merging-a-branch-locally)
   - [10 — Conflict on a GitHub pull request](#scenario-10--conflict-on-a-github-pull-request)
   - [11 — Conflict markers got committed by mistake](#scenario-11--conflict-markers-got-committed-by-mistake)
   - [Backing out — abort](#backing-out--abort-and-return-to-before-the-pull)

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
| Create a new branch and switch to it | `git checkout -b <branch-name>` (or `git switch -c <branch-name>`) |
| Create a branch without switching to it | `git branch <branch-name>` |
| Switch to an existing branch | `git checkout <branch-name>` (or `git switch <branch-name>`) |
| Push a new branch to GitHub (first time) | `git push -u origin <branch-name>` |
| List branches on GitHub (remote) | `git branch -r` |
| List all branches (local + remote) | `git branch -a` |
| Ask GitHub directly for its branch list | `git ls-remote --heads origin` |
| Count how many branches are on GitHub | `git ls-remote --heads origin \| find /c "refs/heads/"` |
| Refresh remote branch list (drop deleted ones) | `git fetch --prune` |
| Download GitHub's commits without merging them | `git fetch origin` |
| See commits GitHub has that you don't | `git log --oneline main..origin/main` |
| Merge GitHub's commits into your local branch | `git pull --no-rebase origin main` |
| Conflict — use local file, remove GitHub's | `git checkout --ours <file>` |
| Conflict — remove local file, use GitHub's | `git checkout --theirs <file>` |
| Pull an old version of a file out of history | `git show <commit-hash>:<file> > <new-file-name>` |
| Conflict — delete the file instead of keeping it | `git rm <file>` |
| List files still conflicted | `git diff --name-only --diff-filter=U` |
| Find leftover conflict markers in tracked files | `git grep -n "<<<<<<<"` |
| Give up on a merge/pull, restore pre-pull state | `git merge --abort` |
| Give up on a rebase | `git rebase --abort` |
| Continue a rebase after fixing a conflict | `git rebase --continue` |
| Park uncommitted edits / bring them back | `git stash` / `git stash pop` |
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

### Creating a new branch — create, switch, then push

A new branch exists only on your machine until it is pushed. Three steps every time:

```bash
git checkout -b feature-login
git branch
git push -u origin feature-login
```

| Step | Command | Result |
|---|---|---|
| 1. Create and switch | `git checkout -b <branch-name>` | Creates the branch from your current commit and switches to it in one go. Newer Git also accepts `git switch -c <branch-name>` — same result |
| 2. Verify | `git branch` | The `*` should sit next to the new branch name — confirms you're on it before committing anything |
| 3. Push and set tracking | `git push -u origin <branch-name>` | Uploads the branch to GitHub and links it, so plain `git push` works from then on — same idea as step 9 in [Section 4](#4-full-setup--every-new-project-step-by-step) |

`git branch <branch-name>` on its own creates the branch but leaves you on the current one — switch to it with `git checkout <branch-name>` (or `git switch <branch-name>`) when ready.

Branch names can't contain spaces — use hyphens (`feature-login`) or slashes (`feature/login`).

### Checking how many branches the GitHub repo has

`git branch` on its own lists **local** branches only — it says nothing about what's on GitHub. Use these instead:

| Command | What it shows |
|---|---|
| `git branch -r` | Remote branches as of your **last fetch** — each listed as `origin/<name>` |
| `git branch -a` | Local **and** remote branches together — local ones plain, remote ones prefixed `remotes/origin/` |
| `git ls-remote --heads origin` | Asks GitHub **right now** for every branch it has, one per line — no fetch needed, always current |
| `git ls-remote --heads origin \| find /c "refs/heads/"` | Same query, but prints just the **number** of branches (Windows CMD). In Git Bash use `\| wc -l` instead |

`git branch -r` can be stale — a branch deleted on GitHub still shows up until you refresh the list:

```bash
git fetch --prune
git branch -r
```

`git ls-remote --heads origin` never has this problem because it talks to GitHub directly, so it's the one to trust when the exact number matters.

**Browser check:** open the repo on github.com — the count is shown next to the branch dropdown (e.g. **3 Branches**). Click it to see every branch, who last committed to it, and how far ahead/behind the default branch it is.

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

---

## 13. Push Rejected — "fetch first"

**Symptom:** `git push` fails with:

```
 ! [rejected]        main -> main (fetch first)
error: failed to push some refs to 'https://github.com/...'
hint: Updates were rejected because the remote contains work that you do not
hint: have locally.
```

**Cause:** the branch on GitHub has a commit your local repo doesn't have. It got there without you — a README added or a file edited on the github.com website, a branch or pull request merged in the browser, or a push from another machine. Git refuses the push because accepting it would erase that commit. This is a safety feature, not a problem with your project.

**Why `git status` still said "up to date with 'origin/main'":** that line compares your branch to your *last-fetched snapshot* of GitHub, not to GitHub itself. The snapshot only refreshes when you run `git fetch` or `git pull` — the same staleness problem as `git branch -r` in [Section 7](#checking-how-many-branches-the-github-repo-has).

**Check first — is this your problem?**

```bash
git ls-remote --heads origin
git log --oneline -1 origin/main
```

The first shows the commit GitHub's `main` is *actually* on; the second shows the commit your local snapshot *thinks* it's on. If the two hashes differ, this is the cause.

Real example from `paimana_1point1`: `ls-remote` showed `main` at `4523a37`, local `origin/main` was at `b080ba4`, and `4523a37` appeared nowhere in `git log` — a commit existed on GitHub that had never been downloaded.

### Fix — download the missing commit, merge it into yours, then push

The `#` lines are explanations for reading, not typing — CMD doesn't understand `#`, so only enter the command lines.

```bash
# 1. Download GitHub's latest commits WITHOUT touching any of your files.
#    Refreshes your local snapshot of origin/main to match GitHub.
git fetch origin

# 2. Show what GitHub has that you don't.
#    "main..origin/main" means "in origin/main but NOT in main".
#    Expect one or more lines. If it prints nothing, skip to step 6.
git log --oneline main..origin/main

# 3. Optional — list the files that commit changed.
#    Any file here that you ALSO changed locally = a conflict is coming in step 4.
git show --stat origin/main

# 4. Merge GitHub's commit into your local main.
#    --no-rebase = keep both commits exactly as they are and add one "merge commit"
#    that joins them (safest option, nothing rewritten).
#    An editor may open with a pre-written message — just save and close it.
#    If it stops with CONFLICT instead, jump to the conflict section below.
git pull --no-rebase origin main

# 5. Confirm it worked: top line is a "Merge branch 'main'..." commit, with
#    your commit and GitHub's commit both underneath it.
git log --oneline

# 6. Push — succeeds now because your main contains everything GitHub's main has.
git push
```

### Conflict: same file on both sides with different content

Git found two different versions of one file and can't choose for you. The message tells you which kind:

| Message | Meaning |
|---|---|
| `CONFLICT (content): Merge conflict in <file>` | The file already existed; you and GitHub both edited the same lines |
| `CONFLICT (add/add): Merge conflict in <file>` | The file is **new on both sides** — you created it locally, and a file with the same name was created on GitHub with different content (e.g. a `README.md` written on the website while you also added one locally) |

Nothing is broken — the merge is **paused**, waiting for you. `git status` says "You have unmerged paths" and lists the file as `both modified` or `both added`. Until you resolve it and commit, `git log` shows no merge commit and `git push` keeps failing.

**Naming trap:** during a merge, **ours** = your local branch (`HEAD`), **theirs** = the commit coming in from GitHub.

Pick **one** of the three:

| Want | Command |
|---|---|
| **Use the local file, remove GitHub's** | `git checkout --ours <file>` |
| **Remove the local file, use GitHub's** | `git checkout --theirs <file>` |
| Keep both / mix them by hand | Open the file in VS Code or Notepad and edit it — see C below |

```bash
# A. Keep YOUR local version, discard GitHub's
git checkout --ours README.md

# B. Keep GITHUB's version, discard your local one
git checkout --theirs README.md

# C. Keep both: the file currently holds both versions between marker lines:
#    <<<<<<< HEAD        ← your version starts here
#    =======             ← divider
#    >>>>>>> 4523a37     ← GitHub's version ends here (hash = GitHub's commit)
#    Delete ONLY the three marker lines, reorder the content if you like, save.
#    VS Code shows "Accept Current / Incoming / Both Changes" buttons instead —
#    Current = your local version, Incoming = GitHub's.
```

Run A **or** B, never both — each one overwrites the file with a single version, and whichever you ran **last** wins. `Updated 1 path from the index` is the confirmation. Check what you're holding before moving on:

```bash
type README.md
```

Then finish the merge — same in all three cases:

```bash
git add README.md                                   # marks the conflict resolved — after this, --ours/--theirs no longer work for this file
git status                                          # should say "All conflicts fixed but you are still merging"
git commit -m "Merge GitHub main into local main"   # creates the merge commit
git log --oneline                                   # merge commit on top, your commit and GitHub's both below it
git push
```

**Getting the dropped version back later:** whichever version you discarded is still in history under its commit. Pull it out into a separate file:

```bash
git show <commit-hash>:<file> > <new-file-name>
git add <new-file-name>
git commit -m "Keep other version of <file> as <new-file-name>"
git push
```

To find the hash, run `git log --oneline`: GitHub's version is the top commit that step 2 listed; your version is your own commit sitting directly under the merge commit.

Real example from `paimana_1point1`: `--ours` kept the local `README.md`, so GitHub's Git-guide README was dropped — but it still lived in commit `4523a37`, recoverable with `git show 4523a37:README.md > GIT_GUIDE.md`.

Other kinds of conflict — a file deleted on one side, binary files, line endings, several files at once, rebase, branch merges, GitHub pull requests — are in [Section 14](#14-merge-conflicts--other-scenarios-and-how-to-fix-each).

**Never use `git push --force` to get past this error.** It would delete the GitHub commit from the branch — and you don't know what that commit was until step 2 shows you.

**The one-line rule:** `push` is only allowed when your branch already contains every commit the remote has. `fetch` downloads the missing commit, `pull` adds it to your branch, and then `push` goes through.

### Summary table

| Step | Command | Purpose |
|---|---|---|
| 1 | `git ls-remote --heads origin` vs `git log --oneline -1 origin/main` | Confirm GitHub is ahead of your local snapshot |
| 2 | `git fetch origin` | Download the missing commit(s) — your files are untouched |
| 3 | `git log --oneline main..origin/main` | See exactly what GitHub has that you don't |
| 4 | `git pull --no-rebase origin main` | Merge it into your local branch |
| 5 | `git checkout --ours <file>` (keep local) **or** `git checkout --theirs <file>` (keep GitHub's) **or** edit by hand → `git add <file>` → `git commit -m "..."` | Only if step 4 reported a conflict |
| 6 | `git push` | Now succeeds |

[⬆ Back to top](#table-of-contents)

---

## 14. Merge Conflicts — Other Scenarios and How to Fix Each

[Section 13](#conflict-same-file-on-both-sides-with-different-content) covers the case already hit once — the same file changed on both sides. This section is the full list: every other way a conflict shows up during `pull` / `merge` / `rebase`, what the message means, and the exact commands to get out of it.

**The pattern is the same in every scenario:**

| Step | What to do | Command |
|---|---|---|
| 1 | Fix each conflicted file — edit by hand, or pick a side | hand-edit · `git checkout --ours <file>` · `git checkout --theirs <file>` · `git rm <file>` |
| 2 | Tell Git that file is done | `git add <file>` (or `git rm <file>` if the answer is "delete it") |
| 3 | Finish the operation you were in the middle of | merge or pull → `git commit` · rebase → `git rebase --continue` · stash pop → `git stash drop` |
| 4 | Push | `git push` |
| Escape hatch | Give up and put everything back exactly as it was | `git merge --abort` or `git rebase --abort` |

As in Section 13, the `#` lines in code blocks are notes for reading, not typing.

### Which state am I in?

Run `git status` — its first lines say exactly what Git is waiting for:

| `git status` says | Meaning | Finish with |
|---|---|---|
| `Unmerged paths:` then `both modified` / `both added` / `deleted by us` / `deleted by them` | A conflict still needs resolving | Fix the file, then `git add <file>` (or `git rm <file>`) |
| `All conflicts fixed but you are still merging.` | Every file is resolved; the merge just needs its commit | `git commit -m "..."` |
| `You are currently rebasing` / `interactive rebase in progress` | The conflict happened during a rebase | `git add <file>` → `git rebase --continue` |
| `Your branch is ahead of 'origin/main' by N commits.` | Done locally, not pushed yet | `git push` |

### Quick lookup — message to scenario

| What you see | Go to |
|---|---|
| `CONFLICT (content): Merge conflict in <file>` | [Scenario 1](#scenario-1--both-sides-edited-the-same-lines) |
| `CONFLICT (add/add): Merge conflict in <file>` | [Scenario 2](#scenario-2--both-sides-created-the-same-new-file) |
| `CONFLICT (modify/delete): <file> deleted in ... and modified in ...` | [Scenario 3](#scenario-3--edited-on-one-side-deleted-on-the-other) |
| `warning: Cannot merge binary files: <file>` | [Scenario 4](#scenario-4--binary-file-screenshot-excel-jar-pdf) |
| Markers wrap the **entire** file though you changed one line | [Scenario 5](#scenario-5--the-whole-file-conflicts-after-a-one-line-change-line-endings) |
| Several `CONFLICT` lines from one pull | [Scenario 6](#scenario-6--several-files-conflict-at-once) |
| `error: Your local changes to the following files would be overwritten by merge` | [Scenario 7](#scenario-7--pull-refused-your-local-changes-would-be-overwritten-by-merge) |
| `error: could not apply <hash>...` and a `git rebase --continue` hint | [Scenario 8](#scenario-8--conflict-during-a-rebase-pull) |
| Conflict after `git merge <branch>` | [Scenario 9](#scenario-9--conflict-when-merging-a-branch-locally) |
| GitHub: "This branch has conflicts that must be resolved" | [Scenario 10](#scenario-10--conflict-on-a-github-pull-request) |
| `<<<<<<< HEAD` lines showing up on GitHub or breaking the build | [Scenario 11](#scenario-11--conflict-markers-got-committed-by-mistake) |
| Don't want to deal with it right now | [Backing out](#backing-out--abort-and-return-to-before-the-pull) |

### Scenario 1 — Both sides edited the same lines

```
CONFLICT (content): Merge conflict in src/main/resources/config.properties
```

**Cause:** the file already existed, and the same lines were changed locally and on GitHub in different ways.

```bash
# Option A — hand-merge: open the file, delete the <<<<<<< / ======= / >>>>>>> marker lines,
#            keep the lines you want from each side, save.
# Option B — take one side whole (run ONE of these, not both — the last one wins):
git checkout --ours <file>       # keep local, drop GitHub's
git checkout --theirs <file>     # keep GitHub's, drop local

git add <file>
git commit -m "Merge GitHub main into local main"
git push
```

Full detail, including the `ours` / `theirs` naming trap and how to get the dropped version back: [Section 13](#conflict-same-file-on-both-sides-with-different-content).

### Scenario 2 — Both sides created the same new file

```
CONFLICT (add/add): Merge conflict in README.md
```

**Cause:** you created a file locally, and a file with the same name was created on GitHub (typically on the website) with different content. Git has no common starting point to merge from, so it usually flags the whole file.

Resolution is identical to Scenario 1 — pick a side with `--ours` / `--theirs`, or hand-merge. The worked example with real output is in [Section 13](#conflict-same-file-on-both-sides-with-different-content).

### Scenario 3 — Edited on one side, deleted on the other

```
CONFLICT (modify/delete): suites/env-check.xml deleted in HEAD and modified in 4523a37.
Version 4523a37 of suites/env-check.xml left in tree.
```

**Cause:** one side deleted the file, the other changed it. `deleted in HEAD` = you deleted it locally and GitHub edited it; `deleted in 4523a37` = the reverse. Git leaves the edited version in your folder and asks which you meant. There are no markers to edit — the choice is keep or delete.

```bash
# Keep the file (the edited version is already sitting in your folder):
git add <file>

# — or — delete it for good:
git rm <file>

git commit -m "Merge GitHub main into local main"
git push
```

`git status` shows this one as `deleted by us` or `deleted by them` under Unmerged paths.

### Scenario 4 — Binary file (screenshot, Excel, jar, PDF)

```
warning: Cannot merge binary files: screenshots/login.png (HEAD vs. 4523a37)
CONFLICT (content): Merge conflict in screenshots/login.png
```

**Cause:** both sides changed a file that isn't text, so Git can't put markers inside it — there is nothing to hand-merge. Common in test projects: screenshots, `.xlsx` test data, `.jar`, `.pdf`.

```bash
git checkout --ours <file>       # keep your version
git checkout --theirs <file>     # keep GitHub's version        (one or the other)

git add <file>
git commit -m "Merge GitHub main into local main"
git push
```

If you need both, keep one under the original name and pull the other out of history under a new name: `git show <commit-hash>:<file> > <new-file-name>`, then `git add` / `git commit` / `git push` it.

### Scenario 5 — The whole file conflicts after a one-line change (line endings)

**Symptom:** you changed one or two lines, but the conflict markers wrap the **entire** file — every line looks different. `git diff` shows `^M` at the ends of lines. Usually preceded by warnings like `CRLF will be replaced by LF the next time Git touches it`.

**Cause:** one side saved the file with Windows line endings (CRLF), the other with Unix (LF), so Git sees every line as changed. The content is the same — only the invisible end-of-line characters differ.

```bash
# 1. Back out of the half-done merge — nothing is lost
git merge --abort

# 2. Redo the pull, telling Git to ignore end-of-line differences while merging
git pull --no-rebase -X ignore-space-at-eol origin main

# 3a. If it completes cleanly (an editor may open — save and close), you're done:
git push

# 3b. If a CONFLICT remains, it's now only the lines you really changed — fix as in Scenario 1:
git add <file>
git commit -m "Merge GitHub main into local main"
git push
```

**Prevent it:** make sure the project's `.gitattributes` contains the line `* text=auto`. Git then stores every text file with LF and hands you CRLF on Windows, so both sides always agree.

### Scenario 6 — Several files conflict at once

```
CONFLICT (content): Merge conflict in pom.xml
CONFLICT (content): Merge conflict in Jenkinsfile
CONFLICT (add/add): Merge conflict in README.md
Automatic merge failed; fix conflicts and then commit the result.
```

**Cause:** nothing special — one pull, several files touched on both sides. Each file is resolved on its own (whichever scenario above applies to it); the merge is finished **once**, after all of them.

```bash
# List every file still waiting — repeat until it prints nothing
git diff --name-only --diff-filter=U

# For each file: fix it (hand-edit / --ours / --theirs / git rm), then mark it done
git add <file>

# When the list is empty:
git status                                          # should say "All conflicts fixed but you are still merging"
git commit -m "Merge GitHub main into local main"
git push
```

`git add .` at the end marks everything at once — fine, but only after you have actually looked at every file. A file still containing `<<<<<<<` markers gets committed as-is, markers and all (that's Scenario 11).

### Scenario 7 — Pull refused: "Your local changes would be overwritten by merge"

```
error: Your local changes to the following files would be overwritten by merge:
        src/main/resources/config.properties
Please commit your changes or stash them before you merge.
Aborting
```

**Cause:** not a conflict yet. Git stopped *before* merging because you have uncommitted edits in a file the incoming commit also changes, and merging would destroy them. Nothing has been touched.

```bash
# Option A (usual) — commit your work first, then pull
git add .
git commit -m "what changed"
git pull --no-rebase origin main

# Option B — not ready to commit? Park the edits, pull, bring them back
git stash                                   # sets your uncommitted edits aside; folder returns to the last commit
git pull --no-rebase origin main
git stash pop                               # re-applies your edits on top of the new code
```

If `git stash pop` itself reports `CONFLICT`, fix the file as in Scenario 1, `git add <file>`, then finish with `git stash drop` (not `git commit` — nothing is being merged), and carry on with the usual add / commit / push.

### Scenario 8 — Conflict during a rebase pull

If you ever use `git pull --rebase` instead of `--no-rebase`, a conflict looks like this:

```
CONFLICT (content): Merge conflict in Jenkinsfile
error: could not apply e2c1786... Update suite's testng file
hint: Resolve all conflicts manually, mark them as resolved with
hint: "git add/rm <conflicted_files>", then run "git rebase --continue".
```

**Cause:** a rebase replays your commits one at a time on top of GitHub's and stops at the first one that clashes. Two things differ from a merge:

1. **`--ours` and `--theirs` are swapped.** During a rebase Git stands on GitHub's commit and applies yours on top, so `--ours` = **GitHub's** version and `--theirs` = **your** version — the reverse of Section 13. Hand-merging avoids the trap: the `<<<<<<< HEAD` side is GitHub's, the `>>>>>>>` side is yours.
2. **Finish with `git rebase --continue`, not `git commit`.**

```bash
# fix the file: hand-edit, or  --ours = GitHub's / --theirs = yours
git add <file>
git rebase --continue          # an editor may open to confirm the commit message — save and close
                               # if it stops again on the next commit, repeat
git push
```

To back out: `git rebase --abort` — your branch returns to exactly how it was before the pull.

### Scenario 9 — Conflict when merging a branch locally

Merging a branch (`git checkout main` → `git merge QA_Testing`) can conflict exactly like a pull:

```
Auto-merging suites/smoke.xml
CONFLICT (content): Merge conflict in suites/smoke.xml
Automatic merge failed; fix conflicts and then commit the result.
```

**Cause:** same mechanics — a pull *is* a fetch followed by a merge. Here **ours** = the branch you are standing on (`main`), **theirs** = the branch being merged in (`QA_Testing`).

```bash
# fix each file: hand-edit, or
git checkout --ours <file>       # keep main's version
git checkout --theirs <file>     # keep QA_Testing's version

git add <file>
git commit -m "Merge QA_Testing into main"
git push
```

Back out with `git merge --abort`.

### Scenario 10 — Conflict on a GitHub pull request

**Symptom:** on github.com the pull request shows **"This branch has conflicts that must be resolved"** and the Merge button is disabled.

**Cause:** `main` moved on after the branch was created — `QA_Testing` was branched, then `main` received new commits — and both touched the same lines. GitHub won't merge until someone chooses.

**Option A — in the browser (simple text conflicts):** click **Resolve conflicts** → GitHub opens the file with the same `<<<<<<<` / `=======` / `>>>>>>>` markers → edit, delete the markers → **Mark as resolved** → **Commit merge**. The PR becomes mergeable. GitHub refuses this route for binary files or large conflicts — use Option B.

**Option B — locally (any conflict, including binary):** bring `main` into the branch, fix it there, push the branch — the PR updates itself.

```bash
git fetch origin
git checkout QA_Testing
git pull --no-rebase origin main        # merges main INTO the branch — conflicts show up here
# fix each file (ours = QA_Testing, theirs = main), then:
git add <file>
git commit -m "Merge main into QA_Testing"
git push                                # PR now shows "This branch has no conflicts with the base branch"
```

### Scenario 11 — Conflict markers got committed by mistake

**Symptom:** a file on GitHub shows `<<<<<<< HEAD` / `=======` / `>>>>>>>` lines, or the build fails with a strange parse error (Maven on `pom.xml`, Jenkins on `Jenkinsfile`, TestNG on a suite XML).

**Cause:** `git add .` was run while a file still contained markers, so the half-resolved file was committed and pushed as-is.

```bash
# Find every tracked file that still contains markers
git grep -n "<<<<<<<"

# Open each one, delete the marker lines, keep the correct content, save. Then:
git add <file>
git commit -m "Remove leftover conflict markers"
git push
```

### Backing out — abort and return to before the pull

Any time a conflict is more than you want to deal with right now:

```bash
git status              # tells you whether you're mid-merge or mid-rebase
git merge --abort       # if it said "you are still merging" or listed Unmerged paths after a pull / merge
git rebase --abort      # if it said "You are currently rebasing"
```

Both put every file back exactly as it was before the pull — your commits, your files, nothing lost. GitHub's commits stay downloaded (the fetch already happened), so `git log --oneline main..origin/main` still shows what's waiting. Run the pull again whenever you're ready.

**Never** try to escape a conflict with `git push --force` (it deletes GitHub's commits) or by deleting the `.git` folder.

### Conflict summary table

| # | Scenario | Fix | Finish |
|---|---|---|---|
| 1 | Both edited the same lines | hand-edit, or `git checkout --ours <file>` / `--theirs <file>` → `git add <file>` | `git commit` → `git push` |
| 2 | Both created the same new file | same as 1 | `git commit` → `git push` |
| 3 | Edited on one side, deleted on the other | keep: `git add <file>` — delete: `git rm <file>` | `git commit` → `git push` |
| 4 | Binary file | `git checkout --ours <file>` / `--theirs <file>` → `git add <file>` | `git commit` → `git push` |
| 5 | Whole file conflicts (line endings) | `git merge --abort` → `git pull --no-rebase -X ignore-space-at-eol origin main` | `git push` |
| 6 | Several files at once | `git diff --name-only --diff-filter=U` → fix + `git add` each | one `git commit` → `git push` |
| 7 | Pull refused (uncommitted edits) | `git add .` → `git commit` → pull again, or `git stash` → pull → `git stash pop` | usual `git push` |
| 8 | Conflict during a rebase | fix (`--ours` = GitHub's, `--theirs` = yours — swapped) → `git add <file>` | `git rebase --continue` → `git push` |
| 9 | Merging a branch locally | fix (`--ours` = current branch, `--theirs` = incoming branch) → `git add <file>` | `git commit` → `git push` |
| 10 | GitHub pull request | **Resolve conflicts** button, or merge `main` into the branch locally and push | PR becomes mergeable |
| 11 | Markers committed by mistake | `git grep -n "<<<<<<<"` → clean each file → `git add <file>` | `git commit` → `git push` |
| — | Give up for now | `git merge --abort` or `git rebase --abort` | nothing lost; pull again later |

[⬆ Back to top](#table-of-contents)
