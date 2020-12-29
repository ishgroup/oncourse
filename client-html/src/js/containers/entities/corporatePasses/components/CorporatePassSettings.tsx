/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import Grid from "@material-ui/core/Grid";
import { change } from "redux-form";
import { Contact } from "@api/model";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { contactLabelCondition, defaultContactName, openContactLink } from "../../contacts/utils";
import ContactSelectItemRenderer from "../../contacts/components/ContactSelectItemRenderer";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";
import { EditViewProps } from "../../../../model/common/ListView";

class CorporatePassSettings extends React.PureComponent<EditViewProps, any> {
  onContactChange = (value: Contact) => {
    const { form, dispatch } = this.props;
    dispatch(change(form, "invoiceEmail", value.email));
    dispatch(change(form, "contactFullName", contactLabelCondition(value)));
  };

  render() {
    const {
      twoColumn,
      values
    } = this.props;

    return (
      <Grid container className="pl-3 pr-3 pt-3">
        <Grid item xs={twoColumn ? 6 : 12}>
          <FormField
            type="remoteDataSearchSelect"
            entity="Contact"
            name="contactId"
            label="Contact (company or person to invoice)"
            selectValueMark="id"
            selectLabelCondition={contactLabelCondition}
            defaultDisplayValue={values && defaultContactName(values.contactFullName)}
            labelAdornment={(
              <LinkAdornment
                linkHandler={openContactLink}
                link={values && values.contactId}
                disabled={!values || !values.contactId}
              />
            )}
            onInnerValueChange={this.onContactChange}
            itemRenderer={ContactSelectItemRenderer}
            rowHeight={55}
            required
          />
        </Grid>

        <Grid item xs={12}>
          <FormField
            type="text"
            name="password"
            label="Password"
            required
          />
        </Grid>
        <Grid item xs={12}>
          <FormField type="text" name="invoiceEmail" label="Email invoice to" />
        </Grid>
        <Grid item xs={12}>
          <FormField type="date" name="expiryDate" label="Expire on" />
        </Grid>
      </Grid>
    );
  }
}

export default CorporatePassSettings;
