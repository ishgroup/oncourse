/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, {
  useCallback, useEffect, useMemo, useRef, useState
} from "react";
import {
  ColumnDef,
  ColumnOrderState,
  flexRender,
  CellContext,
  ColumnDefTemplate,
  getCoreRowModel,
  useReactTable, RowSelectionState, ColumnSizingState,
} from '@tanstack/react-table';
import makeStyles from "@mui/styles/makeStyles";
import TableSortLabel from "@mui/material/TableSortLabel";
import debounce from "lodash.debounce";
import Typography from "@mui/material/Typography";
import DragIndicator from "@mui/icons-material/DragIndicator";
import clsx from "clsx";
import { DragDropContext, Draggable, Droppable } from "react-beautiful-dnd-next";
import { Column, DataResponse, TableModel } from "@api/model";
import InfiniteLoaderList from "./components/InfiniteLoaderList";
import { AnyArgFunction } from "../../../../../model/common/CommonFunctions";
import { getTableRows } from "./utils";
import { StyledCheckbox } from "../../../form/formFields/CheckboxField";
import { CustomColumnFormats } from "../../../../../model/common/ListView";
import ColumnChooser from "./components/ColumnChooser";
import { StringKeyObject } from "../../../../../model/common/CommomObjects";
import styles from "./styles";
import TagDotRenderer from "./components/TagDotRenderer";
import StaticProgress from "../../../progress/StaticProgress";
import { CHECKLISTS_COLUMN, CHOOSER_COLUMN, COLUMN_WITH_COLORS, SELECTION_COLUMN } from "./constants";

const COLUMN_MIN_WIDTH = 55;

const useStyles = makeStyles(styles);

const listRef = React.createRef<any>();

const getRowId = row => row.id;

interface ListTableProps extends Partial<TableListProps> {
  columnsBase: any;
  data: any;
  sorting: any;
  recordsCount: number;
  onChangeColumns: (arg: StringKeyObject<any>, attr: keyof Column, listUpdate?: boolean) => void;
  onChangeColumnsOrder: (arg: string[]) => void;
}

const isNewColumnsEquals = (newCol: string[], prevCol: string[]) => {
  let isEquals = true;

  if (newCol?.length !== prevCol?.length) return false;

  for (const col of newCol) {
    if (!prevCol?.includes(col)) {
      isEquals = false;
      break;
    }
  }
  return isEquals;
};

