package ish.oncourse.webservices.soap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class RandomValueFieldPopulator {
    private static final Logger logger = LogManager.getLogger();

    private static final Random random = new Random();

    public void populate(Object object) {
        ReflectionUtils.doWithFields(object.getClass(), new RandomValueFieldSetterCallback(object));
    }

    private static class RandomValueFieldSetterCallback implements ReflectionUtils.FieldCallback {
        private Object targetObject;

        public RandomValueFieldSetterCallback(Object targetObject) {
            this.targetObject = targetObject;
        }

        @Override
        public void doWith(Field field) throws IllegalAccessException {
            Class<?> fieldType = field.getType();
            if (!Modifier.isFinal(field.getModifiers())) {
                Object value = generateRandomValue(fieldType);
                ReflectionUtils.makeAccessible(field);
                field.set(targetObject, value);
            }
        }
    }

    private static Object generateRandomValue(Class<?> fieldType) {
        if (fieldType.equals(byte[].class)) {
            byte[] result = new byte[32];
            random.nextBytes(result);
            return result;
        }
        if (fieldType.equals(String.class)) {
            return UUID.randomUUID().toString();
        } else if (fieldType.equals(Boolean.TYPE) || fieldType.equals(Boolean.class)) {
            return random.nextBoolean();
        } else if (Date.class.isAssignableFrom(fieldType)) {
            return new Date(System.currentTimeMillis() - random.nextInt(60 * 60 * 1000));
        } else if (Double.class.isAssignableFrom(fieldType) || fieldType.equals(Double.TYPE)) {
            return random.nextDouble();
        } else if (fieldType.equals(Integer.TYPE) || fieldType.equals(Integer.class)) {
            return random.nextInt();
        } else if (fieldType.equals(Long.TYPE) || fieldType.equals(Long.class)) {
            return random.nextLong();
        } else if (BigDecimal.class.equals(BigDecimal.class)) {
            return BigDecimal.valueOf(random.nextDouble());
        } else if (Number.class.isAssignableFrom(fieldType)) {
            return random.nextInt(Byte.MAX_VALUE) + 1;
        } else if (Enum.class.isAssignableFrom(fieldType)) {
            Object[] enumValues = fieldType.getEnumConstants();
            return enumValues[random.nextInt(enumValues.length)];
        } else {
            logger.debug("Cannot create value of class {}", fieldType);
            return null;
        }
    }
}
