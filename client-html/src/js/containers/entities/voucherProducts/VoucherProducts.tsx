/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect } from "react";
import { connect } from "react-redux";
import { initialize } from "redux-form";
import { Account, TableModel, VoucherProduct } from "@api/model";
import { Dispatch } from "redux";
import { clearListState, getFilters, setListEditRecord } from "../../../common/components/list-view/actions";
import { plainCorporatePassPath } from "../../../constants/Api";
import { getVoucherProduct, updateVoucherProduct } from "./actions";
import ListView from "../../../common/components/list-view/ListView";
import VoucherProductEditView from "./components/VoucherProductEditView";
import { FilterGroup } from "../../../model/common/ListView";
import { State } from "../../../reducers/state";
import { getPlainAccounts } from "../accounts/actions";
import { getManualLink } from "../../../common/utils/getManualLink";
import { checkPermissions, getUserPreferences } from "../../../common/actions";
import {
  ACCOUNT_DEFAULT_VOUCHER_LIABILITY_ID,
  ACCOUNT_DEFAULT_VOUCHER_UNDERPAYMENT_ID
} from "../../../constants/Config";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";
import { getEntityTags, getListTags } from "../../tags/actions";
import { getDataCollectionRules, getEntityRelationTypes } from "../../preferences/actions";
import { notesAsyncValidate } from "../../../common/components/form/notes/utils";
import BulkEditCogwheelOption from "../common/components/BulkEditCogwheelOption";

interface VoucherProductsProps {
  getVoucherProductRecord?: () => void;
  getTagsForClassesSearch?: () => void;
  onInit?: (initial: VoucherProduct) => void;
  onSave?: (id: string, voucherProduct: VoucherProduct) => void;
  getFilters?: () => void;
  getRelationTypes?: () => void;
  getTags?: () => void;
  clearListState?: () => void;
  updateTableModel?: (model: TableModel, listUpdate?: boolean) => void;
  getDefaultAccounts?: () => void;
  getAccounts?: () => void;
  getDataCollectionRules?: () => void;
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
      },
    ]
  }
];

const findRelatedGroup: any[] = [
  { title: "Audits", list: "audit", expression: "entityIdentifier == VoucherProduct and entityId" },
  { title: "Contacts purchased", list: "contact", expression: "invoices.invoiceLines.productItems.product.id" },
  { title: "Courses", list: "course", expression: "voucherProductCourses.voucherProduct.id" },
  { title: "Sales", list: "sale", expression: "type is VOUCHER AND product.id" },
  { title: "Vouchers", list: "sale", expression: "product.id" },
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
    onSave,
    getFilters,
    clearListState,
    getDefaultAccounts,
    getTagsForClassesSearch,
    getAccounts,
    preferences,
    accounts,
    checkPermissions,
    getRelationTypes,
    getDataCollectionRules,
    getTags
  } = props;

  const onInitCustom = () => {
    if (accounts && preferences) {
      const voucherLiability = accounts.find(item => item.id === Number(preferences[ACCOUNT_DEFAULT_VOUCHER_LIABILITY_ID]));
      const voucherUnderpayment = accounts.find(item => item.id === Number(preferences[ACCOUNT_DEFAULT_VOUCHER_UNDERPAYMENT_ID]));
      onInit({ ...Initial, liabilityAccountId: voucherLiability?.id, underpaymentAccountId: voucherUnderpayment?.id });
    } else {
      onInit(Initial);
    }
  };

  useEffect(() => {
    getDefaultAccounts();
    getAccounts();
    getFilters();
    getTags();
    getTagsForClassesSearch();
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
          primaryColumn: "name",
          secondaryColumn: "sku"
        }}
      editViewProps={{
          manualLink,
          asyncValidate: notesAsyncValidate,
          asyncBlurFields: ["notes[].message"],
          hideTitle: true
        }}
      EditViewContent={VoucherProductEditView}
      CogwheelAdornment={BulkEditCogwheelOption}
      getEditRecord={getVoucherProductRecord}
      rootEntity="VoucherProduct"
      onInit={onInitCustom}
      onSave={onSave}
      findRelated={findRelatedGroup}
      filterGroupsInitial={filterGroups}
      preformatBeforeSubmit={preformatBeforeSubmit}
      defaultDeleteDisabled
      noListTags
    />
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
  getTags: () => dispatch(getListTags("VoucherProduct")),
  getFilters: () => dispatch(getFilters("VoucherProduct")),
  getAccounts: () => getPlainAccounts(dispatch),
  getDefaultAccounts: () => {
    dispatch(getUserPreferences([ACCOUNT_DEFAULT_VOUCHER_LIABILITY_ID]));
    dispatch(getUserPreferences([ACCOUNT_DEFAULT_VOUCHER_UNDERPAYMENT_ID]));
  },
  clearListState: () => dispatch(clearListState()),
  getVoucherProductRecord: (id: string) => dispatch(getVoucherProduct(id)),
  onSave: (id: string, voucherProduct: VoucherProduct) => dispatch(updateVoucherProduct(id, voucherProduct)),
  checkPermissions: () => dispatch(checkPermissions({ path: plainCorporatePassPath, method: "GET" })),
  getRelationTypes: () => dispatch(getEntityRelationTypes()),
  getDataCollectionRules: () => dispatch(getDataCollectionRules()),
});

const mapStateToProps = (state: State) => ({
  accounts: state.plainSearchRecords.Account.items,
  preferences: state.userPreferences
});

export default connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps
)(VoucherProducts);
