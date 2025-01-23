// package rankifyHub.admin.security
//
// import io.kotest.core.spec.style.DescribeSpec
// import io.kotest.matchers.shouldBe
// import org.springframework.boot.test.context.SpringBootTest
// import org.springframework.boot.test.web.client.TestRestTemplate
// import org.springframework.http.HttpEntity
// import org.springframework.http.HttpHeaders
// import org.springframework.http.HttpMethod
// import org.springframework.http.HttpStatus
// import org.springframework.test.context.ActiveProfiles
// import rankifyHub.admin.presentation.controller.AdminController.LoginRequest
//
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @ActiveProfiles("test")
// class AdminSecurityTest(private val restTemplate: TestRestTemplate) :
//  DescribeSpec({
//    describe("Admin Security") {
//      describe("login endpoint") {
//        it("should handle empty credentials") {
//          val request = LoginRequest("", "")
//
//          val response = restTemplate.postForEntity("/admin/login", request, Any::class.java)
//
//          response.statusCode shouldBe HttpStatus.UNAUTHORIZED
//        }
//
//        it("should handle special characters in username") {
//          val request = LoginRequest("admin!@#$%", "password")
//
//          val response = restTemplate.postForEntity("/admin/login", request, Any::class.java)
//
//          response.statusCode shouldBe HttpStatus.UNAUTHORIZED
//        }
//      }
//
//      describe("protected endpoints") {
//        it("should deny access without token") {
//          val response = restTemplate.getForEntity("/admin/protected", Any::class.java)
//
//          response.statusCode shouldBe HttpStatus.UNAUTHORIZED
//        }
//
//        it("should deny access with invalid token") {
//          val headers = HttpHeaders()
//          headers.set("Authorization", "Bearer invalid.token.here")
//
//          val response =
//            restTemplate.exchange(
//              "/admin/protected",
//              HttpMethod.GET,
//              HttpEntity<Any>(headers),
//              Any::class.java
//            )
//
//          response.statusCode shouldBe HttpStatus.UNAUTHORIZED
//        }
//      }
//    }
//  })
