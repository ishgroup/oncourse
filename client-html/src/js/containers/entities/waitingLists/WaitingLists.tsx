/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { connect } from "react-redux";
import { initialize } from "redux-form";
import { Dispatch } from "redux";
import { CustomFieldType, WaitingList } from "@api/model";
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
import { getCustomFieldTypes } from "../customFieldTypes/actions";
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
  private initializingNew: boolean = false;

  componentDidMount() {
    this.props.getTags();
    this.props.getFilters();
    this.props.getQePermissions();
    this.props.getCustomFieldTypes();
    this.props.getTagsForSitesSearch();
  }

  componentWillUnmount() {
    this.props.clearListState();
  }

  componentDidUpdate(prev) {
    const { customFieldTypesUpdating, customFieldTypes } = this.props;

    if (this.initializingNew && prev.customFieldTypesUpdating && !customFieldTypesUpdating) {
      this.initializingNew = false;

      const customFields = {};

      // customFieldTypes.forEach((field: CustomFieldType) => {
      //   if (field.defaultValue && !field.defaultValue.match(/[;*]/g)) {
      //     customFields[field.fieldKey] = field.defaultValue;
      //   }
      // });

      const initial = {
        ...Initial,
        customFields
      };

      this.props.onInit(initial);
    }
  }

  onInit = () => {
    this.initializingNew = true;
    this.props.getCustomFieldTypes();
  };

  render() {
    const {
      getWaitingListRecord, onCreate, onDelete, onSave, updateTableModel
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
          onInit={this.onInit}
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
  onInit: (initial: WaitingList) => {
    dispatch(setListEditRecord(initial));
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, initial));
  },
  getQePermissions: () => {
    dispatch(checkPermissions({ keyCode: "ENROLMENT_CREATE" }));
  },
  getTagsForSitesSearch: () => {
    dispatch(getEntityTags("Site"));
  },
  getCustomFieldTypes: () => dispatch(getCustomFieldTypes("WaitingList")),
  getFilters: () => dispatch(getFilters("WaitingList")),
  getTags: () => dispatch(getListTags("WaitingList")),
  clearListState: () => dispatch(clearListState()),
  getWaitingListRecord: (id: string) => dispatch(getWaitingList(id)),
  onSave: (id: string, waitingList: WaitingList) => dispatch(updateWaitingList(id, waitingList)),
  onCreate: (waitingList: WaitingList) => dispatch(createWaitingList(waitingList)),
  onDelete: (id: string) => dispatch(removeWaitingList(id))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(WaitingLists);
