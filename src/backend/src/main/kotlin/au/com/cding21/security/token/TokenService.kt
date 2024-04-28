package au.com.cding21.security.token

interface TokenService {
    fun generate(config: TokenConfig, hash: String, vararg claims: TokenClaim): String
}