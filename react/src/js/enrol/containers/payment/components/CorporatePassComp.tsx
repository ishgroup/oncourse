import React from "react";

interface Props {
  onSubmitPass: (code: string) => void;
}

class CorporatePassComp extends React.Component<Props, any> {
  private passInput;

  handleClick() {
    const {onSubmitPass} = this.props;
    onSubmitPass(this.passInput.value);
  }

  render() {
    return (
      <div id="corporate-pass" className="single-tab active">
        <fieldset>
          <p className="info-content">
            Enter a CorporatePass code below to complete this transaction without any payment at this time.
            CorporatePass is available to pre-approved corporate clients only.
          </p>

          <label htmlFor="corporatePass" className="corporatePass-label">Code</label>
          <input
            className="input-fixed"
            name="corporatePass"
            id="corporatePass"
            type="text"
            ref={ref => this.passInput = ref}
          />

          <div className="button" id="addCorporatePass" onClick={() => this.handleClick()}>Submit</div>
        </fieldset>
      </div>
    );
  }
}

export default CorporatePassComp;
