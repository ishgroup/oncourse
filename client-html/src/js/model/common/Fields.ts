import {
  EditInPlaceSearchSelectFieldProps,
  FormFieldBaseProps,
  FormFieldProps
} from "../../../ish-ui/model/Fields";
import { BaseFieldProps } from "redux-form";
import { StringArgFunction } from "./CommonFunctions";
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

export type AngelFormFieldProps =  FormFieldProps | ({ type: "remoteDataSelect" } & FormFieldBaseProps & EditInPlaceRemoteDataSelectFieldProps)

export type FormFieldWrapperProps = AngelFormFieldProps & BaseFieldProps<AngelFormFieldProps>;