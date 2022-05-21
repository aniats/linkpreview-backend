# Linkpreview, Backend Part

This is a small project that is similar to linkpreview.com.

It's a SpringBoot web server on Kotlin that lets you parse HTML pages.

You can get images, videos, descriptions from websites.

## Installation

1. ```./gradlew build```
2. ```./gradlew bootRun```
3. http://localhost:8080/
4. Swagger: http://localhost:8080/swagger-ui/index.html

## driver folder

Project uses [Selenium WebDrive](https://www.selenium.dev/documentation/). Corresponding chromedriver version for Chrome Browser is located in here.

Latest ChromeDriver [downloads](https://chromedriver.chromium.org/downloads).