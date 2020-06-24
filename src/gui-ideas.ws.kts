import ktaf.data.Value
import ktaf.util.ArrowOverloadRHS
import observables.UnitSubscribable
import kotlin.reflect.KProperty1

fun <T, V> KProperty1<T, Value<V>>.lift(): ValueOf<T, V>
    = TODO()

interface ValueOf<T, V>: ArrowOverloadRHS<ValueOf<T, V>> {
    val valueChanged: UnitSubscribable?
    fun get(value: T): V
}

fun <T, R> prop(p: KProperty1<T, R>): ValueOf<T, R> = object: ValueOf<T, R> {
    override val valueChanged: UnitSubscribable? = null
    override fun get(value: T) = p.get(value)
}

fun <T> const(v: T): ValueOf<*, T> = object: ValueOf<Any?, T> {
    override val valueChanged: UnitSubscribable? = null
    override fun get(value: Any?) = v
}

fun <T, A, B, V> join(
        a: ValueOf<T, A>,
        b: ValueOf<T, B>,
        fn: (A, B) -> V
): ValueOf<T, V> = object: ValueOf<T, V> {
    override val valueChanged: UnitSubscribable? = null // TODO
    override fun get(value: T): V = fn(a.get(value), b.get(value))
}

interface ViewContext<T>
object View

fun <T> ViewContext<T>.label(prop: ValueOf<T, String>) = View
fun <T> ViewContext<T>.vstack(fn: ViewContext<T>.() -> View) = fn().let { View }
fun <T, V> ViewContext<T>.with(prop: ValueOf<T, V>, fn: ViewContext<V>.() -> View): View = TODO()
fun <T, V: Any> ViewContext<T>.enableIf(prop: ValueOf<T, V?>, fn: ViewContext<V>.() -> View): View = TODO()
//fun <T, V: Any> ViewContext<T>.enableIf(prop: ValueOf<T, V>, fn: ViewContext<V>.() -> View): View = TODO()
fun <T> view(fn: ViewContext<T>.() -> View) = fn(object: ViewContext<T> {})
val <T> ViewContext<T>.self get(): ValueOf<T, T> = object: ValueOf<T, T> {
    override val valueChanged: UnitSubscribable? = TODO()
    override fun get(value: T): T = value
}
val ViewContext<String>.mlem get(): View = TODO()

data class Person(val firstName: String, val secondName: String?)

val personView = view<Person> {
    vstack {
        with(prop(Person::firstName)) {
            mlem
        }
        label(prop(Person::firstName))
        enableIf(prop(Person::secondName)) {
            label(self)
        }
    }
}
