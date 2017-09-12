import * as React from "react";
import {reduxForm} from "redux-form";
import {isNil} from "lodash";

import HeadingComp from "../../../components/HeadingComp";
import {toFormKey} from "../../../../components/form/FieldFactory";


class EnrolmentFields extends React.Component<any, any> {

  render() {
    const {headings, classId, selected, onUpdate, form, handleSubmit, submit} = this.props;

    const headingsComp = isNil(headings) ? [] : headings.map((h, index) => (
      <HeadingComp heading={h} key={index} touch={() => onUpdate(form)} />
    ));

    return (
      <div className="course-fields col-sm-24">
        {headings && selected &&
        <form onSubmit={handleSubmit} onBlur={() => onUpdate(form)}>
          {headingsComp}
        </form>

        }
      </div>
    );
  }
}

export const EnrolmentFieldsForm = reduxForm({
  validate: (data, props: any) => {
    const errors = {};

    if (props.headings && props.headings.length) {
      props.headings.map(headings =>
        headings.fields.map(field =>
          (field.mandatory && (field.dataType !== "BOOLEAN" || field.key === "isMale") && !data[toFormKey(field.key)])
            ? errors[toFormKey(field.key)] = `Field '${field.name}' is required`
            : field,
        ),
      );
    }

    return errors;
  },
})(EnrolmentFields);
