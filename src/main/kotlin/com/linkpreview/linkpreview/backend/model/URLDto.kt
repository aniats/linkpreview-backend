package com.linkpreview.linkpreview.backend.model

data class URLDto(val title: String, val description: String, val url: String, val pictures: List<String>, val videos: List<String>)
