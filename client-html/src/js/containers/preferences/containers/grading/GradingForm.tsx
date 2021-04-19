/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import { connect } from "react-redux";
import {
  FieldArray,
  Form,
  getFormValues,
  InjectedFormProps,
  reduxForm
} from "redux-form";
import { GradingType } from "@api/model";
import { Grid, Fab, Typography } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import { Dispatch } from "redux";
import { State } from "../../../../reducers/state";
import { Fetch } from "../../../../model/common/Fetch";
import { ShowConfirmCaller } from "../../../../model/common/Confirm";
import { onSubmitFail } from "../../../../common/utils/highlightFormClassErrors";
import RouteChangeConfirm from "../../../../common/components/dialog/confirm/RouteChangeConfirm";
import CustomAppBar from "../../../../common/components/layout/CustomAppBar";
import FormSubmitButton from "../../../../common/components/form/FormSubmitButton";
import GradingsRenderer from "./components/GradingsRenderer";

export interface GradingFormData {
  types: GradingType[];
}

export interface GradingProps {
  values: GradingFormData;
  gradingTypes: GradingType[];
  fetch: Fetch;
  showConfirm: ShowConfirmCaller;
  onSave: (types: GradingType[]) => void;
}

const GradingForm: React.FC<GradingProps & InjectedFormProps & { dispatch: Dispatch }> = (
  {
    values,
    gradingTypes,
    fetch,
    showConfirm,
    handleSubmit,
    dispatch,
    dirty,
    form,
    invalid,
    array
  }
) => {
  const onAddNew = () => {
    const item: GradingType = {
      id: null,
      created: null,
      modified: null,
      name: null,
      minValue: null,
      maxValue: null,
      entryType: "number",
      gradingItems: []
    };
    array.insert("types", 0, item);
    const domNode = document.getElementById("types[0].name");
    if (domNode) domNode.scrollIntoView({ behavior: "smooth" });
  };

  const onClickDelete = (item, index) => {

  };

  const onSave = values => {

  };

  return (
    <Form className="container" noValidate autoComplete="off" onSubmit={handleSubmit(onSave)}>
      <RouteChangeConfirm form={form} when={dirty} />

      <CustomAppBar>
        <Grid container>
          <Grid item xs={12} className="centeredFlex relative">
            <Fab
              type="button"
              size="small"
              color="primary"
              classes={{
              sizeSmall: "appBarFab"
            }}
              onClick={onAddNew}
            >
              <AddIcon />
            </Fab>
            <Typography className="appHeaderFontSize pl-2" variant="body1" color="inherit" noWrap>
              Grading types
            </Typography>

            <div className="flex-fill" />

            {/* {values && ( */}
            {/*  <AppBarHelpMenu */}
            {/*    created={created} */}
            {/*    modified={modified} */}
            {/*    auditsUrl={`audit?search=~"EntityRelationType" and entityId in (${idsToString(data.types)})`} */}
            {/*    manualUrl={manualLink} */}
            {/*  /> */}
            {/* )} */}

            <FormSubmitButton
              disabled={!dirty}
              invalid={invalid}
            />
          </Grid>
        </Grid>
      </CustomAppBar>

      <Grid container className="mt-3">
        <FieldArray
          name="types"
          component={GradingsRenderer}
          onDelete={onClickDelete}
        />
      </Grid>
    </Form>
);
};

const validate = (values: GradingFormData) => {
  const errors: any = {};
  const minMaxError = "Max value should be grater than min value";
  const graidingItemError = "At least one grading item is required";

  values?.types?.forEach((t, index) => {
    if (typeof t.minValue === "number" && typeof t.maxValue === "number" && t.minValue >= t.maxValue) {
      if (!Array.isArray(errors.types)) {
        errors.types = [];
      }
      errors.types[index] = {
        minValue: minMaxError,
        maxValue: minMaxError,
        ...errors.types[index] || {}
      };
    }
    if (t.entryType === "name" && !t.gradingItems.length) {
      if (!Array.isArray(errors.types)) {
        errors.types = [];
      }
      errors.types[index] = {
        gradingItems: { _error: graidingItemError },
        ...errors.types[index] || {}
      };
    }
  });

  return errors;
};

const mapStateToProps = (state: State) => ({
  values: getFormValues("GradingForm")(state),
  gradingTypes: state.preferences.gradingTypes,
  fetch: state.fetch
});

export default reduxForm({
  onSubmitFail,
  validate,
  form: "GradingForm",
  initialValues: {
    types: []
  }
})(connect(mapStateToProps)(GradingForm));

