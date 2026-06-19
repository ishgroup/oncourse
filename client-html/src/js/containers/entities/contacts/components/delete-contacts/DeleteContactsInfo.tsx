import { GroupedContacts } from '@api/model';
import { Typography } from '@mui/material';
import Alert from '@mui/material/Alert';
import React from 'react';
import { ListState } from '../../../../../model/common/ListView';

interface Props {
  groupedContacts: GroupedContacts;
  listRecords: ListState['records'];
}

export const DeleteContactsInfo = ({ groupedContacts, listRecords }: Props) => {
  const nameColumn = listRecords.columns.filter(c => c.visible).findIndex(c => c.title === 'Name');
  return groupedContacts && <div>
    {Boolean(groupedContacts.canBeRemoved.length) && <Typography className='mb-2'>Records will be deleted permanently. This action can not be undone</Typography>}
    {Boolean(groupedContacts.cannotBeRemoved.length) && <Alert severity={groupedContacts.canBeRemoved.length  ? 'warning' : 'error'}>
      {groupedContacts.canBeRemoved.length ? 'Some' : 'All'} of the selected records can't be removed due to existing data bindings
    </Alert>}
    <div className='d-flex'>
      <div className='flex-fill'>
        <h4 className="successColor">Will be removed</h4>
        <div>
          {groupedContacts.canBeRemoved.map(c => <Typography key={c}>
            {listRecords.rows.find(r => r.id === c.toString()).values[nameColumn]}
          </Typography>)}
        </div>
      </div>
      <div className='flex-fill'>
        <h4 className="errorColor">Won't be removed</h4>
        <div>
          {groupedContacts.cannotBeRemoved.map(c => <Typography key={c}>
            {listRecords.rows.find(r => r.id === c.toString()).values[nameColumn]}
          </Typography>)}
        </div>
      </div>
    </div>
  </div>;
};
