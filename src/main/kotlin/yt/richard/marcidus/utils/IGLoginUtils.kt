package yt.richard.marcidus.utils

import com.github.instagram4j.instagram4j.IGClient
import com.github.instagram4j.instagram4j.responses.accounts.LoginResponse
import com.github.instagram4j.instagram4j.utils.IGChallengeUtils
import yt.richard.marcidus.ConfigManager

object IGLoginUtils {

    fun login(): IGClient {
        return IGClient.builder()
            .username(ConfigManager.getConfig().username) // read username from config
            .password(ConfigManager.getConfig().password) // read password from config
            .onTwoFactor(LoginHandler())
            .onChallenge(LoginHandler()) // register challenge handler
            .login()
        // TODO: Serialize and store session and restore on restart so no new login is required
    }

    // handle two factor and email challenges
    private class LoginHandler : IGClient.Builder.LoginHandler {
        override fun accept(client: IGClient, t: LoginResponse): LoginResponse = IGChallengeUtils.resolveChallenge(client, t) { println("Please input code: "); readLine().orEmpty() }
    }
}