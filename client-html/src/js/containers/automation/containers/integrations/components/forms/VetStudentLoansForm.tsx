/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Button, Typography } from '@mui/material';
import $t from '@t';
import * as React from 'react';
import { connect } from 'react-redux';
import { getFormValues, initialize, reduxForm } from 'redux-form';
import FormField from '../../../../../../common/components/form/formFields/FormField';
import { onSubmitFail } from '../../../../../../common/utils/highlightFormErrors';
import { State } from '../../../../../../reducers/state';

class VetStudentLoans extends React.Component<any, any> {
  constructor(props) {
    super(props);
    // Initializing form with values
    props.dispatch(initialize("VetStudentLoansForm", props.item));
  }

  componentDidUpdate(prevProps) {
    if (prevProps.item.id !== this.props.item.id) {
      // Reinitializing form with values
      this.props.dispatch(initialize("VetStudentLoansForm", this.props.item));
    }
  }

  render() {
    const {
      AppBarContent,
      handleSubmit,
      onSubmit,
      values,
    } = this.props;

    const hasNameAndID = values && values.fields.deviceName && values.fields.organisationId;

    return values ? (
      <form onSubmit={handleSubmit(onSubmit)}>
        <AppBarContent>
          <FormField
            name="fields.deviceName"
            label={$t('device_name')}
            type="text"
            required
            disabled={values.id}
            className="mb-2"
          />
          <FormField
            name="fields.organisationId"
            label={$t('organisation_id')}
            type="text"
            required
            disabled={values.id}
            className="mb-2"
          />
          {!values.id && (
            <>
              <FormField
                name="fields.activationCode"
                label={$t('activation_code2')}
                type="text"
                required
                className="mb-2"
              />
              <div className="mt-1">
                <Button
                  href={`https://proda.humanservices.gov.au/piaweb/app/orgdel/orgs/${values.fields.organisationId}/devices/${values.fields.deviceName}`}
                  target="_blank"
                  variant="contained"
                  disabled={!hasNameAndID}
                >
                  {$t('get_activation_code')}
                </Button>
              </div>
            </>
            )}
          {!hasNameAndID && (
            <Typography variant="caption">
              {$t('please_fill_device_name_and_organisation_id_fields')}
            </Typography>
          )}
        </AppBarContent>
      </form>
    ) : null;
  }
}

const mapStateToProps = (state: State) => ({
  values: getFormValues("VetStudentLoansForm")(state)
});

export const VetStudentLoansForm = reduxForm({
  form: "VetStudentLoansForm",
  onSubmitFail
})(connect(mapStateToProps)(VetStudentLoans));
