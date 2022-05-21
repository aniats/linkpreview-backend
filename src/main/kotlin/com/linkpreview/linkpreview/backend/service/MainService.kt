package com.linkpreview.linkpreview.backend.service

import com.linkpreview.linkpreview.backend.model.URLDto
import org.apache.commons.validator.routines.UrlValidator
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

enum class OS {
    WINDOWS, LINUX, MAC, SOLARIS
}

fun getOS(): OS? {
    val os = System.getProperty("os.name").toLowerCase()
    return when {
        os.contains("win") -> {
            OS.WINDOWS
        }
        os.contains("nix") || os.contains("nux") || os.contains("aix") -> {
            OS.LINUX
        }
        os.contains("mac") -> {
            OS.MAC
        }
        else -> null
    }
}


@Service
class MainService {
    companion object {
        val LOG = LoggerFactory.getLogger(MainService.javaClass)
    }

    fun validateURL(url: String): String {
        var urlEdited = url
        if (!url.startsWith("https://")) {
            urlEdited = "https://$url";
        }
        val urlValidator = UrlValidator()
        if (!urlValidator.isValid(urlEdited)) {
            throw IllegalArgumentException("Invalid URL")
        }
        return urlEdited
    }

    fun setWebDriver(url: String): WebDriver {
        var pathDriver: String = when (getOS()) {
            // Loaded from here https://chromedriver.storage.googleapis.com/index.html?path=101.0.4951.41/
            OS.WINDOWS -> "_win32_101.exe"
            OS.LINUX -> "_linux64_101"
            OS.MAC -> "_mac64_101"
            else -> throw Exception("Unknown operating system!")
        }
        System.setProperty("webdriver.chrome.driver", "driver/chromedriver$pathDriver");
        val options = ChromeOptions()
        options.addArguments("--headless")
        val driver = ChromeDriver(options)
        try {
            driver.get(url);
        } catch (e: Exception) {
            LOG.error(e.message)

            throw Exception(e.localizedMessage)
        }
        return driver
    }

    fun parseImgs(driver: WebDriver): List<String> {
        return try {
            val imgs: List<WebElement> = driver.findElements(By.tagName("img"))
            var imgsPaths = imgs.map { webElement -> webElement.getAttribute("src") }.filter { value -> value != "" && value != null }
            if (imgsPaths.isEmpty()) return listOf("No images were found on page")
            LOG.warn("Image Paths: $imgsPaths")
            imgsPaths
        } catch (e: Exception) {
            LOG.error(e.message)
            listOf("No images were found on page")
        }

    }

    fun parseVideos(driver: WebDriver): List<String> {
        return try {
            val videos: List<WebElement> = driver.findElements(By.tagName("video"))
            val videosPaths = videos.map {
                it.getAttribute("src")
            }.filter { value -> value != "" && value != null }
            if (videos.isEmpty()) return listOf("No videos were found on page")
            videosPaths
        } catch (e: Exception) {
            LOG.error(e.message)
            listOf("No videos were found on page")
        }

    }

    fun parseDescription(driver: WebDriver): String {
        return try {
            driver.findElement(By.xpath("//*[@name='description']"))
                .getAttribute("content")
        } catch (e: Exception) {
            LOG.error(e.message)
            "Description was not found"
        }
    }

    fun parsePage(driver: WebDriver): URLDto {
        val imgsPaths = parseImgs(driver)
        val videosPaths = parseVideos(driver)
        val desc = parseDescription(driver)
        return URLDto(driver.title, desc, driver.currentUrl, imgsPaths, videosPaths)
    }

    fun parseURL(url: String): URLDto {
        val editedUrl = validateURL(url)
        var driver = setWebDriver(editedUrl)
        val dto = parsePage(driver)
        driver.quit()
        return dto
    }
}