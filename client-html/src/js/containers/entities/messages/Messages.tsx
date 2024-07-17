/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Message } from "@api/model";
import { format, isValid } from "date-fns";
import { DD_MM_YYYY_SLASHED } from "ish-ui";
import React, { Dispatch, useEffect } from "react";
import { connect } from "react-redux";
import { clearListState, getFilters, setFilterGroups, } from "../../../common/components/list-view/actions";
import ListView from "../../../common/components/list-view/ListView";
import { useAppSelector } from "../../../common/utils/hooks";
import { FilterGroup, FindRelatedItem } from "../../../model/common/ListView";
import { Categories, MessageDateArchived } from "../../../model/preferences";
import { getPreferencesByKeys } from "../../preferences/actions";
import MessageEditView from "./components/MessageEditView";
import MessagesCogwheelActions from "./components/MessagesCogwheelActions";

interface MessagesProps {
  onInit?: () => void;
  getFilters?: () => void;
  getPreferences?: () => void;
  clearListState?: () => void;
  setFilterGroups?: (filterGroups: FilterGroup[]) => void;
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
    clearListState,
    getPreferences,
    setFilterGroups
  } = props;
  
  const archivedDate = useAppSelector(state => state.preferences.messaging && state.preferences.messaging[MessageDateArchived.uniqueKey]);
  const filterGroupdsCurrent = useAppSelector(state => state.list.filterGroups);
  
  useEffect(() => {
    const archivedDateObj = new Date(archivedDate);
    if (isValid(archivedDateObj)) {
      setFilterGroups((filterGroupdsCurrent.length ? filterGroupdsCurrent : filterGroups).map(fg => ({
        ...fg,
        filters: [
          ...fg.filters,
          ...fg.title === 'CORE FILTER' ? [{
            name: "Not archived",
            active: true,
            expression: `createdOn after ${format(archivedDateObj, DD_MM_YYYY_SLASHED)}`
          }] : []
        ]
      })));
    }
  }, [archivedDate]);

  useEffect(() => {
    getFilters();
    getPreferences();
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
      CogwheelAdornment={MessagesCogwheelActions}
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
  clearListState: () => dispatch(clearListState()),
  getPreferences: () => dispatch(getPreferencesByKeys([MessageDateArchived.uniqueKey], Categories.messaging)),
  setFilterGroups: filterGroups => dispatch(setFilterGroups(filterGroups))
});

export default connect<any, any, any>(null, mapDispatchToProps)(Messages);