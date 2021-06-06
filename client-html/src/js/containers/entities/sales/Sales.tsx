/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect } from "react";
import { connect } from "react-redux";
import { ProductItem, TableModel } from "@api/model";
import { clearListState, getFilters } from "../../../common/components/list-view/actions";
import SendMessageEditView from "../messages/components/SendMessageEditView";
import { getSale, updateSale } from "./actions";
import ListView from "../../../common/components/list-view/ListView";
import { FilterGroup } from "../../../model/common/ListView";
import SalesEditView from "./components/SalesEditView";
import { getManualLink } from "../../../common/utils/getManualLink";
import SalesCogwheel from "./components/cogwheel/SalesCogwheel";
import { getPlainAccounts } from "../accounts/actions";
import { getPlainTaxes } from "../taxes/actions";
import { Dispatch } from "redux";

interface SalesProps {
  getSaleRecord?: () => void;
  onInit?: () => void;
  getFilters?: () => void;
  getTaxes?: () => void;
  getAccounts?: () => void;
  clearListState?: () => void;
  updateTableModel?: (model: TableModel, listUpdate?: boolean) => void;
  onSave?: (id: string, productItem: ProductItem) => void;
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

const findRelatedGroup: any[] = [
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

const nestedEditFields = {
  SendMessage: props => <SendMessageEditView {...props} />
};

const Sales: React.FC<SalesProps> = props => {
  const {
    updateTableModel,
    getSaleRecord,
    onInit,
    getFilters,
    getAccounts,
    getTaxes,
    onSave
  } = props;

  useEffect(() => {
    getFilters();
    getAccounts();
    getTaxes();
    return () => {
      clearListState();
    };
  }, []);

  return (
    <div>
      <ListView
        listProps={{
          primaryColumn: "product.name",
          secondaryColumn: "invoiceLine.invoice.contact.fullName"
        }}
        editViewProps={{
          manualLink,
          nameCondition: values => (values ? values.productName : "")
        }}
        nestedEditFields={nestedEditFields}
        updateTableModel={updateTableModel}
        EditViewContent={SalesEditView}
        CogwheelAdornment={SalesCogwheel}
        getEditRecord={getSaleRecord}
        rootEntity="ProductItem"
        onInit={onInit}
        onSave={onSave}
        findRelated={findRelatedGroup}
        filterGroupsInitial={filterGroups}
        defaultDeleteDisabled
        noListTags
      />
    </div>
  );
};

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: () => {},
  getAccounts: () => getPlainAccounts(dispatch),
  getTaxes: () => dispatch(getPlainTaxes()),
  getFilters: () => dispatch(getFilters("ProductItem")),
  onSave: (id: string, productItem: ProductItem) => dispatch(updateSale(id, productItem)),
  clearListState: () => dispatch(clearListState()),
  getSaleRecord: (id: string) => dispatch(getSale(id))
});

export default connect<any, any, any>(null, mapDispatchToProps)(Sales);
