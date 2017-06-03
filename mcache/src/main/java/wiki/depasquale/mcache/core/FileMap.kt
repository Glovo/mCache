package wiki.depasquale.mcache.core

import android.util.Base64
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import wiki.depasquale.mcache.BuildConfig
import wiki.depasquale.mcache.MCache
import java.io.*
import java.text.Normalizer

/**
 * diareuse on 03.06.2017
 */

class FileMap private constructor() {

    private val MAP_NAME = "map.fmp"
    private var folder: File? = null
    @Expose private val files: MutableList<FileParams> = ArrayList(0)
    private val gson: Gson by lazy {
        GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
    }

    private constructor(className: String, isCache: Boolean = false) : this() {
        MCache.get()?.let {
            val dir = if (isCache) it.cacheDir else it.filesDir
            val desiredName = className.getNameForClass()
            val foldersWithDesiredName = dir.listFiles().filter { desiredName == it.name }.toMutableList()
            if (foldersWithDesiredName.size > 1) {
                throw RuntimeException("FileMap Panic", Throwable("There is more than one folder with desired name."))
            }
            if (foldersWithDesiredName.isEmpty()) {
                val folder = File(dir, desiredName)
                folder.mkdirs()
                foldersWithDesiredName.add(folder)
            }
            if (foldersWithDesiredName.size == 1) {
                folder = foldersWithDesiredName[0]
                val filesWithDesiredName = folder!!.listFiles().filter { MAP_NAME == it.name }.toMutableList()
                if (filesWithDesiredName.size > 1) {
                    throw RuntimeException("FileMap Panic", Throwable("There is more than one map"))
                }
                if (filesWithDesiredName.isEmpty()) {
                    updateMap()
                }
                if (filesWithDesiredName.size == 1) {
                    readMap(filesWithDesiredName[0])
                }
            }
        }
    }

    private fun readMap(file: File) {
        file.read()?.convertToMap()?.files?.let {
            files.clear()
            files.addAll(it)
        }
    }

    private fun updateMap(params: FileParams? = null) {
        if (params != null) {
            files.add(params)
        }
        if (folder != null) {
            try {
                val fos = FileOutputStream(File(folder, MAP_NAME))
                fos.write(gson.toJson(this).toByteArray())
                fos.close()
            } catch (e: IOException) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
            }
        } else {
            throw RuntimeException("Root folder is null hence I can't save the file.")
        }
    }

    companion object {
        fun forClass(cls: Class<Any>, isCache: Boolean = false): FileMap {
            return FileMap(cls.simpleName, isCache)
        }
    }

    /**
     * This may throw an exception, but it's nothing bad really...
     */
    private fun String.convertToMap(): FileMap {
        return gson.fromJson(this, FileMap::class.java)
    }

    private fun String.getNameForClass(): String {
        var tempName = Base64.encodeToString(this.toByteArray(), Base64.DEFAULT).replace("=", "")
        tempName = Normalizer.normalize(tempName, Normalizer.Form.NFD)
        return tempName
    }

    private fun File.read(): String? {
        try {
            val inputStreamReader = InputStreamReader(FileInputStream(this))
            return inputStreamReader.use {
                return@use it.readText()
            }
        } catch (e: IOException) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }
        return null
    }
}
