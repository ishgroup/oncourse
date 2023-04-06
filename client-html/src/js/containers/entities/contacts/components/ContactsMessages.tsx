import React, { useCallback } from "react";
import { Contact } from "@api/model";
import { FieldArray } from "redux-form";
import Divider from "@mui/material/Divider";
import { openInternalLink } from "../../../../common/utils/links";
import { NestedTableColumn } from "../../../../model/common/NestedTable";
import NestedTable from "../../../../common/components/list-view/components/list/ReactTableNestedList";
import ExpandableContainer from "../../../../common/components/layout/expandable/ExpandableContainer";
import { EditViewProps } from "../../../../model/common/ListView";

interface ContactsMessagesProps extends EditViewProps<Contact> {
  twoColumn?: boolean;
}

const openRow = ({ messageId }) => {
  openInternalLink(`/message/${messageId}`);
};

const ContactsMessages: React.FC<ContactsMessagesProps> = props => {
  const {
    values,
    tabIndex,
    expanded,
    setExpanded,
    syncErrors
  } = props;

  const messagesColumns: NestedTableColumn[] = [
    {
      name: "createdOn",
      title: "Created on",
      type: "date",
      width: 160
    },
    {
      name: "sentOn",
      title: "Sent",
      type: "date",
      width: 160
    },
    {
      name: "subject",
      title: "Subject"
    },
    {
      name: "creatorKey",
      title: "Creator key"
    },
    {
      name: "status",
      title: "Status",
      width: 120
    },
    {
      name: "type",
      title: "Type",
      width: 120
    }
  ];

  const getMessagesCount = useCallback(() => (values && Array.isArray(values.messages) ? values.messages.length : 0), [
    values.messages
  ]);

  const getMessagesTableTitle = () => (getMessagesCount() > 0 ? `${getMessagesCount()} ` : "") + (getMessagesCount() === 1 ? "message" : "messages");

  return values ? (
    <div className="pl-3 pr-3">
      <ExpandableContainer formErrors={syncErrors} index={tabIndex} expanded={expanded} setExpanded={setExpanded} header={getMessagesTableTitle()}>
        <div
          className="flex-column pb-3"
        >
          <FieldArray
            name="messages"
            component={NestedTable}
            columns={messagesColumns}
            onRowDoubleClick={openRow}
            rerenderOnEveryChange
            sortBy={(a, b) => new Date(b.createdOn).getTime() - new Date(a.createdOn).getTime()}
            calculateHeight
            hideHeader
          />
        </div>
      </ExpandableContainer>
      <Divider className="mb-2" />
    </div>
  ) : null;
};

export default ContactsMessages;
