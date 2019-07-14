package au.com.adamcullen.recipeservice

fun getRandomIngredients(): ArrayList<Ingredient> =
        arrayListOf(
            Ingredient(100, Units.g, "AP Flour", "sifted"),
            Ingredient(100, Units.g, "Water", "34 degrees celsius"),
            Ingredient(2, Units.g, "Salt", "kosher"),
            Ingredient(5, Units.g, "Yeast", "")
    )

fun getRandomIngredient(): Ingredient = Ingredient(100, Units.g, "AP Flour", "sifted")

fun getRandomMethod(): String = "Mix all ingredients until tender. \n Serve."