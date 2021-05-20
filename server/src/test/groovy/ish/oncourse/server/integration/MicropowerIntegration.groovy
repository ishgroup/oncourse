package ish.oncourse.server.integration

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.RESTClient

import javax.crypto.Cipher
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.RSAPublicKeySpec

@CompileStatic
class MicropowerIntegration {

    private static final String modulusString = '0gUU0AmpFmqte4Ei6jztrcnqwQzXYqkn+xRPZiAs8zoDQasx/JpL5PSLnB8wNcQMlj+l4LIAwTt7ex75pRafvfUhEndkM7fuoaVprF6q470BtddgnsxEiGjGeC0ylv/3ldRs3xRqc4henDRswoeOH6uZUVmPIyYZO02k8zsGd/c='
    private static final String publicExponentString = 'AQAB'

    private static final String signature = 'ish'
    private static final String identity = 'knVy3Q5e5NXqFQm68hJPIgzJRmBVmbNP5nuZ3pTT'

    private static final String baseUrl = 'https://mpsapi.micropower.com.au'

    String clubId

    private String getEncryptingData() {
        "${signature}:${identity}:${clubId}".toString()
    }

    private String getEncryption() {
        byte[] modulusBytes = Base64.decoder.decode(modulusString)
        byte[] exponentBytes = Base64.decoder.decode(publicExponentString)

        BigInteger modulus = new BigInteger(1, modulusBytes)
        BigInteger publicExponent = new BigInteger(1, exponentBytes)

        RSAPublicKeySpec rsaPubKey = new RSAPublicKeySpec(modulus, publicExponent)

        KeyFactory fact = KeyFactory.getInstance("RSA")

        PublicKey pubKey = fact.generatePublic(rsaPubKey)

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, pubKey)

        byte[] plainBytes = encryptingData.getBytes("UTF-8")
        byte[] cipherData = cipher.doFinal(plainBytes)
        byte[] encode = Base64.encoder.encode(cipherData)
        new String(encode, StandardCharsets.ISO_8859_1)
    }

    @CompileDynamic
    private getMembers() {
        RESTClient httpClient = new RESTClient(baseUrl)
        httpClient.headers['Authorization'] = "Basic ${encryption}".toString()

        httpClient.request(Method.GET, ContentType.JSON) {
            uri.path = "/v1/data/clubs/${clubId}/members".toString()

            response.success = { resp, result ->
                result
            }
        }
    }
}
