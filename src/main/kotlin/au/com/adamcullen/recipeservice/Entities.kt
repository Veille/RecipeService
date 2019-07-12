package au.com.adamcullen.recipeservice

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Recipe(
        var title: String,
        var author: String,
        var ingredients: String,
        var method: String,
        var slug: String = title.toSlug(),
        var addedAt: LocalDateTime = LocalDateTime.now(),
        @Id @GeneratedValue var id: Long? = null
)
