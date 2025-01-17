/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Account, MembershipProduct, Tax } from "@api/model";
import React, { useEffect, useState } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { initialize } from "redux-form";
import { checkPermissions, getUserPreferences } from "../../../common/actions";
import { getCommonPlainRecords } from "../../../common/actions/CommonPlainRecordsActions";
import { notesAsyncValidate } from "../../../common/components/form/notes/utils";
import { clearListState, getFilters, setListEditRecord, } from "../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";
import ListView from "../../../common/components/list-view/ListView";
import { getManualLink } from "../../../common/utils/getManualLink";
import { plainContactRelationTypePath, plainCorporatePassPath } from "../../../constants/Api";
import { ACCOUNT_DEFAULT_STUDENT_ENROLMENTS_ID, PLAIN_LIST_MAX_PAGE_SIZE } from "../../../constants/Config";
import { FilterGroup, FindRelatedItem } from "../../../model/common/ListView";
import { State } from "../../../reducers/state";
import { getDataCollectionRules, getEntityRelationTypes } from "../../preferences/actions";
import { getListTags } from "../../tags/actions";
import { getPlainAccounts } from "../accounts/actions";
import BulkEditCogwheelOption from "../common/components/BulkEditCogwheelOption";
import { getPlainTaxes } from "../taxes/actions";
import MembershipProductEditView from "./components/MembershipProductEditView";

interface MembershipProductsProps {
  onInit?: (initial: MembershipProduct) => void;
  getRecords?: () => void;
  getFilters?: () => void;
  getTags?: () => void;
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
  tags: [],
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

const findRelatedGroup: FindRelatedItem[] = [
  {
    title: "Audits",
    list: "audit",
    expression: "entityIdentifier == MembershipProduct and entityId"
  },
  { title: "Current members", list: "contact", expression: "productItems.expiryDate > now" + expressionFindMembers },
  { title: "Expired members", list: "contact", expression: "productItems.expiryDate <= now" + expressionFindMembers },
  { title: "Classes", list: "class", expression: "discountCourseClasses.discount.discountMemberships.membershipProduct.id" },
  { title: "Discounts", list: "discount", expression: "discountMemberships.membershipProduct.id" },
  { title: "Sales", list: "sale", expression: "type is MEMBERSHIP AND product.id" },
];

const manualLink = getManualLink("concessions-and-memberships-1");

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

const setRowClasses = ({ isOnSale }) => {
  if (isOnSale === "No") return "text-op05";
  return undefined;
};

const MembershipProducts: React.FC<MembershipProductsProps> = props => {
  const [initNew, setInitNew] = useState(false);

  const {
    onInit,
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
    getTags,
    getDataCollectionRules
  } = props;

  useEffect(() => {
    if (initNew && taxes && accounts && preferences && preferences[ACCOUNT_DEFAULT_STUDENT_ENROLMENTS_ID]) {
      setInitNew(false);
      const defaultId = preferences[ACCOUNT_DEFAULT_STUDENT_ENROLMENTS_ID];
      const account = accounts.find(item => item.id === Number(defaultId));
      if (account) {
        onInit({ ...Initial, incomeAccountId: account.id, taxId: account.tax?.id });
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
    getTags();
    getFilters();
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
      EditViewContent={MembershipProductEditView}
      CogwheelAdornment={BulkEditCogwheelOption}
      rootEntity="MembershipProduct"
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
  onInit: (initial: MembershipProduct) => {
    dispatch(setListEditRecord(initial));
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, initial));
  },
  getMembershipProductContactRelationTypes: () => {
    dispatch(checkPermissions({ path: plainContactRelationTypePath, method: "GET" },
     [
       getCommonPlainRecords("ContactRelationType", 0, "toContactName", true, null, PLAIN_LIST_MAX_PAGE_SIZE, items => items.map(r => ({
         id: r.id,
         description: r.toContactName
       })))
     ]));
  },
  getDefaultIncomeAccount: () => dispatch(getUserPreferences([ACCOUNT_DEFAULT_STUDENT_ENROLMENTS_ID])),
  getTaxes: () => dispatch(getPlainTaxes()),
  getAccounts: () => getPlainAccounts(dispatch, "income"),
  getTags: () => dispatch(getListTags("MembershipProduct")),
  getFilters: () => dispatch(getFilters("MembershipProduct")),
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

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(MembershipProducts);