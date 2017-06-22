import React from "react";
import {Contact} from "../../../../model/web/Contact";
import RadioGroup from "../../../../components/form-new/RadioGroup";
import {Item} from "../../../../model/common/Item";

export interface Props {
  contacts: Contact[];
  payer: Contact;
  onChange: (Contact) => void;
}

export class PayerSelect extends React.Component<Props, any> {
  render() {
    const {contacts, payer, onChange} = this.props;
    const items: Item[] = contacts.map((contact: Contact) => {
      return {key: contact.id, value: `${contact.firstName || ''} ${contact.lastName}`};
    });

    const input = {
      value: payer.id,
    };

    return (
      <div className="clearfix form-group">
        <label htmlFor="payer">
          Payer<em title="This field is required">*</em>
          <small>(issue invoice to)</small>
        </label>
        <div className="select-payer">
          <RadioGroup name="payer" required={true} items={items} input={input} onChange={onChange}/>
        </div>
      </div>
    );
  }
}
