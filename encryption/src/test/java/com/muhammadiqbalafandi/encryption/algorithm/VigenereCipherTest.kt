package com.muhammadiqbalafandi.encryption.algorithm

import com.muhammadiqbalafandi.encryption.Helpers
import io.mockk.mockkObject
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation::class)
internal class VigenereCipherTest {

    private val plaintext = "STIKOMBALI"
    private val key = "KAMPUSKAMP"

    private val indexValueForPlaintext = mutableListOf<Int?>()
    private val indexValueForKey = mutableListOf<Int?>()
    private var resultCharacterValue = mutableListOf<Int>()

    private lateinit var resultEncryptionVigenereCipher: String

    @BeforeEach
    fun setUp() {
        mockkObject(AtbashCipher)
        mockkObject(Helpers)
    }

    @Test
    @Order(1)
    @DisplayName("Get index value")
    fun getIndexValue() {
        val asciiPlaintext = Helpers.changeStringToAscii(plaintext)
        val asciiKey = Helpers.changeStringToAscii(Helpers.repeatKeyWithKey(key, plaintext))

        for (index in asciiPlaintext) {
            indexValueForPlaintext.add(Helpers.getIndexValue(index))
        }
        for (index in asciiKey) {
            indexValueForKey.add(Helpers.getIndexValue(index))
        }
        println("Result Index Value for Plaintext: $indexValueForPlaintext")
        println("Result Index Value for Key: $indexValueForKey")
    }

    @Test
    @Order(2)
    @DisplayName("Calculation for Vigenere Cipher")
    fun calculation() {
        for ((index, _) in indexValueForPlaintext.withIndex()) {
            val indexPlaintext = indexValueForPlaintext[index]
            val indexKey = indexValueForKey[index]
            if (indexKey != null && indexPlaintext != null) {
                // Formula to Encryption
                 val result = (indexPlaintext + indexKey) % 94
                // Formula to decryption
                // val result = (indexPlaintext - indexKey + 94) % 94

                resultCharacterValue.add(result)

                // Print to Encryption
                // println("($indexPlaintext + $indexKey % 94) = $result")
                // Print to Decryption
                println("($indexPlaintext + $indexKey + 94) modulus 94 = $result")
            }
        }
    }

    @Test
    @Order(3)
    @DisplayName("Result from Encryption Vigenere Cipher")
    fun getCharacterValue() {
        val listCharacterValue = mutableListOf<Int>()

        for (value in resultCharacterValue) {
            val characterValue = Helpers.getCharacterValue(value)
            if (characterValue != null) {
                listCharacterValue.add(characterValue)
            }
        }

        val result = Helpers.changeAsciiToString(listCharacterValue)
        println("Result Encryption Vigenere Cipher: $result")
    }

    @Test
    @Order(4)
    @Disabled
    fun encryption() {
        resultEncryptionVigenereCipher = VigenereCipher.encryption(plaintext, key)
        println("Result Encryption Vigenere Cipher: $resultEncryptionVigenereCipher")
    }

    @Test
    @Order(5)
    @Disabled
    fun decryption() {
        val result = VigenereCipher.decryption(resultEncryptionVigenereCipher, key)
        println("Result Decryption Vigenere Cipher: $result")
    }
}