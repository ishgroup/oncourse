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
    const {header = "Enter the person who will attend the class or make a purchase."} = this.props;
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
                 onChange={e => {
                   throw new Error("test error")
                 }}
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
