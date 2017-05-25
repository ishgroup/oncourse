import React from "react";
import {Field} from "redux-form";
import SelectField from "../../../../components/form-new/SelectField";

interface Props {
	concessions: any[],
	onTypeChange: any;
}

class ConcessionForm extends React.Component<Props, any> {
	render(): JSX.Element {
		const {concessions, onTypeChange} = this.props;

		return (
			<Field component={SelectField} name="concessionTypes" label="New Concession" loadOptions={() => Promise.resolve(concessions)} onChange={onTypeChange} />
		)
	}
}

export default ConcessionForm;