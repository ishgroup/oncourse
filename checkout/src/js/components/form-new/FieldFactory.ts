import * as React from "react";
import * as Form from "redux-form";
import {Field, DataType} from "../../model";
import SelectField from "./SelectField";
import Checkbox from "./Checkbox";
import {TextField} from "./TextField";


class FieldFactory {

  public getComponent = (field: Field): any => {
    const props: any = this.propsFrom(field);
    switch (field.dataType) {
      case DataType.STRING:
      case DataType.COUNTRY:
      case DataType.LANGUAGE:
      case DataType.POSTCODE:
      case DataType.PHONE:
        return React.createElement(Form.Field,
          Object.assign({}, props, {type: "string", component: TextField}));
      case DataType.INTEGER:
        return React.createElement(Form.Field,
          Object.assign({}, props, {type: "number", component: TextField}));
      case DataType.ENUM:
        return React.createElement(Form.Field,
          Object.assign({}, props, {component: SelectField, items: field.enumItems}));
      case DataType.BOOLEAN:
        return React.createElement(Form.Field, Object.assign({}, props, {component: Checkbox}));
      case DataType.DATE:
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
      placeholder: field.description,
    };
  }
}

export default FieldFactory;
