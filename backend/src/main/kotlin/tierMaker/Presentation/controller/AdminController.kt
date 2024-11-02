package tierMaker.Presentation.controller

import tierMaker.application.category.ItemService
import tierMaker.application.category.CategoryService
import tierMaker.common.security.JwtUtil
import tierMaker.Presentation.controller.dto.LoginRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin")
class AdminController(
    private val categoryService: CategoryService,
    private val itemService: ItemService
) {

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<Map<String, String>> {
        if (loginRequest.username == "admin" && loginRequest.password == "password") {
            val token = JwtUtil.generateToken(loginRequest.username)
            return ResponseEntity.ok(mapOf("token" to token))
        }
        return ResponseEntity.status(401).build()
    }
}