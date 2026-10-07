# Maven Execute Steps — jobApply_Naukri

How to run the `jobApply_Naukri` Selenium + TestNG project with Maven: checks before the
first run, the run commands in order of use, where results end up, and fixes for common
failures.

| | |
|---|---|
| **Project folder** | `C:\Users\shiva\OneDrive\JavaSelenium\jobApply_Naukri` |
| **Build tool** | Maven (Surefire plugin runs TestNG) |
| **JDK** | 21, matching the other projects and Jenkins |
| **Default suite** | `suite/testng.xml` (UploadCV, then NaukriTest) |
| **Shells** | Commands are shown for `cmd`. Where PowerShell differs, both are shown. |

**Comments in code blocks:** `cmd` comments start with `::`, PowerShell comments with `#`.
Both are skipped by the shell, so you can paste whole blocks.

---

## Contents

- [Prerequisites](#prerequisites)
- [Project Layout](#project-layout)
- [Step 1 — Go to the project root](#step-1--go-to-the-project-root)
- [Step 2 — Run the default suite](#step-2--run-the-default-suite)
- [Step 3 — Force a clean run](#step-3--force-a-clean-run)
- [Step 4 — Run a different suite file](#step-4--run-a-different-suite-file)
- [Step 5 — Run one class or one method](#step-5--run-one-class-or-one-method)
- [Step 6 — Debug a failure](#step-6--debug-a-failure)
- [Step 7 — Read the results](#step-7--read-the-results)
- [Quick Reference](#quick-reference)
- [pom.xml Settings That Make This Work](#pomxml-settings-that-make-this-work)
- [Running from Jenkins](#running-from-jenkins)
- [Troubleshooting](#troubleshooting)

---

## Prerequisites

Check once per machine, and again after installing a new JDK or Maven.

```cmd
:: Java used on the command line: must report version 21
java -version

:: Maven version, and the Java Maven itself uses ("Java version: 21...")
mvn -version

:: The JDK folder Maven reads; must point at JDK 21
echo %JAVA_HOME%

:: Every java.exe on PATH; the first one listed is the one that runs
where java
```

| Check | Expected | If not |
|---|---|---|
| `java -version` | `java version "21..."` | Install JDK 21, or put its `bin` folder first on `PATH`. |
| `mvn -version` | `Apache Maven 3.9.x` and `Java version: 21...` | Maven uses `JAVA_HOME`, not `PATH`. Set `JAVA_HOME` to `C:\Program Files\Java\jdk-21.0.12`. |
| `echo %JAVA_HOME%` | `C:\Program Files\Java\jdk-21.0.12` | System Properties → Environment Variables → set `JAVA_HOME`, then open a **new** terminal. |

`java -version` and `mvn -version` can disagree: `java` comes from `PATH`, Maven from
`JAVA_HOME`. Both must show 21.

[↑ Contents](#contents)

---

## Project Layout

Typical layout of this project. Confirm what really exists before running (see the check
below the tree).

```text
jobApply_Naukri/
├── pom.xml               Maven config; runs suite/testng.xml by default
├── suite/
│   ├── testng.xml        default suite: UploadCV, then NaukriTest, in order
│   └── regression.xml    alternate suite (only if it exists)
├── src/main/java/...     page objects, driver setup, config
├── src/test/java/...     @Test classes (e.g. NaukriTest, UploadCV)
└── target/               build output and reports, recreated on each run
```

```cmd
:: Go to the project root
cd C:\Users\shiva\OneDrive\JavaSelenium\jobApply_Naukri

:: Which suite files really exist? (is regression.xml there?)
dir suite

:: Every test class, with its full path
dir /s /b src\test\java\*.java

:: Show the default suite's content: which classes, in which order
type suite\testng.xml
```

[↑ Contents](#contents)

---

## Step 1 — Go to the project root

Mandatory. Maven needs `pom.xml`, which lives in the project root, not in `suite\` or `src\`.

```cmd
:: Enter the project root
cd C:\Users\shiva\OneDrive\JavaSelenium\jobApply_Naukri

:: VERIFY: pom.xml must be listed
dir pom.xml
```

Running from anywhere else fails with
`The goal you specified requires a project to execute but there is no POM in this directory`.

[↑ Contents](#contents)

---

## Step 2 — Run the default suite

`pom.xml` points Surefire at `suite/testng.xml` (see [pom.xml settings](#pomxml-settings-that-make-this-work)),
so no extra option is needed. Same command in `cmd` and PowerShell.

```cmd
:: Compile, then run suite/testng.xml: UploadCV first, then NaukriTest
mvn test
```

**What happens:** Maven compiles `src/main/java` and `src/test/java` into `target\`, then
Surefire starts TestNG with the suite file. The run ends with `BUILD SUCCESS` (all tests
passed) or `BUILD FAILURE` (a test failed or the build broke).

[↑ Contents](#contents)

---

## Step 3 — Force a clean run

Use when results look stale, after changing `pom.xml`, after renaming or deleting classes,
or after switching branches.

```cmd
:: Delete target\ first, then recompile everything and run the default suite
mvn clean test
```

`clean` deletes `target\`, so every class is recompiled and old reports are removed. A
plain `mvn test` only recompiles changed files, and a deleted class can linger as an old
`.class` file in `target\`.

[↑ Contents](#contents)

---

## Step 4 — Run a different suite file

Useful with more than one suite, e.g. a lighter `regression.xml` next to the full
`testng.xml`. Works only if `pom.xml` reads the `suiteXmlFile` property (see
[pom.xml settings](#pomxml-settings-that-make-this-work)).

**cmd** — quotes optional:

```cmd
:: Run the regression suite instead of the default
mvn test -DsuiteXmlFile=suite/regression.xml

:: Same, with a clean build first
mvn clean test -DsuiteXmlFile=suite/regression.xml
```

**PowerShell** — quotes **required**:

```powershell
# Without quotes PowerShell splits the argument at the dot, and Maven receives
# "-DsuiteXmlFile=suite/regression" plus a separate ".xml" (an unknown goal)
mvn test "-DsuiteXmlFile=suite/regression.xml"
```

**Rule of thumb:** in PowerShell, always wrap every `-D...` option in double quotes.

[↑ Contents](#contents)

---

## Step 5 — Run one class or one method

Quick, isolated debugging of one class. Not for real end-to-end runs.

**cmd**

```cmd
:: One class: ignores the suite file completely
mvn test -Dtest=NaukriTest

:: One method in that class
mvn test -Dtest=NaukriTest#applyJobs

:: Several classes (comma-separated, no spaces)
mvn test -Dtest=UploadCV,NaukriTest
```

**PowerShell**

```powershell
# Quote the whole option: # and , have special meanings in PowerShell
mvn test "-Dtest=NaukriTest"
mvn test "-Dtest=NaukriTest#applyJobs"
mvn test "-Dtest=UploadCV,NaukriTest"
```

`applyJobs` is an example name; use a real method from the class.

> **Caveat — the suite is bypassed.** `-Dtest=` overrides the suite file entirely. TestNG
> runs only the listed classes and ignores the suite's `preserve-order="true"`, parameters
> and listeners. If NaukriTest depends on UploadCV having uploaded the CV first, running
> NaukriTest alone skips that step, and the test may fail or behave differently. Even
> `-Dtest=UploadCV,NaukriTest` doesn't guarantee that order. For an ordered run, use the
> suite (Step 2 or Step 4).

If a class name matches nothing, Surefire stops with
`No tests matching pattern "..." were executed`. Check the exact class name with
`dir /s /b src\test\java\*.java`.

[↑ Contents](#contents)

---

## Step 6 — Debug a failure

Try these in order; each shows more than the last.

```cmd
:: 1. Full Java stack trace of the error, without other noise
mvn test -e

:: 2. Everything Maven does: plugin resolution, classpath, each step (very long)
mvn test -X

:: 3. Save the -X output to a file instead of scrolling the console
mvn test -X > maven-debug.log 2>&1

:: 4. Search that file for the first real error
findstr /n /i "ERROR Exception" maven-debug.log
```

With `-X`, don't read top to bottom: search for the first `ERROR` or `Exception`. The
lines just above it usually name the cause.

Also check the TestNG output in `target\surefire-reports\` (Step 7): the failing test's
exception and stack trace are there, even when the console summary is short.

[↑ Contents](#contents)

---

## Step 7 — Read the results

After every run, Surefire writes reports to `target\surefire-reports\`.

| File | What it shows |
|---|---|
| `index.html` | TestNG's HTML report: suites, classes, passed/failed/skipped tests, exceptions. |
| `emailable-report.html` | One-page summary, easy to share. |
| `testng-results.xml` | Machine-readable results (Jenkins can publish it). |
| `<Suite name>\*.xml` / `*.txt` | Per-class results and stack traces. |

```cmd
:: Open the TestNG HTML report in the default browser
start target\surefire-reports\index.html

:: Open the one-page summary
start target\surefire-reports\emailable-report.html
```

`mvn clean` deletes these reports. Copy them out first if you need to keep a run's results.

[↑ Contents](#contents)

---

## Quick Reference

| Goal | `cmd` | PowerShell | Notes |
|---|---|---|---|
| Run the default suite | `mvn test` | `mvn test` | `suite/testng.xml` via the pom default |
| Fresh run | `mvn clean test` | `mvn clean test` | Deletes `target\` first |
| Specific suite | `mvn test -DsuiteXmlFile=suite/regression.xml` | `mvn test "-DsuiteXmlFile=suite/regression.xml"` | Quotes required in PowerShell |
| One class | `mvn test -Dtest=NaukriTest` | `mvn test "-Dtest=NaukriTest"` | Bypasses suite order: UploadCV won't run first |
| One method | `mvn test -Dtest=NaukriTest#method` | `mvn test "-Dtest=NaukriTest#method"` | Same caveat |
| Error stack trace | `mvn test -e` | `mvn test -e` | Try before `-X` |
| Full debug output | `mvn test -X` | `mvn test -X` | Very verbose |
| Compile only, no tests | `mvn test-compile` | `mvn test-compile` | Fast check that the code compiles |
| Skip tests | `mvn install -DskipTests` | `mvn install "-DskipTests"` | Build without running tests |
| Offline (no downloads) | `mvn test -o` | `mvn test -o` | When the network is down and dependencies are cached |
| Re-download dependencies | `mvn clean test -U` | `mvn clean test -U` | When a dependency seems corrupt or outdated |
| List test classes | `dir /s /b src\test\java\*.java` | `Get-ChildItem -Recurse src\test\java -Filter *.java` | Confirms exact class names |
| Show dependency tree | `mvn dependency:tree` | `mvn dependency:tree` | Finds version conflicts (e.g. two Selenium versions) |

[↑ Contents](#contents)

---

## pom.xml Settings That Make This Work

Steps 2 and 4 rely on this configuration. Check your `pom.xml` contains the equivalent.

```xml
<properties>
  <!-- JDK 21 for compiling -->
  <maven.compiler.release>21</maven.compiler.release>
  <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  <!-- Default suite; overridden on the command line with -DsuiteXmlFile=... -->
  <suiteXmlFile>suite/testng.xml</suiteXmlFile>
</properties>

<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-surefire-plugin</artifactId>
      <version>3.5.2</version>
      <configuration>
        <suiteXmlFiles>
          <!-- Reads the property above, so the command line can replace it -->
          <suiteXmlFile>${suiteXmlFile}</suiteXmlFile>
        </suiteXmlFiles>
      </configuration>
    </plugin>
  </plugins>
</build>
```

| Setting | Why |
|---|---|
| `<suiteXmlFile>suite/testng.xml</suiteXmlFile>` in `<properties>` | The default when no `-DsuiteXmlFile` is given, so a plain `mvn test` runs the full suite. |
| `<suiteXmlFile>${suiteXmlFile}</suiteXmlFile>` in Surefire | Makes the suite replaceable from the command line. With a hard-coded path here instead, `-DsuiteXmlFile=...` has **no effect**. |
| `maven.compiler.release` `21` | Compiles for Java 21. |
| Surefire version | Use a recent 3.x; versions are examples, keep whatever your pom already pins if it works. |

The order of tests in a suite comes from `testng.xml`:

```xml
<suite name="Naukri Suite">
  <!-- preserve-order="true" runs classes in the order listed below -->
  <test name="Apply Flow" preserve-order="true">
    <classes>
      <class name="tests.UploadCV"/>
      <class name="tests.NaukriTest"/>
    </classes>
  </test>
</suite>
```

Replace `tests.` with your real package name (the folder path under `src\test\java`).

[↑ Contents](#contents)

---

## Running from Jenkins

In a Freestyle job, use **Build Steps → Invoke top-level Maven targets**:

| Field | Value | Why |
|---|---|---|
| Maven Version | the Maven installation configured under **Manage Jenkins → Tools** | Which Maven runs the goals |
| Goals | `clean test -DsuiteXmlFile=suite/testng.xml` | Always a fresh, ordered suite run |
| Advanced → POM | `pom.xml` | Default; change only if the pom isn't at the repo root |

Jenkins runs the goals in its own workspace, checked out from GitHub, so push changes
before building. To show results on the job page, add **Post-build Actions → Publish
TestNG Results** (TestNG Results plugin) with report pattern
`**/target/surefire-reports/testng-results.xml`, or **Publish JUnit test result report**
with `**/target/surefire-reports/*.xml`.

Full Jenkins job setup: `JenkinsJobTypesSetup.md`.

[↑ Contents](#contents)

---

## Troubleshooting

**Read the console from the top and fix the first `[ERROR]`.** Later errors are usually
caused by the first one.

| Symptom | Cause | Fix |
|---|---|---|
| `there is no POM in this directory` | Not in the project root | `cd` to the folder containing `pom.xml` (Step 1). |
| `Failed to delete ...\target` | OneDrive syncing `target\`, an IDE, Explorer window or a leftover browser driver holds a file | Pause OneDrive sync, close Explorer windows on `target\`, end stray drivers (below), retry. Long-term: move the project out of OneDrive. |
| `release version 21 not supported` / `invalid target release: 21` | Maven runs on an older JDK | `mvn -version` must show Java 21; fix `JAVA_HOME` and open a new terminal. |
| `Unknown lifecycle phase ".xml"` (PowerShell) | Unquoted `-D` option split at the dot | Quote it: `"-DsuiteXmlFile=suite/regression.xml"`. |
| `-DsuiteXmlFile=...` seems ignored; the default suite runs | Surefire has a hard-coded path, not `${suiteXmlFile}` | Fix `pom.xml` as in [pom.xml settings](#pomxml-settings-that-make-this-work). |
| `No tests matching pattern "..." were executed` | Wrong class name or wrong case in `-Dtest=` | `dir /s /b src\test\java\*.java` for exact names. |
| `Tests run: 0` | Suite file path wrong, classes not in `src/test/java`, or package names in `testng.xml` don't match | `type suite\testng.xml` and compare `<class name=...>` with the real package and class names. |
| NaukriTest fails when run alone, passes in the suite | It depends on UploadCV running first | Run the suite, not `-Dtest=` (Step 5 caveat). |
| `Could not resolve dependencies` | No network, proxy, or corrupt local repository | Check the network; `mvn clean test -U`; as a last resort delete the broken folder under `%USERPROFILE%\.m2\repository`. |
| `SessionNotCreatedException` / browser doesn't start | Browser and driver versions don't match, or an old Selenium version without Selenium Manager | Update Selenium to 4.x recent (it downloads the right driver itself); update the browser. |
| Browsers left open after a failed run | The test failed before `driver.quit()` | Close them, or end the processes (below). Put `driver.quit()` in an `@AfterMethod`/`@AfterClass` with `alwaysRun = true`. |

**Ending leftover browser/driver processes:**

```cmd
:: Force-close leftover ChromeDriver processes (ignore "not found" messages)
taskkill /f /im chromedriver.exe

:: Same for Edge and Firefox drivers, if those are used
taskkill /f /im msedgedriver.exe
taskkill /f /im geckodriver.exe
```

### Before your first run of the day

```cmd
:: 1. Go to the project root
cd C:\Users\shiva\OneDrive\JavaSelenium\jobApply_Naukri

:: 2. Confirm the suite file exists and lists the classes you expect
type suite\testng.xml

:: 3. Fresh run: also proves OneDrive isn't locking target\
::    If it fails with "Failed to delete target", pause OneDrive sync and retry
mvn clean test

:: 4. Open the results
start target\surefire-reports\index.html
```

[↑ Contents](#contents)
