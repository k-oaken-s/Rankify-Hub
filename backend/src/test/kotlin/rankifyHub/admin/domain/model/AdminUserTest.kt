package rankifyHub.admin.domain.model

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.*
import org.springframework.security.crypto.password.PasswordEncoder

class AdminUserTest :
  DescribeSpec({
    describe("AdminUser") {
      val passwordEncoder = mockk<PasswordEncoder>()

      describe("create") {
        it("should create a new AdminUser with encoded password") {
          val username = "admin"
          val rawPassword = "password"
          every { passwordEncoder.encode(rawPassword) } returns "encoded_password"

          val adminUser = AdminUser.create(username, rawPassword, passwordEncoder)

          adminUser.username shouldBe username
          adminUser.getHashedPassword() shouldBe "encoded_password"
        }
      }

      describe("verifyPassword") {
        it("should return true when password matches") {
          val username = "admin"
          val rawPassword = "password"
          val hashedPassword = "hashed_password"
          every { passwordEncoder.matches(rawPassword, hashedPassword) } returns true
          val adminUser = AdminUser.reconstruct(UUID.randomUUID(), username, hashedPassword)

          adminUser.verifyPassword(rawPassword, passwordEncoder) shouldBe true
        }

        it("should return false when password doesn't match") {
          val username = "admin"
          val wrongPassword = "wrong_password"
          val hashedPassword = "hashed_password"
          every { passwordEncoder.matches(wrongPassword, hashedPassword) } returns false
          val adminUser = AdminUser.reconstruct(UUID.randomUUID(), username, hashedPassword)

          adminUser.verifyPassword(wrongPassword, passwordEncoder) shouldBe false
        }
      }
    }
  })
