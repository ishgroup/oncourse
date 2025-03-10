/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import $t from '@t';
import * as React from 'react';
import { connect } from 'react-redux';
import { getFormValues, initialize, reduxForm } from 'redux-form';
import RouteChangeConfirm from '../../../../../../common/components/dialog/RouteChangeConfirm';
import FormField from '../../../../../../common/components/form/formFields/FormField';
import { onSubmitFail } from '../../../../../../common/utils/highlightFormErrors';
import { State } from '../../../../../../reducers/state';

class KronosBaseForm extends React.Component<any, any> {
  constructor(props) {
    super(props);

    // Initializing form with values
    props.dispatch(initialize("KronosForm", props.item));
  }

  componentDidUpdate(prevProps) {
    if (prevProps.item.id !== this.props.item.id) {
      // Reinitializing form with values
      this.props.dispatch(initialize("KronosForm", this.props.item));
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
          <FormField name="fields.username" label={$t('username')} type="text" className="mb-2" />
          <FormField name="fields.password" label={$t('password')} type="text" className="mb-2" />
          <FormField name="fields.apiKey" label={$t('api_key')} type="text" className="mb-2" />
          <FormField name="fields.companyShortName" label={$t('company_short_name')} type="text" className="mb-2" />
          <FormField name="fields.CID" label={$t('company_id')} type="text" className="mb-2" />
        </AppBarContent>
      </form>
    );
  }
}

const mapStateToProps = (state: State) => ({
  values: getFormValues("KronosForm")(state)
});

export const KronosForm = reduxForm({
  form: "KronosForm",
  onSubmitFail
})(connect<any, any, any>(mapStateToProps, null)(KronosBaseForm));

