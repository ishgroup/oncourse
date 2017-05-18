import * as React from "react";
import * as Form from "redux-form";

import {Field} from "../../model/field/Field";
import {DataType} from "../../model/field/DataType";

import SelectField from "./SelectField";
import Checkbox from "./Checkbox";
import TextField from "./TextField";
import RadioGroup from "./RadioGroup";


class FieldFactory {

  public getComponent = (field: Field): any => {
    const props: any = this.propsFrom(field);
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
        return React.createElement(Form.Field,
          Object.assign({}, props, {component: SelectField, items: field.enumItems}));
      case DataType.boolean:
        if (field.key === "isMale") {
          return React.createElement(Form.Field,
            Object.assign({}, props, {component: RadioGroup, items: [{key: "true", value: "Male"}, {key: "false", value: "Female"}]}));
        } else {
          return React.createElement(Form.Field,
            Object.assign({}, props, {component: Checkbox}));
        }
      case DataType.date:
        return React.createElement(Form.Field, Object.assign({}, props, {component: TextField}));
      default:
        return null;
    }
  };

  private propsFrom = (field: Field): any => {
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