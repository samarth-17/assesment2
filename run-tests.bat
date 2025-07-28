@echo off
echo ========================================
echo Running Sum API Test Suite (Recommended)
echo ========================================

echo.
echo 1. Running Unit Tests...
mvn test -Dtest=*Test -DfailIfNoTests=false

echo.
echo 2. Running Integration Tests...
mvn test -Dtest=*IntegrationTest -DfailIfNoTests=false

echo.
echo 3. Running All Tests with Coverage...
mvn clean test jacoco:report

echo.
echo ========================================
echo Test Execution Complete
echo ========================================
echo.
echo Test reports available at:
echo - target/surefire-reports/ (JUnit reports)
echo - target/site/jacoco/ (Coverage report)
echo.
echo For manual performance testing, use:
echo curl -X POST http://localhost:8080/api/sum ^
echo   -H "Content-Type: application/json" ^
echo   -d "{\"numbers\": [1, 2, 3, 4, 5]}" ^
echo   -w "Response time: %%{time_total}s\n"
echo.
pause 