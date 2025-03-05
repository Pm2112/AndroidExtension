package com.pdm.androidextension.shazam

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.pdm.androidextension.SharedPreferencesOwner
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.KeyFactory
import java.security.Security
import java.security.interfaces.ECPrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64
import java.util.Date

@RequiresApi(Build.VERSION_CODES.O)
object ShazamToken {
    private const val TAG = "ShazamToken"

    private const val TEAM_ID = "L48SZCK8SJ"
    private const val KEY_ID = "VZ6LXML3W8"
    private const val BUNDLE_ID = "com.pdm.androidextension"

    var developerToken: String = ""

    fun init(context: Context) {
        val sharedPreferencesOwner = SharedPreferencesOwner.getInstance(context)

        val currentToken = sharedPreferencesOwner.getToken()
        val currentTokenTime = sharedPreferencesOwner.getTokenTime()
        if (currentToken == null || currentTokenTime < System.currentTimeMillis()) {
            developerToken = generateDeveloperToken()
            sharedPreferencesOwner.putToken(developerToken)
            sharedPreferencesOwner.putTokenTime(System.currentTimeMillis() + 15777000 * 1000L)
        } else {
            developerToken = currentToken
        }
    }

    private fun generateDeveloperToken(): String {
        try {
            val privateKeyPEM = "MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQgd+Sv2DbmViYeix/" +
                "978XREPY9jF9oNB4oc4JdMWNk9fSgCgYIKoZIzj0DAQehRANCAAShGRaSuz0aBjti3tpM+YZQeUPpFii" +
                "Ne9cO871fu/vr32Eo7Oola1y1tmKRlv/yTHAUkNKmvNrqZwfDTVGttqDl"

            val keyBytes = Base64.getDecoder().decode(privateKeyPEM)
            val keyFactory = KeyFactory.getInstance("EC")
            val privateKey =
                keyFactory.generatePrivate(PKCS8EncodedKeySpec(keyBytes)) as ECPrivateKey

            Security.addProvider(BouncyCastleProvider())

            val issuedAt = Date()
            val expiration = Date(issuedAt.time + 15777000 * 1000L) // 6 tháng

            return Jwts.builder()
                .setHeaderParam("alg", "ES256")
                .setHeaderParam("kid", KEY_ID)
                .setIssuer(TEAM_ID)
                .setAudience("appstoreconnect-v1")
                .setSubject(BUNDLE_ID)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(privateKey, SignatureAlgorithm.ES256)
                .compact()
        } catch (e: Exception) {
            Log.d(TAG, "Lỗi khi tạo Developer Token: ${e.message}")
            return ""
        }
    }
}
