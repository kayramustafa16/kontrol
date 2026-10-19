package com.example.data.mesh

import android.content.Context
import com.example.data.model.MuscleGroup
import org.json.JSONObject
import java.io.File

/**
 * Resolves Tripo 3D mesh identifiers to logical muscle groups.
 * Ensures bilateral synchronization (e.g. left and right pectorals both highlight chest).
 */
class MuscleMeshMapping(private val context: Context) {

    private val meshToGroupMap = mutableMapOf<String, MuscleGroup>()
    private val groupToMeshesMap = mutableMapOf<MuscleGroup, MutableList<String>>()

    init {
        loadDefaultMapping()
    }

    private fun loadDefaultMapping() {
        try {
            // Check if user has imported a custom mapping file in app storage
            val customFile = File(context.filesDir, "models/muscle_mesh_mapping.json")
            val jsonString = if (customFile.exists()) {
                customFile.readText()
            } else {
                context.assets.open("models/muscle_mesh_mapping.json").bufferedReader().use { it.readText() }
            }

            val json = JSONObject(jsonString)
            val meshToMuscleJson = json.optJSONObject("mesh_to_muscle")

            meshToMuscleJson?.let { obj ->
                val keys = obj.keys()
                while (keys.hasNext()) {
                    val meshName = keys.next()
                    val groupStr = obj.getString(meshName)
                    val group = runCatching { MuscleGroup.valueOf(groupStr.uppercase()) }.getOrDefault(MuscleGroup.CHEST)
                    meshToGroupMap[meshName] = group
                    groupToMeshesMap.getOrPut(group) { mutableListOf() }.add(meshName)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback default mappings
            meshToGroupMap["Pectoralis_Major_L"] = MuscleGroup.CHEST
            meshToGroupMap["Pectoralis_Major_R"] = MuscleGroup.CHEST
            meshToGroupMap["Pectoralis_Minor_L"] = MuscleGroup.CHEST
            meshToGroupMap["Pectoralis_Minor_R"] = MuscleGroup.CHEST
        }
    }

    /**
     * Resolves a mesh name (e.g. from raycast or tap) to its logical MuscleGroup.
     */
    fun resolveMuscleGroup(meshIdentifier: String): MuscleGroup {
        meshToGroupMap[meshIdentifier]?.let { return it }

        // Heuristic fallback matching for Tripo 3D variants
        val lower = meshIdentifier.lowercase()
        return when {
            lower.contains("pec") || lower.contains("chest") || lower.contains("gogus") -> MuscleGroup.CHEST
            lower.contains("lat") || lower.contains("back") || lower.contains("sirt") || lower.contains("trap") -> MuscleGroup.BACK
            lower.contains("delt") || lower.contains("shoulder") || lower.contains("omuz") -> MuscleGroup.SHOULDERS
            lower.contains("bicep") || lower.contains("kol") && !lower.contains("arka") -> MuscleGroup.BICEPS
            lower.contains("tricep") || lower.contains("arka_kol") -> MuscleGroup.TRICEPS
            lower.contains("abdom") || lower.contains("abs") || lower.contains("karin") -> MuscleGroup.ABS
            lower.contains("quad") || lower.contains("femoris") || lower.contains("bacak") -> MuscleGroup.QUADS
            lower.contains("hamstring") -> MuscleGroup.HAMSTRINGS
            lower.contains("glute") || lower.contains("kalca") -> MuscleGroup.GLUTES
            lower.contains("calf") || lower.contains("gastrocnemius") -> MuscleGroup.CALVES
            else -> MuscleGroup.CHEST
        }
    }

    /**
     * Returns all mesh nodes that should be highlighted together for a given muscle group.
     * E.g. For CHEST, returns both left and right pectoral meshes.
     */
    fun getMeshesForGroup(group: MuscleGroup): List<String> {
        return groupToMeshesMap[group] ?: listOf("${group.name}_L", "${group.name}_R")
    }
}
