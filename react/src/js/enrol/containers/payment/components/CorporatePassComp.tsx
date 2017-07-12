import React from "react";

class CorporatePassComp extends React.Component<any, any> {
  render() {
    return (
      <div id="corporate-pass" className="single-tab active">
        <fieldset>
          <p className="info-content">
            Enter a CorporatePass code below to complete this transaction without any payment at this time.
            CorporatePass is available to pre-approved corporate clients only.
          </p>

          <label htmlFor="corporatePass" className="corporatePass-label">Code</label>
          <input className="input-fixed" name="corporatePass" id="corporatePass" type="text"/>

          <div className="button" id="addCorporatePass">Submit</div>
        </fieldset>
      </div>
    );
  }
}

export default CorporatePassComp;
