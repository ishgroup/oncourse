/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import Grid from '@mui/material/Grid';
import $t from '@t';
import * as React from 'react';
import { Form, reduxForm } from 'redux-form';
import FormField from '../../common/components/form/formFields/FormField';
import AppBarContainer from '../../common/components/layout/AppBarContainer';
import LoadingIndicator from '../../common/components/progress/LoadingIndicator';
import { onSubmitFail } from '../../common/utils/highlightFormErrors';
import { useAppDispatch } from '../../common/utils/hooks';
import { downloadLogs } from './actions';

export const LOGS_FORM_NAME = "DownloadLogsForm";

const Logs = ({
  invalid,
  handleSubmit
}) => {
  
  const dispatch = useAppDispatch();

  const onSave = dates => {
    dispatch(downloadLogs(dates));
  };
  
  return (
    <Form className="container" onSubmit={handleSubmit(onSave)} role={LOGS_FORM_NAME} >
      <LoadingIndicator />
      <AppBarContainer
        hideHelpMenu
        disableInteraction
        disabled={invalid}
        submitButtonText="Download"
        title={$t('download_logs')}
      >
        <Grid container rowSpacing={2} columnSpacing={3} className="mt-2">
          <Grid item xs={4}>
            <FormField
              type="dateTime"
              label={$t('from')}
              name="from"
              required
            />
          </Grid>
          <Grid item xs={4}>
            <FormField
              type="dateTime"
              label={$t('to')}
              name="to"
            />
          </Grid>
        </Grid>
      </AppBarContainer>
    </Form>
  );
};

const LogsForm = reduxForm({
  onSubmitFail,
  form: LOGS_FORM_NAME
})(Logs);

export default LogsForm;