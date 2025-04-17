/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import $t from '@t';
import * as React from 'react';
import { connect } from 'react-redux';
import { getFormValues, reduxForm } from 'redux-form';
import FormField from '../../../../../../common/components/form/formFields/FormField';
import { onSubmitFail } from '../../../../../../common/utils/highlightFormErrors';
import { State } from '../../../../../../reducers/state';
import IntegrationFormBase from './IntegrationFormBase';

class CoassembleBaseForm extends IntegrationFormBase {
  render() {
    const {
      handleSubmit, onSubmit, AppBarContent
    } = this.props;

    return (
      <form onSubmit={handleSubmit(onSubmit)}>
        <AppBarContent>
          <FormField name="fields.baseUrl" label={$t('base_url3')} type="text" className="mb-2" />
          <FormField name="fields.apiKey" label={$t('api_key')} type="text" className="mb-2" />
          <FormField name="fields.userId" label={$t('user_id')} type="text" className="mb-2" />
        </AppBarContent>
      </form>
    );
  }
}

const mapStateToProps = (state: State, ownProps) => ({
  values: getFormValues(ownProps.form)(state)
});

export const CoassembleForm = reduxForm({
  onSubmitFail,
})(connect(mapStateToProps)(CoassembleBaseForm));