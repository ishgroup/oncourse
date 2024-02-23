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
import ish.oncourse.server.api.service.SpecialTagsApiService
import ish.oncourse.server.api.v1.model.TagDTO
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.api.v1.service.TagApi
import ish.oncourse.server.cayenne.Tag
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

import static ish.oncourse.server.api.function.TagApiFunctions.*
import static ish.oncourse.server.api.v1.function.TagFunctions.*

class TagApiImpl implements TagApi {

    @Inject
    private ICayenneService cayenneService

    @Inject
    private AqlService aqlService

    @Inject
    private SpecialTagsApiService specialTagsApiService

    @Override
    List<TagDTO> getChecklists(String entityName, Long id) {
        def taggableClassesForEntity = taggableClassesFor(entityName)
        def expr = tagExprFor(NodeType.CHECKLIST, taggableClassesForEntity)
        def checklists = getTagsForExpression(expr, cayenneService.newContext)
        if (id != null)
            checklists = checklists.findAll { checklistAllowed(it, taggableClassesForEntity, id, aqlService) }
        checklists.collect { toRestTag(it) }
    }

    @Override
    void create(TagDTO tag) {
        createTag(tag, cayenneService.newContext)
    }


    @Override
    void createSpecial(List<TagDTO> childTags) {
        specialTagsApiService.createSpecial(childTags)
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
    List<TagDTO> getSpecialTags(String entityName) {
        specialTagsApiService.getSpecialTags(entityName)
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

    @Override
    void remove(Long id) {
        ObjectContext context = cayenneService.newContext
        Tag dbTag = CayenneFunctions
                .getRecordById(context, Tag, id)

        removeTag(dbTag, context, id)
    }

    @Override
    void removeSpecial(Long id) {
        specialTagsApiService.removeSpecial(id)
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
