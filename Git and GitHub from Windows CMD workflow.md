# Git and GitHub from Windows CMD: Verify First, Then Act

A task-based guide for creating, linking, pushing and pulling repositories from **Windows CMD**. Each section is named after the job you want to do, so you can jump straight to it from the contents list.

The whole guide follows one rule: **every command that changes something is preceded by a command that checks something.** Git's destructive commands are fast and quiet. Most damage comes from acting on an assumption that a five-second check would have corrected.

Most sections follow the same four steps: **Verify → Read → Act → Confirm**.

**Example values used in this guide**

| Item | Value |
|---|---|
| Local project folder | `C:\Projects\PlaywrightBasic1` |
| GitHub repository | `https://github.com/ShivamPandit1213/PlaywrightBasic1.git` |
| Default branch | `main` |

Replace these with your own folder and repository URL. Anything in angle brackets, such as `<url>` or `<file>`, is a placeholder you replace, without the brackets.

> **About copy-pasting:** the command blocks contain no inline comments, so every line can be pasted straight into CMD. The tables beside each block explain what each command does.

---

## Contents

- [The Rule](#the-rule)
- [Safe Diagnostic Commands](#safe-diagnostic-commands)
- [1. One-Time Setup](#1-one-time-setup)
- [2. Create a Repository](#2-create-a-repository)
- [3. Clone an Existing Repository](#3-clone-an-existing-repository)
- [4. Verify the Repository Is Connected or Linked](#4-verify-the-repository-is-connected-or-linked)
- [5. Add, Change or Remove a Remote](#5-add-change-or-remove-a-remote)
- [6. Check Status and Changes](#6-check-status-and-changes)
- [7. Stage and Commit](#7-stage-and-commit)
- [8. Push to GitHub](#8-push-to-github)
- [9. Pull from GitHub](#9-pull-from-github)
- [10. Fix a Rejected Push](#10-fix-a-rejected-push)
- [11. Resolve Merge Conflicts](#11-resolve-merge-conflicts)
- [12. Work with Branches](#12-work-with-branches)
- [13. Undo Changes and Commits](#13-undo-changes-and-commits)
- [14. Recover Lost Work](#14-recover-lost-work)
- [15. Delete or Restore Files](#15-delete-or-restore-files)
- [16. Ignore and Untrack Files](#16-ignore-and-untrack-files)
- [17. Fix a Project Inside a Parent Repository](#17-fix-a-project-inside-a-parent-repository)
- [18. Keep Two Clones in Sync](#18-keep-two-clones-in-sync)
- [19. Network and Connection Problems](#19-network-and-connection-problems)
- [20. Daily Workflow](#20-daily-workflow)
- [21. Troubleshooting Index](#21-troubleshooting-index)
- [22. Commands That Need a Check First](#22-commands-that-need-a-check-first)

---

## The Rule

Before any command that writes, run the command that reads.

| Instead of assuming | Run this | Because |
|---|---|---|
| "I'm in the right repo" | `git rev-parse --show-toplevel` | You may be inside a parent repo you didn't know existed |
| "It's linked to my GitHub repo" | `git remote -v` | It may point at another project, or nothing |
| "Only my file is staged" | `git status` | `git add .` catches more than you think |
| "I'm up to date" | `git fetch` then `git log HEAD..origin/main` | Your view of the remote is a cached snapshot |
| "Nothing important is here" | `git clean -nd` | The dry run lists exactly what would be destroyed |
| "That commit is gone" | `git reflog` | It usually isn't, for about 30 days |

[⬆ Back to top](#contents)

---

## Safe Diagnostic Commands

None of these change anything, so they are always safe to run.

| Command | Answers the question |
|---|---|
| `git rev-parse --show-toplevel` | Which repository am I actually in? |
| `git remote -v` | Which GitHub repository does it push to? |
| `git branch -vv` | Which branch am I on, and which remote branch does it track? |
| `git status` | What is staged, unstaged and untracked, and am I ahead or behind? |
| `git log --oneline -5` | What are the last five commits? |
| `git fetch origin` | Refresh my snapshot of GitHub (changes no files) |
| `git log --oneline HEAD..origin/main` | What does GitHub have that I don't? |
| `git log --oneline origin/main..HEAD` | What do I have that GitHub doesn't? |

The last two are the pair that explains every push rejection and every "diverged" message. Run both before reacting to either.

> **`git fetch` is not `git pull`.** Fetch updates your snapshot of GitHub and touches none of your files. Pull fetches *and* merges. When diagnosing, always fetch.

[⬆ Back to top](#contents)

---

## 1. One-Time Setup

Do this once per computer.

### Check that Git is installed

```bat
git --version
where git
```

If CMD says `'git' is not recognized`, install **Git for Windows** from https://git-scm.com, then open a **new** CMD window.

### Set your identity and defaults

```bat
git config --global user.name "Your Name"
git config --global user.email "you@example.com"
git config --global init.defaultBranch main
git config --global core.autocrlf true
git config --global pull.rebase false
git config --global credential.helper manager
```

| Setting | What it does |
|---|---|
| `user.name` / `user.email` | Labels every commit you make. Use the email linked to your GitHub account |
| `init.defaultBranch main` | New repositories start on `main` instead of `master` |
| `core.autocrlf true` | Converts Windows line endings (CRLF) to LF on commit. Produces the harmless "LF will be replaced by CRLF" warning |
| `pull.rebase false` | Makes `git pull` merge by default and silences the "divergent branches" hint. Use `--rebase` when you want it (see [Section 9](#9-pull-from-github)) |
| `credential.helper manager` | Uses Git Credential Manager (bundled with Git for Windows) to remember your GitHub login |

### Confirm

```bat
git config --global --list
```

### Sign in to GitHub

GitHub no longer accepts your account password for `git push`. Two ways to sign in:

- **Browser sign-in (easiest):** on your first push, Git Credential Manager opens a GitHub sign-in window. Approve it once, and Windows remembers it.
- **Personal Access Token:** GitHub → Settings → Developer settings → Personal access tokens. When Git asks for a password, paste the token instead.

### Optional: GitHub CLI (`gh`)

The GitHub CLI lets you create and view GitHub repositories from CMD without opening the browser. Install it from https://cli.github.com, then:

```bat
gh --version
gh auth login
gh auth status
```

[⬆ Back to top](#contents)

---

## 2. Create a Repository

Choose **one** of the two methods below. Both end with a local folder linked to a GitHub repository.

### Verify: make sure you are not inside another repository

```bat
cd /d C:\Projects\PlaywrightBasic1
git rev-parse --show-toplevel
```

| Output | Meaning |
|---|---|
| `fatal: not a git repository` | Clean slate. Proceed |
| This project's own path | Already a repository. Skip to [Section 4](#4-verify-the-repository-is-connected-or-linked) |
| A **parent** folder's path | This folder is inside another repository. Fix it first with [Section 17](#17-fix-a-project-inside-a-parent-repository) |

### Method A: create on GitHub in the browser, then link from CMD

1. On GitHub, click **New repository**, enter the name and click **Create repository**. Leave "Add a README" **unticked**, so the GitHub repository starts empty.
2. In CMD, inside the project folder:

```bat
git init
git rev-parse --show-toplevel
git add .
git status
git commit -m "Initial commit"
git remote add origin https://github.com/ShivamPandit1213/PlaywrightBasic1.git
git branch -M main
git push -u origin main
```

| Command | What it does |
|---|---|
| `git init` | Creates a new, separate `.git` folder in this project |
| `git rev-parse --show-toplevel` | Must now print **this** folder |
| `git add .` | Stages every file not ignored by `.gitignore` |
| `git status` | Check the list. No path should start with `../` |
| `git commit -m "Initial commit"` | Creates the first commit (local only) |
| `git remote add origin <url>` | Links this folder to the GitHub repository under the name `origin` |
| `git branch -M main` | Renames the current branch to `main` |
| `git push -u origin main` | Uploads the commits and sets `main` to track `origin/main` |

> If you ticked "Add a README" on GitHub, the push is rejected because GitHub already has a commit. Run `git pull --no-rebase origin main --allow-unrelated-histories`, then `git push -u origin main`.

### Method B: create the GitHub repository from CMD with `gh`

Requires the GitHub CLI from [Section 1](#1-one-time-setup).

```bat
git init
git add .
git status
git commit -m "Initial commit"
gh repo create PlaywrightBasic1 --public --source=. --remote=origin --push
```

| Option | What it does |
|---|---|
| `--public` / `--private` | Visibility of the new GitHub repository |
| `--source=.` | Uses the current folder as the source |
| `--remote=origin` | Adds the GitHub repository as the remote `origin` |
| `--push` | Pushes the local commits straight away |

### Add a `.gitignore` before the first commit

For a Node / Playwright project:

```bat
echo node_modules/>>.gitignore
echo test-results/>>.gitignore
echo playwright-report/>>.gitignore
echo blob-report/>>.gitignore
echo playwright/.cache/>>.gitignore
type .gitignore
```

Don't put a space before `>>` in CMD, or the line gets a trailing space.

### Confirm

```bat
git remote -v
git status
git ls-files
gh repo view --web
```

`git status` should say "Your branch is up to date with 'origin/main'". The last command opens the repository in your browser (or refresh the GitHub page manually). The file list should match the folder.

[⬆ Back to top](#contents)

---

## 3. Clone an Existing Repository

Use this when the repository already exists on GitHub and you want a copy on this computer.

### Verify

```bat
cd /d C:\Projects
git rev-parse --show-toplevel
```

You want `fatal: not a git repository` here, so the clone doesn't land inside another repository.

### Act

```bat
git clone https://github.com/ShivamPandit1213/PlaywrightBasic1.git
cd PlaywrightBasic1
```

| Variation | Command |
|---|---|
| Clone into a folder with a different name | `git clone <url> MyFolder` |
| Clone one specific branch | `git clone -b <branch> <url>` |
| Clone with the GitHub CLI | `gh repo clone ShivamPandit1213/PlaywrightBasic1` |

### Confirm

```bat
git remote -v
git branch -vv
git log --oneline -3
```

A clone is already linked and tracking. No `git remote add` or `-u` is needed.

> **Don't clone into OneDrive, Dropbox or Google Drive folders.** The sync client copies `.git` while Git is writing to it, which can corrupt the repository and cause `EBUSY` / `EPERM` errors. Use a plain local folder such as `C:\Projects`.

[⬆ Back to top](#contents)

---

## 4. Verify the Repository Is Connected or Linked

Run these whenever you are unsure whether a folder is a repository, which GitHub repository it points to, or whether GitHub is reachable.

```bat
git rev-parse --show-toplevel
git remote -v
git branch -vv
git ls-remote origin
git remote show origin
```

| Command | What it checks | Healthy output |
|---|---|---|
| `git rev-parse --show-toplevel` | This folder is its own repository | This project's path, not a parent folder |
| `git remote -v` | Which GitHub URL it is linked to | Your URL twice, `(fetch)` and `(push)` |
| `git branch -vv` | The current branch tracks a GitHub branch | `* main c5fc942 [origin/main] ...` |
| `git ls-remote origin` | GitHub is reachable and you have access | A list of hashes and `refs/heads/...` |
| `git remote show origin` | Full link details from GitHub | `HEAD branch: main` and `main pushes to main (up to date)` |

### Read

| You see | Meaning | Fix |
|---|---|---|
| `fatal: not a git repository` | The folder is not a repository | [Section 2](#2-create-a-repository) or [Section 3](#3-clone-an-existing-repository) |
| A parent folder's path | Inherited from a parent repository | [Section 17](#17-fix-a-project-inside-a-parent-repository) |
| `git remote -v` prints nothing | Not linked to GitHub | `git remote add origin <url>` |
| The wrong URL | Linked to a different repository | `git remote set-url origin <url>` |
| `git branch -vv` shows no `[origin/main]` | Linked, but the branch doesn't track | `git push -u origin main` (or `git branch -u origin/main`) |
| `remote: Repository not found.` | Wrong URL, or no access to a private repo | Compare `git remote -v` with the URL in the browser |
| `Could not resolve host: github.com` | Network or DNS problem | [Section 19](#19-network-and-connection-problems) |

[⬆ Back to top](#contents)

---

## 5. Add, Change or Remove a Remote

### Verify

```bat
git remote -v
```

### Act

| Task | Command |
|---|---|
| Link to GitHub for the first time | `git remote add origin <url>` |
| Point at a different or renamed repository | `git remote set-url origin <url>` |
| Rename a remote | `git remote rename origin upstream` |
| Unlink completely | `git remote remove origin` |
| Set tracking for the current branch | `git branch -u origin/main` |

Prefer `set-url` over remove-then-add. `git remote remove origin` is not part of any daily routine. After it, every `git push` and `git pull` fails until a remote is added again.

### Confirm

```bat
git remote -v
git ls-remote origin
```

[⬆ Back to top](#contents)

---

## 6. Check Status and Changes

```bat
git status
git diff
git diff --stat
git diff --cached
git log --oneline -5
```

| Command | Shows |
|---|---|
| `git status` | Staged, unstaged and untracked files, plus ahead/behind counts |
| `git diff` | Line-by-line changes **not yet staged** |
| `git diff --stat` | One line per changed file, with a count of changed lines. Empty means nothing changed |
| `git diff --cached` | Line-by-line changes **already staged**, i.e. what the next commit will contain |
| `git log --oneline -5` | The last five commits |
| `git log --oneline --graph --all -15` | Branches and merges drawn as a graph |
| `git ls-files` | Every file Git is tracking |

> **Git only tracks files inside the repository.** Changing your Node version, installing browsers or editing Jenkins settings creates nothing to commit. Edit and save a file, confirm with `git diff --stat`, then commit.

If the terminal shows a `:` prompt and seems stuck, Git's pager is waiting. Press `q`. To turn the pager off permanently: `git config --global core.pager cat`.

[⬆ Back to top](#contents)

---

## 7. Stage and Commit

### Verify

```bat
git status
git diff
```

Reading `git diff` before staging is the cheapest code review available.

### Act

```bat
git add <file>
git status
git commit -m "Describe what changed"
```

| Command | Use when |
|---|---|
| `git add <file>` | Staging one file. Preferred when the folder has other changes |
| `git add "my file.txt"` | The path contains spaces |
| `git add package.json package-lock.json` | Changing dependencies. Always commit both together, or `npm ci` fails |
| `git add .` | Staging everything not ignored. Check `git status` afterwards |
| `git restore --staged <file>` | You staged something by mistake (the edits are kept) |
| `git commit -m "message"` | Recording the staged snapshot |
| `git commit --amend -m "new message"` | Fixing the last commit's message (only if not yet pushed) |

### Confirm

```bat
git log --oneline -1
git status
```

> If the commit message needs the word "and", it is probably two commits.

[⬆ Back to top](#contents)

---

## 8. Push to GitHub

### Verify

```bat
git status
git fetch origin
git log --oneline origin/main..HEAD
```

The last command lists exactly what the push will upload. Empty output means there is nothing to push.

### Act

| Situation | Command |
|---|---|
| First push of a new repository | `git push -u origin main` |
| Every push after that | `git push` |
| First push of a new branch | `git push -u origin <branch>` |
| Push a specific branch | `git push origin <branch>` |
| Push tags | `git push --tags` |
| Delete a branch on GitHub | `git push origin --delete <branch>` |

`-u` (set upstream) is needed only once per branch. After that, plain `git push` knows where to go.

### Read the output

| Output | Meaning |
|---|---|
| `2dd823e..c5fc942  main -> main` | Success. Commits in that range were uploaded |
| `Everything up-to-date` | Nothing new to send. Commit first if you expected changes |
| `! [rejected] main -> main (fetch first)` | GitHub has commits you don't. See [Section 10](#10-fix-a-rejected-push) |
| `src refspec main does not match any` | No commits on `main` yet. Commit first |

### Confirm

```bat
git status
git log --oneline origin/main -3
```

`git status` should say "up to date with 'origin/main'". Refresh the GitHub page to see the new commit.

[⬆ Back to top](#contents)

---

## 9. Pull from GitHub

### Verify

```bat
git status
git fetch origin
git log --oneline HEAD..origin/main
git log --oneline origin/main..HEAD
```

| First list (theirs) | Second list (yours) | Situation |
|---|---|---|
| Empty | Any | Nothing to pull |
| Has commits | Empty | You are only behind. The pull is a simple fast-forward |
| Has commits | Has commits | Diverged. The pull will merge or rebase |

### Act

| Command | What it does | Use when |
|---|---|---|
| `git pull` | Fetch and merge from the tracked branch | Everyday use once upstream is set |
| `git pull --ff-only` | Pull only if it's a fast-forward, otherwise stop | You expect to be only behind and want no surprises |
| `git pull --rebase origin main` | Replays your local commits on top of GitHub's | Your commits are unpushed; gives a straight history |
| `git pull --no-rebase origin main` | Creates a merge commit joining both histories | Others may already have your commits |
| `git pull --no-rebase -X ignore-space-at-eol origin main` | Merge that ignores line-ending differences | Every line conflicts only because of CRLF vs LF |

### Pull refused because of uncommitted changes

```text
error: Your local changes to the following files would be overwritten by merge
```

```bat
git stash
git pull
git stash pop
```

`git stash` parks your edits, the pull runs on a clean folder, and `git stash pop` puts your edits back.

### Confirm

```bat
git log --oneline -5
git status
```

[⬆ Back to top](#contents)

---

## 10. Fix a Rejected Push

```text
! [rejected]        main -> main (fetch first)
```

GitHub has commits you don't. Nothing is wrong yet.

### Verify

```bat
git fetch origin
git log --oneline HEAD..origin/main
git log --oneline origin/main..HEAD
```

### Read

| Result | Situation | Act |
|---|---|---|
| Second list is **empty** | You are only behind | `git pull --ff-only`, then push |
| Both lists have entries | Genuinely diverged | `git pull --rebase origin main` or `git pull --no-rebase origin main` |
| First list is empty | Already current | Retry `git push` |

A real example: a branch reported "diverged, 1 and 9 commits", but after fetching, the second list was empty, because GitHub's merge commit already contained the local work. The pull became a fast-forward with nothing to replay. **The divergence message was stale information, not a conflict.**

### Act

```bat
git pull --rebase origin main
git log --oneline -5
git push
```

If the pull stops with conflicts, go to [Section 11](#11-resolve-merge-conflicts).

> **Never** fix a rejected push with `git push --force` or `--force-with-lease`. It deletes the GitHub commits you haven't looked at yet.

[⬆ Back to top](#contents)

---

## 11. Resolve Merge Conflicts

```text
CONFLICT (content): Merge conflict in suites/smoke.xml
Automatic merge failed; fix conflicts and then commit the result.
```

### Verify

```bat
git status
git diff --name-only --diff-filter=U
```

The second command lists only the conflicted files.

### Read

Open each file. Git has written both versions in place:

```text
<<<<<<< HEAD
your version
=======
their version
>>>>>>> 4523a37
```

`HEAD` is yours. The hash at the bottom is theirs.

### Act

| Situation | Command |
|---|---|
| Both edits matter | Edit the file by hand. Keep what you need and delete the three marker lines |
| Your version is correct | `git checkout --ours <file>` |
| Their version is correct | `git checkout --theirs <file>` |
| The file was deleted on one side and should stay deleted | `git rm <file>` |

Then mark each file as resolved and finish:

```bat
git add <file>
git status
git commit
```

If you were rebasing, finish with `git rebase --continue` instead of `git commit`.

### Confirm no markers are left

```bat
git grep -n "<<<<<<<"
git diff --check
```

Nothing should print. Committed conflict markers are a common and embarrassing mistake.

### Back out

```bat
git merge --abort
```

Use `git rebase --abort` if you were rebasing. Either one returns you exactly to where you were. Nothing is lost.

[⬆ Back to top](#contents)

---

## 12. Work with Branches

### Verify

```bat
git status
git fetch origin
git branch -a
```

Commit or stash anything pending before switching. Uncommitted work follows you to the new branch and lands wherever you commit it.

### Act

| Task | Command |
|---|---|
| List local branches (`*` marks the current one) | `git branch` |
| List local and GitHub branches | `git branch -a` |
| Create a branch and switch to it | `git switch -c <branch>` |
| Switch to an existing local branch | `git switch <branch>` |
| Get a branch that exists only on GitHub | `git switch -c <branch> origin/<branch>` |
| Push a new branch and set tracking | `git push -u origin <branch>` |
| Bring `main`'s latest commits into your branch | `git merge origin/main` |
| Rename the current branch | `git branch -m <new-name>` |
| Delete a merged local branch | `git branch -d <branch>` |
| Delete a branch on GitHub | `git push origin --delete <branch>` |

### `.gitignore` differs per branch

`.gitignore` is a tracked file, so each branch has its own version. A file ignored on `main` may be unprotected on an older feature branch.

```bat
type .gitignore
git status
```

Any file that newly appears under "Untracked files" after a switch is not ignored on this branch. If it holds credentials, `git add .` here will commit it. To copy `main`'s ignore rules into this branch:

```bat
git checkout origin/main -- .gitignore
git status
git diff --cached
```

> **Close your IDE before switching on Windows.** An open project holds folders locked, and Git asks `Deletion of directory '...' failed. Should I try again? (y/n)`. Answering `n` still completes the switch, but leaves empty folders behind.

### Confirm

```bat
git branch -vv
git log --oneline -3
```

[⬆ Back to top](#contents)

---

## 13. Undo Changes and Commits

### Verify

```bat
git status
git log --oneline -5
git log --oneline origin/main..HEAD
```

The last command decides the approach. If the commit appears there, it hasn't been pushed yet.

### Undo uncommitted changes

| Task | Command | Check first |
|---|---|---|
| Unstage a file (keep the edits) | `git restore --staged <file>` | `git status` |
| Discard edits to one file | `git restore <file>` | `git diff <file>` |
| Discard all uncommitted edits to tracked files | `git restore .` | `git diff --stat` |
| Delete untracked files and folders | `git clean -fd` | `git clean -nd` (dry run) |

### Undo a commit

| Pushed? | Shared branch? | Use |
|---|---|---|
| No | — | `git reset`. Safe, because the history is only yours |
| Yes | No | `git reset`, then `git push --force-with-lease` |
| Yes | **Yes** | `git revert`. Never rewrite shared history |

| Command | Effect |
|---|---|
| `git reset --soft HEAD~1` | Undo the commit, keep the changes staged |
| `git reset HEAD~1` | Undo the commit, keep the changes as unstaged edits |
| `git reset --hard HEAD~1` | Undo the commit **and discard** its changes |
| `git revert HEAD` | Add a new commit that reverses the last one. History stays intact |

**Committed to the wrong branch?** Run `git reset --soft HEAD~1`, switch to the right branch, and commit again.

### Confirm

```bat
git log --oneline -3
git status
```

> `--hard` discards **uncommitted** work permanently. Committed work survives in the reflog for roughly 30 days (see the next section).

[⬆ Back to top](#contents)

---

## 14. Recover Lost Work

### Verify

```bat
git reflog
```

Every movement of `HEAD` is listed, newest first:

```text
b080ba4 HEAD@{0}: reset: moving to HEAD~1
31e88d2 HEAD@{1}: commit: Refactor Jenkinsfile
f09dd0b HEAD@{2}: commit: update mvn version details
```

### Read

Find the entry from *before* the mistake, and inspect it before using it:

```bat
git show 31e88d2
```

### Act

| Goal | Command |
|---|---|
| Move the branch back to that commit | `git reset --hard 31e88d2` |
| Look around without moving the branch | `git checkout 31e88d2` (then `git switch -` to return) |
| Save it as a new branch | `git branch recovered-work 31e88d2` |

### Confirm

```bat
git log --oneline -3
git status
```

**Limits:** the reflog exists only in this clone (a fresh clone doesn't have it), and unreachable commits are eventually deleted. If the branch was already pushed, restoring it rewrites GitHub history and needs `--force-with-lease`, with the shared-branch caution from [Section 13](#13-undo-changes-and-commits).

[⬆ Back to top](#contents)

---

## 15. Delete or Restore Files

```text
Changes not staged for commit:
        deleted:    Coal.txt
        deleted:    Payload.json
```

The files are tracked by Git, gone from disk, and the deletion hasn't been committed. The question is whether the deletion was intentional.

### Verify

```bat
git status
git log --oneline -- <file>
git show HEAD:<file>
```

The last command prints the file's contents from the last commit, so you can read it before deciding.

### Act

| Situation | Command |
|---|---|
| Deletion was intentional | `git rm <file-1> <file-2>`, then commit and push |
| Deleted by accident | `git restore <file-1> <file-2>` |
| Delete a tracked file from disk and Git in one step | `git rm <file>` |
| Restore a file from an older commit | `git restore --source <hash> <file>` |

```bat
git rm Coal.txt Payload.json
git status
git commit -m "Remove Coal.txt and Payload.json"
git push
```

### Confirm

```bat
git status
git ls-files | findstr Coal
```

> **Deleting a file doesn't remove it from history.** Every earlier version stays readable by anyone with access to the repository. If the file contained a password or token, change (rotate) that secret first. Removing it from history needs `git filter-repo` plus a force-push.

[⬆ Back to top](#contents)

---

## 16. Ignore and Untrack Files

These are three different fixes, and each solves a different part of the problem.

| Goal | Command | Files on disk |
|---|---|---|
| **Unstage**: remove from the next commit only | `git restore --staged <path>` | Kept, still tracked |
| **Untrack**: stop Git watching a file or folder | `git rm -r --cached <path>` | Kept |
| **Ignore**: stop it ever being added again | Add a line to `.gitignore` | Kept |

To stop tracking a folder that was committed by mistake, such as `node_modules/`:

### Verify

```bat
git ls-files node_modules
```

If this prints file paths, the folder is tracked and needs untracking.

### Act

```bat
git rm -r --cached node_modules
echo node_modules/>>.gitignore
type .gitignore
git add .gitignore
git status
git commit -m "Stop tracking node_modules"
git push
```

### Confirm

```bat
git ls-files node_modules
git status
```

The first command should print nothing. After a test run, ignored folders shouldn't appear in `git status`.

> **`git clean` skips ignored files** unless you add `-x`. That's usually what you want, and occasionally a surprise.

[⬆ Back to top](#contents)

---

## 17. Fix a Project Inside a Parent Repository

**Symptom:** `git status` inside your project lists dozens of unrelated folders, or paths start with `../`.

### Verify

```bat
cd /d C:\Projects\PlaywrightBasic1
git rev-parse --show-toplevel
dir /a:h .git
```

If the first command prints a **parent** path and the second says `File Not Found`, the project has no repository of its own and is using the parent's.

### Act, part 1: decide what to do with the parent repository

**If the parent repository was created by accident** and holds nothing you need, delete its `.git` folder. Your files stay; only the parent's Git history is removed.

```bat
cd /d C:\Projects
dir /a:h .git
rmdir /s /q .git
```

**If the parent repository is real and must be kept,** stop it tracking the other projects instead:

```bat
cd /d C:\Projects
git restore --staged PlaywrightBasic1
git rm -r --cached PlaywrightBasic1
echo PlaywrightBasic1/>>.gitignore
git add .gitignore
git commit -m "Ignore PlaywrightBasic1 folder"
git status
```

### Act, part 2: give the project its own repository

```bat
cd /d C:\Projects\PlaywrightBasic1
git init
git rev-parse --show-toplevel
git add .
git status
git commit -m "Initial commit"
git remote add origin https://github.com/ShivamPandit1213/PlaywrightBasic1.git
git branch -M main
git push -u origin main
```

`git rev-parse --show-toplevel` must now print the project folder, and no path in `git status` may start with `../`.

### Confirm

```bat
git log --oneline
git status
git ls-files
```

### Prevent it permanently

Once a folder has its own `.git`, Git stops searching upward, so the parent can no longer interfere. Make these the first three commands in every new project folder, before writing any code:

```bat
cd /d <new-project-folder>
git init
git rev-parse --show-toplevel
```

[⬆ Back to top](#contents)

---

## 18. Keep Two Clones in Sync

Two copies of the same repository drift apart independently. Each has its own branch, its own uncommitted work and its own snapshot of GitHub. `git status` in one tells you nothing about the other.

### Verify: in each clone separately

```bat
git rev-parse --show-toplevel
git branch
git status
git fetch origin
git log --oneline HEAD..origin/main
```

A clone that hasn't fetched recently reports "up to date" from a snapshot that may be days old.

### Read

```text
f90bc4c (main) Remove Coal.txt and Payload.json
bb6e9a7 (origin/main, origin/HEAD) Add README pointing at ProjectInfo.html
```

The labels in brackets show where each branch points. Here local `main` and `origin/main` are on different commits, so this clone is out of date.

### Act: finish outstanding work first

```bat
git status
git add <files>
git commit -m "Describe what changed"
git push origin <branch>
```

### Act: then sync

```bat
git switch main
git log --oneline HEAD..origin/main
git merge origin/main
git log --oneline -3
```

### Confirm

```bat
git status
git log --oneline origin/main..HEAD
```

Empty output from the last command means nothing is left unpushed.

**Pick one clone and work in it.** Files deleted in one still exist in the other, and ignore rules fixed in one stay broken in the other. If you keep a second clone for a reason, treat it as read-only.

[⬆ Back to top](#contents)

---

## 19. Network and Connection Problems

| Command | Purpose |
|---|---|
| `ping github.com` | Basic reachability. GitHub may drop ping, so a timeout isn't conclusive |
| `curl -I https://github.com` | Tests HTTPS on port 443, the same route Git uses. `200` or `301` means it works |
| `git ls-remote origin` | Tests the connection **and** your access to the repository |
| `ipconfig /flushdns` | Clears the Windows DNS cache. Use for `Could not resolve host: github.com` |
| `git config --global --get http.proxy` | Shows the proxy Git is using, if any |
| `git config --global --unset http.proxy` | Removes a proxy setting, e.g. after leaving an office network or VPN |

### Sign-in problems

| Message | Fix |
|---|---|
| `Authentication failed` / `Support for password authentication was removed` | Use browser sign-in or a Personal Access Token, not your password ([Section 1](#1-one-time-setup)) |
| Git keeps using an old, wrong login | Control Panel → Credential Manager → Windows Credentials → remove the `git:https://github.com` entry, then push again to sign in fresh |
| `remote: Permission to ... denied` | The signed-in account has no write access to that repository |

[⬆ Back to top](#contents)

---

## 20. Daily Workflow

**Start of the day: get the latest from GitHub**

```bat
cd /d C:\Projects\PlaywrightBasic1
git status
git fetch origin
git log --oneline HEAD..origin/main
git pull
```

**After making changes: review, commit and push**

```bat
git status
git diff --stat
git add <file>
git status
git commit -m "Describe what changed"
git push
```

**Confirm**

```bat
git status
git log --oneline -3
```

`git status` should end with "Your branch is up to date with 'origin/main'" and "nothing to commit, working tree clean".

[⬆ Back to top](#contents)

---

## 21. Troubleshooting Index

Find the message you're seeing, read the cause and run the fix.

### Linking and remotes

| Message or symptom | Cause | Fix |
|---|---|---|
| `fatal: not a git repository` | The folder isn't a repository | `git init` ([Section 2](#2-create-a-repository)) or `cd` into the right folder |
| `fatal: 'origin' does not appear to be a git repository` | No remote configured, or it was removed | `git remote add origin <url>`, then `git push -u origin main` |
| `error: remote origin already exists.` | `git remote add` run twice | `git remote set-url origin <url>` |
| `remote: Repository not found.` | Wrong URL, or no access to a private repository | Compare `git remote -v` with the browser URL |
| `There is no tracking information for the current branch` | The branch has no upstream | `git branch -u origin/main` or `git push -u origin main` |
| `Could not resolve host: github.com` | Network or DNS | `ipconfig /flushdns`, check proxy ([Section 19](#19-network-and-connection-problems)) |

### Push and pull

| Message or symptom | Cause | Fix |
|---|---|---|
| `! [rejected] main -> main (fetch first)` | GitHub has commits you don't | [Section 10](#10-fix-a-rejected-push) |
| `Your branch and 'origin/main' have diverged` | Often **stale**: your snapshot predates a merge | `git fetch origin`, then `git log --oneline origin/main..HEAD`. Empty means you're only behind |
| `Your branch is behind 'origin/main' by 2 commits, and can be fast-forwarded` | Only behind, no conflict | `git pull --ff-only` |
| `src refspec main does not match any` | No commits on that branch yet | Commit first, then push |
| `Everything up-to-date` | No new local commits | Normal if already pushed |
| `refusing to merge unrelated histories` | GitHub repo was created with a README | `git pull --no-rebase origin main --allow-unrelated-histories` |
| `Your local changes ... would be overwritten by merge` | Uncommitted edits block the pull | `git stash`, `git pull`, `git stash pop` |
| A clone says "up to date" but is several commits behind | Its snapshot is cached, not live | `git fetch origin` before trusting `git status` |

### Working folder and staging

| Message or symptom | Cause | Fix |
|---|---|---|
| `nothing to commit, working tree clean` | No file was changed and saved | Edit and save, check `git diff --stat`, then commit |
| `deleted: <file>` under *not staged* | Removed from disk, deletion not recorded | Intentional: `git rm <file>`. Accidental: `git restore <file>` |
| `error: pathspec '<file>' did not match any file(s) known to git` | The file isn't tracked | Check the name with `git ls-files`; `git rm --cached` only works on tracked files |
| `git add .` staged unexpected files | `.` means everything not ignored | `git restore --staged <file>`, then add an ignore rule |
| A credentials file shows as untracked after a branch switch | `.gitignore` differs per branch | `git checkout origin/main -- .gitignore` |
| `LF will be replaced by CRLF` | Windows line-ending conversion | Harmless. Not an error |
| Empty folders left after a branch switch | Windows held folders open | Close the IDE and delete them. Git doesn't track empty folders |
| Terminal stuck at `:` | Git's pager is waiting | Press `q` |

### History and recovery

| Message or symptom | Cause | Fix |
|---|---|---|
| A commit seems to have vanished | Usually after `reset --hard` | `git reflog`, `git show <hash>`, `git reset --hard <hash>` |
| Committed to the wrong branch | Uncommitted work followed you across a switch | `git reset --soft HEAD~1`, switch, commit again |
| Conflict markers `<<<<<<<` were committed | Staged without deleting the markers | `git grep -n "<<<<<<<"`, fix each file, commit |
| A secret was committed | Deleting the file doesn't remove it from history | Rotate the secret, then `git filter-repo` and force-push |

[⬆ Back to top](#contents)

---

## 22. Commands That Need a Check First

| Command | What it destroys | Run this first |
|---|---|---|
| `git reset --hard` | Uncommitted changes, permanently | `git status`, and `git stash` if unsure |
| `git clean -fd` | Untracked files, bypassing the Recycle Bin | `git clean -nd` (dry run) |
| `git restore <file>` | Unstaged edits to that file | `git diff <file>` |
| `git push --force-with-lease` | Commits on GitHub | `git fetch` then `git log HEAD..origin/main` |
| `git rm -r --cached <path>` | Tracking only; files survive | `git ls-files <path>` |
| `git rm <file>` | The file, and stages the deletion | `git show HEAD:<file>` |
| `git branch -D <branch>` | An unmerged branch | `git log --oneline <branch>` |
| `git remote remove origin` | The link to GitHub | `git remote -v` (consider `set-url` instead) |
| `rmdir /s /q .git` | All local history of that repository | `git rev-parse --show-toplevel` and `git log --oneline` |
| `git switch <branch>` | Nothing, but changes which `.gitignore` applies | `type .gitignore` after switching |

Two commands look dangerous but aren't: `git fetch` changes no files, and `git stash` is fully reversible with `git stash pop`.

[⬆ Back to top](#contents)
