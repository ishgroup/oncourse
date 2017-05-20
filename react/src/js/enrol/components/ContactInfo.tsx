import React from "react";
import {Contact} from "../../model/web/Contact";

interface Props {
  contact: Contact;
}

class ContactInfo extends React.Component<Props, any> {
  public render() {
    const {contact} = this.props;
    return (
      <div className="col-xs-24 student-name">
        <div className="student-info">
          { contact.firstName + " " + contact.lastName } <span className="student-email">({ contact.email })</span>
        </div>
      </div>
    );
  }
}

export default ContactInfo;