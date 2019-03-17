package pack.transformers

class Transformer<T : Any>(transformer: DataTransformer<T>, private val items: List<T>) : DataTransformer<T> by transformer {

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