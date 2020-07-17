package com.muhammadiqbalafandi.enotes.utils

import androidx.appcompat.app.AppCompatDelegate
import com.muhammadiqbalafandi.classiccryptographyalgorithm.algorithm.AtbashCipher
import com.muhammadiqbalafandi.classiccryptographyalgorithm.algorithm.VigenereCipher

object Utils {
    const val LIGHT_MODE = "Light"

    const val DARK_MODE = "Dark"

    const val BATTERY_SEVER_MODE = "Set by Battery Saver"

    const val DEFAULT_SYSTEM_MODE = "System default"

    fun applyTheme(theme: String) {
        when (theme) {
            LIGHT_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            DARK_MODE -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            BATTERY_SEVER_MODE -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            )
            DEFAULT_SYSTEM_MODE -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            )
        }
    }

    fun encryptionText(text: String, key: String): String {
        return VigenereCipher.encryption(text, key)
    }

    fun decryptionText(text: String, key: String): String {
        return VigenereCipher.decryption(AtbashCipher.decryption(text), key)
    }
}