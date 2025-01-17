import { GradingType } from '@api/model';
import Grid from '@mui/material/Grid';
import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { FieldArray, Form, getFormValues, initialize, InjectedFormProps, reduxForm } from 'redux-form';
import { showConfirm } from '../../../../../common/actions';
import { IAction } from '../../../../../common/actions/IshAction';
import RouteChangeConfirm from '../../../../../common/components/dialog/RouteChangeConfirm';
import AppBarContainer from '../../../../../common/components/layout/AppBarContainer';
import { onSubmitFail } from '../../../../../common/utils/highlightFormErrors';
import { State } from '../../../../../reducers/state';
import { deleteGradingType, updateGradingTypes } from '../../../actions';
import GradingsRenderer from './GradingsRenderer';

export interface GradingFormData {
  types: GradingType[];
}

export interface GradingProps {
  values: GradingFormData;
  onSave: (types: GradingType[]) => void;
  gradingTypes: GradingType[];
}

export const GRADING_FORM: string = "GradingForm";

const GradingTypes: React.FC<GradingProps & InjectedFormProps & { dispatch: Dispatch<IAction> }> = props => {
  const {
    values,
    handleSubmit,
    dispatch,
    dirty,
    form,
    invalid,
    array,
    gradingTypes,
  } = props;

  useEffect(() => {
    if (Array.isArray(gradingTypes)) {
      dispatch(initialize(GRADING_FORM, { types: gradingTypes }));
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

  const onSave = v => {
    dispatch(updateGradingTypes(v.types));
  };

  return (
    <Form className="container" noValidate autoComplete="off" onSubmit={handleSubmit(onSave)} role={GRADING_FORM}>
      <RouteChangeConfirm form={form} when={dirty} />

      <AppBarContainer
        values={values}
        disabled={!dirty}
        invalid={invalid}
        title="Grading types"
        disableInteraction
        hideHelpMenu
        onAddMenu={() => onAddNew()}
      >
        <Grid container className="mt-2">
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
  form: GRADING_FORM,
  initialValues: {
    types: []
  }
})(connect(mapStateToProps)(GradingTypes));

export default GradingTypesForm;
