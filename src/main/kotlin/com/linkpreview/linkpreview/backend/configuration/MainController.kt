package com.linkpreview.linkpreview.backend.configuration

import com.linkpreview.linkpreview.backend.model.URLDto
import com.linkpreview.linkpreview.backend.service.MainService
import org.apache.tomcat.util.http.fileupload.IOUtils
import org.slf4j.LoggerFactory
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.URI
import java.net.URL
import java.nio.file.Paths
import java.util.TimeZone
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.servlet.http.HttpServletResponse


@RestController
@RequestMapping("/url")
@Validated
class MainController(private val mainService: MainService) {
    companion object {
        val LOG = LoggerFactory.getLogger(MainController.javaClass)
    }
    @CrossOrigin(origins = ["http://localhost:3000"])
    @GetMapping("/parse-url")
    fun parseURL(
        @RequestParam url: String,
    ): URLDto {
        return mainService.parseURL(url)
    }

    @CrossOrigin(origins = ["http://localhost:3000"])
    @GetMapping(value= ["/zip"], produces= ["application/zip"])
    @Throws(IOException::class)
    fun zipFiles(@RequestParam url: String, response: HttpServletResponse) {
        // Setting headers
        response.status = HttpServletResponse.SC_OK

        val currentTimestamp = System.currentTimeMillis()
        response.addHeader("Content-Disposition", "attachment; filename=\"$currentTimestamp.zip\"")

        // Create a list to add files to be zipped
        val files = mainService.downloadFiles(url)
        val zipOutputStream = ZipOutputStream(response.outputStream)

        // Package files
        for (file in files) {
            // New zip entry and copying inputstream with file to zipOutputStream, after all closing streams
            val timestamp = System.currentTimeMillis()
            zipOutputStream.putNextEntry(ZipEntry(file.name + timestamp))
            try {
                val fileInputStream = FileInputStream(file)
                IOUtils.copy(fileInputStream, zipOutputStream)
                file.delete()
                fileInputStream.close()
                zipOutputStream.closeEntry()
            } catch (e: Exception) {
                LOG.error(e.message)
            }

        }
        zipOutputStream.close()
    }

}