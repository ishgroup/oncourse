/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useState } from "react";
import clsx from "clsx";
import { Field, FieldArray } from "redux-form";
import { Draggable } from "react-beautiful-dnd";
import Delete from "@mui/icons-material/Delete";
import Edit from "@mui/icons-material/Edit";
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import DragIndicator from "@mui/icons-material/DragIndicator";
import IconButton from "@mui/material/IconButton";
import Typography from "@mui/material/Typography";
import { Collapse } from "@mui/material";
import TagItemsRenderer from "./TagItemsRenderer";
import { useHoverShowStyles } from "../../../common/styles/hooks";
import { FormEditorField } from "../../../common/components/markdown-editor/FormEditor";
import { stopEventPropagation } from "../../../common/utils/events";
import FormField from "../../../common/components/form/formFields/FormField";
import { validateAqlFilterOrTagName, validateSingleMandatoryField } from "../../../common/utils/validation";
import { FormTagProps } from "../../../model/tags";
import ColorPicker from "../../../common/components/color-picker/ColorPicker";

const getFieldName = (parent, name) => (parent ? parent + `.${name}` : name);

const TagItem = React.memo<FormTagProps>(props => {
  const {
    classes, parent, onDelete, changeVisibility, index, item, validatTagsNames, validateName, validateShortName, validateRootTagName, key
  } = props;

  if (!item.parent) {
    item.parent = parent;
  }
  
  const [isEditing, setIsEditing] = useState(false);

  const onEditClick = () => setIsEditing(prev => !prev);

  const onDeleteClick = useCallback(e => {
    stopEventPropagation(e);
    onDelete(parent, index, item);
  }, [item, parent, index]);

  const onVisibilityClick = useCallback(e => {
    stopEventPropagation(e);
    changeVisibility(parent, index, item);
  }, [item, parent, index]);

  const hoverClasses = useHoverShowStyles();

  const validateNameWithPath = useCallback((value, values, props) => validateName(value, values, props, parent), [
    parent
  ]);

  const validateShortNameWithPath = useCallback((value, values) => validateShortName(value, values, parent), [parent]);

  const urlPlaceholder = "/" + item.name?.toLowerCase().replaceAll(" ", "+");

  return (
    <>
      <Draggable key={key} draggableId={parent || "ROOT"} index={item.dragIndex} isDragDisabled={!parent}>
        {(provided, snapshot) => {
          const isDragging = snapshot.isDragging;

          return (
            <div
              ref={provided.innerRef}
              {...provided.draggableProps}
            >
              <div
                className={clsx(classes.card, hoverClasses.container, {
                  [clsx("paperBackgroundColor", classes.dragOver)]: isDragging || Boolean(snapshot.combineTargetFor)
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
                      ? (<Field name={getFieldName(parent, "color")} component={ColorPicker} />)
                      : (<div className={classes.tagColorDot} style={{ background: "#" + item.color }} />)}
                  </div>

                  <div>
                    {isEditing 
                      ? ( 
                        <FormField
                          type="text"
                          name={getFieldName(parent, "name")}
                          validate={[
                            validateSingleMandatoryField,
                            parent ? validateNameWithPath : validateRootTagName,
                            validateAqlFilterOrTagName
                          ]}
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
                        name={getFieldName(parent, "urlPath")}
                        placeholder={urlPlaceholder}
                        normalize={val => {
                          if (!val) return null;
                          return val;
                        }}
                        validate={validateShortNameWithPath}
                        fieldClasses={{
                          text: classes.urlEditable
                        }}
                        className={classes.fieldEditable}
                        onClick={stopEventPropagation}
                        hidePlaceholderInEditMode
                      />
                      ) : (
                        <Typography variant="body2" className={item.urlPath ? undefined : "placeholderContent"} noWrap>
                          {item.urlPath ? "/" + item.urlPath : urlPlaceholder}
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
                      "invisible": !parent
                    })}
                    onClick={onDeleteClick}
                  >
                    <Delete className={classes.actionIcon} />
                  </IconButton>
                </div>

                <Collapse in={isEditing} mountOnEnter unmountOnExit>
                  <div className="pl-3 pr-3" onClick={stopEventPropagation}>
                    <FormEditorField name={getFieldName(parent, "content")} placeholder="Enter description" />
                  </div>
                </Collapse>
              </div>
            </div>
          );
        }}
      </Draggable>

      <div className="ml-2">
        <FieldArray
          name={parent ? `${parent}.childTags` : "childTags"}
          component={TagItemsRenderer}
          changeVisibility={changeVisibility}
          onDelete={onDelete}
          validate={validatTagsNames}
          validatTagsNames={validatTagsNames}
          validateName={validateName}
          validateShortName={validateShortName}
          validateRootTagName={validateRootTagName}
          classes={classes}
          rerenderOnEveryChange
        />
      </div>
    </>
  );
});

export default TagItem;
