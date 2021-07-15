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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class PropertyGetSetFactory {

	private static final Logger logger = LogManager.getLogger();

	public static final String SET = "set";
	public static final String GET = "get";
	public static final String VALUE_ATTRIBUTE = "value";
	public static final String TYPE_ATTRIBUTE = "type";
	public static final String PARAM_TYPES = "params";
	public static final String CUSTOM_FIELD_PROPERTY_PATTERN = "customField.";
	public static final String TAG_PATTERN = "tag/";
	public static final String TAG_S_PATTERN = "singleTag/";
	public static final String TAG_M_PATTERN = "multipleTag/";
	public static final String MAILING_LIST_FIELD_PATTERN = "mailingList/";
	private final String packageName;


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
		final FieldProperty property = FieldProperty.getByKey(propertyKey);

		final Method get = findMethod(GET, property);
		final Method set = findMethod(SET, property);
		final String customFieldKey = propertyKey.split("\\.")[2];

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

			@Override
			public Class getType() {
				return get.getReturnType();
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

			@Override
			public Class getType() {
				return get.getReturnType();
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
					return aClass.getDeclaredMethod(metadata.getMethodName(), attributes.get(PARAM_TYPES)!=null?(Class[]) attributes.get(PARAM_TYPES):new Class[0]);
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

	private Class[] convertTypes(Object types) {
		 ArrayList<Class> classes = new ArrayList<>();

		if (types != null) {
			org.springframework.asm.Type[] typesArry =(org.springframework.asm.Type[]) types;
			for (org.springframework.asm.Type aTypesArry : typesArry) {
				try {
					classes.add(this.getClass().getClassLoader().loadClass(aTypesArry.getClassName()));
				} catch (ClassNotFoundException e) {
					logger.error(String.format("Unexpected exception.Can not load corresponded class type: %s", aTypesArry.getClassName()), e);
					throw new IllegalArgumentException(e);
				}
			}
		}

		return classes.toArray(new Class[classes.size()]);
	}
}
