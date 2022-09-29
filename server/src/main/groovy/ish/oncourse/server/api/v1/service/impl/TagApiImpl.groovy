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

package ish.oncourse.server.api.v1.service.impl

import com.google.inject.Inject
import ish.common.types.NodeType
import ish.oncourse.aql.AqlService
import ish.oncourse.cayenne.TaggableClasses
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.function.CayenneFunctions
import ish.oncourse.server.api.v1.model.TagDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.api.v1.service.TagApi
import ish.oncourse.server.cayenne.Tag
import ish.oncourse.server.cayenne.TagRequirement
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.parser.ASTTrue
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

import static ish.oncourse.server.api.v1.function.TagFunctions.*

class TagApiImpl implements TagApi {

    @Inject
    private ICayenneService cayenneService

    @Inject
    private AqlService aqlService

    @Override
    List<TagDTO> getChecklists(String entityName, Long id) {
        def taggableClassesForEntity = taggableClassesFor(entityName)
        def expr = tagExprFor(NodeType.CHECKLIST, taggableClassesForEntity)
        def checklists = ObjectSelect.query(Tag)
                .where(expr)
                .prefetch(tagGroupPrefetch)
                .orderBy(Tag.CREATED_ON.name)
                .select(cayenneService.newContext)
        if(id != null)
            checklists = checklists.findAll {checklistAllowed(it, taggableClassesForEntity, id, aqlService)}
        checklists.collect {toRestTag(it)}
    }

    @Override
    void create(TagDTO tag) {
        ObjectContext context = cayenneService.newContext

        ValidationErrorDTO error = validateForSave(context, tag)
        if (error) {
            context.rollbackChanges()
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        }
        toDbTag(context, tag, context.newObject(Tag))

        context.commitChanges()


    }

    @Override
    TagDTO getTag(Long id) {
        ObjectContext context = cayenneService.newContext

        def tag = SelectById.query(Tag, id).selectOne(context)
        if (tag == null) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity("Record with id = " + id + " doesn't exist.").build())
        }

        toRestTag(tag)
    }

    @Override
    List<TagDTO> get(String entityName) {
        ObjectContext context = cayenneService.newContext

        Map<Long, Integer> childCountMap = ObjectSelect.query(Tag)
                .columns(Tag.ID, Tag.TAG_RELATIONS.count())
                .select(context)
                .collectEntries { [(it[0]): it[1]] }


        def taggableClassesForEntity = new ArrayList<TaggableClasses>()
        if (entityName) {
            taggableClassesForEntity = taggableClassesFor(entityName)
        }
        def expr = tagExprFor(NodeType.TAG, taggableClassesForEntity)
        ObjectSelect.query(Tag)
                .where(expr)
                .prefetch(tagGroupPrefetch)
                .orderBy(Tag.NAME.asc())
                .select(cayenneService.newContext)
                .collect { toRestTag(it, childCountMap) }
    }

    private static Expression tagExprFor(NodeType nodeType, List<TaggableClasses> taggableClasses){
        Tag.PARENT_TAG.isNull()
                .andExp(Tag.NODE_TYPE.eq(nodeType))
                .andExp(buildTagExprFor(taggableClasses))
    }

    private static Expression buildTagExprFor(List<TaggableClasses> taggableClasses){
        if(taggableClasses.isEmpty())
            return new ASTTrue()
        Expression tagExpr = Tag.TAG_REQUIREMENTS
                .dot(TagRequirement.ENTITY_IDENTIFIER).in(taggableClasses)
        tagExpr
    }

    private static List<TaggableClasses> taggableClassesFor(String entityName){
        TaggableClasses taggableClass = getRequirementTaggableClassForName(entityName)
        TaggableClasses[] additionalTags = getAdditionalTaggableClasses(taggableClass)
        def classes = additionalTags.collect {it}
        classes.add(taggableClass)
        return classes
    }

    @Override
    void remove(Long id) {
        ObjectContext context = cayenneService.newContext
        Tag dbTag = CayenneFunctions
                .getRecordById(context, Tag, id)

        ValidationErrorDTO error = validateForDelete(dbTag, id)
        if (error) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        }

        context.deleteObjects(dbTag)
        context.commitChanges()
    }

    @Override
    void update(Long id, TagDTO tag) {
        ObjectContext context = cayenneService.newContext

        Tag dbTag = CayenneFunctions
                .getRecordById(context, Tag, id, tagGroupPrefetch)

        ValidationErrorDTO error = validateForSave(context, tag)
        if (error) {
            context.rollbackChanges()
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
        }
        toDbTag(context, tag, dbTag)

        context.commitChanges()
    }
}
