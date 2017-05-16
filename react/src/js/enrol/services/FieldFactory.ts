import * as React from "react";
import * as Form from "redux-form";

import {TextField} from "../../enrol/components/form/TextField";
import {Field} from "../../model/field/Field";


import {DataType} from "../../model/field/DataType";
import {ComboboxField} from "../components/form/ComboboxField";
import {CheckboxField} from "../components/form/CheckboxField";
import {DateField} from "../components/form/DateField";
import {RadioGroupField} from "../components/form/RadioGroupField";
import RadioGroupFields from "../../components/RadioGroupFields";


class FieldFactory {

  public getComponent = (field: Field): any => {
    const props: any = this.getProps(field);
    switch (field.dataType) {
      case DataType.string:
      case DataType.country:
      case DataType.language:
        return React.createElement(Form.Field,
          Object.assign({}, props, {type: "string", component: TextField}));
      case DataType.integer:
        return React.createElement(Form.Field,
          Object.assign({}, props, {type: "number", component: TextField}));
      case DataType.enum:
        return React.createElement(ComboboxField,
          Object.assign({}, props, {items: field.enumItems}));
      case DataType.boolean:
        //TODO:temporary solution we should think about how we should handle it
        if (field.key === "isMale") {
          return React.createElement(RadioGroupFields,
            Object.assign({}, props, {items: [{key: "true", value: "Male"}, {key: "false", value: "Female"}]}));
        } else {
          return React.createElement(Form.Field,
            Object.assign({}, props, {component: CheckboxField}));
        }
      case DataType.date:
        return React.createElement(Form.Field, Object.assign({}, props, {component: DateField}));
      default:
        return null;
    }
  };

  private getProps = (field: Field): any => {
    return {
      key: field.id,
      name: field.key,
      label: field.name,
      type: "string",
      required: field.mandatory,
      placeholder: field.description
    }
  }
}
export default FieldFactory;