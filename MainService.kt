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
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.net.URL
import java.nio.file.Paths
import java.util.*
import kotlin.collections.ArrayList

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

    fun downloadFromUrl(url: String): String {
        return try {
            val uri = URI(url).path
            val filename = Paths.get(uri).fileName.toString()

            val url = URL(url)
            val out = FileOutputStream(File(filename)) // Output file
            out.write(url.openStream().readAllBytes())
            out.close()

            filename
        } catch (e: Exception) {
            LOG.error(e.message)
            ""
        }
    }

    fun downloadFiles(url: String): ArrayList<File> {
        val parsedData = parseURL(url)
        val files: ArrayList<File> = ArrayList<File>()
        val links: ArrayList<String> = ArrayList<String>()

        links.addAll(parsedData.videos)
        links.addAll(parsedData.pictures)

        for (link in links) {
            if (link.isNotEmpty()) {
                val fileName = downloadFromUrl(link)
                files.add(File(fileName))
            }
        }
        return files
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
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage")
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
            LOG.warn("Image Paths: $imgsPaths")
            imgsPaths
        } catch (e: Exception) {
            LOG.error(e.message)
            listOf()
        }

    }

    fun parseVideos(driver: WebDriver): List<String> {
        return try {
            val videos: List<WebElement> = driver.findElements(By.tagName("video"))
            val videosPaths = videos.map {
                it.getAttribute("src")
            }.filter { value -> value != "" && value != null }
            LOG.warn("Video Paths: $videosPaths")
            videosPaths
        } catch (e: Exception) {
            LOG.error(e.message)
            listOf()
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
