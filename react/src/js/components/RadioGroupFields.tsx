import * as React from "react";
import {FieldLabel} from "../enrol/components/form/FieldLabel";
import {Field} from "redux-form";
import {Item} from "../model/autocomplete/Item";
/**
 * Redux-Form Fields implementation for Radio Group componenent
 */

interface Props {
  name: string,
  label: string,
  required: boolean,
  items: Item[]
}

class RadioGroupFields extends React.Component<any, any> {

  render() {
    const {name, label, required, items} = this.props;

    const fields = items.map((i) => {
      return (
        <span key={i.key}>
          <Field name={name} component="input" type="radio" value={i.key}/>
          {' '}
          {i.value}
        </span>
      )
    });

    return (<div>
      <FieldLabel
        name={name}
        label={label}
        required={required}
      />
      <span className="radio-list">
        {fields}
      </span>
    </div>)
  }
}

export default RadioGroupFields