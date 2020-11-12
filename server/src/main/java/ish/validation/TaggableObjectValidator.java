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

package ish.validation;

import ish.messaging.ITag;
import ish.messaging.ITaggableObject;
import ish.oncourse.cayenne.Taggable;
import ish.oncourse.cayenne.TaggableClasses;
import ish.oncourse.function.GetTagGroupsInterface;
import org.apache.cayenne.validation.ValidationResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by anarut on 11/21/16.
 */
public class TaggableObjectValidator {

    private ITaggableObject taggableObject;
    private ValidationResult validationResult;
    private GetTagGroupsInterface getTagGroups;
    private Map<Class<? extends Taggable>, TaggableClasses> classes;
    private boolean isClientValidation;

    private TaggableObjectValidator() {
    }

    public static TaggableObjectValidator valueOf(ITaggableObject taggableObject, ValidationResult validationResult, GetTagGroupsInterface getTagGroups, Map<Class<? extends Taggable>, TaggableClasses> classes, boolean isClientValidaton) {
        TaggableObjectValidator taggableObjectValidator = new TaggableObjectValidator();
        taggableObjectValidator.taggableObject = taggableObject;
        taggableObjectValidator.validationResult = validationResult;
        taggableObjectValidator.getTagGroups = getTagGroups;
        taggableObjectValidator.classes = classes;
        taggableObjectValidator.isClientValidation = isClientValidaton;
        return taggableObjectValidator;
    }

    public void validate() {
        List<ITag> nonMultipleTags = new ArrayList<>();
        Map<ITag, Integer> rootTagsUsed = new HashMap<>();

        classes.entrySet().forEach(entry -> getTagGroups.get(entry.getValue())
                .forEach(tag -> {
            if (tag.isRequiredFor(entry.getKey())) {
                rootTagsUsed.put(tag, 0);
            }

            if (!tag.isMultipleFor(entry.getKey())) {
                nonMultipleTags.add(tag);
            }
        }));

        // if nothing to validate then stop here.
        if (rootTagsUsed.size() == 0 && nonMultipleTags.size() == 0) {
            return;
        }

        // validate required and non-multiple tags with existing tag relations
        taggableObject.getTags().forEach(tag -> {
            ITag root = tag.getRoot();
            if (rootTagsUsed.get(root) == null) {
                rootTagsUsed.put(root, 1);
            } else {
                Integer times = rootTagsUsed.get(root);
                rootTagsUsed.remove(root);
                rootTagsUsed.put(root, times + 1);
            }
        });

        if (isClientValidation) {
            rootTagsUsed.keySet().stream()
                    .filter(root -> rootTagsUsed.get(root) == null || rootTagsUsed.get(root) == 0)
                    .forEach(root -> validationResult.addFailure(new ValidationFailure(taggableObject, ITaggableObject.TAGGING_RELATIONS_PROPERTY,
                            String.format("Tag '%s' is mandatory. Modify your tag settings before removing this tag.", root.getName()))));
        }

        nonMultipleTags.stream()
                .filter(root -> rootTagsUsed.get(root) != null && rootTagsUsed.get(root) > 1)
                .forEach(root -> validationResult.addFailure(new ValidationFailure(taggableObject, ITaggableObject.TAGGING_RELATIONS_PROPERTY,
                        String.format("The %s tag group can be set only once.", root.getName()))));
    }
}
