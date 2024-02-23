/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.function

import ish.common.types.NodeType
import ish.oncourse.cayenne.TaggableClasses
import ish.oncourse.server.api.v1.model.TagDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.Tag
import ish.oncourse.server.cayenne.TagRequirement
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.parser.ASTTrue
import org.apache.cayenne.query.ObjectSelect

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

import static ish.oncourse.server.api.v1.function.TagFunctions.getAdditionalTaggableClasses
import static ish.oncourse.server.api.v1.function.TagFunctions.getRequirementTaggableClassForName
import static ish.oncourse.server.api.v1.function.TagFunctions.getTagGroupPrefetch
import static ish.oncourse.server.api.v1.function.TagFunctions.toDbTag
import static ish.oncourse.server.api.v1.function.TagFunctions.validateForDelete
import static ish.oncourse.server.api.v1.function.TagFunctions.validateForSave

class TagApiFunctions {

    static void createTag(TagDTO tag, ObjectContext context) {
        ValidationErrorDTO error = validateForSave(context, tag)
        if (error) {
            context.rollbackChanges()
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        }
        toDbTag(context, tag, context.newObject(Tag))

        context.commitChanges()
    }

    static Expression tagExprFor(NodeType nodeType, List<TaggableClasses> taggableClasses) {
        Tag.PARENT_TAG.isNull()
                .andExp(Tag.NODE_TYPE.eq(nodeType))
                .andExp(buildTagExprFor(taggableClasses))
    }

    static Expression buildTagExprFor(List<TaggableClasses> taggableClasses) {
        if (taggableClasses.isEmpty())
            return new ASTTrue()
        Expression tagExpr = Tag.TAG_REQUIREMENTS
                .dot(TagRequirement.ENTITY_IDENTIFIER).in(taggableClasses)
        tagExpr
    }

    static List<TaggableClasses> taggableClassesFor(String entityName) {
        TaggableClasses taggableClass = getRequirementTaggableClassForName(entityName)
        TaggableClasses[] additionalTags = getAdditionalTaggableClasses(taggableClass)
        def classes = additionalTags.collect { it }
        classes.add(taggableClass)
        return classes
    }

    static void removeTag(Tag dbTag, ObjectContext context, Long id) {
        ValidationErrorDTO error = validateForDelete(dbTag, id)
        if (error) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        }

        context.deleteObjects(dbTag)
        context.commitChanges()
    }

    static List<Tag> getTagsForExpression(Expression expression, ObjectContext context) {
        ObjectSelect.query(Tag)
                .where(expression)
                .prefetch(tagGroupPrefetch)
                .orderBy(Tag.CREATED_ON.name)
                .select(context)
    }
}
