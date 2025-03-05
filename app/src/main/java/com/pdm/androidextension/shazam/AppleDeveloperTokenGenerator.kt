package com.pdm.androidextension.shazam

import android.os.Build
import androidx.annotation.RequiresApi
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.bouncycastle.jcajce.BCFKSLoadStoreParameter
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.File
import java.security.KeyFactory
import java.security.Security
import java.security.interfaces.ECPrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64
import java.util.Date

object AppleDeveloperTokenGenerator {

    private const val TEAM_ID = "L48SZCK8SJ"
    private const val KEY_ID = "VZ6LXML3W8"
    private const val BUNDLE_ID = "com.pdm.androidextension"
    private const val AUTH_KEY_PATH = "src/main/resources/AuthKey_VZ6LXML3W8.p8"

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateDeveloperToken(): String {
        try {
            // Đọc file AuthKey.p8
            val privateKeyPEM = "MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQgd+Sv2DbmViYeix/978XREPY9jF9oNB4oc4JdMWNk9fSgCgYIKoZIzj0DAQehRANCAAShGRaSuz0aBjti3tpM+YZQeUPpFiiNe9cO871fu/vr32Eo7Oola1y1tmKRlv/yTHAUkNKmvNrqZwfDTVGttqDl"

            // Giải mã khóa
            val keyBytes = Base64.getDecoder().decode(privateKeyPEM)
            val keyFactory = KeyFactory.getInstance("EC")
            val privateKey = keyFactory.generatePrivate(PKCS8EncodedKeySpec(keyBytes)) as ECPrivateKey

            // Thêm BouncyCastle Provider
            Security.addProvider(BouncyCastleProvider())

            // Thời gian hết hạn (6 tháng)
            val issuedAt = Date()
            val expiration = Date(issuedAt.time + 15777000 * 1000L) // 6 tháng

            // Tạo JWT Token
            return Jwts.builder()
                .setHeaderParam("alg", "ES256")  // Chỉ định thuật toán ES256
                .setHeaderParam("kid", KEY_ID)
                .setIssuer(TEAM_ID)
                .setAudience("appstoreconnect-v1")
                .setSubject(BUNDLE_ID)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(privateKey, SignatureAlgorithm.ES256)
                .compact()

        } catch (e: Exception) {
            throw RuntimeException("Lỗi khi tạo Developer Token: ${e.message}", e)
        }
    }
}