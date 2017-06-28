import React from "react";
import {ComboboxField} from "../../../../components/form/ComboboxField";
import * as Form from "redux-form";
import {DateField} from "../../../../components/form/DateField";
import {TextField} from "../../../../components/form/TextField";


interface Props {
  concessionTypes?: any;
	onTypeChange: any;

}

class ConcessionForm extends React.Component<Props, any> {
  constructor(props) {
    super(props);

    this.state = {
      concessionType: {},
    };
  }

  handleChange(key) {
    const {concessionTypes} = this.props;

    this.setState({
      concessionType: concessionTypes.find(item => item.key === key)
    });
  }

	render(): JSX.Element {
		const {concessionTypes} = this.props;
		const {hasExpireDate, hasNumber} = this.state.concessionType;

		return (
		  <fieldset>
        <ComboboxField
          required={true}
          name="concessionTypes"
          label="New Concession"
          items={concessionTypes}
          onChange={(event, key) => this.handleChange(key)}
        />

        {hasExpireDate &&
          <Form.Field name="date" label="Expire Date" required component={DateField}/>
        }

        {hasNumber &&
          <Form.Field name="number" label="Number" required component={TextField} type="text"/>
        }

      </fieldset>

		);
	}
}

export default ConcessionForm;
