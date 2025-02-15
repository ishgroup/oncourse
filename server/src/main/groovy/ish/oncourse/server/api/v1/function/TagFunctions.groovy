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

package ish.oncourse.server.api.v1.function

import groovy.transform.CompileStatic
import ish.common.types.NodeSpecialType
import ish.common.types.NodeType
import ish.oncourse.aql.AqlService
import ish.oncourse.cayenne.Taggable
import ish.oncourse.cayenne.TaggableClasses
import ish.oncourse.function.GetTagGroupsInterface
import ish.oncourse.server.api.BidiMap
import ish.oncourse.server.api.v1.model.*
import ish.oncourse.server.api.validation.TagValidation
import ish.oncourse.server.cayenne.*
import ish.oncourse.server.cayenne.glue.TaggableCayenneDataObject
import ish.oncourse.server.function.GetTagGroups
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Property
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.PrefetchTreeNode
import org.apache.commons.lang3.StringUtils

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response
import java.time.ZoneOffset
import java.util.stream.Collectors

import static ish.oncourse.server.api.function.EntityFunctions.addAqlExp
import static ish.oncourse.server.api.v1.function.TagRequirementFunctions.toRestRequirement
import static ish.oncourse.server.api.v1.function.TagRequirementFunctions.validateTagRequirementsForSave
import static org.apache.commons.lang3.StringUtils.trimToNull

@CompileStatic
class TagFunctions {

    private static Map<Long, Integer> childCountMapOf(ObjectContext context) {
        return ObjectSelect.query(Tag)
                .columns(Tag.ID, Tag.TAG_RELATIONS.count())
                .select(context)
                .collectEntries { [(it[0]): it[1]] }
    }

    static SpecialTagDTO toRestSpecial(NodeSpecialType specialType, List<Tag> childTags) {
        return new SpecialTagDTO().with { dto ->
            dto.specialType = SpecialTagTypeDTO.fromValue(specialType.displayName)
            dto.childTags = childTags.sort { it.weight }.collect { toRestTag(it) }
            dto
        }
    }

    static TagDTO toRestTag(Tag dbTag, Map<Long, Integer> childCountMap = childCountMapOf(dbTag.context), boolean isParent = true) {
        new TagDTO().with { tag ->
            tag.id = dbTag.id
            tag.name = dbTag.name
            tag.status = dbTag.isWebVisible ? TagStatusDTO.SHOW_ON_WEBSITE : TagStatusDTO.PRIVATE
            tag.urlPath = dbTag.shortName
            tag.content = dbTag.contents
            tag.type = TagTypeDTO.fromValue(dbTag.nodeType.displayName)
            tag.system = dbTag.specialType != null
            tag.created = dbTag.createdOn?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()
            tag.modified = dbTag.modifiedOn?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()
            tag.taggedRecordsCount = getTaggedRecordsCount(dbTag, childCountMap)
            tag.childrenCount = getChildrenCount(dbTag)
            tag.weight = dbTag.weight
            tag.color = dbTag.colour
            tag.shortWebDescription = dbTag.shortWebDescription
            if (isParent) {
                tag.requirements = dbTag.tagRequirements.collect { req ->
                    toRestRequirement(req, dbTag)
                }
            }

            tag.childTags = dbTag.childTags.sort { it.weight }.collect { toRestTag(it, childCountMap, false) }
            tag
        }
    }

    static TagDTO toRestTagMinimized(Tag dbTag) {
        new TagDTO().with { tag ->
            tag.id = dbTag.id
            tag.name = dbTag.name
            tag.type = TagTypeDTO.TAG
            tag
        }
    }

    static int getTaggedRecordsCount(Tag dbTag, Map<Long, Integer> childCountMap) {
        int count = childCountMap[dbTag.id] ?: 0
        count += dbTag.childTags.collect { getTaggedRecordsCount(it, childCountMap) }.sum() as Integer ?: 0
        count
    }

    static int getChildrenCount(Tag dbTag) {
        int count = dbTag.childTags.size()
        count += dbTag.childTags.collect { getChildrenCount(it) }.sum() as Integer ?: 0
        count
    }

    static ValidationErrorDTO validateForDelete(Tag dbTag, Long id) {
        if (!dbTag) {
            return new ValidationErrorDTO(id?.toString(), 'id', 'Tag group is not exist')
        }

        if (dbTag.specialType) {
            String errorMessage = 'Tag group can not be deleted'
            switch (dbTag.specialType) {
                case NodeSpecialType.SUBJECTS:
                    errorMessage += ' This entity represents the categories of courses/products on your web site and cannot be deleted.'
                    break
                case NodeSpecialType.TERMS:
                    errorMessage += ' This tag group represents the categories of classes on your web site and cannot be deleted.'
                    break
                case NodeSpecialType.ASSESSMENT_METHOD:
                    errorMessage += ' This tag group is required for the assessments.'
                    break
                case NodeSpecialType.PAYROLL_WAGE_INTERVALS:
                    errorMessage += ' This tag group is required for the onCourse tutor pay feature.'
                    break
                default:
                    break
            }
            return new ValidationErrorDTO(id?.toString(), 'id', errorMessage)
        }

        null
    }

