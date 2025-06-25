/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { openInternalLink } from "ish-ui";
import * as React from "react";
import { FieldArray } from "redux-form";
import NestedTable from "../../../../common/components/list-view/components/list/ReactTableNestedList";
import { NestedTableColumn } from "../../../../model/common/NestedTable";

const invoiceColumns: NestedTableColumn[] = [
  {
    name: "invoiceType",
    title: "Type",
    width: 125
  },
  {
    name: "invoiceNumber",
    title: "Invoice number",
    width: 110
  },
  {
    name: "quoteNumber",
    title: "Quote number",
    width: 110
  },
  {
    name: "total",
    title: "Total (inc tax)",
    type: "currency"
  }
];

const LeadInvoiceTab = props => {
  const { isNew, values } = props;

  const quoteAddLink = () => openInternalLink(`/invoice/new?search=lead.id=${values.id}`);

  const openRow = value => openInternalLink(`/invoice/${value.id}`);

  return (
    <div className="pl-3 pr-3">
      <FieldArray
        name="invoices"
        goToLink={`/invoice?search=lead.id=${values.id}`}
        title={(values && values.invoices && values.invoices.length) === 1 ? "Quote/Invoice" : "Quotes/Invoices"}
        component={NestedTable}
        onAdd={isNew ? null : quoteAddLink}
        columns={invoiceColumns}
        onRowDoubleClick={openRow}
        rerenderOnEveryChange
        sortBy={(a, b) => b.invoiceNumber - a.invoiceNumber}
        calculateHeight
        primaryHeader
      />
    </div>
  );
};

export default LeadInvoiceTab;