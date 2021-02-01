/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
  useCallback, useEffect, useMemo, useRef, useState
} from "react";
import {
  useTable,
  useBlockLayout,
  useRowSelect,
  useSortBy,
  useResizeColumns,
  useColumnOrder
} from "react-table";
import makeStyles from "@material-ui/core/styles/makeStyles";
import MaUTable from "@material-ui/core/Table";
import TableCell from "@material-ui/core/TableCell";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import TableSortLabel from "@material-ui/core/TableSortLabel";
import debounce from "lodash.debounce";
import Typography from "@material-ui/core/Typography";
import DragIndicator from "@material-ui/icons/DragIndicator";
import clsx from "clsx";
import { DragDropContext, Droppable, Draggable } from "react-beautiful-dnd";
import { Column, DataResponse, TableModel } from "@api/model";
import InfiniteLoaderList from "./components/InfiniteLoaderList";
import { AnyArgFunction } from "../../../../../model/common/CommonFunctions";
import { getTableRows } from "./utils";
import { StyledCheckbox } from "../../../form/form-fields/CheckboxField";
import { CustomColumnFormats } from "../../../../../model/common/ListView";
import ColumnChooser from "./components/ColumnChooser";
import { StringKeyObject } from "../../../../../model/common/CommomObjects";
import styles from "./styles";

const COLUMN_MIN_WIDTH = 55;

const useStyles = makeStyles(styles);

const listRef = React.createRef<any>();

const getRowId = row => row.id;

interface ListTableProps extends Partial<ListProps>{
  columns: any;
  data: any;
  sorting: any;
  onChangeColumns: (arg: StringKeyObject<any>, listUpdate?: boolean) => void;
  onChangeColumnsOrder: (arg: string[]) => void;
}

