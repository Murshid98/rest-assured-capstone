# Rest Assured Capstone Project – GoRest API Automation

An API automation framework built with **Java, Maven, Rest Assured, TestNG, property files and Extent Reports**
to validate the user, post (product) and comment APIs of the GoRest application (`https://gorest.co.in/`).

## 1. Business goals covered

| Goal | How it is met |
|------|---------------|
| Validate critical APIs automatically | 10 automated tests for GET, POST, PUT and DELETE |
| Reduce manual API testing effort | One command runs everything: `mvn test` |
| Generate execution reports | Extent HTML report (summary, passed, failed, skipped) |
| Multiple environments | `qa.properties` / `uat.properties`, chosen with `-Denv=` |
| Reusable, maintainable framework | Utility classes, POJOs, central endpoints, base test class |

## 2. Prerequisites

* JDK 11 or higher (`java -version`)
* Maven 3.6+ (`mvn -version`)
* Internet access (to download dependencies and call GoRest)
* A free **GoRest access token** – log in at <https://gorest.co.in/consumer/login> and copy the token.
  GET calls work without it, but **POST / PUT / DELETE need it** (Bearer authentication).

## 3. How to run

```bash
# QA environment (default)
mvn clean test -Denv=qa -Dtoken=YOUR_TOKEN

# UAT environment
mvn clean test -Denv=uat -Dtoken=YOUR_TOKEN
```

The token can also be provided through the environment variable `GOREST_TOKEN`
(Windows PowerShell: `$env:GOREST_TOKEN="YOUR_TOKEN"`; Linux/Mac: `export GOREST_TOKEN=YOUR_TOKEN`),
in which case `mvn clean test -Denv=qa` is enough. Never commit a real token to the repository.

**Report location after the run:** `target/extent-reports/ExtentReport.html` (open in any browser).

## 4. Project structure

```
restassured-capstone
├── pom.xml                         Maven build (dependencies, Surefire -> testng.xml)
├── testng.xml                      Suite definition + report listener
├── README.md
├── docs/DEMO_GUIDE.md              Script for the 5-minute demo
└── src/test
    ├── resources/config
    │   ├── qa.properties           QA settings  (baseUrl, basePath, timeout, token)
    │   └── uat.properties          UAT settings
    └── java/com/perficient/restassured
        ├── base/BaseTest.java              Common set-up for all tests
        ├── config/ConfigManager.java       Property reader (env switching, token lookup)
        ├── pojo/                           User, Post, Comment (request/response models)
        ├── utils/
        │   ├── ApiClient.java              GET/POST/PUT/DELETE helper + auth + base URL
        │   ├── Endpoints.java              All API paths in one place
        │   ├── ResponseValidator.java      Status-code and response-time checks
        │   └── TestDataGenerator.java      Unique random test data
        ├── reports/
        │   ├── ExtentManager.java          Builds the HTML report
        │   ├── ExtentTestManager.java      Tracks the currently running test
        │   └── ExtentLoggingFilter.java    Logs each request/response into the report
        ├── listeners/TestListener.java     TestNG listener -> Extent report
        └── tests/
            ├── UserApiTests.java           Assignment 3 (5 tests)
            ├── ProductApiTests.java        Assignment 4 (3 tests)
            └── PostApiTests.java           Create post for user + comments on a post (2 tests)
```

## 5. Assignment checklist

| Assignment | Status | Where |
|-----------|--------|-------|
| 1. Maven project (no archetype), Rest Assured + TestNG dependencies | Done | `pom.xml` |
| 2. QA/UAT property files, Property Reader, Base URL from file | Done | `config/*.properties`, `ConfigManager` |
| 3. User API – 5 tests (status code, user details, response time) | Done | `UserApiTests` |
| 4. Product API – 3 tests (name, ID, status code) | Done | `ProductApiTests` |
| 5. Extent report (summary, passed, failed) | Done | `reports/`, `TestListener` |
| Bonus: POJO class for user creation | Done | `pojo/User`, used in `testCreateUser` |
| Authentication technique | Bearer token | `ApiClient.spec()` |
| 8–10 automated tests | 10 tests | see table below |

### The 10 test cases

| # | Test | HTTP | Endpoint |
|---|------|------|----------|
| 1 | testGetAllUsers | GET | /users |
| 2 | testGetUserById | GET | /users/{id} |
| 3 | testCreateUser | POST | /users |
| 4 | testUpdateUser | PUT | /users/{id} |
| 5 | testDeleteUser | DELETE | /users/{id} (+ GET to confirm 404) |
| 6 | testGetProductList | GET | /posts |
| 7 | testGetProductDetails | GET | /posts/{id} |
| 8 | testSearchProduct | GET | /posts?title=… |
| 9 | testCreatePostForUser | POST | /users/{id}/posts |
| 10 | testCommentOnPost | POST + GET | /posts/{id}/comments |

## 6. Design and execution flow (for the demo)

1. `mvn test` → Surefire reads **testng.xml** and starts TestNG.
2. `TestListener` creates the Extent report; `BaseTest` prints the active environment.
3. `ConfigManager` loads `config/<env>.properties` – the env comes from `-Denv`, so **no code change is needed to switch environments**.
4. A test calls `ApiClient`, which builds the request (base URL, JSON headers, Bearer token) and sends it with Rest Assured.
5. `ExtentLoggingFilter` automatically records the request, status, time and response body in the report.
6. The test validates the response with `ResponseValidator` (status code, response time) and TestNG assertions (user/product details).
7. `TestListener` marks the test passed / failed / skipped; at the end the HTML report is written.

Other design choices:
* **POJOs** (`User`, `Post`, `Comment`) are serialized/deserialized by Jackson – no hand-written JSON.
* **Test chaining**: create → update → delete use `dependsOnMethods`; if one fails the rest are *skipped*, not falsely failed.
* **Unique data** (`TestDataGenerator`) keeps tests repeatable because GoRest rejects duplicate e-mails.
* **Self-cleaning**: `PostApiTests` creates a temporary user in `@BeforeClass` and deletes it in `@AfterClass`.

## 7. Important notes

* **"Product" APIs:** GoRest has no `/products` endpoint. Its resources are users, posts and comments, so a **post is used as the "product"**
  (Product ID = post `id`, Product Name = post `title`; search = `GET /posts?title=…`). This is stated in `ProductApiTests`.
* The guide lists `/post`; the real GoRest resource is **`/posts`** (plural), which is what the framework uses.
* GoRest exposes a single public host, so `qa.properties` and `uat.properties` contain the same `baseUrl` (as in the guide).
  The framework is ready to point to different hosts simply by editing the property files.
* GoRest is a shared public server; a slow response can fail the response-time check.
  Adjust `maxResponseTimeMs` in the property files if needed.
* GoRest limits requests per minute; if you see HTTP 429, wait a minute and re-run.
