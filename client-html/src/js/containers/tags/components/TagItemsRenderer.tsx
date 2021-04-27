/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import TagItem from "./TagItem";
import uniqid from "../../../common/utils/uniqid";

const TagItemsRenderer = React.memo<any>(props => {
  const { fields, onDelete, openTagEditView } = props;

  return fields.map((i, index) => {
    const field = fields.get(index);

    return (
      <TagItem
        parent={i}
        index={index}
        item={field}
        key={uniqid()}
        onDelete={onDelete}
        openTagEditView={openTagEditView}
      />
    );
  });
});

export default TagItemsRenderer;
