import React from "react";
import {Contact} from "../../../../model/web/Contact";
import {Field} from "redux-form";
import RadioGroup from "../../../../components/form-new/RadioGroup";

export interface Props {
	contact: Contact
}

class PayerComp extends React.Component<any, any> {
	render() {
		const {contacts} = this.props;
		const payerList = PayerList({contacts: contacts});

		return (
			<div className="clearfix form-group">
				<label htmlFor="contact">
					Payer<em title="This field is required">*</em>
					<small>(issue invoice to)</small>
				</label>

				<div className="select-payer">
					<Field component={RadioGroup} name="payer" required={true} items={payerList} value={(payerList.length > 0 ? payerList[0].key : "")} />
				</div>
			</div>
		)
	}
}

const PayerList = (props) => {
	let payerList = [];
	props.contacts.map((payer, index) => {
		return payerList.push({key: payer.contact.id, value: payer.contact.firstName + " " + payer.contact.lastName });
	});
	return payerList;
}

export default PayerComp;