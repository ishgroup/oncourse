/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback } from "react";
import clsx from "clsx";
import { Field } from "redux-form";
import Delete from "@mui/icons-material/Delete";
import Edit from "@mui/icons-material/Edit";
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import DragIndicator from "@mui/icons-material/DragIndicator";
import IconButton from "@mui/material/IconButton";
import Typography from "@mui/material/Typography";
import { Collapse } from "@mui/material";
import { useHoverShowStyles } from "../../../common/styles/hooks";
import { FormEditorField } from "../../../common/components/markdown-editor/FormEditor";
import { stopEventPropagation } from "../../../common/utils/events";
import FormField from "../../../common/components/form/formFields/FormField";
import { FormTagProps } from "../../../model/tags";
import ColorPicker from "../../../common/components/color-picker/ColorPicker";

const getFieldName = (parent, name) => (parent ? parent + `.${name}` : name);

const TagItem = React.memo<FormTagProps>(({
  classes,
  onDelete,
  changeVisibility,
  index,
  item,
  snapshot,
  provided,
  isEditing,
  setIsEditing
}) => {
  const onEditClick = () => setIsEditing(isEditing ? null : item.id);

  const onDeleteClick = useCallback(e => {
    stopEventPropagation(e);
    onDelete(index, item);
  }, [item, index]);

  const onVisibilityClick = useCallback(e => {
    stopEventPropagation(e);
    changeVisibility(index, item);
  }, [item, index]);

  const hoverClasses = useHoverShowStyles();

  const urlPlaceholder = item.urlPath
    ? "/" + item.urlPath.trim().toLowerCase().replaceAll(" ", "+")
    : item.name
      ? "/" + item.name.trim().toLowerCase().replaceAll(" ", "+")
      : "No value";

  return (
    <div
      className={clsx(classes.card, hoverClasses.container, {
        [clsx("paperBackgroundColor", classes.dragOver)]: snapshot.isDragging || Boolean(snapshot.combineTargetFor)
      })}
      onClick={onEditClick}
    >
      <div className={classes.cardGrid}>
        <div {...provided.dragHandleProps}>
          <DragIndicator
            className={clsx("d-flex", classes.dragIcon)}
          />
        </div>

        <div className="pr-3">
          {isEditing
            ? (<Field name={getFieldName(item.parent, "color")} component={ColorPicker} />)
            : (<div className={classes.tagColorDot} style={{ background: "#" + item.color }} />)}
        </div>

        <div>
          {isEditing
            ? (
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
            )
            : (
              <Typography variant="body2" fontWeight="500" color={item.name ? undefined : "error"} noWrap>
                {item.name || "Name is mandatory"}
              </Typography>
            )}
        </div>

        <div>
          {isEditing ? (
            <FormField
              type="text"
              name={getFieldName(item.parent, "urlPath")}
              placeholder={urlPlaceholder}
              normalize={val => {
                if (!val) return null;
                return val;
              }}
              fieldClasses={{
                text: classes.urlEditable
              }}
              className={classes.fieldEditable}
              onClick={stopEventPropagation}
              hidePlaceholderInEditMode
            />
          ) : (
            <Typography variant="body2" className={item.urlPath ? undefined : "placeholderContent"} noWrap>
              {urlPlaceholder}
            </Typography>
          )}
        </div>

        <IconButton className={clsx("dndActionIconButton", hoverClasses.target)}>
          <Edit className={classes.actionIcon} />
        </IconButton>

        <IconButton className="dndActionIconButton" onClick={onVisibilityClick}>
          {item.status === "Private"
            ? <VisibilityOffIcon className={classes.actionIconInactive} />
            : <VisibilityIcon className={classes.actionIcon} />}
        </IconButton>

        <IconButton
          className={clsx("dndActionIconButton", {
            "invisible": !item.parent
          })}
          onClick={onDeleteClick}
        >
          <Delete className={classes.actionIcon} />
        </IconButton>
      </div>

      <Collapse in={isEditing} mountOnEnter unmountOnExit>
        <div className="pl-3 pr-3" onClick={stopEventPropagation}>
          <FormEditorField name={getFieldName(item.parent, "content")} placeholder="Enter description" />
        </div>
      </Collapse>
    </div>
  );
});

export default TagItem;
