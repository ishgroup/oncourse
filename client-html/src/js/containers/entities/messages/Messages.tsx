/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Message } from "@api/model";
import React, { Dispatch, useEffect } from "react";
import { connect } from "react-redux";
import { clearListState, getFilters, } from "../../../common/components/list-view/actions";
import ListView from "../../../common/components/list-view/ListView";
import { FilterGroup, FindRelatedItem } from "../../../model/common/ListView";
import MessageEditView from "./components/MessageEditView";
import QuedMessagesBulkDelete from "./components/QuedMessagesBulkDelete";

interface MessagesProps {
  onInit?: () => void;
  getFilters?: () => void;
  clearListState?: () => void;
  onDelete?: (id: string) => void;
}

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

const findRelatedGroup: FindRelatedItem[] = [
  { title: "Audits", list: "audit", expression: "entityIdentifier == Message and entityId" },
  { title: "Contacts", list: "contact", expression: "messages.id" }
];

const primaryColumnCondition = row => row["recipientsString"] || "No recipients";

const Messages: React.FC<MessagesProps> = props => {
  const {
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
      CogwheelAdornment={QuedMessagesBulkDelete}
      rootEntity="Message"
      filterGroupsInitial={filterGroups}
      findRelated={findRelatedGroup}
      createButtonDisabled
      noListTags
    />
  );
};

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  getFilters: () => dispatch(getFilters("Message")),
  clearListState: () => dispatch(clearListState())
});

export default connect<any, any, any>(null, mapDispatchToProps)(Messages);