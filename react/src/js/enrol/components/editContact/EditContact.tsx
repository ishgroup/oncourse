import * as React from "react";
import {TextAreaField} from "../form/TextAreaField";
import {Field} from "redux-form";
import {CheckboxField} from "../form/CheckboxField";
import {RadioGroupField} from "../form/RadioGroupField";
import {TextField} from "../form/TextField";
import {AutocompleteField} from "../form/AutocompleteField";
import {DateField} from "../form/DateField";
import {Concession} from "../contact/Concession";
// import {AvetmissDetails} from "../contact/AvetmissDetails";

export class EditContactComponent extends React.Component<EditContactComponentProps, EditContactState> {
  constructor() {
    super();
  }

  render() {
    return (
      <form>
        <div className="student">Test Test <span>- test@test.com</span></div>
        <div className="message">
          <p>We require a few more details to create the contact record.
            It is important that we have correct contact information in case we need to let you know about course
            changes.
            Please enter the details as you would like them to appear on a certificate or invoice.
          </p>
        </div>
        <fieldset id="student_enrol_details">
          <br/>
          <Field component={AutocompleteField}
                 autocomplete="address-line1"
                 name="street"
                 label="Address"
                 type="text"
                 classes="street"
          />
          <Field component={TextField}
                 name="suburb"
                 label="Suburb"
                 type="text"
                 classes="suburb"
          />
          <Field component={AutocompleteField}
                 name="country"
                 autocomplete="country"
                 label="Country"
                 type="text"
                 classes="country"
          />
          <Field component={AutocompleteField}
                 name="postcode"
                 autocomplete="postal-code"
                 label="Postcode"
                 type="text"
                 classes="postcode"
          />
          <Field component={AutocompleteField}
                 name="state"
                 autocomplete="address-level1"
                 label="State"
                 type="text"
                 classes="state"
          />
          <Field component={AutocompleteField}
                 name="state"
                 autocomplete="address-level1"
                 label="State"
                 type="text"
                 classes="state"
          />
          <Field component={TextField}
                 name="homePhoneNumber"
                 label="Home Phone"
                 type="text"
                 classes="homePhoneNumber"
          />
          {/**/}
          <Field component={TextField}
                 name="businessPhoneNumber"
                 label="Work Phone"
                 type="text"
                 classes="businessPhoneNumber"
          />
          <Field component={TextField}
                 name="faxNumber"
                 label="Fax"
                 type="text"
                 classes="faxNumber"
          />
          <Field component={TextField}
                 name="mobilePhoneNumber"
                 label="Mobile Phone"
                 type="text"
                 classes="mobilePhoneNumber"
          />
          <Field component={DateField}
                 name="dateOfBirth"
                 label="Date of birth"
                 type="text"
                 classes="dateOfBirth"
                 required
          />
          <Field name="isMale"
                 label="Gender"
                 component={RadioGroupField}
                 options={["Male", "Female"]}
          />
          <Field name="specialNeeds"
                 label="Special dietary requirements, allergies, accessibility or medical considerations"
                 component={TextAreaField}
                 rows={4}
                 classes="specialNeeds"
          />
        </fieldset>
        <fieldset className="opt-in">
          <p className="fieldset-intro">
            I would like to receive information and offers via:
          </p>
          <Field name="isMarketingViaEmailAllowed"
                 label="E-Mail"
                 component={CheckboxField}
          />
          <Field name="isMarketingViaSMSAllowed"
                 label="SMS"
                 component={CheckboxField}
          />
          <Field name="isMarketingViaPostAllowed"
                 label="Post"
                 component={CheckboxField}
          />
        </fieldset>

        <Concession/>

        {/*<AvetmissDetails isOptional={false}/>*/}

        <div className="form-controls">
          <input value="OK"
                 className="btn btn-primary"
                 id="submitContact"
                 name="submitContact"
                 type="submit"/>
        </div>
      </form>
    );
  }
}

export interface EditContactComponentProps {
}

interface EditContactState {
}
