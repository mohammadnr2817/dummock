package dev.radis.dummock.model.repository

import com.google.gson.Gson
import dev.radis.dummock.model.dataSource.FileWriter
import dev.radis.dummock.model.entity.Point
import dev.radis.dummock.utils.Logit
import dev.radis.dummock.utils.response.Failure
import dev.radis.dummock.utils.response.Response
import dev.radis.dummock.utils.response.Success
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import javax.inject.Inject

class DirectionFileWriterRepositoryImpl @Inject constructor(
    private val fileWriter: FileWriter
) : DirectionFileWriterRepository {

    private var coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun writeFile(points: List<Point>): Response<String, Throwable> {
        var result: String? = null
        var hasError = false

        coroutineScope.async(Dispatchers.IO) {
            val jsonData = Gson().toJson(points)
            val writeResponse = fileWriter.write(jsonData)
            writeResponse.ifNotSuccessful {
                Logit.d(it)
                hasError = true
            }
            writeResponse.ifSuccessful { filePath ->
                result = filePath
            }
        }.await()
        if (hasError) return Failure(Throwable("File saving exception"))
        return Success(requireNotNull(result))
    }

    override fun dispose() {
        coroutineScope.cancel()
    }
}