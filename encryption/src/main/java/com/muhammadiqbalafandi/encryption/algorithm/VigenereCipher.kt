/**
 * Developed by Muhammad Iqbal Afandi
 * since april 2020.
 */

package com.muhammadiqbalafandi.encryption.algorithm

import com.muhammadiqbalafandi.encryption.Helpers.changeAsciiToString
import com.muhammadiqbalafandi.encryption.Helpers.changeStringToAscii
import com.muhammadiqbalafandi.encryption.Helpers.checkAsciiCharacterSupported
import com.muhammadiqbalafandi.encryption.Helpers.getCharacterValue
import com.muhammadiqbalafandi.encryption.Helpers.getIndexValue
import com.muhammadiqbalafandi.encryption.Helpers.repeatKeyWithKey

object VigenereCipher {
    fun encryption(plaintext: String, key: String): String {
        val listValueAsciiPlaintext: MutableList<Int> = changeStringToAscii(plaintext)
        val listValueAsciiKey: MutableList<Int> =
            changeStringToAscii(repeatKeyWithKey(key, plaintext))
        val ciphertextValue: MutableList<Int> = mutableListOf()

        for (plaintextAsciiValue in listValueAsciiPlaintext.withIndex()) {
            if (checkAsciiCharacterSupported(plaintextAsciiValue.value)) {
                val plaintextIndexValue = getIndexValue(plaintextAsciiValue.value)
                val indexValue = plaintextAsciiValue.index
                val keyIndexValue = getIndexValue(listValueAsciiKey[indexValue])
                if (plaintextIndexValue != null && keyIndexValue != null) {
                    /**
                     * Formula from the vigenere cipher algorithm
                     * please visit the site https://en.wikipedia.org/wiki/Vigen%C3%A8re_cipher
                     * to see more detail.
                     */
                    val vigenereEncryptionResult = (plaintextIndexValue + keyIndexValue) % 94

                    val characterValue = getCharacterValue(vigenereEncryptionResult)
                    if (characterValue != null) {
                        ciphertextValue.add(characterValue)
                    }
                }
            } else {
                ciphertextValue.add(plaintextAsciiValue.value)
            }
        }
        return changeAsciiToString(ciphertextValue)
    }

    fun decryption(ciphertext: String, key: String): String {
        val listValueAsciiCiphertext: MutableList<Int> = changeStringToAscii(ciphertext)
        val listValueAsciiKey: MutableList<Int> =
            changeStringToAscii(repeatKeyWithKey(key, ciphertext))

        val plaintextValue: MutableList<Int> = mutableListOf()

        for (ciphertextAsciiValue in listValueAsciiCiphertext.withIndex()) {
            if (checkAsciiCharacterSupported(ciphertextAsciiValue.value)) {
                val plaintextIndexValue = getIndexValue(ciphertextAsciiValue.value)
                val indexValue = ciphertextAsciiValue.index
                val keyIndexValue = getIndexValue(listValueAsciiKey[indexValue])
                if (plaintextIndexValue != null && keyIndexValue != null) {
                    /**
                     * Formula from the vigenere cipher algorithm
                     * please visit the site https://en.wikipedia.org/wiki/Vigen%C3%A8re_cipher
                     * to see more detail.
                     */
                    val vigenereDecryptionResult = (plaintextIndexValue - keyIndexValue + 94) % 94

                    val characterValue = getCharacterValue(vigenereDecryptionResult)
                    if (characterValue != null) {
                        plaintextValue.add(characterValue)
                    }
                }
            } else {
                plaintextValue.add(ciphertextAsciiValue.value)
            }
        }
        return changeAsciiToString(plaintextValue)
    }
}