# demo-webapp

## Prerequisites
- Java 17
- Maven 3.8+
- (Optional) Apache Tomcat 10+ for deployment
- SonarQube server (if you want to run Sonar analysis)

## Build & run unit tests & generate coverage
```bash
# from project root
mvn clean verify
# outputs:
# - target/demo-webapp.war
# - test reports in target/surefire-reports
# - JaCoCo report in target/site/jacoco

