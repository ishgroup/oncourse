/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import TimelineDot from '@mui/lab/TimelineDot';
import { TreeItem, TreeItemLabel, TreeItemProps } from '@mui/x-tree-view/TreeItem';
import React from 'react';
import { FormMenuTag } from '../../../../../../model/tags';
import { useFilterStyles } from './FilterComponentStyles';

interface Props extends TreeItemProps {
  item: FormMenuTag;
  classes?: any;
  showColoredDots: boolean;
}

const ListTagItem: React.FC<Props> = ({
 item, itemId, showColoredDots
}) => {
  
  const { classes, cx } = useFilterStyles();

  return <TreeItem
    itemId={itemId}
    classes={{
      content: cx(classes.content, classes.labelRoot),
      checkbox: classes.checkbox,
      iconContainer: classes.collapseWrapper
    }}
    label={(
      <TreeItemLabel
        className={cx("centeredFlex", classes.label, classes.checkboxLabel)}
      >
        {item.tagBody.name}
        {showColoredDots && (
          <TimelineDot
            sx={theme => ({
              margin: theme.spacing(0, 0, 0, 1),
              alignSelf: 'center',
              background: "#" + item.tagBody.color,
              boxShadow: 'unset'
            })}
          />
        )}
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
};

export default ListTagItem;
