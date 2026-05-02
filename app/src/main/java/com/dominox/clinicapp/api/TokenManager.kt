package com.dominox.clinicapp.api
import android.content.Context
import android.content.SharedPreferences
import com.auth0.android.jwt.JWT
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class TokenManager @Inject constructor(
@ApplicationContext
private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString("jwt_token", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("jwt_token", null)
    }

    fun clearToken() {
        prefs.edit().remove("jwt_token").apply()
    }
    fun getUserIdFromToken(): Int? {
        val token = getToken() ?: return null
        return try {
            val jwt = JWT(token)
            val userId = jwt.getClaim("userId").asInt()
            userId
        } catch (e: Exception) {
            null
        }
    }
    fun getUserNameFromToken(): String? {
        val token = getToken() ?: return null
        return try {
            val jwt = JWT(token)
            jwt.getClaim("name").asString()
        } catch (e: Exception) {
            null
        }
    }

}