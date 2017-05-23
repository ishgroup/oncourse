import React from "react";
import classNames from "classnames";

interface Props {
	currentForm: string;
}

class CorporatePassComp extends React.Component<Props, any> {
	render() {
		const {currentForm} = this.props;

		return (
			<div id="corporate-pass" className={classNames("single-tab", { "active": currentForm === "corporate-pass" })}>
				<fieldset>
					<p className="info-content">
						Enter a CorporatePass code below to complete this transaction without any payment at this time. CorporatePass is available to pre-approved corporate clients only.
					</p>

					<label htmlFor="corporatePass" className="corporatePass-label">Code </label>
					<input className="input-fixed" name="corporatePass" id="corporatePass" type="text" />

					<div className="button" id="addCorporatePass">Submit</div>
				</fieldset>
			</div>
		)
	}
}

export default CorporatePassComp;