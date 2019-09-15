package ktaf.core

import geometry.vec2
import geometry.vec3
import geometry.vec4
import kotlin.reflect.full.createInstance

// README!
// This is experimental and not meant to be used!
// TODO: experimental shader compilation code

@Deprecated("Experimental")
abstract class ShaderVariableType(val name: String)
@Deprecated("Experimental")
data class ShaderVariable<VariableType: ShaderVariableType>(val type: VariableType, val name: String)

@Deprecated("Experimental")
object GLSLType {
    @Deprecated("Experimental")
    abstract class numeric(name: String): ShaderVariableType(name)
    @Deprecated("Experimental")
    abstract class vector(name: String): numeric(name)
    @Deprecated("Experimental")
    object float: numeric("float")
    @Deprecated("Experimental")
    object bool: ShaderVariableType("bool")
    @Deprecated("Experimental")
    object int: numeric("int")
    @Deprecated("Experimental")
    object vec2: vector("vec2") {
        val x = ShaderVariable(float, "x")
        val y = ShaderVariable(float, "y")
    }
    @Deprecated("Experimental")
    object vec3: vector("vec3") {
        val x = ShaderVariable(float, "x")
        val y = ShaderVariable(float, "y")
        val z = ShaderVariable(float, "z")
    }
    @Deprecated("Experimental")
    object vec4: vector("vec4") {
        val x = ShaderVariable(float, "x")
        val y = ShaderVariable(float, "y")
        val z = ShaderVariable(float, "z")
        val w = ShaderVariable(float, "w")
    }
    @Deprecated("Experimental")
    object mat4: numeric("mat4")
}

@Deprecated("Experimental")
sealed class ShaderGraphNode<out Out: ShaderVariableType>(
        val type: Out,
        val template: String = "",
        val roots: Set<ShaderGraphNode<*>> = setOf()
) {
    constructor(type: Out, vararg roots: ShaderGraphNode<*>): this(type, "", roots.toSet())
    constructor(type: Out, template: String, vararg roots: ShaderGraphNode<*>): this(type, template, roots.toSet())

    open fun compile(name: NameGenerator): String {
        val names = roots.map { it to name.generate() }
        val rootsCompiled = names.map { (v, vname) ->
            "${v.type.name} $vname;\n" + v.compile(name).replace("%result", vname) + "\n"
        }
        val operation = names.zip(1 .. roots.size).fold(template) { s, (v, i) ->
            s.replace("%$i", v.second)
        }

        return "${rootsCompiled.joinToString("")}$operation;"
    }
}

@Deprecated("Experimental")
class ShaderGraphVariableNode<VariableType: ShaderVariableType>(
        variable: ShaderVariable<VariableType>
): ShaderGraphNode<VariableType>(variable.type, "%result = ${variable.name}")

@Deprecated("Experimental")
class ShaderGraphConstantNode<ConstantType: ShaderVariableType>(
        type: ConstantType,
        val value: String
): ShaderGraphNode<ConstantType>(type, "%result = $value")

@Deprecated("Experimental")
class ShaderGraphOutputNode<VariableType: ShaderVariableType>(
        type: VariableType,
        value: ShaderGraphNode<VariableType>,
        val variable: ShaderVariable<VariableType>
): ShaderGraphNode<VariableType>(type, "${variable.name} = %1; %result = %1", value)

@Deprecated("Experimental")
open class ShaderGraphNaryOperation<Output: ShaderVariableType>(
        type: Output,
        operation: String,
        values: List<ShaderGraphNode<*>>
): ShaderGraphNode<Output>(type, "%result = $operation", roots = values.toSet())

@Deprecated("Experimental")
open class ShaderGraphUnaryOperation<Input: ShaderVariableType, Output: ShaderVariableType>(
        type: Output,
        operation: String,
        value: ShaderGraphNode<Input>
): ShaderGraphNaryOperation<Output>(type, operation, listOf(value))

@Deprecated("Experimental")
open class ShaderGraphBinaryOperation<I1: ShaderVariableType, I2: ShaderVariableType, Output: ShaderVariableType>(
        type: Output,
        operation: String,
        lvalue: ShaderGraphNode<I1>,
        rvalue: ShaderGraphNode<I2>
): ShaderGraphNaryOperation<Output>(type, operation, listOf(lvalue, rvalue))

