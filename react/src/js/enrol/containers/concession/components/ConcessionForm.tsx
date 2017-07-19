import React from "react";
import {ComboboxField} from "../../../../components/form/ComboboxField";
import * as Form from "redux-form";
import {DateField} from "../../../../components/form/DateField";
import {TextField} from "../../../../components/form/TextField";
import Checkbox from "../../../../components/form-new/Checkbox";
import {ConcessionType as ConcessionTypeModel} from "../../../../model";


interface Props {
  concessionTypes: ConcessionTypeModel[];
  onTypeChange?: (obj: ConcessionTypeModel) => void;
}

class ConcessionForm extends React.Component<Props, any> {
  constructor(props) {
    super(props);

    this.state = {
      concessionType: {},
      isDefault: true,
    };
  }

  handleChange(key) {
    const {concessionTypes, onTypeChange} = this.props;
    const selectedType = concessionTypes.find(item => item.id === key.toString());

    this.setState({
      concessionType: selectedType,
      isDefault: key === '-1',
    });

    onTypeChange(selectedType);
  }

  render(): JSX.Element {
    const {concessionTypes} = this.props;
    const {hasExpireDate, hasNumber} = this.state.concessionType;
    const {isDefault} = this.state;

    return (
      <div>
        <ComboboxField
          required={true}
          name="concessionType"
          label="New Concession"
          items={concessionTypes.map(t => ({key: t.id, value: t.name}))}
          onChange={(event, key) => this.handleChange(key)}
        />

        {hasExpireDate &&
        <Form.Field name="date" label="Expire Date" required component={DateField}/>
        }

        {hasNumber &&
        <Form.Field name="number" label="Number" required component={TextField} type="text"/>
        }

        {!isDefault &&
        <ConcessionText />
        }
      </div>
    );
  }
}

const ConcessionText = props => {
  return (
    <div className="clearfix conditions">
      <Form.Field
        component={Checkbox}
        type="checkbox"
        name="concessionAgree"
        required={true}
        value={true}
      />
      I certify that the concession I have claimed is valid and
      I understand that the details may be checked with the issuing body.
    </div>
  );
};

export default ConcessionForm;
