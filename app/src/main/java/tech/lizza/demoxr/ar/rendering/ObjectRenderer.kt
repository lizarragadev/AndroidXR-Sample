package tech.lizza.demoxr.ar.rendering

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import com.google.ar.core.Anchor
import com.google.ar.core.Camera
import com.google.ar.core.TrackingState
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

class ObjectRenderer {
    private var program = 0
    private var positionParam = 0
    private var colorParam = 0
    private var modelViewProjectionUniform = 0
    
    private lateinit var androidVertices: FloatBuffer
    private lateinit var androidColors: FloatBuffer
    private var vertexCount = 0
    
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val modelViewMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)
    
    private val ANDROID_GREEN = floatArrayOf(0.55f, 0.85f, 0.25f, 1.0f)
    private val ANDROID_DARK_GREEN = floatArrayOf(0.45f, 0.75f, 0.15f, 1.0f)
    private val ANDROID_LIGHT_GREEN = floatArrayOf(0.65f, 0.95f, 0.35f, 1.0f)
    private val WHITE = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
    
    private fun generateCylinder(
        centerX: Float, centerY: Float, centerZ: Float,
        radius: Float, height: Float, segments: Int = 12
    ): List<Float> {
        val vertices = mutableListOf<Float>()
        
        for (i in 0 until segments) {
            val angle1 = (i.toFloat() / segments) * 2 * PI.toFloat()
            val angle2 = ((i + 1).toFloat() / segments) * 2 * PI.toFloat()
            
            val x1 = centerX + radius * cos(angle1)
            val z1 = centerZ + radius * sin(angle1)
            val x2 = centerX + radius * cos(angle2)
            val z2 = centerZ + radius * sin(angle2)
            
            vertices.addAll(listOf(x1, centerY, z1))
            vertices.addAll(listOf(x2, centerY, z2))
            vertices.addAll(listOf(x1, centerY + height, z1))
            
            vertices.addAll(listOf(x2, centerY, z2))
            vertices.addAll(listOf(x2, centerY + height, z2))
            vertices.addAll(listOf(x1, centerY + height, z1))
            
            vertices.addAll(listOf(centerX, centerY + height, centerZ))
            vertices.addAll(listOf(x1, centerY + height, z1))
            vertices.addAll(listOf(x2, centerY + height, z2))
            
            vertices.addAll(listOf(centerX, centerY, centerZ))
            vertices.addAll(listOf(x2, centerY, z2))
            vertices.addAll(listOf(x1, centerY, z1))
        }
        
        return vertices
    }
    
    private fun generateSphere(
        centerX: Float, centerY: Float, centerZ: Float,
        radius: Float, segments: Int = 8
    ): List<Float> {
        val vertices = mutableListOf<Float>()
        
        for (lat in 0 until segments) {
            val theta1 = (lat.toFloat() / segments) * PI.toFloat()
            val theta2 = ((lat + 1).toFloat() / segments) * PI.toFloat()
            
            for (lon in 0 until segments * 2) {
                val phi1 = (lon.toFloat() / (segments * 2)) * 2 * PI.toFloat()
                val phi2 = ((lon + 1).toFloat() / (segments * 2)) * 2 * PI.toFloat()
                
                val x1 = centerX + radius * sin(theta1) * cos(phi1)
                val y1 = centerY + radius * cos(theta1)
                val z1 = centerZ + radius * sin(theta1) * sin(phi1)
                
                val x2 = centerX + radius * sin(theta1) * cos(phi2)
                val y2 = centerY + radius * cos(theta1)
                val z2 = centerZ + radius * sin(theta1) * sin(phi2)
                
                val x3 = centerX + radius * sin(theta2) * cos(phi1)
                val y3 = centerY + radius * cos(theta2)
                val z3 = centerZ + radius * sin(theta2) * sin(phi1)
                
                val x4 = centerX + radius * sin(theta2) * cos(phi2)
                val y4 = centerY + radius * cos(theta2)
                val z4 = centerZ + radius * sin(theta2) * sin(phi2)
                
                vertices.addAll(listOf(x1, y1, z1, x2, y2, z2, x3, y3, z3))
                vertices.addAll(listOf(x2, y2, z2, x4, y4, z4, x3, y3, z3))
            }
        }
        
        return vertices
    }
    
    private fun generateInclinedCylinder(
        startX: Float, startY: Float, startZ: Float,
        endX: Float, endY: Float, endZ: Float,
        radius: Float, segments: Int = 8
    ): List<Float> {
        val vertices = mutableListOf<Float>()
        
        for (i in 0 until segments) {
            val angle1 = (i.toFloat() / segments) * 2 * PI.toFloat()
            val angle2 = ((i + 1).toFloat() / segments) * 2 * PI.toFloat()
            
            val x1Bottom = startX + radius * cos(angle1)
            val z1Bottom = startZ + radius * sin(angle1)
            val x2Bottom = startX + radius * cos(angle2)
            val z2Bottom = startZ + radius * sin(angle2)
            
            val x1Top = endX + radius * cos(angle1)
            val z1Top = endZ + radius * sin(angle1)
            val x2Top = endX + radius * cos(angle2)
            val z2Top = endZ + radius * sin(angle2)
            
            vertices.addAll(listOf(x1Bottom, startY, z1Bottom, x2Bottom, startY, z2Bottom, x1Top, endY, z1Top))
            vertices.addAll(listOf(x2Bottom, startY, z2Bottom, x2Top, endY, z2Top, x1Top, endY, z1Top))
        }
        
        return vertices
    }
    
    private fun generateSphereHalf(
        centerX: Float, centerY: Float, centerZ: Float,
        radius: Float, segments: Int = 8
    ): List<Float> {
        val vertices = mutableListOf<Float>()
        
        for (lat in 0 until segments / 2) {
            val theta1 = (lat.toFloat() / segments) * PI.toFloat()
            val theta2 = ((lat + 1).toFloat() / segments) * PI.toFloat()
            
            for (lon in 0 until segments * 2) {
                val phi1 = (lon.toFloat() / (segments * 2)) * 2 * PI.toFloat()
                val phi2 = ((lon + 1).toFloat() / (segments * 2)) * 2 * PI.toFloat()
                
                val x1 = centerX + radius * sin(theta1) * cos(phi1)
                val y1 = centerY + radius * cos(theta1)
                val z1 = centerZ + radius * sin(theta1) * sin(phi1)
                
                val x2 = centerX + radius * sin(theta1) * cos(phi2)
                val y2 = centerY + radius * cos(theta1)
                val z2 = centerZ + radius * sin(theta1) * sin(phi2)
                
                val x3 = centerX + radius * sin(theta2) * cos(phi1)
                val y3 = centerY + radius * cos(theta2)
                val z3 = centerZ + radius * sin(theta2) * sin(phi1)
                
                val x4 = centerX + radius * sin(theta2) * cos(phi2)
                val y4 = centerY + radius * cos(theta2)
                val z4 = centerZ + radius * sin(theta2) * sin(phi2)
                
                vertices.addAll(listOf(x1, y1, z1, x2, y2, z2, x3, y3, z3))
                vertices.addAll(listOf(x2, y2, z2, x4, y4, z4, x3, y3, z3))
            }
        }
        
        for (lon in 0 until segments * 2) {
            val phi1 = (lon.toFloat() / (segments * 2)) * 2 * PI.toFloat()
            val phi2 = ((lon + 1).toFloat() / (segments * 2)) * 2 * PI.toFloat()
            
            vertices.addAll(listOf(
                centerX + radius * cos(phi1), centerY, centerZ + radius * sin(phi1),
                centerX + radius * cos(phi2), centerY, centerZ + radius * sin(phi2),
                centerX, centerY, centerZ
            ))
        }
        
        return vertices
    }
    
    private fun buildAndroidRobot(): Pair<FloatArray, FloatArray> {
        val vertices = mutableListOf<Float>()
        val colors = mutableListOf<Float>()
        
        fun addPart(partVertices: List<Float>, color: FloatArray) {
            vertices.addAll(partVertices)
            repeat(partVertices.size / 3) { colors.addAll(color.toList()) }
        }
        
        addPart(generateSphereHalf(0f, 0.39f, 0f, 0.13f, 32), ANDROID_LIGHT_GREEN)
        addPart(generateCylinder(0f, 0.16f, 0f, 0.13f, 0.22f, 32), ANDROID_GREEN)
        addPart(generateCylinder(-0.18f, 0.23f, 0f, 0.035f, 0.10f, 32), ANDROID_GREEN)
        addPart(generateSphere(-0.18f, 0.34f, 0f, 0.035f, 24), ANDROID_GREEN)
        addPart(generateSphere(-0.18f, 0.24f, 0f, 0.035f, 24), ANDROID_GREEN)
        addPart(generateCylinder(0.18f, 0.23f, 0f, 0.035f, 0.10f, 32), ANDROID_GREEN)
        addPart(generateSphere(0.18f, 0.34f, 0f, 0.035f, 24), ANDROID_GREEN)
        addPart(generateSphere(0.18f, 0.24f, 0f, 0.035f, 24), ANDROID_GREEN)
        addPart(generateCylinder(-0.07f, 0.04f, 0f, 0.045f, 0.16f, 32), ANDROID_DARK_GREEN)
        addPart(generateCylinder(0.07f, 0.04f, 0f, 0.045f, 0.16f, 32), ANDROID_DARK_GREEN)
        addPart(generateInclinedCylinder(-0.06f, 0.51f, 0f, -0.10f, 0.56f, 0f, 0.010f, 24), ANDROID_LIGHT_GREEN)
        addPart(generateInclinedCylinder(0.06f, 0.51f, 0f, 0.10f, 0.56f, 0f, 0.010f, 24), ANDROID_LIGHT_GREEN)
        addPart(generateSphere(-0.05f, 0.45f, 0.12f, 0.016f, 24), WHITE)
        addPart(generateSphere(0.05f, 0.45f, 0.12f, 0.016f, 24), WHITE)
        
        return Pair(vertices.toFloatArray(), colors.toFloatArray())
    }
    
    fun createOnGlThread(context: Context) {
        val (vertices, colors) = buildAndroidRobot()
        vertexCount = vertices.size / 3
        
        androidVertices = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(vertices)
                position(0)
            }
        
        androidColors = ByteBuffer.allocateDirect(colors.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(colors)
                position(0)
            }
        
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER)
        
        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
            GLES20.glUseProgram(it)
        }
        
        positionParam = GLES20.glGetAttribLocation(program, "a_Position")
        colorParam = GLES20.glGetAttribLocation(program, "a_Color")
        modelViewProjectionUniform = GLES20.glGetUniformLocation(program, "u_ModelViewProjection")
    }
    
    fun draw(anchors: List<Anchor>, camera: Camera) {
        if (anchors.isEmpty()) return
        
        camera.getViewMatrix(viewMatrix, 0)
        camera.getProjectionMatrix(projectionMatrix, 0, 0.1f, 100f)
        
        GLES20.glUseProgram(program)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        
        for (anchor in anchors) {
            if (anchor.trackingState != TrackingState.TRACKING) continue
            
            anchor.pose.toMatrix(modelMatrix, 0)
            Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0)
            Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0)
            
            GLES20.glUniformMatrix4fv(modelViewProjectionUniform, 1, false, modelViewProjectionMatrix, 0)
            
            GLES20.glVertexAttribPointer(positionParam, 3, GLES20.GL_FLOAT, false, 0, androidVertices)
            GLES20.glVertexAttribPointer(colorParam, 4, GLES20.GL_FLOAT, false, 0, androidColors)
            
            GLES20.glEnableVertexAttribArray(positionParam)
            GLES20.glEnableVertexAttribArray(colorParam)
            
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)
            
            GLES20.glDisableVertexAttribArray(positionParam)
            GLES20.glDisableVertexAttribArray(colorParam)
        }
    }
    
    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }
    
    companion object {
        private const val VERTEX_SHADER = """
            uniform mat4 u_ModelViewProjection;
            attribute vec3 a_Position;
            attribute vec4 a_Color;
            varying vec4 v_Color;
            void main() {
                v_Color = a_Color;
                gl_Position = u_ModelViewProjection * vec4(a_Position, 1.0);
            }
        """
        
        private const val FRAGMENT_SHADER = """
            precision mediump float;
            varying vec4 v_Color;
            void main() {
                gl_FragColor = v_Color;
            }
        """
    }
}
