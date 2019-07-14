package au.com.adamcullen.recipeservice

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTests(@Autowired val restTemplate: TestRestTemplate) {

    @Autowired
    lateinit var recipeRepository: RecipeRepository

    lateinit var bread: Recipe
    lateinit var mapper: ObjectMapper

    @BeforeAll
    fun setup() {
        println(">> setup")

        // register the Java time module so we can decode with no extra effort
        mapper = jacksonObjectMapper().registerModule(JavaTimeModule())

        bread = Recipe(
                title = "Bread",
                author = "AdamC",
                ingredients = getRandomIngredients(),
                method = getRandomMethod())

        recipeRepository.save(bread)
    }

    @Test
    fun `GET All Recipes - Status code OK, and first element is of type recipe`() {
        println(">> GET All Recipes - Status code OK, and first element is of type recipe")

        restTemplate.postForEntity<String>("/api/recipes/", bread)
        restTemplate.postForEntity<String>("/api/recipes/", bread)

        val entity = restTemplate.getForEntity<String>("/api/recipes/")
        val returnedRecipes: ArrayList<Recipe> = mapper.readValue(entity.body.toString())

        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(returnedRecipes[0]).isInstanceOf(Recipe::class.java)
    }

    @Test
    fun `CREATE recipe - Status code OK, and returns recipe`() {
        val entity = restTemplate.postForEntity<String>("/api/recipes/", bread)
        val returnedRecipe: Recipe = mapper.readValue(entity.body.toString())

        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(returnedRecipe).isEqualToComparingOnlyGivenFields(bread, "title", "author", "method", "slug", "addedAt")

        //TODO: the following will fail because each ingredient has a unique ID - need to solve this
        //assertThat(returnedRecipe).isEqualToComparingOnlyGivenFields(bread, "title", "author", "method", "ingredients", "slug", "addedAt")
    }

    @Test
    fun `CREATE Recipe & GET by id - Status code OK`() {
        val postEntity = restTemplate.postForEntity<String>("/api/recipes/", bread)
        val returnedRecipe: Recipe = mapper.readValue(postEntity.body.toString())

        val getEntity = restTemplate.getForEntity<String>("/api/recipes?id=${returnedRecipe.id}")
        assertThat(getEntity.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `CREATE Recipe & GET by slug - Status code OK`() {
        val postEntity = restTemplate.postForEntity<String>("/api/recipes/", bread)
        val returnedRecipe: Recipe = mapper.readValue(postEntity.body.toString())

        val getEntity = restTemplate.getForEntity<String>("/api/recipes/${returnedRecipe.slug}")
        assertThat(getEntity.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `GET recipe with bad id - Status code 404`() {
        val getEntity = restTemplate.getForEntity<String>("/api/recipes?id=666")
        assertThat(getEntity.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `GET recipe with bad slug - status code 404`() {
        val getEntity = restTemplate.getForEntity<String>("/api/recipes/bad_slug_404")
        assertThat(getEntity.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `CREATE recipe & PUT update author - status code OK`() {
        val postEntity = restTemplate.postForEntity<String>("/api/recipes/", bread)
        val returnedRecipe: Recipe = mapper.readValue(postEntity.body.toString())

        returnedRecipe.title = "NEW Bread"

        val requestUpdate = HttpEntity(returnedRecipe, null)
        val response = restTemplate.exchange("/api/recipes/${returnedRecipe.id}", HttpMethod.PUT, requestUpdate, String::class.java)
        val responseBody: Recipe = mapper.readValue(response.body.toString())

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(responseBody.title).isEqualTo(returnedRecipe.title)
    }

    @Test
    fun `PUT update bad record - status code 404`() {
        val requestUpdate = HttpEntity(bread)
        val response = restTemplate.exchange("/api/recipes/666", HttpMethod.PUT, requestUpdate, String::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `CREATE recipe & DELETE recipe with id - status code OK`() {
        val postEntity = restTemplate.postForEntity<String>("/api/recipes/", bread)
        val returnedRecipe: Recipe = mapper.readValue(postEntity.body.toString())

        val requestUpdate = HttpEntity(bread)
        val response = restTemplate.exchange("/api/recipes/${returnedRecipe.id}", HttpMethod.DELETE, requestUpdate, String::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `DELETE bad record - status code 404`() {
        val requestUpdate = HttpEntity(bread)
        val response = restTemplate.exchange("/api/recipes/666", HttpMethod.DELETE, requestUpdate, String::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @AfterEach
    fun teardown() {
        println(">> Tear down")
        recipeRepository.deleteAll()
    }
}