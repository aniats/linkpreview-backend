package com.linkpreview.linkpreview.backend.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.linkpreview.linkpreview.backend.model.URLDto
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import com.ninjasquad.springmockk.SpykBean
import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.every
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.Test
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.ints.exactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.startWith
import io.mockk.clearAllMocks
import io.mockk.impl.annotations.RelaxedMockK
import junit.framework.Assert.assertEquals
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.ResultActionsDsl
import kotlin.text.Charsets.UTF_8

@SpringBootTest()
@AutoConfigureMockMvc
class MainServiceTest(private val mockMvc: MockMvc, private val objectMapper: ObjectMapper, private val mainService: MainService): FeatureSpec()  {

    val LOG = LoggerFactory.getLogger(MainService.javaClass)

    override fun extensions(): List<Extension> = listOf(SpringExtension)

    companion object {
        const val incorrectUrl = "bla.."
        const val noDescriptionLink = "https://dic.academic.ru/dic.nsf/ruwiki/1605923"
        const val neverGonnaGiveYouUpLink = "https://youtu.be/dQw4w9WgXcQ"
        const val noImagesSiteLink = "https://www.textise.net/"
        const val tinkoffSiteLink = "tinkoff.ru"
        const val wikipediaLink = "https://en.wikipedia.org/wiki/Java_(programming_language)"
        val tinkoffSite = URLDto("Тинькофф — Кредитные и дебетовые карты, кредиты для бизнеса и физических лиц",
            "Оформление онлайн и бесплатная доставка: кредитные и дебетовые карты, кредиты, сотовый оператор, страхование и другие услуги для физических и юридических лиц",
            "https://www.tinkoff.ru/", listOf("https://acdn.tinkoff.ru/static/pages/files/b9105277-77aa-47c2-bdad-e7b126151446.png","https://acdn.tinkoff.ru/static/pages/files/e12cc1df-0334-4002-a714-116068dad7bd.png","https://acdn.tinkoff.ru/static/pages/files/577f0478-3545-4735-b52c-6ae8dec77821.png","https://acdn.tinkoff.ru/static/pages/files/879f88a8-6159-4e0c-bb44-bed67e6cf77e.png","https://acdn.tinkoff.ru/static/pages/files/75f25658-bcaa-4e07-8e72-bccd9410bea6.png","https://acdn.tinkoff.ru/static/pages/files/2df65721-c8cf-45d0-8530-ed5b33cac5aa.png","https://acdn.tinkoff.ru/static/pages/files/966a9d87-ddd1-4cf2-9ab6-3b6b5e01f6e3.png","https://acdn.tinkoff.ru/static/pages/files/0a0e5a40-56df-4aec-8571-b718f8c8ebfb.png","https://acdn.tinkoff.ru/static/pages/files/0c4278bf-73a7-4ecb-9846-721a25b9d95b.png","https://acdn.tinkoff.ru/static/pages/files/5179d812-6e7c-468c-8f98-21741f6266ed.png","https://acdn.tinkoff.ru/static/pages/files/fde695c7-8bbe-4922-bb41-834144b586c9.png","https://acdn.tinkoff.ru/static/pages/files/6519d0e1-773d-4d2a-b2f3-7c9f6ababd76.png","https://acdn.tinkoff.ru/static/pages/files/b790fad5-1cfc-464d-b919-9ce954e36bb0.png","https://acdn.tinkoff.ru/static/pages/files/00570022-c354-4666-a9e9-e7d0ac0e04f6.png","https://acdn.tinkoff.ru/static/pages/files/d02a6333-183b-407e-a2fe-e607abe2e90f.png","https://acdn.tinkoff.ru/static/pages/files/2f2b9713-795e-4c11-b876-f0aa53dc6e0d.png","https://acdn.tinkoff.ru/static/pages/files/1542ffd6-bb94-487a-8665-2a6c1defd52a.png","https://acdn.tinkoff.ru/static/pages/files/e698641e-05e3-4312-b8c9-5cb8fc7d256b.png","https://acdn.tinkoff.ru/static/pages/files/14b20d44-a2f5-4565-a1ee-c8c70c8e9b2d.png","https://acdn.tinkoff.ru/static/pages/files/2e9ee52d-1774-4984-9dca-b5621b16c688.png","https://acdn.tinkoff.ru/static/pages/files/42a67f90-da6d-420b-a6ce-901f31b1cc01.png","https://acdn.tinkoff.ru/static/pages/files/ef89e34e-19be-4151-8ae8-8a1fcc351e3d.png","https://acdn.tinkoff.ru/static/pages/files/837059a7-2425-48a4-993a-24c33619c836.png","https://acdn.tinkoff.ru/static/pages/files/c2f82833-efe2-4b6b-b2c4-644efda4f189.png","https://acdn.tinkoff.ru/static/pages/files/7ff2a4b4-2737-498d-ae49-ed109c831008.png","https://acdn.tinkoff.ru/static/pages/files/93972ac2-0c37-46d2-a42d-fce7e43a1057.png","https://acdn.tinkoff.ru/static/pages/files/e0e9ab6e-578f-49a3-a68f-286c323c70d1.png","https://acdn.tinkoff.ru/static/pages/files/9c5c733d-12db-4a41-ba76-b3074b997c44.png","https://acdn.tinkoff.ru/static/pages/files/37debbfc-fea5-4db6-b105-1e018a4d321b.png","https://acdn.tinkoff.ru/static/pages/files/5d5572a5-cd52-4c98-ae4b-aad1fe0778b2.png","https://acdn.tinkoff.ru/static/pages/files/61a399dc-41b1-401a-b166-2242f2eb5d46.png","https://acdn.tinkoff.ru/static/pages/files/5d5572a5-cd52-4c98-ae4b-aad1fe0778b2.png","https://acdn.tinkoff.ru/static/pages/files/16f235a0-3440-4181-841a-3b7c5a7f5372.png","https://acdn.tinkoff.ru/static/pages/files/add8daf4-d095-40bb-b214-6259259b240c.png","https://acdn.tinkoff.ru/static/pages/files/31a2d24f-fc8d-46f2-825a-2ab574f209fb.png","https://acdn.tinkoff.ru/static/pages/files/bb576fe6-abc9-4a23-acac-963d48f7fbb0.png","https://acdn.tinkoff.ru/static/pages/files/1271fcad-a2a5-458e-868e-dc62afda59c6.png","https://acdn.tinkoff.ru/static/pages/files/ad4b3437-9d0e-495c-bba6-a1e7d18226d4.png","https://acdn.tinkoff.ru/static/pages/files/670d33dc-ed90-43cf-9c5c-9d89bcb6ccf6.png","https://acdn.tinkoff.ru/static/pages/files/9ae74a9e-7c1b-4a65-a45f-64790664727f.png","https://acdn.tinkoff.ru/static/pages/files/c87d8d70-664c-41f5-a35d-2006701082e9.png","https://acdn.tinkoff.ru/static/pages/files/6ca823a0-2554-4c46-80c9-a800a8b26a3b.png","https://acdn.tinkoff.ru/static/pages/files/627c9673-3a69-4f44-90bd-f8fde510c81c.png","https://acdn.tinkoff.ru/static/pages/files/c41e54a8-6e7c-4c57-9961-619acc8bbbbf.png","https://acdn.tinkoff.ru/static/pages/files/c7e8e341-927d-4406-b9ad-ba01b541d3b5.png","https://acdn.tinkoff.ru/static/pages/files/1d56e4b0-c37c-4b3b-a523-945a12d75717.png","https://acdn.tinkoff.ru/static/pages/files/f8ce540f-cca8-47d7-a24a-88874553c490.png","https://acdn.tinkoff.ru/static/pages/files/c2de65d2-cc8a-4604-8977-25fb9998d5cb.png","https://acdn.tinkoff.ru/static/pages/files/52b15c88-b8c8-4651-94ee-f8f68e82100a.png","https://acdn.tinkoff.ru/static/pages/files/93972ac2-0c37-46d2-a42d-fce7e43a1057.png","https://acdn.tinkoff.ru/static/pages/files/caa4bbcb-f4b0-4f98-8300-d05582d286ee.png","https://acdn.tinkoff.ru/static/pages/files/61a399dc-41b1-401a-b166-2242f2eb5d46.png","https://acdn.tinkoff.ru/static/pages/files/9c5c733d-12db-4a41-ba76-b3074b997c44.png","https://acdn.tinkoff.ru/static/pages/files/90f598a3-5ecc-4f89-b151-e7c53f3d3033.png","https://acdn.tinkoff.ru/static/pages/files/235c05c6-c851-469a-aeb4-603be584e24f.png","https://acdn.tinkoff.ru/static/pages/files/5e563806-f1c9-416d-96d7-9c16712486d0.png","https://acdn.tinkoff.ru/static/pages/files/f25b6679-5b8f-4e1b-a890-7fc529567ed1.png","https://acdn.tinkoff.ru/static/pages/files/7319c5fb-b464-4f16-bab7-00cd96250cb4.png","https://acdn.tinkoff.ru/static/pages/files/f1474094-02f7-4a7e-82a7-d3c4799ba8e0.svg","https://acdn.tinkoff.ru/static/pages/files/c720f945-b1d2-4a5e-b03d-f4f09a44fa88.svg","https://acdn.tinkoff.ru/static/pages/files/72b28ac3-53ba-49df-9737-4aa8d0f5bf84.svg","https://acdn.tinkoff.ru/static/pages/files/aff3cf48-6e4b-4b01-8302-d3aa1b085b7c.svg","https://acdn.tinkoff.ru/static/pages/files/223d4e42-d7f8-47a6-822f-4c889407baa5.png","https://acdn.tinkoff.ru/static/pages/files/a6b0da21-3632-4dae-aec3-157a15a7479c.svg","https://acdn.tinkoff.ru/static/pages/files/e2fc5957-1c90-4e3e-9a67-897b75ca58be.png","https://acdn.tinkoff.ru/static/pages/files/824a7f40-c383-4d67-8697-d202ff084361.png","https://acdn.tinkoff.ru/static/pages/files/e4d2bcd5-ae38-4552-b741-52cf9fd19d14.png","https://acdn.tinkoff.ru/static/pages/files/de943d2e-c19c-45e7-80c0-e56bc2fa595a.png","https://acdn.tinkoff.ru/static/pages/files/78215372-7c3a-4bd8-a4b3-d0a3b9064bb9.png","https://acdn.tinkoff.ru/static/pages/files/8d0c40ec-c863-4641-9f08-6ba4ac3ee481.svg","https://acdn.tinkoff.ru/static/pages/files/3dea9d42-0663-4823-a895-a4aac31269e5.svg","https://acdn.tinkoff.ru/static/pages/files/334a9f41-5bb1-4140-85ec-1aa9daf77a78.svg","https://acdn.tinkoff.ru/static/pages/files/21af0841-b4a1-444a-8714-d5114b78faba.svg","https://acdn.tinkoff.ru/static/pages/files/7aa0cfc1-8d84-4c38-bc7e-0ddb400923d8.png"), listOf())

        const val tinkoffEducationLink = "https://fintech.tinkoff.ru/"
        val tinkoffEducation = URLDto("Тинькофф Образование", "Образовательные программы для школьников, студентов и выпускников", "https://fintech.tinkoff.ru/", listOf("https://acdn.tinkoff.ru/static/pages/files/c7bc15a4-6c10-4c35-bfe1-2fa8130e8232.png","https://acdn.tinkoff.ru/static/pages/files/ebd04043-48cc-4e97-820e-e90e3ff2e115.png","https://acdn.tinkoff.ru/static/pages/files/4718948e-8a4a-4f0e-8892-1c57901e8f10.png","https://acdn.tinkoff.ru/static/pages/files/523e959a-e9a6-44d7-9b43-f5cdf2f7d9e1.png","https://acdn.tinkoff.ru/static/pages/files/8e6fd790-5059-4c5d-a6e6-95c1e2522d4b.png","https://acdn.tinkoff.ru/static/pages/files/46ea3832-f328-47c9-a6fa-0498b817d8ef.png","https://acdn.tinkoff.ru/static/pages/files/01a09d98-1bba-4f20-afbd-972182448095.png","https://acdn.tinkoff.ru/static/pages/files/62052897-8591-4a86-b0e8-49246a7fbebe.png","https://acdn.tinkoff.ru/static/pages/files/2803b214-2869-43ce-abd7-0bba2a88ae3a.png","https://acdn.tinkoff.ru/static/pages/files/e140aed5-5be5-4e28-b9a2-6545e1e7e70a.png","https://acdn.tinkoff.ru/static/pages/files/25adefc8-0b8f-4cc8-b184-588d9b692244.png","https://acdn.tinkoff.ru/static/pages/files/331c8bc6-5d30-4ed3-9239-c824af532da5.png"), listOf())
    }

