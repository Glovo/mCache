package wiki.depasquale.mcache.core.internal

import android.util.*
import com.google.gson.*
import com.google.gson.annotations.*
import io.reactivex.*
import io.reactivex.schedulers.*
import wiki.depasquale.mcache.*
import java.io.*
import java.text.*

/**
 * diareuse on 03.06.2017
 */

class FileMap private constructor() {

  private val MAP_NAME = "map.fmp"
  private var folder: java.io.File? = null
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
      val dir = File(if (isCache) it.cacheDir else it.filesDir, "mcache")
      if (!dir.exists()) dir.mkdirs()
      val desiredName = className.getNameForClass()
      val foldersWithDesiredName = dir.listFiles().filter { desiredName == it.name }.toMutableList()
      if (foldersWithDesiredName.size > 1) {
        throw RuntimeException("FileMap Panic", Throwable("There is more than one folder with desired name."))
      }
      if (foldersWithDesiredName.isEmpty()) {
        val folder = java.io.File(dir, desiredName)
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

  private fun readMap(file: java.io.File) {
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
      java.io.File(folder, MAP_NAME).write(this, gson = gson)
    } else {
      throw RuntimeException("Root folder is null hence I can't save the file.")
    }
  }

  fun <T> findObjectByParams(cls: Class<T>, params: FileParams): Observable<T> {
    val wantedFiles = files.filter { it.descriptor == params.descriptor }
    if (wantedFiles.size > 1) {
      throw RuntimeException("FileMap Panic", Throwable("Non unique descriptor for single class."))
    } else if (wantedFiles.isEmpty()) {
      return Observable.empty()
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
    Observable.just(files)
        .observeOn(Schedulers.io())
        .map {
          if (it.any { it.descriptor == params.descriptor }) {
            for (param in it) {
              if (param.descriptor == params.descriptor) {
                params.id = param.id
                params.timeCreated = param.timeCreated
                params.timeChanged = System.currentTimeMillis()
                break
              }
            }
          } else {
            params.id = it.computeNewId()
            params.timeCreated = System.currentTimeMillis()
            params.timeChanged = params.timeCreated
          }
          return@map it
        }
        .map {
          java.io.File(folder, params.id.toString()).write(file)
          updateMap(params)
          return@map true
        }
        .subscribe({
          params.listener(it)
        }, {
          it.printStackTrace()
          params.listener(false)
        })
  }

  fun removeObjectWithParams(params: FileParams) {
    if (params.removeAll) {
      removeAllObjects()
      params.listener(true)
      return
    }
    files.filter { it.descriptor == params.descriptor }
        .forEach {
          val file = File(folder, it.id.toString())
          if (file.exists()) {
            file.deleteRecursively()
            files.remove(it)
          }
        }
    updateMap()
    params.listener(true)
  }

  fun removeAllObjects() {
    files.forEach {
      val file = File(folder, it.id.toString())
      if (file.exists()) {
        file.deleteRecursively()
        files.remove(it)
      }
    }
    updateMap()
  }

  companion object {
    private val fileMaps: MutableMap<Class<*>, FileMap> by lazy {
      LinkedHashMap<Class<*>, FileMap>(0)
    }

    fun forClass(cls: Class<*>, isCache: Boolean = false): FileMap {
      return fileMaps.getOrPut(cls) {
        FileMap(cls.simpleName, isCache)
      }
    }

    fun clean(isCache: Boolean = false) {
      MCache.get()?.let {
        val dir = File(if (isCache) it.cacheDir else it.filesDir, "mcache")
        if (dir.exists()) dir.deleteRecursively()
      }
    }
  }

  /**
   * This may throw an exception, but it's nothing bad really...
   */
  private fun String.convertToMap(): FileMap {
    return gson.fromJson(this, FileMap::class.java)
  }

  private fun String.getNameForClass(): String {
    var tempName = Base64.encodeToString(this.toByteArray(), Base64.DEFAULT).replace("=", "").replace(Regex("\\s+"), "")
    tempName = Normalizer.normalize(tempName, Normalizer.Form.NFD)
    return tempName
  }

  private fun java.io.File.read(): String? {
    try {
      val inputStreamReader = InputStreamReader(FileInputStream(this))
      return inputStreamReader.use {
        return@use it.readText()
      }
    } catch (e: java.io.IOException) {
      e.printStackTrace()
    }
    return null
  }

  private fun <T> String.convertToObject(cls: Class<T>): T? {
    return objectGson.fromJson(this, cls)
  }

  private fun java.io.File.write(file: Any, gson: Gson = objectGson) {
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
    this.asSequence()
        .filter { it.id > id }
        .forEach { id = it.id }
    return id + 1L
  }
}
