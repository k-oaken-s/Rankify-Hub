package rankifyHub.admin.presentation.controller

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.HttpStatus
import rankifyHub.admin.application.AdminAuthenticationUseCase
import rankifyHub.admin.presentation.controller.AdminController.LoginRequest

class AdminControllerTest :
  DescribeSpec({
    describe("AdminController") {
      val adminAuthenticationUseCase = mockk<AdminAuthenticationUseCase>()
      val controller = AdminController(adminAuthenticationUseCase)

      describe("login") {
        it("should return 200 OK with token when authentication succeeds") {
          val request = LoginRequest("admin", "password")
          val token = "valid_token"

          every {
            adminAuthenticationUseCase.authenticate(request.username, request.password)
          } returns token

          val response = controller.login(request)

          response.statusCode shouldBe HttpStatus.OK
          response.body?.token shouldBe token
        }

        it("should return 401 Unauthorized when authentication fails") {
          val request = LoginRequest("admin", "wrong_password")

          every {
            adminAuthenticationUseCase.authenticate(request.username, request.password)
          } returns null

          val response = controller.login(request)

          response.statusCode shouldBe HttpStatus.UNAUTHORIZED
        }
      }
    }
  })
