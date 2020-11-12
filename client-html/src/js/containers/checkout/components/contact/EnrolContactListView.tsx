/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import Typography from "@material-ui/core/Typography";
import { format } from "date-fns";
import { D_MMM_YYYY } from "../../../../common/utils/dates/format";
import { getHighlightedPartLabel } from "../../../../common/utils/formatting";
import { getContactName } from "../../../entities/contacts/utils";
import EnrolItemListRenderer from "../items/components/EnrolItemListRenderer";

const EnrolContactListView = React.memo<any>(props => {
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
              : searchString && getHighlightedPartLabel(getContactName(item), searchString))}
          secondaryText={item => (
            <Typography component="span" variant="caption" color="textSecondary" className="text-truncate">
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
          primaryText={item => (searchString ? getHighlightedPartLabel(getContactName(item), searchString) : getContactName(item))}
          secondaryText={item => (
            <Typography component="span">
              <Typography component="span" variant="caption" color="textSecondary" className="text-truncate">
                {` ${item.birthDate ? format(new Date(item.birthDate), D_MMM_YYYY) : ""}  ${item.email || ""}`}
              </Typography>
              <Typography component="p" variant="caption" color="textSecondary" className="text-truncate">
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
