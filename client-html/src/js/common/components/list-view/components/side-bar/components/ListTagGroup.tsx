/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useMemo, useState } from "react";
import clsx from "clsx";
import TreeView from "@material-ui/lab/TreeView";
import makeStyles from "@material-ui/core/styles/makeStyles";
import { Draggable } from "react-beautiful-dnd";
import DragIndicator from "@material-ui/icons/DragIndicator";
import ListTagItem from "./ListTagItem";
import { MenuTag } from "../../../../../../model/tags";
import { updateIndeterminateState, getUpdated } from "../../../utils/listFiltersUtils";
import styles from "../../list/styles";

const useStyles = makeStyles(styles);

interface Props {
  rootTag: MenuTag;
  classes: any;
  showColoredDots: boolean;
  updateActive: (updated: MenuTag) => void;
  dndKey: number;
}

const ListTagGroup: React.FC<Props> = ({ rootTag, classes, updateActive, dndKey, showColoredDots }) => {
  const [expanded, setExpanded] = useState([]);

  const customStyles = useStyles();

  const hasOffset = useMemo(() => !rootTag.children.some(c => Boolean(c.children.length)), [rootTag.children]);

  const handleExpand = useCallback(event => {
    const nodeId = event.currentTarget.getAttribute("role");

    setExpanded(prev => {
      const prevIndex = prev.findIndex(p => p === nodeId);

      const updated = [...prev];

      if (prevIndex !== -1) {
        updated.splice(prevIndex, 1);
      } else {
        updated.push(nodeId);
      }

      return updated;
    });
  }, []);

  const toggleActive = useCallback(
    (e, checked) => {
      const id = e.currentTarget.value;

      const children = getUpdated(rootTag.children, id, checked);
      updateIndeterminateState(children, id);
      updateActive({ ...rootTag, children });
    },
    [rootTag.children]
  );

  const getItemStyle = (isDragging, draggableStyle) => ({
    userSelect: "none",
    ...draggableStyle,
  });

  return (
    <>
      <Draggable
        key={rootTag.prefix + rootTag.tagBody.id.toString()}
        draggableId={rootTag.prefix + rootTag.tagBody.id.toString()}
        index={dndKey}
      >
        {(provided, snapshot) => {
          const isDragging = snapshot.isDragging;

          return (            
            <div
              ref={provided.innerRef}
              {...provided.draggableProps}
              {...provided.dragHandleProps}
              style={getItemStyle(
                snapshot.isDragging,
                provided.draggableProps.style
              )}
              className={clsx("pt-2", { [customStyles.dragOver]: isDragging })}
            >
              <div
                className={clsx(
                  "centeredFlex text-truncate text-nowrap outline-none",
                  customStyles.draggableCellItem,
                  { "pl-3": isDragging },
                )}
              >
                <DragIndicator
                  className={
                    clsx("dndActionIcon", customStyles.dragIndicator, { [customStyles.visibleDragIndicator]: isDragging })
                  }
                />
                <div className={clsx("heading", classes.listHeaderOffset)}>
                  {rootTag.prefix ? `${rootTag.prefix} (${rootTag.tagBody.name})` : rootTag.tagBody.name}
                </div>
              </div>

              <TreeView expanded={expanded}>
                {rootTag.children.map(c => {
                  const key = c.prefix + c.tagBody.id.toString();
                  return (
                    <ListTagItem
                      hasOffset={hasOffset}
                      handleExpand={handleExpand}
                      nodeId={key}
                      item={c}
                      key={key}
                      toggleActive={toggleActive}
                      showColoredDots={showColoredDots}
                    />
                  );
                })}
              </TreeView>
            </div>
          );
        }}
      </Draggable>
    </>
  );
};

export default ListTagGroup;
