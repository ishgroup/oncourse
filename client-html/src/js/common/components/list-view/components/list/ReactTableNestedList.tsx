/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect, useMemo, useState } from "react";
import { useSelector } from "react-redux";
import {
  ColumnDef,
  ColumnSort,
  flexRender,
  getCoreRowModel,
  getSortedRowModel,
  useReactTable
} from '@tanstack/react-table';
import makeStyles from "@mui/styles/makeStyles";
import MaUTable from "@mui/material/Table";
import TableCell from "@mui/material/TableCell";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TableSortLabel from "@mui/material/TableSortLabel";
import Typography from "@mui/material/Typography";
import IconButton from "@mui/material/IconButton";
import Launch from "@mui/icons-material/Launch";
import RemoveCircle from "@mui/icons-material/RemoveCircle";
import clsx from "clsx";
import { NestedTableColumn } from "../../../../../model/common/NestedTable";
import { AnyArgFunction } from  "ish-ui";
import { State } from "../../../../../reducers/state";
import { openInternalLink } from "ish-ui";
import StaticList from "./components/StaticList";
import styles from "./styles";
import { getNestedTableCell } from "./utils";
import { NESTED_TABLE_ROW_HEIGHT } from "../../../../../constants/Config";
import { AddButton } from "ish-ui";

const DEFAULT_COLUMN_WIDTH = 100;

const useStyles = makeStyles(styles);

const getRowId = row => row.id;

interface NestedListTableProps {
  columns: any;
  data: any;
  selection: string[];
  onRowDelete?: any;
  onRowDoubleClick?: any;
  onCheckboxChange?: any;
  onSelectionChangeHangler?: any;
  calculateHeight?: boolean;
}

const Table: React.FC<NestedListTableProps> = ({
                                                 columns,
                                                 data,
                                                 selection,
                                                 onSelectionChangeHangler,
                                                 onRowDelete,
                                                 onRowDoubleClick,
                                                 onCheckboxChange,
                                                 calculateHeight
                                               }) => {

  const [sorting, onSortingChange] = useState<ColumnSort[]>([]);

  const classes = useStyles();

  useEffect(() => {
    const sortInitial = [];
    const sortColumn = columns.find(c => c.defaultSort);
    if (sortColumn) {
      sortInitial.push({
        id: sortColumn.id,
        desc: false
      });
    }
    onSortingChange(sortInitial);
  }, [columns]);

  const table = useReactTable({
    data,
    columns,
    onRowSelectionChange: onSelectionChangeHangler,
    onSortingChange,
    state: {
      sorting,
      rowSelection: selection.reduce((p, c) => {
        p[c] = true;
        return p;
      }, {}),
    },
    enableRowSelection: true,
    enableMultiRowSelection: true,
    getSortedRowModel: getSortedRowModel(),
    getCoreRowModel: getCoreRowModel(),
    getRowId
  });

  const onRowSelect = id => {
    onSelectionChangeHangler(selection.includes(id) ? [] : [id]);
  };

  const Header = useMemo(() => (
    <TableHead component="div" className={classes.header}>
      {table.getHeaderGroups().map(headerGroup => (
        <TableRow key={headerGroup.id} className={clsx(classes.headerRow, "w-100")} component="div">
          {headerGroup.headers.map(({column, getContext}) => {
            const columnDef: any = column.columnDef;
            const canSort = column.getCanSort();
            const direction: any = column.getIsSorted();

            return <TableCell
              key={columnDef.id}
              style={{
                minWidth: '0px',
                boxSizing: "border-box",
                flex: `${column.getSize()} 0 auto`,
                width: `${column.getSize()}px`
              }}
              className={clsx(
                classes.headerCell,
                columnDef.cellClass,
                {
                  [classes.rightAlighed]: columnDef.type === "currency",
                  [classes.activeRight]: columnDef.type === "currency" && column.getIsSorted()
                }
              )}
              component="div"
            >
              {columnDef.header && (<TableSortLabel
                hideSortIcon={!canSort}
                active={Boolean(direction)}
                direction={direction || "asc"}
                classes={{
                  root: clsx(
                    canSort ? classes.canSort : classes.noSort,
                    "overflow-hidden"
                  ),
                  icon: columnDef.type === "currency" && canSort && classes.rightSort
                }}
                onClick={canSort
                  ? column.getToggleSortingHandler()
                  : null
                }
              >
                {flexRender(columnDef.header, getContext())}
              </TableSortLabel>)}
            </TableCell>;
          })}
        </TableRow>
      ))}
    </TableHead>
  ), [sorting]);

  const rows = table.getRowModel().rows;

  const List = useMemo(() => (rows.length ? (
    <StaticList
      rows={rows}
      classes={classes}
      totalColumnsWidth={table.getCenterTotalSize()}
      onRowSelect={onRowSelect}
      onRowDelete={onRowDelete}
      onRowDoubleClick={onRowDoubleClick}
      onCheckboxChange={onCheckboxChange}
    />
  ) : (
    <div className="noRecordsMessage h-100">
      <Typography variant="h6" color="inherit" align="center">
        No data
      </Typography>
    </div>
  )), [rows, selection, onRowDoubleClick, onCheckboxChange, onRowSelect]);

  const tableHeight = useMemo(() => 100 + rows.length * NESTED_TABLE_ROW_HEIGHT,
    [rows.length]);

  const bodyStyle = useMemo(() => (rows.length > 10 ? calculateHeight && {height: NESTED_TABLE_ROW_HEIGHT * 10} : {height: rows.length * NESTED_TABLE_ROW_HEIGHT}), [rows.length]);

  return (
    <>
      <MaUTable
        className={clsx(classes.nestedTable, !bodyStyle && "flex-fill")}
        style={{maxHeight: tableHeight}}
        component="div"
      >
        {Header}
        <div style={bodyStyle} className={classes.tableBody}>
          {List}
        </div>
      </MaUTable>
    </>
  );
};

