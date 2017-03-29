import * as React from "react";

export default class AddContact extends React.Component<AddContactProps, AddContactState> {
  constructor() {
    super();
  }

  render() {
    return (
      <div id="addstudent-block">
        <h2>Add a student</h2>
        <div className="message">
          <p>Please enter the details of a person enrolling, applying or making a purchase.</p>
        </div>
        <form action="https://reactjs.local.oncourse.net.au/enrol/checkout.addcontact" method="post"
              id="addContactForm">
          <div className="t-invisible">
            <input value="" name="t:formdata" type="hidden"/>
          </div>
          <fieldset>
            <br/>
            <p>
              <label htmlFor="firstName">First name<em title="This field is required">*</em></label>
              <span className="valid">
                  <input className="input-fixed contact-field" autoComplete="given-name" id="firstName" name="firstName"
                         type="text"/>
                  <span className="validate-text"></span>
                </span>
            </p>
            <p>
              <label htmlFor="lastName">Last name <em title="This field is required">*</em></label>
              <span className="valid">
                  <input className="input-fixed contact-field" autoComplete="family-name" id="lastName" name="lastName"
                         type="text"/>
                    <span className="validate-text"></span>
                </span>
            </p>
            <p>
              <label htmlFor="email">Email<em title="This field is required">*</em></label>
              <span className="valid">
                  <input className="input-fixed contact-field" autoComplete="email" id="email" name="email"
                         type="text"/>
                  <span className="validate-text"></span>
                </span>
            </p>
          </fieldset>
          <p className="note">
            <strong className="alert">Note</strong>: If you have been here before, please try to use the same email address.
          </p>
          <div className="form-controls">
            <input value="OK" className="btn btn-primary" id="submitContact" name="submitContact" type="submit"/>
          </div>
        </form>
      </div>
    );
  }
}

interface AddContactProps {
}

interface AddContactState {
}
