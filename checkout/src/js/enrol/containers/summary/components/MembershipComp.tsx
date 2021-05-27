import * as React from "react";
import { Contact, Membership, Product } from "../../../../model";
import { ItemWrapper } from "./ItemWrapper";
import classnames from "classnames";
import CustomFieldsForm from "./CustomFieldsForm";
import {getFormInitialValues} from "../../../../components/form/FieldFactory";

export interface Props {
  contact: Contact;
  membership: Membership;
  product: Product;
  onChange?: (item, contact) => void;
  onChangeFields?: (form, type) => any;
  readonly?: boolean;
}

class MembershipComp extends React.Component<Props, any> {
  public render(): JSX.Element {
    const { membership, product, contact, onChange, readonly, onChangeFields } = this.props;
    const divClass = classnames("row", "enrolmentItem", { disabled: !membership.selected });
    const warning = membership.warnings && membership.warnings.length ? this.props.membership.warnings[0] : null;
    const error = membership.warnings && membership.errors.length ? this.props.membership.errors[0] : null;
    const name = `membership-${contact.id}-${membership.productId}`;
    return (
      <div className={divClass}>
        <ItemWrapper title={product.name} name={name} error={error} warning={warning} selected={membership.selected}
          item={membership} contact={contact} readonly={readonly}
          onChange={onChange}>
          <div />
        </ItemWrapper>
        {membership.selected &&
          <div className="col-xs-8 col-md-8 alignright priceValue">
            <div className="row">
              <div className="col-xs-24 col-md-24 fee-full fullPrice text-right">
                <span>${membership.price}</span>
              </div>
            </div>
          </div>
        }

        {!readonly && <CustomFieldsForm
          headings={membership.fieldHeadings}
          selected={membership.selected}
          form={`${membership.contactId}-${membership.productId}`}
          onSubmit={() => undefined}
          initialValues={getFormInitialValues(membership.fieldHeadings)}
          onUpdate={form => onChangeFields(form, 'waitingLists')}
        />}
      </div>
    );
  }
}

export default MembershipComp;
