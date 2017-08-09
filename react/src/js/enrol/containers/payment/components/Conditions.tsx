import * as React from "react";
import {Field} from "redux-form";
import {FieldName} from "../services/PaymentService";

interface Props {
  conditions?: {
    refundPolicyUrl?: string,
    featureEnrolmentDisclosure?: string,
  };
}

export class Conditions extends React.Component<Props, any> {

  render() {
    const {refundPolicyUrl, featureEnrolmentDisclosure} = this.props.conditions;

    return (
      <div className="clearfix payment-conditions">
        <div>
          <label>Conditions<em title="This field is required">*</em></label>
        </div>
        <div className="conditions">
            <span className="valid">
                <Field name={FieldName.agreementFlag} component="input" type="checkbox" value="1"/>
                <div className="conditions-text">
                  {featureEnrolmentDisclosure &&
                    <span>
                      I have read the
                      <a
                        className="nyromodal"
                        title="Student information"
                        href={`${featureEnrolmentDisclosure}?wrap=false`}
                      > Student Information </a>
                    or have had it explained to me, and I agree to accept these  conditions.
                    </span>
                  }

                  {refundPolicyUrl &&
                    <span>
                      I understand the <a href={refundPolicyUrl} target="_blank"> enrolment, sale and refund policy</a>.
                    </span>
                  }

                  {!refundPolicyUrl &&
                    <span>I understand the enrolment, sale and refund policy.</span>
                  }
                </div>
                <span className="validate-text"/>
            </span>
        </div>
      </div>
    );
  }
}
