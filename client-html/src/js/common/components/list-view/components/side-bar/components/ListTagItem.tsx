/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import TimelineDot from '@mui/lab/TimelineDot';
import { TreeItem, TreeItemLabel, TreeItemProps } from '@mui/x-tree-view/TreeItem';
import { makeAppStyles, stopEventPropagation } from 'ish-ui';
import React from 'react';
import { FormMenuTag } from '../../../../../../model/tags';

const useStyles = makeAppStyles()(theme => ({
  content: {
    "&[data-selected],&[data-focused],&[data-selected][data-focused],&:hover,&[data-selected]:hover": {
      backgroundColor: 'unset'
    }
  }
}));

interface Props extends TreeItemProps {
  item: FormMenuTag;
  classes?: any;
  showColoredDots: boolean;
}

const ListTagItem: React.FC<Props> = ({
 item, itemId, showColoredDots
}) => {

  const { classes } = useStyles();

  return <TreeItem
    itemId={itemId}
    onClick={stopEventPropagation}
    classes={classes}
    label={(
      <TreeItemLabel className="centeredFlex">
        {item.tagBody.name}
        {showColoredDots && (
          <TimelineDot
            sx={{
              marginLeft: 1,
              background: "#" + item.tagBody.color,
              boxShadow: 'unset'
            }}
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