    static ValidationErrorDTO validateForSave(ObjectContext context, TagDTO tag, boolean subjectsAsEntity = false) {

        ValidationErrorDTO error = validateTag(tag)
        if (error) {
            return error
        }

        Tag dbTag = ObjectSelect.query(Tag)
                .where(Tag.NAME.eq(tag.name))
                .and(Tag.PARENT_TAG.isNull())
                .selectOne(context)

        if (dbTag != null && dbTag.id != tag.id) {
            return new ValidationErrorDTO(tag.id?.toString(), 'name', 'Name should be unique.')
        }

        if (dbTag && NodeSpecialType.SUBJECTS == dbTag.specialType && subjectsAsEntity) {
            error = validateSubjectAsEntity(tag, dbTag)
            if (error)
                return error
        }

        Set<String> notValidNames = new HashSet<>()
        validateNamesOfNewTag(tag, notValidNames)
        if (notValidNames.size() > 0) {
            return new ValidationErrorDTO(null, 'name', "\'${notValidNames[0]}\' has forbidden symbols. The tag name can not contain \", \\, #.")
        }

        if (validateTagNameUniqueness(tag)) {
            return new ValidationErrorDTO(null, 'name', 'The tag name is not unique within its parent tag.')
        }

        if (validateUrlPathUniqueness(tag)) {
            return new ValidationErrorDTO(null, 'name', 'The tag url path is not unique within its parent tag.')
        }

        return null
    }

    static ValidationErrorDTO validateTag(TagDTO tag, boolean root = true) {
        if (!tag.name?.trim()) {
            return new ValidationErrorDTO(tag.id?.toString(), 'name', 'Name should be set.')
        } else if (tag.name.length() > 512) {
            return new ValidationErrorDTO(tag.id?.toString(), 'name', 'The maximum length is 512.')
        }

        if (tag.urlPath?.trim()) {
            if (tag.urlPath.length() > 128) {
                return new ValidationErrorDTO(tag.id?.toString(), 'urlPath', 'The maximum length is 128.')
            }

            String errorMessage = TagValidation.validateShortName(tag.urlPath)
            if (errorMessage) {
                return new ValidationErrorDTO(tag.id?.toString(), 'urlPath', errorMessage)
            }
        }

        if (tag.content?.trim() && tag.content.length() > 32000) {
            return new ValidationErrorDTO(tag.id?.toString(), 'name', 'The maximum length is 32000.')
        }

        if (!tag.status) {
            return new ValidationErrorDTO(tag.id?.toString(), 'status', 'Status can not be null.')
        }

        if (root) {
            if (tag.requirements.size() < 1) {
                return new ValidationErrorDTO(tag.id?.toString(), 'requirements', 'At least one requirement should be set for root tag.')
            } else if (tag.requirements.stream().anyMatch { rq -> rq.type == null }) {
                return new ValidationErrorDTO(tag.id?.toString(), 'requirements', 'Invalid requirement type.')
            }
        } else if (!tag.requirements.empty) {
            return new ValidationErrorDTO(tag.id?.toString(), 'requirements', 'Requirements can be applied only for root tag.')
        }

        tag.childTags.collect { validateTag(it, false) }.find()
    }

    static boolean validateTagNameUniqueness(TagDTO tag) {
        tag.childTags.collect { validateTagNameUniqueness(it) }.contains(true) || tag.childTags.size() != tag.childTags*.name.unique().size()
    }

    static boolean validateNamesOfNewTag(TagDTO tag, Set notValidNames) {
        tag.childTags.each { validateNamesOfNewTag(it, notValidNames) }
        if ((!isNameValid(tag.name) && tag.id == null)) {
            notValidNames.add(tag.name)
            return true
        } else {
            return false
        }
    }

    static boolean validateUrlPathUniqueness(TagDTO tag) {
        tag.childTags.collect { validateUrlPathUniqueness(it) }.contains(true) || tag.childTags*.urlPath.findAll().size() != tag.childTags*.urlPath.findAll().unique().size()
    }

    private static boolean isNameValid(String name) {
        return !(name.contains("\"") || name.contains("\\") || name.contains("#"))
    }

