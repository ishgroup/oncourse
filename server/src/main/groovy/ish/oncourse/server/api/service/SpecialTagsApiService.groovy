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
import ish.oncourse.server.api.v1.model.*
import ish.oncourse.server.cayenne.Tag
import ish.oncourse.server.cayenne.glue.TaggableCayenneDataObject
import org.apache.cayenne.query.ObjectSelect

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

import static ish.oncourse.server.api.function.TagApiFunctions.*
import static ish.oncourse.server.api.v1.function.TagFunctions.toRestSpecial
import static ish.oncourse.server.api.v1.function.TagFunctions.toRestTag

class SpecialTagsApiService {

    private final Map<NodeSpecialType, TagRequirementTypeDTO> SPECIAL_TYPES_REQUIREMENTS = [
            (NodeSpecialType.COURSE_EXTENDED_TYPES): TagRequirementTypeDTO.COURSE,
            (NodeSpecialType.CLASS_EXTENDED_TYPES) : TagRequirementTypeDTO.COURSECLASS
    ]

    @Inject
    private ICayenneService cayenneService


    SpecialTagDTO getSpecialTags(String entityName) {
        def taggableClassesForEntity = taggableClassesFor(entityName)
        def expr = tagExprFor(NodeType.TAG, taggableClassesForEntity)
        expr = expr.andExp(Tag.SPECIAL_TYPE.in(TaggableCayenneDataObject.HIDDEN_SPECIAL_TYPES))
        def hiddenTags = getTagsForExpression(expr, cayenneService.newContext)
        def hiddenTagsGrouped = (hiddenTags.collect { it.childTags }.flatten() as List<Tag>).groupBy {it.specialType}

        return hiddenTagsGrouped.isEmpty() ? null : toRestSpecial(hiddenTagsGrouped.entrySet().first().key, hiddenTagsGrouped.entrySet().first().value)
    }


    void updateSpecial(SpecialTagDTO specialTagDTO) {
        validateSpecialTag(specialTagDTO)

        def specialType = NodeSpecialType.fromDisplayName(specialTagDTO.specialType.toString())
        def childTags = specialTagDTO.childTags
        def context = cayenneService.newContext

        def specialRootTag = ObjectSelect.query(Tag)
                .where(Tag.SPECIAL_TYPE.eq(specialType).andExp(Tag.PARENT_TAG.isNull()))
                .selectFirst(context)

        TagDTO rootTagDTO
        if (!specialRootTag) {
            rootTagDTO = new TagDTO()
            rootTagDTO.name = specialType.displayName
            rootTagDTO.type = TagTypeDTO.TAG
            rootTagDTO.status = TagStatusDTO.SHOW_ON_WEBSITE
            rootTagDTO.system = true
            rootTagDTO.weight = 1

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

        createOrUpdateTag(rootTagDTO, context, specialRootTag, specialType)
    }

    private static void validateSpecialTag(SpecialTagDTO specialTagDTO) {
        def specialType = specialTagDTO.specialType
        def childTags = specialTagDTO.childTags

        if (specialType == null || !TaggableCayenneDataObject.HIDDEN_SPECIAL_TYPES.contains(NodeSpecialType.fromDisplayName(specialType.toString()))) {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ValidationErrorDTO(childTags.first()?.id?.toString(), "specialType",
                            "You can edit only special tags with this endpoint"))
                    .build())
        }

        if(!specialTagDTO.childTags.empty) {
            if (specialTagDTO.childTags.any { it -> !it.childTags.empty }) {
                throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ValidationErrorDTO(specialTagDTO.childTags.first().id?.toString(), "childTags",
                                "Special tags cannot have second level of hierarchy"))
                        .build())
            }
        }
    }
}
