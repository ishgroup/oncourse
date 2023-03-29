# Selenium tests

This type of testing is used to test onCourse by simulating the usage of service by real users. Testing is carried out
on a running server through interactions with GUI by executing special scripts that imitate users.

Used frameworks:

- https://github.com/SeleniumHQ/selenium
- https://github.com/bonigarcia/webdrivermanager

## How to run

To run tests:

1. Build server by command `./gradlew -x test clean server:build`;
2. Configure attribute `databaseUrl` and `dbUrl` in [build.gradle](build.gradle);
3. Run `./gradlew selenium-test:test`.

### Command description

1. Tests execute on a running server, so you must build it yourself before you start execute tests if you haven't
   already done it. Please pay attention, onCourse starts with an already builded client. Therefore, if you make changes
   on the client side, you need to rebuild the server for changes to take effect.
2. Attribute `dbUrl` used in [onCourse.yml](src/test/resources/onCourse.yml). Attribute `databaseUrl` used
   if you run `./gradlew selenium-test:prepareTestDatabase` separate, in other cases `databaseUrl` = `dbUrl`.
3. Command `./gradlew selenium-test:test` contains 3 commands:
    1. `./gradlew selenium-test:copyConfig`. Copy test [onCourse.yml](src/test/resources/onCourse.yml)
       to the server build. If build already contained it, [onCourse.yml](src/test/resources/onCourse.yml) won't be
       copied;
    2. Run server in default mode;
    3. `./gradlew selenium-test:prepareTestDatabase`. Load datasets in databse.

### Server configuration

Use the standard [onCourse.yml](src/test/resources/onCourse.yml) setup mechanism. For example, to set up SMTP,
just add configuration to file just like you did before.

## How create new test

You can create new test manually or use browser extension https://www.selenium.dev/selenium-ide/.

Extension Overview:

The new Selenium IDE is designed to record your interactions with websites to help you generate and maintain site
automation, tests, and remove the need to manually step through repetitive takes. The IDE gives developers the ability
to export tests to Java, so you can record a test and just export it in Java without wasting time on developing.

Of course, you can create tests on your own. Learn more: https://www.selenium.dev/documentation/webdriver/

### Where are tests stored?

Path: selenium-test/src/test/java/ish/oncourse/selenium/test.

- create or use package according to what you are testing;
- selenium-test/src/test/java/ish/oncourse/selenium/test/AbstractSeleniumTest.java contains all the environment settings
  for the correct test starting and ending. All tests that used selenium should extend this abstract class.

### Where are datasets for tests stored?

Path: selenium-test/src/test/resources/ish.oncourse.selenium.test.

All datasets stored in folder or subfolders will be load to database by
task `./gradlew selenium-test:prepareTestDatabase`.
For 1 test should be 1 dataset that contains all needed data.

Datasets load in alphabetic order (it is work as for folders as for files), but folder `common` is loaded first.
Subfolders can also have a `common` folder, which will be loaded first. The folder `common` contains datasets
with shared data that can be used in several tests.

#### More usefull info datasets

As for api-test you can use constructions `#future_year` and `#previous_year`.

## Test browser configuration

During testing, the browser is controlled by a special webDriver. For each version and type of browser this driver is
special.
That's why, we use WebDriverManager that automatically detect and configure it.

WebDriver has many option. You can reconfigure it on
[WebDriverFactory.java](src/main/java/ish/oncourse/util/selenium/model/WebDriverFactory.java)

- `--headless` — browser graphic mode. GitHub Actions start only in this mode;
- `--disable-gpu` — execution optimization fiture;
- `--ignore-certificate-errors` — need to ingore dev SSL certificate;
- `--disable-extensions` — execution optimization fiture;
- `--no-sandbox` — linux only required option;
- `--disable-dev-shm-usage` — execution optimization fiture.

It is a default list of requred options. You can remove them during the local testing, but for the GitHub Actions,
they should be set. There are 2 browser modes that you should use:

- --headless - is a feature which allows the execution of a full version of the Chrome Browser and provides
  the ability to control Chrome via external programs without the Graphical User Interface (GUI). The headless mode can
  run on servers without the need for dedicated display or graphics.
- --headed - the same as headless but with Graphical User Interface (GUI).

Configure webDriver without setting mode != `--headed` mode. So, you must use one of it!

## Debugging and Error Report

You can use `--headed` mode browser mode and you dev IDE power to debug tests. The prblem is, we haven't got them in GitHub.
That why, you should use it test extensions:

- [PrintBrowserConsole.java](src/test/java/ish/oncourse/selenium/extension/PrintBrowserConsole.java) — prints 
all browser console logs. Executed after the test has completed and before JUnit sets the test as failed;
- [PrintPageNetwork.java](src/test/java/ish/oncourse/selenium/extension/PrintPageNetwork.java) — logs and prints all 
requests and responses made during the test. Executed after the test has completed and before JUnit sets the test as failed;
- [PrintPageScreenshot.java](src/test/java/ish/oncourse/selenium/extension/PrintPageScreenshot.java) — takes a screenshot 
of the tested page, converts it to Base64, and prints it as logs. You can use any Base64 -> PNG converter to get a screenshot. 
Executed after the test has completed and before JUnit sets the test as failed;

This 3 extensions using [ConfigurationResolver.java](src/main/java/ish/oncourse/util/selenium/service/extension/ConfigurationResolver.java) —
controls the creation and usage of webDriver for each test.

To use extensions you should annotate your test by `@ExtendWith`. Example:

`
@ExtendWith({ConfigurationResolver.class, PrintBrowserConsole.class, PrintPageScreenshot.class})
`

## Useful notes

You can face to situations where a script successfully executes in Selenium IDE but doesn't work in the exported Java
test.
In most cases, this problem is due to the fact that UI doesn't have time to fully load. In this case, you need
to manually add pauses of execution or waiting conditions for the element.

If you need execution pauses use [SeleniumUtil.java](src/main/java/ish/oncourse/util/selenium/util/SeleniumUtil.java).
If waiting conditions cases, use `ExpectedConditions.java`, but the best practice is combine them.

## Ideas for future

Implement testing also in Firefox, Safari, and Opera. They are the most popular browser. 
Firefox web driver configuration has many common with chrome, but with others can be a lot of differences.

Current problem:
it will require a lot of resources, and the execution time will also increase if the tests contained are performed 
sequentially. It is necessary to write a system for parallelizing tests.
