PART 1 — PLAYWRIGHT TYPESCRIPT PROJECT
Prerequisites
Node.js v18+        node.js.org — install, then reopen CMD
npm                 comes with Node
A code editor       VS Code recommended

Verify:

node -v
npm -v
npx -v
Step-by-step (existing project)

1. Go to the project folder — mandatory first step

cd C:\Users\shiva\OneDrive\JavaSelenium\PAIMANA_Playwright_1.1
dir

Confirm package.json and playwright.config.ts are present.

2. Install dependencies

npm install

3. Install browsers

npx playwright install

4. Verify

npx playwright --version
npx playwright test --list

5. Run

npx playwright test

6. View report

npx playwright show-report
Step-by-step (brand new project)
mkdir my-playwright-project
cd my-playwright-project
npm init playwright@latest

Answer: TypeScript → tests folder name → GitHub Actions (yes/no) → install browsers (yes)

npx playwright test

==========================================================================================================================================

PART 2 — PLAYWRIGHT JAVA (MAVEN) PROJECT
Prerequisites
JDK 17 or 21          adoptium.net — Playwright Java needs 8+, but match your other tools (17/21)
Maven                 maven.apache.org, or bundled with your IDE
Eclipse or IntelliJ    optional, CLI works standalone

Verify:

java -version
mvn -version

Both must report the same JDK. If mvn -version shows a different Java version than java -version, set JAVA_HOME:

setx JAVA_HOME "C:\Program Files\Java\jdk-21.0.12"

Close and reopen CMD after this.

Step-by-step (existing project)

1. Go to the project folder

cd C:\Users\shiva\OneDrive\JavaSelenium\paimana-playwright-java
dir

Confirm pom.xml is present.

2. Download dependencies and compile

mvn clean compile

3. Install Playwright's browsers — Java projects need this as a separate step, since npm isn't involved:

mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"

(If exec-maven-plugin isn't in your pom.xml, use the direct jar approach instead:)

mvn dependency:build-classpath -Dmdep.outputFile=cp.txt
java -cp "target/classes;%cp.txt%" com.microsoft.playwright.CLI install

4. Run tests

mvn test

5. Run a specific test class

mvn test -Dtest=LoginTest

6. Run with a TestNG suite file (if configured)

mvn test -DsuiteXmlFile=suites/smoke.xml

7. View results

Reports land in: target/surefire-reports/
Open: target/surefire-reports/index.html   (if HTML report configured)
Step-by-step (brand new project)

1. Create via archetype

mvn archetype:generate -DgroupId=com.paimana -DartifactId=paimana-playwright-java -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
cd paimana-playwright-java

2. Add to pom.xml inside <dependencies>:

xml
<dependency>
  <groupId>com.microsoft.playwright</groupId>
  <artifactId>playwright</artifactId>
  <version>1.48.0</version>
</dependency>
<dependency>
  <groupId>org.testng</groupId>
  <artifactId>testng</artifactId>
  <version>7.10.2</version>
  <scope>test</scope>
</dependency>

3. Install and compile

mvn clean compile

4. Install browsers (same as step 3 above)

5. Write a test, then:

mvn test
Quick comparison — which command does what
TypeScript                          Java/Maven equivalent
npm install                         mvn clean compile
npx playwright install              mvn ...CLI install  (above)
npx playwright test                 mvn test
npx playwright test --project=x     mvn test -Dbrowser=x  (if you wired this yourself)
npx playwright test --grep @smoke   mvn test -DsuiteXmlFile=suites/smoke.xml
npx playwright show-report          open target/surefire-reports manually
Common errors — both projects
EPERM ... Temp\WinSAT
    Wrong folder. cd into the project first.

Cannot find module '@playwright/test'
    npm install

Executable doesn't exist at ms-playwright\...
    npx playwright install

package does not exist / cannot find symbol (Java)
    Playwright dependency missing from pom.xml, or mvn clean compile not run

BUILD FAILURE ... Failed to delete target
    OneDrive lock. Pause sync or move project out of OneDrive.

'mvn' or 'npx' not recognized
    Not installed, or terminal not reopened after install
