import React from "react";
import {Contact, StudentMembership, Concession} from "../../model";

interface Props {
  contact: Contact;
  controls?: any;
  concessions?: Concession[];
  memberships?: StudentMembership[];
}

export class ContactInfo extends React.Component<Props, any> {
  public render() {
    const {contact, controls, concessions, memberships} = this.props;
    return (
      <div className="col-xs-24 student-name">
        <div className="student-info">
          { `${contact.firstName || ''}  ${contact.lastName || ''} `}
          <span className="student-email">({ contact.email })</span>
        </div>
        {memberships && memberships.map((item, i) => (
          <div className="membership" key={i}><i>{item.name}</i></div>
        ))}
        {concessions && concessions.map((item, i) => (
          <div className="concession" key={i}><i>{item.name}</i></div>
        ))}
        {controls}
      </div>
    );
  }
}