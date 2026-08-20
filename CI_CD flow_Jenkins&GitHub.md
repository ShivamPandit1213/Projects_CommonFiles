# PAIMANA — Jenkins & GitHub Command Reference

[⬆ Back to top](#table-of-contents)

---

## Table of Contents

1. [Jenkins](#1-jenkins)
   - [Start Jenkins with JDK 21](#start-jenkins-with-jdk-21)
   - [Fixing Jenkinsfile.txt → Jenkinsfile](#fixing-jenkinsfiletxt--jenkinsfile)
2. [Creating a New GitHub Repository](#2-creating-a-new-github-repository)
3. [Fixing a Hijacked Parent Remote](#3-fixing-a-hijacked-parent-remote)
4. [Linking a Project to GitHub and Pushing](#4-linking-a-project-to-github-and-pushing)
5. [Full Setup — Every New Project, Step by Step](#5-full-setup--every-new-project-step-by-step)
6. [Verification Commands](#6-verification-commands--use-before-every-commitpush)
7. [Day-to-Day Workflow](#7-day-to-day-workflow-after-initial-setup)
8. [Quick Reference — Command Purpose Table](#8-quick-reference--command-purpose-table)
9. [Deleting a GitHub Repository](#9-deleting-a-github-repository)
10. [Undoing / Reverting Pushes](#10-undoing--reverting-pushes)
11. [Recovery Path — Repo Has Wrong Content Baked Into History](#11-recovery-path--repo-has-wrong-content-baked-into-history)

[⬆ Back to top](#paimana--jenkins--github-command-reference) — this link appears at the end of every section below.

[⬆ Back to top](#table-of-contents)

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

## 3. Fixing a Hijacked Parent Remote

If a subfolder never got its own `.git` and inherited a parent folder's repo instead, clean the parent first.

```bash
cd C:\Users\shiva\OneDrive\JavaSelenium
git remote -v
git remote remove origin
```

`git remote -v` after removal should print **nothing** — that confirms it's cleared.

[⬆ Back to top](#table-of-contents)

---

## 4. Linking a Project to GitHub and Pushing

```bash
cd C:\Users\shiva\OneDrive\JavaSelenium\paimana_1point1
git remote add origin https://github.com/ShivamPandit1213/paimana_1point1.git
git branch -M main
git push -u origin main
```

[⬆ Back to top](#table-of-contents)

---

## 5. Full Setup — Every New Project, Step by Step

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

[⬆ Back to top](#table-of-contents)

---

## 6. Verification Commands — Use Before Every Commit/Push

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

## 7. Day-to-Day Workflow (After Initial Setup)

Once `origin` and upstream tracking are set, every future change is three commands:

```bash
git add .
git commit -m "what changed"
git push
```

No `-u origin main` needed again — upstream is already remembered.

[⬆ Back to top](#table-of-contents)

---

## 8. Quick Reference — Command Purpose Table

| When | Command |
|---|---|
| Check current folder contents | `dir` |
| Enter a folder | `cd <path>` |
| Start a new repo here | `git init` |
| Confirm repo is isolated (not inherited) | `git rev-parse --show-toplevel` |
| Confirm `.git` physically exists | `dir /a:h .git` |
| Stage all changes | `git add .` |
| Stage one specific file | `git add <filename>` |
| Check what's staged/unstaged | `git status` |
| Commit staged changes | `git commit -m "message"` |
| Link to GitHub | `git remote add origin <url>` |
| Check/remove a remote | `git remote -v` / `git remote remove origin` |
| Rename branch | `git branch -M main` (or `master`) |
| Check current branch | `git branch` |
| Push and set tracking (first time) | `git push -u origin main` |
| Push (after tracking is set) | `git push` |
| View commit history | `git log --oneline` |
| View exact changes before commit | `git diff` |
| List all tracked files | `git ls-files` |
| Rename a file (Windows) | `ren OldName NewName` |
| View a file's content in CMD | `type filename` |

[⬆ Back to top](#table-of-contents)

---

## 9. Deleting a GitHub Repository

Use when a repo was created by mistake or ended up with the wrong content (e.g. it inherited files from a parent folder's `.git`).

1. Open the repo on GitHub → **Settings** (top nav)
2. Scroll to the bottom → **Danger Zone**
3. Click **Delete this repository**
4. Type the full name to confirm: `owner/repo-name`
5. Click **I understand the consequences, delete this repository**

There is no undo. Only delete a repo you're certain you want gone — if in doubt, make it private instead.

[⬆ Back to top](#table-of-contents)

---

## 10. Undoing / Reverting Pushes

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

### Remove specific files/folders from tracking without deleting them locally

Use this when a commit accidentally included the wrong folder (e.g. a sibling project nested inside).

```bash
git rm -r --cached <folder-or-file>
git commit -m "Remove folder from tracking"
git push
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
