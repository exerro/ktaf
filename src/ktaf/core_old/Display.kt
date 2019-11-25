package ktaf.core_old

private fun keyModifiers(mods: Int): Set<GLFWKeyModifier> = setOf(
        GLFWKeyModifier.CTRL.takeIf { mods and GLFW.GLFW_MOD_CONTROL != 0 },
        GLFWKeyModifier.ALT.takeIf { mods and GLFW.GLFW_MOD_ALT != 0 },
        GLFWKeyModifier.SHIFT.takeIf { mods and GLFW.GLFW_MOD_SHIFT != 0 },
        GLFWKeyModifier.SUPER.takeIf { mods and GLFW.GLFW_MOD_SUPER != 0 }
) .filterNotNull().toSet()

private fun mouseModifiers(mods: Int): Set<GLFWMouseModifier> = setOf(
        GLFWMouseModifier.CTRL.takeIf { mods and GLFW.GLFW_MOD_CONTROL != 0 },
        GLFWMouseModifier.ALT.takeIf { mods and GLFW.GLFW_MOD_ALT != 0 },
        GLFWMouseModifier.SHIFT.takeIf { mods and GLFW.GLFW_MOD_SHIFT != 0 },
        GLFWMouseModifier.SUPER.takeIf { mods and GLFW.GLFW_MOD_SUPER != 0 }
) .filterNotNull().toSet()
