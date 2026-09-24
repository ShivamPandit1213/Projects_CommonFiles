# PAIMANA — Complete Git & Jenkins Operational Reference

A comprehensive, tabular cheat sheet mapping every command to its exact function, internal mechanism, and situational trigger.

---

## 1. Network, DNS & Diagnostics

| Command | Purpose | What It Does Under the Hood | When to Use |
| :--- | :--- | :--- | :--- |
| `ping github.com` | Verify GitHub Reachability | Sends ICMP echo request packets to verify internet routing and server availability. | When Git operations hang or fail with connection timeouts. |
| `ipconfig /flushdns` | Reset DNS Resolver Cache | Clears and refreshes the Windows local DNS name resolution cache. | When receiving `fatal: unable to access ... Could not resolve host: github.com`. |
| `git config --global --get http.proxy` | Inspect Git Proxy | Reads the global `.gitconfig` file and outputs the currently configured proxy address. | When troubleshooting connection issues in corporate or VPN environments. |
| `git config --global --unset http.proxy` | Remove Git Proxy | Deletes the `http.proxy` entry from the global Git configuration. | When switching off an office proxy or fixing invalid proxy routing. |
| `git status` | Inspect Working Tree | Compares the working tree against the index (staging area) and current `HEAD` commit. | Run before and after every staging, committing, or pushing operation. |
| `git remote -v` | Verify Remote URLs | Prints the read/write URLs associated with the `origin` alias. | Run to confirm the repository is pointing to the correct GitHub URL. |
| `git rev-parse --show-toplevel` | Identify Repository Root | Traverses upward and outputs the absolute path where the active `.git` directory lives. | Run to verify a subproject is completely isolated from any parent folder `.git`. |
| `git log --oneline` | View Concise History | Traverses the commit graph from `HEAD` backward, printing each commit hash and subject line. | Run to confirm local commits were created or to locate hashes for rollbacks. |
| `git diff` | View Unstaged Changes | Generates a patch representation of modifications in the working directory not yet staged. | Run before `git add` to review line-by-line modifications. |
| `git ls-files` | Inspect Tracked Index | Dumps the list of all file paths currently recorded in the Git staging index. | Run to verify no unwanted binaries (`.class`, `.jar`, `target/`) are tracked. |

---

## 2. Project Isolation, Setup & Initialization

| Command | Purpose | What It Does Under the Hood | When to Use |
| :--- | :--- | :--- | :--- |
| `cd /d <path>` | Switch Directory Across Drives | Changes both the active directory and disk volume drive letter in Windows CMD. | When navigating between project folders across different drives. |
| `rmdir /s /q .git` | Wipe Local Git Repository (CMD) | Recursively and silently deletes the hidden `.git` folder from the file system. | When resetting an accidental parent repository (e.g., `JavaSelenium`) without touching code. |
| `git init` | Initialize Local Repository | Creates an empty `.git` directory containing `objects`, `refs`, and template configurations. | When setting up a brand-new repository in an isolated project folder. |
| `git remote add origin <url>` | Link Remote Repository | Creates a named pointer `origin` mapping to the specified GitHub repository URL. | Run immediately after initializing a repository and creating an empty GitHub repo. |
| `git remote remove origin` | Unlink Remote Repository | Deletes the `origin` reference entry from `.git/config`. | When a repository is pointing to the wrong GitHub project or needs re-pointing. |
| `git branch -M main` | Set Default Branch Name | Renames the current HEAD branch reference to `main`. | Prior to initial push to standardize branch naming with GitHub conventions. |
| `git push -u origin main` | Initial Push & Set Tracking | Transfers packfiles to GitHub and writes `branch.main.remote` and `branch.main.merge` to `.git/config`. | Run on the very first push of a new repository or branch. |
| `git push` | Push Tracked Commits | Uploads local commits on the active branch to its pre-configured upstream branch. | For all standard daily uploads after upstream tracking has been configured. |

---

## 3. Staging, Commits & Rollbacks

