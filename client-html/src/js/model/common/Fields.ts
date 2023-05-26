import { Tag } from "@api/model";
import { InputProps } from "@mui/material/Input";
import { WrappedFieldInputProps, WrappedFieldMetaProps } from "redux-form/lib/Field";
import React, { MutableRefObject, ReactElement, ReactNode } from "react";
import { AnyArgFunction, HTMLTagArgFunction, NumberArgFunction, StringArgFunction } from "./CommonFunctions";
import { Suggestion } from "../../common/components/form/formFields/EditInPlaceQuerySelect";
import { ShowConfirmCaller } from "./Confirm";
import { EntityName } from "../entities/common";

export interface FieldClasses {
  input?: string;
  text?: string;
  underline?: string;
  label?: string;
  selectMenu?: string;
  loading?: string;
  listbox?: string;
}

export interface SelectItemRendererProps<E = any> {
  content: string;
  data: E;
  parentProps: any;
  search?: string
}

export interface EditInPlaceFieldProps {
  inputRef?: MutableRefObject<any>;
  input?: Partial<Omit<WrappedFieldInputProps, "onBlur">> & { onBlur?: AnyArgFunction };
  meta?: Partial<WrappedFieldMetaProps>;
  InputProps?: Partial<InputProps>;
  label?: ReactNode,
  maxLength?: number,
  disabled?: boolean,
  min?: string,
  max?: string,
  step?: string,
  className?: string,
  defaultValue?: any,
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

export interface EditInPlaceDateTimeFieldProps {
  inputRef?: MutableRefObject<any>;
  input?: Partial<Omit<WrappedFieldInputProps, "onBlur">> & { onBlur?: AnyArgFunction };
  meta?: Partial<WrappedFieldMetaProps>;
  fieldClasses?: FieldClasses,
  onKeyPress?: AnyArgFunction;
  labelAdornment?: React.ReactNode;
  formatDate?: string;
  formatTime?: string;
  formatDateTime?: string;
  defaultValue?: any;
  timezone?: string;
  label?: ReactNode,
  formatValue?: string;
  className?: string;
  placeholder?: string;
  warning?: string;
  inline?: boolean;
  disabled?: boolean;
  persistValue?: boolean;
  rightAligned?: boolean;
}

export interface EditInPlaceQueryFieldProps {
  ref?: React.Ref<any>;
  setInputNode?: HTMLTagArgFunction;
  className?: string;
  rootEntity: string;
  classes?: any;
  input?: any;
  editableComponent?: any;
  label?: string;
  disabled?: boolean;
  disableUnderline?: boolean;
  disableErrorText?: boolean;
  inline?: boolean;
  hideLabel?: boolean;
  meta?: Partial<WrappedFieldMetaProps>;
  InputProps?: InputProps;
  filterTags?: Suggestion[];
  tags?: Suggestion[];
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

export interface EditInPlaceSearchSelectFieldProps {
  inputRef?: MutableRefObject<any>;
  input?: Partial<Omit<WrappedFieldInputProps, "onBlur">> & { onBlur?: AnyArgFunction };
  meta?: Partial<WrappedFieldMetaProps>;
  items?: any[];
  classes?: any;
  label?: ReactNode,
  disabled?: boolean;
  className?: string;
  labelAdornment?: any;
  inline?: boolean;
  rightAligned?: boolean;
  loading?: boolean;
  hideLabel?: boolean;
  colors?: any;
  creatable?: boolean;
  endAdornment?: any;
  allowEmpty?: boolean;
  fieldClasses?: FieldClasses;
  selectLabelCondition?: any;
  selectFilterCondition?: any;
  defaultValue?: any;
  rowHeight?: number;
  remoteRowCount?: number;
  loadMoreRows?: AnyArgFunction;
  onCreateOption?: AnyArgFunction;
  itemRenderer?: (content, data, search, parentProps) => ReactElement;
  valueRenderer?: (content, data, search, parentProps) => ReactElement;
  onInputChange?: AnyArgFunction;
  onClearRows?: AnyArgFunction;
  onInnerValueChange?: AnyArgFunction;
  selectValueMark?: string;
  remoteData?: boolean;
  disableUnderline?: boolean;
  createLabel?: string;
  selectLabelMark?: string;
  returnType?: "object" | "string";
  alwaysDisplayDefault?: boolean;
  popperAnchor?: any;
  placeholder?: string;
  sort?: ((a: any, b: any) => number) | boolean;
  sortPropKey?: string;
  hasError?: boolean;
  multiple?: boolean;
  hideMenuOnNoResults?: boolean;
  hideEditIcon?: boolean;
  warning?: string;
  categoryKey?: string;
  selectAdornment?: { position: "start" | "end", content: ReactElement }
}

export interface EditInPlaceRemoteDataSelectFieldProps extends EditInPlaceSearchSelectFieldProps {
  onSearchChange?: StringArgFunction;
  getCustomSearch?: (search: string) => string;
  onLoadMoreRows?: NumberArgFunction;
  onClearRows?: AnyArgFunction;
  entity: EntityName;
  aqlFilter?: string;
  aqlColumns?: string;
  preloadEmpty?: boolean;
}

export interface CodeFieldProps {
  input?: Partial<Omit<WrappedFieldInputProps, "onBlur">> & { onBlur?: AnyArgFunction };
  meta?: Partial<WrappedFieldMetaProps>;
  classes?: any;
  className?: string;
  errorMessage?: string;
  disabled?: boolean;
  onFocus?: AnyArgFunction;
}

export interface FormSwitchProps {
  input?: Partial<Omit<WrappedFieldInputProps, "onBlur">> & { onBlur?: AnyArgFunction };
  color?: string;
  disabled?: boolean;
  stringValue?: boolean;
  label?: ReactNode;
  className?: string;
  inline?: boolean;
  onChangeHandler?: AnyArgFunction;
  onClick?: AnyArgFunction;
}

export interface CheckboxFieldProps {
  input?: Partial<Omit<WrappedFieldInputProps, "onBlur">> & { onBlur?: AnyArgFunction };
  color?: string;
  disabled?: boolean;
  stringValue?: boolean;
  className?: string;
  uncheckedClass?: string;
  onChangeHandler?: AnyArgFunction;
  stopPropagation?: boolean;
}

export interface TagsFieldProps extends EditInPlaceFieldProps {
  tags: Tag[];
  showConfirm?: ShowConfirmCaller;
  classes?: any;
  validateEntity?: EntityName;
}

interface FormFieldBaseProps {
  required?: boolean;
  format?: AnyArgFunction;
  onClick?: AnyArgFunction;

  // Performance optimization property that handles field value rendering outside of Redux Form life circle.
  // It is true by default
  // Should be set to false when Redux Form lificircle handlers are used (onChange,onBlur and etc)
  debounced?: boolean;
}

export type FormFieldProps =
    ({ type: "phone" | "duration" | "text" | "multilineText" | "password" | "number" | "file" | "money" } & FormFieldBaseProps & EditInPlaceFieldProps)
  | ({ type: "select" } & FormFieldBaseProps & EditInPlaceSearchSelectFieldProps)
  | ({ type: "remoteDataSelect" } & FormFieldBaseProps & EditInPlaceRemoteDataSelectFieldProps)
  | ({ type: "date" | "time" | "dateTime" } & FormFieldBaseProps & EditInPlaceDateTimeFieldProps)
  | ({ type: "aql" } & FormFieldBaseProps & EditInPlaceQueryFieldProps)
  | ({ type: "code" } & FormFieldBaseProps & CodeFieldProps)
  | ({ type: "switch" } & FormFieldBaseProps & FormSwitchProps)
  | ({ type: "checkbox" } & FormFieldBaseProps & CheckboxFieldProps)
  | ({ type: "tags" } & FormFieldBaseProps & TagsFieldProps)
  | ({ type: "stub" })