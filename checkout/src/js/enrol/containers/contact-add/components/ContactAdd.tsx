import * as React from "react";
import {Field} from "redux-form";
import {TextField} from "../../../../components/form/TextField";
import {scrollToTop} from "../../../../common/utils/DomUtils";

export interface Props {
  header?: string;
}

export class ContactAdd extends React.Component<Props, any> {
  componentDidMount() {
    scrollToTop();
  }

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
                 name="firstName"
                 label="First name"
                 type="text"
                 autoFocus
                 required
          />
          <Field component={TextField}
                 autocomplete="off"
                 name="lastName"
                 label="Last name"
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
      </div>
    );
  }
}
