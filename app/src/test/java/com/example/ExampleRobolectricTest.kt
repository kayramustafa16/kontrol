package com.example

import android.content.Context
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.MuscleGroup
import com.example.ui.components.getMeshNamesForMuscle
import com.example.ui.components.mapMeshToMuscle
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Anabolik Atlas", appName)
  }

  @Test
  fun `verify anatomy_model-v1 glb asset exists and is valid glTF binary`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val assetManager = context.assets

    val inputStream = assetManager.open("models/anatomy_model-v1.glb")
    assertNotNull("anatomy_model-v1.glb must exist in assets/models/", inputStream)

    val headerBytes = ByteArray(12)
    val readBytes = inputStream.read(headerBytes)
    assertEquals("Must read 12-byte glTF header", 12, readBytes)

    val buffer = ByteBuffer.wrap(headerBytes).order(ByteOrder.LITTLE_ENDIAN)
    val magic = buffer.int
    val version = buffer.int
    val length = buffer.int

    // 0x46546C67 is ascii for 'glTF' in little-endian
    assertEquals("GLB magic header must be 'glTF'", 0x46546C67, magic)
    assertEquals("GLB version must be 2", 2, version)
    assertTrue("GLB length must exceed 10MB", length > 10_000_000)

    inputStream.close()
  }

  @Test
  fun `verify local 3d webview assets and draco decoders exist without cdn dependencies`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val assetManager = context.assets

    // 1. viewer.html
    val viewerStream = assetManager.open("web/viewer.html")
    assertNotNull("viewer.html must exist in assets/web/", viewerStream)
    val htmlContent = viewerStream.bufferedReader().use { it.readText() }
    assertTrue("viewer.html must include THREE.DRACOLoader", htmlContent.contains("DRACOLoader"))
    assertTrue("viewer.html must specify local draco decoder path", htmlContent.contains("/web/draco/"))
    viewerStream.close()

    // 2. three_bundle.min.js
    val threeStream = assetManager.open("web/three_bundle.min.js")
    assertNotNull("three_bundle.min.js must exist in assets/web/", threeStream)
    val jsContent = threeStream.bufferedReader().use { it.readText() }
    assertTrue("bundle must have GLTFLoader", jsContent.contains("GLTFLoader"))
    assertTrue("bundle must have DRACOLoader", jsContent.contains("DRACOLoader"))
    threeStream.close()

    // 3. Local Draco decoders
    val wasmStream = assetManager.open("web/draco/draco_decoder.wasm")
    assertNotNull("draco_decoder.wasm must exist", wasmStream)
    val wasmHeader = ByteArray(4)
    wasmStream.read(wasmHeader)
    // WASM magic: 0x00, 0x61, 0x73, 0x6D (\0asm)
    assertEquals(0x00.toByte(), wasmHeader[0])
    assertEquals('a'.code.toByte(), wasmHeader[1])
    assertEquals('s'.code.toByte(), wasmHeader[2])
    assertEquals('m'.code.toByte(), wasmHeader[3])
    wasmStream.close()

    val wasmWrapperStream = assetManager.open("web/draco/draco_wasm_wrapper.js")
    assertNotNull("draco_wasm_wrapper.js must exist", wasmWrapperStream)
    assertTrue(wasmWrapperStream.available() > 10_000)
    wasmWrapperStream.close()

    val jsDecoderStream = assetManager.open("web/draco/draco_decoder.js")
    assertNotNull("draco_decoder.js must exist", jsDecoderStream)
    assertTrue(jsDecoderStream.available() > 100_000)
    jsDecoderStream.close()
  }

  @Test
  fun `verify webview request interception serves correct binary and text mime types and encodings`() {
    val context = ApplicationProvider.getApplicationContext<Context>()

    fun simulateIntercept(urlStr: String, method: String = "GET"): WebResourceResponse? {
      val uri = Uri.parse(urlStr)
      val path = uri.path?.removePrefix("/") ?: return null

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
        val stream = context.assets.open(assetPath)
        WebResourceResponse(mimeType, encoding, 200, "OK", headers, stream)
      } catch (e: Exception) {
        null
      }
    }

    // 1. GLB binary model request
    val glbResp = simulateIntercept("https://local.app/models/anatomy_model-v1.glb")
    assertNotNull("GLB response must not be null", glbResp)
    assertEquals("model/gltf-binary", glbResp?.mimeType)
    assertNull("Binary GLB encoding must be null (not UTF-8) to prevent byte corruption", glbResp?.encoding)
    assertEquals(200, glbResp?.statusCode)

    val glbHeader = ByteArray(12)
    glbResp?.data?.read(glbHeader)
    val glbBuffer = ByteBuffer.wrap(glbHeader).order(ByteOrder.LITTLE_ENDIAN)
    assertEquals(0x46546C67, glbBuffer.int) // 'glTF'
    glbResp?.data?.close()

    // 2. Draco WASM binary request
    val wasmResp = simulateIntercept("https://local.app/web/draco/draco_decoder.wasm")
    assertNotNull("WASM response must not be null", wasmResp)
    assertEquals("application/wasm", wasmResp?.mimeType)
    assertNull("Binary WASM encoding must be null", wasmResp?.encoding)
    assertEquals(200, wasmResp?.statusCode)
    wasmResp?.data?.close()

    // 3. Three bundle script request
    val jsResp = simulateIntercept("https://local.app/web/three_bundle.min.js")
    assertNotNull(jsResp)
    assertEquals("application/javascript", jsResp?.mimeType)
    assertEquals("UTF-8", jsResp?.encoding)
    jsResp?.data?.close()

    // 4. HTML viewer request
    val htmlResp = simulateIntercept("https://local.app/web/viewer.html")
    assertNotNull(htmlResp)
    assertEquals("text/html", htmlResp?.mimeType)
    assertEquals("UTF-8", htmlResp?.encoding)
    htmlResp?.data?.close()

    // 5. OPTIONS preflight CORS request
    val optionsResp = simulateIntercept("https://local.app/models/anatomy_model-v1.glb", "OPTIONS")
    assertNotNull(optionsResp)
    assertEquals(200, optionsResp?.statusCode)
    assertEquals("*", optionsResp?.responseHeaders?.get("Access-Control-Allow-Origin"))
  }

  @Test
  fun `verify muscle mapping exclusivity and absence of accidental overlap`() {
    val meshToGroups = mutableMapOf<String, MutableList<MuscleGroup>>()

    for (group in MuscleGroup.entries) {
      val meshes = getMeshNamesForMuscle(group)
      assertTrue("Muscle group $group must have mapped meshes", meshes.isNotEmpty())
      for (mesh in meshes) {
        meshToGroups.getOrPut(mesh) { mutableListOf() }.add(group)
      }
    }

    // Verify no single GLB mesh is shared across multiple distinct muscle groups
    for ((mesh, groups) in meshToGroups) {
      assertEquals(
        "Mesh '$mesh' must belong to exactly one muscle group, but was found in $groups",
        1,
        groups.size
      )
    }
  }

  @Test
  fun `verify bilateral chest selection and bidirectional synchronization`() {
    // Both left and right pectorals must map together to CHEST
    val chestMeshes = getMeshNamesForMuscle(MuscleGroup.CHEST)
    assertTrue("CHEST must include left pectoral tripo_part_69", chestMeshes.contains("tripo_part_69"))
    assertTrue("CHEST must include right pectoral tripo_part_77", chestMeshes.contains("tripo_part_77"))

    // Tapping either left or right pectoral must select CHEST
    assertEquals(MuscleGroup.CHEST, mapMeshToMuscle("tripo_part_69"))
    assertEquals(MuscleGroup.CHEST, mapMeshToMuscle("tripo_part_77"))
    assertEquals(MuscleGroup.CHEST, mapMeshToMuscle("Pectoralis_Major_L"))
    assertEquals(MuscleGroup.CHEST, mapMeshToMuscle("Pectoralis_Major_R"))
  }

  @Test
  fun `verify abs mapping excludes connective torso shell and pelvis to prevent body-wide blue highlighting`() {
    val absMeshes = getMeshNamesForMuscle(MuscleGroup.ABS)

    // Center 6-pack and obliques must be present
    assertTrue("ABS must include center 6-pack tripo_part_67", absMeshes.contains("tripo_part_67"))
    assertTrue("ABS must include left oblique tripo_part_43", absMeshes.contains("tripo_part_43"))
    assertTrue("ABS must include right oblique tripo_part_68", absMeshes.contains("tripo_part_68"))

    // Broad torso shell and pelvis must NOT be in ABS (they caused the forearm/leg bug)
    assertFalse("ABS must NOT include torso shell tripo_part_73", absMeshes.contains("tripo_part_73"))
    assertFalse("ABS must NOT include pelvis core tripo_part_5", absMeshes.contains("tripo_part_5"))

    // Tapping the broad connective shell must not trigger ABS
    assertNull("Tapping tripo_part_73 must not map to ABS", mapMeshToMuscle("tripo_part_73"))

    // tripo_part_5 must map to GLUTES only, not ABS
    assertEquals("tripo_part_5 must map strictly to GLUTES", MuscleGroup.GLUTES, mapMeshToMuscle("tripo_part_5"))
  }

  @Test
  fun `verify muscle_mesh_mapping json asset matches strict mapping configuration`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val jsonString = context.assets.open("models/muscle_mesh_mapping.json").bufferedReader().use { it.readText() }
    val json = JSONObject(jsonString)

    val muscleToMeshes = json.getJSONObject("muscle_to_meshes")
    val chestArray = muscleToMeshes.getJSONArray("CHEST")
    assertEquals(2, chestArray.length())
    assertEquals("tripo_part_69", chestArray.getString(0))
    assertEquals("tripo_part_77", chestArray.getString(1))

    val absArray = muscleToMeshes.getJSONArray("ABS")
    assertEquals(3, absArray.length())
    assertEquals("tripo_part_67", absArray.getString(0))
    assertEquals("tripo_part_43", absArray.getString(1))
    assertEquals("tripo_part_68", absArray.getString(2))
  }
}

