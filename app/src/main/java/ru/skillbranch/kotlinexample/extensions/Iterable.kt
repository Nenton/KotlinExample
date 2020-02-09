package ru.skillbranch.kotlinexample.extensions

/**
 * @author Susev Sergey
 */

fun <T> List<T>.dropLastUntil(predicate: (T) -> Boolean): List<T> {
    val resultList = mutableListOf<T>()
    resultList.addAll(this)

    var isCompleted = false
    for (t in resultList.reversed()) {
        val invoke = predicate.invoke(t)
        when {
            !isCompleted && !invoke -> resultList.remove(t)
            !isCompleted && invoke -> {
                resultList.remove(t)
                isCompleted = true
            }
        }
    }
    return resultList.toList()
}