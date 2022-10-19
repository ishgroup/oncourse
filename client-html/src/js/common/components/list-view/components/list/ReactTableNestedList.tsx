/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, {
  useMemo, useState
} from "react";
import { useSelector } from "react-redux";
import {
  useTable,
  useFlexLayout,
  useRowSelect,
  useSortBy
} from "react-table";
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
import { AnyArgFunction } from "../../../../../model/common/CommonFunctions";
import { State } from "../../../../../reducers/state";
import { openInternalLink } from "../../../../utils/links";
import AddButton from "../../../icons/AddButton";
import StaticList from "./components/StaticList";
import styles from "./styles";
import { getNestedTableCell } from "./utils";
import { NESTED_TABLE_ROW_HEIGHT } from "../../../../../constants/Config";

const DEFAULT_COLUMN_WIDTH = 100;

const useStyles = makeStyles(styles);

const getRowId = row => row.id;

interface NestedListTableProps {
  columns: any;
  data: any;
  selection: any[];
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
  onRowDoubleClick,
  onCheckboxChange,
  calculateHeight
  }) => {
  const classes = useStyles();

  const selectedRowIdsObj = useMemo(() => (selection ? selection.reduce((p, c) => ({ ...p, [c]: true }), []) : []), [
    selection
  ]);

  const initialState = useMemo(() => {
    const sortColumn = columns.find(c => c.defaultSort);

    if (sortColumn) {
      return {
        sortBy: [{
          id: sortColumn.id,
          desc: false
        }]
      };
    }
    return [];
  }, [columns]);

  const {
    rows,
    prepareRow,
    headerGroups,
    getTableProps,
    totalColumnsWidth
  } = useTable(
    {
      columns,
      data,
      defaultColumn: {
        width: DEFAULT_COLUMN_WIDTH
      },
      initialState,
      getRowId,
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
    useFlexLayout
  );

  const onRowSelect = id => {
    onSelectionChangeHangler(selection.includes(id) ? [] : [id]);
  };

  const Header = useMemo(() => (
    <TableHead component="div" className={classes.header}>
      {headerGroups.map(headerGroup => (
        <TableRow key={headerGroup.getHeaderGroupProps().key} className={clsx(classes.headerRow, "w-100")} component="div">
          {headerGroup.headers.map(column => (
            <TableCell
              {...column.getHeaderProps()}
              className={clsx(classes.headerCell, column.cellClass)}
              component="div"
            >
              <TableSortLabel
                {...column.getSortByToggleProps()}
                hideSortIcon={!column.canSort}
                active={column.isSorted}
                direction={column.isSortedDesc ? "desc" : "asc"}
                classes={{
                    root: clsx(!column.canSort && classes.noSort)
                  }}
              >
                {column.render("Header")}
              </TableSortLabel>
            </TableCell>
            ))}
        </TableRow>
      ))}
    </TableHead>
  ), [headerGroups]);

  const List = useMemo(() => (rows.length ? (
    <StaticList
      prepareRow={prepareRow}
      rows={rows}
      classes={classes}
      totalColumnsWidth={totalColumnsWidth}
      onRowSelect={onRowSelect}
      onRowDoubleClick={onRowDoubleClick}
      onCheckboxChange={onCheckboxChange}
    />
  ) : (
    <div className="noRecordsMessage h-100">
      <Typography variant="h6" color="inherit" align="center">
        No data
      </Typography>
    </div>
  )), [rows, totalColumnsWidth, selection, onRowDoubleClick, onCheckboxChange, onRowSelect]);

  const tableHeight = useMemo(() => 100 + rows.length * NESTED_TABLE_ROW_HEIGHT,
    [rows.length]);

  const bodyStyle = useMemo(() => (rows.length > 10 ? calculateHeight && { height: NESTED_TABLE_ROW_HEIGHT * 10 } : { height: rows.length * NESTED_TABLE_ROW_HEIGHT }), [rows.length]);

  return (
    <>
      <MaUTable
        {...getTableProps()}
        className={clsx(classes.nestedTable, !bodyStyle && "flex-fill")}
        style={{ maxHeight: tableHeight }}
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
    onRowDoubleClick,
    onCheckboxChange,
    meta: { invalid, error },
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

  const columnsFormated = useMemo(
    () => columns.map(c => ({
      id: c.name,
      width: c.width || DEFAULT_COLUMN_WIDTH,
      Header: c.title,
      accessor: row => row[`${c.name}`],
      cellClass: c.type === "currency" ? "money text-end" : null,
      disableSortBy: c.disableSort || !sortable,
      ...c
    })),
    [columns, sortable]
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
                  <Launch fontSize="inherit" />
                </IconButton>
              )}
              {onAdd && (
                <AddButton size="small" onClick={onAdd} />
              )}
              {Boolean(rows.length) && removeEnabled && (
                <IconButton
                  size="small"
                  className="errorColor"
                  disabled={selection.length !== 1}
                  onClick={removeRow}
                >
                  <RemoveCircle color="inherit" fontSize="inherit" />
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
