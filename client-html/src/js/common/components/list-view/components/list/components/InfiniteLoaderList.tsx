/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
  createContext,
  forwardRef,
  memo, useCallback, useMemo
} from "react";
import { FixedSizeList, areEqual } from "react-window";
import AutoSizer from "react-virtualized-auto-sizer";
import InfiniteLoader from "react-window-infinite-loader";
import clsx from "clsx";
import debounce from "lodash.debounce";
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

const ListRow = memo<any>(({ data, index, style }) => {
  const {
    prepareRow,
    rows,
    classes,
    onRowSelect,
    threeColumn,
    onRowDoubleClick,
    onMouseOver
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
        <div>
          <Typography variant="subtitle2" color="textSecondary" component="div" noWrap>
            {row.original.secondary}
          </Typography>
          <Typography variant="body1" component="div" className="centeredFlex" noWrap>
            <span className="flex-fill">
              {row.original.primary}
            </span>
            {row.cells[1].column.tagsVisible && (
              <TagDotRenderer
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
        </div>
      ) : row.cells.filter(cell => ![COLUMN_WITH_COLORS, CHECKLISTS_COLUMN].includes(cell.column.id)).map(cell => (
        <div
          {...cell.getCellProps()}
          className={clsx(classes.bodyCell, cell.column.cellClass)}
          onMouseOver={!["selection", "chooser"].includes(cell.column.id) && onMouseOver ? () => onMouseOver(cell.column.id) : undefined}
        >
          {cell.render("Cell")}
        </div>
      ))}
    </div>
  );
}, areEqual);

const StickyListContext = createContext(null);
StickyListContext.displayName = "StickyListContext";

const innerElementType = forwardRef<any>(({ children, ...rest }, ref) => (
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
  recordsLeft,
  listRef,
  threeColumn,
  onRowDoubleClick,
  onMouseOver,
  mainContentWidth,
  header
}) => {
  const isItemLoaded = index => rows[index];

  const loadMoreItems = useCallback<any>(
    debounce(
      (startIndex, stopIndex) => new Promise(resolve => onLoadMore(startIndex, stopIndex, resolve)),
      600
    ),
    []
  );

  const itemData = useMemo(
    () => ({
      prepareRow,
      rows,
      classes,
      onRowSelect,
      threeColumn,
      onRowDoubleClick,
      onMouseOver,
    }),
    [prepareRow, rows, classes, onRowSelect, onRowDoubleClick, totalColumnsWidth, threeColumn, onMouseOver]
  );

  const itemCount = useMemo(() => (rows.length + (recordsLeft >= LIST_PAGE_SIZE ? LIST_PAGE_SIZE : recordsLeft === 1 ? 0 : recordsLeft))
    + (threeColumn ? 0 : HEADER_ROWS_COUNT),
   [threeColumn, recordsLeft, rows.length]);

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
