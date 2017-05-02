import * as React from "react";
import {TextField} from "../form/TextField";
import {Field, FormProps} from "redux-form";
import {CreateContactParams} from "../../../model/web/CreateContactParams";

export const AddPayerComponent = (props: AddPayerComponentProps) => {
  const {submitAddPayer, values, errors, pristine, invalid, submitting} = props;

  return (
    <div>
      {showFormErrors(errors)}

      <div id="addstudent-block">
        <h2>Add a payer</h2>
        <div className="message">
          <p>To continue, please enter the details of a business which will pay the invoice.</p>
        </div>

        <form onSubmit={(e) => {
          e.preventDefault();
          submitAddPayer(values);
        }}>
          <div className="t-invisible">
            <input value="" name="t:formdata" type="hidden"/>
          </div>
          <fieldset>
            <br/>
            <Field component={TextField}
                   autocomplete="business-name"
                   name="businessName"
                   label="Business name"
                   type="text"
                   required
            />
            <Field component={TextField}
                   autocomplete="email"
                   name="email"
                   label="Email"
                   type="text"
                   required
            />
          </fieldset>
          <div className="form-controls">
            <input value="Cancel"
                   className="btn btn-primary"
                   name="submitContact"
                   type="submit"
                   disabled={invalid || pristine || submitting}
            />
            <input value="OK"
                   className="btn btn-primary"
                   name="submitContact"
                   type="submit"
                   disabled={invalid || pristine || submitting}
            />
          </div>
        </form>
      </div>
    </div>
  )
};

function showFormErrors(errors = []) {
  if (!errors.length) {
    return null;
  }

  return (
    <div className="validation">
      <ul>
        {errors.map((error, idx) => (
          <li key={idx}>{error}</li>
        ))}
      </ul>
    </div>
  );
}

export interface AddPayerComponentProps extends FormProps<AddPayerFormFields, AddPayerComponentProps, {}> {
  errors?: string[];
  values?: CreateContactParams;
  submitAddPayer?: (params: CreateContactParams) => void;
}

export interface AddPayerFormFields {
  businessName?: string;
  email?: string;
}
