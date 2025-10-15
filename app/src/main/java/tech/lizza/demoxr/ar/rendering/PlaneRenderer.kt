package tech.lizza.demoxr.ar.rendering

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import com.google.ar.core.Camera
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import java.nio.ByteBuffer
import java.nio.ByteOrder

class PlaneRenderer {
    private var planeProgram = 0
    private var planePositionParam = 0
    private var planeModelViewProjectionUniform = 0
    private var planeColorUniform = 0
    
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val modelViewMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)
    
    fun createOnGlThread(context: Context) {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER)
        
        planeProgram = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
            GLES20.glUseProgram(it)
        }
        
        planePositionParam = GLES20.glGetAttribLocation(planeProgram, "a_Position")
        planeModelViewProjectionUniform = GLES20.glGetUniformLocation(planeProgram, "u_ModelViewProjection")
        planeColorUniform = GLES20.glGetUniformLocation(planeProgram, "u_Color")
    }
    
    fun drawPlanes(allPlanes: Collection<Plane>, camera: Camera) {
        if (allPlanes.isEmpty()) return
        
        camera.getViewMatrix(viewMatrix, 0)
        camera.getProjectionMatrix(projectionMatrix, 0, 0.1f, 100f)
        
        GLES20.glUseProgram(planeProgram)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        
        for (plane in allPlanes) {
            if (plane.trackingState != TrackingState.TRACKING || plane.polygon.limit() / 2 < 3) continue
            
            val vertexBuffer = ByteBuffer.allocateDirect(plane.polygon.limit() * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .apply {
                    plane.polygon.rewind()
                    put(plane.polygon)
                    rewind()
                }
            
            plane.centerPose.toMatrix(modelMatrix, 0)
            Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0)
            Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0)
            
            GLES20.glUniform4f(planeColorUniform, 0.3f, 0.7f, 1.0f, 0.4f)
            GLES20.glUniformMatrix4fv(planeModelViewProjectionUniform, 1, false, modelViewProjectionMatrix, 0)
            
            GLES20.glVertexAttribPointer(planePositionParam, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer)
            GLES20.glEnableVertexAttribArray(planePositionParam)
            
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, plane.polygon.limit() / 2)
            
            GLES20.glDisableVertexAttribArray(planePositionParam)
        }
        
        GLES20.glDisable(GLES20.GL_BLEND)
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
            attribute vec2 a_Position;
            void main() {
                gl_Position = u_ModelViewProjection * vec4(a_Position.x, 0.0, a_Position.y, 1.0);
            }
        """
        
        private const val FRAGMENT_SHADER = """
            precision mediump float;
            uniform vec4 u_Color;
            void main() {
                gl_FragColor = u_Color;
            }
        """
    }
}
