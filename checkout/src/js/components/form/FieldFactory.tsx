import * as React from "react";
import * as Form from "redux-form";
import {Field, DataType, Item} from "../../model";
import {CheckboxField} from "./CheckboxField";
import {TextField} from "./TextField";
import {TextAreaField} from "./TextAreaField";
import {DateField} from "./DateField";
import SelectField from "../form-new/SelectField";
import SearchService from "../../enrol/services/SearchService";
import {connect} from "react-redux";
import {validateDate, validateDateTime, validateEmail, validateURL} from "../../common/utils/FormControlsValidation";
import {replaceWithNl} from "../../common/utils/HtmlUtils";
import {DateTimeField} from "./DateTimeField";
import {MoneyField} from "./MoneyField";

class FieldFactory extends React.Component<any, any> {

  private getComponent = (field: Field): any => {
    const props: any = toFormFieldProps(field);
    props.onBlurSelect = this.props.onBlurSelect;
    props.onChangeSuburb = this.props.onChangeSuburb;

    switch (field.dataType) {
      case DataType.STRING:
      case DataType.PHONE:
      case DataType.POSTCODE:
        return <Form.Field {...props} component={TextField} type="text"/>;

      case DataType.EMAIL:
        return <Form.Field {...props} component={TextField} validate={validateEmail} type="text"/>;

      case DataType.URL:
        return <Form.Field {...props} component={TextField} validate={validateURL} type="text"/>;

      case DataType.LONG_STRING:
        return <Form.Field {...props} component={TextAreaField} type="text"/>;

      case DataType.INTEGER:
        return <Form.Field {...props} component={TextField} type="number"/>;

      case DataType.MONEY:
        return <Form.Field {...props} component={MoneyField} type="number"/>;

      case DataType.ENUM:
        return <Form.Field
          {...props}
          component={SelectField}
          loadOptions={() => Promise.resolve(field.enumItems)}
          newOptionEnable={false}
          searchable={false}
          returnType="object"
          placeholder="Please Select..."
        />;

      case DataType.BOOLEAN:
        return <Form.Field
          {...props}
          component={CheckboxField}
          />;
      case DataType.DATE:
        return <Form.Field {...props} component={DateField} validate={validateDate}/>;

      case DataType.DATETIME:
        return <Form.Field {...props} component={DateTimeField} validate={validateDateTime}/>;

      case DataType.COUNTRY:
        return CountryField(props);

      case DataType.SUBURB:
        return <SuburbField { ...props} form={this.props.form}/>;

      case DataType.LANGUAGE:
        return LanguageField(props);

      case DataType.CHOICE:
        return <Form.Field
          {...props}
          component={SelectField}
          loadOptions={() => Promise.resolve(withOptionOther(field.enumItems))}
          newOptionEnable={true}
          searchable={true}
          showOnFocus={true}
          placeholder="Please Select..."
        />;

      case DataType.TAGGROUP_S:
        return <Form.Field
          {...props}
          hint={props.label}
          component={SelectField}
          loadOptions={() => Promise.resolve(withOptionNotSet(field.enumItems))}
          newOptionEnable={true}
          searchable={true}
          showOnFocus={true}
          fullWidth={true}
        />;

      case DataType.TAGGROUP_M:
        return <Form.Field
          {...props}
          hint={props.label}
          component={CheckboxField}
        />;

      case DataType.MAILINGLIST:
        return <Form.Field
          {...props}
          hint={props.label}
          required={false}
          component={CheckboxField}
        />;

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
    name: toFormKey(field.key),
    label: replaceWithNl(field.name),
    type: "text",
    required: field.mandatory,
    placeholder: replaceWithNl(field.description),
  };
};

// replace all dots to slashes, b/c redux form converts it to object
export const toFormKey = name => (name.replace(/\./g, '/'));


const SuburbFieldBase = (props): any => {
  const suburbs = (i: string): Promise<Item[]> => {
    return SearchService.getPreparedSuburbs(i);
  };
  const updateRelativeFields = value => {
    props.onChangeSuburb && props.onChangeSuburb(value);
  };

  const isAustralia = props.values && (!props.values["country"] || props.values["country"] === "Australia");

  return isAustralia ?
    <Form.Field
      {...props}
      component={SelectField}
      loadOptions={suburbs}
      newOptionEnable={true}
      allowEditSelected={true}
      returnType="object"
      onChange={val => updateRelativeFields(val)}
    /> :
    <Form.Field {...props} component={TextField} type="text"/> ;
};

const SuburbField =  connect((state,{form}) => ({
  values: Form.getFormValues(form)(state),
}),
)(SuburbFieldBase);

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

const withOptionOther = items => {
  return items.concat({
    key: null,
    value: 'Other',
  });
};

const withOptionNotSet = items => {
  return items.concat({
    key: null,
    value: "Not set",
  });
};

export default FieldFactory;