@Deprecated("Experimental")
open class ShaderGraphTernaryOperation<I1: ShaderVariableType, I2: ShaderVariableType, I3: ShaderVariableType, Output: ShaderVariableType>(
        type: Output,
        operation: String,
        t1: ShaderGraphNode<I1>,
        t2: ShaderGraphNode<I2>,
        t3: ShaderGraphNode<I3>
): ShaderGraphNaryOperation<Output>(type, operation, listOf(t1, t2, t3))

@Deprecated("Experimental")
open class ShaderGraphIf<Output: ShaderVariableType, I1: Output, I2: Output>(
        type: Output,
        condition: ShaderGraphNode<GLSLType.bool>,
        truthy: ShaderGraphNode<I1>,
        falsey: ShaderGraphNode<I2>
): ShaderGraphNode<Output>(type, condition, truthy, falsey) {
    override fun compile(name: NameGenerator): String {
        val names = roots.map { it to name.generate() }
        val rootsCompiled = names.map { (v, vname) ->
            "${v.type.name} $vname;\n" + v.compile(name).replace("%result", vname)
        }

        return "${rootsCompiled[0]}\n" +
                "if (${names[0].second}) {\n\t" +
                    "${rootsCompiled[1]}\n" +
                    "%result = ${names[1].second}\n" +
                "}\n" +
                "else {\n\t" +
                    "${rootsCompiled[2]}\n" +
                    "%result = ${names[2].second}\n" +
                "}"
    }
}

@Deprecated("Experimental")
class ShaderGraph(
        vararg val nodes: ShaderGraphNode<*>
) {
    val uniforms: MutableSet<ShaderVariable<*>> = mutableSetOf()
    val ins: MutableSet<ShaderVariable<*>> = mutableSetOf()
    val outs: MutableSet<ShaderVariable<*>> = mutableSetOf()

    fun addUniform(name: String, type: ShaderVariableType) { uniforms.add(ShaderVariable(type, name)) }
    fun addIn(name: String, type: ShaderVariableType) { uniforms.add(ShaderVariable(type, name)) }
    fun addOut(name: String, type: ShaderVariableType) { uniforms.add(ShaderVariable(type, name)) }

    fun compile(): String {
        val nameGenerator = NameGenerator()
        val variablesToDefine = generateSequence(nodes.toList()) { roots ->
            (roots.flatMap { it.roots } - roots).takeIf { it.isNotEmpty() }
        } .filterIsInstance(ShaderGraphOutputNode::class.java) .map { it.variable }
        val uniformsDefined = uniforms.joinToString("\n") {
            "uniform ${it.type.name} ${it.name};"
        }
        val insDefined = ins.joinToString("\n") {
            "in ${it.type.name} ${it.name};"
        }
        val outsDefined = outs.joinToString("\n") {
            "out ${it.type.name} ${it.name};"
        }
        val result = nodes.map { it.compile(nameGenerator) }.joinToString("\n\t") {
            it.replace("\n", "\n\t")
        } + "\n"

        return "#version 440 core\n\n" +
                uniformsDefined + "\n\n" +
                insDefined + "\n\n" +
                outsDefined + "\n\n" +
                "void main(void) {\n\t" +
                    variablesToDefine.joinToString("\n\t") { "${it.type.name} ${it.name};" } + "\n\t" +
                    result.replace(Regex("%result = .*;"), "") +
                "}"
    }

    infix fun joinTo(graph: ShaderGraph): ShaderGraph {
        val new = ShaderGraph(*this.nodes, *graph.nodes)

        new.uniforms.addAll(uniforms)
        new.ins.addAll(ins)
        new.outs.addAll(outs)

        new.uniforms.addAll(graph.uniforms)
        new.ins.addAll(graph.ins)
        new.outs.addAll(graph.outs)

        return new
    }
}

@Deprecated("Experimental")
class ShaderGraphNodeBuilder(fn: ShaderGraphNodeBuilder.() -> ShaderGraphNode<*>) {
    val node = fn(this)

