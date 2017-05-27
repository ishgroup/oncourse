import * as React from "react";

import {Contact} from "../../../../model/web/Contact";
import EnrolmentComp, {Props as EnrolmentProps} from "./EnrolmentComp";
import ContactInfo from "../../../components/ContactInfo";
import {Enrolment} from "../../../../model/checkout/Enrolment";
import {Membership} from "../../../../model/checkout/Membership";
import {Article} from "../../../../model/checkout/Article";
import {Voucher} from "../../../../model/checkout/Voucher";

export interface Props {
  contact: Contact
  enrolments: EnrolmentProps[]
  onSelect?: (item: Enrolment | Membership | Article | Voucher, selected: boolean) => void
}

class ContactComp extends React.Component<Props, any> {
  render() {
    const {contact, enrolments, onSelect} = this.props;
    return (
      <div className="row">
        <ContactInfo contact={contact} controls={<AddConcessionLink/>}/>
        <div className="col-xs-24 checkoutList">
          {enrolments.map((props, index) => {
            return <EnrolmentComp key={index} {...props}
                                  onChange={ () => onSelect(props.enrolment, !props.enrolment.selected)}/>
          })}
        </div>
      </div>
    );
  }
}

const AddConcessionLink = (props) => {
  return (<div><a className="add-concession" href="#">Add Concession</a></div>);
};

export default ContactComp;