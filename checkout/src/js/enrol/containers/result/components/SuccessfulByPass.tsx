import * as React from "react";

interface Props {
  successLink?: string;
}

export class SuccessfulByPass extends React.Component<Props, any> {
  render() {
    const {successLink} = this.props;

    return (
      <div>
        <h2>Payment <span>»</span> Successful</h2>
        <p>Your transaction was <strong>SUCCESSFUL</strong> and recorded in our system .</p>
        <p>Each student will shortly receive an enrolment or application confirmation, if a fee was incurred a tax
          invoice will also be sent. If you don't receive these within 24 hours, please contact us.</p>
        <p><strong>Please press continue to view further important information</strong></p>
        <p><a className="link-continue" href={successLink}>Continue</a></p>
      </div>
    );
  }
}
