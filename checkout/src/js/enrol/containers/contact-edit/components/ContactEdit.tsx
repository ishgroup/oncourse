import * as React from "react";
import {isNil} from "lodash";
import {Contact, ContactFields} from "../../../../model";
import HeadingComp from "../../../components/HeadingComp";
import {ContactInfo} from "../../../components/ContactInfo";

export interface Props {
  contact: Contact;
  fields: ContactFields;
  header?: string;
  form?: string;
  touch?: (field) => void; // hack: Fire touch event on blur react select
  onChangeSuburb?: (item) => void;
}

export class ContactEdit extends React.Component<Props, any> {
  render() {
    const {contact, fields, touch, onChangeSuburb, form } = this.props;

    const headings = isNil(fields) ? [] : fields.headings.map((h, index) => (
      <HeadingComp heading={h} key={index} touch={touch} onChangeSuburb={onChangeSuburb} form={form}/>
    ));

    return (
      <div>
        <ContactInfo contact={contact}/>
        {headings}
      </div>
    );
  }
}
