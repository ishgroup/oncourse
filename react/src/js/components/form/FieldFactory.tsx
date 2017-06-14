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
import {Item} from "../../model/common/Item";
import SearchService from "../../enrol/services/SearchService";


class FieldFactory extends React.Component<any, any> {

  private propsFrom = (field: Field): any => {
    return {
      key: field.id,
      name: field.key,
      label: field.name,
      type: "text",
      required: field.mandatory,
      placeholder: field.description
    }
  };

  private getComponent = (field: Field): any => {
    const props: any = this.propsFrom(field);
    switch (field.dataType) {
      case DataType.STRING:
      case DataType.LANGUAGE:
      case DataType.POSTCODE:
      case DataType.PHONE:
        return <Form.Field {...props} component={TextField} type="text"/>;

      case DataType.INTEGER:
        return <Form.Field {...props} component={TextField} type="number"/>;

      case DataType.ENUM:
        return <ComboboxField {...props} items={field.enumItems}/>;

      case DataType.BOOLEAN:
        if (field.key === "isMale") {
          return <Form.Field
            {...props}
            component={RadioGroupField}
            items={[{key: "true", value: "Male"}, {key: "false", value: "Female"}]}
          />;
        } else {
          return <Form.Field
            {...props}
            component={CheckboxField}
          />;
        }
      case DataType.DATE:
        return <Form.Field {...props} component={DateField}/>;

      case DataType.COUNTRY:
        return CountryField(props);

      case DataType.SUBURB:
        return SuburbField(props);
      default:
        return null;
    }
  };

  render() {
    return (
      <div>
        {this.getComponent(this.props.field)}
      </div>
    )
  }
}

const SuburbField = (props): any => {
  const suburbs = (i: string): Promise<Item[]> => {
    return SearchService.getPreparedSuburbs(i);
  };
  return <Form.Field
    {...props}
    component={SelectField}
    loadOptions={suburbs}
    required={true}
    newOptionEnable={true}
  />;
};

const CountryField = (props): any => {
  const countries = (i: string): Promise<Item[]> => {
    return SearchService.getCountries(i);
  };
  return <Form.Field
    {...props}
    component={SelectField}
    loadOptions={countries}
    required={true}
  />
};

export default FieldFactory;