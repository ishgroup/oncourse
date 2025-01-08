/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package io.bootique.di.spi;

import io.bootique.di.TypeLiteral;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

// Clone class of io.bootique.di.spi.GenericTypesUtils,
// in which the method 'resolveVariableType' is fixed to supprt Generic Types in Generics like 'EntityApiService'
public class GenericTypesUtils {

    public GenericTypesUtils() {}

    static Type getGenericParameterType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType)type;
            Type[] parameters = parameterizedType.getActualTypeArguments();
            if (parameters.length == 1) {
                return parameters[0];
            }
        }

        return null;
    }

    static Class<?> parameterClass(Type type) {
        Type parameterType = getGenericParameterType(type);
        return parameterType == null ? null : typeToClass(parameterType);
    }

    private static Class<?> typeToClass(Type type) {
        if (type instanceof Class) {
            return (Class)type;
        } else {
            return type instanceof ParameterizedType ? (Class)((ParameterizedType)type).getRawType() : Object.class;
        }
    }

    static TypeLiteral<?> resolveVariableType(Class<?> objectClass, Field field, Type variableType) {
        Class<?> declaringClass = field.getDeclaringClass();
        Type genericSuperclass = getSuperclassDeclaringField(objectClass, declaringClass);
        if (genericSuperclass == null) {
            return null;
        } else {
            int idx = getTypeVariableIdx(variableType.getTypeName(), declaringClass);
            if (idx == -1) {
                return null;
            } else {
                Class<?> superClass = objectClass;
                Type actualType;
                do {
                    actualType = ((ParameterizedType) superClass.getGenericSuperclass()).getActualTypeArguments()[idx];
                    if (!(actualType instanceof TypeLiteral)) {
                        return TypeLiteral.of(actualType);
                    }
                    superClass = superClass.getSuperclass();
                } while (superClass != genericSuperclass.getClass());

                return TypeLiteral.of(actualType);
            }
        }
    }

    private static Type getSuperclassDeclaringField(Class<?> objectClass, Class<?> declaringClass) {
        while(objectClass.getSuperclass() != null && !objectClass.getSuperclass().equals(declaringClass)) {
            objectClass = objectClass.getSuperclass();
        }

        Type genericSuperclass = objectClass.getGenericSuperclass();
        if (!(genericSuperclass instanceof ParameterizedType)) {
            return null;
        } else {
            return genericSuperclass;
        }
    }

    private static int getTypeVariableIdx(String typeVariableName, Class<?> declaringClass) {
        TypeVariable<? extends Class<?>>[] typeParameters = declaringClass.getTypeParameters();

        int idx;
        for(idx = 0; idx < typeParameters.length && !typeParameters[idx].getName().equals(typeVariableName); ++idx) {
        }

        return idx == typeParameters.length ? -1 : idx;
    }
}
