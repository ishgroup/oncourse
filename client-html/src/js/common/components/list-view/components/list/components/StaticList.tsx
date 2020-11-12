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
import TableCell from "@material-ui/core/TableCell";
import { NestedTableColumnsTypes } from "../../../../../../model/common/NestedTable";
import NestedTableCheckboxCell from "./NestedTableCheckboxCell";
import NestedTableLinkCell from "./NestedTableLinkCell";

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
    prepareRow,
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
    row.isSelected && classes.selected,
    row.original && row.original.customClasses,
    index % 2 && classes.oddRow
  );
  prepareRow(row);

  return (
    <div
      {...row.getRowProps()}
      style={style}
      className={rowClasses}
      onClick={() => onRowSelect(row.id)}
      onDoubleClick={() => (onRowDoubleClick ? onRowDoubleClick(row.original.initial) : null)}
    >
      {row.cells.map(cell => (
        <TableCell
          component="div"
          {...cell.getCellProps()}
          className={clsx(classes.bodyCell, cell.column.cellClass)}
        >
          <ListCell
            value={cell.render("Cell")}
            column={cell.column}
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
    prepareRow,
    rows,
    classes,
    onRowDoubleClick,
    onCheckboxChange,
    onRowSelect
  }) => {
  const itemData = useMemo(
    () => ({
      prepareRow,
      rows,
      classes,
      onRowDoubleClick,
      onCheckboxChange,
      onRowSelect
    }),
    [prepareRow, rows, classes, onRowDoubleClick, onCheckboxChange, onRowSelect, totalColumnsWidth]
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