const Table: React.FC<ListTableProps> = ({
   columnsBase,
   data,
   onLoadMore,
   onChangeModel,
   onChangeColumns,
   sorting,
   threeColumn,
   onSelectionChange,
   onRowDoubleClick,
   selection,
   getContainerNode,
   onChangeColumnsOrder,
   mainContentWidth,
   sidebarWidth,
   recordsCount
  }) => {
  const [isDraggingColumn, setColumnIsDragging] = useState(false);
  const [columnVisibility, onColumnVisibilityChange] = React.useState({});
  const [columnSizing, onColumnSizingChange] = React.useState<ColumnSizingState>({});
  const [columnOrder, onColumnOrderChange] = React.useState<ColumnOrderState>([]);
  const [rowSelection, onRowSelectionChange] = React.useState<RowSelectionState>({});

  const isMountedRef = useRef(false);
  const tableRef = useRef<any>();

  const classes = useStyles();

  const onSelectionChangeHangler = newSelection => {
    if (newSelection.length === 1 && selection.length === 1 && newSelection[0] === selection[0]) {
      return;
    }
    onSelectionChange(newSelection);
  };

  useEffect(() => {
    if (tableRef.current) {
      getContainerNode(tableRef.current);
    }
  }, [tableRef.current]);

  useEffect(() => {
    onSelectionChangeHangler(Object.keys(rowSelection).map(k => k));
  }, [rowSelection]);

  const toggleRowSelect = id => {
    const updated = { ...table.getState().rowSelection };
    if (updated[id]) {
      delete updated[id];
    } else {
      updated[id] = true;
    }
    onRowSelectionChange(updated);
  };

  const onRowSelect = (e, row) => {
    const currentSelection = table.getState().rowSelection;
    const currentSelectionKeys = Object.keys(currentSelection);

    if (e.shiftKey && currentSelectionKeys.length) {
      const rowsById = table.getRowModel().rowsById;
      const selectionIndicies = currentSelectionKeys.map(id => rowsById[id].index);
      const firstSelectedIndex = Math.min(...selectionIndicies);
      const lastSelectedIndex = Math.max(...selectionIndicies);

      const selectionData = table.getRowModel().rows
        .slice(Math.min(firstSelectedIndex, row.index), Math.max(lastSelectedIndex, row.index) + 1)
        .reduce((p, c) => {
          p[c.id] = true;
          return p;
        }, {});

      onRowSelectionChange(selectionData);
      return;
    }
    if (e.ctrlKey || e.metaKey) {
      toggleRowSelect(row.id);
      return;
    }
    onRowSelectionChange({ [row.id]: true });
  };

  const onRowCheckboxSelect = (e, id) => {
    e.stopPropagation();
    toggleRowSelect(id);
  };

  const columns = useMemo<ColumnDef<any>[]>(
    () => ([
      {
        id: SELECTION_COLUMN,
        disableResizing: true,
        size: 40,
        cell: ({ row }) => {
          return  <>
            <StyledCheckbox
              checked={row.getIsSelected()}
              className={classes.selectionCheckbox}
              onClick={e => onRowCheckboxSelect(e, row.id)}
            />
            {row.original[COLUMN_WITH_COLORS] && (
              <TagDotRenderer
                colors={row.original[COLUMN_WITH_COLORS]?.replace(/[[\]]/g, "").split(", ")}
                className={classes.listDots}
              />
            )}
          </>;
        }
      },
      ...columnsBase,
      {
        id: CHOOSER_COLUMN,
        disableResizing: true,
        size: 80
      }
    ]),
    [columnsBase]
  );

  // const initialState = useMemo(() => ({
  //   sortBy: sorting,
  //   hiddenColumns: columns.filter(c => !c.visible).map(c => c.id),
  // }), [sorting, columns]);
  
  useEffect(() => {
    const updatedSizing: ColumnSizingState = {};
    const updatedOrder: ColumnOrderState = [];
    columns.forEach(c => {
      updatedSizing[c.id] = c.size;
      updatedOrder.push(c.id);
    });
    onColumnOrderChange(updatedOrder);
    onColumnSizingChange(updatedSizing);
  }, [columns]);

  const onScroll = e => {
    if (listRef.current) {
      listRef.current.scrollTo(e.currentTarget.scrollTop as any);
    }
  };

  const table = useReactTable({
    data,
    columns,
    onRowSelectionChange,
    onColumnVisibilityChange,
    onColumnOrderChange,
    onColumnSizingChange,
    state: {
      columnVisibility,
      columnOrder,
      rowSelection,
      columnSizing
    },
    enableRowSelection: true,
    enableMultiRowSelection: true,
    columnResizeMode: "onChange",
    getCoreRowModel: getCoreRowModel(),
    getRowId
  });

  // Table model change effects
  // useEffect(() => {
  //   if (isMountedRef.current && tableRef.current) {
  //     if (tableRef.current.scrollTop) {
  //       tableRef.current.scrollTop = 0;
  //     }
  //     setTimeout(() => {
  //       onChangeModel({
  //         sortings: state.sortBy.map(({ id, desc }) => ({
  //           attribute: id,
  //           ascending: !desc,
  //           complexAttribute: columns.find(c => c.id === id).complexAttribute
  //         }))
  //       }, true);
  //     }, 500);
  //   } else {
  //     isMountedRef.current = true;
  //   }
  // }, [state.sortBy]);

  useEffect(() => {
    isMountedRef.current = true;
  }, []);

  const onHiddenChange = useCallback<any>(debounce(hiddenColumns => {
    const updated = {};
    columns.forEach(c => {
      updated[c.id] = {
        visible: !hiddenColumns.includes(c.id)
      };
    });
    onChangeColumns(updated, "visible", true);
  }, 500), [columns]);

  const onOrderChange = useCallback<any>(debounce(columnsOrder => {
    if (tableRef.current && tableRef.current.scrollTop) tableRef.current.scrollTop = 0;
    onChangeColumnsOrder(columnsOrder);
  }, 500), [columns]);

  // const prevHiddenColumns = usePrevious(state.hiddenColumns);
  //
  // useEffect(() => {
  //   if (isMountedRef.current && listRef.current && !isNewColumnsEquals(state.hiddenColumns, prevHiddenColumns)) {
  //     onHiddenChange(state.hiddenColumns);
  //   }
  // }, [state.hiddenColumns]);

  // useEffect(() => {
  //   if (state.columnResizing.isResizingColumn) {
  //     const column = state.columnResizing.isResizingColumn;
  //     const width = state.columnResizing.columnWidths[state.columnResizing.isResizingColumn];
  //     onChangeColumnsWidthDebounced(column, width >= COLUMN_MIN_WIDTH ? width : COLUMN_MIN_WIDTH);
  //   }
  // }, [state.columnResizing.columnWidths]);

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

  const reorderColumns = ({
     destination, source, headers
   }) => {
    if (destination) {
      const findDestinationColumn = headers[destination.index];
      const findSourceColumn = headers[source.index];
      if (findDestinationColumn && findSourceColumn) {
        const fields = table.getState().columnOrder;
        const dIndex = fields.indexOf(findDestinationColumn.id);
        const sIndex = fields.indexOf(findSourceColumn.id);
        if (dIndex !== -1 && sIndex !== -1 && dIndex !== sIndex) {
          const updated = Array.from(fields);
          const [removed] = updated.splice(sIndex, 1);
          updated.splice(dIndex, 0, removed);
          onColumnOrderChange(updated as any);
          onOrderChange(updated.filter(id => ![SELECTION_COLUMN, CHOOSER_COLUMN].includes(id)));
        }
      }
    }
    setColumnIsDragging(false);
  };

  const getItemStyle = (isDragging, draggableStyle) => {
    if (isDragging) {
      if (listRef.current && listRef.current.scrollTop) listRef.current.scrollTop = 0;
      if (tableRef.current && tableRef.current.scrollTop) tableRef.current.scrollTop = 0;
    }
    return {
      userSelect: 'none',
      ...draggableStyle,
      ...isDragging ? { left: draggableStyle.left - sidebarWidth + tableRef.current.scrollLeft } : {}
    };
  };
  
  const updateRemoteColumnsWidth = () => {
    onChangeColumns(table.getState().columnSizing, "width");
  };

  const Header = useMemo(() => (
    <div className={classes.header}>
      {table.getHeaderGroups().map((headerGroup, groupIndex) => (
        <DragDropContext
          key={groupIndex}
          onDragEnd={args => reorderColumns({
            ...args,
            headers: headerGroup.headers.filter(column => ![COLUMN_WITH_COLORS, CHECKLISTS_COLUMN].includes(column.id))
          })}
          onDragStart={() => setColumnIsDragging(true)}
        >
          <Droppable key={headerGroup.id} droppableId="droppable" direction="horizontal">
            {(provided, snapshot) => (
              <div
                {...provided.droppableProps}
                ref={provided.innerRef}
                className={classes.headerRow}
                style={{ ...snapshot.isDraggingOver ? { pointerEvents: "none" } : {} }}
              >
                {headerGroup.headers.filter(({ column }) => ![COLUMN_WITH_COLORS, CHECKLISTS_COLUMN].includes(column.id)).map(({ column, getContext, getResizeHandler }, columnIndex) => {
                  const disabledCell = [SELECTION_COLUMN, CHOOSER_COLUMN].includes(column.id);
                  const columnDef = column.columnDef as any;
                  const canSort = column.getCanSort();
                  const canResize = column.getCanResize();

                  return (
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
                            style={getItemStyle(
                              snapshot.isDragging,
                              provided.draggableProps.style
                            )}
                          >
                            <div
                               style={{ width: column.getSize() }}
                               className={clsx(
                                 classes.draggableCellItem,
                                 "text-truncate text-nowrap",
                                 {
                                   [classes.isDragging]: isDragging,
                                   [classes.rightAlighed]: columnDef.type === "Money",
                                   [classes.activeRight]: columnDef.type === "Money" && column.getIsSorted()
                                 })
                               }
                            >
                              <Typography
                                variant="subtitle2"
                                color="textSecondary"
                                component="div"
                                fontSize="inherit"
                                position="relative"
                                display="flex"
                                className={columnDef.cellClass}
                              >
                                {!disabledCell && (<>
                                  <span  {...provided.dragHandleProps} className="relative">
                                    <DragIndicator
                                      className={
                                        clsx(
                                          "dndActionIcon",
                                          classes.dragIndicator,
                                          {
                                            [classes.visibleDragIndicator]: isDragging
                                          },
                                        )
                                      }
                                    />
                                  </span>
                                  <TableSortLabel
                                    hideSortIcon={isDragging || !canSort}
                                    active={Boolean(column.getIsSorted())}
                                    direction={column.getAutoSortDir()}
                                    classes={{
                                      root: clsx(
                                        canSort ? classes.canSort : classes.noSort,
                                        columnDef.colClass,
                                        "overflow-hidden"
                                      ),
                                      icon: columnDef.type === "Money" && canSort && classes.rightSort
                                    }}
                                    component="span"
                                  >
                                    {flexRender(columnDef.header, getContext())}
                                    &nbsp;
                                  </TableSortLabel>
                                </>
                                )}
                              </Typography>
                                {!isDraggingColumn && canResize &&
                                  <div
                                    className={classes.resizer}
                                    onMouseDown={getResizeHandler()}
                                    onTouchStart={getResizeHandler()}
                                    onMouseUp={updateRemoteColumnsWidth}
                                  />
                                }
                            </div>
                          </div>
                        );
                      }}
                    </Draggable>
                  );
                })}
              </div>
            )}
          </Droppable>
        </DragDropContext>
      ))}
    </div>
  ), [isDraggingColumn, columnSizing]);

  const List = useMemo(() => (data.length ? (
    <InfiniteLoaderList
      listRef={listRef}
      table={table}
      classes={classes}
      onRowSelect={onRowSelect}
      onLoadMore={onLoadMore}
      recordsCount={recordsCount}
      threeColumn={threeColumn}
      onRowDoubleClick={onRowDoubleClick}
      mainContentWidth={mainContentWidth}
      header={!threeColumn && Header}
    />
  ) : (
    <div className="noRecordsMessage h-100">
      <Typography variant="h6" color="inherit" align="center">
        No data
      </Typography>
    </div>
  )), [Header, rowSelection, recordsCount, mainContentWidth, threeColumn, onRowDoubleClick]);

  return (
    <div
      ref={tableRef}
      className={classes.table}
      style={threeColumn ? { overflowX: "hidden" } : null}
      onScroll={onScroll}
    >
      {!threeColumn && <ColumnChooser columns={table.getAllColumns()} classes={classes} />}
      <div className="flex-fill">
        {List}
      </div>
    </div>
  );
};

