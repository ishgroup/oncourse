/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import $t from '@t';
import React from 'react';
import { initialize, reduxForm } from 'redux-form';
import FormField from '../../../../../../common/components/form/formFields/FormField';
import { onSubmitFail } from '../../../../../../common/utils/highlightFormErrors';

class AmazonS3FormBase extends React.Component<any, any> {
  constructor(props) {
    super(props);
    // Initializing form with values
    props.dispatch(initialize("AmazonS3Form", props.item));
  }

  componentDidUpdate(prevProps) {
    if (prevProps.item.id !== this.props.item.id) {
      // Reinitializing form with values
      this.props.dispatch(initialize("AmazonS3Form", this.props.item));
    }
  }

  render() {
    const {
      handleSubmit, onSubmit, AppBarContent
    } = this.props;

    return (
      <form onSubmit={handleSubmit(onSubmit)}>
        <AppBarContent>
          <FormField type="text" name="fields.account" label={$t('account')} required className="mb-2" />
          <FormField type="text" name="fields.key" label={$t('key')} required className="mb-2" />
          <FormField type="text" name="fields.bucket" label={$t('bucket')} required className="mb-2" />
          <FormField type="text" name="fields.region" label={$t('region')} required className="mb-2" />
        </AppBarContent>
      </form>
    );
  }
}

export const AmazonS3Form = reduxForm({
  form: "AmazonS3Form",
  onSubmitFail
})(AmazonS3FormBase);
