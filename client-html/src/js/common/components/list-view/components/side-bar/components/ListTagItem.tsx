/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import TimelineDot from '@mui/lab/TimelineDot';
import { TreeItem, TreeItemLabel, TreeItemProps } from '@mui/x-tree-view/TreeItem';
import React, { memo, useMemo } from 'react';
import { FormMenuTag } from '../../../../../../model/tags';
import { useFilterStyles } from './FilterComponentStyles';

interface Props extends TreeItemProps {
  item: FormMenuTag;
  showColoredDots: boolean;
}

const dotSx = color => theme => ({
  margin: theme.spacing(0, 0, 0, 1),
  alignSelf: 'center',
  background: "#" + color,
  boxShadow: 'unset'
});

const ListTagItem = memo<Props>(({
 item, itemId, showColoredDots
}) => {
  const { classes, cx } = useFilterStyles();

  const treeItemClasses = useMemo(() => ({
    content: cx(classes.content, classes.labelRoot),
    checkbox: classes.checkbox,
    iconContainer: classes.collapseWrapper
  }), [classes, cx]);

  const labelClassName = useMemo(
    () => cx("centeredFlex", classes.label, classes.checkboxLabel),
    [classes, cx]
  );

  return <TreeItem
    itemId={itemId}
    classes={treeItemClasses}
    label={(
      <TreeItemLabel className={labelClassName}>
        {item.tagBody.name}
        {showColoredDots && <TimelineDot sx={dotSx(item.tagBody.color)} />}
      </TreeItemLabel>
    )}
  >
    {item.children.map(t => <ListTagItem
      itemId={t.tagBody.id.toString()}
      item={t}
      key={t.prefix + t.tagBody.id.toString()}
      showColoredDots={showColoredDots}
    />)}
  </TreeItem>;
});

export default ListTagItem;
