package pack.transformers

/**
 * An interface that defines some object that is used to transform some data set of type [T]
 */
interface DataTransformer<T> {
    /**
     * Indicates whether or not to remove an item.
     * @return true if [item] should be removed
     */
    fun removeItem(item: T): Boolean

    /**
     * Take an item and map or modify it.
     * @return The item with any modifications made to it
     */
    fun mapItem(item: T): T

    /**
     * Append some list of items to the end of the dataset.
     * @return A list of new items to be appended to the dataset
     */
    fun injectItems(): List<T>
}