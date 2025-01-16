/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Lead } from "@api/model";
import React, { useEffect } from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { initialize } from "redux-form";
import { checkPermissions } from "../../../common/actions";
import { notesAsyncValidate } from "../../../common/components/form/notes/utils";
import { clearListState, getFilters, setListEditRecord, } from "../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";
import ListView from "../../../common/components/list-view/ListView";
import { getManualLink } from "../../../common/utils/getManualLink";
import { FilterGroup, FindRelatedItem } from "../../../model/common/ListView";
import { State } from "../../../reducers/state";
import { getActiveUsers } from "../../security/actions";
import { getEntityTags, getListTags } from "../../tags/actions";
import LeadCogWheel from "./components/LeadCogWheel";
import LeadEditView from "./components/LeadEditView";

const Initial: Lead = {
  id: null,
  status: "Open",
  relatedSellables: [],
  studentNotes: null,
  studentCount: 1,
  tags: [],
  sites: [],
  customFields: {}
};

const filterGroups: FilterGroup[] = [
  {
    title: "Status",
    filters: [
      {
        name: "Open",
        expression: "(status == OPEN)",
        active: true
      },
      {
        name: "Closed",
        expression: "(status == CLOSED)",
        active: false
      }
    ]
  }
];

const findRelatedGroup: FindRelatedItem[] = [
  { title: "Audits", list: "audit", expression: "entityIdentifier == Lead and entityId" },
  { title: "Contacts", list: "contact", expression: "student.leads.id" },
  { title: "Courses", list: "course", expression: "leadItems.lead.id" },
  {
    title: "Related products...",
    items: [
      {
        title: "Related articles",
        list: "product",
        expression: "leadItems.lead.id"
      },
      {
        title: "Related memberships",
        list: "membership",
        expression: "leadItems.lead.id"
      },
      {
        title: "Related vouchers",
        list: "voucher",
        expression: "leadItems.lead.id"
      }
    ]
  },
];

const manualLink = getManualLink("leads");

const setRowClasses = ({ status }) => (status === "Closed" ? "text-op05" : undefined);

const Leads = props => {
  const {
    onInit
  } = props;

  useEffect(() => {
    props.getTags();
    props.getFilters();
    props.getQePermissions();
    props.getTagsForSitesSearch();
    props.getActiveUsers();

    return () => props.clearListState();
  }, []);

  const preformatBeforeSubmit = (value: Lead): Lead => {
    if (value.relatedSellables.length) {
      value.relatedSellables.forEach((s: any) => {
        if (s.tempId) {
          delete s.tempId;
        }
      });
    }

    return value;
  };

  return (
    <ListView
      listProps={{
        setRowClasses,
        primaryColumn: "estimatedValue",
        secondaryColumn: "customer.fullName"
      }}
      editViewProps={{
        manualLink,
        asyncValidate: notesAsyncValidate,
        asyncChangeFields: ["notes[].message"],
        hideTitle: true
      }}
      EditViewContent={LeadEditView}
      rootEntity="Lead"
      onInit={onInit}
      findRelated={findRelatedGroup}
      filterGroupsInitial={filterGroups}
      CogwheelAdornment={LeadCogWheel}
      preformatBeforeSubmit={preformatBeforeSubmit}
    />
  );
};

const mapStateToProps = (state: State) => ({
  customFieldTypesUpdating: state.customFieldTypes.updating,
  customFieldTypes: state.customFieldTypes.types["Lead"]
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  onInit: () => {
    dispatch(setListEditRecord(Initial));
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, Initial));
  },
  getQePermissions: () => {
    dispatch(checkPermissions({ keyCode: "ENROLMENT_CREATE" }));
  },
  getTagsForSitesSearch: () => {
    dispatch(getEntityTags("Site"));
  },
  getActiveUsers: () => dispatch(getActiveUsers()),
  getFilters: () => dispatch(getFilters("Lead")),
  getTags: () => dispatch(getListTags("Lead")),
  clearListState: () => dispatch(clearListState())
});

export default connect(mapStateToProps, mapDispatchToProps)(Leads);