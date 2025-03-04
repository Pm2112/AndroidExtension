package com.pdm.androidextension.shazam

import android.content.Context
import com.shazam.shazamkit.*
import com.shazam.shazamkit.ShazamKitResult.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

object ShazamManager {
    private lateinit var session: Session
    private lateinit var catalog: Catalog
    private val shazamToken =
        "MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQgd+Sv2DbmViYeix/978XREPY9jF9oNB4oc4JdMWNk9fSgCgYIKoZIzj0DAQehRANCAAShGRaSuz0aBjti3tpM+YZQeUPpFiiNe9cO871fu/vr32Eo7Oola1y1tmKRlv/yTHAUkNKmvNrqZwfDTVGttqDl"

    suspend fun initialize(
        locale: Locale = Locale.US,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            try {
                val tokenProvider = DeveloperTokenProvider { DeveloperToken(shazamToken) }

                // Tạo catalog của Shazam
                catalog = ShazamKit.createShazamCatalog(tokenProvider)


                // Tạo session từ catalog
                val sessionResult = ShazamKit.createSession(catalog)
                if (sessionResult is Success) {
                    session = sessionResult.data
                    onSuccess()
                } else {
                    onError("Không thể tạo session")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Lỗi không xác định")
            }
        }
    }
}