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

package ish.oncourse.server.api.function

import ish.oncourse.aql.AqlService
import ish.oncourse.aql.CompilationResult
import ish.oncourse.aql.impl.ExpressionUtil
import ish.oncourse.cayenne.TaggableClasses
import ish.oncourse.server.api.v1.function.TagFunctions
import ish.oncourse.server.api.v1.model.TagGroupDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.Tag
import ish.oncourse.server.cayenne.TagRelation
import ish.oncourse.server.cayenne.glue.CayenneDataObject
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.PersistentObject
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.ExpressionException
import org.apache.cayenne.exp.parser.*
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

class EntityFunctions {

    private static Logger logger = LogManager.logger

    static ValidationErrorDTO validateIdParam(Long id) {
        if (!id) {
            return new ValidationErrorDTO(id?.toString(), 'id', "Incorrect ID value: '$id'. ID must be number")
        }
        null
    }

    static ValidationErrorDTO validateEntityExistence(Long id, PersistentObject entity) {
        if (!entity) {
            return new ValidationErrorDTO(id?.toString(),'id',"Entity with id = '$id' doesn't exist")
        }
        null
    }

    static void checkForBadRequest(ValidationErrorDTO error) {
        if (error) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        }
    }

    static ObjectSelect addAqlExp(String search, Class clzz, ObjectContext context, ObjectSelect query, AqlService aql) {
        if (search) {
            CompilationResult aqlCompile = aql.compile(search, clzz, context)
            if (!aqlCompile.errors) {
                try {
                    return query & aqlCompile.cayenneExpression.get()
                } catch (ExpressionException e) {
                    logger.catching(e)
                    throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(null, null, "Invalid search expression: '${search}'.")).build())
                }
            } else {
                throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(null, null, "Invalid search expression: '${search}'.")).build())
            }
        }
        return query
    }

    static Expression createTagGroupExpression(String alias, String realPath, List<Long> tagIds, String entity) {
        TaggableClasses taggable = TagFunctions.taggableClassesBidiMap.get(entity)
        //special case: taggable class for tutor/student records is actually Contact.class
        if(taggable in [TaggableClasses.TUTOR,TaggableClasses.STUDENT]) {
            taggable = TaggableClasses.CONTACT
        }

        SimpleNode taggedNode = new ASTAnd()
        Map aliases = Collections.singletonMap(alias, realPath)


        ASTObjPath entityPath = new ASTObjPath(alias + "+." + TagRelation.ENTITY_IDENTIFIER.name)
        entityPath.setPathAliases(aliases)

        ASTObjPath idPath = new ASTObjPath(alias+ "+." + TagRelation.TAG.name + "." + Tag.ID.name)
        idPath.setPathAliases(aliases)

        ASTEqual entityIdentifier = new ASTEqual(entityPath, taggable.getDatabaseValue())
        ASTIn tagId = new ASTIn(idPath, new ASTList(tagIds))

        ExpressionUtil.addChild(taggedNode, entityIdentifier, 0)
        ExpressionUtil.addChild(taggedNode, tagId, 1)

        return taggedNode
    }

    static ObjectSelect addTagsExpression(List<TagGroupDTO> tagGroups, ObjectSelect query, String entity) {

        int aliasCounter = 0

        Expression result = null

        tagGroups.each { tagGroup ->

            String alias = "${Tag.ALIAS}${aliasCounter++}"

            String path = tagGroup.path != null ? "${tagGroup.path}+.${Tag.TAG_PATH}" : Tag.TAG_PATH

            Expression expr = createTagGroupExpression(alias, path, tagGroup.tagIds, tagGroup.entity?:entity)

            if (result == null) {
                result = expr
            } else {
                result = result.andExp(expr)
            }

        }

        query = query & result
        return query
    }

    static ObjectSelect<CayenneDataObject> parseSearchQuery(ObjectSelect<CayenneDataObject> query, ObjectContext context, AqlService aql, String entity, String search, String filter, List<TagGroupDTO> tagGroups) {

        if (filter) {
            query = addAqlExp(filter, query.getEntityType(), context, query, aql)
        }

        if (tagGroups && !tagGroups.empty) {
            query = addTagsExpression(tagGroups,  query, entity)
        }

        if (search) {
            query = addAqlExp(search, query.getEntityType(), context, query, aql)
        }

        query
    }
}
