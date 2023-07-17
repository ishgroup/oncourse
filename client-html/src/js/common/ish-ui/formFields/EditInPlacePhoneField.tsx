/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, {
  useCallback, useEffect, useRef, useState
} from "react";
import NumberFormat from "react-number-format";
import InputAdornment from "@mui/material/InputAdornment";
import PhoneIcon from '@mui/icons-material/Phone';
import debounce from "lodash.debounce";
import EditInPlaceField from "./EditInPlaceField";
import { getPhoneMask } from "../../../constants/PhoneMasks";
import { WrappedFieldInputProps, WrappedFieldMetaProps } from "redux-form/lib/Field";
import { AnyArgFunction } from "../../../model/common/CommonFunctions";
import { EditInPlaceFieldProps, FieldInputProps, FieldMetaProps } from "../model/Fields";

interface NumberFormatCustomProps {
  onChange?: (event: any) => void;
  value?: string;
  input?: Partial<Omit<WrappedFieldInputProps, "onBlur">> & { onBlur?: AnyArgFunction };
  meta?: Partial<WrappedFieldMetaProps>;
}

const NumberFormatCustom = React.forwardRef<any, NumberFormatCustomProps>((props, ref) => {
  const { onChange, ...other } = props;

  const inputRef = useRef<any>();

  const [format, setFormat] = useState(null);
  const [prefix, setPrefix] = useState(undefined);

  const processFormat = useCallback(debounce(data => {
    setFormat(getPhoneMask(data.value));
    if (!data.value) {
      setPrefix(undefined);
    }
    if (data.formattedValue.startsWith("+") && prefix !== "+") {
      setPrefix("+");
    }
  }, 500), []);

  const onValueChange = useCallback(data => {
    onChange(data.formattedValue?.replace(/[^0-9+]/g, "") || null);
    processFormat(data);
  }, []);

  useEffect(() => {
    if (other.value) {
      setFormat(getPhoneMask(other.value));
    }
  }, []);

  const onKeyPress = e => {
    if (e.key === "+") {
      inputRef.current._valueTracker.setValue("+");
      if (prefix !== "+") {
        setPrefix("+");
      }
    }
  };
  
  const getInputRef = input => {
    ref = input;
    inputRef.current = input;
  };

  return (
    <NumberFormat
      {...other}
      getInputRef={getInputRef}
      onKeyPress={onKeyPress}
      prefix={prefix}
      onValueChange={onValueChange}
      format={format}
      type="text"
    />
  );
});

const EditInPlacePhoneField = ({ InputProps, className, ...restProps }: EditInPlaceFieldProps<FieldInputProps, FieldMetaProps>) => {
  return (
    <EditInPlaceField
      {...restProps}
      InputProps={{
        ...InputProps,
        startAdornment: <InputAdornment position="start"><PhoneIcon /></InputAdornment>,
        inputComponent: NumberFormatCustom
      }}
      className={className}
    />
  );
};

export default EditInPlacePhoneField;