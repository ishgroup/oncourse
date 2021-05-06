import * as React from "react";
import {IshState} from "../../../../services/IshState";
import {connect} from "react-redux";
import {ContactProps} from "../../summary/components/Index";
import ContactComp from "../../summary/components/ContactComp";
import {CorporatePass} from "../../../../model";

interface StateProps {
  contacts: ContactProps[];
  corporatePass: CorporatePass;
  successLink: string;
}

export class SummaryListComp extends React.Component<StateProps, any> {
  render() {
    const { contacts, successLink, corporatePass } = this.props

    if (!contacts) {
      return null;
    }

    return <div className="payment-summary summaryPaymentsInfo">
      <h1>Thank you</h1>
      {corporatePass &&
        <div className="message">
          <strong>Invoice email sent</strong>
          <div>
            Invoice email was sent to {corporatePass.name} on <a href={`mailto:${corporatePass.email}`}>{corporatePass.email}</a> email
          </div>
        </div>}
      {contacts.map((item, index) => <ContactComp
        {...item}
        key={item.contact?.id || index}
        isPayer={corporatePass ? false : item.isPayer}
        readonly
      />)}
      <p><a className="link-continue" href={successLink}>Close</a></p>
    </div>
  }
}



const mapStateToProps = (state: IshState): any => ({
  contacts: state.checkout.summary.resultDetails && state.checkout.summary.resultDetails.contacts,
  corporatePass: state.checkout.summary.resultDetails && state.checkout.summary.resultDetails.corporatePass,
  successLink: state.config.paymentSuccessURL
});

export default connect<StateProps,any,any>(mapStateToProps)(SummaryListComp);
