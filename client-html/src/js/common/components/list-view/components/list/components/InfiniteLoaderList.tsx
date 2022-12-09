/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, {
  createContext,
  forwardRef,
  memo,
  useMemo,
  useState
} from "react";
import { FixedSizeList, areEqual } from "react-window";
import AutoSizer from "react-virtualized-auto-sizer";
import InfiniteLoader from "react-window-infinite-loader";
import clsx from "clsx";
import Typography from "@mui/material/Typography";
import {
  HEADER_ROWS_COUNT,
  HEADER_ROWS_INDICES,
  LIST_PAGE_SIZE,
  LIST_THREE_COLUMN_ROW_HEIGHT,
  LIST_TWO_COLUMN_ROW_HEIGHT
} from "../../../../../../constants/Config";
import { CHECKLISTS_COLUMN, COLUMN_WITH_COLORS } from "../utils";
import TagDotRenderer from "./TagDotRenderer";
import StaticProgress from "../../../../progress/StaticProgress";
import { stubFunction } from "../../../../../utils/common";

const ThreeColumnCell = ({ row }) => (<div>
  <Typography variant="subtitle2" color="textSecondary" component="div" noWrap>
    {row.original.secondary}
  </Typography>
  <Typography variant="body1" component="div" className="centeredFlex" noWrap>
    <span className="flex-fill text-truncate">
      {row.original.primary}
    </span>
    {row.cells[1].column.tagsVisible && (
      <TagDotRenderer
        className="ml-1"
        colors={row.values[COLUMN_WITH_COLORS]?.replace(/[[\]]/g, "").split(", ")}
      />
    )}
    {row.cells[1].column.checklistsVisible && row.values[CHECKLISTS_COLUMN] && (
      <StaticProgress
        className="ml-1"
        color={row.values[CHECKLISTS_COLUMN].split("|")[0]}
        value={parseFloat(row.values[CHECKLISTS_COLUMN].split("|")[1]) * 100}
        size={18}
      />
    )}
  </Typography>
</div>);

const TwoColumnCell = ({ cell, classes, ...rest }) => (<div
  {...rest}
  className={clsx(classes.bodyCell, cell.column.cellClass)}
>
  {cell.render("Cell")}
</div>);

const ListRow = memo<any>(({ data, index, style }) => {
  const {
    prepareRow,
    rows,
    classes,
    onRowSelect,
    threeColumn,
    onRowDoubleClick
  } = data;

  if (!threeColumn && HEADER_ROWS_INDICES.includes(index)) {
    return null;
  }

  const currentIndex = threeColumn ? index : index - HEADER_ROWS_COUNT;

  const row = rows[currentIndex];
  const rowClasses = clsx(
    classes.row,
    row && row.isSelected && classes.selected,
    row && row.original && row.original.customClasses,
    threeColumn ? classes.threeColumnRow : index % 2 && classes.oddRow
  );

  if (!row) {
    return <div style={style} className={rowClasses} />;
  }
  prepareRow(row);

  return (
    <div
      {...row.getRowProps()}
      style={style}
      className={rowClasses}
      onClick={e => onRowSelect(e, row.id, currentIndex)}
      onDoubleClick={() => onRowDoubleClick(row.id)}
    >
      {threeColumn ? (
        <ThreeColumnCell row={row} />
      ) : row.cells.filter(cell => ![COLUMN_WITH_COLORS, CHECKLISTS_COLUMN].includes(cell.column.id)).map(cell => (
        <TwoColumnCell {...cell.getCellProps()} cell={cell} classes={classes} />
      ))}
    </div>
  );
}, areEqual);

const StickyListContext = createContext(null);
StickyListContext.displayName = "StickyListContext";

const innerElementType = forwardRef<any, { children?: React.ReactNode }>(({ children, ...rest }, ref) => (
  <StickyListContext.Consumer>
    {({ header }) => (
      <div ref={ref} {...rest}>
        {header}
        {children}
      </div>
    )}
  </StickyListContext.Consumer>
));

export default ({
  totalColumnsWidth,
  prepareRow,
  rows,
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
  const [isLoading, setIsLoading] = useState(false);
  
  const isItemLoaded = index => index >= recordsCount ? true : !!rows[index];

  const loadMoreItems = isLoading
    ? stubFunction
    : (startIndex, stopIndex) => {
      setIsLoading(true);
      return new Promise(resolve => onLoadMore(stopIndex, resolve)).then(() => { setIsLoading(false); });
    };

  const itemCountBase = (rows.length + LIST_PAGE_SIZE);

  const itemCount = (itemCountBase < recordsCount ? itemCountBase : recordsCount) + (threeColumn ? 0 : HEADER_ROWS_COUNT);

  const itemData = useMemo(
    () => ({
      prepareRow,
      rows,
      classes,
      onRowSelect,
      threeColumn,
      onRowDoubleClick,
    }),
    [prepareRow, rows, classes, onRowSelect, onRowDoubleClick, totalColumnsWidth, threeColumn]
  );

  return (
    <StickyListContext.Provider value={{ header }}>
      <InfiniteLoader
        threshold={0}
        minimumBatchSize={LIST_PAGE_SIZE}
        isItemLoaded={isItemLoaded}
        itemCount={itemCount}
        loadMoreItems={loadMoreItems}
      >
        {({ onItemsRendered, ref }) => (
          <AutoSizer>
            {({ height, width }) => (
              <FixedSizeList
                style={{ overflow: false }}
                itemCount={itemCount}
                itemData={itemData}
                itemSize={threeColumn ? LIST_THREE_COLUMN_ROW_HEIGHT : LIST_TWO_COLUMN_ROW_HEIGHT}
                height={height}
                width={threeColumn ? mainContentWidth : (totalColumnsWidth > width ? totalColumnsWidth : width)}
                onItemsRendered={onItemsRendered}
                innerElementType={innerElementType}
                ref={r => {
                  if (r) {
                    // eslint-disable-next-line no-param-reassign
                    ref.current = r;
                    // eslint-disable-next-line no-param-reassign
                    listRef.current = r;
                  }
                }}
              >
                {ListRow}
              </FixedSizeList>
            )}
          </AutoSizer>
        )}
      </InfiniteLoader>
    </StickyListContext.Provider>
  );
};