import * as React from "react";
import classnames from 'classnames';
import {Field} from "redux-form";
import {FieldName} from "../services/PaymentService";

interface Props {
  conditions?: {
    refundPolicyUrl?: string,
    featureEnrolmentDisclosure?: string,
  };
  agreementChecked?: boolean;
}

const validateAgreement = value => value ? undefined : 'You must agree to the policies before proceeding.';

export class Conditions extends React.Component<Props, any> {

  render() {
    const {conditions: {refundPolicyUrl, featureEnrolmentDisclosure}, agreementChecked} = this.props;

    return (
      <div className="clearfix payment-conditions">
        <div className="conditions">
            <span className="valid">
                <Field
                  name={FieldName.agreementFlag}
                  value="1"
                  type="checkbox"
                  component={props => <input
                    {...props.input}
                    value="1"
                    type="checkbox"
                    className={classnames({'t-error': props.meta.invalid && props.meta.touched})}
                    disabled={agreementChecked}
                  />}
                  validate={validateAgreement}
                />
                <div className="conditions-text">
                  {featureEnrolmentDisclosure &&
                  <span>
                      I have read the
                      <a
                        title="Student information"
                        href={`${featureEnrolmentDisclosure}`}
                        target="_blank"
                      > Student Information </a>
                      or have had it explained to me, and I agree to accept these  conditions.{' '}
                    </span>
                  }

                  {refundPolicyUrl && (agreementChecked ?
                    <span>
                      The <a href={refundPolicyUrl} target="_blank"> enrolment, sale and refund policy</a> has been accepted.
                    </span>
                    :
                      <span>
                      I agree to the <a href={refundPolicyUrl} target="_blank"> enrolment, sale and refund policy</a>.
                    </span>
                    )
                  }

                  {!refundPolicyUrl && (agreementChecked ?
                    <span>The enrolment, sale and refund policy has been accepted.</span>
                    : <span>I agree to the enrolment, sale and refund policy.</span>)
                  }
                  {!agreementChecked && <em title="This field is required">*</em>}
                </div>
                <span className="validate-text"/>
            </span>
        </div>
      </div>
    );
  }
}
