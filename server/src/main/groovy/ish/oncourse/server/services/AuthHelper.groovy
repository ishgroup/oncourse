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

package ish.oncourse.server.services

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.KeyUse
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import groovy.time.TimeCategory
import groovy.transform.CompileDynamic

@CompileDynamic
class AuthHelper {

    static String generateJwk(String keyId) {
        new RSAKeyGenerator(RSAKeyGenerator.MIN_KEY_SIZE_BITS)
                .keyUse(KeyUse.SIGNATURE) // indicate the intended use of the key
                .keyID(keyId)
                .algorithm(JWSAlgorithm.RS256)// give the key a unique ID
                .generate().toJSONString()
    }

    static String generateJwt(String jwkString, String subject, String issuer, String audience, String tokenAudience) {
        RSAKey jwk = RSAKey.parse(jwkString)
        JWSSigner signer = new RSASSASigner(jwk)
        Date issuedAtTime = new Date()
        Date authTokenExpary = null
        use (TimeCategory) {
            authTokenExpary = issuedAtTime + 1.hour
        }
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(subject)
                .issuer(issuer)
                .audience(audience)
                .claim('token.aud', tokenAudience)
                .issueTime(issuedAtTime)
                .expirationTime(authTokenExpary)
                .build()

        JWSHeader headers = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID(jwk.keyID)
                .build()

        SignedJWT signedJWT = new SignedJWT(headers, claims)

        signedJWT.sign(signer)
        return signedJWT.serialize()
    }

}
