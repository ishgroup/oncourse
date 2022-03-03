/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.function

import groovy.transform.CompileStatic
import ish.oncourse.server.api.v1.model.TagDTO
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

@CompileStatic
class TagFunctionsTest {

    @Test
    void validateTagNameWhenNameIsValid1() {
        TagDTO tag = new TagDTO()
        tag.setName("Test Name 123")
        assertFalse(TagFunctions.validateTagName(tag))
    }

    @Test
    void validateTagNameWhenNameIsValid2() {
        TagDTO tag = new TagDTO()
        tag.setName("TestName123")
        assertFalse(TagFunctions.validateTagName(tag))
    }

    @Test
    void validateTagNameWhenNameIsValidAndChildNameIsValid() {
        TagDTO tag = new TagDTO()
        tag.setName("1-Test_Name-12_3")
        TagDTO tagChild = new TagDTO()
        tagChild.setName("_test_Child--123")
        tag.setChildTags(List.of(tagChild))
        assertFalse(TagFunctions.validateTagName(tag))
    }

    @Test
    void validateTagNameWhenNameIsValidAndChildNameIsNotValid() {
        TagDTO tag = new TagDTO()
        tag.setName("TestName")
        TagDTO tagChild = new TagDTO()
        tagChild.setName("Days >= 10")
        tag.setChildTags(List.of(tagChild))
        assertTrue(TagFunctions.validateTagName(tag))
    }

    @Test
    void validateTagNameWhenNameIsNotValidAndChildNameIsValid() {
        TagDTO tag = new TagDTO()
        tag.setName("last day + filters")
        TagDTO tagChild = new TagDTO()
        tagChild.setName("Tag Name - 1")
        tag.setChildTags(List.of(tagChild))
        assertTrue(TagFunctions.validateTagName(tag))
    }

    @Test
    void validateTagNameWhenNameIsNotValid1() {
        TagDTO tag = new TagDTO()
        tag.setName("last day + filters")
        assertTrue(TagFunctions.validateTagName(tag))
    }

    @Test
    void validateTagNameWhenNameIsNotValid2() {
        TagDTO tag = new TagDTO()
        tag.setName("Test (days)")
        assertTrue(TagFunctions.validateTagName(tag))
    }
}