# Enterprise Best Practice Architecture

Cross-browser execution for the PAIMANA Selenium suite. Two approaches: the CI/CD
route for production runs, and TestNG XML parameterisation when you need a browser
matrix inside a single run.

---

## 1. Command line and CI/CD pipeline driven

Recommended for production. In enterprise setups such as Jenkins, GitHub Actions or
GitLab CI, the execution environment controls the browser through Maven arguments:

```bash
# Chrome, headless
mvn clean test -DsuiteFile=suites/smoke.xml -Dbrowser.name=chrome -Dbrowser.headless=true

# Firefox, headless
mvn clean test -DsuiteFile=suites/smoke.xml -Dbrowser.name=firefox -Dbrowser.headless=true
```

`DriverFactory.java:46` calls `PropertyReader.get("browser.name", "chrome")`, which
reads the `-Dbrowser.name` system property passed from CI and falls back to Chrome
when none is supplied.

### Why this is the production default

The browser choice lives in the pipeline definition rather than in the repository, so
the same commit runs against different browsers without a code change. It also keeps
the suite file free of environment concerns — `smoke.xml` describes *what* to run,
the command line decides *where*.

---

## 2. TestNG suite XML parameterisation

Use this when the same suite must execute across Chrome and Firefox concurrently
within one run.

**`suites/cross-browser.xml`**

```xml
<suite name="Enterprise-CrossBrowser" parallel="tests" thread-count="2">

  <test name="Chrome-Tests">
    <parameter name="browser" value="chrome"/>
    <classes>
      <class name="com.paimana.tests.smoke.LoginTest"/>
    </classes>
  </test>

  <test name="Firefox-Tests">
    <parameter name="browser" value="firefox"/>
    <classes>
      <class name="com.paimana.tests.smoke.LoginTest"/>
    </classes>
  </test>

</suite>
```

`parallel="tests"` runs each `<test>` block on its own thread, so both browsers
execute at the same time. `thread-count="2"` matches the number of blocks.

**`BaseTest.java:82`** — accept the parameter, falling back to the system property:

```java
@Parameters({"browser"})
@BeforeMethod(alwaysRun = true)
public void startBrowser(@Optional String browserName, Method method) {
    String targetBrowser = (browserName != null && !browserName.isEmpty())
        ? browserName
        : PropertyReader.get("browser.name", "chrome");

    DriverManager.set(DriverFactory.create(targetBrowser));
}
```

`@Optional` matters here. Without it, running any suite that does not declare a
`browser` parameter — `smoke.xml`, for example — fails at setup rather than falling
through to the system property.

---

## Choosing between them

| | CI/CD driven | Suite XML |
|---|---|---|
| Browser chosen by | Pipeline definition | Suite file, committed to the repo |
| Browsers per run | One | Several, in parallel |
| Report | One result set per pipeline job | One combined result set |
| Best for | Scheduled and per-commit runs | Pre-release cross-browser checks |

The two are complementary. Most teams run a single browser per pipeline job for
speed on every commit, then run `cross-browser.xml` on a nightly or pre-release
schedule.

---

## Notes

- `DriverManager` holds one driver per thread, so no Java change is needed to run
  browsers in parallel.
- Add `parallel="methods" thread-count="N"` to the `<suite>` tag to parallelise
  within a browser as well. Note that TestNG's `priority` ordering becomes
  unreliable under parallel execution — use `dependsOnMethods` where order matters.
- On CI agents, always pass `-Dbrowser.headless=true`. A headed browser on a
  headless agent hangs until the pipeline timeout rather than failing fast.
