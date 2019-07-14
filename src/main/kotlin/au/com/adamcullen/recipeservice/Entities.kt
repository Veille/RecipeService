package au.com.adamcullen.recipeservice

import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class Recipe(
        var title: String,
        var author: String,
        @OneToMany(cascade = [CascadeType.ALL], targetEntity = Ingredient::class)
        var ingredients: List<Ingredient>,
        var method: String,
        var slug: String = title.toSlug(),
        var addedAt: LocalDateTime = LocalDateTime.now(),
        @Id @GeneratedValue var id: Long? = null
)

@Entity
data class Ingredient(
        var quantity: Int,
        var quantityUnit: Units,
        var name: String,
        var details: String?,
        @Id @GeneratedValue var id: Int? = null
)

enum class Units {
    tbsp,
    tsp,
    g,
    kg,
    cup,
    l,
    ml,
    qty
}