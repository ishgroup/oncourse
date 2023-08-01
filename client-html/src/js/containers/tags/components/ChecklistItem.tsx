/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import Delete from "@mui/icons-material/Delete";
import DragIndicator from "@mui/icons-material/DragIndicator";
import IconButton from "@mui/material/IconButton";
import clsx from "clsx";
import { stopEventPropagation, useHoverShowStyles } from "ish-ui";
import React, { useCallback } from "react";
import FormField from "../../../common/components/form/formFields/FormField";
import { FormTagProps } from "../../../model/tags";

const getFieldName = (parent, name) => (parent ? parent + `.${name}` : name);

const ChecklistItem = React.memo<FormTagProps>(({
  classes,
  onDelete,
  item,
  snapshot,
  provided
}) => {
  const onDeleteClick = useCallback(e => {
    stopEventPropagation(e);
    onDelete(item);
  }, [item]);

  const hoverClasses = useHoverShowStyles();

  return (
    <div
      className={clsx(classes.card, hoverClasses.container, {
        [clsx("paperBackgroundColor", classes.dragOver)]: snapshot.isDragging || Boolean(snapshot.combineTargetFor)
      })}
    >
      <div className={classes.checklistCardGrid}>
        <div {...provided.dragHandleProps}>
          <DragIndicator
            className={clsx("d-flex", classes.dragIcon, !item.parent && "pointer-events-none")}
          />
        </div>
        <FormField
          type="text"
          name={getFieldName(item.parent, "name")}
          fieldClasses={{
            text: classes.nameEditable
          }}
          disabled={item.system}
          className={classes.fieldEditable}
          onClick={stopEventPropagation}
        />

        <IconButton
          className={clsx("dndActionIconButton", hoverClasses.target, {
            "invisible": !item.parent
          })}
          onClick={onDeleteClick}
        >
          <Delete className={classes.actionIcon} />
        </IconButton>
      </div>
    </div>
  );
});

export default ChecklistItem;
