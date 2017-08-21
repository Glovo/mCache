package wiki.depasquale.mcache.core.internal

import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import wiki.depasquale.mcache.MCache
import wiki.depasquale.mcache.core.internal.compat.OldFileParams
import java.io.*
import java.text.Normalizer

class FileMap private constructor() {

  @Transient private val MAP_NAME = "map.fmp"
  @Transient private lateinit var folder: File
  @Transient private val gson: Gson = Gson()

  @SerializedName("files") private val oldFiles: MutableList<OldFileParams> = mutableListOf()
  private val filesList: MutableList<FileParams> = mutableListOf()

  private constructor(className: String, isCache: Boolean = false) : this() {
    MCache.get()?.let {
      val dir = File(if (isCache) it.cacheDir else it.filesDir, "mcache")
      if (!dir.exists()) dir.mkdirs()

      val desiredName = className.getNameForClass()
      val foldersWithDesiredName = dir.listFiles().filter { desiredName == it.name }.toMutableList()

      if (foldersWithDesiredName.isEmpty()) {
        val folder = File(dir, desiredName)
        folder.mkdirs()
        foldersWithDesiredName.add(folder)
      }

      folder = File(dir, "error")
      folder = foldersWithDesiredName.firstOrNull() ?: return
      val filesWithDesiredName = folder.listFiles().filter { MAP_NAME == it.name }.toMutableList()
      if (filesWithDesiredName.size > 1) {
        filesWithDesiredName.forEach { it.deleteRecursively() }
        filesWithDesiredName.clear()
      }
      if (filesWithDesiredName.isEmpty()) {
        updateMap()
      }
      if (filesWithDesiredName.size == 1) {
        readMap(filesWithDesiredName.firstOrNull() ?: return)
      }
    }
  }

  private fun readMap(file: File) {
    file.read()?.convertToMap()?.let {
      //Compat clause, do not remove unless version bump!
      if (it.oldFiles.isNotEmpty()) {
        it.filesList.addAll(it.oldFiles.map { it.toNew() })
        it.oldFiles.clear()
        file.write(it)
      }
      filesList.clear()
      filesList.addAll(it.filesList)
    }
  }

  private fun updateMap(params: FileParams? = null) {
    if (params != null) {
      var found = false
      for ((index, param) in filesList.withIndex()) {
        if (param.core.descriptor == params.core.descriptor) {
          param.core.id = params.core.id
          param.core.timeCreated = params.core.timeCreated
          param.core.timeChanged = params.core.timeChanged
          filesList[index] = param
          found = true
          break
        }
      }
      if (!found) {
        filesList.add(params)
      }
    }
    File(folder, MAP_NAME).write(this)
  }

  /**
   * **Base function**: Creates Observable with given class and params
   *
   * **Detailed function**: Queries files index for descriptor. If result is longer than 1 it will
   * panic. If it finds none returns Observable.empty(). If it finds exactly one looks for it's
   * file by id and reads it. If it's null somehow function returns the same thing as if it found
   * none else the object is reconstructed and converted to Observable.
   */
  fun <T> findObjectByParams(cls: Class<T>, params: FileParams): Observable<T> {
    if (params.read.all) {
      return filesList.map { File(folder, it.core.id.toString()).read()?.convertToObject(cls) }.toObservable().flatMapIterable { it }
    }

    val wantedFiles = filesList.findByParams(params)
    if (wantedFiles.isEmpty()) {
      return Observable.empty()
    } else {
      if (wantedFiles.size > 1)
        Log.e("mCache", "There is more than one file for params: ${Gson().toJson(params)}")
      val wantedFile = wantedFiles.firstOrNull() ?: return Observable.empty<T>()
      val final = File(folder, wantedFile.core.id.toString()).read()?.convertToObject(cls)
      return if (final == null) Observable.empty<T>() else final.toObservable()
    }
  }

