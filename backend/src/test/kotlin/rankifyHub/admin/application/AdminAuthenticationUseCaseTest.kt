package rankifyHub.admin.application

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.security.crypto.password.PasswordEncoder
import rankifyHub.admin.domain.model.AdminUser
import rankifyHub.admin.domain.repository.AdminUserRepository
import rankifyHub.common.security.JwtUtil

class AdminAuthenticationUseCaseTest :
  DescribeSpec({
    describe("AdminAuthenticationUseCase") {
      val adminUserRepository = mockk<AdminUserRepository>()
      val passwordEncoder = mockk<PasswordEncoder>()
      val jwtUtil = mockk<JwtUtil>()
      val useCase = AdminAuthenticationUseCase(adminUserRepository, passwordEncoder, jwtUtil)

      describe("authenticate") {
        it("should return token when credentials are valid") {
          val username = "admin"
          val password = "password"
          val adminUser =
            mockk<AdminUser>() {
              every { verifyPassword(any(), any()) } returns true
              every { this@mockk.username } returns username
            }
          val token = "valid_token"

          every { adminUserRepository.findByUsername(username) } returns adminUser
          every { adminUser.verifyPassword(password, passwordEncoder) } returns true
          every { jwtUtil.generateToken(username) } returns token

          val result = useCase.authenticate(username, password)

          result shouldBe token
          verify {
            adminUserRepository.findByUsername(username)
            adminUser.verifyPassword(password, passwordEncoder)
            jwtUtil.generateToken(username)
          }
        }

        it("should return null when user is not found") {
          val username = "unknown"
          val password = "password"

          every { adminUserRepository.findByUsername(username) } returns null

          val result = useCase.authenticate(username, password)

          result shouldBe null
          verify { adminUserRepository.findByUsername(username) }
        }

        it("should return null when password is invalid") {
          val username = "admin"
          val password = "wrong_password"
          val adminUser = mockk<AdminUser>()

          every { adminUserRepository.findByUsername(username) } returns adminUser
          every { adminUser.verifyPassword(password, passwordEncoder) } returns false

          val result = useCase.authenticate(username, password)

          result shouldBe null
          verify {
            adminUserRepository.findByUsername(username)
            adminUser.verifyPassword(password, passwordEncoder)
          }
        }
      }
    }
  })
