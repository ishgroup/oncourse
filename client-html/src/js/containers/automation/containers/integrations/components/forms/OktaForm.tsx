/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import $t from '@t';
import * as React from 'react';
import { connect } from 'react-redux';
import { getFormValues, initialize, reduxForm } from 'redux-form';
import RouteChangeConfirm from '../../../../../../common/components/dialog/RouteChangeConfirm';
import FormField from '../../../../../../common/components/form/formFields/FormField';
import { onSubmitFail } from '../../../../../../common/utils/highlightFormErrors';
import { State } from '../../../../../../reducers/state';

class OktaBaseForm extends React.Component<any, any> {
  constructor(props) {
    super(props);

    // Initializing form with values
    props.dispatch(initialize("OktaForm", props.item));
  }

  componentDidUpdate(prevProps) {
    if (prevProps.item.id !== this.props.item.id) {
      // Reinitializing form with values
      this.props.dispatch(initialize("OktaForm", this.props.item));
    }
  }

  render() {
    const {
      handleSubmit, onSubmit, AppBarContent, dirty, form
    } = this.props;

    return (
      <form onSubmit={handleSubmit(onSubmit)}>
        {dirty && <RouteChangeConfirm form={form} when={dirty} />}
        <AppBarContent>
          <FormField name="fields.clientId" label={$t('client_id')} type="text" className="mb-2" />
          <FormField name="fields.clientSecret" label={$t('client_secret')} type="password" className="mb-2" />
          <FormField name="fields.appUrl"  label={$t('okta_app_url_in_form_httpsoktacom')} type="text" className="mb-2" />
          <FormField name="fields.webRedirect" disabled label={$t('signin_redirect_uri')} type="text" className="mb-2" />
        </AppBarContent>
      </form>
    );
  }
}

const mapStateToProps = (state: State) => ({
  values: getFormValues("OktaForm")(state)
});

export const OktaForm = reduxForm({
  form: "OktaForm",
  onSubmitFail
})(connect<any, any, any>(mapStateToProps, null)(OktaBaseForm));