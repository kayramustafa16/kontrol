package com.example.ui.components

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.MuscleGroup
import com.example.ui.theme.*
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import org.json.JSONArray

enum class ViewAngle(val titleTr: String, val yawDegrees: Float) {
    FRONT("Ön", 0f),
    BACK("Arka", 180f),
    SIDE("Yan", 90f)
}

/**
 * 3D Anatomy Viewport component rendering the real anatomy_model-v1.glb model.
 * Features:
 * - Prominent central 3D display of the real GLB model
 * - Smooth continuous 360-degree horizontal finger-drag rotation
 * - Strictly NO zoom (fixed camera distance)
 * - Quick camera preset controls (Ön, Arka, Yan)
 * - Reset view orientation button
 * - Preserved separate mesh hierarchy (48 segmented muscle parts)
 * - Bilateral chest synchronization (left & right pectorals highlight together)
 * - 100% local assets with zero runtime CDN dependencies
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AnatomyViewport3D(
    selectedMuscle: MuscleGroup,
    onMuscleSelected: (MuscleGroup) -> Unit,
    modifier: Modifier = Modifier,
    highlightedMuscles: Set<MuscleGroup> = setOf(selectedMuscle),
    customModelPath: String? = null,
    onOpenModelManager: () -> Unit = {}
) {
    var yaw by remember { mutableFloatStateOf(0f) }
    var activeAngle by remember { mutableStateOf(ViewAngle.FRONT) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var isModelLoaded by remember { mutableStateOf(false) }
    var lastHighlightedMuscle by remember { mutableStateOf<MuscleGroup?>(null) }
    val mainHandler = remember { Handler(Looper.getMainLooper()) }

    // Sync highlighted muscles into 3D meshes whenever selection changes (guards against redundant calls)
    LaunchedEffect(selectedMuscle, isModelLoaded) {
        if (!isModelLoaded) return@LaunchedEffect
        if (lastHighlightedMuscle == selectedMuscle) return@LaunchedEffect
        lastHighlightedMuscle = selectedMuscle

        val meshNames = getMeshNamesForMuscle(selectedMuscle)
        val json = JSONArray(meshNames).toString()
        webViewRef?.evaluateJavascript("window.highlightMeshes('$json', '#00E5FF');", null)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF1B2335), DarkCardSurface, DarkBackground),
                    radius = 800f
                )
            )
            .border(1.dp, DarkCardBorder, RoundedCornerShape(20.dp))
            .testTag("anatomy_3d_viewport")
    ) {
        // 1. Central 3D Model WebGL Canvas (Replaces previous 2D placeholder illustration)
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    setLayerType(View.LAYER_TYPE_HARDWARE, null)
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        allowFileAccess = true
                        allowContentAccess = true
                        allowFileAccessFromFileURLs = true
                        allowUniversalAccessFromFileURLs = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        // Strictly disable zoom gestures and controls
                        setSupportZoom(false)
                        builtInZoomControls = false
                        displayZoomControls = false
                    }

                    webChromeClient = object : WebChromeClient() {
                        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                            val msg = consoleMessage?.message() ?: ""
                            val level = consoleMessage?.messageLevel()
                            val line = consoleMessage?.lineNumber()
                            val source = consoleMessage?.sourceId()
                            when (level) {
                                ConsoleMessage.MessageLevel.ERROR -> Log.e("Anatomy3D_Console", "$msg ($source:$line)")
                                ConsoleMessage.MessageLevel.WARNING -> Log.w("Anatomy3D_Console", "$msg ($source:$line)")
                                else -> Log.d("Anatomy3D_Console", "$msg ($source:$line)")
                            }
                            return true
                        }
                    }

                    addJavascriptInterface(
                        object {
                            @JavascriptInterface
                            fun onRotationChanged(degrees: Float) {
                                mainHandler.post {
                                    yaw = degrees
                                    val normalized = ((degrees % 360f) + 360f) % 360f
                                    activeAngle = when {
                                        normalized in 45f..135f -> ViewAngle.SIDE
                                        normalized in 135f..225f -> ViewAngle.BACK
                                        else -> ViewAngle.FRONT
                                    }
                                }
                            }

                            @JavascriptInterface
                            fun onModelLoaded(count: Int, meshNamesJson: String?) {
                                Log.d("Anatomy3D_Bridge", "3D Model loaded successfully with $count meshes!")
                                mainHandler.post {
                                    isModelLoaded = true
                                    // Highlight active muscle group on initial load
                                    val meshNames = getMeshNamesForMuscle(selectedMuscle)
                                    val json = JSONArray(meshNames).toString()
                                    evaluateJavascript("window.highlightMeshes('$json', '#00E5FF');", null)
                                }
                            }

                            @JavascriptInterface
                            fun onMeshClicked(meshName: String) {
                                val mapped = mapMeshToMuscle(meshName)
                                if (mapped != null) {
                                    Log.d("Anatomy3D_Bridge", "Mesh '$meshName' tapped -> selecting $mapped")
                                    mainHandler.post {
                                        onMuscleSelected(mapped)
                                    }
                                } else {
                                    Log.d("Anatomy3D_Bridge", "Mesh '$meshName' tapped -> ignored non-muscle part")
                                }
                            }

                            @JavascriptInterface
                            fun onLoadProgress(percent: Int) {
                                Log.d("Anatomy3D_Bridge", "3D Model Load Progress: $percent%")
                            }

                            @JavascriptInterface
                            fun onModelError(error: String) {
                                Log.e("Anatomy3D_Bridge", "3D Model Error: $error")
                            }
                        },
                        "AndroidBridge"
                    )

                    webViewClient = object : WebViewClient() {
                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?
                        ) {
                            Log.e("Anatomy3D_WebView", "WebView resource error for ${request?.url}: ${error?.description}")
                        }

                        override fun shouldInterceptRequest(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): WebResourceResponse? {
                            val url = request?.url ?: return null
                            val path = url.path?.removePrefix("/") ?: return null
                            val method = request.method ?: "GET"

                            val headers = mapOf(
                                "Access-Control-Allow-Origin" to "*",
                                "Access-Control-Allow-Methods" to "GET, OPTIONS, HEAD",
                                "Access-Control-Allow-Headers" to "*"
                            )

                            if (method.equals("OPTIONS", ignoreCase = true)) {
                                return WebResourceResponse(
                                    "text/plain",
                                    "UTF-8",
                                    200,
                                    "OK",
                                    headers,
                                    ByteArrayInputStream(ByteArray(0))
                                )
                            }

                            if (path.endsWith("favicon.ico")) {
                                return WebResourceResponse(
                                    "image/x-icon",
                                    null,
                                    204,
                                    "No Content",
                                    headers,
                                    ByteArrayInputStream(ByteArray(0))
                                )
                            }

                            // Support custom uploaded model if specified
                            if (path.contains("anatomy_model-v1.glb") && customModelPath != null) {
                                try {
                                    val customFile = File(customModelPath)
                                    if (customFile.exists() && customFile.length() > 0) {
                                        val stream = FileInputStream(customFile)
                                        return WebResourceResponse("model/gltf-binary", null, 200, "OK", headers, stream)
                                    }
                                } catch (e: Exception) {
                                    Log.e("Anatomy3D_WebView", "Custom model load failed: ${e.message}")
                                }
                            }

                            val assetPath = when {
                                path.startsWith("models/") || path.startsWith("web/") -> path
                                path.endsWith(".html") || path.endsWith(".js") || path.endsWith(".css") || path.endsWith(".wasm") -> "web/$path"
                                else -> path
                            }

                            return try {
                                val mimeType = when {
                                    assetPath.endsWith(".html") -> "text/html"
                                    assetPath.endsWith(".js") -> "application/javascript"
                                    assetPath.endsWith(".css") -> "text/css"
                                    assetPath.endsWith(".glb") -> "model/gltf-binary"
                                    assetPath.endsWith(".wasm") -> "application/wasm"
                                    assetPath.endsWith(".json") -> "application/json"
                                    else -> "application/octet-stream"
                                }
                                val isBinary = assetPath.endsWith(".glb") || assetPath.endsWith(".wasm") || assetPath.endsWith(".bin")
                                val encoding = if (isBinary) null else "UTF-8"
                                val stream = ctx.assets.open(assetPath)
                                WebResourceResponse(mimeType, encoding, 200, "OK", headers, stream)
                            } catch (e: Exception) {
                                Log.e("Anatomy3D_WebView", "Failed to open asset '$assetPath': ${e.message}")
                                null
                            }
                        }
                    }

                    loadUrl("https://local.app/web/viewer.html")
                    webViewRef = this
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // 2. Top Controls: Camera Presets (Ön / Arka / Yan)
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 12.dp)
                .background(Color(0xBB111520), RoundedCornerShape(24.dp))
                .border(1.dp, Color(0xFF283145), RoundedCornerShape(24.dp))
                .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ViewAngle.entries.forEach { angle ->
                val isSelected = activeAngle == angle
                Surface(
                    onClick = {
                        activeAngle = angle
                        yaw = angle.yawDegrees
                        webViewRef?.evaluateJavascript("window.setRotationDegrees(${angle.yawDegrees});", null)
                    },
                    shape = RoundedCornerShape(18.dp),
                    color = if (isSelected) ElectricCyan else Color.Transparent,
                    modifier = Modifier.testTag("angle_${angle.name.lowercase()}")
                ) {
                    Text(
                        text = angle.titleTr,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        color = if (isSelected) Color.Black else TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // 3. Right Side 3D HUD Tool Buttons (3D Badge & Orientation Reset)
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 3D Mode Badge
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xCC1A233A), CircleShape)
                    .border(1.dp, ElectricCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "3D",
                    color = ElectricCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Reset Orientation Button
            IconButton(
                onClick = {
                    yaw = 0f
                    activeAngle = ViewAngle.FRONT
                    webViewRef?.evaluateJavascript("window.resetOrientation();", null)
                },
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xCC131926), CircleShape)
                    .border(1.dp, DarkCardBorder, CircleShape)
                    .testTag("reset_3d_orientation")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Görünümü Sıfırla",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Maps logical muscle groups to individual mesh names within the GLB model.
 * Bilateral synchronization: Left and right pectorals map together to CHEST.
 * Mappings are strictly 1-to-1 / non-overlapping to prevent accidental cross-highlighting.
 */