const Table: React.FC<ListTableProps> = ({
  columns,
  data,
  onLoadMore,
  onChangeModel,
  onChangeColumns,
  recordsLeft,
  sorting,
  threeColumn,
  onSelectionChange,
  onRowDoubleClick,
  selection,
  getContainerNode,
  onChangeColumnsOrder
}) => {
  const [isDraggingColumn, setColumnIsDragging] = useState(false);

  const isMountedRef = useRef(false);
  const isResizingRef = useRef(false);
  const tableRef = useRef<any>();

  useEffect(() => {
    if (tableRef.current) {
      getContainerNode(tableRef.current);
    }
  }, [tableRef.current]);

  const onSelectionChangeHangler = newSelection => {
    if (newSelection.length === 1 && selection.length === 1 && newSelection[0] === selection[0]) {
      return;
    }
    onSelectionChange(newSelection);
  };

  const onChangeColumnsWidthDebounced = useCallback<any>(debounce((id, width) => {
    onChangeColumns({
      [id]: {
        width
      }
    });
    isResizingRef.current = false;
  }, 500), [onChangeColumns]);

  const selectedRowIdsObj = useMemo(() => (selection ? selection.reduce((p, c) => ({ ...p, [c]: true }), []) : []), [
    selection
  ]);

  const classes = useStyles();

  const defaultColumn = useMemo(
    () => ({
      width: 40
    }),
    []
  );

  const initialState = useMemo(() => ({
    sortBy: sorting,
    hiddenColumns: columns.filter(c => !c.visible).map(c => c.id),
    columnOrder: columns.map(c => c.id)
  }), [sorting, columns]);

  const onRowSelect = useCallback((e, id, index) => {
    if (e.shiftKey && selection.length) {
      const firstItem = data.find(d => d.id === selection[0]);
      const lastItem = data.find(d => d.id === selection[selection.length - 1]);
      if (firstItem && lastItem) {
        const firstSelectedIndex = firstItem.index;
        const lastSelectedIndex = lastItem.index;
        const selectionData = data
          .slice(Math.min(firstSelectedIndex, index), Math.max(lastSelectedIndex, index) + 1)
          .map(d => d.id);

        onSelectionChangeHangler(selectionData);
      }
      return;
    }
    if (e.ctrlKey || e.metaKey) {
      const updated = [...selection];
      const idIndex = updated.indexOf(id);
      if (idIndex === -1) {
        updated.push(id);
      } else {
        updated.splice(idIndex, 1);
      }
      onSelectionChangeHangler(updated);
      return;
    }
    onSelectionChangeHangler([id]);
  }, [data, selection]);

  const onRowCheckboxSelect = (e, id, st) => {
    e.stopPropagation();
    const updated = Object.keys(st.selectedRowIds).filter(k => st.selectedRowIds[k]);
    const idIndex = updated.indexOf(id);
    if (idIndex === -1) {
      updated.push(id);
    } else {
      updated.splice(idIndex, 1);
    }
    onSelectionChangeHangler(updated);
  };

  const onScroll = e => {
    if (listRef.current) {
      listRef.current.scrollTo(e.currentTarget.scrollTop as any);
    }
  };

  const {
    rows,
    state,
    allColumns,
    prepareRow,
    headerGroups,
    getTableProps,
    setColumnOrder,
    getTableBodyProps,
    totalColumnsWidth
  } = useTable(
    {
      columns,
      data,
      defaultColumn,
      initialState,
      getRowId,
      manualSortBy: true,
      useControlledState: state => useMemo(
        () => ({
          ...state,
          selectedRowIds: selectedRowIdsObj
        }),
        [state, selectedRowIdsObj]
      )
    },
    useSortBy,
    useRowSelect,
    useColumnOrder,
    useBlockLayout,
    useResizeColumns,
    hooks => {
      hooks.visibleColumns.push(columns => [
        {
          id: "selection",
          disableResizing: true,
          Cell: ({ row, state }) => (
            <StyledCheckbox
              checked={row.isSelected}
              className={classes.selectionCheckbox}
              onClick={e => onRowCheckboxSelect(e, row.id, state)}
            />
          )
        },
        ...columns,
        {
          id: "chooser",
          disableResizing: true,
          width: 80
        }
      ]);
    }
  );

  // Table model change effects
  useEffect(() => {
    if (isMountedRef.current && tableRef.current) {
      if (tableRef.current.scrollTop) {
        tableRef.current.scrollTop = 0;
      }
      setTimeout(() => {
        onChangeModel({
          sortings: state.sortBy.map(({ id, desc }) => ({
            attribute: id,
            ascending: !desc,
            complexAttribute: columns.find(c => c.id === id).complexAttribute
          }))
        }, true);
      }, 500);
    } else {
      isMountedRef.current = true;
    }
  }, [state.sortBy]);

  const onHiddenChange = useCallback<any>(debounce(hiddenColumns => {
    const updated = {};
    columns.forEach(c => {
      updated[c.id] = {
        visible: !hiddenColumns.includes(c.id)
      };
    });
    onChangeColumns(updated, true);
  }, 500), [columns]);

  const onOrderChange = useCallback<any>(debounce(columnsOrder => {
    if (tableRef.current && tableRef.current.scrollTop) tableRef.current.scrollTop = 0;
    onChangeColumnsOrder(columnsOrder);
  }, 500), [columns]);

  useEffect(() => {
    if (isMountedRef.current && !isDraggingColumn && JSON.stringify(state.columnOrder) !== JSON.stringify(initialState.columnOrder)) {
      onOrderChange(state.columnOrder);
    }
  }, [isDraggingColumn]);

  useEffect(() => {
    if (isMountedRef.current && listRef.current) {
      onHiddenChange(state.hiddenColumns);
    }
  }, [state.hiddenColumns]);

  useEffect(() => {
    if (state.columnResizing.isResizingColumn) {
      isResizingRef.current = true;
      const column = state.columnResizing.isResizingColumn;
      const width = state.columnResizing.columnWidths[state.columnResizing.isResizingColumn];
      onChangeColumnsWidthDebounced(column, width >= COLUMN_MIN_WIDTH ? width : COLUMN_MIN_WIDTH);
    }
  }, [state.columnResizing.columnWidths]);

  useEffect(() => {
    if (isMountedRef.current && tableRef.current && listRef.current) {
      listRef.current.scrollTo(0);
      setTimeout(() => {
        if (tableRef.current) {
          tableRef.current.scrollTop = 10;
          tableRef.current.scrollLeft = 0;
          tableRef.current.scrollTop = 0;
        }
      }, 200);
    }
  }, [threeColumn]);

  const onColumnOrderChange = useCallback(({
 destination, source, fields, headers
}) => {
    if (destination) {
      const findDestinationColumn = headers[destination.index];
      const findSourceColumn = headers[source.index];
      if (findDestinationColumn && findSourceColumn) {
        const dIndex = fields.indexOf(findDestinationColumn.id);
        const sIndex = fields.indexOf(findSourceColumn.id);
        if (dIndex !== -1 && sIndex !== -1 && dIndex !== sIndex) {
          const updated = Array.from(fields);
          const [removed] = updated.splice(sIndex, 1);
          updated.splice(dIndex, 0, removed);
          setColumnOrder(updated);
        }
      }
    }
    setColumnIsDragging(false);
  }, []);

  const getItemStyle = (isDragging, draggableStyle) => {
    if (isDragging) {
      setColumnIsDragging(true);
      if (listRef.current && listRef.current.scrollTop) listRef.current.scrollTop = 0;
      if (tableRef.current && tableRef.current.scrollTop) tableRef.current.scrollTop = 0;
    }
    return {
      userSelect: 'none',
      ...draggableStyle,
    };
  };

  const Header = useMemo(() => (
    <TableHead component="div" className={classes.header}>
      {headerGroups.map((headerGroup, groupIndex) => (
        <DragDropContext
          key={groupIndex}
          onDragEnd={args => onColumnOrderChange({ ...args, fields: state.columnOrder, headers: headerGroup.headers })}
        >
          <Droppable key={headerGroup.getHeaderGroupProps().key} droppableId="droppable" direction="horizontal">
            {provided => (
              <TableRow
                {...provided.droppableProps}
                ref={provided.innerRef}
                className={classes.headerRow}
                component="div"
              >
                {headerGroup.headers.map((column, columnIndex) => {
                  const disabledCell = ["selection", "chooser"].includes(column.id);
                  return (
                    <TableCell
                      {...column.getHeaderProps()}
                      className={clsx(classes.headerCell, classes.listHeaderCell)}
                      component="div"
                    >
                      <div
                        className={clsx("centeredFlex", column.cellClass)}
                      >
                        <Draggable
                          key={columnIndex}
                          draggableId={columnIndex.toString()}
                          index={columnIndex}
                          isDragDisabled={disabledCell}
                        >
                          {(provided, snapshot) => {
                            const isDragging = snapshot.isDragging;
                            return (
                              <div
                                ref={provided.innerRef}
                                {...provided.draggableProps}
                                {...provided.dragHandleProps}
                                style={getItemStyle(
                                  snapshot.isDragging,
                                  provided.draggableProps.style
                                )}
                                className={clsx(
                                  "centeredFlex text-truncate text-nowrap outline-none",
                                  classes.draggableCellItem,
                                  { [classes.dragOver]: isDragging }
                                )}
                              >
                                {!disabledCell && (
                                  <DragIndicator
                                    className={
                                      clsx("dndActionIcon", classes.dragIndicator, { [classes.visibleDragIndicator]: isDragging })
                                    }
                                  />
                                )}
                                {column.render("Header")}
                                &nbsp;
                              </div>
                            );
                          }}
                        </Draggable>
                        {!isDraggingColumn && (
                          <TableSortLabel
                            {...column.getSortByToggleProps()}
                            hideSortIcon={!column.canSort}
                            active={column.isSorted}
                            direction={column.isSortedDesc ? "desc" : "asc"}
                            classes={{
                              root: clsx(
                                !column.canSort && classes.noSort,
                                column.colClass
                              ),
                              icon: classes.tableSortLabel
                            }}
                            component="span"
                          />
                        )}
                        {!isDraggingColumn && column.canResize && <div {...column.getResizerProps()} className={classes.resizer} />}
                      </div>
                    </TableCell>
                  );
                })}
                {provided.placeholder}
              </TableRow>
            )}
          </Droppable>
        </DragDropContext>
      ))}
    </TableHead>
  ), [headerGroups, isDraggingColumn]);

  const List = useMemo(() => (rows.length ? (
    <InfiniteLoaderList
      listRef={listRef}
      prepareRow={prepareRow}
      rows={rows}
      classes={classes}
      onRowSelect={onRowSelect}
      totalColumnsWidth={totalColumnsWidth}
      onLoadMore={onLoadMore}
      recordsLeft={recordsLeft}
      threeColumn={threeColumn}
      onRowDoubleClick={onRowDoubleClick}
      onMouseOver={() => {}}
    />
  ) : (
    <div className="noRecordsMessage h-100">
      <Typography variant="h6" color="inherit" align="center">
        No data
      </Typography>
    </div>
  )), [rows, totalColumnsWidth, selectedRowIdsObj, recordsLeft, threeColumn, onRowDoubleClick, state.columnOrder]);

  return (
    <>
      {!threeColumn && <ColumnChooser columns={allColumns} classes={classes} />}
      <MaUTable
        {...getTableProps()}
        ref={tableRef}
        className={clsx(classes.table, { [classes.hideOverflowY]: isDraggingColumn })}
        onScroll={onScroll}
        component="div"
      >
        {!threeColumn && Header}
        <div {...getTableBodyProps()} className={classes.tableBody}>
          {List}
        </div>
      </MaUTable>
    </>
  );
};

