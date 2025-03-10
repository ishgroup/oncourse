/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import TableCell from '@mui/material/TableCell';
import { flexRender } from '@tanstack/react-table';
import clsx from 'clsx';
import React, { memo } from 'react';
import AutoSizer from 'react-virtualized-auto-sizer';
import { areEqual, FixedSizeList } from 'react-window';
import { NestedTableColumnsTypes } from '../../../../../../model/common/NestedTable';
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

const ListRow = memo<any>(({data, index, style}) => {
  const {
    rows,
    classes,
    onRowSelect,
    onRowDelete,
    onRowDoubleClick,
    onCheckboxChange
  } = data;

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

export default itemData => {
  const {
    totalColumnsWidth,
    rows
  } = itemData;

  return (
    <AutoSizer>
      {({height, width}) => (
        <FixedSizeList
          style={{overflow: "hidden auto"}}
          itemCount={rows.length}
          itemData={itemData}
          itemSize={27}
          height={isNaN(height) ? 0 : height}
          width={totalColumnsWidth > width ? totalColumnsWidth : (isNaN(width) ? 0 : width)}
        >
          {ListRow}
        </FixedSizeList>
      )}
    </AutoSizer>
  );
};
