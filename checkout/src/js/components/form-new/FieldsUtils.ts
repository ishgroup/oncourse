import {WrappedFieldInputProps, WrappedFieldMetaProps} from "redux-form";
export const inputFrom = (props: any): WrappedFieldInputProps => {
  return props.input ? props.input : {};
};

export const metaFrom = (props: any): WrappedFieldMetaProps => {
  return props.meta ? props.meta : {};
};
