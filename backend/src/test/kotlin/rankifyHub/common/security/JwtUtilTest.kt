package rankifyHub.common.security

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class JwtUtilTest :
  DescribeSpec({
    describe("JwtUtil") {
      val secretKey = "your-256-bit-secret-key-here-for-testing-purposes-only"
      val expirationTime = 3600000L // 1 hour
      val jwtUtil = JwtUtil(secretKey, expirationTime)

      describe("generateToken and validateTokenAndGetUsername") {
        it("should generate and validate token successfully") {
          val username = "testuser"

          val token = jwtUtil.generateToken(username)
          val validatedUsername = jwtUtil.validateTokenAndGetUsername(token)

          token shouldNotBe null
          validatedUsername shouldBe username
        }

        it("should return null for invalid token") {
          jwtUtil.validateTokenAndGetUsername("invalid.token.here") shouldBe null
        }

        it("should handle expired token") {
          val username = "testuser"
          val shortExpirationJwtUtil = JwtUtil(secretKey, 1L)

          val token = shortExpirationJwtUtil.generateToken(username)
          Thread.sleep(2)

          jwtUtil.validateTokenAndGetUsername(token) shouldBe null
        }
      }

      describe("getClaims") {
        it("should return claims for valid token") {
          val username = "testuser"
          val token = jwtUtil.generateToken(username)

          val claims = jwtUtil.getClaims(token)

          claims shouldNotBe null
          claims?.subject shouldBe username
        }

        it("should return null for invalid token") {
          jwtUtil.getClaims("invalid.token.here") shouldBe null
        }
      }
    }
  })
