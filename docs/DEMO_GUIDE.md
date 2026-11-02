# 5-Minute Demo Guide

| Time | What to show / say |
|------|--------------------|
| 0:00 – 0:30 | **Goal**: automate GoRest user, post and comment APIs with Rest Assured. Tools: Java, Maven, TestNG, property files, Extent Reports. |
| 0:30 – 1:15 | **Project structure** (README section 4): config, pojo, utils, reports, listeners, tests. Mention `pom.xml` (no archetype) and `testng.xml`. |
| 1:15 – 2:00 | **Environment management**: open `qa.properties` / `uat.properties`; show `ConfigManager`. Explain `-Denv=qa` vs `-Denv=uat` – no code change. |
| 2:00 – 3:00 | **Framework design**: `ApiClient` (base URL, Bearer token, JSON headers), `Endpoints`, `ResponseValidator`, `TestDataGenerator`, POJO `User`. |
| 3:00 – 4:00 | **Run live**: `mvn clean test -Denv=qa -Dtoken=...` – point out GET, POST, PUT, DELETE tests and the create → update → delete chain. |
| 4:00 – 4:45 | **Report**: open `target/extent-reports/ExtentReport.html` – summary dashboard, passed/failed tests, request/response logs. |
| 4:45 – 5:00 | **Wrap-up**: 10 tests, reusable utilities, POJO bonus, note that "product" = GoRest post. |
