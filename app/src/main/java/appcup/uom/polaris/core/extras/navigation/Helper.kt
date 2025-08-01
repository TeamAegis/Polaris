package appcup.uom.polaris.core.extras.navigation

fun <T> MutableList<T>.rebaseTo(newItems: List<T>) {
    if (newItems.isEmpty()) return  // Don't allow empty nav stacks

    // If current list is empty, just add all
    if (isEmpty()) {
        addAll(newItems)
        return
    }

    // Replace in place
    val minSize = minOf(this.size, newItems.size)
    for (i in 0 until minSize) {
        this[i] = newItems[i]
    }

    // If newItems has more, append them
    if (newItems.size > this.size) {
        addAll(newItems.subList(this.size, newItems.size))
    }

    // If current has more, remove the extras
    if (this.size > newItems.size) {
        // Remove from end to avoid triggering empty list
        val removeCount = this.size - newItems.size
        repeat(removeCount) {
            removeAt(lastIndex)
        }
    }
}

