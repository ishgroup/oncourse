/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.cayenne

import ish.common.types.NodeType
import ish.oncourse.API
import ish.oncourse.server.cayenne.glue._Faculty

import javax.annotation.Nonnull

/**
 * A Faculty is the section of the University offering a course.
 *
 */
@API
class Faculty extends _Faculty implements NotableTrait, AttachableTrait {

    public static final int FACULTY_NAME_MAX_LENGTH = 200
    public static final int FACULTY_CODE_MAX_LENGTH = 32


    /**
     * @return The list of tags assigned to faculty
     */
    @Nonnull
    @API
    List<Tag> getTags() {
        List<Tag> tagList = new ArrayList<>(getTaggingRelations().size())
        for (FacultyTagRelation relation : getTaggingRelations()) {
            if(relation.tag?.nodeType?.equals(NodeType.TAG))
                tagList.add(relation.getTag())
        }
        return tagList
    }

    @Override
    Class<? extends TagRelation> getTagRelationClass() {
        return FacultyTagRelation.class
    }

    @Override
    void addToAttachmentRelations(AttachmentRelation relation) {
        addToAttachmentRelations((FacultyAttachmentRelation) relation)
    }

    @Override
    void removeFromAttachmentRelations(AttachmentRelation relation) {
        removeFromAttachmentRelations((FacultyAttachmentRelation) relation)
    }

    @Override
    Class<? extends AttachmentRelation> getRelationClass() {
        return FacultyAttachmentRelation.class
    }

    /**
     * @return the date and time this record was created
     */
    @API
    @Override
    Date getCreatedOn() {
        return super.getCreatedOn()
    }

    /**
     * @return the date and time this record was modified
     */
    @API
    @Override
    Date getModifiedOn() {
        return super.getModifiedOn()
    }

    /**
     * @return The name for this faculty.
     */
    @Nonnull
    @API
    @Override
    String getName() {
        return super.getName()
    }

    /**
     * This code must be unique across all faculties. It cannot contain spaces or hyphens. Because it is used to generate
     * a URL for the faculty, it should also be selected for readability, websafe characters and SEO.
     *
     * @return course code
     */
    @Nonnull
    @API
    @Override
    String getCode() {
        return super.getCode()
    }

    /**
     * The faculty description is displayed on the faculty detail page. It can contain rich text for embedding images,
     * blocks, video, dynamic text, and more. It also supported unicode for multi-language support.
     *
     * @return a rich text field for display on the web
     */
    @API
    @Override
    String getWebDescription() {
        return super.getWebDescription()
    }

    /**
     * The faculty short description is displayed on the faculties page as a description of faculty under the title.
     * If value not set, webDescription will be displayed.
     * It can contain rich text for embedding images, blocks, video, dynamic text, and more. It also supported unicode for multi-language support.
     *
     * @return a rich text field for display on the web
     */
    @API
    @Override
    String getShortWebDescription() {
        return super.getShortWebDescription()
    }

    /**
     * @return courses in which this faculty is assigned
     */
    @Nonnull
    @API
    List<Course> getCourses() {
        return super.getCourses()
    }
}
