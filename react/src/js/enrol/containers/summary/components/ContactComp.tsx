import * as React from "react";

import {Contact} from "../../../../model/web/Contact";
import EnrolmentComp, {Props as EnrolmentProps} from "./EnrolmentComp";
import ContactInfo from "../../../components/ContactInfo";

export interface Props {
  contact: Contact
  enrolments: EnrolmentProps[]
}

class ContactComp extends React.Component<Props, any> {
  render() {
    const {contact, enrolments} = this.props;
    return (
      <div className="row">
        <div className="col-xs-24 student-name">
          <ContactInfo contact={contact} controls={<AddConcessionLink/>}/>
        </div>
        <div className="col-xs-24 checkoutList">
          {enrolments.map((props, index) => {
            return <EnrolmentComp key={index} {...props}/>
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