export interface ListProps {
  onLoadMore?: (startIndex: number, stopIndex: number, resolve: AnyArgFunction) => void;
  shortCurrencySymbol?: string;
  records?: DataResponse;
  recordsLeft?: number;
  onChangeModel?: (model: TableModel, listUpdate?: boolean) => void;
  customColumnFormats?: CustomColumnFormats;
  setRowClasses?: AnyArgFunction<string>;
  threeColumn?: boolean;
  primaryColumn: string;
  secondaryColumn: string;
  primaryColumnCondition?: (tableRow: any) => any;
  secondaryColumnCondition?: (tableRow: any) => any;
  onRowDoubleClick?: (id: string) => void;
  onSelectionChange?: any;
  selection?: string[];
  firstColumnName?: string;
  getContainerNode?: AnyArgFunction;
  updateColumns?: (columns: Column[]) => void;
}

const ListRoot = React.memo<ListProps>(({
  records,
  recordsLeft,
  shortCurrencySymbol,
  onLoadMore,
  onChangeModel,
  customColumnFormats,
  setRowClasses,
  threeColumn,
  primaryColumn,
  secondaryColumn,
  primaryColumnCondition,
  secondaryColumnCondition,
  onRowDoubleClick,
  onSelectionChange,
  selection,
  firstColumnName,
  getContainerNode,
  updateColumns
}) => {
  const columns = useMemo(
    () => {
      const result = records.columns.map((c, i) => ({
        index: i,
        id: c.attribute,
        Header: <span className="text-truncate text-nowrap">{c.title}</span>,
        accessor: row => row[`${c.attribute}`],
        visible: c.visible,
        width: c.width + 24,
        cellClass: c.type === "Money" ? "money text-end justify-content-end" : null,
        colClass: c.type === "Money" ? "justify-content-end" : null,
        minWidth: COLUMN_MIN_WIDTH,
        disableSortBy: !c.sortable,
        complexAttribute: c.sortFields,
        disableVisibility: [primaryColumn,
          secondaryColumn].includes(c.attribute)
      }));

      if (firstColumnName) {
        result.sort(
          (a, b) => (a.id === firstColumnName ? -1 : b.id === firstColumnName ? 1 : 0)
        );
      }

      return result;
    },
    [records.columns, firstColumnName]
  );

  const sorting = useMemo(() => records.sort.map(s => ({ id: s.attribute, desc: !s.ascending })), [records.sort]);

  const rows = useMemo(() => getTableRows(
    records,
    shortCurrencySymbol,
    primaryColumn,
    secondaryColumn,
    primaryColumnCondition,
    secondaryColumnCondition,
    customColumnFormats,
    setRowClasses
  ), [records.rows, records.columns, shortCurrencySymbol]);

  const onChangeColumns = useCallback((columnsUpdated, listUpdate) => {
    const columns = records.columns.map(c => {
      if (columnsUpdated[c.attribute]) {
        return {
          ...c,
          width: columnsUpdated[c.attribute].width || c.width,
          visible: typeof columnsUpdated[c.attribute].visible === "boolean" ? columnsUpdated[c.attribute].visible : c.visible
        };
      }
      return c;
    });

    if (!listUpdate) {
      updateColumns(columns);
    }

    onChangeModel({
      columns
    }, listUpdate);
  }, [records.columns]);

  const onChangeColumnsOrder = useCallback(columnsOrder => {
    onChangeModel({
      columns: columnsOrder.map(id => records.columns.find(c => c.attribute === id))
    }, true);
  }, [records.columns]);

  return columns.length
    ? (
      <Table
        columns={columns}
        data={rows}
        sorting={sorting}
        onChangeColumns={onChangeColumns}
        onChangeColumnsOrder={onChangeColumnsOrder}
        threeColumn={threeColumn}
        onLoadMore={onLoadMore}
        recordsLeft={recordsLeft}
        onChangeModel={onChangeModel}
        onRowDoubleClick={onRowDoubleClick}
        onSelectionChange={onSelectionChange}
        selection={selection}
        getContainerNode={getContainerNode}
      />
    )
    : null;
});

export default ListRoot;
