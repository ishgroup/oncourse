/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { connect } from "react-redux";
import Grid from "@material-ui/core/Grid";
import { change } from "redux-form";
import { Contact, CorporatePass } from "@api/model";
import { Dispatch } from "redux";
import FormField from "../../../../common/components/form/form-fields/FormField";
import { clearContacts, getContacts, setContactsSearch } from "../../contacts/actions";
import { State } from "../../../../reducers/state";
import { getListNestedEditRecord } from "../../../../common/components/list-view/actions";
import { StringArgFunction, NumberArgFunction, AnyArgFunction } from "../../../../model/common/CommonFunctions";
import { contactLabelCondition, defaultContactName, openContactLink } from "../../contacts/utils";
import ContactSelectItemRenderer from "../../contacts/components/ContactSelectItemRenderer";
import { LinkAdornment } from "../../../../common/components/form/FieldAdornments";

interface Props {
  getContacts: NumberArgFunction;
  setContactsSearch: StringArgFunction;
  contacts: Contact[];
  contactsLoading: boolean;
  twoColumn: boolean;
  contactsRowsCount: number;
  form: string;
  manualLink: string;
  dispatch: any;
  values: CorporatePass;
  clearContacts?: AnyArgFunction;
}

class CorporatePassSettings extends React.PureComponent<Props, any> {
  onContactChange = (value: Contact) => {
    const { form, dispatch } = this.props;

    dispatch(change(form, "invoiceEmail", value.email));
    dispatch(change(form, "contactFullName", contactLabelCondition(value)));
  };

  render() {
    const {
      contacts,
      setContactsSearch,
      getContacts,
      contactsLoading,
      twoColumn,
      contactsRowsCount,
      values,
      clearContacts
    } = this.props;

    return (
      <>
        <Grid container className="pl-3 pr-3 pt-3">
          <Grid item xs={twoColumn ? 6 : 12}>
            <FormField
              type="remoteDataSearchSelect"
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
              items={contacts || []}
              onSearchChange={setContactsSearch}
              onLoadMoreRows={getContacts}
              onClearRows={clearContacts}
              loading={contactsLoading}
              remoteRowCount={contactsRowsCount}
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
      </>
    );
  }
}

const mapStateToProps = (state: State) => ({
  contacts: state.contacts.items,
  contactsSearch: state.contacts.search,
  contactsLoading: state.contacts.loading,
  contactsRowsCount: state.contacts.rowsCount
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  openNestedEditView: (entity: string, id: number) => dispatch(getListNestedEditRecord(entity, id)),
  getContacts: (offset?: number) => dispatch(getContacts(offset, null, true)),
  clearContacts: () => dispatch(clearContacts()),
  setContactsSearch: (search: string) => dispatch(setContactsSearch(search))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(CorporatePassSettings);
