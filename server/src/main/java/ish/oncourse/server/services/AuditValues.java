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

package ish.oncourse.server.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import ish.math.Money;
import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * Before/after values of an audit entry, stored in {@code Audit.beforeValues} and {@code Audit.afterValues}.
 * <p>
 * Format: a compact JSON object {@code {"property": value}}, keys are Cayenne property names in alphabetical order.
 * <ul>
 *     <li>strings, booleans and numbers as they are, Money as its amount, null as null;</li>
 *     <li>{@code java.util.Date} as an ISO-8601 instant in UTC ({@code "2026-11-26T01:30:00Z"}),
 *     {@code LocalDate}/{@code LocalDateTime}/{@code LocalTime} in ISO-8601 ({@code "2026-11-26"});</li>
 *     <li>enums as their constant name ({@code "ACTIVE"});</li>
 *     <li>to-one relationships as the id of the related record ({@code "room": 12}); for created and deleted
 *     records only the relationships that were loaded;</li>
 *     <li>binary values as {@code "<binary>"}, credentials as {@code "***"}: see {@link #isSecret}.</li>
 * </ul>
 * An update records the changed properties only, a created record its values in "after",
 * a deleted record its values in "before". {@code id}, {@code modifiedOn} and {@code lastAccess} are never recorded.
 * {@link #parse} reads whole numbers back as Long and decimals as BigDecimal.
 */
public final class AuditValues {

    private static final Logger logger = LogManager.getLogger();

    public static final String MASK = "***";
    public static final String BINARY = "<binary>";

    private static final Set<String> IGNORED = Set.of("id", "modifiedOn", "lastAccess");
    // anywhere in the property name, ignoring case
    private static final List<String> SECRET_NAME_PARTS = List.of(
            "password", "passwd", "passphrase", "secret", "token", "credential", "sessionid");
    // a key that unlocks something; not keyCode, uniqueKey, creatorKey or primaryKey, which are ordinary identifiers
    private static final Pattern SECRET_KEY = Pattern.compile(
            "(api|access|private|secret|signing|encryption|security|licen[cs]e|auth|client|master|shared)_?key",
            Pattern.CASE_INSENSITIVE);
    // whole words of a camelCase property name, ignoring case: too short to be matched anywhere in the name
    private static final Set<String> SECRET_NAME_WORDS = Set.of("pin", "otp", "salt");
    private static final Pattern WORD_BOUNDARY = Pattern.compile("(?<=[a-z0-9])(?=[A-Z])|_");
    // values that may hold anything, credentials of integrations among them, and stored payment card tokens
    private static final Map<String, Set<String>> SECRET_PROPERTIES = Map.of(
            "IntegrationProperty", Set.of("value"),
            "Preference", Set.of("value", "valueString"),
            "Settings", Set.of("value"),
            "PaymentIn", Set.of("billingId"));

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
            .enable(DeserializationFeature.USE_LONG_FOR_INTS);

    private AuditValues() {
    }

    /**
     * @return true for the properties that are never recorded: id (the entry has it already), modifiedOn and lastAccess
     */
    public static boolean isIgnored(String property) {
        return IGNORED.contains(property);
    }

    /**
     * @return values of the record's attributes and loaded to-one relationships, nulls left out
     */
    public static Map<String, Object> snapshot(Persistent object, EntityResolver resolver) {
        Map<String, Object> values = new TreeMap<>();
        ObjEntity entity = resolver.getObjEntity(object);
        if (entity == null) {
            return values;
        }
        for (ObjAttribute attribute : entity.getAttributes()) {
            Object value = readPropertyDirectly(object, attribute.getName());
            if (value != null && !isIgnored(attribute.getName())) {
                values.put(attribute.getName(), value);
            }
        }
        for (ObjRelationship relationship : entity.getRelationships()) {
            // an unresolved relationship (a fault) is left out: a deleted record can't load it any more
            Object value = relationship.isToMany() ? null : readPropertyDirectly(object, relationship.getName());
            if (value instanceof Persistent) {
                values.put(relationship.getName(), value);
            }
        }
        return values;
    }

    /**
     * @return values as JSON in the format above, or null when there are none
     */
    public static String toJson(String entityName, Map<String, ?> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        Map<String, Object> json = new TreeMap<>();
        values.forEach((property, value) -> json.put(property, isSecret(entityName, property) && value != null ? MASK : normalize(value)));
        try {
            return MAPPER.writeValueAsString(json);
        } catch (JsonProcessingException e) {
            logger.warn("Can not write audit values of {}", entityName, e);
            return null;
        }
    }

    /**
     * @return values read from JSON written by {@link #toJson}; empty when there are none or the JSON is broken
     */
    public static Map<String, Object> parse(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return MAPPER.readValue(json, new TypeReference<LinkedHashMap<String, Object>>() {});
        } catch (JsonProcessingException e) {
            // the values may hold personal data and be large: log the error only, not the JSON
            logger.warn("Can not read audit values ({} characters): {}", json.length(), e.getOriginalMessage());
            return Collections.emptyMap();
        }
    }

    /**
     * @return true for the properties whose values are recorded as {@code "***"}: credentials by their name
     * (password, passphrase, secret, token, credential, session id, API/access/private/... key, PIN, OTP, salt)
     * and the values of integration properties, preferences, settings and payment card tokens
     */
    static boolean isSecret(String entityName, String property) {
        String name = property.toLowerCase(Locale.ROOT);
        return SECRET_NAME_PARTS.stream().anyMatch(name::contains)
                || SECRET_KEY.matcher(property).find()
                || Arrays.stream(WORD_BOUNDARY.split(property)).anyMatch(word -> SECRET_NAME_WORDS.contains(word.toLowerCase(Locale.ROOT)))
                || SECRET_PROPERTIES.getOrDefault(entityName, Set.of()).contains(property);
    }

    private static Object normalize(Object value) {
        if (value == null || value instanceof String || value instanceof Boolean || value instanceof Number) {
            return value;
        }
        if (value instanceof Money) {
            return ((Money) value).toBigDecimal();
        }
        if (value instanceof Date) {
            return Instant.ofEpochMilli(((Date) value).getTime()).toString();
        }
        if (value instanceof TemporalAccessor) {
            return value.toString();
        }
        if (value instanceof Enum) {
            return ((Enum<?>) value).name();
        }
        if (value instanceof byte[]) {
            return BINARY;
        }
        if (value instanceof Persistent) {
            return id(((Persistent) value).getObjectId());
        }
        if (value instanceof ObjectId) {
            return id((ObjectId) value);
        }
        return value.toString();
    }

    private static Object id(ObjectId id) {
        if (id == null) {
            return null;
        }
        if (id.isTemporary()) {
            if (!id.isReplacementIdAttached()) {
                return null;
            }
            id = id.createReplacementId();
        }
        Map<String, Object> snapshot = id.getIdSnapshot();
        return snapshot.size() == 1 ? snapshot.values().iterator().next() : snapshot.toString();
    }

    private static Object readPropertyDirectly(Persistent object, String property) {
        return object instanceof DataObject ? ((DataObject) object).readPropertyDirectly(property) : null;
    }
}
