package src.main.mlp

import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.tanh

/**
 * [Wikipedia](https://en.wikipedia.org/wiki/Activation_function)
 */
@Suppress("SpellCheckingInspection", "MemberVisibilityCanBePrivate", "FunctionName", "unused")
class ActivationFunction(
    val name: String,
    val function: (Float) -> Float,
    val derivative: (Float) -> Float,
    val range: Range
) : (Float) -> Float {

    init {
        names[name.lowercase()] = this
    }

    override fun hashCode(): Int =
        name.hashCode()

    override fun toString(): String =
        name

    override fun equals(other: Any?): Boolean =
        other is ActivationFunction && name == other.name

    override fun invoke(x: Float): Float = function(x)

    // TODO use Range instead
    sealed class Range {
        class Continuous(
            val a: Float,
            val b: Float,
            val aInclusive: Boolean = false,
            val bInclusive: Boolean = false,
        ) : Range()

        class Discrete(val values: Set<Float>) : Range()

        companion object {
            val R = Continuous(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY)
        }
    }

    companion object {
        private val names = mutableMapOf<String, ActivationFunction>()
        fun findByName(name: String): ActivationFunction? {
            return names[name.lowercase()]
        }

        val IDENTITY = ActivationFunction(
            "IDENTITY",
            { x -> x },
            { 1f },
            Range.R
        )

        val BINARY_STEP = ActivationFunction(
            "BINARY_STEP",
            { x -> if (x >= 0f) 1f else 0f },
            { 0f },
            Range.Discrete(setOf(0f, 1f))
        )

        private fun g1(x: Float): Float = 1 / (1 + exp(-x))

        val LOGISTIC = ActivationFunction(
            "LOGISTIC",
            ::g1,
            { x -> g1(x) * (1 - g1(x)) },
            Range.Continuous(0f, 1f)
        )

        val SIGMOID = LOGISTIC
        val SOFT_STEP = LOGISTIC

        val TANH = ActivationFunction(
            "TANH",
            ::tanh,
            { x -> 1 - (tanh(x)).pow(2) },
            Range.Continuous(-1f, 1f)
        )

        val RELU = ActivationFunction(
            "RELU",
            { x -> if (x > 0f) x else 0f },
            { x -> if (x > 0f) 1f else 0f },
            Range.Continuous(0f, Float.POSITIVE_INFINITY, aInclusive = true)
        )

        val CAPPED_RELU = ActivationFunction(
            "CAPPED_RELU",
            { x -> if (x > 1f) 1f else if (x > 0f) x else 0f },
            { x -> if (x > 0f) 1f else 0f },
            Range.Continuous(0f, 1f, aInclusive = true, bInclusive = true)
        )

        fun PRELU(name: String? = null, a: Float) = ActivationFunction(
            name ?: "PRELU(${a})",
            { x -> if (x >= 0f) x else a * x },
            { x -> if (x >= 0f) 1f else a },
            Range.R
        )

        val LEAKY_RELU =
            PRELU("LEAKY_RELU", 0.01f)

        val SOFT_PLUS = ActivationFunction(
            "SOFT_PLUS",
            { x -> ln(1 + exp(x)) },
            { x -> 1 / (1 + exp(-x)) },
            Range.Continuous(0f, Float.POSITIVE_INFINITY)
        )

        fun ELU(name: String? = null, a: Float) = ActivationFunction(
            name ?: "ELU(${a})",
            { x -> if (x > 0f) x else a * (exp(x) - 1) },
            { x -> if (x > 0f) 1f else a * exp(x) },
            Range.Continuous(-a, Float.POSITIVE_INFINITY)
        )

        val ELU1 =
            ELU("ELU1", 1f)

        fun SELU(name: String? = null, a: Float, l: Float) = ActivationFunction(
            name ?: "SELU(${a}, ${l})",
            { x -> l * (if (x >= 0f) x else a * (exp(x) - 1)) },
            { x -> l * (if (x >= 0f) 1f else a * exp(x)) },
            Range.Continuous(-l * a, Float.POSITIVE_INFINITY)
        )

        val SELU_DEFAULT =
            SELU("SELU_DEFAULT", 1.67326f, 1.0507f)

        val SILU = ActivationFunction(
            "SILU",
            { x -> x / (1 + exp(-x)) },
            { x -> (1 + exp(-x) + x * exp(-x)) / ((1 + exp(-x)).pow(2)) },
            Range.Continuous(-0.278f, Float.POSITIVE_INFINITY)
        )

        val GAUSSIAN = ActivationFunction(
            "GAUSSIAN",
            { x -> exp(-x.pow(2)) },
            { x -> -2 * x * exp(-x.pow(2)) },
            Range.Continuous(0f, 1f, bInclusive = true)
        )
    }
}