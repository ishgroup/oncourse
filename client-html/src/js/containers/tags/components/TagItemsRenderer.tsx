/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { Tag } from "@api/model";
import { WrappedFieldArrayProps } from "redux-form";
import TagItem from "./TagItem";
import uniqid from "../../../common/utils/uniqid";
import { FormTagProps } from "../../../model/tags";

const preventRerenderFields: (keyof Tag)[] = ['name', 'status', 'urlPath', 'content', 'color'];

const shouldUpdate = (prevProps, currentProps) => {
  const prevTags: Tag[] = prevProps.fields.getAll();
  const newTags: Tag[] = currentProps.fields.getAll();

  prevTags.forEach((t, i) => {
    // @ts-ignore
    preventRerenderFields.forEach(f => {
      if (newTags[i] && t[f] !== newTags[i][f]) {
        return true;
      }
    });
  });
  
  return false;
};

const TagItemsRenderer = React.memo<Partial<FormTagProps> & WrappedFieldArrayProps>(props => {
  const {
 fields, onDelete, changeVisibility, classes, validatTagsNames, validateName, validateShortName, validateRootTagName
} = props;

  return fields.map((i, index) => {
    const field = fields.get(index);

    return (
      <TagItem
        parent={i}
        index={index}
        validatTagsNames={validatTagsNames}
        validateName={validateName}
        validateShortName={validateShortName}
        validateRootTagName={validateRootTagName}
        item={field}
        classes={classes}
        key={field.id || uniqid()}
        onDelete={onDelete}
        changeVisibility={changeVisibility}
      />
    );
  }) as any;
}, shouldUpdate);

export default TagItemsRenderer;
