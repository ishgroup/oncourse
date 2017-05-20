import * as React from "react";

import {Contact} from "../../../../model/web/Contact";
import ContactInfo from "../../../components/ContactInfo";
import EnrolmentComp, {Props as EnrolmentProps} from "./EnrolmentComp";

export interface Props {
  contact: Contact
  enrolments: EnrolmentProps[]
}

class ContactComp extends React.Component<Props, any> {
  render() {
    const {contact, enrolments} = this.props;
    return (
      <div className="row">
        <ContactInfo contact={contact}/>
        <div className="col-xs-24 checkoutList">
          {enrolments.map((ep) => {
            return <EnrolmentComp {...ep}/>
          })}
        </div>
      </div>
    );
  }
}

export default ContactComp;