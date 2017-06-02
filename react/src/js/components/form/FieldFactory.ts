import * as React from "react";
import * as Form from "redux-form";

import {Field} from "../../model/field/Field";
import {DataType} from "../../model/field/DataType";

import {ComboboxField} from "./ComboboxField";
import {CheckboxField} from "./CheckboxField";
import {TextField} from "./TextField";
import {RadioGroupField} from "./RadioGroupField";
import {DateField} from "./DateField";
import SelectField from "../form-new/SelectField";
import {Injector} from "../../injector";
import {Item} from "../../model/common/Item";
import {SuburbOption, SuburbValue} from "./Renders";
import {Suburb} from "../../model/field/Suburb";

const {
  searchApi
} = Injector.of();


class FieldFactory {

  public getComponent = (field: Field): any => {
    const props: any = this.propsFrom(field);
    switch (field.dataType) {
      case DataType.STRING:
      case DataType.LANGUAGE:
      case DataType.SUBURB:
      case DataType.COUNTRY:
        return React.createElement(Form.Field,
          Object.assign({}, props, {type: "string", component: TextField}));
      case DataType.INTEGER:
        return React.createElement(Form.Field,
          Object.assign({}, props, {type: "number", component: TextField}));
      case DataType.ENUM:
        return React.createElement(ComboboxField,
          Object.assign({}, props, {items: field.enumItems}));
      case DataType.BOOLEAN:
        if (field.key === "isMale") {
          return React.createElement(Form.Field,
            Object.assign({}, props, {
              component: RadioGroupField,
              items: [{key: "true", value: "Male"}, {key: "false", value: "Female"}]
            }));
        } else {
          return React.createElement(Form.Field,
            Object.assign({}, props, {component: CheckboxField}));
        }
      case DataType.DATE:
        return React.createElement(Form.Field, Object.assign({}, props, {component: DateField}));
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

const SuburbField = (props): any => {
  const suburbs = (i: string): Promise<Item[]> => {
    return searchApi.getSuburbs(i);
  };
  return React.createElement(Form.Field, Object.assign({}, props,
    {
      component: SelectField,
      loadOptions: suburbs,
      valueComponent: SuburbValue,
      optionComponent: SuburbOption,
      filterOption: (option, filter) => {
        return filter ? option.value.suburb.toLowerCase().startsWith(filter) : true;
      },
      newOptionCreator: (newOption: { label: string, labelKey: string, valueKey: string }) => {
        const item:Item = new Item();
        item.key = newOption.label;
        item.value = new Suburb();
        item.value.suburb = newOption.label;
        return item;
      }
    }));
};

const CountryField = (props): any => {
  const countries = (i: string): Promise<Item[]> => {
    return searchApi.getCountries(i);
  };
  return React.createElement(Form.Field, Object.assign({}, props, {
    component: SelectField,
    loadOptions: countries
  }));
};

export default FieldFactory;