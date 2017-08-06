import React from "react";
import {Field} from "redux-form";
import classnames from 'classnames';

import {CorporatePass} from "../../../../model";
import {TextField} from "../../../../components/form-new/TextField";

interface Props {
  onSubmitPass: (code: string) => void;
  corporatePass?: CorporatePass;
  corporatePassError?: string;
  onUnmount?: () => void;
  fetching?: boolean;
}

class CorporatePassComp extends React.Component<Props, any> {
  private passInput;
  private referenceInput;

  componentWillUnmount() {
    // recalculate amount after reset corporate pass
    const {corporatePass, onUnmount} = this.props;
    if (corporatePass.id) {
      onUnmount();
    }
  }

  handleClick() {
    const {onSubmitPass} = this.props;
    onSubmitPass(this.passInput.value);
  }

  render() {
    const {corporatePass, corporatePassError, fetching} = this.props;

    const passField = () => (
      <div>
        <Field
          component={TextField}
          maxLength={40}
          className="input-fixed"
          autoComplete="off"
          name="code"
          label="Code"
          type="text"
          required={true}
          ref={ref => this.passInput = ref}
        />
        <div className="button" id="addCorporatePass" onClick={() => this.handleClick()}>Submit</div>
      </div>
    );

    const referenceField = () => (
      <div>
        <Field
          component={TextField}
          maxLength={40}
          className="input-fixed "
          autoComplete="off"
          name="reference"
          label="Your Reference (Optional)"
          type="text"
          ref={ref => this.referenceInput = ref}
        />
      </div>
    );

    return (
      <div id="corporate-pass" className={classnames("single-tab active", {fetching})}>
        <fieldset>
          <p className="info-content">
            Enter a CorporatePass code below to complete this transaction without any payment at this time.
            CorporatePass is available to pre-approved corporate clients only.
          </p>

          {!corporatePass.id &&
            passField()
          }

          {corporatePass.id &&
            referenceField()
          }

          {corporatePass.message &&
          <div className="message">
            {corporatePass.message}
          </div>
          }

          {corporatePassError &&
          <div className="validation">
            {corporatePassError}
          </div>
          }


        </fieldset>
      </div>
    );
  }
}

export default CorporatePassComp;
