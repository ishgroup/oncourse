/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useState } from "react";
import clsx from "clsx";
import { FieldArray } from "redux-form";
import { Draggable } from "react-beautiful-dnd";
import { createStyles, withStyles } from "@mui/styles";
import Delete from "@mui/icons-material/Delete";
import Edit from "@mui/icons-material/Edit";
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import DragIndicator from "@mui/icons-material/DragIndicator";
import IconButton from "@mui/material/IconButton";
import Typography from "@mui/material/Typography";
import { alpha } from "@mui/material/styles";
import { Collapse } from "@mui/material";
import TagItemsRenderer from "./TagItemsRenderer";
import { AppTheme } from "../../../model/common/Theme";
import { useHoverShowStyles } from "../../../common/styles/hooks";
import { FormEditorField } from "../../../common/components/markdown-editor/FormEditor";
import { stopEventPropagation } from "../../../common/utils/events";

const styles = (theme: AppTheme) => createStyles({
  dragIcon: {
    margin: theme.spacing(0, 2),
    color: theme.palette.action.focus,
    "&:hover": {
      color: theme.palette.action.active
    }
  },
  actionButton: {
    marginRight: "10px"
  },
  actionIcon: {
    color: theme.palette.action.focus,
    fontSize: "20px"
  },
  actionIconInactive: {
    color: theme.palette.action.hover,
    fontSize: "20px"
  },
  card: {
    borderRadius: `${theme.shape.borderRadius}px`,
    padding: theme.spacing(0.25, 0),
    margin: theme.spacing(1, 0),

    backgroundColor: alpha(theme.palette.text.primary, 0.025),
    "&:hover $actionIcon": {
      color: theme.palette.action.active
    },
    "&:hover $actionIconInactive": {
      color: theme.palette.action.focus
    }
  },
  cardGrid: {
    gridTemplateColumns: "auto auto 1fr 1fr auto auto auto",
    display: "grid",
    alignItems: "center",
    cursor: "pointer",
  },
  dragOver: {
    boxShadow: theme.shadows[2]
  },
  tagColorDot: {
    width: "1em",
    height: "1em",
    borderRadius: "100%"
  }
});

const TagItem = React.memo<any>(props => {
  const {
    classes, parent, onDelete, changeVisibility, index, item, openTagEditView, validatTagsNames, key
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
                    <div className={classes.tagColorDot} style={{ background: "#" + item.color }} />
                  </div>

                  <div>
                    <Typography variant="body2" fontWeight="500" color={item.name ? undefined : "error"} noWrap>
                      {item.name || "Name is mandatory"}
                    </Typography>
                  </div>

                  <Typography variant="body2" className={item.urlPath ? undefined : "placeholderContent"} noWrap>
                    {item.urlPath || "No Value"}
                  </Typography>

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
                    <FormEditorField name={parent ? parent + ".content" : "content"} placeholder="Enter description" />
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
          openTagEditView={openTagEditView}
          validate={validatTagsNames}
          rerenderOnEveryChange
        />
      </div>
    </>
  );
});

export default withStyles(styles)(TagItem);
