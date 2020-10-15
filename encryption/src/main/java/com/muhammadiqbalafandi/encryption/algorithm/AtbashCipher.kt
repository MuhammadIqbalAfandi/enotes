/**
 * Developed by Muhammad Iqbal Afandi
 * since april 2020.
 */

package com.muhammadiqbalafandi.encryption.algorithm

import com.muhammadiqbalafandi.encryption.Helpers.changeAsciiToString
import com.muhammadiqbalafandi.encryption.Helpers.changeStringToAscii
import com.muhammadiqbalafandi.encryption.Helpers.checkAsciiCharacterSupported
import com.muhammadiqbalafandi.encryption.Helpers.getCharacterValueForAtbash
import com.muhammadiqbalafandi.encryption.Helpers.getIndexValue
import com.muhammadiqbalafandi.encryption.Helpers.modulus

object AtbashCipher {

    fun encryption(str: String): String {
        val listValueAsciiPlaintext: MutableList<Int> = changeStringToAscii(str)
        val ciphertextValue: MutableList<Int> = mutableListOf()
        for (plaintextAsciiValue in listValueAsciiPlaintext) {
            if (checkAsciiCharacterSupported(plaintextAsciiValue)) {
                val indexValue = getIndexValue(plaintextAsciiValue)
                if (indexValue != null) {
                    val plaintextIndexValue = indexValue + 1

                    /**
                     * Formula from the atbash cipher algorithm
                     * please visit the site https://en.wikipedia.org/wiki/Atbash
                     * to see more detail.
                     */
                    val atbashEncryptionResult = ((modulus(-plaintextIndexValue, 94)) + 1)

                    val characterValue = getCharacterValueForAtbash(atbashEncryptionResult)
                    if (characterValue != null) {
                        ciphertextValue.add(characterValue)
                    }
                }
            } else {
                ciphertextValue.add(plaintextAsciiValue)
            }
        }
        return changeAsciiToString(ciphertextValue)
    }
}