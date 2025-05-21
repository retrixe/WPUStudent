package xyz.retrixe.wpustudent.api.endpoints

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.retrixe.wpustudent.api.CLIENT_SECRET

@Serializable
private data class FetchAssetRequest(
    @SerialName("ContainerName") val containerName: String,
    @SerialName("BlobRelativePath") val blobRelativePath: String,
    @SerialName("ActualFileName") val actualFileName: String,
)

suspend fun fetchAsset(
    client: HttpClient,
    containerName: String,
    blobRelativePath: String,
    actualFileName: String,
): ByteArray {
    /*  curl 'https://mymitwpu.integratededucation.pwc.in/apigateway/docstorage/api/storage/downloadfile' \
          -H 'authorization: Bearer TOKEN' \
          -H 'content-type: application/json' \
          -H 'x-applicationname: connectportal' \
          -H 'x-appsecret: hu5UEMnT0sg51gGtC7nC' \
          -H 'x-requestfrom: web' \
          --data-raw '{"ContainerName":"iemsfilecontainer","BlobRelativePath":"MIT-WPU Student Photos/03052024/School of Computer Science and Engineering/1032233145_1032233145/SPH_1032233145.jpg","ActualFileName":"profile-picture.png"}'
    */
    val response = client.post("apigateway/docstorage/api/storage/downloadfile") {
        contentType(ContentType.Application.Json)
        header("x-applicationname", "connectportal")
        header("x-appsecret", CLIENT_SECRET)
        header("x-requestfrom", "web")
        setBody(FetchAssetRequest(containerName, blobRelativePath, actualFileName))
    }
    val body: ByteArray = response.body()
    return body
}
