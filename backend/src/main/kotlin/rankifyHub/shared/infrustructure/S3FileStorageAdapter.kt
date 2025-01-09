package rankifyHub.shared.infrustructure

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import rankifyHub.shared.domain.repository.FileStorageRepository
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.net.URI

@Component
class S3FileStorageAdapter(
  @Value("\${aws.s3.bucket-name}") private val bucketName: String,
  @Value("\${aws.s3.region}") private val region: String,
  @Value("\${aws.s3.access-key}") private val accessKey: String,
  @Value("\${aws.s3.secret-key}") private val secretKey: String,
  @Value("\${aws.s3.endpoint-override:}") private val endpointOverride: String?
) : FileStorageRepository {

  private val s3Client: S3Client = run {
    val creds = AwsBasicCredentials.create(accessKey, secretKey)
    val builder =
      S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(creds))
        .serviceConfiguration { it.pathStyleAccessEnabled(true) }

    if (!endpointOverride.isNullOrEmpty()) {
      builder.endpointOverride(URI.create(endpointOverride))
    }

    builder.build()
  }

  override fun saveFile(
    directory: String,
    identifier: String,
    fileData: ByteArray,
    extension: String
  ): String {
    val objectKey = "$directory/$identifier.$extension"
    val putRequest = PutObjectRequest.builder().bucket(bucketName).key(objectKey).build()

    s3Client.putObject(putRequest, RequestBody.fromBytes(fileData))
    return generateUrl(objectKey)
  }

  override fun deleteFile(urlOrKey: String) {
    val objectKey = extractKeyFromUrl(urlOrKey)
    val deleteRequest = DeleteObjectRequest.builder().bucket(bucketName).key(objectKey).build()
    s3Client.deleteObject(deleteRequest)
  }

  override fun generateUrl(objectKey: String): String {
    return if (!endpointOverride.isNullOrEmpty()) {
      "http://minio:9000/$bucketName/$objectKey"
    } else {
      "https://s3.$region.amazonaws.com/$bucketName/$objectKey"
    }
  }

  private fun extractKeyFromUrl(urlOrKey: String): String {
    return if (urlOrKey.startsWith("http")) {
      urlOrKey.substringAfter("$bucketName/").substringAfter("/")
    } else {
      urlOrKey
    }
  }
}
