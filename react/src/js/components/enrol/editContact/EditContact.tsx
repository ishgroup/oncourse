import * as React from "react";

export default class EditContact extends React.Component<EditContactProps, EditContactState> {
  constructor() {
    super();
  }

  render() {
    return (
      <form action="" method="post" id="contactEditorForm">
        <div className="t-invisible">
          <input value="" name="t:formdata" type="hidden"/>
        </div>
        <div className="student">Test Test <span>- test@test.com</span></div>
        <div className="message">
          <p>We require a few more details to create the contact record.
    It is important that we have correct contact information in case we need to let you know about course changes.
    Please enter the details as you would like them to appear on a certificate or invoice.
          </p>
        </div>
        <fieldset id="student_enrol_details">
          <br/>
          <p>
            <label htmlFor="street">Address</label>
            <span className="valid">
              <input className="street input-fixed "
                     autoComplete="address-line1" id="street"
                     name="street" type="text"/>
              <span className="validate-text"></span>
            </span>
          </p>
          <p>
            <label htmlFor="suburb">Suburb</label>
            <span className="valid">
              <input className="suburb input-fixed  ui-autocomplete-input" autoComplete="off" id="suburb" name="suburb"
                     type="text"/>
                <span role="status" aria-live="polite" className="ui-helper-hidden-accessible"></span>
                <span className="validate-text"></span>
            </span>
          </p>
          <p>
            <label htmlFor="country">Country</label>
            <span className="valid">
              <input className="country input-fixed  ui-autocomplete-input" autoComplete="off" value="Australia"
                     id="country"
                     name="country" type="text"/>
              <span role="status" aria-live="polite" className="ui-helper-hidden-accessible"></span><span
              className="validate-text"><span id="givenName-hint" className="hint hidden-text"> Please specify Country if outside of Australia. <span
              className="hint-pointer"></span></span></span></span>
          </p>
          <p>
            <label htmlFor="postcode">Postcode</label>
            <span className="valid">
              <input className="postcode input-fixed " autoComplete="postal-code"
                     id="postcode" name="postcode" type="text"/>
                <span className="validate-text"><span id="givenName-hint" className="hint hidden-text"> Enter a postcode or zipcode. <span
                  className="hint-pointer"></span></span></span></span>
          </p>
          <p>
            <label htmlFor="state">State</label>
            <span className="valid">
              <input className="state input-fixed " autoComplete="address-level1" id="state" name="state"
                     type="text"/>
                <span className="validate-text"></span>
            </span>
          </p>
          <p>
            <label htmlFor="homePhoneNumber">Home Phone</label>
            <span className="valid">
            <input className="homePhoneNumber input-fixed " autoComplete="home tel" id="homePhoneNumber"
                   name="homePhoneNumber" type="text"/>
              <span className="validate-text">
                <span id="givenName-hint" className="hint hidden-text"> Enter 10 digit home phone number including area code for Australian numbers. <span
                  className="hint-pointer"></span></span></span></span>
          </p>
          <p>
            <label htmlFor="businessPhoneNumber">Work Phone</label>
            <span className="valid">
              <input className="businessPhoneNumber input-fixed " autoComplete="work tel" id="businessPhoneNumber"
                     name="businessPhoneNumber" type="text"/>
              <span className="validate-text"><span id="givenName-hint" className="hint hidden-text"> Enter 10 digit business phone number including area code for Australian numbers. <span
                className="hint-pointer"></span></span></span></span>
          </p>
          <p>
            <label htmlFor="faxNumber">Fax</label>
            <span className="valid">
              <input className="faxNumber input-fixed " autoComplete="fax tel" id="faxNumber" name="faxNumber"
                     type="text"/>
              <span className="validate-text"><span id="givenName-hint" className="hint hidden-text"> Enter 10 digit fax number including area code for Australian numbers. <span
                className="hint-pointer"></span></span></span></span>
          </p>
          <p>
            <label htmlFor="mobilePhoneNumber">Mobile Phone</label>
            <span className="valid">
              <input className="mobilePhoneNumber input-fixed " autoComplete="mobile tel" id="mobilePhoneNumber"
                     name="mobilePhoneNumber" type="text"/>
              <span className="validate-text">
                <span id="givenName-hint" className="hint hidden-text"> Enter 10 digit mobile phone number for Australian numbers. <span
                  className="hint-pointer"></span></span></span></span>
          </p>
          <p>
            <label htmlFor="dateOfBirth">Date of birth<em title="This field is required">*</em></label>
            <span className="valid">
              <input
                className="dateOfBirth input-fixed " autoComplete="bday" id="dateOfBirth" name="dateOfBirth"
                type="text"/>
              <span
                className="validate-text">
                <span id="givenName-hint" className="hint hidden-text"> Enter your date of birth in the form DD/MM/YYYY. <span
                  className="hint-pointer"></span></span>
              </span>
            </span>
          </p>
          <p>
            <label htmlFor="isMale">Gender</label>
            <span className="radio-list">
              <input value="true" name="isMale"
                     id="radio"
                     type="radio"/> Male
<input value="false" name="isMale" id="radio_0" type="radio"/> Female
<span className="validate-text"></span></span></p>
          <p><label htmlFor="specialNeeds">Special dietary requirements, allergies, accessibility or medical
      considerations</label><span className="valid">
            <textarea placeholder="None" className="specialNeeds input-fixed "
                      rows={4}
                      id="specialNeeds" name="specialNeeds"></textarea><span
            className="validate-text"><span id="givenName-hint" className="hint hidden-text"> Special dietary requirements, allergies, accessibility or medical considerations. <span
            className="hint-pointer"></span></span></span></span></p>
        </fieldset>
        <fieldset className="opt-in"><p className="fieldset-intro">
          I would like to receive information and offers via:</p>
          <p><label htmlFor="isMarketingViaEmailAllowed">E-Mail</label><input checked
                                                                              id="isMarketingViaEmailAllowed"
                                                                              name="isMarketingViaEmailAllowed"
                                                                              type="checkbox"/>
          </p>
          <p><label htmlFor="isMarketingViaSMSAllowed">SMS</label><input checked id="isMarketingViaSMSAllowed"
                                                                         name="isMarketingViaSMSAllowed"
                                                                         type="checkbox"/>
          </p>
          <p><label htmlFor="isMarketingViaPostAllowed">Post</label><input checked
                                                                           id="isMarketingViaPostAllowed"
                                                                           name="isMarketingViaPostAllowed"
                                                                           type="checkbox"/></p>
        </fieldset>
        <div className="form-controls"><input value="OK" className="btn btn-primary" id="submitContact"
                                              name="submitContact"
                                              type="submit"/></div>
      </form>
    );
  }
}

interface EditContactProps {
}

interface EditContactState {
}
