/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { Dispatch, useEffect, useState } from "react";
import { connect } from "react-redux";
import { initialize } from "redux-form";
import { Account, TableModel, VoucherProduct } from "@api/model";
import {
  setListEditRecord,
  getFilters,
  clearListState
} from "../../../common/components/list-view/actions";
import { plainCorporatePassPath } from "../../../constants/Api";
import { getVoucherProduct, updateVoucherProduct, createVoucherProduct } from "./actions";
import ListView from "../../../common/components/list-view/ListView";
import VoucherProductEditView from "./components/VoucherProductEditView";
import { FilterGroup } from "../../../model/common/ListView";
import { State } from "../../../reducers/state";
import { getLiabilityAccounts } from "../accounts/actions";
import { getManualLink } from "../../../common/utils/getManualLink";
import { checkPermissions, getUserPreferences } from "../../../common/actions";
import { ACCOUNT_DEFAULT_VOUCHER_LIABILITY_ID } from "../../../constants/Config";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";
import { getEntityTags } from "../../tags/actions";
import { getDataCollectionRules, getEntityRelationTypes } from "../../preferences/actions";

interface VoucherProductsProps {
  getVoucherProductRecord?: () => void;
  getTagsForClassesSearch?: () => void;
  onInit?: (initial: VoucherProduct) => void;
  onCreate?: (voucherProduct: VoucherProduct) => void;
  onSave?: (id: string, voucherProduct: VoucherProduct) => void;
  getFilters?: () => void;
  getRelationTypes?: () => void;
  clearListState?: () => void;
  updateTableModel?: (model: TableModel, listUpdate?: boolean) => void;
  getDefaultLiabilityAccount?: () => void;
  getAccounts?: () => void;
  getDataCollectionRules?: () => void;
  updatingAccounts?: boolean;
  accounts?: Account[];
  preferences?: any;
  checkPermissions?: () => void;
}

const Initial: VoucherProduct = {
  code: null,
  corporatePasses: [],
  courses: [],
  description: null,
  expiryDays: 365,
  feeExTax: 0,
  liabilityAccountId: null,
  maxCoursesRedemption: null,
  relatedSellables: [],
  name: null,
  status: "Can be purchased in office & online",
  value: 0
};

const manualLink = getManualLink("vouchers");

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
  { title: "Audits", list: "audit", expression: "entityIdentifier == VoucherProduct and entityId" },
  { title: "Contacts purchased", list: "contact", expression: "invoices.invoiceLines.productItems.product.id" },
  { title: "Courses", list: "course", expression: "voucherProductCourses.voucherProduct.id" },
  { title: "Vouchers", list: "sale", expression: "product.id" }
];

const preformatBeforeSubmit = (value: VoucherProduct): VoucherProduct => {
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

const VoucherProducts: React.FC<VoucherProductsProps> = props => {
  const {
    getVoucherProductRecord,
    onInit,
    onCreate,
    onSave,
    getFilters,
    clearListState,
    getDefaultLiabilityAccount,
    getTagsForClassesSearch,
    getAccounts,
    updatingAccounts,
    preferences,
    accounts,
    checkPermissions,
    getRelationTypes,
    getDataCollectionRules
  } = props;

  const [initNew, setInitNew] = useState(false);

  useEffect(() => {
    if (initNew && accounts && preferences && preferences[ACCOUNT_DEFAULT_VOUCHER_LIABILITY_ID]) {
      setInitNew(false);
      const defaultId = preferences[ACCOUNT_DEFAULT_VOUCHER_LIABILITY_ID];
      const account = accounts.find(item => item.id === Number(defaultId));
      if (account) {
        onInit({ ...Initial, liabilityAccountId: account.id });
      } else {
        onInit(Initial);
      }
    }
  }, [initNew, updatingAccounts, preferences]);

  useEffect(() => {
    getDefaultLiabilityAccount();
    getAccounts();
    getFilters();
    getTagsForClassesSearch();
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
        EditViewContent={VoucherProductEditView}
        getEditRecord={getVoucherProductRecord}
        rootEntity="VoucherProduct"
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
  onInit: (initial: VoucherProduct) => {
    dispatch(setListEditRecord(initial));
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, initial));
  },
  getTagsForClassesSearch: () => {
    dispatch(getEntityTags("Course"));
  },
  getFilters: () => dispatch(getFilters("VoucherProduct")),
  getAccounts: () => dispatch(getLiabilityAccounts()),
  getDefaultLiabilityAccount: () => dispatch(getUserPreferences([ACCOUNT_DEFAULT_VOUCHER_LIABILITY_ID])),
  clearListState: () => dispatch(clearListState()),
  getVoucherProductRecord: (id: string) => dispatch(getVoucherProduct(id)),
  onSave: (id: string, voucherProduct: VoucherProduct) => dispatch(updateVoucherProduct(id, voucherProduct)),
  onCreate: (voucherProduct: VoucherProduct) => dispatch(createVoucherProduct(voucherProduct)),
  checkPermissions: () => dispatch(checkPermissions({ path: plainCorporatePassPath, method: "GET" })),
  getRelationTypes: () => dispatch(getEntityRelationTypes()),
  getDataCollectionRules: () => dispatch(getDataCollectionRules()),
});

const mapStateToProps = (state: State) => ({
  updatingAccounts: state.accounts.updatingLiabilityItems,
  accounts: state.accounts.liabilityItems,
  preferences: state.userPreferences
});

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(VoucherProducts);
