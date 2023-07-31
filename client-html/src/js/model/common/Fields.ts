import { InputProps } from "@mui/material/Input";
import {
  EditInPlaceSearchSelectFieldProps, FieldClasses,
  FieldMetaProps,
  FormFieldBaseProps,
  FormFieldProps,
  HTMLTagArgFunction
} from "ish-ui";
import { ReactNode, Ref } from "react";
import { BaseFieldProps } from "redux-form";
import { StringArgFunction } from  "ish-ui";
import { WrappedFieldInputProps, WrappedFieldMetaProps } from "redux-form/lib/Field";
import { EntityName } from "../entities/common";

export interface QueryFieldSuggestion {
  token: string;
  value: string;
  label: string;
  prefix?: string;
  queryPrefix?: string;
}

export interface EditInPlaceRemoteDataSelectFieldProps extends EditInPlaceSearchSelectFieldProps<WrappedFieldInputProps, WrappedFieldMetaProps> {
  onSearchChange?: StringArgFunction;
  onLoadMoreRows?: any;
  getCustomSearch?: (search: string) => string;
  getDefaultColumns?: (entity: string) => string;
  entity: EntityName;
  aqlFilter?: string;
  aqlColumns?: string;
  preloadEmpty?: boolean;
}

export interface EditInPlaceQueryFieldProps {
  ref?: Ref<any>;
  setInputNode?: HTMLTagArgFunction;
  className?: string;
  rootEntity: string;
  classes?: any;
  input?: any;
  editableComponent?: any;
  label?: ReactNode;
  disabled?: boolean;
  disableUnderline?: boolean;
  disableErrorText?: boolean;
  inline?: boolean;
  hideLabel?: boolean;
  meta?: Partial<FieldMetaProps>;
  InputProps?: InputProps;
  filterTags?: QueryFieldSuggestion[];
  tagSuggestions?: QueryFieldSuggestion[];
  customFields?: string[];
  performSearch?: () => void;
  theme?: any;
  onFocus?: any;
  onBlur?: any;
  placeholder?: string;
  labelAdornment?: any;
  endAdornment?: any;
  menuHeight?: number;
  fieldClasses?: FieldClasses;
  itemRenderer?: any;
}

export type AngelFormFieldProps = FormFieldProps | ({
  type: "remoteDataSelect"
} & FormFieldBaseProps & EditInPlaceRemoteDataSelectFieldProps) | ({
  type: "aql"
} & FormFieldBaseProps & EditInPlaceQueryFieldProps)

export type FormFieldWrapperProps = AngelFormFieldProps & BaseFieldProps<AngelFormFieldProps>;