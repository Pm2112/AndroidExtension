package com.pdm.adbilling.consent

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform

class ConsentManager private constructor(private val context: Context) {
    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(context)

    companion object {
        private var instance: ConsentManager? = null
        private const val TAG = "ConsentManager"

        fun getInstance(context: Context): ConsentManager {
            return instance ?: synchronized(this) {
                instance ?: ConsentManager(context.applicationContext).also { instance = it }
            }
        }
    }

    val canRequestAds: Boolean
        get() = consentInformation.canRequestAds()

    val isPrivacyOptionsRequired: Boolean
        get() =
            consentInformation.privacyOptionsRequirementStatus ==
                    ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    /**
     * Kiểm tra xem người dùng có ở vùng yêu cầu đồng ý không.
     */
    fun isUserInConsentRegion(): Boolean {
        return consentInformation.privacyOptionsRequirementStatus !=
                ConsentInformation.PrivacyOptionsRequirementStatus.NOT_REQUIRED
    }

    /**
     * Khởi tạo UMP và AdMob nếu cần thiết
     */
    fun initialize(activity: Activity, onComplete: (Boolean) -> Unit) {
        val params = ConsentRequestParameters.Builder().build()

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                Log.d(TAG, "Consent info updated successfully.")

                // Nếu người dùng không thuộc khu vực cần xin quyền thì khởi tạo AdMob ngay
                if (!isUserInConsentRegion()) {
                    Log.d(TAG, "Người dùng không thuộc vùng yêu cầu đồng ý. Bỏ qua UMP.")
                    handleConsentComplete(onComplete)
                    return@requestConsentInfoUpdate
                }

                // Nếu thuộc vùng yêu cầu, kiểm tra xem có form đồng ý không
                if (consentInformation.isConsentFormAvailable) {
                    showConsentForm(activity, onComplete)
                } else {
                    handleConsentComplete(onComplete)
                }
            },
            { requestError ->
                Log.e(TAG, "Lỗi khi cập nhật thông tin đồng ý: ${requestError.message}")
                onComplete(false)
            }
        )
    }

    private fun showConsentForm(activity: Activity, onComplete: (Boolean) -> Unit) {
        UserMessagingPlatform.loadAndShowConsentFormIfRequired(
            activity
        ) { formError: FormError? ->
            if (formError != null) {
                Log.e(TAG, "Lỗi khi hiển thị form đồng ý: ${formError.message}")
            }
            handleConsentComplete(onComplete)
        }
    }

    private fun handleConsentComplete(onComplete: (Boolean) -> Unit) {
        if (canRequestAds) {
            Log.d(TAG, "Người dùng đã đồng ý hoặc không cần xin quyền, khởi tạo MobileAds...")
            MobileAds.initialize(context) {}
        } else {
            Log.w(TAG, "Người dùng từ chối, không khởi tạo MobileAds.")
        }
        onComplete(canRequestAds)
    }

    fun showPrivacyOptions(activity: Activity) {
        if (isPrivacyOptionsRequired) {
            UserMessagingPlatform.showPrivacyOptionsForm(activity) { formError ->
                if (formError != null) {
                    Log.e(TAG, "Lỗi khi hiển thị cài đặt quyền riêng tư: ${formError.message}")
                }
            }
        }
    }
}