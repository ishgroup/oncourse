import * as React from "react";
import {WrappedFieldProps} from "redux-form";
import {CommonFieldProps} from "./CommonFieldProps";
import {WrappedMouseHoverProps} from "./MouseHover";
import {MaskedField} from "./MaskedField";

export const DateTimeField: React.SFC<DateTimeFieldProps> = props => (
  <MaskedField
    mask={[/\d/, /\d/, "/", /\d/, /\d/, "/", /\d/, /\d/, /\d/, /\d/," ", /\d/, /\d/, ":", /\d/, /\d/ ]}
    {...props}
  />
);

export interface DateTimeFieldProps extends Partial<WrappedFieldProps<{}>>, CommonFieldProps, WrappedMouseHoverProps {
  type: string;
  required: boolean;
  hint: string;
}
