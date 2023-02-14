/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
  memo, useMemo
} from "react";
import { FixedSizeList, areEqual } from "react-window";
import AutoSizer from "react-virtualized-auto-sizer";
import clsx from "clsx";
import TableCell from "@mui/material/TableCell";
import { NestedTableColumnsTypes } from "../../../../../../model/common/NestedTable";
import NestedTableCheckboxCell from "./NestedTableCheckboxCell";
import NestedTableLinkCell from "./NestedTableLinkCell";
import { flexRender } from "@tanstack/react-table";

const ListCell = React.memo<any>(({
 value, fieldName, column, row, onCheckboxChange, classes
} ) => {
  switch (column.type as NestedTableColumnsTypes) {
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

const ListRow = memo<any>(({ data, index, style }) => {
  const {
    rows,
    classes,
    onRowSelect,
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
            classes={classes}
          />
        </TableCell>
      ))}
    </div>
  );
}, areEqual);

export default ({
  totalColumnsWidth,
  rows,
  classes,
  onRowDoubleClick,
  onCheckboxChange,
  onRowSelect
}) => {
  const itemData = useMemo(
    () => ({
      rows,
      classes,
      onRowDoubleClick,
      onCheckboxChange,
      onRowSelect
    }),
    [rows, classes, onRowDoubleClick, onCheckboxChange, onRowSelect, totalColumnsWidth]
  );

  return (
    <AutoSizer>
      {({ height, width }) => (
        <FixedSizeList
          style={{ overflow: "hidden auto" }}
          itemCount={rows.length}
          itemData={itemData}
          itemSize={27}
          height={height}
          width={totalColumnsWidth > width ? totalColumnsWidth : width}
        >
          {ListRow}
        </FixedSizeList>
      )}
    </AutoSizer>
  );
};
