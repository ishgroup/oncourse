import React, { useEffect } from "react";
import { connect } from "react-redux";
import {
 FieldArray, Form, getFormValues, initialize, InjectedFormProps, reduxForm
} from "redux-form";
import { Dispatch } from "redux";
import { GradingType } from "@api/model";
import withStyles from "@mui/styles/withStyles";
import AddIcon from "@mui/icons-material/Add";
import { Fab, Grid } from "@mui/material";
import { showConfirm } from "../../../../../common/actions";
import { deleteGradingType, updateGradingTypes } from "../../../actions";
import RouteChangeConfirm from "../../../../../common/components/dialog/confirm/RouteChangeConfirm";
import { onSubmitFail } from "../../../../../common/utils/highlightFormClassErrors";
import { State } from "../../../../../reducers/state";
import GradingsRenderer from "./GradingsRenderer";
import AppBarContainer from "../../../../../common/components/layout/AppBarContainer";
import { formCommonStyles } from "../../../styles/formCommonStyles";

export interface GradingFormData {
  types: GradingType[];
}

export interface GradingProps {
  values: GradingFormData;
  onSave: (types: GradingType[]) => void;
  gradingTypes: GradingType[];
  classes?: any;
}

export const FORM = "GradingForm";

const GradingTypes: React.FC<GradingProps & InjectedFormProps & { dispatch: Dispatch }> = props => {
  const {
    values,
    handleSubmit,
    dispatch,
    dirty,
    form,
    invalid,
    array,
    gradingTypes,
    classes,
  } = props;

  useEffect(() => {
    if (Array.isArray(gradingTypes)) {
      dispatch(initialize(FORM, { types: gradingTypes }));
    }
  }, [gradingTypes]);

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

  const onClickDelete = index => {
    const field = values.types[index];

    if (field.id) {
      return dispatch(showConfirm({
        onConfirm: () => {
          array.remove("types", index);
          dispatch(deleteGradingType(field.id));
        },
        confirmMessage: "Grading type will be deleted permanently",
        confirmButtonText: "Delete"
      }));
    }
    array.remove("types", index);
  };

  const onSave = values => {
    dispatch(updateGradingTypes(values.types));
  };

  return (
    <Form className="container" noValidate autoComplete="off" onSubmit={handleSubmit(onSave)}>
      <RouteChangeConfirm form={form} when={dirty} />

      <AppBarContainer
        values={values}
        disabled={!dirty}
        invalid={invalid}
        title="Grading types"
        disableInteraction
        classes={{ fullScreenTitleItem: classes.fullScreenTitleItem }}
        hideHelpMenu
      >
        <div className={classes.scriptAddMenu}>
          <Fab
            type="button"
            size="small"
            color="primary"
            classes={{
              sizeSmall: "appBarFab"
            }}
            onClick={() => onAddNew()}
          >
            <AddIcon />
          </Fab>
        </div>
        <Grid container className="mt-3">
          <FieldArray
            name="types"
            component={GradingsRenderer}
            onDelete={onClickDelete}
            dispatch={dispatch}
            rerenderOnEveryChange
          />
        </Grid>
      </AppBarContainer>
    </Form>
  );
};

const validate = (values: GradingFormData) => {
  const errors: any = {};
  const minMaxError = "Max value should be grater than min value";

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
    if ((t.entryType === "number" && (t.gradingItems.length > 0 && t.gradingItems.length < 2))
      || (t.entryType === "choice list" && t.gradingItems.length < 2)) {
      if (!Array.isArray(errors.types)) {
        errors.types = [];
      }
      errors.types[index] = {
        gradingItems: { _error: "At least two grading items required" },
        ...errors.types[index] || {}
      };
    }
  });

  return errors;
};

const mapStateToProps = (state: State) => ({
  values: getFormValues("GradingForm")(state)
});

const GradingTypesForm = reduxForm({
  onSubmitFail,
  validate,
  form: FORM,
  initialValues: {
    types: []
  }
})(connect(mapStateToProps)(withStyles(formCommonStyles)(GradingTypes)));

export default GradingTypesForm;
