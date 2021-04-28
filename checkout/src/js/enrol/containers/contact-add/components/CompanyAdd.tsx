import * as React from "react";
import {Field} from "redux-form";
import {TextField} from "../../../../components/form/TextField";
import {replaceWithNl} from "../../../../common/utils/HtmlUtils";

export interface Props {
  header?: string;
}

export class CompanyAdd extends React.Component<Props, any> {
  render() {
    const {header = "Enter the business to be invoiced and paying for this transaction."} = this.props;
    return (
      <div id="addstudent-block">
        <div className="message">
          <p style={{whiteSpace: "pre-line"}}>{replaceWithNl(header)}</p>
        </div>
        <fieldset>
          <br/>
          <Field
             component={TextField}
             autocomplete="family-name"
             name="lastName"
             label="Company name"
             type="text"
             required
          />
          <Field
             component={TextField}
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
      </div>);
  }
}
