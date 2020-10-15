/**
 * Developed by Muhammad Iqbal Afandi
 * since april 2020.
 */

package com.muhammadiqbalafandi.encryption

/**
 * Class that provides helper functions.
 */
object Helpers {

    private const val LIST_OF_SUPPORTED_CHARACTERS =
        "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~"

    /**
     * Change the string value to ascii value.
     *
     * @param string Request text to be changed to ascii value.
     * @return Return the ascii value in an array form.
     */
    fun changeStringToAscii(string: String): MutableList<Int> {
        val asciiValue: MutableList<Int> = mutableListOf()
        string.forEach { character ->
            asciiValue.add(character.toInt())
        }
        return asciiValue
    }

    /**
     * Change the ascii value to string value.
     *
     * @param asciiArray Request the ascii array value to be converted to a string value.
     * @return Return string.
     */
    fun changeAsciiToString(asciiArray: MutableList<Int>): String {
        val valueString: StringBuilder = StringBuilder()
        asciiArray.forEach { valueAscii ->
            valueString.append(valueAscii.toChar())
        }
        return valueString.toString()
    }

    /**
     * Check whether the ascii value is the same as the supported ascii value.
     *
     * @param ascii Request ascii value to check.
     * @return Return true if the ascii value is the ascii value of the supported character.
     */
    fun checkAsciiCharacterSupported(ascii: Int): Boolean = ascii in 33..126

    /**
     * Retrieve character values based on index value.
     *
     * @param index Character index value.
     * @return Return ascii value.
     */
    fun getCharacterValue(index: Int): Int? {
        for (supportedCharacterValue in LIST_OF_SUPPORTED_CHARACTERS.withIndex()) {
            if (index == supportedCharacterValue.index)
                return changeStringToAscii(supportedCharacterValue.value.toString())[0]
        }
        return null
    }

    fun getCharacterValueForAtbash(index: Int): Int? {
        for (supportedCharacterValue in LIST_OF_SUPPORTED_CHARACTERS.withIndex()) {
            if (index == supportedCharacterValue.index + 1)
                return changeStringToAscii(supportedCharacterValue.value.toString())[0]
        }
        return null
    }

    /**
     * Take the character index value.
     *
     * @param ascii Character ascii value.
     * @return Return the index value of the supported character or null.
     */
    fun getIndexValue(ascii: Int): Int? {
        for ((index, value) in changeStringToAscii(LIST_OF_SUPPORTED_CHARACTERS).withIndex()) {
            if (ascii == value) return index
        }
        return null
    }

    /**
     * Removes spaces in the text.
     *
     * @param string Required string to delete the space.
     * @return Return string without spaces.
     */
    fun removeSpace(string: String): String {
        val listValueAsciiString: MutableList<Int> = changeStringToAscii(string)
        val valueStringWithoutSpaces: MutableList<Int> = mutableListOf()
        listValueAsciiString.forEach { valueAsciiString ->
            if (checkAsciiCharacterSupported(valueAsciiString))
                valueStringWithoutSpaces.add(valueAsciiString)
        }
        return changeAsciiToString(valueStringWithoutSpaces)
    }

    /**
     * Repeat the key as much as the text to be encrypted.
     *
     * @param key Request text to be used as a key for encryption.
     * @param plaintext Request text to be encrypted.
     * @return Return a key that has been added with the key string.
     */
    fun repeatKeyWithKey(key: String, plaintext: String): String {
        val listValueAsciiKey: MutableList<Int> = changeStringToAscii(removeSpace(key))
        val listValueAsciiPlaintext: MutableList<Int> = changeStringToAscii(plaintext)
        val temporaryKeyValue: MutableList<Int> = mutableListOf()
        var keyIndexValue = 0
        for (plaintextAsciiValue in listValueAsciiPlaintext) {
            if (checkAsciiCharacterSupported(plaintextAsciiValue)) {
                temporaryKeyValue.add(listValueAsciiKey[keyIndexValue])
                if (keyIndexValue >= listValueAsciiKey.size - 1) keyIndexValue = 0 else keyIndexValue += 1
            } else {
                temporaryKeyValue.add(plaintextAsciiValue)
            }
        }
        return changeAsciiToString(temporaryKeyValue)
    }

    /**
     * Repeat the key as much as the text to be encrypted by adding the plaintext value to the key.
     *
     * @param key Request text to be used as a key for encryption.
     * @param plaintext Request text to be encrypted.
     * @return Return a key that has been added with the plaintext string.
     */
    fun repeatKeyWithPlaintext(key: String, plaintext: String): String {
        val listValueAsciiKey: MutableList<Int> = changeStringToAscii(removeSpace(key))
        val listValueAsciiPlaintext: MutableList<Int> = changeStringToAscii(plaintext)
        val temporaryKeyValue: MutableList<Int> = mutableListOf()
        var keyIndexValue = 0
        var plaintextIndexValue = 0
        for (plaintextAsciiValue in listValueAsciiPlaintext) {
            if (checkAsciiCharacterSupported(plaintextAsciiValue)) {
                if (keyIndexValue > listValueAsciiKey.size - 1) {
                    temporaryKeyValue.add(changeStringToAscii(removeSpace(plaintext))[plaintextIndexValue])
                    plaintextIndexValue += 1
                } else {
                    temporaryKeyValue.add(listValueAsciiKey[keyIndexValue])
                    keyIndexValue += 1
                }
            } else {
                temporaryKeyValue.add(plaintextAsciiValue)
            }
        }
        return changeAsciiToString(temporaryKeyValue)
    }

    /**
     * The modulus function is taken from someone"s posting on stackoverflow.
     * https://stackoverflow.com/questions/4467539/javascript-modulo-gives-a-negative-result-for-negative-numbers
     *
     * @param n The number to be modulated.
     * @param m Modulated by how much.
     * @return
     */
    fun modulus(n: Int, m: Int): Int = ((n % m) + m) % m
}