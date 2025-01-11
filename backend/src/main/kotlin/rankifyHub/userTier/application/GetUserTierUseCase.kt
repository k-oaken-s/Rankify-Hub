package rankifyHub.userTier.application

import java.util.*
import org.springframework.stereotype.Service
import rankifyHub.userTier.domain.model.UserTier
import rankifyHub.userTier.domain.repository.UserTierRepository

@Service
class GetUserTierUseCase(private val userTierRepository: UserTierRepository) {
  fun getUserTierById(userTierId: UUID): UserTier {
    return userTierRepository.findById(userTierId) ?: throw NoSuchElementException("Not found")
  }
}
