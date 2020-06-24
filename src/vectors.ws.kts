
sealed class Dimensions {
    object Zero: Dimensions()
    class Inc<D: Dimensions>(): Dimensions()
}

typealias One = Dimensions.Inc<Dimensions.Zero>
typealias Two = Dimensions.Inc<One>
typealias Three = Dimensions.Inc<Two>
typealias Four = Dimensions.Inc<Three>

sealed class FList<D: Dimensions> {
    object Empty: FList<Dimensions.Zero>()
    class Cons<D: Dimensions>(val v: Float, val l: FList<D>): FList<Dimensions.Inc<D>>()

    fun components(): List<Float> = when (this) {
        is Empty -> listOf()
        is Cons<*> -> listOf(v) + l.components()
    }

    fun map(other: FList<D>, fn: (Float, Float) -> Float): FList<D> = when (this) {
        is Empty -> this
        is Cons<*> -> Cons(fn(v, (other as Cons<*>).v), (l as FList<D>).map((other as Cons<D>).l, fn)) as FList<D>
    }
}

class Vector<D: Dimensions>(val fl: FList<D>) {
    operator fun plus(v: Vector<D>): Vector<D>
            = Vector(fl.map(v.fl) { a, b -> a + b })
}
typealias vec2 = Vector<Two>
typealias vec3 = Vector<Three>
typealias vec4 = Vector<Four>

fun <D: Dimensions> Vector<D>.introduce(n: Float): Vector<Dimensions.Inc<D>>
        = TODO()

fun <D: Dimensions> Vector<Dimensions.Inc<D>>.reduce(): Vector<D>
        = TODO()

fun vec2(x: Float, y: Float) = vec2(FList.Cons(x, FList.Cons(y, FList.Empty)))
fun vec3(x: Float, y: Float, z: Float) = vec3(FList.Cons(x, FList.Cons(y, FList.Cons(z, FList.Empty))))
fun vec4(x: Float, y: Float, z: Float, w: Float) = vec4(FList.Cons(x, FList.Cons(y, FList.Cons(z, FList.Cons(z, FList.Empty)))))

fun <D: Dimensions> Vector<D>.dot(v: Vector<D>): Float
        = fl.components().zip(v.fl.components()) { a, b -> a * b } .sum()

val a: vec2 = vec2(1f, 2f)
val b: vec2 = vec2(2f, 3f)
val c: vec2 = a + b
val d: vec3 = c.introduce(5f)
val e: vec2 = d.reduce()
val f: vec4 = a.introduce(2f).introduce(5f)
val g: vec2 = f.reduce().reduce()
