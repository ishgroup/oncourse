/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import * as React from "react";
import { connect } from "react-redux";
import { initialize } from "redux-form";
import { Dispatch } from "redux";
import { WaitingList } from "@api/model";
import ListView from "../../../common/components/list-view/ListView";
import SendMessageEditView from "../messages/components/SendMessageEditView";
import WaitingListEditView from "./components/WaitingListEditView";
import {
  clearListState,
  getFilters,
  setListEditRecord,
 } from "../../../common/components/list-view/actions";
import { getEntityTags, getListTags } from "../../tags/actions";
import {
  createWaitingList, getWaitingList, removeWaitingList, updateWaitingList
} from "./actions";
import { getManualLink } from "../../../common/utils/getManualLink";
import { State } from "../../../reducers/state";
import WaitingListCogWheel from "./components/WaitingListCogWheel";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";
import { checkPermissions } from "../../../common/actions";

const Initial: WaitingList = {
  id: null,
  privateNotes: null,
  studentNotes: null,
  studentCount: 1,
  tags: [],
  sites: [],
  customFields: {}
};

const findRelatedGroup: any[] = [
  { title: "Audits", list: "audit", expression: "entityIdentifier == WaitingList and entityId" },
  { title: "Contacts", list: "contact", expression: "student.waitingLists.id" }
];

const nestedEditFields = {
  SendMessage: props => <SendMessageEditView {...props} />
};

const manualLink = getManualLink("waitingLists");

const nameCondition = (value: WaitingList) => value.courseName;

class WaitingLists extends React.Component<any, any> {
  componentDidMount() {
    this.props.getTags();
    this.props.getFilters();
    this.props.getQePermissions();
    this.props.getTagsForSitesSearch();
  }

  componentWillUnmount() {
    this.props.clearListState();
  }

  render() {
    const {
      getWaitingListRecord, onCreate, onDelete, onSave, updateTableModel, onInit
    } = this.props;

    return (
      <div>
        <ListView
          listProps={{
            primaryColumn: "student.contact.fullName",
            secondaryColumn: "course.name"
          }}
          editViewProps={{
            manualLink,
            nameCondition
          }}
          updateTableModel={updateTableModel}
          nestedEditFields={nestedEditFields}
          EditViewContent={WaitingListEditView}
          getEditRecord={getWaitingListRecord}
          rootEntity="WaitingList"
          onInit={onInit}
          onCreate={onCreate}
          onDelete={onDelete}
          onSave={onSave}
          findRelated={findRelatedGroup}
          CogwheelAdornment={WaitingListCogWheel}
        />
      </div>
    );
  }
}

const mapStateToProps = (state: State) => ({
  customFieldTypesUpdating: state.customFieldTypes.updating,
  customFieldTypes: state.customFieldTypes.types["WaitingList"]
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
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
  getFilters: () => dispatch(getFilters("WaitingList")),
  getTags: () => dispatch(getListTags("WaitingList")),
  clearListState: () => dispatch(clearListState()),
  getWaitingListRecord: (id: string) => dispatch(getWaitingList(id)),
  onSave: (id: string, waitingList: WaitingList) => dispatch(updateWaitingList(id, waitingList)),
  onCreate: (waitingList: WaitingList) => dispatch(createWaitingList(waitingList)),
  onDelete: (id: string) => dispatch(removeWaitingList(id))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(WaitingLists);
