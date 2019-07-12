package au.com.adamcullen.recipeservice

import org.springframework.data.repository.CrudRepository


interface RecipeRepository :CrudRepository<Recipe, Long> {
    fun findBySlug(slug: String): Recipe?
    fun findAllByOrderByAddedAtDesc(): Iterable<Recipe>
}