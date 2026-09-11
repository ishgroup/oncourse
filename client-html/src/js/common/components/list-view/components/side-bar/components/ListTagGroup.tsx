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
import useEventCallback from '@mui/utils/useEventCallback';
import { SimpleTreeView } from '@mui/x-tree-view/SimpleTreeView';
import { makeAppStyles } from 'ish-ui';
import React, { memo, useEffect, useState } from 'react';
import { Draggable } from 'react-beautiful-dnd-next';
import { FormMenuTag } from '../../../../../../model/tags';
import {
  getTagIdsWithActiveDescendants,
  getTagsUpdatedBySelection,
  setIndeterminate
} from '../../../utils/listFiltersUtils';
import styles from '../../list/styles';
import ListTagItem from './ListTagItem';

const useStyles = makeAppStyles()(styles);

interface Props {
  rootTag: FormMenuTag;
  activeTags: string[];
  showColoredDots: boolean;
  updateActive: (updated: FormMenuTag) => void;
  dndKey?: number;
  dndEnabled?: boolean;
}

const ExpandIcon = props => <IconButton
  {...props}
  className='p-0'
>
  <KeyboardArrowUp />
</IconButton>;

const treeSlots = {
  expandIcon: ExpandIcon,
  collapseIcon: ExpandIcon
};

const treeSelectionPropagation = {
  descendants: true,
  parents: true
};

const treeSx = { marginLeft: -1 };

const getItemStyle = draggableStyle => ({
  userSelect: "none",
  ...draggableStyle,
});

const ListTagGroup = memo<Props>((
  {
    activeTags,
    rootTag,
    updateActive,
    dndKey,
    showColoredDots,
    dndEnabled = true
  }
) => {

  const [expanded, setExpanded] = useState([]);
  const { classes: customStyles, cx } = useStyles();

  // every ancestor of a selection has to be open, not just the partially selected ones - the tree
  // view reads a collapsed parent as a childless leaf and would show it unselected until opened.
  // Does not re-run on our own `expanded` updates: it settles in one pass and bails out when
  // nothing changed
  useEffect(() => {
    if (!activeTags.length) {
      return;
    }
    const updateExpanded = Array.from(
      new Set([...activeTags, ...getTagIdsWithActiveDescendants(rootTag.children, activeTags)])
    );
    setExpanded(prev => (updateExpanded.some(id => !prev.includes(id)) ? updateExpanded : prev));
  }, [activeTags, rootTag.children]);

  const toggleActive = useEventCallback((e, active: string[]) => {
    // The tree view also reports selection changes while it is mounting, with no event, to catch a
    // collapsed branch up with its parent. `getTagsUpdatedBySelection` already carried the
    // selection down the whole tree, so there is nothing left to catch up on and storing these
    // would only grow the saved selection on every page load.
    if (!e) {
      return;
    }

    const children = getTagsUpdatedBySelection(rootTag.children, active.map(n => Number(n)));
    const updatedRoot = { ...rootTag, children };
    setIndeterminate(updatedRoot);
    updateActive(updatedRoot);
  });

  const onExpandedItemsChange = useEventCallback((e, items: string[]) => setExpanded(items));

  const heading = (
    <div className="heading">
      {rootTag.prefix ? `${rootTag.prefix} (${rootTag.tagBody.name})` : rootTag.tagBody.name}
    </div>
  );

  const tree = (
    <SimpleTreeView
      multiSelect
      checkboxSelection
      expandedItems={expanded}
      selectedItems={activeTags}
      onSelectedItemsChange={toggleActive}
      onExpandedItemsChange={onExpandedItemsChange}
      slots={treeSlots}
      selectionPropagation={treeSelectionPropagation}
      sx={treeSx}
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
            style={getItemStyle(provided.draggableProps.style)}
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
  ) : (
    <div className="mt-2">
      {heading}
      {tree}
    </div>
);
});

export default ListTagGroup;
