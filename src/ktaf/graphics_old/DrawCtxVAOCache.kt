
    fun circleVAO(numPoints: Int): GLVAO {
        if (circleCache.size >= MAX_CACHE_SIZE) {
            for ((k, _) in circleCache) {
                circleCache.remove(k)
                break
            }
        }

        return circleCache.computeIfAbsent(numPoints) {
            val startPoint = listOf(vec3(0f))
            val angles = (0 until numPoints).map { i -> i * PI.toFloat() * 2 / numPoints }
            val points = angles.map { mat3_rotate(it, -vec3_z) * vec3_x }

            createElementGLVAO(
                    glContext,
                    (1..numPoints).flatMap { i -> listOf(0, i, i % numPoints + 1) },
                    startPoint + points,
                    List(numPoints + 1) { vec3(0f, 0f, 1f) },
                    true
            )
        }
    }

    fun calculateCirclePointCount(radius: Float)
            = max(radius.toInt(), 3)
