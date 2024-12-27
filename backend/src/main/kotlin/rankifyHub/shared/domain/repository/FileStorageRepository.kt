package rankifyHub.shared.domain.repository

/** ファイルストレージ操作を表すリポジトリインターフェース。 複数のコンテキストで利用可能。 */
interface FileStorageRepository {

  /**
   * ファイルを保存し、そのアクセス可能な URL またはキーを返す。
   *
   * @param directory 保存先ディレクトリ
   * @param identifier ファイル識別子
   * @param fileData 保存するバイナリデータ
   * @param extension ファイル拡張子
   * @return 保存されたファイルのアクセス可能な URL またはキー
   */
  fun saveFile(
    directory: String,
    identifier: String,
    fileData: ByteArray,
    extension: String
  ): String

  /**
   * ファイルを削除する。
   *
   * @param urlOrKey 削除対象を特定するURLまたはキー
   */
  fun deleteFile(urlOrKey: String)

  /**
   * 指定したオブジェクトキーに基づいてアクセス可能なURLを生成する。
   *
   * @param objectKey S3オブジェクトのキー
   * @return アクセス可能なURL
   */
  fun generateUrl(objectKey: String): String
}
