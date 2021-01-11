/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Typography } from "@material-ui/core";
import Button from "@material-ui/core/Button";
import * as React from "react";
import { connect } from "react-redux";
import {
 getFormValues, initialize, reduxForm
} from "redux-form";
import FormField from "../../../../../../common/components/form/form-fields/FormField";
import CustomAppBar from "../../../../../../common/components/layout/CustomAppBar";
import RouteChangeConfirm from "../../../../../../common/components/dialog/confirm/RouteChangeConfirm";
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
      appBarContent,
      dirty,
      handleSubmit,
      onSubmit,
      values,
      form
    } = this.props;

    const hasNameAndID = values && values.fields.deviceName && values.fields.organisationId;

    return values ? (
      <form onSubmit={handleSubmit(onSubmit)}>
        {dirty && <RouteChangeConfirm form={form} when={dirty} />}
        <CustomAppBar>{appBarContent}</CustomAppBar>
        <FormField
          name="fields.deviceName"
          label="Device name"
          type="text"
          required
          disabled={values.id}
          fullWidth
        />
        <FormField
          name="fields.organisationId"
          label="Organisation ID"
          type="text"
          required
          disabled={values.id}
          fullWidth
        />
        {!values.id && (
          <>
            <FormField
              name="fields.activationCode"
              label="Activation Code"
              type="text"
              required
              fullWidth
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
        {!hasNameAndID
          && <Typography variant="caption">Please fill Device name and Organisation ID fields to be able to get activation code</Typography>}
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
