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
import ish.oncourse.aql.CompilationError
import ish.oncourse.aql.CompilationResult
import ish.oncourse.aql.impl.ExpressionUtil
import ish.oncourse.cayenne.TaggableClasses
import ish.oncourse.server.api.v1.function.TagRequirementFunctions
import ish.oncourse.server.api.v1.model.SearchCountDTO
import ish.oncourse.server.api.v1.model.SearchErrorDTO
import ish.oncourse.server.api.v1.model.TagGroupDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.Tag
import ish.oncourse.server.cayenne.TagRelation
import ish.oncourse.server.cayenne.glue.CayenneDataObject
import ish.util.EntityUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.PersistentObject
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.ExpressionException
import org.apache.cayenne.exp.Property
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

    /**
     * Validates the AQL search against the entity and counts the records it matches.
     * Unlike {@link #addAqlExp}, an invalid search isn't a bad request: the result carries the compiler errors with their positions.
     * It is also stricter: characters the lexer skips (an unclosed quote, a stray '$') make the search invalid.
     */
    static SearchCountDTO validateAndCount(String entity, String search, ObjectContext context, AqlService aql) {
        Class<? extends CayenneDataObject> clzz = entityClassForName(entity)
        ObjectSelect<CayenneDataObject> query = ObjectSelect.query(clzz) as ObjectSelect<CayenneDataObject>

        try {
            if (search?.trim()) {
                CompilationResult aqlCompile = aql.compile(search, clzz, context)
                if (aqlCompile.lexerErrors || aqlCompile.errors) {
                    List<CompilationError> errors = aqlCompile.lexerErrors + aqlCompile.errors
                    return new SearchCountDTO(valid: false, errors: errors.collect { toSearchError(it) })
                }
                query = query & aqlCompile.cayenneExpression.get()
            }
            return new SearchCountDTO(valid: true, count: countRecords(query, context))
        } catch (ExpressionException e) {
            logger.catching(e)
            return new SearchCountDTO(valid: false, errors: [new SearchErrorDTO(message: e.unlabeledMessage ?: e.message)])
        }
    }

    /**
     * Counts distinct records of the query. Can't use query.selectCount(context), because selectCount with Joins can't use Distinct correct.
     * If tables with Joins have same column names (id, createdOn, modifiedOn, ...) 'Select *' and 'Select count(*)' using Distinct will have duplicates
     */
    static Long countRecords(ObjectSelect query, ObjectContext context) {
        return query.column(Property.create("id", Long).countDistinct()).suppressDistinct().selectOne(context) as Long
    }

    private static Class<? extends CayenneDataObject> entityClassForName(String entity) {
        try {
            return EntityUtil.entityClassForName(entity.capitalize())
        } catch (RuntimeException e) {
            logger.catching(e)
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(null, 'entity', "Unknown entity: '${entity}'.")).build())
        }
    }

    // the compiler reports -1 when it doesn't know the position of an error
    private static SearchErrorDTO toSearchError(CompilationError error) {
        new SearchErrorDTO(message: error.message,
                line: error.line >= 0 ? error.line : null,
                position: error.charPositionInLine >= 0 ? error.charPositionInLine : null)
    }

    static Expression createTagGroupExpression(String alias, List<String> realPaths, List<Long> tagIds, String entity, boolean forbidden) {

        SimpleNode taggedNode = new ASTAnd()
        Map aliases = Collections.singletonMap(alias, realPaths.first())


        ASTObjPath entityPath = new ASTObjPath(alias + "+." + TagRelation.ENTITY_IDENTIFIER.name)
        entityPath.setPathAliases(aliases)

        ASTObjPath idPath = new ASTObjPath(alias+ "+." + TagRelation.TAG.name + "." + Tag.ID.name)
        idPath.setPathAliases(aliases)

        SimpleNode taggableNode = new ASTOr()

        for(curEntity in entity.split("\\|")) {
            TaggableClasses taggable = TagRequirementFunctions.getTaggableClassForName(curEntity)
            //special case: taggable class for tutor/student records is actually Contact.class
            if (taggable in [TaggableClasses.TUTOR, TaggableClasses.STUDENT]) {
                taggable = TaggableClasses.CONTACT
            }
            ASTEqual entityIdentifier = new ASTEqual(entityPath, taggable.getDatabaseValue())
            ExpressionUtil.addChild(taggableNode, entityIdentifier, taggableNode.jjtGetNumChildren())
        }

        ExpressionUtil.addChild(taggedNode, taggableNode, 0)

        Expression inExp = forbidden ? new ASTNotIn(idPath, new ASTList(tagIds)) : new ASTIn(idPath, new ASTList(tagIds))
        ExpressionUtil.addChild(taggedNode, inExp, 1)

        if(forbidden){
            //we require it to show entities without tags for checklists request, because of aql specific
            taggedNode = taggedNode.orExp(new ASTEqual(new ASTObjPath(alias+ "+"), null)) as SimpleNode
        }

        return taggedNode
    }

    static ObjectSelect addTagsExpression(List<TagGroupDTO> tagGroups, ObjectSelect query, String entity, List<TagGroupDTO> forbiddenChecklists) {

        int aliasCounter = 0

        Expression result = null

        tagGroups.each { tagGroup ->
            result = processGroup(result, tagGroup, entity, false, aliasCounter++)
        }

        forbiddenChecklists.each { tagGroup ->
            result = processGroup(result, tagGroup, entity, true, aliasCounter++)
        }

        query = query & result
        return query
    }

    private static Expression processGroup(Expression result, TagGroupDTO tagGroup, String entity, boolean forbidden, int aliasCounter){
        String alias = "${Tag.ALIAS}${aliasCounter}"

        List<String> paths = tagGroup.path != null
                ? tagGroup.path.split("\\|").collect { it + "+.${Tag.TAG_PATH}" }
                : List.of(Tag.TAG_PATH)

        Expression expr = createTagGroupExpression(alias, paths, tagGroup.tagIds, tagGroup.entity ?: entity, forbidden)

        if (result == null) {
            result = expr
        } else {
            result = result.andExp(expr)
        }
        return result
    }

    static ObjectSelect<CayenneDataObject> parseSearchQuery(ObjectSelect<CayenneDataObject> query, ObjectContext context, AqlService aql, String entity, String search, String filter, List<TagGroupDTO> tagGroups, List<TagGroupDTO> checklists = new ArrayList<>()) {

        if (filter) {
            query = addAqlExp(filter, query.getEntityType(), context, query, aql)
        }

        if (tagGroups && !tagGroups.empty || checklists && ! checklists.empty) {
            query = addTagsExpression(tagGroups,  query, entity, checklists)
        }

        if (search) {
            query = addAqlExp(search, query.getEntityType(), context, query, aql)
        }

        query
    }
}
