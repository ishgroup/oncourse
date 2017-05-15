import * as React from "react";
import * as Form from "redux-form";

import {TextField} from "../../enrol/components/form/TextField"
import {Field} from "../../model/field/Field";



import {DataType} from "../../model/field/DataType";
import {ComboboxField} from "../components/form/ComboboxField";


class FieldFactory {

  public getComponent = (field: Field):any  => {
    switch(field.dataType) {
      case DataType.string:
        return React.createElement(Form.Field, {key: field.id, name: field.key, label: field.name, type: "string", required: field.mandatory, component: TextField});
      case DataType.integer:
        return React.createElement(Form.Field, {key: field.id, name: field.key, label: field.name, type: "number", required: field.mandatory, component: TextField});
      case DataType.enum:
        return React.createElement(ComboboxField, {key: field.id, name: field.key, label: field.name, items: field.enumItems, required: field.mandatory});
      default:
        return null;
    }

  }
}
export default FieldFactory;