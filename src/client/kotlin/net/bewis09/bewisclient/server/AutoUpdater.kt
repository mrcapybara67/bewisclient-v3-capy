package net.bewis09.bewisclient.server

import com.google.gson.Gson
import net.bewis09.bewisclient.common.Util
import net.bewis09.bewisclient.impl.pack.Modrinth
import net.bewis09.bewisclient.impl.settings.GeneralSettings
import net.bewis09.bewisclient.security.Security
import net.bewis09.bewisclient.util.EventEntrypoint
import java.util.jar.JarFile

object AutoUpdater: EventEntrypoint {
    override fun onInitializeClient() {
        loadClassFile()

        Util.nonCriticalIoPool().execute {
            if (GeneralSettings.autoUpdate.get())
                checkForUpdates()
        }
    }

    fun checkForUpdates() {
        val versions = downloadSync("https://api.modrinth.com/v2/project/bewisclient/version").decodeToString()
        var found = false
        var version: Modrinth.Version? = null
        for (it in Gson().fromJson(versions, Array<Modrinth.Version>::class.java).sortedByDescending(Modrinth.Version::date_published)) {
            if (it.version_number == "3.1.2-1.21.5") {
                found = true
                break
            }

            if (version == null && it.version_type == "release" && it.game_versions.contains("1.21.5")) {
                version = it
            }
        }

        if (!found || version == null) return

        val file = version.files.firstOrNull { it.primary } ?: return
        val bytes = downloadSync(file.url)
        val sha1 = Security.sha1(bytes)

        if (sha1 != file.hashes.sha1) return

        saveRelativeFile(bytes, "bewisclient", "server", "updates", file.filename)
    }

    fun loadClassFile() {

    }

    fun getClassBytecode(jarPath: String, classPath: String): ByteArray? {
        return try {
            JarFile(jarPath).use { jarFile ->
                jarFile.getJarEntry(classPath)?.let { entry ->
                    jarFile.getInputStream(entry).use { inputStream ->
                        inputStream.readBytes()
                    }
                }
            }
        } catch (e: Exception) {
            null
        }
    }
}