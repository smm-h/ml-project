package src.main.util

import kotlin.math.max
import kotlin.math.min

sealed class Stats {
    class Doubles : Stats() {
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
            count++
            sum += it
            min = min(min, it)
            max = max(max, it)
        }

        fun clear() {
            count = 0
            sum = 0.0
            min = Double.MAX_VALUE
            max = Double.MIN_VALUE
        }
    }

    class Floats : Stats() {
        var count: Int = 0
            private set
        var sum: Float = 0f
            private set
        var min: Float = Float.MAX_VALUE
            private set
        var max: Float = Float.MIN_VALUE
            private set
        val average: Float
            get() = sum / count
        val range: Float
            get() = max - min

        fun check(it: Float) {
            count++
            sum += it
            min = min(min, it)
            max = max(max, it)
        }

        fun clear() {
            count = 0
            sum = 0f
            min = Float.MAX_VALUE
            max = Float.MIN_VALUE
        }
    }
}