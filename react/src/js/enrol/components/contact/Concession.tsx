import * as React from "react";
import {Field} from "redux-form";
import {ComboboxField} from "../form/ComboboxField";
import {TextField} from "../form/TextField";
import {DateField} from "../form/DateField";
import {CheckboxField} from "../form/CheckboxField";
import {Item} from "../../../model/field/enumeration/Item";

const concessionTypes:Item[] = [
  {displayName: "no concession", databaseValue: -1},
  {displayName: "Seniors card", databaseValue: 0},
  {displayName: "Pension", databaseValue: 1}
];

export function Concession() {
  return (
    <div style={{position: "static", zoom: 1}}>
      <fieldset className="concessions">
        <br/>
        <ComboboxField
          items={concessionTypes}
          name="concessionTypes"
          label="New Concession"
        />
        <Field
          component={TextField}
          name="concessionNumber"
          label="Concession number"
          type="text"
          size={15}
          required
        />
        <Field
          component={DateField}
          name="expiresOn"
          label="Expiry date"
          type="text"
          classes="dateOfBirth"
          required
        />
        <div className="conditions">
          <Field
            component={CheckboxField}
            name="concessionAgree"
            label="I certify that the concession I have claimed is valid and I understand that the details may be checked with the issuing body."
            required
          />
        </div>
      </fieldset>
    </div>
  );
}
