/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback } from "react";
import clsx from "clsx";
import { FieldArray } from "redux-form";
import { Draggable } from "react-beautiful-dnd";
import { withStyles, createStyles } from "@material-ui/core/styles";
import Delete from "@material-ui/icons/Delete";
import Edit from "@material-ui/icons/Edit";
import DragIndicator from "@material-ui/icons/DragIndicator";
import IconButton from "@material-ui/core/IconButton";
import Typography from "@material-ui/core/Typography/Typography";
import TagItemsRenderer from "./TagItemsRenderer";

const styles = theme => createStyles({
  dragIcon: {
    margin: theme.spacing(0, 2)
  },
  actionButton: {
    marginRight: "10px"
  },
  actionIcon: {
    fontSize: "20px"
  },
  card: {
    borderRadius: `${theme.shape.borderRadius}px`,
    padding: "2px 0",
    margin: "2px 0",
    "&:hover": {
      boxShadow: theme.shadows[2]
    },
    gridTemplateColumns: "auto auto 1fr 40% auto auto"
  },
  dragOver: {
    boxShadow: theme.shadows[2]
  },
  tagColorDot: {
    width: theme.spacing(3),
    height: theme.spacing(3),
    borderRadius: "100%"
  }
});

const TagItem = React.memo<any>(props => {
  const {
    classes, parent, onDelete, index, item, noTransformClass, openTagEditView, validatTagsNames, key
  } = props;

  if (!item.parent) {
    item.parent = parent;
  }

  const onEditClick = useCallback(() => openTagEditView(item, parent), [item, parent]);

  const onDeleteClick = useCallback(() => onDelete(parent, index, item), [item, parent, index]);

  return (
    <>
      <Draggable key={key} draggableId={parent || "ROOT"} index={item.dragIndex} isDragDisabled={!parent}>
        {(provided, snapshot) => {
          const isDragging = snapshot.isDragging;

          return (
            <div
              ref={provided.innerRef}
              {...provided.draggableProps}
              {...provided.dragHandleProps}
              className={noTransformClass}
            >
              <div
                className={clsx("cursor-pointer d-grid align-items-center", classes.card, {
                  [clsx("paperBackgroundColor", classes.dragOver)]: isDragging || Boolean(snapshot.combineTargetFor)
                })}
              >
                <div>
                  <DragIndicator
                    className={clsx({
                      "dndActionIcon": true,
                      [clsx("d-flex", classes.dragIcon)]: true
                    })}
                  />
                </div>

                <div className="pr-2">
                  <div className={classes.tagColorDot} style={{ background: "#" + item.color }} />
                </div>

                <div>
                  <div>
                    <Typography variant="caption" color="textSecondary">
                      Name
                    </Typography>

                    <Typography variant="body2" color={item.name ? undefined : "error"} noWrap>
                      {item.name || "Name is mandatory"}
                    </Typography>
                  </div>
                </div>

                <div className="centeredFlex">
                  <div className="pr-1 flex-fill overflow-hidden">
                    <Typography variant="caption" color="textSecondary">
                      URL path
                    </Typography>

                    <Typography variant="body2" className={item.urlPath ? undefined : "placeholderContent"} noWrap>
                      {item.urlPath || "No Value"}
                    </Typography>
                  </div>

                  <div className="flex-fill">
                    <Typography variant="caption" color="textSecondary">
                      Visibility
                    </Typography>

                    <Typography variant="body2">{item.status}</Typography>
                  </div>
                </div>

                <IconButton className={clsx(classes.actionButton, "dndActionIconButton")} onClick={onEditClick}>
                  <Edit className={clsx(classes.actionIcon, "dndActionIcon")} />
                </IconButton>

                <IconButton
                  className={clsx(classes.actionButton, {
                    "invisible": !parent,
                    "dndActionIconButton": true
                  })}
                  onClick={onDeleteClick}
                >
                  <Delete className={clsx(classes.actionIcon, "dndActionIcon")} />
                </IconButton>
              </div>
            </div>
          );
        }}
      </Draggable>

      <div className="ml-2">
        <FieldArray
          noTransformClass={noTransformClass}
          name={parent ? `${parent}.childTags` : "childTags"}
          component={TagItemsRenderer}
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
