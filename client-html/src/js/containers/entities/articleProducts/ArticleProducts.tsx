/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Account, ArticleProduct, Tax } from "@api/model";
import React, { useEffect, useState } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { initialize } from "redux-form";
import { checkPermissions, getUserPreferences } from "../../../common/actions";
import { notesAsyncValidate } from "../../../common/components/form/notes/utils";
import { clearListState, getFilters, setListEditRecord } from "../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";
import ListView from "../../../common/components/list-view/ListView";
import { getManualLink } from "../../../common/utils/getManualLink";
import { plainCorporatePassPath } from "../../../constants/Api";
import { ACCOUNT_DEFAULT_STUDENT_ENROLMENTS_ID } from "../../../constants/Config";
import { FilterGroup, FindRelatedItem } from "../../../model/common/ListView";
import { State } from "../../../reducers/state";
import { getDataCollectionRules, getEntityRelationTypes } from "../../preferences/actions";
import { getListTags } from "../../tags/actions";
import { getPlainAccounts } from "../accounts/actions";
import BulkEditCogwheelOption from "../common/components/BulkEditCogwheelOption";
import { getPlainTaxes } from "../taxes/actions";
import ArticleProductEditView from "./components/ArticleProductEditView";

interface ArticleProductsProps {
  onInit?: (initial: ArticleProduct) => void;
  onDelete?: (id: string) => void;
  getFilters?: () => void;
  getTags?: () => void;
  clearListState?: () => void;
  getAccounts?: () => void;
  getRelationTypes?: () => void;
  getTaxes?: () => void;
  checkPermissions?: () => void;
  getDefaultIncomeAccount?: () => void;
  getDataCollectionRules?: () => void;
  accounts?: Account[];
  taxes?: Tax[];
  preferences?: any;
  updatingAccounts?: boolean;
  updatingTaxes?: boolean;
}

const Initial: ArticleProduct = {
  tags: [],
  code: null,
  corporatePasses: [],
  description: null,
  feeExTax: 0,
  id: 0,
  incomeAccountId: null,
  relatedSellables: [],
  name: null,
  taxId: null,
  totalFee: 0,
  status: "Can be purchased in office & online"
};

const filterGroups: FilterGroup[] = [
  {
    title: "CORE FILTER",
    filters: [
      {
        name: "Active",
        expression: "isOnSale == true",
        active: true
      },
      {
        name: "Inactive",
        expression: "isOnSale == false",
        active: false
      }
    ]
  }
];

const findRelatedGroup: FindRelatedItem[] = [
  {
    title: "Audits",
    list: "audit",
    expression: "entityIdentifier == ArticleProduct and entityId"
  },
  { title: "Courses", list: "course", expression: "allRelatedProducts.id" },
  {
    title: "Contact purchased",
    list: "contact",
    expression: "productItems.status is ACTIVE AND productItems.product.id"
  },
  {
    title: "Invoices",
    list: "invoice",
    expression: "(invoiceLines.productItems.status  ==  ACTIVE) and invoiceLines.productItems.product.id"
  },
  { title: "Sales", list: "sale", expression: "type is ARTICLE AND product.id" },
];

const manualLink = getManualLink("navigating-around-the-product-window");

const preformatBeforeSubmit = (value: ArticleProduct): ArticleProduct => {
  if (value.relatedSellables.length) {
    value.relatedSellables.forEach((s: any) => {
      if (s.tempId) {
        s.id = null;
        delete s.tempId;
      }
    });
  }

  return value;
};

const setRowClasses = ({ isOnSale }) => {
  if (isOnSale === "No") return "text-op05";
  return undefined;
};

const ArticleProducts: React.FC<ArticleProductsProps> = props => {
  const [initNew, setInitNew] = useState(false);

  const {
    onInit,
    getFilters,
    getDefaultIncomeAccount,
    getTaxes,
    getAccounts,
    accounts,
    taxes,
    preferences,
    updatingAccounts,
    updatingTaxes,
    checkPermissions,
    getRelationTypes,
    getDataCollectionRules,
    getTags
  } = props;

  useEffect(() => {
    if (initNew && taxes && accounts && preferences && preferences[ACCOUNT_DEFAULT_STUDENT_ENROLMENTS_ID]) {
      setInitNew(false);
      const defaultId = preferences[ACCOUNT_DEFAULT_STUDENT_ENROLMENTS_ID];
      const account = accounts.find(item => item.id === Number(defaultId));
      if (account) {
        onInit({ ...Initial, incomeAccountId: account.id, taxId: Number(account["tax.id"]) });
      } else {
        onInit(Initial);
      }
    }
  }, [initNew, updatingAccounts, updatingTaxes, preferences]);

  useEffect(() => {
    getDefaultIncomeAccount();
    getAccounts();
    getTaxes();
    getFilters();
    getTags();
    checkPermissions();
    getRelationTypes();
    getDataCollectionRules();
    return () => {
      clearListState();
    };
  }, []);

  return (
    <ListView
      listProps={{
        setRowClasses,
        primaryColumn: "name",
        secondaryColumn: "sku"
      }}
      editViewProps={{
        manualLink,
        asyncValidate: notesAsyncValidate,
        asyncChangeFields: ["notes[].message"],
        hideTitle: true
      }}
      EditViewContent={ArticleProductEditView}
      CogwheelAdornment={BulkEditCogwheelOption}
      rootEntity="ArticleProduct"
      onInit={() => setInitNew(true)}
      findRelated={findRelatedGroup}
      filterGroupsInitial={filterGroups}
      preformatBeforeSubmit={preformatBeforeSubmit}
      defaultDeleteDisabled
      noListTags
    />
  );
};

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: (initial: ArticleProduct) => {
    dispatch(setListEditRecord(initial));
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, initial));
  },
  getDefaultIncomeAccount: () => dispatch(getUserPreferences([ACCOUNT_DEFAULT_STUDENT_ENROLMENTS_ID])),
  getTaxes: () => dispatch(getPlainTaxes()),
  getAccounts: () => getPlainAccounts(dispatch, "income"),
  getTags: () => dispatch(getListTags("ArticleProduct")),
  getFilters: () => dispatch(getFilters("ArticleProduct")),
  clearListState: () => dispatch(clearListState()),
  checkPermissions: () => dispatch(checkPermissions({ path: plainCorporatePassPath, method: "GET" })),
  getRelationTypes: () => dispatch(getEntityRelationTypes()),
  getDataCollectionRules: () => dispatch(getDataCollectionRules()),
});

const mapStateToProps = (state: State) => ({
  updatingTaxes: state.taxes.updatingItems,
  taxes: state.taxes.items,
  updatingAccounts: state.plainSearchRecords.Account.loading,
  accounts: state.plainSearchRecords.Account.items,
  preferences: state.userPreferences
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(ArticleProducts);