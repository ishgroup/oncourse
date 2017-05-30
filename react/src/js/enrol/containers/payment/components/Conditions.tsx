import * as React from "react";
import {Field} from "redux-form";
import Any = jasmine.Any;

export class Conditions extends React.Component<any, any> {
  render() {
    return (
      <div className="clearfix payment-conditions">
        <div>
          <label>Conditions<em title="This field is required">*</em></label>
        </div>
        <div className="conditions">
            <span className="valid">
                <Field name="userAgreed" component="input" type="checkbox" value="1"/>
                <div className="conditions-text">
                    I understand the <a target="_blank" href="/terms-conditions">enrolment, sale and refund policy</a>.
                </div>
                <span className="validate-text"/>
            </span>
        </div>
      </div>
    )
  }
}