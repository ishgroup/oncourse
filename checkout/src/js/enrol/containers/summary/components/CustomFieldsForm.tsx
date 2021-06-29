import * as React from "react";
import {connect} from 'react-redux';
import {reduxForm, Form} from "redux-form";
import {isNil} from "lodash";
import HeadingComp from "../../../components/HeadingComp";
import {toFormKey} from "../../../../components/form/FieldFactory";
import {IshState} from "../../../../services/IshState";

class CustomFields extends React.Component<any, any> {
  getFormErrors() {
    const {forms, form, submitFailed} = this.props;

    if (forms && forms[form]) {
      const formErrors = forms[form].syncErrors;

      if (formErrors && submitFailed) {
        const errors = Object.values(formErrors);

        return errors.length > 0 && (
          <div className="validation">
            <ul>
              {errors.map((er, i) => (
                <li key={i}>
                  {er}
                </li>
              ))}
            </ul>
          </div>
        );
      }
    }

    return null;
  }

  render() {
    const {headings, selected, onUpdate, form, handleSubmit, dispatch} = this.props;

    const headingsComp = isNil(headings) ? [] : headings.map((h, index) => (
      <HeadingComp heading={h} key={index} touch={() => onUpdate(form)} form={form} dispatch={dispatch}/>
    ));

    return (
      <div className="course-fields col-sm-24">
        {headings && selected &&
        <Form
          onSubmit={handleSubmit}
          onBlur={() => onUpdate(form)}
        >
          {this.getFormErrors()}
          {headingsComp}
        </Form>
        }
      </div>
    );
  }
}

const CustomFieldsForm = reduxForm({
  destroyOnUnmount: false,
  validate: (data, props: any) => {
    const errors = {};

    if (!props.selected) return errors;

    if (props.headings && props.headings.length) {
      props.headings.map(headings =>
        headings.fields.map((field, index) =>
          (field.mandatory && (field.dataType !== "BOOLEAN" || field.key === "isMale") && !data[toFormKey(field.key) + index])
            ? errors[toFormKey(field.key) + index] = `Field '${field.name}' is required`
            : field,
        ),
      );
    }
    return errors;
  },
})(CustomFields);

export default connect<any, any, any, IshState>((state: any) => ({forms: state.form}))(CustomFieldsForm) as any;
