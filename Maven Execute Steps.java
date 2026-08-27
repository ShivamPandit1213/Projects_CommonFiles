Prerequisites
java -version
mvn -version

Both must show your target JDK (21, matching the rest of your projects). Confirm:

java -version

jobApply_Naukri/
  pom.xml              ← Maven config; likely wired to run suite/testng.xml by default
  suite/
    testng.xml         ← the default suite — probably runs UploadCV then NaukriTest, in order
    regression.xml     ← an alternate suite, if it exists
  src/main/java/...    ← page objects, driver setup, config
  src/test/java/...    ← actual @Test classes (e.g. NaukriTest, UploadCV)
  target/              ← build output, regenerated each run

Check what actually exists before running anything:
cd C:\Users\shiva\OneDrive\JavaSelenium\jobApply_Naukri
dir suite
That confirms whether regression.xml is real or hypothetical.
==================================================================================================================
Step 1 — Go to the project root (mandatory)
bash
cd C:\Users\shiva\OneDrive\JavaSelenium\jobApply_Naukri
dir

Confirm pom.xml is present. Never run from suite\ — Maven needs the pom.xml, which lives one level up.

Step 2 — Run the default suite

Since pom.xml is already configured to default to suite/testng.xml:

PowerShell:

powershell
mvn test

CMD:

cmd
mvn test

No -D flag needed — this is now the pom's built-in default.

Step 3 — Force a clean run (when results look stale or cached)
bash
mvn clean test

clean deletes target\ first, so every class recompiles and every test genuinely re-executes rather than reusing anything cached.

Step 4 — Point at a different suite file explicitly

Useful if you have more than one suite (e.g. a lighter regression.xml vs the full testng.xml).

PowerShell — quotes are required here, or PowerShell mangles the -D argument:

powershell
mvn test "-DsuiteXmlFile=suite/regression.xml"

CMD — quotes are optional but harmless:

cmd
mvn test -DsuiteXmlFile=suite/testng.xml
Step 5 — Run a single test class, bypassing the suite entirely
powershell
mvn test "-Dtest=NaukriTest"

Important caveat: -Dtest= overrides suiteXmlFiles completely. TestNG runs only that one class and ignores the suite's preserve-order="true" setting. If your suite runs UploadCV before NaukriTest because the CV needs to be uploaded first, running NaukriTest alone skips that step — the test may fail or behave differently since the prerequisite step never ran.

Use this only for quick isolated debugging of one class, not for a real end-to-end run.

Step 6 — Full debug output (when a test fails with no clear reason)
mvn test -X

Produces verbose Maven internals — plugin resolution, classpath construction, every step. Very noisy; scroll to the actual error rather than reading top to bottom.

Quick reference table
Goal	Command	Notes
Run default suite	mvn test	Uses suite/testng.xml via pom default
Force fresh run	mvn clean test	Wipes target\ first
Run a specific suite	mvn test "-DsuiteXmlFile=suite/regression.xml"	Quotes required in PowerShell
Run one class only	mvn test "-Dtest=NaukriTest"	Skips suite order — UploadCV won't run first
Debug a silent failure	mvn test -X	Verbose Maven output
List available tests	mvn test -Dtest=list (if configured) or check dir src\test\java	Confirms class names exist
Before your first run today — two checks

1. Confirm the suite file exists and has the content you expect:
type suite\testng.xml

2. Confirm no OneDrive lock issue (you've hit this before with Maven target\ deletion):
mvn clean test

If it fails with Failed to delete target, pause OneDrive sync and retry — same fix as your earlier Maven Playwright project issue.