export interface TableListProps {
  onLoadMore?: (startIndex: number, stopIndex: number, resolve: AnyArgFunction) => void;
  shortCurrencySymbol?: string;
  records?: DataResponse;
  sidebarWidth?: number;
  mainContentWidth?: number;
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
}

const RenderCell = props => {
  const value = props.getValue();

  if (props.column && props.row && props.column.columnDef.checklistsVisible && props.column.columnDef.index === props.column.columnDef.firstVisibleIndex && props.row.original[CHECKLISTS_COLUMN]) {
    const [color, progress] = props.row.original[CHECKLISTS_COLUMN].split("|");

    return (
      <div className="centeredFlex overflow-hidden">
        <span className="text-truncate">
          {value}
        </span>

        <StaticProgress
          className="ml-0-5"
          color={color}
          value={parseFloat(progress) * 100}
          size={18}
        />
      </div>
    );
  }

  return value;
};

const ListRoot = React.memo<TableListProps>(({
   records,
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
   sidebarWidth,
   mainContentWidth
 }) => {
  const columns = useMemo(
    () => {
      let firstVisibleIndex;
      let checklistsVisible;
      let tagsVisible;

      records.columns.forEach((c, i) => {
        if (c.attribute === firstColumnName || (typeof firstVisibleIndex !== "number" && c.visible && ![COLUMN_WITH_COLORS, CHECKLISTS_COLUMN].includes(c.attribute))) {
          firstVisibleIndex = i;
        }
        if (typeof checklistsVisible !== "boolean" && c.attribute === CHECKLISTS_COLUMN) {
          checklistsVisible = c.visible;
        }
        if (typeof tagsVisible !== "boolean" && c.attribute === COLUMN_WITH_COLORS) {
          tagsVisible = c.visible;
        }
      });

      const result: ColumnDef<Record<any, any>>[] = records.columns.map((c, i) => ({
        index: i,
        id: c.attribute,
        visible: c.visible,
        size: c.width,
        type: c.type,
        cellClass: c.type === "Money" ? "money text-end justify-content-end" : null,
        minSize: COLUMN_MIN_WIDTH,
        enableSorting: c.sortable,
        complexAttribute: c.sortFields,
        disableVisibility: [primaryColumn, secondaryColumn].includes(c.attribute),
        accessorFn: row => row[`${c.attribute}`],
        header: () => <span className="text-truncate text-nowrap">{c.title}</span>,
        cell: RenderCell,
        firstVisibleIndex,
        checklistsVisible,
        tagsVisible
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

  const onChangeColumns = useCallback((columnsUpdated, attr, listUpdate) => {
    const columns = records.columns.map(c => {
      if (columnsUpdated[c.attribute]) {
        return {
          ...c,
          [attr]: columnsUpdated[c.attribute] || c[attr],
        };
      }
      return c;
    });

    console.log('!!!!', columnsUpdated, columns );

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
        columnsBase={columns}
        data={rows}
        sorting={sorting}
        onChangeColumns={onChangeColumns}
        onChangeColumnsOrder={onChangeColumnsOrder}
        threeColumn={threeColumn}
        onLoadMore={onLoadMore}
        recordsCount={records.filteredCount}
        onChangeModel={onChangeModel}
        onRowDoubleClick={onRowDoubleClick}
        onSelectionChange={onSelectionChange}
        selection={selection}
        getContainerNode={getContainerNode}
        sidebarWidth={sidebarWidth}
        mainContentWidth={mainContentWidth}
      />
    )
    : null;
});

export default ListRoot;