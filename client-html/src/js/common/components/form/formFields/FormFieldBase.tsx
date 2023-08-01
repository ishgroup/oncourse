/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import {
  CheckboxField,
  CodeEditorField,
  ColoredCheckBox,
  EditInPlaceDateTimeField,
  EditInPlaceDurationField,
  EditInPlaceField,
  EditInPlaceFileField,
  EditInPlaceMoneyField,
  EditInPlacePhoneField,
  EditInPlaceSearchSelect,
  FormSwitch,
  stubFunction,
  TagInputList,
} from "ish-ui";
import debounce from "lodash.debounce";
import React, { useCallback, useEffect, useMemo, useState } from "react";
import { WrappedFieldInputProps, WrappedFieldMetaProps } from "redux-form/lib/Field";
import { AngelFormFieldProps } from "../../../../model/common/Fields";
import { useAppSelector } from "../../../utils/hooks";
import EditInPlaceQuerySelect from "./EditInPlaceQuerySelect";
import EditInPlaceRemoteDataSearchSelect from "./EditInPlaceRemoteDataSearchSelect";

const stubFieldMocks = { input: { onChange: stubFunction, onBlur: stubFunction }, format: null, debounced: null };

const FormFieldBase = (props: AngelFormFieldProps) => {

  const { type, ...rest } = props;

  const { input, format, debounced = true } = type !== "stub"
    ? props
    : stubFieldMocks;

  const currencySymbol = useAppSelector(state => state.currency?.shortCurrencySymbol);
  const processActionId = useAppSelector(state => state.fieldProcessing[input?.name]);

  const entity = type === "remoteDataSelect" ? props.entity : null;

  const color = type === "coloredCheckbox" ? props.color : null;

  const rootEntity = type === "aql" ? props.rootEntity : null;

  const [value, setValue] = useState(input?.value);

  const debounceChange = useCallback(debounce(input?.onChange, 600), [input?.onChange]);

  const debounceBlur = useCallback(debounce(input?.onBlur, 600), [input?.onBlur]);

  const inputProxy = useMemo(() => ({
    ...input || {},
    value,
    onChange: e => {
      const val = e?.target ? e.target.value : e;
      setValue(format ? format(val) : val);
      debounceChange(e);
    },
    onBlur: e => {
      const val = e?.target ? e.target.value : e;
      setValue(format ? format(val) : val);
      debounceBlur(e);
    },
  }), [value, input]);

  useEffect(() => {
    if (input?.value !== value) {
      setValue(format ? format(input?.value) : input?.value);
    }
  }, [input?.value]);

  const sharedProps = {
    ...rest,
    ...debounced ? { input: inputProxy } : {}
  };

  switch (type) {
    case "aql":
      return <EditInPlaceQuerySelect
        {...sharedProps}
        rootEntity={rootEntity}
      />;
    case "phone":
      return <EditInPlacePhoneField
        <WrappedFieldInputProps, WrappedFieldMetaProps>
        {...sharedProps}
      />;
    case "duration":
      return <EditInPlaceDurationField
        <WrappedFieldInputProps, WrappedFieldMetaProps>
        {...sharedProps}
      />;
    case "file":
      return <EditInPlaceFileField
        <WrappedFieldInputProps, WrappedFieldMetaProps>
        {...sharedProps}
      />;
    case "money":
      return <EditInPlaceMoneyField
        <WrappedFieldInputProps, WrappedFieldMetaProps>
        {...sharedProps}
        currencySymbol={currencySymbol}
      />;
    case "select":
      return <EditInPlaceSearchSelect
        <WrappedFieldInputProps, WrappedFieldMetaProps>
        {...sharedProps}
      />;
    case "remoteDataSelect":
      return <EditInPlaceRemoteDataSearchSelect
        entity={entity}
        {...sharedProps}
      />;
    case "number":
      return <EditInPlaceField<WrappedFieldInputProps, WrappedFieldMetaProps>
        {...sharedProps} type="number"/>;
    case "date":
      return <EditInPlaceDateTimeField<WrappedFieldInputProps, WrappedFieldMetaProps>
        {...sharedProps}
        processActionId={processActionId}
        type="date"
      />;
    case "time":
      return <EditInPlaceDateTimeField<WrappedFieldInputProps, WrappedFieldMetaProps>
        {...sharedProps}
        processActionId={processActionId}
        type="time"
      />;
    case "dateTime":
      return <EditInPlaceDateTimeField<WrappedFieldInputProps, WrappedFieldMetaProps>
        {...sharedProps}
        processActionId={processActionId}
        type="datetime"
      />;
    case "code":
      return <CodeEditorField<WrappedFieldInputProps, WrappedFieldMetaProps>
        {...sharedProps} />;
    case "coloredCheckbox":
      return <ColoredCheckBox <WrappedFieldInputProps, WrappedFieldMetaProps>
        {...sharedProps}
        color={color}
      />;
    case "password":
      return <EditInPlaceField<WrappedFieldInputProps, WrappedFieldMetaProps>
        {...sharedProps}
        type="password"
      />;
    case "switch":
      return <FormSwitch<WrappedFieldInputProps> {...sharedProps} />;
    case "checkbox":
      return <CheckboxField<WrappedFieldInputProps>
        {...sharedProps} />;
    case "multilineText":
      return <EditInPlaceField<WrappedFieldInputProps, WrappedFieldMetaProps>
        {...sharedProps} multiline/>;
    case "stub":
      return <div className="invisible"/>;
    case "tags":
      return <TagInputList<WrappedFieldInputProps, WrappedFieldMetaProps>
        {...sharedProps}/>;
    case "text":
    default:
      return <EditInPlaceField<WrappedFieldInputProps, WrappedFieldMetaProps>
        {...sharedProps}/>;
  }
};

export default FormFieldBase;