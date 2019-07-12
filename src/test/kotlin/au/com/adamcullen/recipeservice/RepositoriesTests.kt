package au.com.adamcullen.recipeservice

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.repository.findByIdOrNull

@DataJpaTest
class RepositoriesTests @Autowired constructor(
        val entityManager: TestEntityManager,
        val recipeRepository: RecipeRepository) {

    @Test
    fun `When findByIdOrNull then return Recipe`() {
        val bread = Recipe("Bread", "AdamC", "Flour, Water, Salt, Yeast", "Mix. Heat.")
        entityManager.persist(bread)
        entityManager.flush()

        val found = recipeRepository.findByIdOrNull(bread.id!!)
        assertThat(found).isEqualTo(bread)
    }

    @Test
    fun `When findBySlug then return Recipe`() {
        val bread = Recipe("Bread", "AdamC", "Flour, Water, Salt, Yeast", "Mix. Heat.")
        entityManager.persist(bread)
        entityManager.flush()

        val foundBySlug = recipeRepository.findBySlug(bread.slug)
        assertThat(bread).isEqualTo(foundBySlug)
    }
}
