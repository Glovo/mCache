package wiki.depasquale.mcache.core

import android.util.Base64
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import io.reactivex.Observable
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
    private val objectGson: Gson by lazy {
        Gson()
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
            var found: Boolean = false
            for ((index, param) in files.withIndex()) {
                if (param.descriptor == params.descriptor) {
                    param.id = params.id
                    param.timeCreated = params.timeCreated
                    param.timeChanged = params.timeChanged
                    files[index] = param
                    found = true
                    break
                }
            }
            if (!found) {
                files.add(params)
            }
        }
        if (folder != null) {
            File(folder, MAP_NAME).write(this, gson = gson)
        } else {
            throw RuntimeException("Root folder is null hence I can't save the file.")
        }
    }

    fun <T> findObjectByParams(cls: Class<T>, params: FileParams): Observable<T> {
        val wantedFiles = files.filter { it.descriptor == params.descriptor }
        if (wantedFiles.size > 1 || wantedFiles.isEmpty()) {
            throw RuntimeException("FileMap Panic", Throwable("Non unique descriptor for single class."))
        } else {
            val wantedFile = wantedFiles[0]
            val final = File(folder, wantedFile.id.toString()).read()?.convertToObject(cls)
            if (final == null) {
                val observable = Observable.empty<T>()
                return observable
            } else return final.toObservable()
        }
    }

    fun saveObjectWithParams(file: Any, params: FileParams) {
        if (files.any { it.descriptor == params.descriptor }) {
            for (param in files) {
                if (param.descriptor == params.descriptor) {
                    params.id = param.id
                    params.timeCreated = param.timeCreated
                    params.timeChanged = System.currentTimeMillis()
                    break
                }
            }
        } else {
            params.id = files.computeNewId()
            params.timeCreated = System.currentTimeMillis()
            params.timeChanged = params.timeCreated
        }
        File(folder, params.id.toString()).write(file)
        updateMap(params)
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
            e.printStackTrace()
        }
        return null
    }

    private fun <T> String.convertToObject(cls: Class<T>): T? {
        return objectGson.fromJson(this, cls)
    }

    private fun File.write(file: Any, gson: Gson = objectGson) {
        try {
            val fos = FileOutputStream(this)
            fos.write(gson.toJson(file).toByteArray())
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun <T> T.toObservable(): Observable<T> {
        return Observable.just(this)
    }

    private fun List<FileParams>.computeNewId(): Long {
        var id = 0L
        this
            .asSequence()
            .filter { it.id > id }
            .forEach { id = it.id }
        return id + 1L
    }
}