internal fun getMeshNamesForMuscle(muscle: MuscleGroup): List<String> {
    return when (muscle) {
        MuscleGroup.CHEST -> listOf(
            "tripo_part_69", // Left Pectoralis Major
            "tripo_part_77"  // Right Pectoralis Major
        )
        MuscleGroup.ABS -> listOf(
            "tripo_part_67", // Rectus Abdominis (Center 6-pack)
            "tripo_part_43", // Left Oblique
            "tripo_part_68"  // Right Oblique
        )
        MuscleGroup.SHOULDERS -> listOf(
            "tripo_part_80", // Left Deltoid
            "tripo_part_44"  // Right Deltoid
        )
        MuscleGroup.BICEPS -> listOf(
            "tripo_part_56", "tripo_part_57", "tripo_part_58", "tripo_part_75", "tripo_part_54"
        )
        MuscleGroup.TRICEPS -> listOf(
            "tripo_part_52", "tripo_part_46", "tripo_part_22", "tripo_part_70", "tripo_part_41", "tripo_part_50"
        )
        MuscleGroup.BACK -> listOf(
            "tripo_part_45", // Upper Trapezius
            "tripo_part_11", // Middle Latissimus Dorsi
            "tripo_part_79", // Right Latissimus
            "tripo_part_32"  // Lower Back / Lat
        )
        MuscleGroup.GLUTES -> listOf(
            "tripo_part_5"   // Gluteal musculature & pelvis
        )
        MuscleGroup.QUADS -> listOf(
            "tripo_part_19"  // Upper front thigh (Quadriceps)
        )
        MuscleGroup.HAMSTRINGS -> listOf(
            "tripo_part_37"  // Lower thigh & posterior leg (Hamstrings)
        )
        MuscleGroup.CALVES -> listOf(
            "tripo_part_31", // Right Calf
            "tripo_part_72"  // Left/Achilles Calf
        )
    }
}

