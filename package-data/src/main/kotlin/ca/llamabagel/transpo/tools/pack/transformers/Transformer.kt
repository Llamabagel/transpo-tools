package ca.llamabagel.transpo.tools.pack.transformers

class Transformer<T : Any>(transformer: DataTransformer<T>, private val items: List<T>) : DataTransformer<T> by transformer {

    /**
     * Performs all of the transformation operations specified by the [DataTransformer] on the list of [items].
     * @return [items] after all transformations have been performed
     */
    fun transform(): List<T> {
        val mapped = items.mapNotNull {item ->
            when {
                removeItem(item) -> null
                else -> mapItem(item)
            }
        }

        return mapped + injectItems()
    }

}