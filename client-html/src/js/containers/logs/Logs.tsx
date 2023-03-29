/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import * as React from "react";
import Grid from "@mui/material/Grid";
import AppBarContainer from "../../common/components/layout/AppBarContainer";
import { Form, reduxForm } from "redux-form";
import { onSubmitFail } from "../../common/utils/highlightFormErrors";
import FormField from "../../common/components/form/formFields/FormField";
import { useAppDispatch } from "../../common/utils/hooks";
import { downloadLogs } from "./actions";
import LoadingIndicator from "../../common/components/progress/LoadingIndicator";

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
        title="Download logs"
      >
        <Grid container rowSpacing={2} columnSpacing={3} className="mt-2">
          <Grid item xs={4}>
            <FormField
              type="dateTime"
              label="From"
              name="from"
              required
            />
          </Grid>
          <Grid item xs={4}>
            <FormField
              type="dateTime"
              label="To"
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