  /**
   * **Base function**: Saves object with given params
   *
   * **Detailed function**: Creates Observable with files index, queries for descriptor. If result
   * is not empty it will take the last FileParams and copies data from it. Field timeChanged is
   * set to currentTimeMillis. If it's empty it creates new id upon existing ids and sets
   * timeCreated and timeChanged to currentTimeMillis. Then it's created a file with name equal to
   * FileParams id. If it proceeds nominally listener is notified with >true< otherwise >false<.
   */
  fun saveObjectWithParams(file: Any, params: FileParams) {
    Observable.just(filesList)
        .observeOn(Schedulers.io())
        .map {
          if (it.any { it.core.descriptor == params.core.descriptor }) {
            for (param in it) {
              if (param.core.descriptor == params.core.descriptor) {
                params.core.id = param.core.id
                params.core.timeCreated = param.core.timeCreated
                params.core.timeChanged = System.currentTimeMillis()
                break
              }
            }
          } else {
            params.core.id = it.computeNewId()
            params.core.timeCreated = System.currentTimeMillis()
            params.core.timeChanged = params.core.timeCreated
          }
          return@map it
        }
        .map {
          File(folder, params.core.id.toString()).write(file)
          updateMap(params)
          return@map true
        }
        .subscribe({
          params.write.listener.invoke(it)
        }, {
          it.printStackTrace()
          params.write.listener.invoke(false)
        })
  }

  /**
   * **Base function**: Removes object with given params within class
   *
   * **Detailed function**: Checks whether FileParams contains removeAll tag, if so it will remove
   * all objects unconditionally else files index will be queried for descriptor, those matching
   * are deleted recursively (if it is a folder for instance...) then they are removed from files
   * index.
   */
  fun removeObjectWithParams(params: FileParams) {
    if (params.write.all) {
      removeAllObjects()
      params.write.listener.invoke(true)
      return
    }
    //filter with write params
    filesList.findByParams(params)
        .forEach {
          val file = File(folder, it.core.id.toString())
          if (file.exists()) {
            file.deleteRecursively()
            filesList.remove(it)
          }
        }
    updateMap()
    params.write.listener.invoke(true)
  }

  /**
   * **Base function**: Removes all objects within class
   *
   * **Detailed function**: For each file in files index checks whether the file exists and then it deletes it recursively and removes it from files index.
   */
  private fun removeAllObjects() {
    filesList.forEachRemove {
      val file = File(folder, it.core.id.toString())
      if (file.exists()) {
        file.deleteRecursively()
        return@forEachRemove true
      }
      return@forEachRemove false
    }
    updateMap()
  }

  companion object {
    private val fileMaps: MutableMap<Class<*>, FileMap> by lazy {
      LinkedHashMap<Class<*>, FileMap>(0)
    }

    /**
     * **Base function**: Finds or creates map for given class.
     *
     * **Detailed function**: Checks for context if it's not null proceeds. Takes only thing from
     * class - it's name - which is base64ed and stripped off signs that are not wanted (\s+)(=).
     * Looks for this stripped name in cache/files folder respectively if it founds more than one
     * folder it will panic otherwise it will create folder with respective name (if it's the first
     * time) else it will query the folder for it's map which has to be present. If map is found
     * more than one time it will panic, otherwise it will create the map. Creation process is
     * defined by condition whether the map is present or not. If not it will write empty map. Then
     * it continues to read index of all files and writes them to this instance.
     */
    fun forClass(cls: Class<*>, isCache: Boolean = false): FileMap {
      return fileMaps.getOrPut(cls) {
        FileMap(cls.simpleName, isCache)
      }
    }

    /**
     * **Base function**: Deletes dir (cache/files) for this library recursively.
     *
     * **Detailed function**: If context is valid it finds cache/files directory and it's child
     * **mcache** which is deleted recursively afterwards.
     */
    fun clean(isCache: Boolean = false) {
      MCache.get()?.let {
        val dir = File(if (isCache) it.cacheDir else it.filesDir, "mcache")
        if (dir.exists()) dir.deleteRecursively()
      }
    }
  }

  private inline fun <T> MutableList<T>.forEachRemove(call: (T) -> Boolean) {
    val iterator = this.iterator()
    while (iterator.hasNext()) {
      if (call(iterator.next())) iterator.remove()
    }
  }

  private fun String.convertToMap(): FileMap? {
    return gson.fromJson(this, FileMap::class.java)
  }

  private fun String.getNameForClass(): String {
    var tempName = Base64.encodeToString(this.toByteArray(), Base64.DEFAULT).replace("=", "").replace(Regex("\\s+"), "")
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
    return gson.fromJson(this, cls)
  }

  private fun File.write(file: Any) {
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
        .filter { it.core.id > id }
        .forEach { id = it.core.id }
    return id + 1L
  }

  private fun MutableList<FileParams>.findByParams(params: FileParams): MutableList<FileParams> {
    return filter {
      when {
        params.core.descriptor.isNotEmpty() ->
          return@filter it.core.descriptor == params.core.descriptor
        params.read.hasSetBoundaries() ->
          return@filter params.read.hasValidBoundaries(it)
        else ->
          return@filter false
      }
    }.toMutableList()
  }
}
