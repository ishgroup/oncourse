import React from "react";
import {Contact} from "../../../model/web/Contact";
import ConcessionForm from "./components/ConcessionForm";
import {Field, reduxForm} from "redux-form";
import Checkbox from "../../../components/form-new/Checkbox";

export interface Props {
	contact: Contact
	concessions: any[]
}

export const NAME = "Concession";

class Concession extends React.Component<any, any> {
	constructor(props) {
		super(props);
	}

	componentWillMount() {
		this.state = {
			validConcession: false
		}
	}

	onTypeChange = (value) => {
		this.setState({
			validConcession: (value.key !== -1 ? true : false)
		})
	}

	render() {
		const {contact, concessions, handleSubmit, pristine, invalid, submitting} = this.props;

		return (
			<div className="concessionEditor">
				<h3>Add concession for {contact.firstName +" "+ contact.lastName}</h3>
				<form onSubmit={handleSubmit}>
					<fieldset>
						<ConcessionForm concessions={concessions} onTypeChange={this.onTypeChange} />
						{this.state.validConcession && <ConcessionText />}
					</fieldset>
					<p>
						<button id="cancelConcession" className="btn" disabled={invalid || pristine || submitting}>Cancel</button>
					</p>
				</form>
			</div>
		);
	}
}

const ConcessionText = () => {
	return (
		<div className="clearfix conditions">
			<Field component={Checkbox} type="checkbox" name="concessionAgree" required={true} /> I certify that the concession I have claimed is valid and I understand that the details may be checked with the issuing body.
		</div>
	)
}

const Container = reduxForm({
	form: NAME,
	//validate: validate,
	onSubmitSuccess: (result, dispatch, props: any) => {

	},
	onSubmitFail: (error, dispatch, submitError, props) => {

	}
})(Concession);

export default Container;