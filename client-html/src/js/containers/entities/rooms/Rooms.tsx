/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Room } from "@api/model";
import * as React from "react";
import { connect } from "react-redux";
import { Dispatch } from "redux";
import { initialize } from "redux-form";
import { getCommonPlainRecords } from "../../../common/actions/CommonPlainRecordsActions";
import { notesAsyncValidate } from "../../../common/components/form/notes/utils";
import { clearListState, getFilters, setListEditRecord, } from "../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";
import ListView from "../../../common/components/list-view/ListView";
import { getManualLink } from "../../../common/utils/getManualLink";
import { PLAIN_LIST_MAX_PAGE_SIZE } from "../../../constants/Config";
import { FilterGroup, FindRelatedItem } from "../../../model/common/ListView";
import { getListTags } from "../../tags/actions";
import BulkEditCogwheelOption from "../common/components/BulkEditCogwheelOption";
import RoomEditView from "./components/RoomEditView";

const manualLink = getManualLink("working-with-sites-and-rooms#rooms");

const filterGroups: FilterGroup[] = [
  {
    title: "CORE FILTER",
    filters: [
      {
        name: "Virtual",
        expression: "site.isVirtual is true",
        active: false
      },
      {
        name: "Real",
        expression: "site.isVirtual not is true",
        active: false
      }
    ]
  }
];

const Initial: Room = {
  id: null,
  name: null,
  seatedCapacity: 0,
  siteId: null,
  kioskUrl: null,
  directions: null,
  facilities: null,
  tags: [],
  documents: [],
  rules: []
};

const findRelatedGroup: FindRelatedItem[] = [
  { title: "Audits", list: "audit", expression: "entityIdentifier == Room and entityId" },
  {
    title: "Current classes",
    list: "class",
    expression: "(endDateTime > today or isDistantLearningCourse == true) and sessions.room.id"
  },
  {
    title: "Documents",
    list: "document",
    expression: "attachmentRelations.entityIdentifier == Room and attachmentRelations.entityRecordId"
  },
  { title: "Student feedback", list: "survey", expression: "enrolment.courseClass.room.id" },
  { title: "Timetable", list: "timetable", expression: "room.id" }
];

class Rooms extends React.Component<any, any> {
  componentDidMount() {
    this.props.getFilters();
    this.props.getTags();
    this.props.getSites();
  }

  shouldComponentUpdate() {
    return false;
  }

  componentWillUnmount() {
    this.props.clearListState();
  }

  render() {
    const {
      onInit
    } = this.props;

    return (
      <ListView
        listProps={{
          primaryColumn: "name",
          secondaryColumn: "site.name"
        }}
        editViewProps={{
          manualLink,
          asyncValidate: notesAsyncValidate,
          asyncChangeFields: ["notes[].message"],
          hideTitle: true
        }}
        CogwheelAdornment={BulkEditCogwheelOption}
        EditViewContent={RoomEditView}
        rootEntity="Room"
        onInit={onInit}
        filterGroupsInitial={filterGroups}
        findRelated={findRelatedGroup}
      />
    );
  }
}

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: () => {
    dispatch(setListEditRecord(Initial));
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, Initial));
  },
  getSites: () => dispatch(getCommonPlainRecords("Site", 0, "name,localTimezone", true, "name", PLAIN_LIST_MAX_PAGE_SIZE)),
  getFilters: () => dispatch(getFilters("Room")),
  getTags: () => {
    dispatch(getListTags("Room"));
  },
  clearListState: () => dispatch(clearListState())
});

export default connect(null, mapDispatchToProps)(Rooms);