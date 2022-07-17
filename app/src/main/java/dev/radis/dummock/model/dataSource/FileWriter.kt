package dev.radis.dummock.model.dataSource

import android.content.Context
import dev.radis.dummock.utils.constants.StringConstants.APP_NAME
import dev.radis.dummock.utils.constants.StringConstants.DUMMOCK_EXTENSION
import dev.radis.dummock.utils.response.Failure
import dev.radis.dummock.utils.response.Response
import dev.radis.dummock.utils.response.Success
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import javax.inject.Inject

class FileWriter @Inject constructor(
    context: Context,
) : Writer.IO.Suspend<String, Response<String, Throwable>> {

    private var cachedFile: File? = null
    private var fileName: String? = ""

    init {
        fileName = APP_NAME + System.currentTimeMillis().toString() + DUMMOCK_EXTENSION
        cachedFile = File(
            context.cacheDir,
            requireNotNull(fileName)
        )
    }

    override suspend fun write(input: String): Response<String, Throwable> {
        return try {
            cachedFile?.let { file ->
                val fileOutputStream = FileOutputStream(file)
                val outputStreamWriter = OutputStreamWriter(fileOutputStream)
                val bufferedWriter = BufferedWriter(outputStreamWriter)
                bufferedWriter.write(input)
                bufferedWriter.flush()
                bufferedWriter.close()
                outputStreamWriter.close()
                fileOutputStream.close()
            }
            Success(requireNotNull(cachedFile).absolutePath)
        } catch (t: Throwable) {
            Failure(t)
        }
    }

}