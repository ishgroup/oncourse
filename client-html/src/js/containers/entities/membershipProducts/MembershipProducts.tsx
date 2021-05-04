/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { Dispatch, useEffect, useState } from "react";
import { connect } from "react-redux";
import { initialize } from "redux-form";
import { Account, MembershipProduct, Tax } from "@api/model";
import {
  clearListState,
  getFilters,
  setListEditRecord,
 } from "../../../common/components/list-view/actions";
import { plainContactRelationTypePath, plainCorporatePassPath } from "../../../constants/Api";
import {
  createMembershipProduct,
  getMembershipProduct,
  updateMembershipProduct
} from "./actions";
import ListView from "../../../common/components/list-view/ListView";
import MembershipProductEditView from "./components/MembershipProductEditView";
import { FilterGroup } from "../../../model/common/ListView";
import { getManualLink } from "../../../common/utils/getManualLink";
import { getPlainTaxes } from "../taxes/actions";
import { State } from "../../../reducers/state";
import { getIncomeAccounts } from "../accounts/actions";
import { checkPermissions, getUserPreferences } from "../../../common/actions";
import { ACCOUNT_DEFAULT_STUDENT_ENROLMENTS_ID, PLAIN_LIST_MAX_PAGE_SIZE } from "../../../constants/Config";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";
import { getDataCollectionRules, getEntityRelationTypes } from "../../preferences/actions";
import { getCommonPlainRecords } from "../../../common/actions/CommonPlainRecordsActions";

interface MembershipProductsProps {
  getMembershipProductRecord?: () => void;
  onInit?: (initial: MembershipProduct) => void;
  onCreate?: (membershipProduct: MembershipProduct) => void;
  onSave?: (id: string, membershipProduct: MembershipProduct) => void;
  getRecords?: () => void;
  getFilters?: () => void;
  getRelationTypes?: () => void;
  getAccounts?: () => void;
  getTaxes?: () => void;
  clearListState?: () => void;
  getDataCollectionRules?: () => void;
  getDefaultIncomeAccount?: () => void;
  accounts?: Account[];
  taxes?: Tax[];
  preferences?: any;
  updatingTaxes?: boolean;
  updatingAccounts?: boolean;
  checkPermissions?: any;
  getMembershipProductContactRelationTypes?: () => void;
}

const Initial: MembershipProduct = {
  code: null,
  corporatePasses: [],
  description: null,
  expiryDays: null,
  expiryType: "1st January",
  feeExTax: null,
  id: 0,
  incomeAccountId: null,
  membershipDiscounts: [],
  name: null,
  status: "Can be purchased in office & online",
  taxId: null,
  totalFee: 0,
  relatedSellables: [],
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

const expressionFindMembers = " and productItems.status not ( CANCELLED , CREDITED ) and productItems.product.id";

const findRelatedGroup: any[] = [
  {
    title: "Audits",
    list: "audit",
    expression: "entityIdentifier == MembershipProduct and entityId"
  },
  { title: "Current members", list: "contact", expression: "productItems.expiryDate > now" + expressionFindMembers },
  { title: "Expired members", list: "contact", expression: "productItems.expiryDate <= now" + expressionFindMembers },
  { title: "Classes", list: "class", expression: "discountCourseClasses.discount.discountMemberships.membershipProduct.id" },
  { title: "Discounts", list: "discount", expression: "discountMemberships.membershipProduct.id" }
];

const manualLink = getManualLink("concessions_creatingMemberships");

const preformatBeforeSubmit = (value: MembershipProduct): MembershipProduct => {
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

const MembershipProducts: React.FC<MembershipProductsProps> = props => {
  const [initNew, setInitNew] = useState(false);

  const {
    getMembershipProductRecord,
    onInit,
    onCreate,
    onSave,
    getFilters,
    clearListState,
    getAccounts,
    getTaxes,
    getDefaultIncomeAccount,
    updatingTaxes,
    updatingAccounts,
    taxes,
    accounts,
    preferences,
    getMembershipProductContactRelationTypes,
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
        onInit({ ...Initial, incomeAccountId: account.id, taxId: account.tax.id });
      } else {
        onInit(Initial);
      }
    }
  }, [initNew, updatingAccounts, updatingTaxes, preferences]);

  useEffect(() => {
    getDefaultIncomeAccount();
    getMembershipProductContactRelationTypes();
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
        listProps={{ primaryColumn: "name", secondaryColumn: "sku" }}
        editViewProps={{
          manualLink
        }}
        EditViewContent={MembershipProductEditView}
        getEditRecord={getMembershipProductRecord}
        rootEntity="MembershipProduct"
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
  onInit: (initial: MembershipProduct) => {
    dispatch(setListEditRecord(initial));
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, initial));
  },
  getMembershipProductContactRelationTypes: () => {
    dispatch(checkPermissions({ path: plainContactRelationTypePath, method: "GET" },
     [
       getCommonPlainRecords("ContactRelationType", 0, "toContactName", true, null, PLAIN_LIST_MAX_PAGE_SIZE)
     ]));
  },
  getDefaultIncomeAccount: () => dispatch(getUserPreferences([ACCOUNT_DEFAULT_STUDENT_ENROLMENTS_ID])),
  getTaxes: () => dispatch(getPlainTaxes()),
  getAccounts: () => dispatch(getIncomeAccounts()),
  getFilters: () => dispatch(getFilters("MembershipProduct")),
  clearListState: () => dispatch(clearListState()),
  getMembershipProductRecord: (id: string) => dispatch(getMembershipProduct(id)),
  onSave: (id: string, membershipProduct: MembershipProduct) => dispatch(updateMembershipProduct(id, membershipProduct)),
  onCreate: (membershipProduct: MembershipProduct) => dispatch(createMembershipProduct(membershipProduct)),
  checkPermissions: () => dispatch(checkPermissions({ path: plainCorporatePassPath, method: "GET" })),
  getRelationTypes: () => dispatch(getEntityRelationTypes()),
  getDataCollectionRules: () => dispatch(getDataCollectionRules()),
});

const mapStateToProps = (state: State) => ({
  updatingTaxes: state.taxes.updatingItems,
  taxes: state.taxes.items,
  updatingAccounts: state.accounts.updatingIncomeItems,
  accounts: state.accounts.incomeItems,
  preferences: state.userPreferences
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(MembershipProducts);
