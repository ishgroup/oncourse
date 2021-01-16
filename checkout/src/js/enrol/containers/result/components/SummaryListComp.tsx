import * as React from "react";
import {IshState} from "../../../../services/IshState";
import {connect} from "react-redux";
import {ContactProps} from "../../summary/components";
import ContactComp from "../../summary/components/ContactComp";

interface Props {
  details: ContactProps[];
}

export class SummaryListComp extends React.Component<Props, any> {
  render() {
    const { details } = this.props

    if (!details) {
      return null;
    }

    return <div className="summaryPaymentsInfo">
      {details.map(item => <ContactComp
        {...item}
        key={item.contact.id}
        readonly
      />)}
    </div>
  }
}



const mapStateToProps = (state: IshState): any => ({
  details: state.checkout.summary.resultDetails
});

export default connect(mapStateToProps)(SummaryListComp);
