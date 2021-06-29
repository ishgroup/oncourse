import React from "react";
import SelectField from "../../../../components/form-new/SelectField";
import { Field } from "redux-form";
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
      hasValue: false,
    };
  }

  handleChange(key) {
    const {concessionTypes, onTypeChange} = this.props;
    const selectedType = concessionTypes.find(item => item.id === key.toString());

    this.setState({
      concessionType: selectedType,
      hasValue: !!key,
    });

    onTypeChange(selectedType);
  }

  render(): JSX.Element {
    const {concessionTypes} = this.props;
    const {hasExpireDate, hasNumber} = this.state.concessionType;
    const {hasValue} = this.state;

    return (
      <div>
        <Field
          name="concessionType"
          required={false}
          onChange={result => this.handleChange(result.key)}
          component={SelectField as any}
          props={{
            label: "New Concession",
            returnType: "object",
            searchable: false,
            loadOptions: () => Promise.resolve(concessionTypes.map(t => ({key: t.id, value: t.name})))
          }}
        />

        {hasExpireDate &&
        <Field name="expiryDate" type="text" label="Expiry Date" required component={DateField}/>
        }

        {hasNumber &&
        <Field name="number" label="Number" required component={TextField} type="text"/>
        }

        {hasValue &&
        <ConcessionText />
        }
      </div>
    );
  }
}

const ConcessionText = () => {
  return (
    <div className="clearfix conditions">
      <Field
        component={Checkbox}
        type="checkbox"
        name="concessionAgree"
        id="concessionAgree"
        required={true}
        value={true}
      />
      <span>
        I certify that the concession I have claimed is valid and
        I understand that the details may be checked with the issuing body.
      </span>
    </div>
  );
};

export default ConcessionForm;
