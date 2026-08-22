/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { CheckBox, CheckBoxOutlineBlank, IndeterminateCheckBox } from '@mui/icons-material';
import TimelineDot from '@mui/lab/TimelineDot';
import Checkbox from '@mui/material/Checkbox';
import { useTreeItemUtils } from '@mui/x-tree-view/hooks';
import { TreeItem, TreeItemLabel, TreeItemProps } from '@mui/x-tree-view/TreeItem';
import { makeAppStyles } from 'ish-ui';
import React, { useCallback } from 'react';
import { FormMenuTag } from '../../../../../../model/tags';
import styles from './FilterComponentStyles';

const useStyles = makeAppStyles<void, 'collapseWrapper'>()((theme,p,classes) => ({
  content: {
    gap: 0,
    paddingLeft: 0,
    "&[data-selected],&[data-focused],&[data-selected][data-focused],&:hover,&[data-selected]:hover": {
      backgroundColor: 'unset'
    },
    [`&[data-expanded] .${classes.collapseWrapper}`]: {
      transform: "rotate(180deg)"
    }
  },
  label: {
    cursor: "pointer",
    userSelect: "none"
  },
  collapseWrapper: {
    transition: `transform ${theme.transitions.duration.shortest}ms ${theme.transitions.easing.easeInOut}`,
  }
}));

const useFilterStyles = makeAppStyles<any, ReturnType<keyof typeof styles>>()(styles as any);

interface Props extends TreeItemProps {
  item: FormMenuTag;
  classes?: any;
  showColoredDots: boolean;
}

const ListTagItem: React.FC<Props> = ({
 item, itemId, showColoredDots
}) => {

  const { classes, cx } = useStyles();

  const { classes: filterClasses } = useFilterStyles({});

  const { publicAPI } = useTreeItemUtils({ itemId });

  // checkboxSelection disables selection on content click, so toggle it manually to make the whole label clickable
  const onLabelClick = useCallback(
    e => {
      e.preventDefault();
      publicAPI.setItemSelection({ itemId, event: e, keepExistingSelection: true });
    },
    [itemId, publicAPI]
  );

  return <TreeItem
    itemId={itemId}
    classes={{
      content: cx(classes.content, filterClasses.labelRoot),
      checkbox: filterClasses.checkbox,
      iconContainer: classes.collapseWrapper
    }}
    slots={{
      checkbox: props => <Checkbox
        {...props}
        className={filterClasses.checkbox}
        color="secondary"
        icon={<CheckBoxOutlineBlank className={filterClasses.checkboxFontSize} />}
        checkedIcon={<CheckBox className={filterClasses.checkboxFontSize} />}
        indeterminateIcon={<IndeterminateCheckBox className={filterClasses.checkboxFontSize} />}
      />
    }}
    label={(
      <TreeItemLabel
        className={cx("centeredFlex", classes.label, filterClasses.checkboxLabel)}
        onClick={onLabelClick}
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
