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

package ish.oncourse.aql.model;

import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;

import java.util.*;

/**
 * Runtime description of Cayenne entity.
 *
 * Essentially this class is just a wrapper around {@link ObjEntity},
 * with some optional information about custom fields available for this ObjEntity.
 *
 * @see EntityFactory
 *

 */
public class Entity {

    private final EntityFactory factory;
    private final ObjEntity objEntity;
    private final Map<String, Class<?>> customAttributes;
    private final Map<String, SyntheticAttributeDescriptor> syntheticAttributes;

    Entity(EntityFactory factory, ObjEntity objEntity, Map<String, Class<?>> customAttributes,
           Collection<? extends SyntheticAttributeDescriptor> syntheticAttributes) {
        this.factory = factory;
        this.objEntity = Objects.requireNonNull(objEntity);
        this.customAttributes = Objects.requireNonNull(customAttributes);
        this.syntheticAttributes = new HashMap<>();
        syntheticAttributes.forEach(attr -> Entity.this.syntheticAttributes.put(attr.getAttributeName(), attr));
    }

    public Optional<Class<?>> getAttribute(String name) {
        var attribute = objEntity.getAttribute(name);
        if(attribute != null) {
            return Optional.of(attribute.getJavaClass());
        }

        var type = customAttributes.get(name);
        if(type != null) {
            return Optional.of(type);
        }

        return Optional.empty();
    }

    public Optional<Entity> getRelationship(String name) {
        var relationship = objEntity.getRelationship(name);
        if(relationship != null) {
            return Optional.of(factory.createEntity(relationship.getTargetEntity()));
        }

        return Optional.empty();
    }

    public Optional<SyntheticAttributeDescriptor> getSyntheticAttribute(String name) {
        var attribute = syntheticAttributes.get(name);
        if(attribute != null) {
            return Optional.of(attribute);
        }

        if(objEntity.getSuperEntity() != null) {
            Entity superEntity = factory.createEntity(objEntity.getSuperEntity());
            attribute = superEntity.syntheticAttributes.get(name);

            if(attribute != null) {
                return Optional.of(attribute);
            }
        }

        return Optional.empty();
    }

    public String getName() {
        return objEntity.getName();
    }

    public Class<?> getJavaClass() {
        try {
            return Class.forName(objEntity.getJavaClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to load class " + objEntity.getJavaClassName()
                    + " for entity " + objEntity.getName(), e);
        }
    }

}
