import React from "react";
import {createStyles, IconButton} from "@material-ui/core";
import clsx from "clsx";
import {Draggable} from "react-beautiful-dnd";
import {Delete, DragIndicator} from "@material-ui/icons";
import {withStyles} from "@material-ui/core/styles";
import {stubFunction} from "../../../../../common/utils/Components";
import EditInPlaceField from "../../../../../common/components/form/form-fields/EditInPlaceField";
import MenuItemChildrenRenderer from "./MenuItemChildrenRenderer";

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
  },
  noTransform: {
    transform: "none !important",
  },
});

interface Props {
  changeNode: (value: string, type: string, itemId: number) => void;
  classes: any;
  menuItemsWithIds: any[];
  index: number;
  item: any;
  removeItem: (menuItemsWithIds: any[], item: any) => void;
}

const MenuItem = (props: Props) => {
  const { changeNode, classes, item, menuItemsWithIds, removeItem } = props;

  return (
    <div key={item.id}>
      <Draggable key={item.id} draggableId={typeof item.id === "string" ? item.id : item.id && item.id.toString()} index={item.dragId}>
        {(provided, snapshot) => {
          const isDragging = snapshot.isDragging;

          return (
            <div
              ref={provided.innerRef}
              {...provided.draggableProps}
              {...provided.dragHandleProps}
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
                  <div className={classes.tagColorDot} />
                {/*  /!*<div className={classes.tagColorDot} style={{ background: "#" + item.color }} />*!/*/}
                </div>

                <div>
                  <EditInPlaceField
                    label="Title"
                    name="itemTitle"
                    id="itemTitle"
                    meta={{}}
                    input={{
                      onChange: e => changeNode(e.target.value, 'title', item.id),
                      onFocus: stubFunction,
                      onBlur: stubFunction,
                      value:  item.title,
                    }}
                  />
                </div>

                <div className="centeredFlex">
                  <EditInPlaceField
                    label="Url"
                    name="itemUrl"
                    id="itemUrl"
                    meta={{}}
                    input={{
                      onChange: e => changeNode(e.target.value, 'url', item.id),
                      onFocus: stubFunction,
                      onBlur: stubFunction,
                      value: item.url,
                    }}
                  />
                </div>

                <IconButton
                  className={clsx(classes.actionButton, {
                    "invisible": !parent,
                    "dndActionIconButton": true
                  })}
                  onClick={() => removeItem(menuItemsWithIds, item)}
                >
                  <Delete className={clsx(classes.actionIcon, "dndActionIcon")} />
                </IconButton>
              </div>
            </div>
          )
        }}
      </Draggable>

      {item.children && item.children.length > 0 && (
        <div className="ml-2">
          {item.children.map((child, index) => (
            <MenuItemChildrenRenderer
              changeNode={changeNode}
              index={index}
              item={child}
              key={child.id}
              removeItem={removeItem}
              menuItemsWithIds={menuItemsWithIds}
            />
          ))}
        </div>
      )}
    </div>
  )
}

export default (withStyles(styles)(MenuItem));