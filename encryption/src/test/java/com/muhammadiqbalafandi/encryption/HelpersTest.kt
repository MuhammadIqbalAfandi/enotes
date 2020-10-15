package com.muhammadiqbalafandi.encryption

import io.mockk.mockkObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class HelpersTest {

    private val plaintext = "SerBu Berlin"
    private val asciiPlaintext = mutableListOf(83, 101, 114, 66, 117, 66, 101, 114, 108, 105, 110)
    private val key = "Pizzap"

    @BeforeEach
    fun setUp() {
        mockkObject(Helpers)
    }

    @Test
    fun testChangeStringToAsciiSuccess() {
        val result = Helpers.changeStringToAscii(plaintext)
        println("Result: $result")
        val expected = mutableListOf(83, 101, 114, 66, 117, 32, 66, 101, 114, 108, 105, 110)
        assertEquals(expected, result)
    }

    @Test
    fun testChangeAsciiToStringSuccess() {
        val result = Helpers.changeAsciiToString(asciiPlaintext)
        println("Result: $result")
        assertEquals("SerBuBerlin", result)
    }

    @Test
    fun testCheckAsciiCharacterSupportedSuccess() {
        val result = Helpers.checkAsciiCharacterSupported(asciiPlaintext[0])
        println("Result: $result")
        assertEquals(true, result)
    }

    @Test
    fun testCheckAsciiCharacterSupportedFail() {
        val result = Helpers.checkAsciiCharacterSupported(127)
        println("Result: $result")
        assertEquals(false, result)
    }

    @Test
    fun testGetCharacterValueSuccess() {
        val index = Helpers.getIndexValue(asciiPlaintext[0])
        if (index != null) {
            println("Index: $index")

            val result = Helpers.getCharacterValue(index)
            println("Result: $result")
            assertEquals(83, result)
        }
    }

    @Test
    fun testGetCharacterValueFail() {
        val index = Helpers.getIndexValue(32)
        println("Index: $index")
        if (index != null) {
            val result = Helpers.getCharacterValue(index)
            println("Result: $result")
            assertEquals(null, result)
        }
    }

    @Test
    fun testGetCharacterValueForAtbashSuccess() {
        val index = Helpers.getIndexValue(asciiPlaintext[0] + 1)
        if (index != null) {
            println("Index: $index")

            val result = Helpers.getCharacterValueForAtbash(index)
            println("Result: $result")
            assertEquals(83, result)
        }
    }

    @Test
    fun testGetCharacterValueForAtbashFail() {
        val index = Helpers.getIndexValue(32 + 1)
        if (index != null) {
            println("Index: $index")

            val result = Helpers.getCharacterValueForAtbash(index)
            println("Result: $result")
            assertEquals(null, result)
        }
    }

    @Test
    fun testGetIndexValueSuccess() {
        val result = Helpers.getIndexValue(asciiPlaintext[0])
        println("Result: $result")
        assertEquals(50, result)
    }

    @Test
    fun testGetIndexValueFail() {
        val result = Helpers.getIndexValue(32)
        println("Result: $result")
        assertEquals(null, result)
    }

    @Test
    fun testRemoveSpaceSuccess() {
        //val plaintext = "sadf 123  1!#$  # %#&* (  %)* sdf 24   sdfdas as a sd af "
        val result = Helpers.removeSpace(plaintext)
        println("Result: $result")
        assertEquals("SerBuBerlin", result)
    }

    @Test
    fun testRepeatKeyWithKeySuccess() {
        val result = Helpers.repeatKeyWithKey(key, plaintext)
        println("Result: $result")
        assertEquals("Pizza pPizza", result)
    }

    @Test
    fun testRepeatKeyWithPlaintextSuccess() {
        val result = Helpers.repeatKeyWithPlaintext(key, plaintext)
        println("Result: $result")
        assertEquals("Pizza pSerBu", result)
    }

    @Test
    fun testModulusSuccess() {
        val result1 = ((-5) % 26)
        println("Result: $result1")

        val result2 = Helpers.modulus(-5, 26)
        println("Result using modulus function: $result2")
        assertEquals(21, result2)
    }
}