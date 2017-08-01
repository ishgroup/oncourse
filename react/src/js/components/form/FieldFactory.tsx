import * as React from "react";
import * as Form from "redux-form";

import {Field, DataType, Item} from "../../model";

import {ComboboxField} from "./ComboboxField";
import {CheckboxField} from "./CheckboxField";
import {TextField} from "./TextField";
import {RadioGroupField} from "./RadioGroupField";
import {DateField} from "./DateField";
import SelectField from "../form-new/SelectField";
import SearchService from "../../enrol/services/SearchService";


class FieldFactory extends React.Component<any, any> {

  private getComponent = (field: Field): any => {
    const props: any = toFormFieldProps(field);
    props.onBlurSelect = this.props.onBlurSelect;

    switch (field.dataType) {
      case DataType.STRING:
      case DataType.PHONE:
        return <Form.Field {...props} component={TextField} type="text"/>;

      case DataType.INTEGER:
        return <Form.Field {...props} component={TextField} type="number"/>;

      case DataType.ENUM:
        return <Form.Field
          {...props}
          component={SelectField}
          loadOptions={() => Promise.resolve(field.enumItems)}
          newOptionEnable={false}
          searchable={false}
          returnType="object"
          placeholder="Please Select..."
        />

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

      case DataType.LANGUAGE:
        return LanguageField(props);

      case DataType.POSTCODE:
        return PostcodeField(props);

      default:
        return null;
    }
  }

  render() {
    return (
      <div>
        {this.getComponent(this.props.field)}
      </div>
    );
  }
}

export const toFormFieldProps = (field: Field): any => {
  return {
    key: field.id,
    name: field.key,
    label: field.name,
    type: "text",
    required: field.mandatory,
    placeholder: field.description,
  };
};


const SuburbField = (props): any => {
  const suburbs = (i: string): Promise<Item[]> => {
    return SearchService.getPreparedSuburbs(i);
  };
  return <Form.Field
    {...props}
    component={SelectField}
    loadOptions={suburbs}
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
  />;
};

const LanguageField = (props): any => {
  const langs = (i: string): Promise<Item[]> => {
    return SearchService.getLanguages(i);
  };
  return <Form.Field
    {...props}
    component={SelectField}
    loadOptions={langs}
  />;
};

const PostcodeField = (props): any => {
  const codes = (i: string): Promise<Item[]> => {
    return SearchService.getPostcodes(i);
  };
  return <Form.Field
    {...props}
    component={SelectField}
    loadOptions={codes}
    newOptionEnable={true}
  />;
};

const withDefaultItem = items => {
  const defaultEnumItem = {
    key: -1,
    value: 'Please select...',
  };
  return [defaultEnumItem].concat(items);
}

export default FieldFactory;
