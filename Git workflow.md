# Git Workflow — Verify First, Then Act

A working guide built around one rule: **every command that changes state is preceded
by a command that reports state.** Git's destructive operations are fast and quiet, and
most damage comes from acting on an assumption that a five-second check would have
corrected.

Each workflow below follows the same four steps — **Verify → Read → Act → Confirm**.

---

## Contents

- [The rule](#the-rule)
- [Diagnostic toolkit](#diagnostic-toolkit)
- [Workflow 1 — Starting a new project](#workflow-1--starting-a-new-project)
- [Workflow 2 — Daily commit and push](#workflow-2--daily-commit-and-push)
- [Workflow 3 — Push rejected](#workflow-3--push-rejected)
- [Workflow 4 — Merge conflict](#workflow-4--merge-conflict)
- [Workflow 5 — Undoing a commit](#workflow-5--undoing-a-commit)
- [Workflow 6 — Recovering lost work](#workflow-6--recovering-lost-work)
- [Workflow 7 — Isolating a project from a shared parent](#workflow-7--isolating-a-project-from-a-shared-parent)
- [Workflow 8 — Files show as deleted but not staged](#workflow-8--files-show-as-deleted-but-not-staged)
- [Commands that need a check first](#commands-that-need-a-check-first)

---

## The rule

Before any command that writes, run the command that reads. The cost is seconds; the
cost of skipping it ranges from a confusing afternoon to unrecoverable work.

| Instead of assuming | Run this | Because |
|---|---|---|
| "I'm in the right repo" | `git rev-parse --show-toplevel` | You may be inside a parent repo you didn't know existed |
| "It's linked to my repo" | `git remote -v` | It may point at another project, or nothing |
| "Only my file is staged" | `git status` | `git add .` catches more than you think |
| "I'm up to date" | `git fetch` then `git log HEAD..origin/main` | Your view of the remote is a cached snapshot |
| "Nothing important is here" | `git clean -nd` | The dry run lists exactly what would be destroyed |
| "That commit is gone" | `git reflog` | It usually isn't, for about 30 days |

---

## Diagnostic toolkit

Six commands answer almost every "what is going on" question. None of them change
anything, so they are always safe to run.

```bash
git rev-parse --show-toplevel   # which repo am I actually in
git remote -v                   # where does it push to
git status                      # staged, unstaged, untracked, branch tracking
git log --oneline -5            # recent history
git fetch && git log --oneline HEAD..origin/main   # what the remote has that I don't
git log --oneline origin/main..HEAD                # what I have that the remote doesn't
```

The last two are the pair that explains every push rejection and every "diverged"
message. Run both before reacting to either.

> **`git fetch` is not `git pull`.** Fetch updates your snapshot of the remote and
> touches none of your files. Pull fetches *and* merges. When diagnosing, always fetch.

[⬆ Back to top](#contents)

---

## Workflow 1 — Starting a new project

The failure this prevents: a subfolder with no `.git` of its own silently operating on
a parent repository, so commits sweep in unrelated projects.

### Verify

```bash
cd <project-folder>               # enter the exact project folder, never a parent
git rev-parse --show-toplevel     # prints the repo root Git is actually using
```

### Read

| Output | Meaning |
|---|---|
| `fatal: not a git repository` | Clean slate — proceed |
| This project's own path | Already initialised correctly |
| A **parent** folder's path | Inheriting a parent repo — fix before doing anything else |

### Act

```bash
git init                          # creates a new, separate .git in this folder
git rev-parse --show-toplevel     # must now print THIS folder
git add .                         # stages everything, including files you may not expect
git status                        # no path may start with ../
git commit -m "Initial commit"    # first commit, local only
git remote add origin <url>       # link the local repo to the empty GitHub repo
git branch -M main                # rename the current branch to main
git push -u origin main           # upload and set upstream tracking
```

### Confirm

```bash
git remote -v                     # your URL, twice
git status                        # "up to date with 'origin/main'"
git ls-files                      # exactly the files you expect
```

Then refresh the repository page in the browser. The file list should match the folder.

[⬆ Back to top](#contents)

---

## Workflow 2 — Daily commit and push

### Verify

```bash
git status                        # staged, unstaged, untracked, and branch tracking
git diff                          # line-level changes not yet staged
```

`git diff` shows unstaged line changes. Reading it before staging is the cheapest code
review available.

### Act

```bash
git add <specific-file>           # prefer over "git add ." when the tree is noisy
git status                        # confirm only what you intended is staged
git commit -m "what changed"      # one commit, one change
git push                          # upstream is already set, so no arguments needed
```

### Confirm

```bash
git log --oneline -1              # confirm the commit landed
git status                        # staged, unstaged, untracked, and branch tracking
```

> If the commit message needs the word "and", it is probably two commits.

[⬆ Back to top](#contents)

---

## Workflow 3 — Push rejected

```text
! [rejected]        main -> main (fetch first)
```

The remote has commits you do not. Nothing is wrong yet.

### Verify

```bash
git fetch origin                  # refresh the remote snapshot, touching no files
git log --oneline HEAD..origin/main     # theirs, missing from yours
git log --oneline origin/main..HEAD     # yours, missing from theirs
```

### Read

| Result | Situation | Act |
|---|---|---|
| Second list is **empty** | You are simply behind | `git pull --rebase origin main` fast-forwards |
| Both lists have entries | Genuinely diverged | Rebase or merge, below |
| First list is empty | Already current — retry the push | |

A real example: a branch reported "diverged, 1 and 9 commits" but after fetching, the
second list was empty — the remote's merge commit already contained the local work. The
pull became a fast-forward with nothing to replay. **The divergence message was stale
information, not a conflict.**

### Act

```bash
git pull --rebase origin main     # linear history, your commits replayed on top
```

Prefer `--rebase` when your commits are unpushed and local. Use a plain merge when
others may already have your commits.

### Confirm

```bash
git log --oneline -5              # recent history, one line each
git status                        # staged, unstaged, untracked, and branch tracking
git push                          # upstream is already set, so no arguments needed
```

> **Never** resolve a rejected push with `--force-with-lease`. It deletes the remote
> commits you have not yet looked at.

[⬆ Back to top](#contents)

---

## Workflow 4 — Merge conflict

```text
CONFLICT (content): Merge conflict in suites/smoke.xml
Automatic merge failed; fix conflicts and then commit the result.
```

### Verify

```bash
git status                        # lists every conflicted path
git diff --name-only --diff-filter=U# list only the unmerged (conflicted) paths
```

### Read

Open the file. Git has written both versions in place:

```text
<<<<<<< HEAD
your version
=======
their version
>>>>>>> 4523a37
```

`HEAD` is yours. The hash below is theirs.

### Act

Three routes, by situation:

| Situation | Command |
|---|---|
| Both edits matter | Hand-edit: keep what you need, delete the three marker lines |
| Yours is correct | `git checkout --ours <file>` |
| Theirs is correct | `git checkout --theirs <file>` |

Then mark resolved and continue:

```bash
git add <file>                    # mark this conflict as resolved
git status                        # repeat until no conflicts remain
git commit                        # or: git rebase --continue
```

### Confirm

```bash
grep -rn "<<<<<<<" --include="*" . | grep -v ".git/"# nothing should print
```

Nothing should print. Committed conflict markers are a common and embarrassing failure.

### Backing out

```bash
git merge --abort                 # or: git rebase --abort
```

Returns you to exactly where you were. Nothing is lost.

[⬆ Back to top](#contents)

---

## Workflow 5 — Undoing a commit

### Verify

```bash
git log --oneline -5              # recent history, one line each
git log --oneline origin/main..HEAD     # has it been pushed?
```

That second command decides the whole approach.

### Read

| Pushed? | Shared branch? | Use |
|---|---|---|
| No | — | `git reset` — safe, history is yours alone |
| Yes | No | `git reset` then `--force-with-lease` |
| Yes | **Yes** | `git revert` — never rewrite shared history |

### Act

```bash
# keep changes staged
git reset --soft HEAD~1           # undo the commit, keep changes staged

# keep changes in the working tree, unstaged
git reset HEAD~1                  # undo the commit, keep changes in the working tree

# discard the commit and its changes entirely
git reset --hard HEAD~1           # undo the commit and discard its changes

# safest on a shared branch: a new commit that undoes the old one
git revert HEAD                   # new commit that undoes the last one, history intact
```

### Confirm

```bash
git log --oneline -3              # last three commits
git status                        # staged, unstaged, untracked, and branch tracking
```

> `--hard` discards **uncommitted** work permanently. Committed work survives in the
> reflog for roughly 30 days — see the next workflow.

[⬆ Back to top](#contents)

---

## Workflow 6 — Recovering lost work

### Verify

```bash
git reflog                        # every movement of HEAD, newest first
```

Every movement of `HEAD` is listed, newest first:

```text
b080ba4 HEAD@{0}: reset: moving to HEAD~1
31e88d2 HEAD@{1}: commit: Refactor Jenkinsfile
f09dd0b HEAD@{2}: commit: update mvn version details
```

### Read

Find the entry from *before* the mistake. Inspect it before committing to it:

```bash
git show 31e88d2                  # read that commit before acting on it
```

### Act

```bash
git reset --hard 31e88d2          # move the branch back to that commit
```

To look around without moving your branch:

```bash
git checkout 31e88d2      # detached HEAD; git switch - returns you
```

### Confirm

```bash
git log --oneline -3              # last three commits
git status                        # staged, unstaged, untracked, and branch tracking
```

**Caveats.** The reflog is local and per-clone — it does not exist in a fresh clone, and
unreachable commits are eventually garbage-collected. If the branch was already pushed,
restoring it rewrites remote history and needs `--force-with-lease` plus the shared-branch
caution from Workflow 5.

[⬆ Back to top](#contents)

---

## Workflow 7 — Isolating a project from a shared parent

Symptom: `git status` inside your project lists dozens of unrelated folders, or paths
appear with a `../` prefix.

### Verify

```bash
cd <project-folder>               # enter the exact project folder, never a parent
git rev-parse --show-toplevel     # prints a PARENT path -> this is the problem
dir /a:h .git                     # File Not Found -> no repo of its own
```

### Act — part 1, clean the parent

Three different fixes, each solving a different part. All three are needed.

```bash
cd <parent-folder>                # move up to the repo that is wrongly tracking everything

# 1. Unstage — removes from the pending commit, still tracked
git restore --staged <folder>     # unstage it, modifications preserved

# 2. Untrack — stops Git watching it, files stay on disk
git rm -r --cached <folder>       # stop tracking it, files stay on disk

# 3. Ignore — stops it ever resurfacing
#    add each folder to .gitignore
```

```gitignore
other-project-1/                  # one line per folder to ignore
other-project-2/                   # add every folder except the one you keep
target/                           # build output
test-output/                      # TestNG reports
```

```bash
git remote -v                     # should print nothing
git remote remove origin          # if it doesn't
git add .gitignore                # stage only the ignore rules
git commit -m "Ignore unrelated project folders"# record the cleanup
git status                        # only .gitignore and your project remain
```

### Act — part 2, give the project its own repo

```bash
cd <project-folder>               # enter the exact project folder, never a parent
git init                          # creates a new, separate .git in this folder
git rev-parse --show-toplevel     # must print THIS folder, not the parent
git add .                         # stages everything, including files you may not expect
git status                        # no path may start with ../
git commit -m "Initial commit"    # first commit, local only
git remote add origin <url>       # link the local repo to the empty GitHub repo
git branch -M main                # rename the current branch to main
git push -u origin main           # upload and set upstream tracking
```

### Confirm

```bash
git log --oneline                 # full history for this branch
git status                        # staged, unstaged, untracked, and branch tracking
git ls-files                      # every file Git currently tracks
```

### Preventing it permanently

Unstage, untrack and ignore are repair tools, used once. A `.gitignore` in a parent has
no effect on a subfolder that already has its own `.git`, because Git stops searching
upward the moment it finds one.

The habit that makes this section unnecessary — the first three commands in any new
project folder, before writing code:

```bash
cd <new-project-folder>           # do this before writing any code
git init                          # creates a new, separate .git in this folder
git rev-parse --show-toplevel     # prints the repo root Git is actually using
```

[⬆ Back to top](#contents)

---

## Workflow 8 — Files show as deleted but not staged

```text
Changes not staged for commit:
        deleted:    Coal.txt
        deleted:    Payload.json
```

The files are tracked by Git, gone from disk, and the deletion has not been committed.
Git is reporting a fact, not a problem — the decision is whether the deletion was
intentional.

### Verify

```bash
git status                        # which files, and are they staged or unstaged
git log --oneline -- <file>       # when it last changed, and why
git show HEAD:<file> | head -20   # what it contained, straight from the last commit
```

The third command is the one people skip. A tracked file still exists in history even
after it leaves the disk, so you can always read it before deciding.

### Read

| Situation | Act |
|---|---|
| Deletion was intentional | Stage and commit it |
| Deleted by accident | Restore from the last commit |
| Unsure what it held | Read it first with `git show HEAD:<file>` |

### Act

Commit the deletion:

```bash
git rm <file-1> <file-2>          # stage the deletion of both files
git status                        # confirm only these two are staged
git commit -m "Remove <file-1> and <file-2>"# record the deletion
git push                          # upstream is already set, so no arguments needed
```

Or restore the files:

```bash
git restore <file-1> <file-2>     # bring both back from the last commit
```

`git restore` works here precisely because the files are still tracked — Git pulls them
back from the last commit.

### Confirm

```bash
git status                        # clean, or exactly the deletion you intended
git ls-files | findstr <name>     # Windows; use grep on macOS/Linux
```

> **Deleting a file does not remove its contents from the repository.** Every previous
> version stays in history and remains readable by anyone with access. If the file held
> credentials, a token, or anything else that should not have been committed, deleting
> it is not the fix — the secret must be rotated, and removing it from history needs
> `git filter-repo` plus a force-push.

[⬆ Back to top](#contents)

---

## Commands that need a check first

| Command | Destroys | Check before |
|---|---|---|
| `git reset --hard` | Uncommitted changes, permanently | `git status`, `git stash` if unsure |
| `git clean -fd` | Untracked files, bypassing the Recycle Bin | `git clean -nd` — dry run |
| `git restore <file>` | Unstaged edits to that file | `git diff <file>` |
| `git push --force-with-lease` | Remote commits | `git log HEAD..origin/main` |
| `git rm -r --cached` | Tracking (files survive) | `git ls-files <path>` |
| `git branch -D` | An unmerged branch | `git log --oneline <branch>` |
| `git rm <file>` | The file, and stages the deletion | `git show HEAD:<file>` |

Two that look dangerous and are not: `git fetch` changes no files, and `git stash`
is fully reversible with `git stash pop`.

> **`git clean` skips `.gitignore`d files** unless you add `-x`. That is usually what
> you want — and occasionally a surprise.

[⬆ Back to top](#contents)
