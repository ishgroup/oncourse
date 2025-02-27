/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import DragIndicator from '@mui/icons-material/DragIndicator';
import { SimpleTreeView } from '@mui/x-tree-view/SimpleTreeView';
import { makeAppStyles } from 'ish-ui';
import React, { useCallback, useMemo, useState } from 'react';
import { Draggable } from 'react-beautiful-dnd-next';
import { FormMenuTag } from '../../../../../../model/tags';
import { getUpdated, updateIndeterminateState } from '../../../utils/listFiltersUtils';
import styles from '../../list/styles';
import ListTagItem from './ListTagItem';

const useStyles = makeAppStyles()(styles);

interface Props {
  rootTag: FormMenuTag;
  classes: any;
  showColoredDots: boolean;
  updateActive: (updated: FormMenuTag) => void;
  dndKey?: number;
  dndEnabled?: boolean;
}

const ListTagGroup: React.FC<Props> = (
  { 
    rootTag, 
    classes, 
    updateActive, 
    dndKey, 
    showColoredDots,
    dndEnabled = true
  }
) => {
  const [expanded, setExpanded] = useState([]);

  const { classes: customStyles, cx } = useStyles();

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
  
  const heading = (
    <div className={cx("heading", classes.listHeaderOffset)}>
      {rootTag.prefix ? `${rootTag.prefix} (${rootTag.tagBody.name})` : rootTag.tagBody.name}
    </div>
  );

  const tree = (
    <SimpleTreeView expandedItems={expanded}>
      {rootTag.children.map(c => {
      const key = c.prefix + c.tagBody.id.toString();
      return (
        <ListTagItem
          hasOffset={hasOffset}
          handleExpand={handleExpand}
          itemId={key}
          item={c}
          key={key}
          toggleActive={toggleActive}
          showColoredDots={showColoredDots}
        />
      );
    })}
    </SimpleTreeView>
  );

  return dndEnabled ? (
    <>
      <Draggable
        key={rootTag.prefix + rootTag.tagBody.id.toString()}
        draggableId={rootTag.prefix + rootTag.tagBody.id.toString()}
        index={dndKey}
      >
        {(provided, snapshot) => {
          const isDragging = snapshot.isDragging;

          return (
            (<div
              ref={provided.innerRef}
              {...provided.draggableProps}
              {...provided.dragHandleProps}
              style={getItemStyle(
                snapshot.isDragging,
                provided.draggableProps.style
              )}
              className={cx("pt-2", { [customStyles.isDragging]: isDragging })}
            >
              <div
                className={cx(
                  "p-0",
                  customStyles.draggableCellItem,
                  { "pl-3": isDragging },
                )}
              >
                <div className="d-flex">
                  <span className="relative">
                    <DragIndicator
                      className={
                        cx(
                          "dndActionIcon",
                          customStyles.dragIndicator,
                          {
                            [customStyles.visibleDragIndicator]: isDragging
                          },
                        )
                      }
                    />
                  </span>
                  {heading}
                </div>
              </div>
              {tree}
            </div>)
          );
        }}
      </Draggable>
    </>
  ) : (
    <div className="mt-2">
      {heading}
      {tree}
    </div>
);
};

export default ListTagGroup;
