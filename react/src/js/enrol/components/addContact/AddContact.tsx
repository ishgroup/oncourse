import * as React from "react";
import {TextField} from "../form/TextField";
import {Field, FormProps} from "redux-form";
import {CreateContactParams} from "../../../model/web/CreateContactParams";

export const AddContactComponent = (props: AddContactComponentProps) => {
  const {submitAddContact, values, errors, pristine, invalid, submitting} = props;

  return (
    <div>
      {showFormErrors(errors)}

      <div id="addstudent-block">
        <h2>Add a student</h2>
        <div className="message">
          <p>Please enter the details of a person enrolling, applying or making a purchase.</p>
        </div>

        <form onSubmit={(e) => {
          e.preventDefault();
          submitAddContact(values);
        }}>
          <div className="t-invisible">
            <input value="" name="t:formdata" type="hidden"/>
          </div>
          <fieldset>
            <br/>
            <Field component={TextField}
                   autocomplete="given-name"
                   name="firstName"
                   label="First name"
                   type="text"
                   required
            />
            <Field component={TextField}
                   autocomplete="family-name"
                   name="lastName"
                   label="Last name"
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
          <p className="note">
            <strong className="alert">Note</strong>: If you have been here before, please try to use the same email
            address.
          </p>
          <div className="form-controls">
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

export interface AddContactComponentProps extends FormProps<AddContactFormFields, AddContactComponentProps, {}> {
  errors?: string[];
  values?: CreateContactParams;
  submitAddContact?: (params: CreateContactParams) => void;
}

export interface AddContactFormFields {
  firstName?: string;
  lastName?: string;
  email?: string;
}
