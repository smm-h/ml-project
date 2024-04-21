import java.util.*
import kotlin.math.max
import kotlin.math.min

class Stats(
    private val findCount: Boolean = false,
    private val findSum: Boolean = false,
    private val findAverage: Boolean = false,
    private val findMin: Boolean = false,
    private val findMax: Boolean = false,
    private val findRange: Boolean = false,
) {
    private val calculateCount = findCount || findAverage
    private val calculateSum = findSum || findAverage
    private val calculateMin = findMin || findRange
    private val calculateMax = findMax || findRange

    var count: Int = 0
        private set
    var sum: Double = 0.0
        private set
    var min: Double = Double.MAX_VALUE
        private set
    var max: Double = Double.MIN_VALUE
        private set
    val average: Double
        get() = sum / count
    val range: Double
        get() = max - min

    fun check(it: Double) {
        if (calculateCount) count++
        if (calculateSum) sum += it
        if (calculateMin) min = min(min, it)
        if (calculateMax) max = max(max, it)
    }

    fun getSummary(): String {
        val s = StringJoiner("; ", "[", "]")
        if (findCount) s.add("Count: $count")
        if (findSum) s.add("Sum: $sum")
        if (findAverage) s.add("Average: $average")
        if (findMin) s.add("Min: $min")
        if (findMax) s.add("Max: $max")
        if (findRange) s.add("Range: $range")
        return s.toString()
    }

    fun clear() {
        count = 0
        sum = 0.0
        min = Double.MAX_VALUE
        max = Double.MIN_VALUE
    }
}