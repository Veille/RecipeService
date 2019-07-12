package au.com.adamcullen.recipeservice

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/recipes")
class RecipeController(private val recipeRepository: RecipeRepository) {

    @GetMapping("/")
    fun findAll() = recipeRepository.findAllByOrderByAddedAtDesc()

    @GetMapping("/{slug}")
    fun getRecipeBySlug(@PathVariable slug: String): ResponseEntity<Recipe> {

        val recipe = recipeRepository.findBySlug(slug) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(recipe)
    }

    @GetMapping
    fun getRecipeById(@RequestParam id: Long): ResponseEntity<Recipe> {
        return recipeRepository.findById(id).map { recipe ->
            ResponseEntity.ok(recipe)
        }.orElse(ResponseEntity.notFound().build())
    }

    @PostMapping("/")
    fun createRecipe(@Valid @RequestBody recipe: Recipe): Recipe =
        recipeRepository.save(recipe)

    @PutMapping("/{id}")
    fun updateRecipeById(@PathVariable id: Long,
                         @Valid @RequestBody newRecipe: Recipe): ResponseEntity<Recipe> {
            return recipeRepository.findById(id).map { existingRecipe ->
                val updatedRecipe: Recipe = existingRecipe
                        .copy(title = newRecipe.title,
                                author = newRecipe.author,
                                ingredients = newRecipe.ingredients,
                                method = newRecipe.method,
                                slug = existingRecipe.slug,
                                addedAt = existingRecipe.addedAt)
                ResponseEntity.ok().body(recipeRepository.save(updatedRecipe))

            }.orElse(ResponseEntity.notFound().build())
    }

    @DeleteMapping("/{id}")
    fun deleteRecipeById(@PathVariable id: Long): ResponseEntity<Void> {
        return recipeRepository.findById(id).map { recipe ->
            recipeRepository.delete(recipe)
            ResponseEntity<Void>(HttpStatus.OK)
        }.orElse(ResponseEntity.notFound().build())
    }
}