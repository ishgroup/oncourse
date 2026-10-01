/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Tag } from '@api/model';
import { debounce } from 'es-toolkit/compat';
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
} from 'ish-ui';
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { WrappedFieldInputProps, WrappedFieldMetaProps } from 'redux-form/lib/Field';
import { COMMON_PLACEHOLDER } from '../../../../constants/Forms';
import { AngelFormFieldProps } from '../../../../model/common/Fields';
import { applyTagsChange, getAvailableTagIds, removeUnavailableTagIds } from '../../../../containers/tags/utils';
import { useAppSelector } from '../../../utils/hooks';
import EditInPlaceQuerySelect from './EditInPlaceQuerySelect';
import EditInPlaceRemoteDataSearchSelect from './EditInPlaceRemoteDataSearchSelect';

const stubFieldMocks = { input: { onChange: stubFunction, onBlur: stubFunction }, format: null, debounced: null, placeholder: null };

const isSameValue = (a: any, b: any) =>
  Array.isArray(a) && Array.isArray(b)
    ? a.length === b.length && a.every((v, i) => v === b[i])
    : a === b;

const FormFieldBase = (props: AngelFormFieldProps) => {

  const { type, availableTagIds: declaredTagIds, ...rest } = props;

  const { input, format, debounced = true } = type !== "stub" && type !== "color" && type !== "radio"
    ? props
    : stubFieldMocks;

  const currencySymbol = useAppSelector(state => state.location.currency?.shortCurrencySymbol);
  const processActionId = useAppSelector(state => state.fieldProcessing[input?.name]);

  const entity = type === "remoteDataSelect" ? props.entity : null;

  const color = type === "coloredCheckbox" ? props.color : null;

  const rootEntity = type === "aql" ? props.rootEntity : null;

  const tags = type === "tags" ? props.tags : null;

  // the tag input only renders the ids of the tag tree it is given, everything else is shown as an
  // "Error: Tag not found!" chip. A view can narrow that tree down further (roles, entity types), so
  // the declared ids are intersected with the ones of the rendered tree instead of replacing them -
  // a declared id which is not in the tree would produce a chip again
  const availableTagIds = useMemo(() => {
    if (type !== "tags") return null;

    const renderedTagIds = getAvailableTagIds(tags);
    if (declaredTagIds === undefined) return renderedTagIds;
    if (!renderedTagIds) return declaredTagIds;

    return new Set(Array.from(declaredTagIds).filter(id => renderedTagIds.has(id)));
  }, [type, declaredTagIds, tags]);

  const availableValue = useMemo(
    () => (availableTagIds ? removeUnavailableTagIds(input?.value, availableTagIds) : input?.value),
    [input?.value, availableTagIds]
  );

  const [value, setValue] = useState(availableValue);

  const debounceChange = useCallback(debounce(input?.onChange, 600), [input?.onChange]);

  const debounceBlur = useCallback(debounce(input?.onBlur, 600), [input?.onBlur]);

  // the tag input reports back the ids it rendered, so its own value has to stay display only -
  // handing it the form value puts ids it can not resolve back into its chips. The form value is the
  // one the input reported, with the ids it can not render merged back in
  const onTagsValue = useCallback((e, onFormChange) => {
    const val = e?.target ? e.target.value : e;

    if (!availableTagIds) {
      setValue(format ? format(val) : val);
      onFormChange(e);
      return;
    }

    const displayed = removeUnavailableTagIds(value, availableTagIds);
    const next = removeUnavailableTagIds(val, availableTagIds);

    setValue(format ? format(next) : next);
    onFormChange(applyTagsChange(input?.value, displayed, next));
  }, [value, input?.value, format, availableTagIds]);

  const inputProxy = useMemo(() => ({
    ...input || {},
    value,
    onChange: e => onTagsValue(e, debounceChange),
    onBlur: e => onTagsValue(e, debounceBlur),
  }), [value, input, onTagsValue]);

  // only the displayed value is filtered - tag ids that are not in the tag menu are hidden from the
  // tag input (it renders them as a "Tag not found!" chip), the form value itself is left untouched.
  // Tag ids which are still valid but belong to another source (completed checklist tasks are kept in
  // the same form field and have no entry in the tag menu) would be lost by writing the filtered value
  // back to the form. Those are deliberately not rendered as a chip either - the checklist card next
  // to the field is what shows their state
  useEffect(() => {
    if (!isSameValue(availableValue, value)) {
      setValue(format ? format(availableValue) : availableValue);
    }
  }, [availableValue]);

  const sharedProps = {
    ...rest,
    ...debounced ? { input: inputProxy } : {},
    placeholder: (rest as any).placeholder || COMMON_PLACEHOLDER,
    label: (rest as any).label || ''
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
        {...sharedProps}
        color={props.color as any}
      />;
    case "multilineText":
      return <EditInPlaceField<WrappedFieldInputProps, WrappedFieldMetaProps>
        {...sharedProps} multiline/>;
    case "stub":
      return <input className="d-none" name={sharedProps?.input?.name}/>;
    case "tags":
      return <TagInputList<Tag, WrappedFieldInputProps, WrappedFieldMetaProps>
        {...sharedProps}
        tags={tags}
      />;
    case "text":
    default:
      return <EditInPlaceField<WrappedFieldInputProps, WrappedFieldMetaProps>
        {...sharedProps}/>;
  }
};

export default FormFieldBase;