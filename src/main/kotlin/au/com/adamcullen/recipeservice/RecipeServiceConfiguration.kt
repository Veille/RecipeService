package au.com.adamcullen.recipeservice

import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RecipeServiceConfiguration {

    @Bean
    fun databaseInitializer(recipeRepository: RecipeRepository) = ApplicationRunner {

        val riceRecipe = Recipe(
                title = "Cooked Rice",
                author = "Adam Cullen",
                ingredients = "Rice, Water, Salt",
                method = "Put rice in water. Add Salt")

        recipeRepository.save(riceRecipe)
    }
}