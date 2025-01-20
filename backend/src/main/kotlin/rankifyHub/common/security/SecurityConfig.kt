package rankifyHub.common.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(private val jwtAuthenticationFilter: JwtAuthenticationFilter) {
  @Bean fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

  @Bean
  fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
    http
      .cors { it.configurationSource(corsConfigurationSource()) }
      .csrf { it.disable() }
      .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
      .authorizeHttpRequests { auth ->
        auth
          .requestMatchers("/admin/login")
          .permitAll()
          .requestMatchers(HttpMethod.GET, "/categories/**")
          .permitAll()
          .requestMatchers("/categories/**")
          .hasRole("ADMIN")
          .requestMatchers("/user-tiers/**")
          .permitAll()
          .requestMatchers("/admin/**")
          .hasRole("ADMIN")
          .anyRequest()
          .authenticated()
      }

    return http.build()
  }

  @Bean
  fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
    val source = UrlBasedCorsConfigurationSource()
    val config =
      CorsConfiguration().apply {
        allowedOriginPatterns =
          listOf(
            "http://localhost:3000",
            "http://frontend:3000",
            "https://www.rankify-hub.com",
            "https://rankify-hub.com",
            "https://rankify-hub.vercel.app"
          )
        allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        allowedHeaders = listOf("Authorization", "Content-Type", "X-Requested-With", "Accept")
        exposedHeaders = listOf("Authorization")
        allowCredentials = true
        maxAge = 3600L
      }
    source.registerCorsConfiguration("/**", config)
    return source
  }

  @Bean fun corsFilter(): CorsFilter = CorsFilter(corsConfigurationSource())
}
