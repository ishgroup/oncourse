import React from "react";

interface Props {
	refId: string;
}

export class Successful extends React.Component<Props, any> {
	render() {
		const {refId} = this.props;

		return (
			<div>
				<h2>Payment <span>»</span> Successful</h2>
				<p>Thank you for enrolling, applying or purchasing from omega.</p>
				<p>Your payment was <strong>SUCCESSFUL</strong> and recorded in our system against reference number <strong>{refId}</strong>.</p>
				<p>Each student will shortly receive an enrolment or application confirmation, if a fee was incurred a tax invoice will also be sent. If you don't receive these within 24 hours, please contact us.</p>
				<p><strong>Please press continue to view further important information</strong></p>
				<p><a href="#/">Continue</a></p>
			</div>
		);
	}
}
