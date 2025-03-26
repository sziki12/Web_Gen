package app.web_gen.security

import app.web_gen.user.User
import app.web_gen.user.UserRepository
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository:UserRepository
) {
    fun getCurrentUser(): User {

        return userRepository.findById(0).get()
    }
}