    static Tag toDbTag(ObjectContext context, TagDTO tag, Tag dbTag, boolean isParent = true, Map<Long, Tag> childTagsToRemove = getAllChildTags(dbTag)) {

        Map<Long, TagRequirement> requirementMap = dbTag.tagRequirements.collectEntries { [(it.id), it] }

        tag.requirements.each { r ->
            TagRequirement tagRequirement = r.id ? requirementMap.remove(r.id) : context.newObject(TagRequirement)
            tagRequirement.entityIdentifier = TagRequirementFunctions.tagRequirementBidiMap.getByValue(r.type)
            tagRequirement.isRequired = r.mandatory
            tagRequirement.displayRule = r.displayRule?.empty ? null : r.displayRule
            tagRequirement.manyTermsAllowed = !r.limitToOneTag
            tagRequirement.tag = dbTag
        }

        List<TaggableClasses> deletedEntityList = requirementMap.values().collect { it.entityIdentifier }

        if (isParent) {
            ValidationErrorDTO error = validateTagRequirementsForSave(dbTag, deletedEntityList)
            if (error != null) {
                throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(error).build())
            }
        }

        _toDbTag(context, tag, dbTag, isParent, deletedEntityList, childTagsToRemove);
        context.deleteObjects(requirementMap.values())
        context.deleteObjects(childTagsToRemove.values())

