import React from "react";
import {Contact} from "../../model/web/Contact";

interface Props {
  contact: Contact;
  controls?: any;
}

class ContactInfo extends React.Component<Props, any> {
  public render() {
    const {contact, controls} = this.props;
    return (
      <div className="col-xs-24 student-name">
        <div className="student-info">
          { contact.firstName + " " + contact.lastName } <span className="student-email">({ contact.email })</span>
        </div>
        {controls}
      </div>
    );
  }
}

export default ContactInfo;