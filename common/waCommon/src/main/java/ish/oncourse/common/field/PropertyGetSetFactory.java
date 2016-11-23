/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.common.field;

import ish.oncourse.cayenne.FieldInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class PropertyGetSetFactory {

	private static final Logger logger = LogManager.getLogger();

	public static final String SET = "set";
	public static final String GET = "get";
	public static final String VALUE_ATTRIBUTE = "value";
	public static final String TYPE_ATTRIBUTE = "type";
	public static final String PARAM_TYPES = "parameterTypes";
	public static final String CUSTOM_FIELD_PROPERTY_PATTERN = "customField.";
	private String packageName;
			

	public PropertyGetSetFactory(String packageName) {
		this.packageName = packageName;
	}

	public PropertyGetSet get(FieldInterface field, final Object model) {
		String propertyKey = field.getProperty();
		if (propertyKey.startsWith(CUSTOM_FIELD_PROPERTY_PATTERN)) {
			return customFieldGetSet(propertyKey, model);
		} else {
			return regularGetSet(propertyKey, model);
		}
	}

	private PropertyGetSet customFieldGetSet(String propertyKey, final Object model) {
		final Method get = findMethod(GET, FieldProperty.CUSTOM_FIELD);
		final Method set = findMethod(SET, FieldProperty.CUSTOM_FIELD);
		final String customFieldKey = propertyKey.replace(CUSTOM_FIELD_PROPERTY_PATTERN, "");

		return new PropertyGetSet() {

			@Override
			public Object get() {
				try {
					return get.invoke(model, customFieldKey);
				} catch (InvocationTargetException | IllegalAccessException e) {
					logger.error(String.format("Unexpected exception occurred during perform Get custom field method, key: %s, package: %s.", customFieldKey, packageName));
					throw new RuntimeException(e);
				}
			}

			@Override
			public void set(Object value) {
				try {
					set.invoke(model, customFieldKey, value);
				} catch (InvocationTargetException | IllegalAccessException e) {
					logger.error(String.format("Unexpected exception occurred during perform  Set custom field method, key: %s, package: %s.", customFieldKey, packageName));
					throw new RuntimeException(e);
				}
			}
		};
		
	}
	
	private PropertyGetSet regularGetSet(String propertyKey, final Object model) {
		final FieldProperty property = FieldProperty.getByKey(propertyKey);
		final Method get = findMethod(GET, property);
		final Method set = findMethod(SET, property);

		return new PropertyGetSet() {

			@Override
			public Object get() {
				try {
					return get.invoke(model);
				} catch (InvocationTargetException | IllegalAccessException e) {
					logger.error(String.format("Unexpected exception occurred during perform Get method, context type: %s, property name: %s, package: %s.", property.getContextType().name(), property.name(), packageName));
					throw new RuntimeException(e);
				}
			}

			@Override
			public void set(Object value) {
				try {
					set.invoke(model, value);
				} catch (InvocationTargetException | IllegalAccessException e) {
					logger.error(String.format("Unexpected exception occurred during perform  Set method, context type: %s, property name: %s, package: %s.", property.getContextType().name(), property.name(), packageName));
					throw new RuntimeException(e);
				}
			}
		};
	}

	private Method findMethod(String type, final FieldProperty field)  {
		try {
			ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(true);
			provider.addIncludeFilter(new TypeFilter() {
				@Override
				public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) {
					AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
					return metadata.hasAnnotation(Type.class.getName()) &&
							metadata.getAnnotationAttributes(Type.class.getName()).get("value").equals(field.getContextType());
				}
			});
			Set<BeanDefinition> defeSet =	provider.findCandidateComponents(packageName);

			if (defeSet.size() < 1) {
				logger.error(String.format("Can not find corresponded class, type: %s, context type: %s, property name: %s, package: %s.",
						type, field.getContextType().name(), field.name(),packageName));
				throw new IllegalArgumentException();
			}
			ScannedGenericBeanDefinition definition = (ScannedGenericBeanDefinition)defeSet.toArray()[0];
				
			Class aClass = Class.forName(definition.getBeanClassName());

			for (MethodMetadata metadata : definition.getMetadata().getAnnotatedMethods(Property.class.getName())) {
				Map<String, Object> attributes = metadata.getAnnotationAttributes(Property.class.getName());
				if (attributes.get(VALUE_ATTRIBUTE).equals(field) && attributes.get(TYPE_ATTRIBUTE).equals(type)) {
					return aClass.getDeclaredMethod(metadata.getMethodName(), (Class<?>[]) attributes.get(PARAM_TYPES));
				}
			}
			logger.error(String.format("Can not find corresponded class/method, type: %s, context type: %s, property name: %s, package: %s.",
					type, field.getContextType().name(), field.name(),packageName));
			throw new IllegalArgumentException();
			
		} catch (ClassNotFoundException | NoSuchMethodException e) {
			logger.error(String.format("Unexpected exception.Can not find corresponded class/method, type: %s, context type: %s, property name: %s, package: %s.",
					type, field.getContextType().name(), field.name(), packageName), e);
			throw new IllegalArgumentException(e);
		}
			
	}
}
