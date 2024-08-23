/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Site } from "@api/model";
import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { initialize } from "redux-form";
import { getUserPreferences } from "../../../common/actions";
import { notesAsyncValidate } from "../../../common/components/form/notes/utils";
import { clearListState, getFilters, setListEditRecord, } from "../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";
import ListView from "../../../common/components/list-view/ListView";
import { getManualLink } from "../../../common/utils/getManualLink";
import { DEFAULT_TIMEZONE_KEY } from "../../../constants/Config";
import { FilterGroup, FindRelatedItem } from "../../../model/common/ListView";
import { State } from "../../../reducers/state";
import { getCountries, getTimezones } from "../../preferences/actions";
import { getListTags } from "../../tags/actions";
import BulkEditCogwheelOption from "../common/components/BulkEditCogwheelOption";
import SiteEditView from "./components/SiteEditView";

const filterGroups: FilterGroup[] = [
  {
    title: "CORE FILTER",
    filters: [
      {
        name: "Administration Center",
        expression: "isAdministrationCentre == true",
        active: false
      },
      {
        name: "Shown on Web",
        expression: "isShownOnWeb == true",
        active: false
      },
      {
        name: "Virtual",
        expression: "isVirtual == true",
        active: false
      }
    ]
  }
];

const Initial: Site = {
  id: null,
  isAdministrationCentre: false,
  isVirtual: false,
  isShownOnWeb: false,
  kioskUrl: null,
  name: null,
  street: null,
  suburb: null,
  state: null,
  postcode: null,
  country: null,
  timezone: null,
  longitude: null,
  latitude: null,
  drivingDirections: null,
  publicTransportDirections: null,
  specialInstructions: null,
  tags: [],
  rooms: [],
  documents: [],
  rules: []
};

const findRelatedGroup: FindRelatedItem[] = [
  { title: "Audits", list: "audit", expression: "entityIdentifier == Site and entityId" },
  {
    title: "Current classes",
    list: "class",
    expression: "(endDateTime > today or isDistantLearningCourse == true) and sessions.room.site.id"
  },
  {
    title: "Documents",
    list: "document",
    expression: "attachmentRelations.entityIdentifier == Site and attachmentRelations.entityRecordId"
  },
  { title: "Rooms", list: "room", expression: "site.id" },
  { title: "Student feedback", list: "survey", expression: "enrolment.courseClass.room.site.id" },
  { title: "Timetable", list: "timetable", expression: "room.site.id" }
];

const secondaryColumnCondition = rows => {
  if (rows["isVirtual"] && rows["isVirtual"] === "true") {
    return "Virtual Site";
  }
  return rows["suburb"] || rows["street"] || "No Address";
};

const manualLink = getManualLink("working-with-sites-and-rooms");

class Sites extends React.Component<any, any> {
  componentDidMount() {
    this.props.getFilters();
    this.props.getTags();
    this.props.getTimezones();
    this.props.getCountries();

    if (!this.props.collegeTimezone) {
      this.props.getDefaultTimezone();
    }
  }

  shouldComponentUpdate() {
    return false;
  }

  componentWillUnmount() {
    this.props.clearListState();
  }

  onInit = () => {
    const { defaultTimezone, defaultCountry, dispatch } = this.props;
    Initial.timezone = defaultTimezone;
    Initial.country = defaultCountry;

    dispatch(setListEditRecord(Initial));
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, Initial));
  };

  render() {
    return (
      <ListView
        listProps={{
          primaryColumn: "name",
          secondaryColumn: "suburb",
          secondaryColumnCondition
        }}
        editViewProps={{
          manualLink,
          asyncValidate: notesAsyncValidate,
          asyncChangeFields: ["notes[].message"],
          hideTitle: true
        }}
        CogwheelAdornment={BulkEditCogwheelOption}
        EditViewContent={SiteEditView}
        rootEntity="Site"
        onInit={this.onInit}
        findRelated={findRelatedGroup}
        filterGroupsInitial={filterGroups}
      />
    );
  }
}

const mapStateToProps = (state: State) => ({
  defaultTimezone: state.userPreferences[DEFAULT_TIMEZONE_KEY],
  defaultCountry: state.countries.find(c => c.name === "Australia")
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  dispatch,
  getFilters: () => {
    dispatch(getFilters("Site"));
  },
  getTags: () => {
    dispatch(getListTags("Site"));
  },
  getTimezones: () => {
    dispatch(getTimezones());
  },
  getDefaultTimezone: () => {
    dispatch(getUserPreferences([DEFAULT_TIMEZONE_KEY]));
  },
  getCountries: () => {
    dispatch(getCountries());
  },
  clearListState: () => dispatch(clearListState())
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Sites);