export interface NestedListProps {
  columns: NestedTableColumn[];
  idPath?: string;
  removeEnabled?: boolean;
  sortable?: boolean;
  sortBy?: any;
  fields?: any;
  title?: string;
  name?: string;
  className?: string;
  hideHeader?: boolean;
  calculateHeight?: boolean;
  onAdd?: any;
  currencySymbol?: string;
  onRowDoubleClick?: any;
  onRowDelete?: any;
  onCheckboxChange?: AnyArgFunction;
  meta?: any;
  total?: any;
  goToLink?: string;
  primaryHeader?: boolean;
}

const ListRoot = React.memo<NestedListProps>(({
                                                columns,
                                                removeEnabled,
                                                sortable,
                                                sortBy,
                                                fields,
                                                title,
                                                className,
                                                hideHeader,
                                                onAdd,
                                                onRowDelete,
                                                onRowDoubleClick,
                                                onCheckboxChange,
                                                meta: {invalid, error},
                                                total,
                                                goToLink,
                                                calculateHeight,
                                                primaryHeader
                                              }) => {
  const [selection, setSelection] = useState([]);

  const removeRow = () => {
    fields.remove(fields.getAll().findIndex(f => f.id === selection[0]));
    setSelection([]);
  };

  const currencySymbol = useSelector<State, any>(state => state.currency && state.currency.shortCurrencySymbol);

  const columnsFormated = useMemo<ColumnDef<Record<any, any>>[]>(
    () => columns.concat(onRowDelete ? [{
      name: "delete",
      type: "delete",
      cellClass: "p-0 text-center",
      width: 10
    }] : [])
      .map(c => ({
        id: c.name,
        size: c.width || DEFAULT_COLUMN_WIDTH,
        minSize: c.width || DEFAULT_COLUMN_WIDTH,
        header: c.title,
        accessorFn: row => row[`${c.name}`],
        cellClass: c.type === "currency" ? "money text-end justify-content-end" : null,
        enableSorting: !c.disableSort || sortable,
        ...c
      })),
    [columns, sortable, onRowDelete]
  );

  const rows = useMemo(() => {
    if (!fields.length) return [];
    const allFields = fields.getAll();

    if (typeof sortBy === "function") {
      allFields.sort(sortBy);
    }

    return allFields.map((v, index) => {
      const row: any = {};
      row.id = index.toString();
      row.index = index;
      columns.forEach(c => {
        row[c.name] = getNestedTableCell(v[c.name], c.type, currencySymbol);
      });
      row.initial = v;
      row.fieldName = `${fields.name}[${index}]`;
      return row;
    });
  }, [fields, columns, sortBy]);

  return columns.length
    ? (
      <div className={clsx("flex-fill flex-column", className)}>
        {!hideHeader && (
          <div>
            <div className="centeredFlex">
              <Typography className={`${primaryHeader ? "heading" : "secondaryHeading"} pt-1 pb-1`}>
                {rows.length}
                {" "}
                {title}
              </Typography>
              {goToLink && (
                <IconButton
                  color="primary"
                  size="small"
                  onClick={() => openInternalLink(goToLink)}
                >
                  <Launch fontSize="inherit"/>
                </IconButton>
              )}
              {onAdd && (
                <AddButton size="small" onClick={onAdd}/>
              )}
              {Boolean(rows.length) && removeEnabled && (
                <IconButton
                  size="small"
                  className="errorColor"
                  disabled={selection.length !== 1}
                  onClick={removeRow}
                >
                  <RemoveCircle color="inherit" fontSize="inherit"/>
                </IconButton>
              )}
            </div>
          </div>
        )}
        {Boolean(rows.length) && (
          <Table
            columns={columnsFormated}
            data={rows}
            selection={selection}
            onRowDelete={onRowDelete}
            onRowDoubleClick={onRowDoubleClick}
            onCheckboxChange={onCheckboxChange}
            onSelectionChangeHangler={setSelection}
            calculateHeight={calculateHeight}
          />
        )}
        {invalid && (
          <Typography variant="subtitle2" color="error" component="div" className="pt-1">
            {error}
          </Typography>
        )}
        {total && (
          <div className="pt-1 pb-1">
            <div className="centeredFlex justify-content-end">
              <div>
                <Typography variant="subtitle2" noWrap>
                  Total
                </Typography>
              </div>

              <Typography
                variant="body2"
                color="textSecondary"
                className="centeredFlex pl-1 money pr-0-5"
              >
                {total}
              </Typography>
            </div>
          </div>
        )}
      </div>
    )
    : null;
});

export default ListRoot;