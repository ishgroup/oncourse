import Delete from '@mui/icons-material/Delete';
import IconButton from '@mui/material/IconButton';
import Tooltip from '@mui/material/Tooltip';
import { TreeItem, TreeItemLabel } from '@mui/x-tree-view/TreeItem';
import $t from '@t';
import * as React from 'react';
import { memo, useMemo } from 'react';
import { useFilterStyles } from './FilterComponentStyles';

export type DeleteFilterHandler = (id: string, rootEntity: string, checked: boolean, isPrivate: boolean) => void;

interface Props {
  label: string;
  checked: boolean;
  id: string;
  rootEntity: string;
  deletable: boolean;
  onDelete: DeleteFilterHandler;
  isPrivate?: boolean;
  expression?: string;
  customLabel?: () => React.ReactNode;
}

const FilterItem = memo<Props>(({
  label, checked, onDelete, id, isPrivate, deletable, rootEntity, expression, customLabel
}) => {
  const renderedLabel = customLabel ? customLabel() : label;

  const { classes, cx } = useFilterStyles();

  const treeItemClasses = useMemo(() => ({
    root: classes.root,
    content: cx(classes.content, classes.labelRoot),
    checkbox: classes.checkbox,
    iconContainer: classes.collapseWrapper,
    label: classes.labelRoot
  }), [classes, cx]);

  return (
    <TreeItem
      itemId={id}
      classes={treeItemClasses}
      label={(
        <TreeItemLabel
          className={cx("centeredFlex", classes.checkboxLabel)}
        >
          {
            expression ? (
              <Tooltip title={expression} placement="right">
                <div className="text-truncate">{renderedLabel}</div>
              </Tooltip>
            ) : (
              renderedLabel
            )
          }
          {deletable && (
            <Tooltip title={$t('delete_filter')} placement="right">
              <IconButton className={classes.deleteButton} onClick={() => onDelete(id, rootEntity, checked, isPrivate)}>
                <Delete fontSize="inherit" color="secondary" />
              </IconButton>
            </Tooltip>
          )}
        </TreeItemLabel>
      )}
    />
  );
});

export default FilterItem;
