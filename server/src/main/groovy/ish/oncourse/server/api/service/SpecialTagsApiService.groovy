/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.service

import com.google.inject.Inject
import ish.common.types.NodeSpecialType
import ish.common.types.NodeType
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.function.CayenneFunctions
import ish.oncourse.server.api.function.TagApiFunctions
import ish.oncourse.server.api.v1.model.*
import ish.oncourse.server.cayenne.Tag
import ish.oncourse.server.cayenne.glue.TaggableCayenneDataObject
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

import static ish.oncourse.server.api.function.TagApiFunctions.*
import static ish.oncourse.server.api.v1.function.TagFunctions.toRestTag

class SpecialTagsApiService {

    private final Map<NodeSpecialType, TagRequirementTypeDTO> SPECIAL_TYPES_REQUIREMENTS = [
            (NodeSpecialType.COURSE_EXTENDED_TYPES): TagRequirementTypeDTO.COURSE,
            (NodeSpecialType.CLASS_EXTENDED_TYPES) : TagRequirementTypeDTO.COURSECLASS
    ]

    @Inject
    private ICayenneService cayenneService


    List<TagDTO> getSpecialTags(String entityName) {
        def taggableClassesForEntity = taggableClassesFor(entityName)
        def expr = tagExprFor(NodeType.TAG, taggableClassesForEntity)
        expr = expr.andExp(Tag.SPECIAL_TYPE.in(TaggableCayenneDataObject.HIDDEN_SPECIAL_TYPES))
        def hiddenTags = getTagsForExpression(expr, cayenneService.newContext)
        return (hiddenTags.collect { it.childTags }.flatten() as List<Tag>).collect { toRestTag(it) }
    }


    void updateSpecial(List<TagDTO> childTags) {
        validateSpecialTags(childTags)

        def specialType = NodeSpecialType.fromDisplayName(childTags.first().specialType.toString())
        def context = cayenneService.newContext

        def specialRootTag = ObjectSelect.query(Tag)
                .where(Tag.SPECIAL_TYPE.eq(specialType).andExp(Tag.PARENT_TAG.isNull()))
                .selectFirst(context)

        if (childTags.empty) {
            if (specialRootTag) {
                context.deleteObject(specialRootTag)
                context.commitChanges()
            }

            return
        }

        TagDTO rootTagDTO
        if (!specialRootTag) {
            rootTagDTO = new TagDTO()
            rootTagDTO.specialType = childTags.first().specialType
            rootTagDTO.name = specialType.displayName
            rootTagDTO.type = TagTypeDTO.TAG
            rootTagDTO.system = true

            TagRequirementTypeDTO requirementTypeDTO = SPECIAL_TYPES_REQUIREMENTS.get(specialType)
            def specialTagRequirementDTO = new TagRequirementDTO()
            specialTagRequirementDTO.type = requirementTypeDTO
            specialTagRequirementDTO.system = true
            specialTagRequirementDTO.limitToOneTag = true
            specialTagRequirementDTO.mandatory = false

            rootTagDTO.requirements = [specialTagRequirementDTO]
        } else {
            rootTagDTO = toRestTag(specialRootTag)
        }

        rootTagDTO.childTags = childTags
        createTag(rootTagDTO, context)
    }

    private static void validateSpecialTags(List<TagDTO> childTags) {
        def specialTypes = childTags.collect { NodeSpecialType.fromDisplayName(it.specialType?.toString()) }.unique().findAll { it }

        if (specialTypes.empty || !TaggableCayenneDataObject.HIDDEN_SPECIAL_TYPES.containsAll(specialTypes) ||
                childTags.any { !it.specialType }) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ValidationErrorDTO(childTags.first().id?.toString(), "specialType",
                            "You can create only special tags with this endpoint"))
                    .build())
        }

        if (specialTypes.size() > 1) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ValidationErrorDTO(childTags.first().id?.toString(), "specialType",
                            "Special tags cannot have different special types for this endpoint"))
                    .build())
        }

        if (childTags.any { it -> !it.childTags.empty }) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ValidationErrorDTO(childTags.first().id?.toString(), "childTags",
                            "Special tags cannot have second level of hierarchy"))
                    .build())
        }
    }
}
