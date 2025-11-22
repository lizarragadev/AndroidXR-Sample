package tech.lizza.demoxr.ar

import android.Manifest
import android.content.pm.PackageManager
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.ar.core.*
import com.google.ar.core.exceptions.CameraNotAvailableException
import tech.lizza.demoxr.ar.rendering.BackgroundRenderer
import tech.lizza.demoxr.ar.rendering.ObjectRenderer
import tech.lizza.demoxr.ar.rendering.PlaneRenderer
import tech.lizza.demoxr.ui.theme.DemoXRTheme
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ModernARMapActivity : ComponentActivity(), GLSurfaceView.Renderer {
    
    companion object {
        private const val TAG = "ARActivity"
        private const val MAX_FRAME_AGE_MS = 100L
    }
    
    private lateinit var arSession: Session
    private lateinit var surfaceView: GLSurfaceView
    private val anchors = mutableListOf<Anchor>()
    
    private lateinit var backgroundRenderer: BackgroundRenderer
    private lateinit var planeRenderer: PlaneRenderer
    private lateinit var objectRenderer: ObjectRenderer
    
    var showPlanes = true
        private set
    
    private var lastFrame: Frame? = null
    private var frameTimestamp: Long = 0
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            initializeAR()
        } else {
            showToast("Se necesita permiso de cámara para AR")
            finish()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                initializeAR()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
    
    private fun initializeAR() {
        val availability = ArCoreApk.getInstance().checkAvailability(this)
        
        if (!availability.isSupported) {
            showToast("ARCore no está disponible")
            finish()
            return
        }
        
        try {
            arSession = Session(this).apply {
                configure(Config(this).apply {
                    planeFindingMode = Config.PlaneFindingMode.HORIZONTAL
                    lightEstimationMode = Config.LightEstimationMode.AMBIENT_INTENSITY
                    updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
                })
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creando ARCore Session", e)
            showToast("Error: ${e.message}")
            finish()
            return
        }
        
        surfaceView = GLSurfaceView(this).apply {
            preserveEGLContextOnPause = true
            setEGLContextClientVersion(2)
            setEGLConfigChooser(8, 8, 8, 8, 16, 0)
            setRenderer(this@ModernARMapActivity)
            renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            setOnTouchListener { _, event ->
                handleTap(event)
                true
            }
        }
        
        val frameLayout = android.widget.FrameLayout(this).apply {
            addView(surfaceView, android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT
            ))
            
            val composeView = androidx.compose.ui.platform.ComposeView(this@ModernARMapActivity).apply {
                setContent {
                    DemoXRTheme {
                        AROverlay(
                            onBackPressed = { finish() },
                            onTogglePlanes = { togglePlanes() }
                        )
                    }
                }
                isClickable = false
                isFocusable = false
                isFocusableInTouchMode = false
            }
            
            addView(composeView, android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT
            ))
        }
        
        setContentView(frameLayout)
        showToast("ARCore inicializado")
    }
    
    private fun togglePlanes() {
        showPlanes = !showPlanes
    }
    
    private fun handleTap(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_UP) return false
        
        val frame = lastFrame ?: run {
            showToast("Espera un momento...")
            return false
        }
        
        if (System.currentTimeMillis() - frameTimestamp > MAX_FRAME_AGE_MS) {
            showToast("Mueve el teléfono y toca de nuevo")
            return false
        }
        
        try {
            val hits = frame.hitTest(event.x, event.y)
            
            if (hits.isEmpty()) {
                showToast("No hay superficie aquí")
                return false
            }
            
            for (hit in hits) {
                val trackable = hit.trackable
                if (trackable is Plane && trackable.isPoseInPolygon(hit.hitPose)) {
                    anchors.add(hit.createAnchor())
                    showToast("Robot ${anchors.size} colocado!")
                    return true
                }
            }
            
            showToast("Toca sobre una superficie plana")
        } catch (e: Exception) {
            Log.e(TAG, "Error en handleTap", e)
            showToast("Error: ${e.message}")
        }
        
        return false
    }
    
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
        
        try {
            backgroundRenderer = BackgroundRenderer().apply { createOnGlThread(this@ModernARMapActivity) }
            planeRenderer = PlaneRenderer().apply { createOnGlThread(this@ModernARMapActivity) }
            objectRenderer = ObjectRenderer().apply { createOnGlThread(this@ModernARMapActivity) }
            arSession.setCameraTextureName(backgroundRenderer.getTextureId())
        } catch (e: Exception) {
            Log.e(TAG, "Error creando renderers", e)
        }
    }
    
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        
        val displayRotation = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            display?.rotation ?: 0
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.rotation
        }
        
        arSession.setDisplayGeometry(displayRotation, width, height)
    }
    
    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        
        try {
            arSession.setCameraTextureName(backgroundRenderer.getTextureId())
            val frame = arSession.update()
            lastFrame = frame
            frameTimestamp = System.currentTimeMillis()
            
            if (frame.camera.trackingState != TrackingState.TRACKING) return
            
            backgroundRenderer.draw(frame)
            
            if (showPlanes) {
                planeRenderer.drawPlanes(
                    arSession.getAllTrackables(Plane::class.java),
                    frame.camera
                )
            }
            
            objectRenderer.draw(anchors, frame.camera)
            
        } catch (e: CameraNotAvailableException) {
            Log.e(TAG, "Cámara no disponible", e)
        } catch (e: Exception) {
            Log.e(TAG, "Error en onDrawFrame", e)
        }
    }
    
    override fun onResume() {
        super.onResume()
        
        if (!::arSession.isInitialized) return
        
        try {
            arSession.resume()
            surfaceView.onResume()
        } catch (e: CameraNotAvailableException) {
            Log.e(TAG, "Cámara no disponible", e)
            showToast("No se puede acceder a la cámara")
            finish()
        }
    }
    
    override fun onPause() {
        super.onPause()
        if (::surfaceView.isInitialized) surfaceView.onPause()
        if (::arSession.isInitialized) arSession.pause()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        if (::arSession.isInitialized) arSession.close()
    }
    
    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun AROverlay(
    onBackPressed: () -> Unit,
    onTogglePlanes: () -> Unit
) {
    var showPlanes by remember { mutableStateOf(true) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.7f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🎯 ARCore",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "1️⃣ Mueve el teléfono lentamente",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF90CAF9)
                )
                Text(
                    text = "2️⃣ Aparecerán planos celestes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4DD0E1)
                )
                Text(
                    text = "3️⃣ Toca en cualquier parte de la pantalla",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFFFC107)
                )
                Text(
                    text = "4️⃣ Aparecerá el robot Android 3D",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF8BC34A)
                )
            }
        }
        
        Button(
            onClick = onBackPressed,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 80.dp, start = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF44336).copy(alpha = 0.9f)
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "⬅️", style = MaterialTheme.typography.titleMedium)
                Text(text = "Salir", style = MaterialTheme.typography.titleMedium)
            }
        }
        
        Button(
            onClick = { 
                showPlanes = !showPlanes
                onTogglePlanes()
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 80.dp, end = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (showPlanes) Color(0xFF4CAF50).copy(alpha = 0.9f) else Color(0xFF9E9E9E).copy(alpha = 0.9f)
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = if (showPlanes) "👁️" else "🚫",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (showPlanes) "Ocultar" else "Mostrar",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
