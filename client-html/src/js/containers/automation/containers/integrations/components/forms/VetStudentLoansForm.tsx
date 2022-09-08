/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Typography } from "@mui/material";
import Button from "@mui/material/Button";
import * as React from "react";
import { connect } from "react-redux";
import { getFormValues, initialize, reduxForm } from "redux-form";
import FormField from "../../../../../../common/components/form/formFields/FormField";
import { onSubmitFail } from "../../../../../../common/utils/highlightFormClassErrors";
import { State } from "../../../../../../reducers/state";

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
            label="Device name"
            type="text"
            required
            disabled={values.id}
            className="mb-2"
          />
          <FormField
            name="fields.organisationId"
            label="Organisation ID"
            type="text"
            required
            disabled={values.id}
            className="mb-2"
          />
          {!values.id && (
            <>
              <FormField
                name="fields.activationCode"
                label="Activation code"
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
                  Get activation code
                </Button>
              </div>
            </>
            )}
          {!hasNameAndID && (
            <Typography variant="caption">
              Please fill Device name and Organisation ID fields to be able to get activation code
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
