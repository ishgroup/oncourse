/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

/**
 * Wrapper component for Material Select and Text Field with edit in plaxce functional
 * */

import React, { useRef } from "react";
import AttachmentIcon from '@mui/icons-material/Attachment';
import InputAdornment from "@mui/material/InputAdornment";
import EditInPlaceField from "./EditInPlaceField";
import { stubFunction } from "../../utils/common";
import { EditInPlaceFieldProps, FieldInputProps, FieldMetaProps } from "../model/Fields";

function EditInPlaceFileField<InputProps extends FieldInputProps, MetaProps extends FieldMetaProps>(
  {
    input,
    ...restProps
  }: EditInPlaceFieldProps<InputProps, MetaProps>) {

  const inputRef = useRef<HTMLInputElement>();
  const fileRef = useRef<HTMLInputElement>();

  const handleFileSelect = e => {
    if (e.target.files[0]) {
      input.onChange(e.target.files[0]);
      fileRef.current.value = null;
      inputRef.current.blur();
    }
  };
  
  const onFocus = () => {
    fileRef.current.click();
    setTimeout(() => {
      inputRef.current.blur();
    }, 200);
  };

  return (
    <>
      <input type="file" ref={fileRef} onChange={handleFileSelect} className="d-none" />
      <EditInPlaceField
        {...restProps}
        input={{
          onChange: stubFunction,
          onFocus: stubFunction,
          onBlur: stubFunction
        }}
        InputProps={{
          startAdornment: <InputAdornment position="start"><AttachmentIcon /></InputAdornment>,
          onFocus,
          inputProps: {
            ref: inputRef
          },
          value: input.value ? input.value.name : "",
        }}
      />
    </>
  );
}

export default EditInPlaceFileField;