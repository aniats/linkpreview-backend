package com.linkpreview.linkpreview.backend.configuration

import com.linkpreview.linkpreview.backend.model.URLDto
import com.linkpreview.linkpreview.backend.service.MainService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/url")
@Validated
class MainController(private val mainService: MainService) {
    @CrossOrigin(origins = ["http://localhost:3000"])
    @GetMapping("/parse-url")
    fun parseURL(
        @RequestParam url: String,
    ): URLDto {
        return mainService.parseURL(url)
    }
}