import * as React from "react";
import {isNil} from "lodash";
import {Contact} from "../../../../model/web/Contact";
import {ContactFields} from "../../../../model/field/ContactFields";
import HeadingComp from "./HeadingComp";
import ContactInfo from "../../../components/ContactInfo";

export interface Props {
  contact: Contact,
  fields: ContactFields,
  header?: string
}

export class ContactEdit extends React.Component<Props, any> {
  render() {
    const {contact, fields, header} = this.props;

    const headings = isNil(fields) ? [] : fields.headings.map((h, index) => {
      return (<HeadingComp heading={h} key={index}/>)
    });

    return (
      <div>
        <ContactInfo contact={contact}/>
        <div className="clearfix" />
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