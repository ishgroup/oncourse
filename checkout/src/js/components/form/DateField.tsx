import * as React from "react";
import {WrappedFieldProps} from "redux-form";
import {CommonFieldProps} from "./CommonFieldProps";
import {WrappedMouseHoverProps} from "./MouseHover";
import {MaskedField} from "./MaskedField";

export const DateField: React.SFC<DateFieldProps> = props => (
  <MaskedField
    mask={[/\d/, /\d/, "/", /\d/, /\d/, "/", /\d/, /\d/, /\d/, /\d/]}
    {...props}
  />
);

export interface DateFieldProps extends Partial<WrappedFieldProps>, CommonFieldProps, WrappedMouseHoverProps {
  type: string;
  required: boolean;
  hint: string;
}
