import { Contact } from '@api/model';
import Divider from '@mui/material/Divider';
import { openInternalLink } from 'ish-ui';
import React, { useEffect, useState } from 'react';
import InstantFetchErrorHandler from '../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler';
import ExpandableContainer from '../../../../common/components/layout/expandable/ExpandableContainer';
import NestedTable from '../../../../common/components/list-view/components/list/ReactTableNestedList';
import EntityService from '../../../../common/services/EntityService';
import { getCustomColumnsMap } from '../../../../common/utils/common';
import { NESTED_LIST_PAGE_SIZE } from '../../../../constants/Config';
import { EditViewProps } from '../../../../model/common/ListView';
import { NestedTableColumn } from '../../../../model/common/NestedTable';

interface ContactsMessagesProps extends EditViewProps<Contact> {
  twoColumn?: boolean;
}

const openRow = ({ messageId }) => {
  openInternalLink(`/message/${messageId}`);
};

const plainColumns = 'createdOn,timeOfDelivery,emailSubject,creatorKey,status,type';

const messagesColumns: NestedTableColumn[] = [
  {
    name: "createdOn",
    title: "Created on",
    type: "date",
    width: 160
  },
  {
    name: "timeOfDelivery",
    title: "Sent",
    type: "date",
    width: 160
  },
  {
    name: "emailSubject",
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

const messagesColumnsMap = getCustomColumnsMap(plainColumns);

const ContactsMessages: React.FC<ContactsMessagesProps> = props => {
  const {
    values,
    tabIndex,
    expanded,
    setExpanded,
    syncErrors,
    dispatch
  } = props;

  const [messageRows, setMessageRows] = useState({
    rows: [],
    getAll: () => [],
    length: 0
  });
  
  const loadMoreMessages = (start: number, end?: number) => end > values.messagesCount
    ? null
    : EntityService.getPlainRecords(
    'Message',
    plainColumns,
    `contact.id is ${values.id}`,
    NESTED_LIST_PAGE_SIZE,
    start,
    'createdOn'
  )
    .then(data => setMessageRows(prev => {
      const newRows = [
        ...prev.rows,
        ...data.rows.map(messagesColumnsMap)];
      return {
        rows: newRows,
        getAll: () => newRows,
        length: newRows.length
      };
    }))
    .catch(e => InstantFetchErrorHandler(dispatch, e));

  useEffect(() => {
    if (values.messagesCount) {
      loadMoreMessages(0);
    }
  }, [values.messagesCount]);

  return values ? (
    <div className="pl-3 pr-3">
      <ExpandableContainer 
        formErrors={syncErrors} 
        index={tabIndex} 
        expanded={expanded} 
        setExpanded={setExpanded} 
        header={`${values.messagesCount} message${values.messagesCount === 1 ? '' : 's'}`}
      >
        <div
          className="flex-column pb-3"
        >
          <NestedTable
            goToLink={`/message?search=contact.id is ${values.id}`}
            columns={messagesColumns}
            onRowDoubleClick={openRow}
            fields={messageRows}
            onLoadMore={loadMoreMessages}
            calculateHeight
            meta={{}}
            hideHeader
          />
        </div>
      </ExpandableContainer>
      <Divider className="mb-2" />
    </div>
  ) : null;
};

export default ContactsMessages;