    init {
        feature("Parse URL") {
            scenario("Incorrect url") {
                getStatusCodeParseURL(incorrectUrl) shouldBe HttpStatus.BAD_REQUEST.value()
            }
            scenario("Correct url") {
                val result = parseURL(tinkoffSiteLink)
                result shouldBe tinkoffSite
                getStatusCodeParseURL(tinkoffSiteLink) shouldBe HttpStatus.OK.value()
            }
            scenario("Another correct url") {
                val result = parseURL(tinkoffEducationLink)
                result shouldBe tinkoffEducation
                getStatusCodeParseURL(tinkoffEducationLink) shouldBe HttpStatus.OK.value()
            }
        }
        feature("Get zip archive with images") {
            scenario("Success") {
                getStatusCodeZipFiles(wikipediaLink) shouldBe HttpStatus.OK.value()
            }
        }
        feature("Validate URL") {
            scenario("Ok") {
                val urlEdited = mainService.validateURL(tinkoffSiteLink)
                urlEdited shouldBe "https://tinkoff.ru"
            }
            scenario("Not Ok") {
                val exception = shouldThrow<IllegalArgumentException> {
                    mainService.validateURL(incorrectUrl)
                }
                exception.message should startWith("Invalid URL")
            }
        }
        feature("Parse Description") {
            scenario("Ok") {
                val url =  mainService.validateURL(tinkoffSiteLink)
                var driver = mainService.setWebDriver(url)
                val desc = mainService.parseDescription(driver)
                desc shouldBe tinkoffSite.description
            }
            scenario("No description") {
                val url =  mainService.validateURL(noDescriptionLink)
                var driver = mainService.setWebDriver(url)
                val desc = mainService.parseDescription(driver)
                desc shouldBe "Description was not found"
            }
        }
        feature("Parse Videos") {
            scenario("No videos") {
                val url =  mainService.validateURL(tinkoffSiteLink)
                var driver = mainService.setWebDriver(url)
                val videoLink = mainService.parseVideos(driver)
                videoLink shouldBe listOf()
            }
            scenario("Successfully parsed") {
                val url =  mainService.validateURL(neverGonnaGiveYouUpLink)
                var driver = mainService.setWebDriver(url)
                val videoLink = mainService.parseVideos(driver)
                (videoLink.isNotEmpty()) shouldBe true
            }
        }
        feature("Parse Images") {
            scenario("No images on a site") {
                val url =  mainService.validateURL(noImagesSiteLink)
                var driver = mainService.setWebDriver(url)
                val images = mainService.parseImgs(driver)
                images shouldBe listOf()
            }
            scenario("Success") {
                val url =  mainService.validateURL(tinkoffSiteLink)
                var driver = mainService.setWebDriver(url)
                val images = mainService.parseImgs(driver)
                images shouldBe tinkoffSite.pictures
            }
        }
    }

    private inline fun <reified T> ResultActionsDsl.readResponse(expectedStatus: HttpStatus = HttpStatus.OK): T = this
        .andExpect { status { isEqualTo(expectedStatus.value()) } }
        .andReturn().response.getContentAsString(UTF_8)
        .let { if (T::class == String::class) it as T else objectMapper.readValue(it) }


    private fun parseURL(url: String): URLDto =
        mockMvc.get("/url/parse-url?url={url}", url).readResponse()

    private fun getStatusCodeParseURL(url: String): Int =
        mockMvc.get("/url/parse-url?url={url}", url).andReturn().response.status

    private fun zipFiles(url: String): Unit =
        mockMvc.get("/url/zip?url={url}", url).readResponse()

    private fun getStatusCodeZipFiles(url: String): Int =
        mockMvc.get("/url/zip?url={url}", url)
            .andReturn().response.status
}