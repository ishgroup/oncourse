/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { ChangeEvent, DragEvent, FocusEvent, MutableRefObject, ReactElement, ReactNode } from "react";
import { AnyArgFunction, NumberArgFunction, StringArgFunction } from "../../../model/common/CommonFunctions";
import { ShowConfirmCaller } from "../../../model/common/Confirm";
import { Dispatch } from "redux";
import { InputProps } from "@mui/material/Input/Input";
import { TagRequirement, TagStatus, TagType } from "@api/model";

export type EventHandler<Event> = (event: Event, name?: string) => void;

export interface EventOrValueHandler<Event> extends EventHandler<Event> {
  (value: any): void;
}

export interface TagLike {
  id?: number;
  name?: string;
  color?: string;
  system?: boolean;
  urlPath?: string;
  content?: string;
  weight?: number;
  taggedRecordsCount?: number;
  childrenCount?: number;
  created?: string;
  modified?: string;
  childTags?: TagLike[];
}

export interface MenuTag<T> {
  active: boolean;
  tagBody: T;
  children: MenuTag<T>[];
  parent?: MenuTag<T>;
  entity?: string;
  path?: string;
  prefix?: string;
  queryPrefix?: string;
  indeterminate?: boolean;
}

export interface FieldInputProps {
  checked?: boolean | undefined;
  value: any;
  name: string;
  onBlur: EventOrValueHandler<FocusEvent<any>>;
  onChange: EventOrValueHandler<ChangeEvent<any>>;
  onDragStart: EventHandler<DragEvent<any>>;
  onDrop: EventHandler<DragEvent<any>>;
  onFocus: EventHandler<FocusEvent<any>>;
}

export interface FieldMetaProps {
  active?: boolean | undefined;
  dispatch: Dispatch<any>;
  error?: any;
  invalid: boolean;
  touched: boolean;
}

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

export interface ColoredCheckboxFieldProps<IP, MP> {
  input?: Partial<Omit<IP, "onBlur">> & { onBlur?: AnyArgFunction };
  meta?: Partial<MP>;
  color: string;
  label?: string;
  className?: string;
  disabled?: boolean;
}

export interface EditInPlaceFieldProps<IP, MP> {
  inputRef?: MutableRefObject<any>;
  input?: Partial<Omit<IP, "onBlur">> & { onBlur?: AnyArgFunction };
  meta?: Partial<MP>;
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

export interface EditInPlaceDateTimeFieldProps<IP, MP> {
  inputRef?: MutableRefObject<any>;
  input?: Partial<Omit<IP, "onBlur">> & { onBlur?: AnyArgFunction };
  meta?: Partial<MP>;
  fieldClasses?: FieldClasses,
  onKeyPress?: AnyArgFunction;
  labelAdornment?: ReactNode;
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

export interface EditInPlaceSearchSelectFieldProps<IP, MP> {
  inputRef?: MutableRefObject<any>;
  input?: Partial<Omit<IP, "onBlur">> & ({ onBlur?: AnyArgFunction });
  meta?: Partial<MP>;
  items?: any[];
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

export interface CodeFieldProps<IP, MP> {
  input?: Partial<Omit<IP, "onBlur">> & { onBlur?: AnyArgFunction };
  meta?: Partial<MP>;
  classes?: any;
  className?: string;
  errorMessage?: string;
  disabled?: boolean;
  onFocus?: AnyArgFunction;
}

export interface FormSwitchProps<IP> {
  input?: Partial<Omit<IP, "onBlur">> & { onBlur?: AnyArgFunction };
  color?: string;
  disabled?: boolean;
  stringValue?: boolean;
  label?: ReactNode;
  className?: string;
  inline?: boolean;
  onChangeHandler?: AnyArgFunction;
  onClick?: AnyArgFunction;
}

export interface CheckboxFieldProps<IP> {
  input?: Partial<Omit<IP, "onBlur">> & { onBlur?: AnyArgFunction };
  color?: string;
  disabled?: boolean;
  stringValue?: boolean;
  className?: string;
  uncheckedClass?: string;
  onChangeHandler?: AnyArgFunction;
  stopPropagation?: boolean;
}

export interface TagsFieldProps<T, IP, MP>  extends EditInPlaceFieldProps<IP, MP>  {
  tags: T[];
  showConfirm?: ShowConfirmCaller;
  classes?: any;
  validateEntity?: string;
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
  | ({ type: "date" | "time" | "dateTime" } & FormFieldBaseProps & EditInPlaceDateTimeFieldProps)
  | ({ type: "code" } & FormFieldBaseProps & CodeFieldProps)
  | ({ type: "coloredCheckbox" } & FormFieldBaseProps & ColoredCheckboxFieldProps)
  | ({ type: "switch" } & FormFieldBaseProps & FormSwitchProps)
  | ({ type: "checkbox" } & FormFieldBaseProps & CheckboxFieldProps)
  | ({ type: "tags" } & FormFieldBaseProps & TagsFieldProps)
  | ({ type: "stub" })