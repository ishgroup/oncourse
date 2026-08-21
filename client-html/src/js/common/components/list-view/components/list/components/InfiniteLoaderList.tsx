/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import Typography from "@mui/material/Typography";
import { flexRender } from '@tanstack/react-table';
import clsx from "clsx";
import { stubFunction } from "ish-ui";
import React, { memo, useMemo, useState } from "react";
import AutoSizer from "react-virtualized-auto-sizer";
import { List } from "react-window";
import { useInfiniteLoader } from "react-window-infinite-loader";
import {
  APP_BAR_HEIGHT,
  HEADER_ROWS_COUNT,
  HEADER_ROWS_INDICES,
  LIST_PAGE_SIZE,
  LIST_TWO_COLUMN_ROW_HEIGHT
} from "../../../../../../constants/Config";
import areEqual from "../../../../../utils/react-window/areEqual";
import StaticProgress from "../../../../progress/StaticProgress";
import { CHECKLISTS_COLUMN, COLUMN_WITH_COLORS } from "../constants";
import TagDotRenderer from "./TagDotRenderer";

const ThreeColumnCell = ({ row }) => (<div>
  <Typography variant="subtitle2" color="textSecondary" component="div" noWrap>
    {row.original.secondary}
  </Typography>
  <Typography variant="body1" component="div" className="centeredFlex" noWrap>
    <span className="flex-fill text-truncate">
      {row.original.primary}
    </span>
    {row.getVisibleCells()[1].column.tagsVisible && (
      <TagDotRenderer
        className="ml-1"
        colors={row.original[COLUMN_WITH_COLORS]?.replace(/[[\]]/g, "").split(", ")}
      />
    )}
    {row.getVisibleCells()[1].column.checklistsVisible && row.original[CHECKLISTS_COLUMN] && (
      <StaticProgress
        className="ml-1"
        color={row.original[CHECKLISTS_COLUMN].split("|")[0]}
        value={parseFloat(row.original[CHECKLISTS_COLUMN].split("|")[1]) * 100}
        size={18}
      />
    )}
  </Typography>
</div>);

const TwoColumnCell = ({ cell, classes }) => (<div
  style={{
    width: cell.column.getSize()
  }}
  className={clsx(classes.bodyCell, cell.column.columnDef.cellClass)}
>
  {flexRender(cell.column.columnDef.cell, cell.getContext())}
</div>);

const ListRow = memo<any>(({
  index,
  style,
  rows,
  classes,
  onRowSelect,
  threeColumn,
  onRowDoubleClick
}) => {
  if (!threeColumn && HEADER_ROWS_INDICES.includes(index)) {
    return null;
  }

  const currentIndex = threeColumn ? index : index - HEADER_ROWS_COUNT;

  const row = rows[currentIndex];

  const rowClasses = clsx(
    classes.row,
    row && row.getIsSelected() && classes.selected,
    row && row.original && row.original.customClasses,
    threeColumn ? classes.threeColumnRow : index % 2 && classes.oddRow
  );

  if (!row) {
    return <div style={style} className={rowClasses}/>;
  }

  return (
    <div
      style={style}
      className={rowClasses}
      onClick={e => onRowSelect(e, row)}
      onDoubleClick={() => onRowDoubleClick(row.id)}
    >
      {threeColumn ? (
        <ThreeColumnCell row={row}/>
      ) : row.getVisibleCells().filter(cell => ![COLUMN_WITH_COLORS, CHECKLISTS_COLUMN].includes(cell.column.id)).map(cell => (
        <TwoColumnCell cell={cell} key={cell.id} classes={classes}/>
      ))}
    </div>
  );
}, areEqual);

export default ({
                  table,
                  classes,
                  onRowSelect,
                  onLoadMore,
                  recordsCount,
                  listRef,
                  threeColumn,
                  onRowDoubleClick,
                  mainContentWidth,
                  header
                }) => {
  const rows = table.getRowModel().rows;
  const totalColumnsWidth = table.getCenterTotalSize();

  const [isLoading, setIsLoading] = useState(false);

  const isItemLoaded = index => index >= recordsCount ? true : !!rows[index];

  const loadMoreItems = isLoading
    ? stubFunction
    : (startIndex, stopIndex) => {
      setIsLoading(true);
      return new Promise(resolve => onLoadMore(stopIndex, resolve)).then(() => {
        setIsLoading(false);
      });
    };

  const itemCountBase = (rows.length + LIST_PAGE_SIZE);

  const itemCount = (itemCountBase < recordsCount ? itemCountBase : recordsCount) + (threeColumn ? 0 : HEADER_ROWS_COUNT);

  const rowProps = useMemo(
    () => ({
      rows,
      classes,
      onRowSelect,
      threeColumn,
      onRowDoubleClick,
    }),
    [rows, classes, onRowSelect, onRowDoubleClick, totalColumnsWidth, threeColumn]
  );

  const onRowsRendered = useInfiniteLoader({
    threshold: 0,
    minimumBatchSize: LIST_PAGE_SIZE,
    isRowLoaded: isItemLoaded,
    rowCount: itemCount,
    loadMoreRows: loadMoreItems
  });

  return (
    <AutoSizer>
      {({ height, width }) => (
        <List
          rowComponent={ListRow as any}
          rowCount={itemCount}
          rowProps={rowProps}
          rowHeight={threeColumn ? APP_BAR_HEIGHT : LIST_TWO_COLUMN_ROW_HEIGHT}
          onRowsRendered={onRowsRendered}
          listRef={listRef}
          style={{
            height,
            width: threeColumn ? mainContentWidth : (totalColumnsWidth > width ? totalColumnsWidth : width)
          }}
        >
          {header && (
            // v2 renders `children` inside the scroll container, so the header still
            // scrolls horizontally with the rows while sticking to the top. The wrapper
            // is sticky and zero-height so it pins to the top without adding its own
            // height to the scrollable content — the first HEADER_ROWS_COUNT rows are
            // already reserved for it and render as null.
            <div style={{ position: "sticky", top: 0, height: 0, zIndex: 2 }}>
              {header}
            </div>
          )}
        </List>
      )}
    </AutoSizer>
  );
};