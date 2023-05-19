/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useEffect } from "react";
import { connect } from "react-redux";
import { ProductItem, ProductItemStatus } from "@api/model";
import { clearListState, getFilters } from "../../../common/components/list-view/actions";
import { getSalesManuTags } from "./actions";
import ListView from "../../../common/components/list-view/ListView";
import { FilterGroup, FindRelatedItem } from "../../../model/common/ListView";
import SalesEditView from "./components/SalesEditView";
import { getManualLink } from "../../../common/utils/getManualLink";
import SalesCogwheel from "./components/cogwheel/SalesCogwheel";
import { getPlainAccounts } from "../accounts/actions";
import { getPlainTaxes } from "../taxes/actions";
import { Dispatch } from "redux";
import { getEntityTags } from "../../tags/actions";
import { notesAsyncValidate } from "../../../common/components/form/notes/utils";

interface SalesProps {
  getSaleRecord?: () => void;
  onInit?: () => void;
  getFilters?: () => void;
  getTaxes?: () => void;
  getTags?: () => void;
  getAccounts?: () => void;
  clearListState?: () => void;
}

const filterGroups: FilterGroup[] = [
  {
    title: "CORE FILTER",
    filters: [
      {
        name: "Product",
        expression: "(type is ARTICLE)",
        active: true
      },
      {
        name: "Membership",
        expression: "(type is MEMBERSHIP)",
        active: true
      },
      {
        name: "Voucher",
        expression: "(type is VOUCHER)",
        active: true
      }
    ]
  },
  {
    title: "STATUS",
    filters: [
      {
        name: "Active",
        expression: "(status == ACTIVE and ((type is ARTICLE or type is VOUCHER) or expiryDate after today))",
        active: true
      },
      {
        name: "Cancelled",
        expression: "(status in (CREDITED, CANCELLED))",
        active: false
      },
      {
        name: "Expired",
        expression:
          "(status == EXPIRED or (expiryDate before today and status == ACTIVE or status == NEW and type is MEMBERSHIP))",
        active: false
      },
      {
        name: "Delivered (Products)",
        expression: "(status == DELIVERED)",
        active: false
      },
      {
        name: "Redeemed (Vouchers)",
        expression: "(status == REDEEMED)",
        active: false
      }
    ]
  }
];

const findRelatedGroup: FindRelatedItem[] = [
  {
    title: "Audits",
    list: "audit",
    expression:
      "(entityIdentifier = Membership || entityIdentifier = Voucher || entityIdentifier = Article) AND entityId"
  },
  { title: "Product", list: "product", expression: "productItems.id" },
  { title: "Membership", list: "membership", expression: "productItems.id" },
  { title: "Voucher", list: "voucher", expression: "productItems.id" },
  { title: "Purchased by contact", list: "contact", expression: "invoices.invoiceLines.productItems.id" },
  { title: "Invoice", list: "invoice", expression: "invoiceLines.productItems.id" }
];

const manualLink = getManualLink("sales");

const setRowClasses = ({ displayStatus }: { displayStatus: ProductItemStatus }) => {
  if (['Credited', 'Redeemed', 'Delivered'].includes(displayStatus)) return "text-op065";
  if (['Cancelled', 'Expired'].includes(displayStatus)) return "text-op05";
  return undefined;
};

const Sales: React.FC<SalesProps> = props => {
  const {
    getFilters,
    getAccounts,
    getTaxes,
    getTags
  } = props;

  useEffect(() => {
    getFilters();
    getTags();
    getAccounts();
    getTaxes();
    return () => {
      clearListState();
    };
  }, []);

  return (
    <ListView
      listProps={{
        setRowClasses,
        primaryColumn: "product.name",
        secondaryColumn: "invoiceLine.invoice.contact.fullName"
      }}
      editViewProps={{
        manualLink,
        asyncValidate: notesAsyncValidate,
        asyncChangeFields: ["notes[].message"],
        nameCondition: values => (values ? values.productName : "")
      }}
      EditViewContent={SalesEditView}
      CogwheelAdornment={SalesCogwheel}
      rootEntity="ProductItem"
      findRelated={findRelatedGroup}
      filterGroupsInitial={filterGroups}
      createButtonDisabled
      defaultDeleteDisabled
      noListTags
    />
  );
};

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getAccounts: () => getPlainAccounts(dispatch),
  getTaxes: () => dispatch(getPlainTaxes()),
  getTags: () => {
    dispatch(getSalesManuTags());
    dispatch(getEntityTags("Article"));
    dispatch(getEntityTags("Voucher"));
    dispatch(getEntityTags("Membership"));
  },
  getFilters: () => dispatch(getFilters("ProductItem")),
  clearListState: () => dispatch(clearListState())
});

export default connect<any, any, any>(null, mapDispatchToProps)(Sales);