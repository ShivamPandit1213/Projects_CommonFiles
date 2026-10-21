| Task | TypeScript / Playwright CLI | Java / Maven Equivalent | Purpose |
| :--- | :--- | :--- | :--- |
| **Pull code** | `git pull origin main` | `git pull origin main` | Syncs local branch with GitHub remote. |
| **Install libs** | `npm install` or `npm ci` | `mvn clean compile` | Downloads project dependencies. |
| **Install browsers** | `npx playwright install` | `mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"` | Downloads browser binaries. |
| **Run all tests** | `npx playwright test` | `mvn test` | Executes all test suites. |
| **Run single test** | `npx playwright test tests/home.spec.js` | `mvn test -Dtest=LoginTest` | Runs one test file or class. |
| **Filter by tag** | `npx playwright test --grep @smoke` | `mvn test -DsuiteXmlFile=suites/smoke.xml` | Runs specific tagged test group. |
| **Force headless** | `set HEADLESS=true && npx playwright test` | `mvn test -Dheadless=true` | Runs in background at 1080p. |
| **Force headed** | `set HEADLESS=false && npx playwright test` | `mvn test -Dheadless=false` | Opens visible browser windows. |
