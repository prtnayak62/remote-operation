@echo off
REM Test script to generate report with actual data
echo Testing Report Generation with Actual Data
echo ==========================================
echo.

REM Check if Python is available
python --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Python is not installed or not in PATH
    exit /b 1
)

REM Check if required files exist
if not exist "quality report.txt" (
    echo ERROR: quality report.txt not found
    exit /b 1
)

if not exist "review.txt" (
    echo ERROR: review.txt not found
    exit /b 1
)

echo Found required files:
echo - quality report.txt (review data)
echo - review.txt (quality gate data)
echo.

REM Generate report
echo Generating HTML report...
python scripts\generate_report.py ^
    --review-file "quality report.txt" ^
    --quality-gate-file "review.txt" ^
    --commit "5627433" ^
    --author "Test Author" ^
    --output-file "test-report.html"

if errorlevel 1 (
    echo.
    echo ERROR: Report generation failed
    exit /b 1
)

echo.
echo ========================================
echo SUCCESS: Report generated successfully
echo ========================================
echo.
echo Output file: test-report.html
echo.
echo You can now open test-report.html in your browser to view the report.
echo.

REM Display summary
echo Report Summary:
echo ---------------
type test-report.html | findstr /C:"Quality Gate" | findstr /C:"status-"
echo.

exit /b 0

@REM Made with Bob
