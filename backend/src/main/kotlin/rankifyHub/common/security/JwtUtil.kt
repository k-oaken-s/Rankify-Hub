package rankifyHub.common.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.nio.charset.StandardCharsets
import java.util.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class JwtUtil(
  @Value("\${jwt.secret}") private val secretKey: String,
  @Value("\${jwt.expiration}") private val expirationTime: Long
) {
  private val key = Keys.hmacShaKeyFor(secretKey.toByteArray(StandardCharsets.UTF_8))

  fun generateToken(username: String): String {
    val now = Date()
    val expiration = Date(now.time + expirationTime)

    return Jwts.builder()
      .setSubject(username)
      .setIssuedAt(now)
      .setExpiration(expiration)
      .signWith(key)
      .compact()
  }

  fun validateTokenAndGetUsername(token: String): String? {
    return try {
      val claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body

      claims.subject
    } catch (e: Exception) {
      null
    }
  }

  fun getClaims(token: String): Claims? {
    return try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body
    } catch (e: Exception) {
      null
    }
  }
}
