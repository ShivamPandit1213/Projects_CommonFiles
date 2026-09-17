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
- [Workflow 9 — Switching branches](#workflow-9--switching-branches)
- [Workflow 10 — Keeping two clones in sync](#workflow-10--keeping-two-clones-in-sync)
- [Troubleshooting index](#troubleshooting-index)
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

## Workflow 9 — Switching branches

Two things bite on a branch switch: files held open by an IDE, and `.gitignore` rules
that differ between branches. The second is the dangerous one, because it is silent.

### Verify

```bash
git status                        # a switch carries uncommitted work with you
git branch -a                     # which branches exist locally and on the remote
git fetch origin                  # refresh the remote snapshot first
```

Commit or stash anything pending before switching, and decide which branch it belongs
on. Uncommitted work follows you across the switch and lands wherever you commit it.

### Act

```bash
git switch <branch>               # branch already exists locally
git switch -c <branch> origin/<branch>   # first time: create it from the remote
git status                        # confirm the branch and its tracking
```

> **Close your IDE first on Windows.** A loaded project holds directory handles open,
> and Git prompts `Deletion of directory '...' failed. Should I try again? (y/n)` for
> each one. Answering `n` completes the switch but leaves empty folders behind, which
> can confuse the build path. The switch itself still succeeds.

### The check people skip — `.gitignore` differs per branch

`.gitignore` is a tracked file, so each branch carries its own version. A file protected
on `main` can be completely unprotected on a feature branch created before the rule was
added.

```bash
type .gitignore                   # Windows; use cat on macOS/Linux
git status                        # anything newly untracked is unprotected here
```

Any file that appears under "Untracked files" after a switch — and did not appear before
— is a file this branch does not ignore. If one of them holds credentials, a `git add .`
here will commit them.

### Act — align the ignore rules

Append the missing entries:

```bash
echo test-output/>>.gitignore     # no space before >> in CMD, or you get a stray char
type .gitignore                   # confirm each line landed on its own row
git status                        # the files should disappear from the listing
```

Or take the maintained version from the other branch wholesale, which also prevents the
two drifting further apart:

```bash
git checkout origin/main -- .gitignore .gitattributes   # take both files from main
git status                        # the untracked entries should vanish
git diff --cached                 # read exactly what you are about to commit
```

### Bring the branch up to date

A branch that is *behind* is building on stale code:

```bash
git log --oneline <branch>..origin/main   # what main has that this branch does not
git merge origin/main             # fast-forward if the branch is 0 ahead
git log --oneline -5              # confirm main's commits are now present
```

### Confirm

```bash
git status                        # "up to date with 'origin/<branch>'"
git log --oneline -3              # your commit on top
```

Then refresh the repository's Branches page. The branch should read 0 behind and however
many commits ahead you pushed.

[⬆ Back to top](#contents)

---

## Workflow 10 — Keeping two clones in sync

Two working copies of the same repository drift independently. Each has its own branch,
its own uncommitted work, and its own snapshot of the remote. Neither knows the other
exists, and `git status` in one tells you nothing about the other.

### Verify — in each clone separately

```bash
git rev-parse --show-toplevel     # which clone am I in
git branch                        # which branch this clone is on
git status                        # uncommitted work, and its branch
git fetch origin                  # refresh this clone's remote snapshot
git log --oneline HEAD..origin/main    # commits this clone is missing
```

Run these in the second clone before assuming anything. A clone that has not fetched
recently reports "up to date" from a cached snapshot that may be days old.

### Read

The fetch output names the branch tips, which is where a stale clone shows itself:

```text
f90bc4c (main) Remove Coal.txt and Payload.json
bb6e9a7 (origin/main, origin/HEAD) Add README pointing at ProjectInfo.html
```

Local `main` sits at `f90bc4c` while `origin/main` is at `bb6e9a7` — six commits behind.
The label in parentheses tells you where each ref points.

### Act — finish outstanding work first

Uncommitted work belongs to the clone and the branch it is sitting on. Commit or stash
it before switching or merging, or it follows you somewhere it does not belong.

```bash
git status                        # read what is outstanding
git add <files>                   # stage deliberately, not with a blanket .
git commit -m "what changed"             # one commit, one change
git push origin <branch>          # push the branch you are actually on
```

### Act — then sync

```bash
git switch main                   # close the IDE first; see Workflow 9
git log --oneline HEAD..origin/main    # confirm what is incoming
git merge origin/main             # fast-forward when nothing is local
git log --oneline -3              # newest remote commit should be on top
```

### Confirm

```bash
git status                        # "up to date with 'origin/main'"
git log --oneline origin/main..HEAD    # empty means nothing unpushed
```

### Pick one clone and stay in it

Two clones is a workflow smell, not a feature. Files deleted in one still exist in the
other; ignore rules fixed in one stay broken in the other; and work committed in one is
invisible until the other fetches.

If a second clone exists for a reason — a different branch checked out long-term, or a
separate build — keep it read-only and do the work in one place.

> **Avoid keeping a clone inside a synced folder** such as OneDrive, Dropbox or Google
> Drive. The sync client replicates `.git` while Git is writing to it, which can corrupt
> the index mid-operation, and it uploads every `target/` and `test-output/` rebuild. A
> plain local path is the safer home.

[⬆ Back to top](#contents)

---

## Troubleshooting index

Find the message you are seeing, read the cause, run the fix. Every entry here was hit
on this project.

### Git — remote and sync

| Message or symptom | Cause | Fix |
|---|---|---|
| `! [rejected] main -> main (fetch first)` | Remote has commits you do not | `git fetch` then compare both directions — [Workflow 3](#workflow-3--push-rejected) |
| `Your branch and 'origin/main' have diverged, and have 1 and 9 different commits` | Often **stale** — your remote snapshot predates a merge | `git fetch origin` then `git log --oneline origin/main..HEAD`. Empty means you are only behind; the pull fast-forwards |
| `Your branch is behind 'origin/main' by 2 commits, and can be fast-forwarded` | Simply behind, no conflict | `git pull --rebase origin main` |
| `fatal: 'origin' does not appear to be a git repository` | No remote configured | `git remote add origin <url>` |
| `error: remote origin already exists.` | `git remote add` run twice | `git remote set-url origin <url>` |
| `remote: Repository not found.` | URL points at a repo that does not exist | Check `git remote -v` against the browser URL |
| `src refspec main does not match any` | No commits exist yet on that branch | Commit first, then push |

### Git — working tree and staging

| Message or symptom | Cause | Fix |
|---|---|---|
| `deleted: <file>` under *not staged* | File removed from disk, deletion not recorded | Intentional → `git rm <file>`. Accidental → `git restore <file>` — [Workflow 8](#workflow-8--files-show-as-deleted-but-not-staged) |
| `error: pathspec '<file>' did not match any file(s) known to git` | The file is untracked, or already unstaged | Nothing to fix. `git rm --cached` only works on tracked files |
| A credentials file appears as untracked after switching branches | `.gitignore` is tracked, so it **differs per branch** | `git checkout origin/main -- .gitignore` — [Workflow 9](#workflow-9--switching-branches) |
| `git add .` staged files you did not expect | `.` means everything not ignored | `git restore --staged <file>`, then add the ignore rule |
| A file you deleted is still present in another clone | Clones are independent working copies | Fetch and merge in that clone — [Workflow 10](#workflow-10--keeping-two-clones-in-sync) |
| A clone reports "up to date" but is six commits behind | Its remote snapshot is cached, not live | `git fetch origin` before trusting any status |
| Empty folders left behind after a branch switch | Windows held directory handles open | Close the IDE, delete the folders manually. Git does not track empty directories |
| `Deletion of directory '...' failed. Should I try again? (y/n)` | Same as above, during the switch | Answer `n`. The switch still completes |

### Git — history and recovery

| Message or symptom | Cause | Fix |
|---|---|---|
| Committed to the wrong branch | Uncommitted work follows you across a switch | `git reset --soft HEAD~1`, switch, commit again |
| Committed a file that should never have been committed | — | Deleting it later does **not** remove it from history. Rotate the secret, then `git filter-repo` |
| A commit seems to have vanished | Usually after `reset --hard` | `git reflog`, find the hash, `git show <hash>`, then `git reset --hard <hash>` — [Workflow 6](#workflow-6--recovering-lost-work) |
| Conflict markers `<<<<<<<` committed by mistake | Resolved and staged without deleting the markers | `grep -rn "<<<<<<<" . ` then fix each file and amend |
| Terminal stuck at a `:` prompt | Git's pager is waiting | Press `q`. Permanently: `git config --global core.pager cat` |
| `warning: CRLF will be replaced by LF` | Line-ending normalisation | Expected on Windows with a `.gitattributes` in place. Not an error |

### Maven and TestNG

| Message or symptom | Cause | Fix |
|---|---|---|
| A browserless suite launches real browsers | Surefire 3.6.0+ drops `surefire-testng` and routes TestNG through the JUnit Platform engine, ignoring `<suiteXmlFiles>` | Pin `<surefire.version>` to 3.5.6 or lower **and** pin the provider as a plugin-level dependency |
| `Using auto detected provider ...TestNGProvider` | Provider resolved implicitly — a version bump can change it silently | Pin it explicitly; the log should then read `Using configured provider` |
| Test count far higher than the suite declares | The suite file is being ignored | Compare two runs: `mvn test -DsuiteFile=suites/config.xml` against plain `mvn test`. Different counts mean the suite file **is** being read |
| `Running TestSuite` instead of the suite's name | Surefire's own label for the TestNG run | Not a fault. Judge by test count and elapsed time, not this line |
| `Tests run: 0` with BUILD SUCCESS | Suite points at packages that no longer exist | Check the package names in the suite file against the source tree after any refactor |
| `skip non existing resourceDirectory src/test/resources` | Directory absent because Git does not track empty folders | Add a `.gitkeep` file inside it |
| `SLF4J(W): No SLF4J providers were found` | No logging binding on the classpath | Harmless. Add `slf4j-simple` with test scope to silence it |
| Surefire declared under project `<dependencies>` | It is a build plugin, not a dependency | Move it to `<build><plugins>`. A provider pin belongs in `<plugin><dependencies>` |

### Jenkins

| Message or symptom | Cause | Fix |
|---|---|---|
| Build runs on the controller | `agent any` with no agents configured | Use `agent { label '...' }`; set the controller to 0 executors |
| A hung browser pins an executor forever | No pipeline timeout | `options { timeout(time: 30, unit: 'MINUTES') }` |
| A failed build goes unnoticed for weeks | No notification configured | `post { failure { emailext(...) } }` |
| Checkout appears twice in the stage view | An explicit `checkout scm` stage plus Declarative's automatic one | Delete the explicit stage |
| Every build reports "No Changes" | Nothing triggers it but a human | Add `githubPush()` and a `cron` trigger |
| Green build, nothing actually tested | Zero tests still passes | `junit testResults: '...', allowEmptyResults: false` |

### API testing — the traps that return a 200

| Message or symptom | Cause | Fix |
|---|---|---|
| Validation errors that do not match the payload you sent | A reused Idempotency-Key replayed a cached response | Rotate the key on every request — `{{$guid}}` in Postman |
| `Invalid signature` | The body changed after the signature was generated, including whitespace | Finalise the body, sign it, send without editing. Never press Beautify after signing |
| `errorCode: null` but the record is incomplete | HTTP 200 is returned for success, validation failure and auth failure alike | Assert on `errorCode` **and** on the contents of `resultIds`, never on status alone |
| Two identical `requestId` values in a row | The second response is a cached replay | Rotate the key. A genuine request always returns a new id |
| Data sent but silently not stored | Row identifiers missing, so the server cannot match the rows | Send the ids returned by the create response, with `actionFlag: "U"` |
| Duplicate child rows after an update | Id `0` with `actionFlag: "I"` means insert | Real id plus `"U"` modifies; the identifier decides, not the endpoint |

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
| `git switch <branch>` | Nothing, but changes which `.gitignore` applies | `type .gitignore` after switching |

Two that look dangerous and are not: `git fetch` changes no files, and `git stash`
is fully reversible with `git stash pop`.

> **`git clean` skips `.gitignore`d files** unless you add `-x`. That is usually what
> you want — and occasionally a surprise.

[⬆ Back to top](#contents)