        return dbTag
    }

    private static void _toDbTag(ObjectContext context, TagDTO tag, Tag dbTag, boolean isParent = true, List<TaggableClasses> deletedEntityList, Map<Long, Tag> childTagsToRemove = getAllChildTags(dbTag)) {
        if (!dbTag.specialType || dbTag.isHidden()) {
            dbTag.name = trimToNull(tag.name)
            dbTag.isWebVisible = tag.status == TagStatusDTO.SHOW_ON_WEBSITE
            dbTag.shortName = trimToNull(tag.urlPath)
            dbTag.nodeType = NodeType.TAG
            dbTag.colour = tag.color
            dbTag.weight = tag.weight
        }

        dbTag.contents = trimToNull(tag.content)
        dbTag.nodeType = NodeType.fromDisplayName(tag.type.toString())
        dbTag.shortWebDescription = tag.shortWebDescription

        tag.childTags.each { child ->
            Tag childTag = child.id ? childTagsToRemove.remove(child.id) : context.newObject(Tag)
            childTag.parentTag = dbTag

            if (dbTag.isHidden()) {
                childTag.setSpecialType(dbTag.specialType)
            }

            _toDbTag(context, child, childTag, false, deletedEntityList, childTagsToRemove)

            if (!deletedEntityList.isEmpty()) {
                List<TagRelation> relationsList = new ArrayList<>();
                childTag.tagRelations.each {
                    TaggableClasses classes = it.entity;
                    if (deletedEntityList.contains(it.entity)) relationsList.add(it)
                }

                deleteTagRelations(context, relationsList)
            }
        }
    }

    private static void deleteTagRelations(ObjectContext context, List<TagRelation> relationsList) {

        int n = 0
        while (true) {
            List<TagRelation> part = relationsList
                    .stream()
                    .skip(n++ * 100)
                    .limit(100)
                    .collect(Collectors.toList())

            if (part.isEmpty()) break;

            context.deleteObjects(part)
        }

    }


    static Map<Long, Tag> getAllChildTags(Tag rootTag, Map<Long, Tag> map = new HashMap<>()) {
        rootTag.childTags.each { it ->
            map.put(it.id, it)
            getAllChildTags(it, map)
        }
        map
    }

    static List<Tag> getAllLeafTags(Tag tag) {
        List<Tag> result = new ArrayList<>()
        if (tag.childTags != null && tag.childTags.size() > 0) {
            result.addAll(tag.childTags.each { t -> getAllLeafTags(t) })
        } else {
            result.add(tag)
        }
        result
    }

    static PrefetchTreeNode getTagGroupPrefetch() {
        PrefetchTreeNode prefetch = Tag.TAG_REQUIREMENTS.joint()
        prefetch.merge(Tag.CHILD_TAGS.joint())
        prefetch
    }

    static boolean checklistAllowed(Tag checklist, List<TaggableClasses> taggableClasses, Long id, AqlService aql) {
        def tagRequirement = checklist.tagRequirements.find { taggableClasses.contains(it.entityIdentifier) }

        if (tagRequirement?.displayRule == null)
            return true
        def query = ObjectSelect.query(tagRequirement.getEntityClass())
                .where(Property.create("id", Long).eq(id))
        query = addAqlExp(tagRequirement.displayRule, tagRequirement.getEntityClass(), tagRequirement.context, query, aql)
        return query.selectOne(tagRequirement.context) != null

    }


    static void updateTags(Taggable relatedObject, List<? extends TagRelation> tagRelations, List<Long> tags, Class<? extends TagRelation> relationClass, ObjectContext context) {

        Map<Boolean, List<TagRelation>> map = (tagRelations.groupBy { tags.contains(it.tag.id) } as Map<Boolean, List<TagRelation>>)

        map[Boolean.FALSE]?.each { if(it.tag?.parentTag) context.deleteObjects(it) }

        List<Long> tagsToSkip = map[true] ? map[true]*.tag.id : []

        ObjectSelect.query(Tag)
                .where(Tag.ID.in(tags.findAll() { !tagsToSkip.contains(it) }))
                .select(context)
                .each { dbTag ->
                    context.newObject(relationClass).with { relation ->
                        relation.tag = dbTag
                        relation.taggedRelation = relatedObject
                        relation
                    }
                }
    }

    static ValidationErrorDTO validateTagForSave(Class clazz, ObjectContext context, List<Long> tagIds) {
        for (Long tagId : tagIds) {
            Tag tag = ObjectSelect.query(Tag)
                    .where(Tag.ID.eq(tagId))
                    .selectOne(context)
            if (tag == null) {
                return new ValidationErrorDTO(null, 'tags',
                        "Tag with id = " + tagId + " doesn\'t exist.")
            }

            if (tag.parentTag == null) {
                return new ValidationErrorDTO(null, 'tags',
                        "Tag relations cannot be directly related to a tag group.")
            }

            Tag currTag = tag
            while (currTag.getParentTag() != null) {
                currTag = currTag.getParentTag()
            }
            TagRequirement tagRequirement = currTag.getTagRequirement(clazz)
            if (tagRequirement == null) {
                return new ValidationErrorDTO(null, 'tags',
                        "Tag with id = " + tagId + " is used for other entities.")
            }
        }
    }

    static ValidationErrorDTO validateRelationsForSave(Class clazz, ObjectContext context, List<Long> tagIds, TaggableClasses... taggableClasses) {
        List<Tag> nonMultipleTags = new ArrayList<>()
        Map<Tag, Integer> rootTagsUsed = new HashMap<>()
        GetTagGroupsInterface getTagGroups = GetTagGroups.valueOf(context, taggableClasses)

        (taggableClasses.collect { getTagGroups.get(it) }.flatten() as List<Tag>).each { Tag tag ->

            if (tag.isRequiredFor(clazz)) {
                rootTagsUsed.put(tag, 0)
            }

            if (!tag.isMultipleFor(clazz)) {
                nonMultipleTags.add(tag)
            }
        }
        if (rootTagsUsed.isEmpty() && nonMultipleTags.empty) {
            return null
        }

        ObjectSelect.query(Tag)
                .where(Tag.ID.in(tagIds))
                .select(context)
                .each { tag ->
                    Tag root = tag.getRoot()
                    if (!rootTagsUsed[root]) {
                        rootTagsUsed.put(root, 1)
                    } else {
                        Integer times = rootTagsUsed[root]
                        rootTagsUsed.remove(root)
                        rootTagsUsed.put(root, times + 1)
                    }
                }

        Tag unassignedRootTad = rootTagsUsed.keySet().find { root -> rootTagsUsed[root] == null || rootTagsUsed[root] == 0 }
        if (unassignedRootTad) {
            return new ValidationErrorDTO(null, 'tags', "Tag $unassignedRootTad.name is mandatory. Modify your tag settings before removing this tag.")
        }

        Tag duplicatedRootTad = nonMultipleTags.find { root -> !root.isMultipleFor(clazz) && rootTagsUsed[root] != null && rootTagsUsed[root] > 1 }
        if (duplicatedRootTad) {
            return new ValidationErrorDTO(null, 'tags', "The $duplicatedRootTad.name tag group can be set only once.")
        }
        return null
    }


    private static ValidationErrorDTO validateSubjectAsEntity(TagDTO tagDTO, Tag tag) {
        if (tagDTO.requirements.find { !it.id })
            return new ValidationErrorDTO(null, 'subjects', "You cannot update requirement for subject entity")

        if (tagDTO.requirements.id.find { !tag.tagRequirements.id.contains(it) }) {
            return new ValidationErrorDTO(null, 'subjects', "You cannot add new requirement for subject entity")
        }

        if (tag.tagRequirements.id.find { !tagDTO.requirements.id.contains(it) }) {
            return new ValidationErrorDTO(null, 'subjects', "You cannot remove requirement for subject entity")
        }
    }
}
