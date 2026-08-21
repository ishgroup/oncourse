/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import DragIndicator from '@mui/icons-material/DragIndicator';
import KeyboardArrowUp from '@mui/icons-material/KeyboardArrowUp';
import IconButton from '@mui/material/IconButton';
import { SimpleTreeView } from '@mui/x-tree-view/SimpleTreeView';
import { makeAppStyles } from 'ish-ui';
import React, { useCallback, useState } from 'react';
import { Draggable } from 'react-beautiful-dnd-next';
import { FormMenuTag } from '../../../../../../model/tags';
import { getTagsUpdatedByIds, setIndeterminate } from '../../../utils/listFiltersUtils';
import styles from '../../list/styles';
import ListTagItem from './ListTagItem';

const useStyles = makeAppStyles()(styles);

interface Props {
  rootTag: FormMenuTag;
  activeTags: string[];
  classes: any;
  showColoredDots: boolean;
  updateActive: (updated: FormMenuTag) => void;
  dndKey?: number;
  dndEnabled?: boolean;
}

const ExpandIcon = props => <IconButton
  {...props}
>
  <KeyboardArrowUp />
</IconButton>;

const ListTagGroup: React.FC<Props> = (
  {
    activeTags,
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

  const getItemStyle = (isDragging, draggableStyle) => ({
    userSelect: "none",
    ...draggableStyle,
  });

  const toggleActive = useCallback(
    (e, active: string[]) => {
      const children = getTagsUpdatedByIds(rootTag.children, active.map(n => Number(n)));
      const updatedRoot = { ...rootTag, children };
      setIndeterminate(updatedRoot);
      updateActive(updatedRoot);
    },
    [rootTag.children]
  );
  
  const heading = (
    <div className={cx("heading", classes.listHeaderOffset)}>
      {rootTag.prefix ? `${rootTag.prefix} (${rootTag.tagBody.name})` : rootTag.tagBody.name}
    </div>
  );

  const tree = (
    <SimpleTreeView
      multiSelect
      checkboxSelection
      expandedItems={expanded}
      selectedItems={activeTags}
      slots={{
        expandIcon: ExpandIcon,
        collapseIcon: ExpandIcon
      }}
      selectionPropagation={{
        descendants: true
      }}
      onSelectedItemsChange={toggleActive}
      onExpandedItemsChange={(e, items) => setExpanded(items)}
    >
      {rootTag.children.map(t => <ListTagItem
        itemId={t.tagBody.id.toString()}
        item={t}
        key={t.prefix + t.tagBody.id.toString()}
        showColoredDots={showColoredDots}
      />)}
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
