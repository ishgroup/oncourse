import * as React from 'react';
import classnames from 'classnames';
import { Field } from 'redux-form';
import { FieldName } from '../services/PaymentService';

interface Props {
  conditions?: {
    termsUrl?: string,
    termsLabel?: string,
  };
  agreementChecked?: boolean;
}

const validateAgreement = (value) => (value ? undefined : 'You must agree to the policies before proceeding.');

export class Conditions extends React.Component<Props, any> {
  render() {
    const { conditions: { termsUrl, termsLabel }, agreementChecked } = this.props;

    return (
      <div className="clearfix payment-conditions">
        <div className="conditions">
          <span className="valid">
            <Field
              name={FieldName.agreementFlag}
              value="1"
              type="checkbox"
              component={(props) => (
                <input
                  {...props.input}
                  value="1"
                  type="checkbox"
                  className={classnames({ 't-error': props.meta.invalid && props.meta.touched })}
                  disabled={agreementChecked}
                />
              )}
              validate={validateAgreement}
            />
            <div className="conditions-text">
              {Boolean(termsLabel) && <a href={termsUrl || ''} target="_blank" rel="noreferrer">{termsLabel}</a>}

              {!termsLabel && (agreementChecked
                ? <span>The enrolment, sale and refund policy has been accepted.</span>
                : <span>I agree to the enrolment, sale and refund policy.</span>)}
              {!agreementChecked && <em title="This field is required">*</em>}
            </div>
            <span className="validate-text" />
          </span>
        </div>
      </div>
    );
  }
}
