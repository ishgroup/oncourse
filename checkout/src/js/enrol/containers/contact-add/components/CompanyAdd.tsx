import * as React from "react";
import {Field} from "redux-form";
import {TextField} from "../../../../components/form/TextField";

export interface Props {
  header?: string;
}

export class CompanyAdd extends React.Component<Props, any> {
  render() {
    const {header = "Please enter the details of a person enrolling, applying or making a purchase."} = this.props;
    return (
      <div id="addstudent-block">
        <div className="message">
          <p>{header}</p>
        </div>
        <fieldset>
          <br/>
          <Field component={TextField}
                 autocomplete="off"
                 name="lastName"
                 label="Company name"
                 type="text"
                 required
          />
          <Field component={TextField}
                 autocomplete="on"
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
      </div>);
  }
}
