/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import TableCell from '@mui/material/TableCell';
import { flexRender } from '@tanstack/react-table';
import { Row } from '@tanstack/table-core/src/types';
import clsx from 'clsx';
import { stubFunction } from 'ish-ui';
import React, { memo } from 'react';
import AutoSizer from 'react-virtualized-auto-sizer';
import { List } from 'react-window';
import { useInfiniteLoader } from 'react-window-infinite-loader';
import { NESTED_LIST_PAGE_SIZE } from '../../../../../../constants/Config';
import { NestedTableColumnsTypes } from '../../../../../../model/common/NestedTable';
import areEqual from '../../../../../utils/react-window/areEqual';
import NestedTableCheckboxCell from './NestedTableCheckboxCell';
import NestedTableDeleteCell from './NestedTableDeleteCell';
import NestedTableLinkCell from './NestedTableLinkCell';

const ListCell = React.memo<{
  value, fieldName, column, row, onCheckboxChange, onRowDelete, classes?
}>(({
  value, fieldName, column, row, onCheckboxChange, onRowDelete, classes
}) => {
  switch (column.type as NestedTableColumnsTypes) {
    case "delete":
      return (
        <NestedTableDeleteCell
          classes={classes}
          onRowDelete={onRowDelete}
        />
      );

    case "checkbox": {
      return (
        <NestedTableCheckboxCell
          fieldName={`${fieldName}.${column.name}`}
          column={column}
          row={row}
          classes={{
            checkbox: classes.cellButton
          }}
          onChange={onCheckboxChange}
        />
      );
    }

    case "link": {
      return (
        <NestedTableLinkCell
          value={value}
          classes={{
            linkIcon: classes.cellLinkIcon
          }}
          link={`/${column.linkEntity}/${row[column.linkPath]}`}
        />
      );
    }

    default: {
      return value;
    }
  }
});

const ListRow = memo<any>(({
  index,
  style,
  rows,
  classes,
  onRowSelect,
  onRowDelete,
  onRowDoubleClick,
  onCheckboxChange
}) => {
  const row = rows[index];
  const rowClasses = clsx(
    "d-flex",
    classes.row,
    row.getIsSelected() && classes.selected,
    row.original && row.original.customClasses,
    index % 2 && classes.oddRow
  );

  return (
    <div
      style={style}
      className={rowClasses}
      onClick={() => onRowSelect(row.id)}
      onDoubleClick={() => (onRowDoubleClick ? onRowDoubleClick(row.original.initial) : null)}
    >
      {row.getVisibleCells().map(cell => (
        <TableCell
          key={cell.id}
          style={{
            minWidth: '0px',
            boxSizing: "border-box",
            flex: `${cell.column.getSize()} 0 auto`,
            width: `${cell.column.getSize()}px`
          }}
          component="div"
          className={clsx(classes.bodyCell, cell.column.columnDef.cellClass)}
        >
          <ListCell
            value={flexRender(cell.column.columnDef.cell, cell.getContext())}
            column={cell.column.columnDef}
            row={row.original.initial}
            fieldName={row.original.fieldName}
            onCheckboxChange={onCheckboxChange}
            onRowDelete={() => onRowDelete(row.original.initial.id)}
            classes={classes}
          />
        </TableCell>
      ))}
    </div>
  );
}, areEqual);

interface StaticListProps {
  rows: Row<any>[];
  classes?: Record<string, string>;
  totalColumnsWidth?: number;
  onLoadMore?: any;
  onRowSelect
  onRowDelete
  onRowDoubleClick
  onCheckboxChange
}

export default function NestedList(props: StaticListProps)  {
  const {
    totalColumnsWidth,
    rows,
    onLoadMore = stubFunction
  } = props;

  const isRowLoaded = index => Boolean(rows[index]);

  const onRowsRendered = useInfiniteLoader({
    minimumBatchSize: NESTED_LIST_PAGE_SIZE,
    isRowLoaded,
    rowCount: rows.length * 2,
    loadMoreRows: onLoadMore
  });

  return (
    <AutoSizer>
      {({ height, width }) => (
        <List
          style={{
            overflow: "hidden auto",
            height: isNaN(height) ? 0 : height,
            width: totalColumnsWidth > width ? totalColumnsWidth : (isNaN(width) ? 0 : width)
          }}
          rowComponent={ListRow as any}
          rowCount={rows.length}
          rowProps={props as any}
          rowHeight={27}
          onRowsRendered={onRowsRendered}
        />
      )}
    </AutoSizer>
  );
}
