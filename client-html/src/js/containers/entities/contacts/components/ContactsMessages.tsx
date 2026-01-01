import { Contact, DataRow } from '@api/model';
import Launch from '@mui/icons-material/Launch';
import { IconButton } from '@mui/material';
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

const openRow = ({ id }) => {
  openInternalLink(`/message/${id}`);
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

const messageRowsInitial = {
  rows: [],
  getAll: () => [],
  length: 0
};

const ContactsMessages: React.FC<ContactsMessagesProps> = props => {
  const {
    values,
    tabIndex,
    expanded,
    setExpanded,
    syncErrors,
    dispatch
  } = props;

  const [messagesCount, setMessagesCount] = useState(0);
  const [messageRows, setMessageRows] = useState(messageRowsInitial);
  
  const loadMoreMessages = (start: number, end?: number) => end > messagesCount
    ? null
    : EntityService.getPlainRecords(
    'Message',
    plainColumns,
    `contact.id is ${values.id}`,
    NESTED_LIST_PAGE_SIZE,
    start,
    'createdOn'
  )
    .then(data => {
      setMessagesCount(data.filteredCount)
      setMessageRows(prev => {
        const newRows = [
          ...prev.rows,
          ...data.rows.map(messagesColumnsMap)];
        return {
          rows: newRows,
          getAll: () => newRows,
          length: newRows.length
        };
      })
    })
    .catch(e => InstantFetchErrorHandler(dispatch, e));

  useEffect(() => {
    setMessageRows(messageRowsInitial);
    loadMoreMessages(0);
  }, [values.id]);

  return values ? (
    <div className="pl-3 pr-3">
      <ExpandableContainer 
        formErrors={syncErrors} 
        index={tabIndex} 
        expanded={expanded} 
        setExpanded={setExpanded}
        header={`${messagesCount} message${messagesCount === 1 ? '' : 's'}`}
        headerAdornment={
          <IconButton
            color="primary"
            size="small"
            onClick={() => openInternalLink(`/message?search=contact.id is ${values.id}`)}
          >
            <Launch fontSize="inherit"/>
          </IconButton>
        }
      >
        <div
          className="flex-column pb-3"
        >
          <NestedTable
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
