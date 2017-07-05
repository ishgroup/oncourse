import React from "react";
import {Contact} from "../../model/web/Contact";

interface Props {
  contact: Contact;
  controls?: any;
  concessions?: any;
}

export class ContactInfo extends React.Component<Props, any> {
  public render() {
    const {contact, controls, concessions} = this.props;
    return (
      <div className="col-xs-24 student-name">
        <div className="student-info">
          { contact.firstName + " " + contact.lastName } <span className="student-email">({ contact.email })</span>
        </div>
        {concessions && concessions.map(item => (
          <div key={item.id}><i>{item.name}</i></div>
        ))}
        {controls}
      </div>
    );
  }
}