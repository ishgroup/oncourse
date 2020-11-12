/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
  memo, useCallback, useMemo
} from "react";
import { FixedSizeList, areEqual } from "react-window";
import AutoSizer from "react-virtualized-auto-sizer";
import InfiniteLoader from "react-window-infinite-loader";
import clsx from "clsx";
import TableCell from "@material-ui/core/TableCell";
import debounce from "lodash.debounce";
import Typography from "@material-ui/core/Typography";
import { LIST_PAGE_SIZE } from "../../../../../../constants/Config";

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

  const row = rows[index];
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
      onClick={e => onRowSelect(e, row.id, index)}
      onDoubleClick={() => onRowDoubleClick(row.id)}
    >
      {threeColumn ? (
        <div>
          <Typography variant="subtitle2" color="textSecondary" noWrap>
            {row.original.secondary}
          </Typography>
          <Typography variant="body1" noWrap>
            {row.original.primary}
          </Typography>
        </div>
      ) : row.cells.map(cell => (
        <TableCell
          component="div"
          {...cell.getCellProps()}
          className={clsx(classes.bodyCell, cell.column.cellClass)}
          onMouseOver={!["selection", "chooser"].includes(cell.column.id) && onMouseOver ? () => onMouseOver(cell.column.id) : undefined}
        >
          {cell.render("Cell")}
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
  onRowSelect,
  onLoadMore,
  recordsLeft,
  listRef,
  threeColumn,
  onRowDoubleClick,
  onMouseOver
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
      onMouseOver
    }),
    [prepareRow, rows, classes, onRowSelect, onRowDoubleClick, totalColumnsWidth, threeColumn, onMouseOver]
  );

  const itemCount = useMemo(() => rows.length + (recordsLeft >= LIST_PAGE_SIZE ? LIST_PAGE_SIZE : recordsLeft === 1 ? 0 : recordsLeft), [
    recordsLeft,
    rows.length
  ]);

  return (
    <div className={clsx("w-100 h-100 d-block", classes.infiniteLoaderListRoot)}>
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
                itemSize={threeColumn ? 64 : 27}
                height={height}
                width={!threeColumn && totalColumnsWidth > width ? totalColumnsWidth : width}
                onItemsRendered={onItemsRendered}
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
    </div>
  );
};
