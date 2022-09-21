/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { Dispatch, useEffect } from "react";
import { connect } from "react-redux";
import { initialize } from "redux-form";
import { Message } from "@api/model";
import {
  setListEditRecord,
  getFilters,
  clearListState,
} from "../../../common/components/list-view/actions";
import { FilterGroup } from "../../../model/common/ListView";
import MessageEditView from "./components/MessageEditView";
import ListView from "../../../common/components/list-view/ListView";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../common/components/list-view/constants";

interface MessagesProps {
  onInit?: () => void;
  getFilters?: () => void;
  clearListState?: () => void;
  onDelete?: (id: string) => void;
}

const Initial: Message = {
  createdOn: null,
  creatorKey: null,
  id: 0,
  message: null,
  modifiedOn: null,
  sentToContactFullname: null,
  subject: null
};

const filterGroups: FilterGroup[] = [
  {
    title: "CORE FILTER",
    filters: [
      {
        name: "Email",
        expression: "smsText is null",
        active: false
      },
      {
        name: "SMS",
        expression: "smsText not is null",
        active: false
      }
    ]
  }
];

const findRelatedGroup: any = [
  { title: "Audits", list: "audit", expression: "entityIdentifier == Message and entityId" },
  { title: "Contacts", list: "contact", expression: "messages.id" }
];

const primaryColumnCondition = row => row["recipientsString"] || "No recipients";

const Messages: React.FC<MessagesProps> = props => {
  const {
    onInit,
    getFilters,
    clearListState
  } = props;

  useEffect(() => {
    getFilters();
    return () => {
      clearListState();
    };
  }, []);

  return (
    <ListView
      listProps={{
        primaryColumn: "recipientsString",
        secondaryColumn: "createdOn",
        primaryColumnCondition
      }}
      editViewProps={{
        nameCondition: values => (values ? values.subject : "")
      }}
      EditViewContent={MessageEditView}
      rootEntity="Message"
      onInit={onInit}
      filterGroupsInitial={filterGroups}
      findRelated={findRelatedGroup}
      noListTags
    />
  );
};

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  onInit: () => {
    dispatch(setListEditRecord(Initial));
    dispatch(initialize(LIST_EDIT_VIEW_FORM_NAME, Initial));
  },
  getFilters: () => dispatch(getFilters("Message")),
  clearListState: () => dispatch(clearListState())
});

export default connect<any, any, any>(null, mapDispatchToProps)(Messages);