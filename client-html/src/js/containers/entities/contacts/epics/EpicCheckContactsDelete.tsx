/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { GroupedContacts } from '@api/model';
import React from 'react';
import { showConfirm } from '../../../../common/actions';
import { Create, Request } from '../../../../common/epics/EpicUtils';
import store from '../../../../constants/Store';
import { ListState } from '../../../../model/common/ListView';
import { bulkDeleteEntityRecordsRequest } from '../../common/actions';
import { CHECK_CONTACTS_DELETE } from '../actions';
import { DeleteContactsInfo } from '../components/delete-contacts/DeleteContactsInfo';
import ContactsService from '../services/ContactsService';

const request: Request<
  GroupedContacts,
  ListState['selection']
> = {
  type: CHECK_CONTACTS_DELETE,
  getData: selection => ContactsService.checkIfCanBeDeleted(selection),
  processData: (groups, state) =>[
    showConfirm({
      confirmMessage: <DeleteContactsInfo groupedContacts={groups} listRecords={state.list.records} />,
      ... groups.canBeRemoved.length
        ? {
          confirmButtonText: 'Delete anyway',
          onConfirm: () => {
            store.dispatch(bulkDeleteEntityRecordsRequest(
              'Contact',
              {
                ids: groups.canBeRemoved,
                search: state.list.searchQuery.search,
                filter: state.list.searchQuery.filter,
                tagGroups: state.list.searchQuery.tagGroups
              }));
          }
        }
        : {  }
    })
  ]
};

export const EpicCheckContactsDelete = Create(request);