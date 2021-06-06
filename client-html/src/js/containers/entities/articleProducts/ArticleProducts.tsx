/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { connect } from "react-redux";
import React, { useEffect, useState } from "react";
import { initialize } from "redux-form";
import { Account, ArticleProduct, Tax } from "@api/model";
import { Dispatch } from "redux";
import ListView from "../../../common/components/list-view/ListView";
import { plainCorporatePassPath } from "../../../constants/Api";
import ArticleProductEditView from "./components/ArticleProductEditView";
import { FilterGroup } from "../../../model/common/ListView";
import { clearListState, getFilters, setListEditRecord } from "../../../common/components/list-view/actions";
import { createArticleProduct, getArticleProduct, updateArticleProduct } from "./actions";
import { getManualLink } from "../../../common/utils/getManualLink";
import { State } from "../../../reducers/state";
import { getPlainTaxes } from "../taxes/actions";
import { getPlainAccounts } from "../accounts/actions";
import { ACCOUNT_DEFAULT_STUDENT_ENROLMENTS_ID } from "../../../constants/Config";
import { checkPermissions, getUserPreferences } from "../../../common/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";
import { getDataCollectionRules, getEntityRelationTypes } from "../../preferences/actions";

interface ArticleProductsProps {
  getArticleProductRecord?: () => void;
  onInit?: (initial: ArticleProduct) => void;
  onCreate?: (articleProduct: ArticleProduct) => void;
  onDelete?: (id: string) => void;
  onSave?: (id: string, articleProduct: ArticleProduct) => void;
  getFilters?: () => void;
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
  code: null,
  corporatePasses: [],
  description: null,
  feeExTax: null,
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

const findRelatedGroup: any[] = [
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
  }
];

const manualLink = getManualLink("product");

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

const ArticleProducts: React.FC<ArticleProductsProps> = props => {
  const [initNew, setInitNew] = useState(false);

  const {
    getArticleProductRecord,
    onInit,
    onCreate,
    onSave,
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
    getDataCollectionRules
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
    checkPermissions();
    getRelationTypes();
    getDataCollectionRules();
    return () => {
      clearListState();
    };
  }, []);

  return (
    <div>
      <ListView
        listProps={{
          primaryColumn: "name",
          secondaryColumn: "sku"
        }}
        editViewProps={{
          manualLink
        }}
        EditViewContent={ArticleProductEditView}
        getEditRecord={getArticleProductRecord}
        rootEntity="ArticleProduct"
        aqlEntity="Product"
        onInit={() => setInitNew(true)}
        onCreate={onCreate}
        onSave={onSave}
        findRelated={findRelatedGroup}
        filterGroupsInitial={filterGroups}
        preformatBeforeSubmit={preformatBeforeSubmit}
        defaultDeleteDisabled
        noListTags
      />
    </div>
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
  getFilters: () => dispatch(getFilters("ArticleProduct")),
  clearListState: () => dispatch(clearListState()),
  getArticleProductRecord: (id: string) => dispatch(getArticleProduct(id)),
  onSave: (id: string, articleProduct: ArticleProduct) => dispatch(updateArticleProduct(id, articleProduct)),
  onCreate: (articleProduct: ArticleProduct) => dispatch(createArticleProduct(articleProduct)),
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
