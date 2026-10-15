# Allure Report – Command Reference

Working commands for viewing, serving, and generating Allure reports for the **PaimanaLogIn_Dev_FullScreen** project on Windows.

> All commands are for the Windows Command Prompt (`cmd`).

## Table of Contents

1. [View Report via Jenkins](#1-view-report-via-jenkins)
2. [View Downloaded Report Folder via Local Server](#2-view-downloaded-report-folder-via-local-server)
3. [Generate a Standalone File (Offline HTML)](#3-generate-a-standalone-file-offline-html)
4. [Serve Raw Results on the Fly](#4-serve-raw-results-on-the-fly)
5. [Quick Reference](#quick-reference)

---

## 1. View Report via Jenkins

**Fastest option – no local server required.** Opens the fully generated, hosted Allure report in your default browser.

**Open the latest build report:**

```cmd
start http://localhost:8080/job/PaimanaLogIn_Dev_FullScreen/lastBuild/allure/
```

**Open a specific build report** (e.g. Build #12):

```cmd
start http://localhost:8080/job/PaimanaLogIn_Dev_FullScreen/12/allure/
```

> Replace `12` with the build number you want.

---

## 2. View Downloaded Report Folder via Local Server

Use these when working with a report downloaded from Jenkins to:

```
C:\Users\shiva\Downloads\allure-report\allure-report
```

> An Allure report can't be opened by double-clicking `index.html` (the browser blocks it with CORS errors), so it must be served locally — or see [Section 3](#3-generate-a-standalone-file-offline-html) for a standalone file.

### Option A: Allure Preview Server (opens browser automatically)

```cmd
cd C:\Users\shiva\Downloads\allure-report\allure-report
npx allure-commandline open .
```

> Leave the terminal window open while viewing. Press `Ctrl + C` when finished.

### Option B: Jenkins' Local Allure Binary

Uses the Allure installation that Jenkins already manages — no `npx` download needed.

```cmd
cd C:\Users\shiva\Downloads\allure-report\allure-report
"C:\Users\shiva\.jenkins\tools\org.allurereport.jenkins.tools.AllureCommandlineInstallation\allure\bin\allure.bat" open .
```

### Option C: Using `serve` (manual browser open)

```cmd
cd C:\Users\shiva\Downloads\allure-report\allure-report
npx serve .
```

> Keep the terminal open and visit **http://localhost:3000** in your browser.

---

## 3. Generate a Standalone File (Offline HTML)

Builds a single, portable `index.html` from the raw results in the Jenkins workspace. It opens with a double-click — **no local server and no CORS errors**.

```cmd
npx allure-commandline generate "C:\Users\shiva\.jenkins\workspace\PaimanaLogIn_Dev_FullScreen\allure-results" --clean --single-file -o "C:\Users\shiva\Downloads\allure-single-report"
```

Then open the report by double-clicking:

```
C:\Users\shiva\Downloads\allure-single-report\index.html
```

| Flag | Purpose |
|------|---------|
| `--clean` | Deletes the output folder's previous contents before generating |
| `--single-file` | Bundles all assets into one self-contained `index.html` |
| `-o <path>` | Output directory for the generated report |

> This file is easy to share by email or chat — the recipient only needs a browser.

---

## 4. Serve Raw Results on the Fly

Points directly to the raw `.json` test outputs (`allure-results`) rather than an already-rendered report. Allure generates a temporary report and opens it in the browser.

**From the Jenkins workspace:**

```cmd
cd C:\Users\shiva\.jenkins\workspace\PaimanaLogIn_Dev_FullScreen
npx allure-commandline serve allure-results
```

**From the local project workspace** (after running tests locally):

```cmd
cd C:\Users\shiva\Projects\PaimanaLogIn_Dev_FullScreen
npx allure-commandline serve allure-results
```

> Press `Ctrl + C` to stop the server when finished.

---

## Quick Reference

| Goal | Command | Needs server? |
|------|---------|---------------|
| Latest Jenkins report | `start http://localhost:8080/job/PaimanaLogIn_Dev_FullScreen/lastBuild/allure/` | Jenkins running |
| Specific Jenkins build | `start http://localhost:8080/job/PaimanaLogIn_Dev_FullScreen/<build#>/allure/` | Jenkins running |
| Open downloaded report | `npx allure-commandline open .` | Yes (auto) |
| Open with Jenkins' Allure | `"...\allure\bin\allure.bat" open .` | Yes (auto) |
| Serve downloaded report | `npx serve .` → http://localhost:3000 | Yes (manual) |
| Single offline HTML | `npx allure-commandline generate "<results>" --clean --single-file -o "<out>"` | No |
| Serve raw results | `npx allure-commandline serve allure-results` | Yes (auto) |

## Key Paths

| Item | Path |
|------|------|
| Jenkins job URL | `http://localhost:8080/job/PaimanaLogIn_Dev_FullScreen/` |
| Jenkins workspace | `C:\Users\shiva\.jenkins\workspace\PaimanaLogIn_Dev_FullScreen` |
| Jenkins Allure binary | `C:\Users\shiva\.jenkins\tools\org.allurereport.jenkins.tools.AllureCommandlineInstallation\allure\bin\allure.bat` |
| Local project | `C:\Users\shiva\Projects\PaimanaLogIn_Dev_FullScreen` |
| Downloaded report | `C:\Users\shiva\Downloads\allure-report\allure-report` |
| Standalone report output | `C:\Users\shiva\Downloads\allure-single-report` |

## Prerequisites

- **Node.js / npm** – required for `npx allure-commandline` and `npx serve`
- **Java** – required by the Allure command-line tool
- **Jenkins** running at `http://localhost:8080` with the Allure plugin (for Section 1)
