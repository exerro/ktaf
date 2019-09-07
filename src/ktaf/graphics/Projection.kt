package ktaf.graphics

import geometry.*

class Projection(
        internal val matrix: (Int, Int) -> mat4
) {
    companion object {
        fun identity() = Projection { _, _ -> mat4_identity }

        fun screen() = Projection { w, h -> mat4_identity *
                mat4_translate(vec3(-1f, 1f, -1f)) *
                mat3_scale(vec3(2f/w, -2f/h, 0f)).mat4()
        }

        fun perspective() = Projection { w, h ->
            TODO()
        }
    }
}
