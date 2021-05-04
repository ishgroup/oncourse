import React from "react";
import {Contact, Item} from "../../../../model";
import RadioGroup from "../../../../components/form-new/RadioGroup";

export interface Props {
  contacts: Contact[];
  payer: Contact;
  onChange: (Contact) => void;
  disabled?: boolean;
}

export class PayerSelect extends React.Component<Props, any> {
  render() {
    const {contacts, payer, onChange, disabled} = this.props;
    const items: Item[] = contacts.map((contact: Contact) => {
      return {
        key: contact.id,
        value: `${contact.firstName || ''} ${contact.lastName || ''}`,
        disabled: contact.parentRequired,
      };
    });

    const input = {
      value: payer?.id,
    };

    return (
      <div className="clearfix form-group">
        <label htmlFor="payer">
          <span>Payer</span>
          <span><em title="This field is required">*</em></span>
          <small>(issue invoice to)</small>
        </label>
        <div className="select-payer">
          <RadioGroup
            name="payer"
            required={true}
            items={items}
            input={input}
            onChange={onChange}
            disabled={disabled}
          />
        </div>
      </div>
    );
  }
}
