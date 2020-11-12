import React, { useCallback } from "react";
import { Contact } from "@api/model";
import { FieldArray } from "redux-form";
import { openInternalLink } from "../../../../common/utils/links";
import { NestedTableColumn } from "../../../../model/common/NestedTable";
import NestedTable from "../../../../common/components/list-view/components/list/ReactTableNestedList";
import { getTableWrapperHeight, THEME_SPACING } from "../utils";

interface ContactsMessagesProps {
  twoColumn?: boolean;
  values?: Contact;
}

const openRow = ({ messageId }) => {
  openInternalLink(`/message/${messageId}`);
};

const ContactsMessages: React.FC<ContactsMessagesProps> = props => {
  const { values } = props;

  const messagesColumns: NestedTableColumn[] = [
    {
      name: "createdOn",
      title: "Created",
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

  const getMessagesTableTitle = () => (getMessagesCount() === 1 ? "message" : "messages");

  const tableWrapperHeight = getTableWrapperHeight(getMessagesCount());

  return values ? (
    <div
      className="flex-column"
      style={{
        height: tableWrapperHeight,
        padding: THEME_SPACING * 3,
        marginBottom: THEME_SPACING * 3
      }}
    >
      <FieldArray
        name="messages"
        title={getMessagesTableTitle()}
        component={NestedTable}
        columns={messagesColumns}
        onRowDoubleClick={openRow}
        rerenderOnEveryChange
        sortBy={(a, b) => new Date(b.createdOn).getTime() - new Date(a.createdOn).getTime()}
      />
    </div>
  ) : null;
};

export default ContactsMessages;
