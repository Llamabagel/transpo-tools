package ca.llamabagel.transpo.tools.pack.transformers

/**
 * An interface that defines some object that is used to transform some data set of type [T]
 */
abstract class DataTransformer<T : Any> {
    /**
     * Indicates whether or not to remove an item.
     * @return true if [item] should be removed
     */
    protected abstract fun removeItem(item: T): Boolean

    /**
     * Take an item and map or modify it.
     * @return The item with any modifications made to it
     */
    protected abstract fun mapItem(item: T): T

    /**
     * Append some list of items to the end of the dataset.
     * @return A list of new items to be appended to the dataset
     */
    protected abstract fun injectItems(): List<T>

    /**
     * Performs all of the transformation operations specified by the [DataTransformer] on the list of [items].
     * @return [items] after all transformations have been performed
     */
    fun transform(items: List<T>): List<T> {
        val mapped = items.mapNotNull { item ->
            when {
                removeItem(item) -> null
                else -> mapItem(item)
            }
        }

        return mapped + injectItems()
    }
}