    fun const(value: Int) = ShaderGraphConstantNode(GLSLType.int, value.toString())
    fun const(value: Float) = ShaderGraphConstantNode(GLSLType.float, value.toString())
    fun const(value: vec2) = ShaderGraphConstantNode(GLSLType.vec2, "vec2(${value.x}, ${value.y})")
    fun const(value: vec3) = ShaderGraphConstantNode(GLSLType.vec3, "vec3(${value.x}, ${value.y}, ${value.z})")
    fun const(value: vec4) = ShaderGraphConstantNode(GLSLType.vec4, "vec4(${value.x}, ${value.y}, ${value.z}, ${value.w})")

    fun <T: ShaderVariableType> variable(variable: ShaderVariable<T>)
            = ShaderGraphVariableNode(variable)

    fun <T: ShaderVariableType> variable(name: String, type: T)
            = ShaderGraphVariableNode(ShaderVariable(type, name))

    inline infix fun <reified T: ShaderVariableType> ShaderVariable<T>.setTo(value: ShaderGraphNode<T>): ShaderGraphOutputNode<T>
            = ShaderGraphOutputNode(T::class.objectInstance ?: T::class.createInstance(), value, this)

    inline operator fun <reified T: GLSLType.numeric> ShaderGraphNode<T>.plus(other: ShaderGraphNode<T>): ShaderGraphNode<T>
            = ShaderGraphBinaryOperation(T::class.objectInstance ?: T::class.createInstance(), "%1 + %2", this, other)

    inline operator fun <reified T: GLSLType.numeric> ShaderGraphNode<T>.minus(other: ShaderGraphNode<T>): ShaderGraphNode<T>
            = ShaderGraphBinaryOperation(T::class.objectInstance ?: T::class.createInstance(), "%1 - %2", this, other)

    inline operator fun <reified T: GLSLType.numeric> ShaderGraphNode<T>.times(other: ShaderGraphNode<T>): ShaderGraphNode<T>
            = ShaderGraphBinaryOperation(T::class.objectInstance ?: T::class.createInstance(), "%1 * %2", this, other)

    fun ShaderGraphNode<GLSLType.float>.vec2() = ShaderGraphUnaryOperation(GLSLType.vec2, "vec2(%1, %1)", this)
    fun ShaderGraphNode<GLSLType.float>.vec3() = ShaderGraphUnaryOperation(GLSLType.vec3, "vec3(%1, %1, %1)", this)
    fun ShaderGraphNode<GLSLType.float>.vec4() = ShaderGraphUnaryOperation(GLSLType.vec4, "vec4(%1, %1, %1, %1)", this)
}

@Deprecated("Experimental")
class ShaderGraphBuilder(fn: ShaderGraphBuilder.() -> Unit) {
    val nodes = mutableListOf<ShaderGraphNode<*>>()

    init { fn(this) }

    fun s(fn: ShaderGraphNodeBuilder.() -> ShaderGraphNode<*>) {
        nodes.add(ShaderGraphNodeBuilder(fn).node)
    }

    fun toShaderGraph(): ShaderGraph = ShaderGraph(*nodes.toTypedArray())
}

@Deprecated("Experimental")
class Shader {
    var vertex = ShaderGraph()
    var fragment = ShaderGraph()

    companion object {
        val GL_POSITION = ShaderVariable(GLSLType.vec4, "gl_Position")
    }
}

@Deprecated("Experimental")
class NameGenerator {
    private var id = 0
    fun generate() = "__${id++}"
}

fun main() {
    val graph1 = ShaderGraphBuilder {
        s { Shader.GL_POSITION setTo (const(vec4(2f)) + const(vec4(1f))) }
    } .toShaderGraph()

    val graph2 = ShaderGraphBuilder {
        s { Shader.GL_POSITION setTo (const(5f).vec4() * variable("offset", GLSLType.vec4)) }
    } .toShaderGraph()

    graph2.addUniform("offset", GLSLType.vec4)

    val graph = graph1 joinTo graph2

    print(graph.compile())
}
