/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.deduplication

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.Contact

import java.nio.ByteBuffer
import java.util.zip.Adler32

/**
 * This class creates 32bit int simhashes for a contact record. Simhashes which are near (as hamming distance)
 * to each other are more likely to be duplicate contact records.
 */
@CompileStatic
class DuplicateFinder {

    int[] bits = new int[32]

    Collection<? extends Object> build(Contact c) {
        addToken(c.birthDate, 2)

        addToken(c.email,3)

        addToken(ngram(c.firstName, 2, true), 1)
        addToken(ngram(c.lastName, 2, true), 2)

        addToken(c.suburb, 1)
        addToken(c.homePhone, 2)
        addToken(c.mobilePhone, 2)
    }

    /**
     * Convert the bits array into a 32 bit integer suitable for storage in the contact record
     * @return
     */
    long getHash() {
        long hash = 0L
        long one = 1L
        (0..31).each { i ->
            if (bits[i] > 0) {
                hash |= one
            }
            one = one << 1
        }
        return hash
    }

    /**
     * Add weight to the bits array in each position where the 32bit checksum
     * of the token has a bit set
     * Subtract weight where there is no bit set
     *
     * @param token
     * @param weight
     */
    void addToken(Object token, int weight=1) {
        if (token == null) {
            return
        }
        if (token instanceof Collection) {
            token.each { addToken(it)}
        } else {
            def checksum = getChecksum(token)
            (0..31).each { int i ->
                if (((checksum >> i) & 1) == 1)
                    bits[i] += weight
                else
                    bits[i] -= weight
            }
        }
    }

    /**
     *
     * @param input
     * @param n count of the number of token in each ngram
     * @param charGram if true, create grams with characters instead of words
     * @return
     */
    static List<String> ngram(String input, int n=2, boolean charGram=false) {
        if (!input) {
            return null
        }
        if (n < 2) {
            throw new IllegalArgumentException("Size has to be at least 2.")
        }
        List<String> result, tokens = [] as List<String>
        String join
        if (charGram) {
            tokens = input.replaceAll(/[\p{Punct}\p{Space}]/, '').toCharArray().collect{ it.toString() }
            join = ''
        } else {
            tokens = input.tokenize(" \t\n\r\f.,;:-()&#@[]'\"")
            join = ' '
        }
        if (tokens.size() <= n) {
            return [input]
        }
        result = tokens.collate(n, 1, false).collect { group ->
            group.join(join)
        }

        return result
    }


    static long getChecksum(Object o) {
        Adler32 checksum = new Adler32()
        def b = ByteBuffer.wrap(o.toString().toUpperCase().getBytes())
        checksum.update(b)
        return checksum.getValue()
    }

    /**
     * Get the hamming distance between the two numbers
     *
     * @param hash1
     * @param hash2
     * @return
     */
    static int getDistance(long hash1, long hash2) {
        def differentBits = hash1 ^ hash2 // XOR the input
        def distanceCount = 0
        while (differentBits>0) {
            distanceCount += differentBits & 1
            differentBits = differentBits >> 1
        }
        return distanceCount
    }
}

