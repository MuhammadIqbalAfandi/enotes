package com.muhammadiqbalafandi.encryption.algorithm

import com.muhammadiqbalafandi.encryption.Helpers
import io.mockk.mockkObject
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation::class)
internal class AtbashCipherTest {

    private val plaintext = "{P2bHliC8;O"

    private val indexValueForPlaintext = mutableListOf<Int?>()
    private var resultCharacterValue = mutableListOf<Int>()

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

        for (index in asciiPlaintext) {
            indexValueForPlaintext.add(Helpers.getIndexValue(index + 1))
        }
        println("Result Index Value for Plaintext: $indexValueForPlaintext")
    }

    @Test
    @Order(2)
    @DisplayName("Calculation for Atbash Cipher")
    fun calculation() {
        for ((index, _) in indexValueForPlaintext.withIndex()) {
            val indexPlaintext = indexValueForPlaintext[index]
            if (indexPlaintext != null) {
                val result = ((Helpers.modulus(-indexPlaintext, 94)) + 1)
                resultCharacterValue.add(result)

                println("(-$indexPlaintext modulus 94) + 1 = $result")
            }
        }
    }

    @Test
    @Order(3)
    @DisplayName("Result from Encryption Vigenere Cipher")
    fun getCharacterValue() {
        val listCharacterValue = mutableListOf<Int>()

        for (value in resultCharacterValue) {
            val characterValue = Helpers.getCharacterValueForAtbash(value)
            if (characterValue != null) {
                listCharacterValue.add(characterValue)
            }
        }

        val result = Helpers.changeAsciiToString(listCharacterValue)
        println("Result Encryption Atbash Cipher: $result")
    }

    @Test
    @Order(4)
    @Disabled
    fun testEncryptionSuccess() {
        val result = AtbashCipher.encryption(plaintext)
        println("Result Encryption Atbash Cipher: $result")
    }
}