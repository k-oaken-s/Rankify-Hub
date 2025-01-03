package rankifyHub.common.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

/**
 * Spring Security の設定クラス
 *
 * アプリケーションのセキュリティ設定を管理します。 主に認証、認可、CORS、CSRF などの設定を行います。
 */
@Configuration
open class SecurityConfig {

  /**
   * パスワードエンコーダーの Bean を生成
   *
   * アプリケーション全体で使用するパスワードのハッシュ化アルゴリズムを提供します。
   *
   * @return PasswordEncoder のインスタンス
   */
  @Bean
  open fun passwordEncoder(): PasswordEncoder {
    return BCryptPasswordEncoder()
  }

  /**
   * セキュリティフィルターチェーンを定義
   *
   * HTTP リクエストのセキュリティルールを設定します。CSRF の無効化、CORS 設定、認可のルールなどを定義。
   *
   * @param http HttpSecurity インスタンス
   * @return SecurityFilterChain のインスタンス
   */
  @Bean
  open fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
    http
      .cors { it.configurationSource(corsConfigurationSource()) } // CORS設定を適用
      .csrf { it.disable() } // CSRFを無効化
      .authorizeHttpRequests { auth ->
        auth.anyRequest().permitAll() // 全てのリクエストを許可
      }
    return http.build()
  }

  /**
   * CORS 設定のソースを生成
   *
   * アプリケーションにおける CORS（クロスオリジンリソース共有）のルールを定義。 全てのオリジン、メソッド、ヘッダーを許可する設定を適用。
   *
   * @return CORS 設定のソース
   */
  @Bean
  open fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
    val source = UrlBasedCorsConfigurationSource()
    val config = CorsConfiguration()

    // 全てのオリジン、メソッド、ヘッダーを許可してCORSを無効化
    config.allowedOriginPatterns = listOf("*")
    config.allowedMethods = listOf("*")
    config.allowedHeaders = listOf("*")
    config.allowCredentials = true

    source.registerCorsConfiguration("/**", config)
    return source
  }

  /**
   * CORS フィルターを生成
   *
   * HTTP リクエストに CORS 設定を適用するためのフィルターを作成。
   *
   * @return CORS フィルター
   */
  @Bean
  open fun corsFilter(): CorsFilter {
    return CorsFilter(corsConfigurationSource())
  }
}
