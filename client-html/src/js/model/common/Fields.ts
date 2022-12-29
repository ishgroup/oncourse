import { InputProps } from "@mui/material/Input";
import { WrappedFieldInputProps, WrappedFieldMetaProps } from "redux-form/lib/Field";
import { ReactNode } from "react";

export interface FieldClasses {
  input?: string;
  text?: string;
  underline?: string;
  label?: string;
  selectMenu?: string;
  placeholder?: string;
  loading?: string;
}

export interface SelectItemRendererProps<E = any> {
  content: string;
  data: E;
  parentProps: any;
  search?: string
}

export interface EditInPlaceFieldProps {
  ref?: any;
  input?: Partial<WrappedFieldInputProps>;
  meta?: Partial<WrappedFieldMetaProps>;
  InputProps?: Partial<InputProps>;
  type?: "password" | "percentage" | "number" | "text",
  label?: ReactNode,
  maxLength?: number,
  disabled?: boolean,
  min?: number,
  max?: number,
  step?: string,
  className?: string,
  onKeyPress?: any,
  placeholder?: string,
  warning?: string,
  labelAdornment?: any,
  preformatDisplayValue?: any,
  truncateLines?: number,
  fieldClasses?: FieldClasses,
  rightAligned?: boolean,
  disableInputOffsets?: boolean,
  onKeyDown?: any,
  multiline?: boolean,
  inline?: boolean,
}
