/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { initialize } from "redux-form";
import { Site } from "@api/model";
import { notesAsyncValidate } from "../../../common/components/form/notes/utils";
import ListView from "../../../common/components/list-view/ListView";
import {
  createSite, getSite, removeSite, updateSite
} from "./actions";
import { FilterGroup } from "../../../model/common/ListView";
import { getListTags } from "../../tags/actions";
import SiteEditView from "./components/SiteEditView";
import {
  setListEditRecord,
  clearListState,
  getFilters,
 } from "../../../common/components/list-view/actions";
import { getCountries, getTimezones } from "../../preferences/actions";
import { State } from "../../../reducers/state";
import { getUserPreferences } from "../../../common/actions";
import { DEFAULT_TIMEZONE_KEY } from "../../../constants/Config";
import { getManualLink } from "../../../common/utils/getManualLink";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";
import BulkEditCogwheelOption from "../common/components/BulkEditCogwheelOption";

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

const findRelatedGroup: any[] = [
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
  { title: "Student feedback", list: "survey", expression: "enrolment.courseClass.room.site.id" }
];

const secondaryColumnCondition = rows => {
  if (rows["isVirtual"] && rows["isVirtual"] === "true") {
    return "Virtual Site";
  }
  return rows["suburb"] || rows["street"] || "No Address";
};

const manualLink = getManualLink("sitesRooms");

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

  onSave = (id: string, item: Site) => {
    if (item.isAdministrationCentre === undefined) {
      item.isAdministrationCentre = false;
    }

    if (item.isShownOnWeb === undefined) {
      item.isShownOnWeb = false;
    }

    if (item.isVirtual === undefined) {
      item.isVirtual = false;
    }

    this.props.onSave(id, item);
  };

  onCreate = (item: Site) => {
    if (item.isAdministrationCentre === undefined) {
      item.isAdministrationCentre = false;
    }

    if (item.isShownOnWeb === undefined) {
      item.isShownOnWeb = false;
    }

    if (item.isVirtual === undefined) {
      item.isVirtual = false;
    }

    this.props.onCreate(item);
  };

  onInit = () => {
    const { defaultTimezone, defaultCountry, dispatch } = this.props;
    Initial.timezone = defaultTimezone;
    Initial.country = defaultCountry;

    dispatch(setListEditRecord(Initial));
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, Initial));
  };

  render() {
    const {
      getSiteRecord, onDelete
    } = this.props;

    return (
      <div>
        <ListView
          listProps={{
            primaryColumn: "name",
            secondaryColumn: "suburb",
            secondaryColumnCondition
          }}
          editViewProps={{
            manualLink,
            asyncValidate: notesAsyncValidate,
            asyncBlurFields: ["notes[].message"]
          }}
          CogwheelAdornment={BulkEditCogwheelOption}
          EditViewContent={SiteEditView}
          getEditRecord={getSiteRecord}
          rootEntity="Site"
          onInit={this.onInit}
          onDelete={onDelete}
          onCreate={this.onCreate}
          onSave={this.onSave}
          findRelated={findRelatedGroup}
          filterGroupsInitial={filterGroups}
        />
      </div>
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
  clearListState: () => dispatch(clearListState()),
  getSiteRecord: (id: string) => dispatch(getSite(id)),
  onSave: (id: string, site: Site) => dispatch(updateSite(id, site)),
  onCreate: (site: Site) => dispatch(createSite(site)),
  onDelete: (id: string) => dispatch(removeSite(id))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(Sites);
