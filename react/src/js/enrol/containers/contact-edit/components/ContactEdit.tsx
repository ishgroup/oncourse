import * as React from "react";
import {isNil} from "lodash";
import {Contact, ContactFields} from "../../../../model";
import HeadingComp from "./HeadingComp";
import {ContactInfo} from "../../../components/ContactInfo";

export interface Props {
  contact: Contact,
  fields: ContactFields,
  header?: string,
}

export class ContactEdit extends React.Component<Props, any> {
  render() {
    const {contact, fields, header} = this.props;

    const headings = isNil(fields) ? [] : fields.headings.map((h, index) => (
      <HeadingComp heading={h} key={index}/>
    ));

    return (
      <div>
        <ContactInfo contact={contact}/>
        {headings}
      </div>
    );
  }
}
