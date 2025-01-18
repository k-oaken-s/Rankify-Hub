package rankifyHub.common.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(private val jwtUtil: JwtUtil) : OncePerRequestFilter() {

  override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain
  ) {
    try {
      val token = extractToken(request)
      if (!token.isNullOrBlank()) {
        val username = jwtUtil.validateTokenAndGetUsername(token)
        if (username != null) {
          val auth =
            UsernamePasswordAuthenticationToken(
              username,
              null,
              listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
            )
          SecurityContextHolder.getContext().authentication = auth
        }
      }
    } catch (e: Exception) {
      SecurityContextHolder.clearContext()
    }

    filterChain.doFilter(request, response)
  }

  private fun extractToken(request: HttpServletRequest): String? {
    val header = request.getHeader("Authorization")
    return if (header?.startsWith("Bearer ") == true) {
      header.substring(7)
    } else null
  }
}
