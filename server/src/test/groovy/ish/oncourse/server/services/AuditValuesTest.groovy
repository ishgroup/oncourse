/*
 * Copyright ish group pty ltd 2026.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.services

import org.junit.jupiter.api.Test
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList

import javax.xml.parsers.DocumentBuilderFactory

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertFalse
import static org.junit.jupiter.api.Assertions.assertTrue

/**
 * Credentials must never be written to Audit.beforeValues/afterValues.
 */
class AuditValuesTest {

    /**
     * Every attribute of the Cayenne model that stores a credential or may store one.
     * A new attribute whose name looks like a credential fails {@link #testMaskedAttributesOfTheModel} until it is added here.
     */
    private static final Set<String> SECRET_ATTRIBUTES = [
            'ApiToken.secret',
            'CheckoutSession.sessionId',
            'CorporatePass.password',
            'IntegrationProperty.value',
            'PaymentIn.billingId',
            'PaymentIn.sessionId',
            'Preference.value',
            'Preference.valueString',
            'Settings.value',
            'SystemUser.invitationToken',
            'SystemUser.invitationTokenExpiryDate',
            'SystemUser.password',
            'SystemUser.passwordLastChanged',
            'SystemUser.passwordUpdateRequired',
            'SystemUser.sessionId',
            'SystemUser.token',
            'SystemUser.tokenScratchCodes',
    ] as Set

    @Test
    void testMaskedAttributesOfTheModel() {
        Set<String> masked = modelAttributes().findAll { String attribute ->
            String[] names = attribute.split('\\.')
            AuditValues.isSecret(names[0], names[1])
        } as Set

        assertEquals(SECRET_ATTRIBUTES.sort(), masked.sort())
    }

    @Test
    void testCredentialsAreMaskedByName() {
        ['password', 'smtpPassword', 'passwd', 'passphrase', 'clientSecret', 'secretAccessKey', 'token', 'refreshToken',
         'credentials', 'awsCredential', 'sessionId', 'apiKey', 'apikey', 'API_KEY', 'accessKey', 'accessKeyId',
         'privateKey', 'secretKey', 'signingKey', 'encryptionKey', 'securityKey', 'licenseKey', 'authKey', 'clientKey',
         'pin', 'PIN', 'pinCode', 'cardPin', 'otp', 'otpSeed', 'salt', 'passwordSalt'].each { String property ->
            assertTrue(AuditValues.isSecret('Contact', property), property)
        }
    }

    @Test
    void testOrdinaryPropertiesAreNotMasked() {
        ['keyCode', 'keycode', 'key', 'uniqueKey', 'creatorKey', 'primaryKey', 'foreignKey', 'name', 'isPinned',
         'shipping', 'spinner', 'hotpot', 'salting', 'description', 'value', 'billingId', 'code'].each { String property ->
            assertFalse(AuditValues.isSecret('Contact', property), property)
        }
    }

    @Test
    void testSecretValuesAreWrittenMasked() {
        Map<String, Object> values = AuditValues.parse(AuditValues.toJson('IntegrationProperty',
                [keyCode: 'apiKey', value: 'abc', accessKey: 'AKIA', privateKey: 'pk', name: 'Xero', pin: null]))

        assertEquals([accessKey: AuditValues.MASK, keyCode: 'apiKey', name: 'Xero', pin: null,
                      privateKey: AuditValues.MASK, value: AuditValues.MASK], values)
    }

    private static Set<String> modelAttributes() {
        InputStream map = AuditValuesTest.classLoader.getResourceAsStream('cayenne/AngelMap.map.xml')
        assert map != null
        Document model = map.withCloseable { DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(it) }
        Set<String> attributes = [] as Set
        NodeList entities = model.getElementsByTagName('obj-entity')
        for (int i = 0; i < entities.length; i++) {
            Element entity = entities.item(i) as Element
            NodeList entityAttributes = entity.getElementsByTagName('obj-attribute')
            for (int j = 0; j < entityAttributes.length; j++) {
                attributes.add("${entity.getAttribute('name')}.${(entityAttributes.item(j) as Element).getAttribute('name')}".toString())
            }
        }
        assert attributes.size() > 1000
        return attributes
    }
}
