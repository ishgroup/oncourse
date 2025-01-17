/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Delete } from '@mui/icons-material';
import Launch from '@mui/icons-material/Launch';
import { ButtonBase, Typography } from '@mui/material';
import Button from '@mui/material/Button';
import Paper from '@mui/material/Paper';
import clsx from 'clsx';
import { DynamicSizeList, openInternalLink } from 'ish-ui';
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import AutoSizer from 'react-virtualized-auto-sizer';
import { withStyles } from 'tss-react/mui';
import { NestedListItem } from '../NestedList';
import { listStyles } from './styles';

interface Props {
  classes?: any;
  type: "list" | "search";
  items: NestedListItem[];
  onDelete?: (item: NestedListItem, index: number) => void;
  onClick?: (item: NestedListItem) => void;
  fade?: boolean;
  dataRowClass?: string;
  disabled?: boolean;
  CustomCell?: React.ReactNode;
}

const RowContent = React.memo<{
  style?,
  item?,
  index?,
  classes?,
  dataRowClass?,
  disabled?,
  onDelete?,
  onClick?,
  type?,
  forwardedRef?,
  CustomCell?
}>(({
  style,
  item,
  index,
  classes,
  dataRowClass,
  disabled,
  onDelete,
  onClick,
  type,
  forwardedRef,
  CustomCell
}) => (
  <li
    ref={forwardedRef}
    style={style}
    className={clsx("pb-1", {
      [classes.fade]: !item.active
    })}
  >
    <div className={clsx("d-flex", classes.root__item)}>
      <div
        className={clsx(classes.textRow, dataRowClass)}
      >
        <Typography
          variant="body2"
          className={clsx("text-truncate", classes.dInline, item.link && "linkDecoration")}
          onClick={item.link ? () => openInternalLink(item.link) : undefined}
        >
          {item.primaryText}
          {Boolean(item.link) && <Launch fontSize="inherit" color="primary" className="vert-align-mid ml-0-5"/>}
        </Typography>

        <div className={classes.chipsWrapper}>
          <Typography
            className={clsx(
              "ml-2 mr-2 text-truncate",
              classes.dInline
            )}
            variant="caption"
          >
            {item.secondaryText}
          </Typography>

          <Typography variant="body2" className={classes.chips}>
            {item.entityName}
          </Typography>
        </div>

        {CustomCell && <CustomCell item={item} index={index}/>}
      </div>
      <span className={clsx("centeredFLex", disabled && "invisible")}>
        {type === "list" && onDelete && (
          <ButtonBase
            className={classes.deleteButton}
            onClick={() => onDelete(item, index)}
          >
            <Delete className={classes.deleteIcon}/>
          </ButtonBase>
        )}
        {type === "search" && (
          <>
            <span className="flex-fill"/>
            <Button className={classes.button} onClick={() => onClick(item, index)}>
              Add
            </Button>
          </>
        )}
      </span>
    </div>
  </li>
));

export const NestedListRow = withStyles(RowContent, listStyles);

const RowRenderer = React.forwardRef<any, any>(({data, index, style}, ref) => {
  const {items, ...rest} = data;
  return <RowContent item={items[index]} style={style} forwardedRef={ref} index={index} {...rest} />;
});

const ListRenderer = React.memo<Props>(
  ({
     classes, items, onDelete, onClick, fade, dataRowClass, disabled, type, CustomCell
   }: Props) => {
    const [changedIndex, setChangedIndex] = useState<number>();

    useEffect(() => {
      if (typeof changedIndex === "number") {
        setChangedIndex(null);
      }
    }, [changedIndex]);

    const isVirtual = useMemo(() => items.length > 15, [items.length]);

    const deleteHandler = useCallback(
      onDelete
        ? (item, index) => {
          onDelete(item, index);
          setChangedIndex(index);
        }
        : undefined,
      [onDelete]
    );

    const clickHandler = useCallback((item, index) => {
      onClick(item);
      setChangedIndex(index);
    }, []);

    const itemData = useMemo(() => ({
      items, dataRowClass, type, classes, disabled, onDelete: deleteHandler, onClick: clickHandler, CustomCell
    }), [items, dataRowClass, type, classes, disabled, deleteHandler, clickHandler, CustomCell]);

    return (
      <Paper className={clsx(classes.root, {[classes.fade]: fade, [classes.root__height]: isVirtual})}>
        {isVirtual ? (
          <AutoSizer disableHeight>
            {({width}) => (
              <DynamicSizeList
                height={384}
                width={width}
                itemCount={items.length}
                itemData={itemData}
              >
                {RowRenderer as any}
              </DynamicSizeList>
            )}
          </AutoSizer>
        ) : (
          items.map((item, index) => (
            <RowContent
              key={item.id}
              item={item}
              style={{}}
              index={index}
              disabled={disabled}
              classes={classes}
              dataRowClass={dataRowClass}
              type={type}
              onDelete={onDelete}
              onClick={onClick}
              CustomCell={CustomCell}
            />
          ))
        )}
      </Paper>
    );
  }
);

export default withStyles(ListRenderer, listStyles);
