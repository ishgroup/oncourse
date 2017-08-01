import React from "react";
import SelectField from "../../../../components/form-new/SelectField";
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
        <Form.Field
          component={SelectField}
          name="concessionType"
          label="New Concession"
          returnType="object"
          searchable={false}
          required={false}
          onChange={result => this.handleChange(result.key)}
          loadOptions={() => Promise.resolve(concessionTypes.map(t => ({key: t.id, value: t.name})))}
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

const ConcessionText = () => {
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
