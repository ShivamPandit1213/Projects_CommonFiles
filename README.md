Jenkins support: Jenkins supports 17 and 21 only
Appium support: any latest version, but for Jenkins compatibility use JDK21

Jenkins    17, 21     ← the binding constraint
Appium     11+
Selenium   11+
TestNG     11+

# Automation CLI Reference

Command reference for Playwright (Node) and Cucumber (JVM + JS).

## Table of Contents

- [Playwright — Core Commands](#playwright--core-commands)
- [Playwright — Filtering Tests](#playwright--filtering-tests)
- [Playwright — Debugging and Execution](#playwright--debugging-and-execution)
- [Playwright — Trace Viewer](#playwright--trace-viewer)
- [Cucumber-JVM (Maven + TestNG)](#cucumber-jvm-maven--testng)
- [Cucumber-JS (Node)](#cucumber-js-node)
- [Everyday Commands](#everyday-commands)

## Playwright — Core Commands

| Command | Purpose |
|---|---|
| `npx playwright test` | Run all tests in the project |
| `npx playwright test <file>` | Run one spec file, e.g. `tests/login.spec.ts` |
| `npx playwright test <file>:<line>` | Run the single test at that line number |
| `npx playwright show-report` | Open the HTML report from the last run |
| `npx playwright codegen <url>` | Record browser actions and generate test code |
| `npx playwright install` | Download the browser binaries |
| `npx playwright install chromium` | Download one browser only |
| `npx playwright install --with-deps` | Install browsers plus OS-level dependencies (Linux/CI) |
| `npx playwright install-deps` | Install only the OS dependencies |
| `npm init playwright@latest` | Scaffold a new Playwright project |
| `npx playwright --version` | Print the installed version |

## Playwright — Filtering Tests

| Command | Purpose |
|---|---|
| `--grep <pattern>`, `-g` | Run only tests whose title matches, e.g. `-g @smoke` |
| `--grep-invert <pattern>` | Run everything except matches |
| `--project=<name>` | Run one project or browser, e.g. `--project=chromium` |
| `--only-changed` | Run only tests affected by uncommitted git changes |
| `--last-failed` | Re-run only the tests that failed last time |
| `--shard=<n>/<total>` | Split the suite across machines, e.g. `--shard=1/4` |

## Playwright — Debugging and Execution

| Command | Purpose |
|---|---|
| `--ui` | Open interactive UI mode with time-travel and DOM snapshots |
| `--headed` | Show the browser window instead of running headless |
| `--debug` | Launch the Playwright Inspector and step through |
| `--trace=on` | Record a trace for every test (`on-first-retry` is the usual CI setting) |
| `--workers=<n>` | Set parallel worker count; `--workers=1` forces serial |
| `--repeat-each=<n>` | Run each test N times, useful for hunting flaky tests |
| `--retries=<n>` | Retry failed tests N times |
| `--max-failures=<n>`, `-x` | Stop after N failures (`-x` stops at the first) |
| `--timeout=<ms>` | Override the per-test timeout |
| `--reporter=<name>` | Choose reporter: `list`, `line`, `dot`, `html`, `json`, `junit` |
| `--update-snapshots`, `-u` | Regenerate visual and snapshot baselines |
| `--list` | List matching tests without running them |
| `--config=<file>`, `-c` | Use a specific config file |

## Playwright — Trace Viewer

| Command | Purpose |
|---|---|
| `npx playwright show-trace <file.zip>` | Open a recorded trace |
| `npx playwright show-trace` | Open the trace viewer and drop a file in |
| `npx playwright open <url>` | Open a page in a Playwright-controlled browser |

## Cucumber-JVM (Maven + TestNG)

Cucumber-JVM has no CLI of its own in a Maven project. Drive it through Maven
and system properties.

| Command | Purpose |
|---|---|
| `mvn test` | Run all features via your runner class |
| `mvn test -Dcucumber.filter.tags="@smoke"` | Run scenarios with one tag |
| `mvn test -Dcucumber.filter.tags="@smoke and not @wip"` | Combine tags with `and`, `or`, `not` |
| `mvn test -Dcucumber.filter.name="login"` | Filter by scenario name |
| `mvn test -Dcucumber.features=<path>` | Run one feature file |
| `mvn test -Dcucumber.plugin="pretty,html:target/report.html"` | Set reporters |
| `mvn test -Dcucumber.glue=<package>` | Point at the step-definition package |
| `mvn test -Dcucumber.execution.dry-run=true` | Check step bindings without executing |
| `mvn test -Dcucumber.publish.quiet=true` | Suppress the publish-report banner |
| `mvn test -Dtest=<RunnerClass>` | Run a specific TestNG runner class |

The dry run is the fastest way to find missing step definitions. It prints
snippets for anything unmatched.

## Cucumber-JS (Node)

| Command | Purpose |
|---|---|
| `npx cucumber-js` | Run all features |
| `npx cucumber-js <file>` | Run one feature file |
| `npx cucumber-js --tags "@smoke"` | Filter by tag |
| `npx cucumber-js --tags "@smoke and not @wip"` | Combined tag expression |
| `npx cucumber-js --name "login"` | Filter by scenario name |
| `npx cucumber-js --dry-run` | Validate step bindings without running |
| `npx cucumber-js --format html:report.html` | Choose output format |
| `npx cucumber-js --parallel <n>` | Run N scenarios concurrently |
| `npx cucumber-js --retry <n>` | Retry failing scenarios |
| `npx cucumber-js --fail-fast` | Stop on first failure |
| `npx cucumber-js --require <path>` | Load support and step files |

## Everyday Commands

```bash
# Writing tests: fast loop, one browser, interactive
npx playwright test --project=chromium --ui

# Quick tagged check on the Java suite
mvn test -Dcucumber.filter.tags="@smoke"

# When a step mysteriously does not fire
mvn test -Dcucumber.execution.dry-run=true
```
