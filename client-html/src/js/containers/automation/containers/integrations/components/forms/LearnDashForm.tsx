/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import $t from '@t';
import React from 'react';
import { reduxForm } from 'redux-form';
import FormField from '../../../../../../common/components/form/formFields/FormField';
import { onSubmitFail } from '../../../../../../common/utils/highlightFormErrors';
import IntegrationFormBase from './IntegrationFormBase';

class LearnDashFormBase extends IntegrationFormBase {
  render() {
    const {
      handleSubmit, onSubmit, AppBarContent
    } = this.props;

    return (
      <form onSubmit={handleSubmit(onSubmit)}>
        <AppBarContent>
          <FormField type="text" name="fields.baseUrl" label={$t('base_url2')} className="mb-2" />
          <FormField type="text" name="fields.userLogin" label={$t('user_login')} className="mb-2" />
          <FormField
            type="password"
            name="fields.userPassword"
            label={$t('user_password')}
            className="mb-2"
          />
        </AppBarContent>
      </form>
    );
  }
}

export const LearnDashForm = reduxForm({
  onSubmitFail
})(LearnDashFormBase);
