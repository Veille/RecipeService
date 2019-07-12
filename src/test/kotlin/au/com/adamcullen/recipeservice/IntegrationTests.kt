package au.com.adamcullen.recipeservice

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTests(@Autowired val restTemplate: TestRestTemplate) {

    @BeforeAll
    fun setup() {
        println(">> setup")
    }

    @Test
    fun `Assert status code OK, and is of type recipe`() {
        println(">> Assert recipe title, author and status code")

        val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())

        val entity = restTemplate.getForEntity<String>("/api/recipes/")
        val returnedRecipes: ArrayList<Recipe> = mapper.readValue(entity.body.toString())

        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(returnedRecipes[0]).isInstanceOf(Recipe::class.java)
    }

    @AfterAll
    fun teardown() {
        println(">> Tear down")
    }
}