/**
 * Translates tapped 3D mesh names back to high-level muscle groups.
 * Strict, exact matching prevents taps on background or unrelated tissues from wrongly triggering muscle groups.
 */
internal fun mapMeshToMuscle(meshName: String): MuscleGroup? {
    return when (meshName) {
        // Bilateral CHEST: tapping either pectoral selects CHEST
        "tripo_part_69", "tripo_part_77", "Pectoralis_Major_L", "Pectoralis_Major_R" -> MuscleGroup.CHEST

        // ABS (strictly abdominal wall & obliques, excluded connective base shell tripo_part_73)
        "tripo_part_67", "tripo_part_43", "tripo_part_68",
        "Rectus_Abdominis", "External_Obliques_L", "External_Obliques_R" -> MuscleGroup.ABS

        // SHOULDERS (Deltoids)
        "tripo_part_80", "tripo_part_44",
        "Deltoid_Anterior_L", "Deltoid_Anterior_R", "Deltoid_Lateral_L", "Deltoid_Lateral_R",
        "Deltoid_Posterior_L", "Deltoid_Posterior_R" -> MuscleGroup.SHOULDERS

        // BICEPS (Anterior arms)
        "tripo_part_56", "tripo_part_57", "tripo_part_58", "tripo_part_75", "tripo_part_54",
        "Biceps_Brachii_L", "Biceps_Brachii_R" -> MuscleGroup.BICEPS

        // TRICEPS (Posterior arms)
        "tripo_part_52", "tripo_part_46", "tripo_part_22", "tripo_part_70", "tripo_part_41", "tripo_part_50",
        "Triceps_Brachii_L", "Triceps_Brachii_R" -> MuscleGroup.TRICEPS

        // BACK (Trapezius & Latissimus)
        "tripo_part_45", "tripo_part_11", "tripo_part_79", "tripo_part_32",
        "Latissimus_Dorsi_L", "Latissimus_Dorsi_R",
        "Trapezius_Upper", "Trapezius_Middle", "Trapezius_Lower" -> MuscleGroup.BACK

        // GLUTES
        "tripo_part_5", "Gluteus_Maximus_L", "Gluteus_Maximus_R" -> MuscleGroup.GLUTES

        // QUADS
        "tripo_part_19", "Quadriceps_Femoris_L", "Quadriceps_Femoris_R" -> MuscleGroup.QUADS

        // HAMSTRINGS
        "tripo_part_37", "Biceps_Femoris_L", "Biceps_Femoris_R" -> MuscleGroup.HAMSTRINGS

        // CALVES
        "tripo_part_31", "tripo_part_72", "Gastrocnemius_L", "Gastrocnemius_R" -> MuscleGroup.CALVES

        // Head, neck, feet, hands, and connective base shells are non-muscle meshes
        else -> null
    }
}
