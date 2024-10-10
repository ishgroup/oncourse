/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import Typography from '@mui/material/Typography';
import { format } from 'date-fns';
import { D_MMM_YYYY, getHighlightedPartLabel } from 'ish-ui';
import * as React from 'react';
import { getContactFullName } from '../../../entities/contacts/utils';
import EnrolItemListRenderer from '../items/components/EnrolItemListRenderer';

const EnrolContactListView = React.memo<{
  title, contacts, onChangeHandler, disabledHandler, searchString, selectedContacts, relatedContacts, contactsLoading
}>(props => {
  const {
    title, contacts, onChangeHandler, disabledHandler, searchString, selectedContacts, relatedContacts, contactsLoading
  } = props;

  return (
    <>
      {contacts.length > 0 && (
        <EnrolItemListRenderer
          type="contact"
          showFirst={100}
          title={title}
          items={contacts}
          onChangeHandler={onChangeHandler}
          disabledHandler={disabledHandler}
          primaryText={item => (item.type && item.type === "create"
              ? item.name
              : searchString && getHighlightedPartLabel(getContactFullName(item), searchString))}
          secondaryText={item => (
            <Typography component="div" variant="caption" color="textSecondary" noWrap>
              {` ${item.birthDate ? format(new Date(item.birthDate), D_MMM_YYYY) : ""}  ${item.email || ""}`}
            </Typography>
          )}
          selectedItems={selectedContacts}
          itemsLoading={contactsLoading}
        />
      )}
      {relatedContacts.length > 0 && (
        <EnrolItemListRenderer
          type="contact"
          showFirst={100}
          title={`${relatedContacts.length > 1 ? "Related Contacts" : "Related Contact"}`}
          items={relatedContacts}
          onChangeHandler={onChangeHandler}
          disabledHandler={disabledHandler}
          primaryText={item => (searchString ? getHighlightedPartLabel(getContactFullName(item), searchString) : getContactFullName(item))}
          secondaryText={item => (
            <Typography component="div">
              <Typography component="span" variant="caption" color="textSecondary" noWrap>
                {` ${item.birthDate ? format(new Date(item.birthDate), D_MMM_YYYY) : ""}  ${item.email || ""}`}
              </Typography>
              <Typography component="p" variant="caption" color="textSecondary" noWrap>
                {` ${item.relationString}`}
              </Typography>
            </Typography>
          )}
          selectedItems={selectedContacts}
          itemsLoading={contactsLoading}
        />
      )}
    </>
  );
});

export default EnrolContactListView;
