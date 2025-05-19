/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Contact } from '@api/model';
import Grid from '@mui/material/Grid';
import $t from '@t';
import * as React from 'react';
import { change } from 'redux-form';
import {
  ContactLinkAdornment,
  HeaderContactTitle
} from '../../../../common/components/form/formFields/FieldAdornments';
import FormField from '../../../../common/components/form/formFields/FormField';
import FullScreenStickyHeader
  from '../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader';
import { EditViewProps } from '../../../../model/common/ListView';
import ContactSelectItemRenderer from '../../contacts/components/ContactSelectItemRenderer';
import { getContactFullName } from '../../contacts/utils';

class CorporatePassSettings extends React.PureComponent<EditViewProps, any> {
  onContactChange = (value: Contact) => {
    const { form, dispatch } = this.props;
    dispatch(change(form, "invoiceEmail", value.email));
    dispatch(change(form, "contactFullName", getContactFullName(value)));
  };

  render() {
    const {
      twoColumn,
      values,
      isNew,
      syncErrors
    } = this.props;

    return (
      <Grid container columnSpacing={3} rowSpacing={2} className="pl-3 pr-3 pt-3">
        <Grid item xs={12}>
          <FullScreenStickyHeader
            opened={isNew || Object.keys(syncErrors).includes("contactId")}
            disableInteraction={!isNew}
            twoColumn={twoColumn}
            title={(
              <HeaderContactTitle name={values?.contactFullName} id={values?.contactId} />
            )}
            fields={(
              <Grid item xs={twoColumn ? 6 : 12}>
                <FormField
                  type="remoteDataSelect"
                  entity="Contact"
                  name="contactId"
                  label={$t('contact_company_or_person_to_invoice')}
                  selectValueMark="id"
                  selectLabelCondition={getContactFullName}
                  defaultValue={values && values.contactFullName}
                  labelAdornment={(
                    <ContactLinkAdornment id={values?.contactId} />
                  )}
                  onInnerValueChange={this.onContactChange}
                  itemRenderer={ContactSelectItemRenderer}
                  rowHeight={55}
                  required
                />
              </Grid>
            )}
          />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField
            type="text"
            name="password"
            label={$t('password')}
            required
          />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField type="text" name="invoiceEmail" label={$t('email_invoice_to')} />
        </Grid>
        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField type="date" name="expiryDate" label={$t('expire_on')} />
        </Grid>
      </Grid>
    );
  }
}

export default CorporatePassSettings;
