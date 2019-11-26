package ktaf.graphics_old

data class ShaderUniform(val name: String, val type: String)

class FragmentShader internal constructor(internal val code: String, vararg uniforms: ShaderUniform) {
    internal val uniforms = uniforms.toList()

    companion object {
        fun create(code: String, vararg uniforms: ShaderUniform): FragmentShader {
            return FragmentShader(code, *uniforms)
        }
    }
}

fun List<FragmentShader>.compile(): String {
    var outputIndex = 0
    val uniforms = fold(listOf<ShaderUniform>()) { a, b -> a + b.uniforms }
    val initialText = "$KTAF_COLOUR_OUT = $FRAGMENT_COLOUR_IN;\n" +
            "if (useTexture) $KTAF_COLOUR_OUT *= texture(texture_sampler, $FRAGMENT_UV_IN);"
    val body = (1 .. size).joinToString("") { "vec4 ktaf_gen_colour$it;\n\t" } +
    reversed().fold(initialText) { prev, next ->
        outputIndex++
        val colourVariable = "ktaf_gen_colour${outputIndex}"
        prev.replace(KTAF_COLOUR_OUT, colourVariable) + "\n\t" +
        next.code.replace(KTAF_COLOUR_IN, colourVariable)
    }

    return FRAGMENT_SHADER_TEMPLATE
            .replace("%UNIFORMS", uniforms.joinToString("\n") {
                "uniform ${it.type} ${it.name};"
            })
            .replace("%BODY", body
                    .replace(KTAF_COLOUR_OUT, FRAGMENT_COLOUR_OUT)
                    .replace(KTAF_POSITION_IN, FRAGMENT_POSITION_IN)
                    .replace(KTAF_NORMAL_IN, FRAGMENT_NORMAL_IN)
                    .replace(KTAF_UV_IN, FRAGMENT_UV_IN)
            )
}

private const val KTAF_POSITION_IN = "ktaf_position"
private const val KTAF_NORMAL_IN = "ktaf_normal"
private const val KTAF_COLOUR_IN = "ktaf_colour"
private const val KTAF_UV_IN = "ktaf_uv"

private const val KTAF_COLOUR_OUT = "ktaf_fragment_colour"

private const val FRAGMENT_POSITION_IN = "fragment_position"
private const val FRAGMENT_NORMAL_IN = "fragment_normal"
private const val FRAGMENT_COLOUR_IN = "fragment_colour"
private const val FRAGMENT_UV_IN = "fragment_uv"

private const val FRAGMENT_COLOUR_OUT = "fragment_colour_out"

private const val FRAGMENT_SHADER_TEMPLATE = """#version 440 core
    
in vec3 $FRAGMENT_POSITION_IN;
in vec3 $FRAGMENT_NORMAL_IN;
in vec4 $FRAGMENT_COLOUR_IN;
in vec2 $FRAGMENT_UV_IN;

out vec4 $FRAGMENT_COLOUR_OUT;

uniform sampler2D texture_sampler;
uniform bool useTexture;
    
%UNIFORMS

void main(void) {
    %BODY
}
"""
