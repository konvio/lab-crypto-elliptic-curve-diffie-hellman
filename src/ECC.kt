import java.math.BigInteger
import java.math.BigInteger.*
import java.security.SecureRandom

val TWO = BigInteger.valueOf(2L)

fun main(args: Array<String>) {
    ECC().connect()
}

class ECC {

    private val p = BigInteger("6277101735386680763835789423207666416083908700390324961279")
    private val a = BigInteger("3")
    private val b = BigInteger("2455155546008943817740293915197451784769108058161191238065")
    private val g = Point(BigInteger("602046282375688656758213480587526111916698976636884684818"), BigInteger("174050332293622031404857552280219410364023488927386650641"))
    val n = BigInteger("6277101735386680763835789423176059013767194773182842284081")

    fun connect() {
        val random = SecureRandom()
        val aliceSecret = BigInteger(37, 50, random) % n
        println("Alice generated secret key: $aliceSecret")

        val bobSecret = BigInteger(37, 50, random)
        println("Bob generated secret key: $bobSecret")

        val alicePublic = g * aliceSecret
        println("Alice generated public key: $alicePublic")

        val bobPublic = g * bobSecret
        println("Bob generated public key: $bobPublic")

        println("Transfer public keys...Nobody can hack me now")

        val alliceKey = bobPublic * aliceSecret
        println("Alice computed common key: $alliceKey")


        val bobKey = alicePublic * bobSecret
        println("Bob computed common key: $bobKey")
    }

    inner class Point(val x: BigInteger, val y: BigInteger) {

        operator fun plus(other: Point): Point {
            var dy = other.y - y
            var dx = other.x - x

            if (dx < ZERO) dx += p
            if (dy < ZERO) dy += p

            var m = (dy * dx.modInverse(p)) % p
            if (m < ZERO) m += p
            var px = (m * m - x - other.x) % p
            var py = (m * (x - px) - y) % p

            if (px < ZERO) px += p
            if (py < ZERO) py += p

            return Point(px, py)
        }

        fun doubled(): Point {
            var dy = BigInteger("3") * x * x + a
            var dx = TWO * y

            if (dx < ZERO) dx += p
            if (dy < ZERO) dy += p

            val m = (dy * dx.modInverse(p)) % p

            var px = (m * m - x - x) % p
            var py = (m * (x - px) - y) % p

            if (px < ZERO) px += p
            if (py < ZERO) py += p

            return Point(px, py)
        }

        operator fun times(n: BigInteger): Point {
            if (n == ONE) return this
            if (n.isEven()) return (this * (n / TWO)).doubled()
            return (this * (n - ONE)) + this
        }

        override fun toString(): String = "<$x, $y>"
    }
}

fun BigInteger.isEven(): Boolean = this % TWO == BigInteger.ZERO