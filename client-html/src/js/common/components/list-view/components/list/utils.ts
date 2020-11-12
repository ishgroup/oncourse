/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { format } from "date-fns";
import { ColumnType, DataResponse } from "@api/model";
import { NestedTableColumnsTypes } from "../../../../../model/common/NestedTable";
import { EEE_D_MMM_YYYY, III_DD_MMM_YYYY, III_DD_MMM_YYYY_HH_MM } from "../../../../utils/dates/format";
import { formatCurrency, formatPercent } from "../../../../utils/numbers/numbersNormalizing";
import { CustomColumnFormats } from "../../../../../model/common/ListView";
import { AnyArgFunction } from "../../../../../model/common/CommonFunctions";

const formatBoolean = val => {
  switch (val) {
    case "true": {
      return "Yes";
    }
    case "false": {
      return "No";
    }
    default: {
      return val;
    }
  }
};

export const getTableRows = (records: DataResponse,
  shortCurrencySymbol: string,
  primaryColumn: string,
  secondaryColumn: string,
  primaryColumnCondition?: AnyArgFunction,
  secondaryColumnCondition?: AnyArgFunction,
  customColumnFormats?: CustomColumnFormats,
  setRowClasses?: AnyArgFunction) => {
  const visibleColumns = records.columns.filter(c => c.visible || c.system);
  let counter = 0;

  return records.rows.map(row => {
    const record = {} as any;
    record.id = row.id;

    row.values.forEach((v, i) => {
      // Check column with specific type and format value
      const visibleColumn = visibleColumns[i];
      const type = visibleColumn && visibleColumns[i].type;

      if (visibleColumn && customColumnFormats && customColumnFormats[visibleColumn.attribute]) {
        record[visibleColumn.attribute] = customColumnFormats[visibleColumns[i].attribute](v, row, records.columns);
        return;
      }

      switch (type) {
        case ColumnType.Date:
          record[visibleColumn.attribute] = v ? format(new Date(v), III_DD_MMM_YYYY) : null;
          break;
        case ColumnType.Datetime:
          record[visibleColumn.attribute] = v ? format(new Date(v), III_DD_MMM_YYYY_HH_MM) : null;
          break;
        case ColumnType.Money:
          record[visibleColumn.attribute] = v ? formatCurrency(Number(v), shortCurrencySymbol) : null;
          break;
        case ColumnType.Boolean:
          record[visibleColumn.attribute] = formatBoolean(v);
          break;
        case ColumnType.Percent:
          record[visibleColumn.attribute] = v ? formatPercent(v) : null;
          break;
        default:
          record[visibleColumn.attribute] = v;
      }
    });

    record.index = counter;
    counter++;

    if (setRowClasses) {
      record.customClasses = setRowClasses(record);
    }

    record.primary = primaryColumnCondition ? primaryColumnCondition(record) : record[primaryColumn];
    record.secondary = secondaryColumnCondition ? secondaryColumnCondition(record) : record[secondaryColumn];

    return record;
  });
};

export const getNestedTableCell = (value, type, currencySymbol) => {
  switch (type as NestedTableColumnsTypes) {
    case "date":
    case "date-time": {
      const dateFormat = type === "date" ? EEE_D_MMM_YYYY : III_DD_MMM_YYYY_HH_MM;
      return value === null ? "not set" : format(new Date(value), dateFormat);
    }

    case "currency": {
      return formatCurrency(Number(value), currencySymbol);
    }
    default: {
      return value;
    }
  }
};
