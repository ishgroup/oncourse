import * as React from "react";
import * as Form from "redux-form";
import {Field, DataType, Item, Suburb} from "../../model";
import {CheckboxField} from "./CheckboxField";
import {TextField} from "./TextField";
import {TextAreaField} from "./TextAreaField";
import {DateField} from "./DateField";
import SelectField from "../form-new/SelectField";
import SearchService from "../../enrol/services/SearchService";
import {connect} from "react-redux";
import {
  validateDate,
  validateDateTime,
  validateEmail,
  validatePattern,
  validateURL
} from "../../common/utils/FormControlsValidation";
import {replaceWithNl} from "../../common/utils/HtmlUtils";
import {DateTimeField} from "./DateTimeField";
import {MoneyField} from "./MoneyField";
import moment from "moment";

class FieldFactory extends React.Component<any, any> {

  private getComponent = (field: Field): any => {
    const props: any = toFormFieldProps(field);
    props.onBlurSelect = this.props.onBlurSelect;
    props.onChangeSuburb = this.props.onChangeSuburb;

    switch (field.dataType) {
      case DataType.STRING:
      case DataType.PHONE:
        return <Form.Field {...props} component={TextField} type="text" autocomplete="tel"/>;
      case DataType.POSTCODE:
        return <Form.Field {...props} component={TextField} type="text" autocomplete="postal-code"/>;

      case DataType.PATTERN_TEXT:
        return <Form.Field
          {...props}
          component={TextField}
          validate={val => validatePattern(val,props.pattern)}
          type="text"
        />;

      case DataType.EMAIL:
        return <Form.Field {...props} component={TextField} validate={validateEmail} autocomplete="email" type="text"/>;

      case DataType.URL:
        return <Form.Field {...props} component={TextField} validate={validateURL} autocomplete="url" type="text"/>;

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
    pattern: field.pattern,
    placeholder: replaceWithNl(field.description),
  };
};

// replace all dots to slashes, b/c redux form converts it to object
export const toFormKey = name => (name.replace(/\./g, '/'));

// format fields values to valid for server side
export const toServerValues = (fields: Field[], values: { [key: string]: any }) => {
  fields.forEach(f => {
    const formKey = toFormKey(f.key);
    const value = values[formKey];

    f.value = value && value.key || value || null;
    f.itemValue = null;

    switch (f.dataType) {
      case DataType.BOOLEAN: {
        if (f.value === null) {
          f.value = 'false';
        }
        break;
      }
      case DataType.SUBURB: {
        if (value && value.suburb) {
          const suburb: Suburb = {
            postcode: value.postcode,
            state: value.state,
            suburb: value.suburb,
          };

          f.value = null;
          f.itemValue = {key: value.key, value: suburb};
        }
        break;
      }
      case DataType.DATE: {
        if (value) {
          f.value = moment(value, "DD/MM/YYYY").format("YYYY-MM-DD");
        }
        break;
      }
      case DataType.DATETIME: {
        if (value) {
          f.value = moment(value, "DD/MM/YYYY hh:mm").toISOString();
        }
        break;
      }
      case DataType.MONEY: {
        if (value) {
          f.value = parseFloat(value.replace(/[$,]/g,""));
        }
        break;
      }
    }
  });
};


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
      autocomplete="street-address"
    /> :
    <Form.Field {...props} component={TextField} type="text"/> ;
};

const SuburbField =  connect<any,any,any>((state,{form}) => ({
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
    autocomplete="country-name"
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
