import * as React from "react";
import {Contact} from "../../../../model/web/Contact";
import {ContactFields} from "../../../../model/field/ContactFields";
import HeadingComp from "./HeadingComp";

export interface Props {
  contact: Contact,
  fields: ContactFields,
  header?: string
}

export class ContactEdit extends React.Component<Props, any> {
  render() {
    const {contact, fields, header} = this.props;

    const headings = fields.classHeadings[0].headings.map((h,index) => { return (<HeadingComp heading={h} key={index}/>)});


    return (
      <div>
        <div className="student">{contact.firstName} {contact.lastName} <span>- {contact.email}</span></div>
        <div className="message">
          <p>We require a few more details to create the contact record.
            It is important that we have correct contact information in case we need to let you know about course
            changes.
            Please enter the details as you would like them to appear on a certificate or invoice.
          </p>
        </div>
        {headings}
      </div>
    );
  }
}