| Command | Purpose | What It Does Under the Hood | When to Use |
| :--- | :--- | :--- | :--- |
| `git add .` | Stage All Modifications | Adds all new, modified, and deleted working tree files to the index staging area. | When bundling all project changes into the next commit snapshot. |
| `git add "filename"` | Stage Specific File | Stages only the designated file path (double quotes protect paths containing spaces). | When committing specific individual files (e.g., `Jenkinsfile` or `pom.xml`). |
| `git commit -m "message"` | Create Commit Object | Packages the current staging area into a tree object, writes metadata, and advances `HEAD`. | Directly following `git add` to record a permanent change point. |
| `git rm -r --cached <folder>` | Untrack Folder from Git | Removes the directory tree from the Git index while leaving all physical files on disk. | When build directories (like `target/` or `.vscode/`) were tracked by accident. |
| `git reset --soft HEAD~1` | Undo Previous Commit | Moves the `HEAD` and branch reference back one commit while leaving all changes staged. | When a commit was made prematurely or needs an updated message. |
| `git revert HEAD` | Safely Revert Public Commit | Appends a brand-new commit that applies the exact inverse diff of the `HEAD` commit. | When an erroneous commit has already been pushed to a remote GitHub branch. |
| `git checkout -- .` | Discard Uncommitted Edits | Overwrites modified files in the working directory with the exact copies from `HEAD`. | When local experimental changes need to be discarded completely. |

---

## 4. Synchronization & Merge Conflict Resolution

| Command | Purpose | What It Does Under the Hood | When to Use |
| :--- | :--- | :--- | :--- |
| `git fetch origin` | Download Remote References | Fetches commit objects and updates remote tracking branches (`origin/main`) without merging. | To inspect remote commits before deciding how to integrate them. |
| `git pull --no-rebase origin main` | Fetch and Merge | Pulls commits and generates a standard three-way merge commit joining histories. | When remote has commits you lack (`[rejected] fetch first` error). |
| `git checkout --ours <file>` | Resolve Conflict: Keep Local | Overwrites the conflicted file in the working tree with the stage 2 version (`HEAD`). | During merge conflicts when your local implementation is the one to retain. |
| `git checkout --theirs <file>` | Resolve Conflict: Keep Remote | Overwrites the conflicted file in the working tree with the stage 3 version (incoming). | During merge conflicts when the GitHub/remote version is the one to retain. |
| `git rm <file>` | Resolve Modify/Delete Conflict | Removes the file from both the working directory and the staging index. | When a file was deleted on one side and the deletion should be accepted. |
| `git merge --abort` | Abort Active Merge | Resets the working index and working tree back to the pre-merge `HEAD` state. | When merge conflicts are extensive and you need to safely return to a clean baseline. |
| `git stash` | Park Working Directory Changes | Serializes uncommitted edits into storage refs (`refs/stash`) and resets to `HEAD`. | When `git pull` is refused due to uncommitted local edits. |
| `git stash pop` | Reapply Parked Changes | Applies the latest stashed patch on top of the working tree and removes it from the stash stack. | Immediately after completing a `git pull` on a clean tree. |
| `git pull --no-rebase -X ignore-space-at-eol origin main` | Merge Ignoring CRLF Differences | Executes the merge while instructing the diff engine to disregard carriage-return mismatches. | When files conflict across every line strictly due to Windows (CRLF) vs Unix (LF) formatting. |

---

## 5. Jenkins & Build Environment

| Command | Purpose | What It Does Under the Hood | When to Use |
| :--- | :--- | :--- | :--- |
| `ren Jenkinsfile.txt Jenkinsfile` | Remove Windows `.txt` Suffix | Invokes Windows shell renaming to strip the `.txt` extension from the filename. | When Windows text editors save `Jenkinsfile` with an unwanted extension. |
| `type Jenkinsfile` | Read File to Console | Streams the raw character contents of `Jenkinsfile` directly to the CMD terminal. | To verify script logic and pipeline stages directly from the command prompt. |
| `mvn clean` | Purge Target Artifacts | Triggers the `maven-clean-plugin` to delete the `target/` directory and compiled `.class` files. | Run prior to initial Git commits to keep build artifacts out of source control. |
| `"%JAVA_HOME%\bin\java" -jar jenkins.war` | Run Jenkins Standalone | Launches the embedded Winstone servlet engine hosting Jenkins using Java 21. | When starting your local Jenkins service on port 8080. |
