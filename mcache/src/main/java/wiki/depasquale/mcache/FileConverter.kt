package wiki.depasquale.mcache

import wiki.depasquale.mcache.Cache.gson

internal class FileConverter<T>(override val builder: FilePresenterBuilderInterface<T>) :
    FileConverterInterface<T> {

    /**
     * Sets or returns file which is encoded in json.
     *
     * It further visits [FileWrapperInterface] which
     * wraps/unwraps file to/from base64 for basic file (integrity) protection. Then [FileRWInterface]
     * which is designed to read/write raw strings given by [FileWrapperInterface]
     *
     * This field is designed as "field" but rather getter and setter with parameters from
     * [FilePresenterBuilderInterface]
     */
    override var encodedFile: String
        set(value) {
            FileWrapper(this)
                .wrap(value)
        }
        get() {
            return FileWrapper(this)
                .unwrap()
        }
    /**
     * Fetches list of all files with given class.
     *
     * Behaves practically same as getter of [encodedFile]
     */
    override val encodedFiles: List<String>
        get() {
            return FileWrapper(this)
                .unwrapList()
        }

    internal fun run() {
        if (builder.file == null) {
            decode()
        } else {
            encode()
        }
    }

    internal fun fetchAll() {
        builder.files = encodedFiles.map { gson.fromJson(it, builder.cls) }
    }

    internal fun delete(): Boolean {
        return FileWrapper(this).delete()
    }

    private fun encode() {
        encodedFile = gson.toJson(builder.file)
    }

    private fun decode() {
        builder.file = gson.fromJson(encodedFile, builder.cls)
    }

}