# Jenkins Job Types Setup Guide

A step-by-step reference for creating each common Jenkins job type, showing **which fields are mandatory**, which are optional, and **what each field is for**.

---

## Table of Contents

1. [How to Read This Guide](#1-how-to-read-this-guide)
2. [Prerequisites](#2-prerequisites)
3. [Choosing the Right Job Type](#3-choosing-the-right-job-type)
4. [Common First Step: Creating a New Item](#4-common-first-step-creating-a-new-item)
5. [Freestyle Project](#5-freestyle-project)
6. [Pipeline](#6-pipeline)
7. [Multibranch Pipeline](#7-multibranch-pipeline)
8. [Organization Folder](#8-organization-folder)
9. [Multi-configuration Project (Matrix)](#9-multi-configuration-project-matrix)
10. [Folder](#10-folder)
11. [Setting Up Credentials](#11-setting-up-credentials)
12. [Cron Schedule Cheat Sheet](#12-cron-schedule-cheat-sheet)
13. [Common Mistakes and Fixes](#13-common-mistakes-and-fixes)
14. [Worked Example: Playwright Freestyle Job on Windows](#14-worked-example-playwright-freestyle-job-on-windows)

---

## 1. How to Read This Guide

Jenkins itself enforces very few fields when you click **Save**. Usually only the item name is strictly required. However, many fields become mandatory the moment you enable a related option, and a job saved with nothing configured will run but do nothing useful. So each field below is marked with one of these labels:

| Label | Meaning |
|---|---|
| ✅ **Mandatory** | Jenkins will not let you create or save the job without it. |
| ⚠️ **Conditionally mandatory** | Required only once you enable the parent option (e.g. selecting *Git* makes *Repository URL* required). |
| 🔶 **Practically required** | Jenkins lets you save without it, but the job will fail or do nothing. |
| ⬜ **Optional** | Improves behaviour, safety or housekeeping, but not needed to run. |

> Field names match the default Jenkins UI (2.4xx+ LTS). Some labels vary slightly between versions and installed plugins.

---

## 2. Prerequisites

Before creating jobs, make sure the required plugins are installed via **Manage Jenkins → Plugins**.

| Plugin | Needed for |
|---|---|
| Git | Pulling code from Git repositories (all job types) |
| Pipeline | Pipeline jobs and Jenkinsfile support |
| Pipeline: Multibranch | Multibranch Pipeline jobs |
| Branch API | Multibranch and Organization Folder jobs |
| GitHub Branch Source / Bitbucket Branch Source / GitLab Branch Source | Multibranch and Organization Folder sources |
| Matrix Project | Multi-configuration (Matrix) jobs |
| Folders | Folder items |
| Credentials Binding | Using secrets inside builds |
| Timestamper | Timestamps in console output |
| Workspace Cleanup | Deleting the workspace before/after builds |
| JUnit | Publishing test reports |

You also need:

- At least one **agent** (or the built-in node) able to run builds.
- **Credentials** for any private repository (see [Section 11](#11-setting-up-credentials)).
- Build tools (JDK, Maven, Node.js, etc.) installed on the agent or configured in **Manage Jenkins → Tools**.

---

## 3. Choosing the Right Job Type

| Job Type | Best for | Configuration lives in |
|---|---|---|
| **Freestyle Project** | Simple, single-purpose tasks (run a script, trigger a deploy) | Jenkins UI |
| **Pipeline** | Multi-stage CI/CD for one branch of one repo | Jenkinsfile (inline or in repo) |
| **Multibranch Pipeline** | One repo with many branches / pull requests | Jenkinsfile in each branch |
| **Organization Folder** | Every repo in a GitHub org / Bitbucket team / GitLab group | Jenkinsfile in each repo |
| **Multi-configuration (Matrix)** | Running the same build across many combinations (OS × JDK × browser) | Jenkins UI |
| **Folder** | Grouping and organizing other jobs, scoping credentials | Jenkins UI |

**Quick rule:** for new projects, prefer **Pipeline** or **Multibranch Pipeline** so the build definition is version-controlled alongside the code.

---

## 4. Common First Step: Creating a New Item

Every job type starts the same way.

**Step 1.** From the Jenkins dashboard, click **New Item** (top-left).

**Step 2.** Fill in the item name.

| Field | Status | Purpose |
|---|---|---|
| Enter an item name | ✅ Mandatory | Unique identifier for the job. Becomes part of the job URL and workspace path. Avoid spaces and special characters (use `my-app-build`, not `My App Build!`). |

**Step 3.** Select the job type.

| Field | Status | Purpose |
|---|---|---|
| Item type (Freestyle, Pipeline, etc.) | ✅ Mandatory | Decides which configuration screen and behaviour the job gets. Cannot be changed later; you would need to recreate the job. |
| Copy from | ⬜ Optional | Clones the configuration of an existing job, saving setup time for similar jobs. |

**Step 4.** Click **OK** to open the configuration page.

---

## 5. Freestyle Project

**What it is:** The classic, UI-driven job type. You configure source code, triggers, build steps and post-build actions through form fields.

**Use when:** You need a quick, simple job such as running a shell script, a scheduled cleanup, or a one-step deployment.

### Step 1 — General

| Field | Status | Purpose |
|---|---|---|
| Description | ⬜ Optional | Explains what the job does. Shown on the job page; helps other team members. |
| Discard old builds | ⬜ Optional (recommended) | Deletes old build records and artifacts to save disk space. |
| ↳ Days to keep builds / Max # of builds to keep | ⚠️ Conditionally mandatory (at least one) | Defines the retention rule. Without a value, nothing is discarded. |
| This project is parameterized | ⬜ Optional | Lets users supply inputs (branch name, environment, version) when starting a build. |
| ↳ Parameter **Name** | ⚠️ Conditionally mandatory | Variable name used in build steps (e.g. `$ENVIRONMENT`). |
| ↳ Default Value / Choices / Description | ⬜ Optional | Pre-filled value, list of allowed values, and help text. |
| Disable this project | ⬜ Optional | Stops new builds from running without deleting the job. |
| Execute concurrent builds if necessary | ⬜ Optional | Allows multiple builds of this job to run at the same time. Leave off if builds share resources. |
| Restrict where this project can be run | ⬜ Optional | Pins the job to specific agents. |
| ↳ Label Expression | ⚠️ Conditionally mandatory | Agent label(s) to run on, e.g. `linux && docker`. |

**Advanced (click "Advanced" under General):**

| Field | Status | Purpose |
|---|---|---|
| Quiet period | ⬜ Optional | Seconds to wait before starting, so several quick commits trigger one build. |
| Retry Count | ⬜ Optional | How many times to retry an SCM checkout that fails. |
| Block build when upstream project is building | ⬜ Optional | Prevents running against half-built dependencies. |
| Use custom workspace → Directory | ⚠️ Conditionally mandatory | Overrides the default workspace path. |

### Step 2 — Source Code Management

| Field | Status | Purpose |
|---|---|---|
| SCM type (None / Git) | ⬜ Optional | Choose **Git** to pull code; **None** for jobs that don't need a repository. |
| ↳ Repository URL | ⚠️ Conditionally mandatory | Where to clone from, e.g. `https://github.com/org/repo.git`. |
| ↳ Credentials | ⚠️ Conditionally mandatory (private repos) | Username/token or SSH key used to access the repository. Public repos can use *- none -*. |
| ↳ Branches to build (Branch Specifier) | ⚠️ Conditionally mandatory | Branch to check out. Default is `*/master`; change to `*/main` if your repo uses `main`. |
| ↳ Repository browser | ⬜ Optional | Adds links from Jenkins changes to your Git web UI. |
| ↳ Additional Behaviours | ⬜ Optional | Extras like *Clean before checkout*, *Shallow clone*, *Checkout to a sub-directory*. |

### Step 3 — Build Triggers

All triggers are optional; without any, the job only runs when started manually.

| Field | Status | Purpose |
|---|---|---|
| Trigger builds remotely | ⬜ Optional | Allows triggering via a URL (e.g. from scripts). |
| ↳ Authentication Token | ⚠️ Conditionally mandatory | Secret token that must be included in the trigger URL. |
| Build after other projects are built | ⬜ Optional | Chains this job after upstream jobs. |
| ↳ Projects to watch | ⚠️ Conditionally mandatory | Names of the upstream jobs. |
| ↳ Trigger only if build is stable / unstable / fails | ⬜ Optional | Controls which upstream result starts this job. |
| Build periodically | ⬜ Optional | Runs on a fixed schedule (nightly builds, cleanups). |
| ↳ Schedule | ⚠️ Conditionally mandatory | Cron expression, e.g. `H 2 * * *` (see [Section 12](#12-cron-schedule-cheat-sheet)). |
| GitHub hook trigger for GITScm polling | ⬜ Optional | Builds immediately when GitHub sends a webhook. Requires a webhook configured in GitHub. |
| Poll SCM | ⬜ Optional | Jenkins checks the repo for changes on a schedule and builds only if something changed. |
| ↳ Schedule | ⚠️ Conditionally mandatory | Cron expression for how often to poll, e.g. `H/5 * * * *`. |

### Step 4 — Build Environment

| Field | Status | Purpose |
|---|---|---|
| Delete workspace before build starts | ⬜ Optional | Guarantees a clean build with no leftovers from previous runs. |
| Use secret text(s) or file(s) | ⬜ Optional | Injects credentials as environment variables, masked in logs. |
| ↳ Binding → Variable + Credentials | ⚠️ Conditionally mandatory | Variable name to expose and which stored credential to use. |
| Add timestamps to the Console Output | ⬜ Optional | Prefixes each log line with a time, useful for spotting slow steps. |
| Terminate a build if it's stuck | ⬜ Optional | Aborts builds that hang beyond a time limit. |
| ↳ Time-out strategy + Timeout minutes | ⚠️ Conditionally mandatory | Defines when a build is considered stuck. |

### Step 5 — Build Steps

| Field | Status | Purpose |
|---|---|---|
| Add build step | 🔶 Practically required | The actual work of the job. Without at least one step, the build succeeds but does nothing. |
| ↳ Execute shell → Command | ⚠️ Conditionally mandatory | Shell commands run on Linux/macOS agents. |
| ↳ Execute Windows batch command → Command | ⚠️ Conditionally mandatory | Batch commands run on Windows agents. |
| ↳ Invoke top-level Maven targets → Goals | ⚠️ Conditionally mandatory | Maven goals, e.g. `clean install`. Maven version is also selected here. |
| ↳ Invoke Gradle script → Tasks | ⚠️ Conditionally mandatory | Gradle tasks, e.g. `clean build`. |

Example shell step:

```bash
npm ci
npm test
npm run build
```

### Step 6 — Post-build Actions

| Field | Status | Purpose |
|---|---|---|
| Archive the artifacts | ⬜ Optional | Saves build outputs (JARs, ZIPs, reports) with the build record. |
| ↳ Files to archive | ⚠️ Conditionally mandatory | Ant-style pattern, e.g. `target/*.jar` or `dist/**`. |
| Publish JUnit test result report | ⬜ Optional | Shows test results and trends on the job page. |
| ↳ Test report XMLs | ⚠️ Conditionally mandatory | Path pattern to JUnit XML files, e.g. `**/target/surefire-reports/*.xml`. |
| Build other projects | ⬜ Optional | Triggers downstream jobs after this one. |
| ↳ Projects to build | ⚠️ Conditionally mandatory | Names of downstream jobs. |
| E-mail Notification | ⬜ Optional | Emails people when builds fail or recover. |
| ↳ Recipients | ⚠️ Conditionally mandatory | Space-separated email addresses. |

### Step 7 — Save and Test

1. Click **Save**.
2. Click **Build Now** (or **Build with Parameters** if parameterized).
3. Open the build number → **Console Output** to verify the result.

---

## 6. Pipeline

**What it is:** A job whose entire process (checkout, build, test, deploy) is defined in code using a **Jenkinsfile** written in Groovy-based Pipeline syntax.

**Use when:** You need multi-stage CI/CD for a single branch and want the build logic version-controlled.

### Step 1 — General

Same options as Freestyle (Description, Discard old builds, Parameterized, Disable, concurrent builds). All ⬜ Optional. Pipeline-specific additions:

| Field | Status | Purpose |
|---|---|---|
| Do not allow concurrent builds | ⬜ Optional | Prevents overlapping runs. Can also be set in the Jenkinsfile with `options { disableConcurrentBuilds() }`. |
| Pipeline speed/durability override | ⬜ Optional | Trades crash resilience for speed. Leave at default unless you have performance issues. |

> Most General settings can instead be declared inside the Jenkinsfile (`options {}`, `parameters {}`, `triggers {}`). Settings in the Jenkinsfile overwrite the UI after the first run.

### Step 2 — Build Triggers

Same as Freestyle: *Build periodically*, *Poll SCM*, *GitHub hook trigger*, *Trigger builds remotely*, *Build after other projects*. All ⬜ Optional, with the same conditionally mandatory sub-fields (Schedule, Token, Projects to watch).

### Step 3 — Pipeline Definition (the core section)

| Field | Status | Purpose |
|---|---|---|
| Definition | ✅ Mandatory | Chooses where the pipeline code comes from: **Pipeline script** (typed into Jenkins) or **Pipeline script from SCM** (Jenkinsfile in your repo). |

#### Option A — Pipeline script

| Field | Status | Purpose |
|---|---|---|
| Script | 🔶 Practically required | The pipeline code itself. An empty script fails at runtime. |
| Use Groovy Sandbox | ⬜ Optional (keep enabled) | Runs the script with security restrictions. Disabling it requires administrator script approval. |
| Pipeline Syntax (link) | — | Opens the Snippet Generator, which writes step syntax for you. |

#### Option B — Pipeline script from SCM (recommended)

| Field | Status | Purpose |
|---|---|---|
| SCM | ⚠️ Conditionally mandatory | Source control type, usually **Git**. |
| ↳ Repository URL | ⚠️ Conditionally mandatory | Repo containing the Jenkinsfile. |
| ↳ Credentials | ⚠️ Conditionally mandatory (private repos) | Access to the repository. |
| ↳ Branch Specifier | ⚠️ Conditionally mandatory | Branch to read the Jenkinsfile from (e.g. `*/main`). |
| Script Path | ⚠️ Conditionally mandatory | Path to the Jenkinsfile in the repo. Default `Jenkinsfile`; change if it lives elsewhere (e.g. `ci/Jenkinsfile`). |
| Lightweight checkout | ⬜ Optional | Reads only the Jenkinsfile instead of cloning the whole repo first. Faster; leave enabled. |

### Step 4 — Sample Jenkinsfile

```groovy
pipeline {
    agent any

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    stages {
        stage('Checkout') {
            steps { checkout scm }
        }
        stage('Build') {
            steps { sh 'npm ci && npm run build' }
        }
        stage('Test') {
            steps { sh 'npm test' }
        }
    }

    post {
        always  { junit allowEmptyResults: true, testResults: 'reports/*.xml' }
        failure { echo 'Build failed!' }
    }
}
```

| Jenkinsfile block | Status | Purpose |
|---|---|---|
| `pipeline {}` | ✅ Mandatory | Root of every Declarative Pipeline. |
| `agent` | ✅ Mandatory | Where the pipeline runs (`any`, a label, or a Docker image). |
| `stages {}` with at least one `stage` | ✅ Mandatory | Ordered phases of the build. |
| `steps {}` inside each stage | ✅ Mandatory | Commands executed in that stage. |
| `options`, `environment`, `parameters`, `triggers`, `post` | ⬜ Optional | Settings, variables, inputs, schedules and cleanup/notification actions. |

### Step 5 — Save and Test

Click **Save** → **Build Now**. The **Stage View** on the job page shows each stage's status and duration.

---

## 7. Multibranch Pipeline

**What it is:** A container job that scans a repository, finds every branch (and optionally pull request) containing a Jenkinsfile, and automatically creates a Pipeline job for each one.

**Use when:** Your team works with feature branches and pull requests and you want each one built automatically.

### Step 1 — General

| Field | Status | Purpose |
|---|---|---|
| Display Name | ⬜ Optional | Friendlier name shown in the UI (the item name stays in the URL). |
| Description | ⬜ Optional | Explains the project. |
| Disable | ⬜ Optional | Pauses scanning and building of all branches. |

### Step 2 — Branch Sources

| Field | Status | Purpose |
|---|---|---|
| Add source | 🔶 Practically required | Where to discover branches from: **Git**, **GitHub**, **Bitbucket**, **GitLab**. Without a source, no branches are found. |

**For a GitHub source:**

| Field | Status | Purpose |
|---|---|---|
| Credentials | 🔶 Practically required | GitHub username + personal access token (or GitHub App). Needed for private repos and strongly recommended for public ones to avoid API rate limits. |
| Repository HTTPS URL | ⚠️ Conditionally mandatory | The repository to scan, e.g. `https://github.com/org/repo`. |
| Behaviours → Discover branches | ⬜ Optional (added by default) | Which branches to build (e.g. exclude branches that are also PRs). |
| Behaviours → Discover pull requests from origin | ⬜ Optional (added by default) | Builds PRs opened from branches in the same repo. |
| Behaviours → Discover pull requests from forks | ⬜ Optional (added by default) | Builds PRs from forks; the *Trust* setting controls whose Jenkinsfile changes are trusted. |
| Behaviours → Filter by name (with wildcards) | ⬜ Optional | Include/exclude branches, e.g. include `main develop feature/*`. |
| Property strategy | ⬜ Optional | Applies properties to branches, e.g. suppress automatic builds for certain branches. |

**For a plain Git source:**

| Field | Status | Purpose |
|---|---|---|
| Project Repository | ⚠️ Conditionally mandatory | Repository URL. |
| Credentials | ⚠️ Conditionally mandatory (private repos) | Repository access. |
| Behaviours | ⬜ Optional | Discover branches / tags, filter by name, etc. |

### Step 3 — Build Configuration

| Field | Status | Purpose |
|---|---|---|
| Mode | ✅ Mandatory (default: *by Jenkinsfile*) | How each branch's pipeline is defined. |
| Script Path | ✅ Mandatory (default: `Jenkinsfile`) | Path of the Jenkinsfile in each branch. Only branches containing this file become jobs. |

### Step 4 — Scan Multibranch Pipeline Triggers

| Field | Status | Purpose |
|---|---|---|
| Periodically if not otherwise run | ⬜ Optional (recommended) | Rescans the repo to detect new/deleted branches if webhooks are missed. |
| ↳ Interval | ⚠️ Conditionally mandatory | How often to rescan, e.g. `1 hour` or `1 day`. |

> With webhooks configured, new commits trigger builds instantly. The periodic scan is a safety net.

### Step 5 — Orphaned Item Strategy

| Field | Status | Purpose |
|---|---|---|
| Discard old items | ⬜ Optional (enabled by default) | Removes jobs for branches deleted from the repo. |
| ↳ Days to keep old items / Max # of old items to keep | ⬜ Optional | How long jobs for deleted branches remain. Blank means delete immediately. |

### Step 6 — Other Sections

| Field | Status | Purpose |
|---|---|---|
| Appearance → Icon | ⬜ Optional | Cosmetic. |
| Health metrics | ⬜ Optional | Controls how the folder's weather icon is calculated. |
| Properties → Pipeline Libraries | ⬜ Optional | Shared libraries available to all branches. |

### Step 7 — Save

Clicking **Save** automatically runs **Scan Multibranch Pipeline Now**. Check **Scan Multibranch Pipeline Log** to confirm which branches were found and which were skipped (and why).

---

## 8. Organization Folder

**What it is:** Scans an entire GitHub organization, Bitbucket team/project, or GitLab group, and creates a Multibranch Pipeline for every repository that contains a Jenkinsfile.

**Use when:** You want every repo in an organization onboarded to CI automatically, without creating jobs one by one.

### Step 1 — General

| Field | Status | Purpose |
|---|---|---|
| Display Name / Description | ⬜ Optional | Friendly name and explanation. |

### Step 2 — Projects (Repository Sources)

| Field | Status | Purpose |
|---|---|---|
| Repository Sources | ✅ Mandatory | Platform type: GitHub Organization, Bitbucket Team/Project, or GitLab Group. |

**For GitHub Organization:**

| Field | Status | Purpose |
|---|---|---|
| API endpoint | ⬜ Optional | Leave default for github.com; set for GitHub Enterprise. |
| Credentials | 🔶 Practically required | Token or GitHub App with read access to the org's repos. Anonymous scanning hits rate limits quickly and can't see private repos. |
| Owner | ✅ Mandatory | Organization or user name to scan, e.g. `my-company`. |
| Behaviours → Discover branches / pull requests | ⬜ Optional (added by default) | Same purpose as in Multibranch. |
| Behaviours → Filter by name (with regular expression / wildcards) | ⬜ Optional | Limit which repositories are included. |

### Step 3 — Project Recognizers

| Field | Status | Purpose |
|---|---|---|
| Pipeline Jenkinsfile | ✅ Mandatory (default) | Decides which repos become jobs: only those containing the Jenkinsfile. |
| ↳ Script Path | ✅ Mandatory (default `Jenkinsfile`) | Expected location of the Jenkinsfile in each repo. |

### Step 4 — Scan Organization Triggers

| Field | Status | Purpose |
|---|---|---|
| Periodically if not otherwise run → Interval | ⬜ Optional (recommended) | Rescans the organization to find new or removed repos. |

### Step 5 — Orphaned Item Strategy & Child Strategies

| Field | Status | Purpose |
|---|---|---|
| Orphaned Item Strategy | ⬜ Optional | Removes jobs for deleted repositories. |
| Child Orphaned Item Strategy | ⬜ Optional | Removes jobs for deleted branches inside each repo. |
| Automatic branch project triggering → Branch names to build automatically | ⬜ Optional | Regex for which branches build automatically after scanning (default `.*` = all). |

### Step 6 — Save

Save triggers an organization scan. Review **Scan Organization Log** to see which repos were recognised.

---

## 9. Multi-configuration Project (Matrix)

**What it is:** A UI-configured job that runs the same build steps across every combination of defined variables ("axes"), e.g. 3 JDK versions × 2 operating systems = 6 builds.

**Use when:** You need compatibility testing across platforms, versions or browsers. (In Pipelines, the equivalent is the `matrix {}` directive.)

**Requires:** Matrix Project plugin.

### Step 1 — General, Source Code Management, Build Triggers, Build Environment

Same fields and rules as [Freestyle](#5-freestyle-project).

### Step 2 — Configuration Matrix (the core section)

| Field | Status | Purpose |
|---|---|---|
| Add axis | 🔶 Practically required | Defines a variable to vary. Without axes, there is no matrix and only one configuration runs. |
| ↳ **User-defined Axis** → Name | ⚠️ Conditionally mandatory | Variable name used in build steps, e.g. `BROWSER`. |
| ↳ **User-defined Axis** → Values | ⚠️ Conditionally mandatory | Space-separated values, e.g. `chrome firefox edge`. |
| ↳ **Agents** / **Label expression** axis → Name + nodes/labels | ⚠️ Conditionally mandatory | Runs each combination on different agents (e.g. `linux`, `windows`). |
| ↳ **JDK** axis | ⚠️ Conditionally mandatory | Picks JDK installations configured under **Tools**. |
| Combination Filter | ⬜ Optional | Groovy expression to skip invalid combinations, e.g. `!(OS == "linux" && BROWSER == "edge")`. |
| Run each configuration sequentially | ⬜ Optional | Runs combinations one after another instead of in parallel (saves resources). |
| Execution Strategy → Touchstone builds | ⬜ Optional | Runs a subset first; if it fails, the remaining combinations are skipped. |

### Step 3 — Build Steps

| Field | Status | Purpose |
|---|---|---|
| Add build step | 🔶 Practically required | Runs once per combination. Reference axis values as variables. |

Example:

```bash
echo "Testing on $BROWSER with Java $JDK_VERSION"
mvn test -Dbrowser=$BROWSER
```

### Step 4 — Post-build Actions

Same as Freestyle. Test reports are aggregated across all combinations on the parent job page.

### Step 5 — Save and Test

Click **Build Now**. The job page shows a grid with the result of each combination.

---

## 10. Folder

**What it is:** A container used to organize jobs into groups, similar to a directory. It does not build anything itself.

**Use when:** You have many jobs and want to group them by team, product or environment, or restrict credentials and permissions to a subset of jobs.

### Step 1 — Configuration

| Field | Status | Purpose |
|---|---|---|
| Display Name | ⬜ Optional | Friendly name shown in the UI. |
| Description | ⬜ Optional | Explains what the folder contains. |
| Health metrics | ⬜ Optional | How the folder's weather icon summarizes its jobs. |
| Properties → Pipeline Libraries | ⬜ Optional | Shared libraries available only to jobs inside this folder. |

### Step 2 — Folder-scoped Credentials (after saving)

1. Open the folder → **Credentials** → **Folder** store → **Global credentials** → **Add Credentials**.
2. Credentials added here are visible only to jobs inside this folder, which is safer than global credentials.

### Step 3 — Add Jobs

Open the folder and click **New Item**; jobs created there live inside it. Existing jobs can be moved via the job's **Move** option.

---

## 11. Setting Up Credentials

Most private-repository and deployment jobs need credentials. Add them via **Manage Jenkins → Credentials → System → Global credentials → Add Credentials** (or in a folder's credential store).

| Field | Status | Purpose |
|---|---|---|
| Kind | ✅ Mandatory | Type of secret: *Username with password*, *SSH Username with private key*, *Secret text*, *Secret file*, *Certificate*, or *GitHub App*. |
| Scope | ✅ Mandatory | **Global** = usable by jobs; **System** = only Jenkins internals (e.g. agent connections). Choose Global for job credentials. |
| Username | ⚠️ Conditionally mandatory | For username/password and SSH key kinds. |
| Password / Secret / Private Key | ⚠️ Conditionally mandatory | The secret itself. For GitHub, use a personal access token as the password. |
| ID | ⬜ Optional (strongly recommended) | Stable name used in Jenkinsfiles, e.g. `github-token`. If blank, Jenkins generates a random UUID that is hard to reference. |
| Description | ⬜ Optional | Helps identify the credential in dropdowns. |

Using a credential in a Jenkinsfile:

```groovy
withCredentials([string(credentialsId: 'api-token', variable: 'TOKEN')]) {
    sh 'curl -H "Authorization: Bearer $TOKEN" https://api.example.com/deploy'
}
```

---

## 12. Cron Schedule Cheat Sheet

Used by **Build periodically**, **Poll SCM** and the `triggers {}` block. Format:

```
MINUTE  HOUR  DAY-OF-MONTH  MONTH  DAY-OF-WEEK
(0-59)  (0-23)   (1-31)     (1-12)   (0-7, 0 and 7 = Sunday)
```

`H` means "a hashed value" chosen per job, which spreads load so all jobs don't start at the same minute. Prefer `H` over fixed numbers.

| Expression | Meaning |
|---|---|
| `H/15 * * * *` | Every 15 minutes |
| `H * * * *` | Once every hour |
| `H 2 * * *` | Once a day, sometime between 2:00 and 2:59 AM |
| `H 9 * * 1-5` | Weekdays, around 9 AM |
| `H 0 * * 0` | Weekly on Sunday, around midnight |
| `@daily` / `@weekly` / `@midnight` | Shortcut aliases |

---

## 13. Common Mistakes and Fixes

| Problem | Likely cause | Fix |
|---|---|---|
| `Couldn't find any revision to build` | Branch Specifier is `*/master` but repo uses `main` | Change Branch Specifier to `*/main`. |
| `Permission denied` / `Authentication failed` on checkout | Missing or wrong credentials | Add credentials and select them in the SCM section. |
| Multibranch shows no branches | No Jenkinsfile at Script Path, or branch filter excludes them | Check the **Scan Log**; verify Script Path and Behaviours filters. |
| Build succeeds but does nothing | Freestyle job with no build steps | Add at least one build step. |
| Job never runs automatically | No trigger configured, or webhook not set up | Add a trigger, or configure the webhook in GitHub/GitLab to `https://<jenkins>/github-webhook/`. |
| Disk fills up | Old builds and artifacts never deleted | Enable **Discard old builds** or `buildDiscarder` in the Jenkinsfile. |
| `Scripts not permitted to use method...` | Groovy sandbox blocking a method | An administrator approves it under **Manage Jenkins → In-process Script Approval**, or rewrite the step. |
| Job settings keep resetting | Jenkinsfile `options`/`parameters`/`triggers` overwrite UI settings | Make changes in the Jenkinsfile instead of the UI. |
| `Failed to connect to github.com:443 ... Could not connect to server` | The Jenkins machine can't reach GitHub at all: network drop, VPN change, firewall/antivirus, or a proxy that `git.exe` doesn't know about. Not a credentials problem. | Run `Test-NetConnection github.com -Port 443` and `git ls-remote <repo-url>` on the same machine. If a proxy is needed, set `git config --global http.proxy http://HOST:PORT` (or `HTTPS_PROXY` in Jenkins global environment variables). Set **Retry Count** to survive short blips. See [Section 14.6](#146-troubleshooting). |
| Only the first command in a Windows batch step runs | `npm` and `npx` are `.cmd` scripts; calling one without `call` ends the whole batch step | Prefix each line with `call`, e.g. `call npm ci`. |
| Jenkins builds old code after you changed files locally | The job builds from the Git repository, not your local folder | Commit and push, then rebuild. |
| HTML report archives the whole workspace | **Directory to archive** left blank in *Publish HTML reports* | Set it to the report folder, e.g. `playwright-report`. |
| Browser windows are oversized or run off-screen in a headed Jenkins run | `CI` is set in the build environment, so the test config assumes headless sizes, while `--headed` forces windows open anyway | Switch headed mode through the project's own setting (e.g. `HEADLESS=false`), not the `--headed` flag. See [Section 14.4](#144-headed-vs-headless-runs). |
| Headed browsers never appear / tests hang on Windows | Jenkins runs as a Windows service, which has no visible desktop | Run headless, or start Jenkins (or an agent) in a logged-in user session. |

---

## 14. Worked Example: Playwright Freestyle Job on Windows

A real Freestyle job that runs a JavaScript Playwright project (`Paimana_Dev`) from GitHub on a local Windows Jenkins, with visible, maximized browser windows. It applies the field rules from [Section 5](#5-freestyle-project) to a concrete case and records the problems hit along the way.

### 14.1 Setup Assumed

| Item | Value in this example |
|---|---|
| Jenkins | Started with `java -jar jenkins.war` in the logged-in user's desktop session; `JENKINS_HOME` is `C:\Users\<user>\.jenkins` |
| Repository | `https://github.com/<owner>/Paimana_Dev.git`, branch `master` |
| Project | Node 24, `@playwright/test`, scripts `test:headed` and `test:headless` in `package.json` |
| Agent tools | Node.js and Git installed and on `PATH` for the same Windows user |

> The console line `Running as SYSTEM` refers to Jenkins's internal permission identity, not the Windows account. The build process runs as whichever Windows user started Jenkins.

### 14.2 Field-by-Field Configuration

**New Item**

| Field | Value | Status | Purpose |
|---|---|---|---|
| Enter an item name | `Paimana_Dev` | ✅ Mandatory | Job name and workspace folder (`.jenkins\workspace\Paimana_Dev`). |
| Item type | Freestyle project | ✅ Mandatory | UI-configured job, no Jenkinsfile needed. |

**General**

| Field | Value | Status | Purpose |
|---|---|---|---|
| Description | `Playwright UI + API tests for PAIMANA dev` | ⬜ Optional | Tells others what the job does. |
| Discard old builds → Max # of builds to keep | `20` | ⬜ Optional (recommended) | Stops build logs and reports filling the disk. |
| Advanced → Retry Count | `3` | ⬜ Optional (recommended) | Retries the Git checkout, so a brief network blip doesn't fail the build. |

**Source Code Management**

| Field | Value | Status | Purpose |
|---|---|---|---|
| SCM | Git | ⬜ Optional (needed here) | The tests live in GitHub. |
| Repository URL | `https://github.com/<owner>/Paimana_Dev.git` | ⚠️ Conditionally mandatory | Where to fetch the code. |
| Credentials | `github-shivam` (Username with password; password = GitHub personal access token) | ⚠️ Conditionally mandatory (private repo) | Authenticates the fetch. |
| Branch Specifier | `*/master` | ⚠️ Conditionally mandatory | Must match the repo's real default branch (`master` here, not `main`). |

**Build Triggers:** none, so the job runs only on **Build Now**. Add *Poll SCM* (`H/15 * * * *`) or *GitHub hook trigger* to run on every push.

**Build Environment**

| Field | Value | Status | Purpose |
|---|---|---|---|
| Add timestamps to the Console Output | ✅ ticked | ⬜ Optional | Shows how long each step takes. |
| Terminate a build if it's stuck → Absolute, 30 minutes | ✅ ticked | ⬜ Optional | Kills a run where a browser hangs. |

**Build Steps → Execute Windows batch command**

| Field | Value | Status | Purpose |
|---|---|---|---|
| Command | see below | ⚠️ Conditionally mandatory | The actual work. |

```bat
call npm ci
call npx playwright install
call npm run test:headed
```

| Line | Why |
|---|---|
| `call npm ci` | Installs the exact versions from `package-lock.json` into a clean `node_modules`. Needed every build, because new dev dependencies (e.g. `cross-env`) arrive through Git. |
| `call npx playwright install` | Makes sure the browser builds match the installed Playwright version. Quick when they're already cached for this Windows user. |
| `call npm run test:headed` | Runs all tests with visible, maximized windows. Use `test:headless` instead when nobody watches the run. |
| `call` on every line | `npm`/`npx` are `.cmd` files; without `call`, Windows ends the batch step after the first one. |

**Post-build Actions → Publish HTML reports** (HTML Publisher plugin)

| Field | Value | Status | Purpose |
|---|---|---|---|
| HTML directory to archive | `playwright-report` | ⚠️ Conditionally mandatory | Only the report folder. Left blank, Jenkins archives the whole workspace, including `node_modules`. |
| Index page[s] | `index.html` | ⚠️ Conditionally mandatory | Entry page of the Playwright report. |
| Report title | `Playwright Report` | ⬜ Optional | Link name on the job page. |
| Keep past HTML reports | ✅ ticked | ⬜ Optional | Lets you compare runs. |
| Allow missing report | ✅ ticked | ⬜ Optional | Avoids a second error when a build fails before tests run (e.g. checkout failure). |

> The Playwright HTML report uses JavaScript, which Jenkins's default Content Security Policy blocks, so it may open blank. Download the archived folder instead, or have an administrator relax the CSP for the HTML Publisher.

### 14.3 Save and Run

1. Click **Save**, then **Build Now**.
2. Open the build → **Console Output**. A good run checks out the commit, prints `Running 7 tests using N workers`, then `7 passed`.
3. Open **Playwright Report** on the job page for screenshots and traces of any failure.

### 14.4 Headed vs Headless Runs

In this setup the build environment had `CI` set: the console showed `Running 7 tests using 1 worker`, while the same command on the desktop used 6 workers, and the project's config limits workers to 1 only when `CI` is set. The config also treated `CI` as "always headless" and gave every browser a fixed 1920×1080 page. The original build step, `npx playwright test --headed`, forced windows open anyway, so they kept the 1920×1080 size and ran off a 1920×1080 screen at 125% Windows scaling (only 1536×816 usable).

| Approach | Result |
|---|---|
| `npx playwright test --headed` | ❌ Windows open, but the config doesn't know, so they keep the fixed headless size and aren't maximized. |
| `npm run test:headed` (sets `HEADLESS=false`) | ✅ The config sees an explicit `HEADLESS=false`, which takes priority over `CI`, and maximizes every window to the real screen. |
| `npm run test:headless` (sets `HEADLESS=true`) | ✅ No windows; fixed 1920×1080 page for repeatable results. |

Two prerequisites for headed runs on a Windows Jenkins:

- Jenkins (or the agent running the job) must run in a **logged-in desktop session**. A Jenkins installed as a Windows service runs where no desktop is visible, so use `test:headless` there.
- The screen stays at whatever resolution and scaling the logged-in user has; the maximized windows adapt to it automatically.

### 14.5 Getting Local Changes into Jenkins

The job builds whatever is on GitHub, not the files on your disk. After changing the project:

```powershell
git add -A
git commit -m "Describe the change"
git push origin master
```

Then **Build Now**. The console's `Commit message:` line confirms which commit was built.

### 14.6 Troubleshooting

**Checkout fails with `Failed to connect to github.com:443 after 21136 ms: Could not connect to server`**

The machine couldn't open a connection to GitHub. Bad credentials look different (`Authentication failed` or `403`). If an earlier build with the same settings fetched fine, something about the network changed.

| Step | Command / action | What it tells you |
|---|---|---|
| 1. Test the connection | `Test-NetConnection github.com -Port 443` | `TcpTestSucceeded : True` means the network path is open. |
| 2. Test git itself | `git ls-remote https://github.com/<owner>/Paimana_Dev.git` | Lists branches if git can reach and read the repo. If both work, the failure was temporary: rebuild. |
| 3. Check GitHub | Open https://www.githubstatus.com | Rules out an outage. |
| 4. Look for a proxy | `netsh winhttp show proxy` and the *ProxyServer* value under `HKCU:\Software\Microsoft\Windows\CurrentVersion\Internet Settings` | Browsers use the Windows proxy automatically; `git.exe` doesn't. |
| 5. Tell git about the proxy | `git config --global http.proxy http://HOST:PORT` | Applies to Jenkins too when it runs under the same Windows user. Alternatively add `HTTPS_PROXY` under **Manage Jenkins → System → Global properties → Environment variables**. |
| 6. VPN / firewall | Toggle the VPN; allow `git.exe` in the firewall/antivirus | Whichever state lets step 2 succeed is the one to build in. |

> **Manage Jenkins → Plugins → Advanced → HTTP Proxy** only affects plugin downloads, not the `git` command used for checkout.

**Other symptoms seen with this job**

| Symptom | Cause | Fix |
|---|---|---|
| Firefox tests time out on `page.goto` | A third-party font never finishes loading, so Firefox's `load` event never fires | Navigate with `waitUntil: 'domcontentloaded'`; assertions still wait for their own elements. |
| `Running N tests using 1 worker` in Jenkins but more locally | `CI` is set in the build environment | Expected; it also switches on retries. Use `test:headed`/`test:headless` to choose the window mode. |
| `HEADLESS must be 'true' or 'false'` | A typo in the `HEADLESS` value | Use exactly `true` or `false`. |

---

*End of guide.*
