/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, {
  useCallback, useEffect, useState
} from "react";
import NumberFormat from "react-number-format";
import InputAdornment from "@mui/material/InputAdornment";
import PhoneIcon from '@mui/icons-material/Phone';
import { WrappedFieldProps } from "redux-form";
import debounce from "lodash.debounce";
import EditInPlaceField from "./EditInPlaceField";
import { getPhoneMask } from "../../../../constants/PhoneMasks";
import { stubFunction } from "../../../utils/common";

interface NumberFormatCustomProps extends WrappedFieldProps {
  onChange?: (event: { target: { value: string } }) => void;
  value?: string;
}

const NumberFormatCustom = React.forwardRef<any, NumberFormatCustomProps>((props, ref) => {
  const { onChange, ...other } = props;
  
  const [format, setFormat] = useState(null);

  const processFormat = data => {
    setFormat(getPhoneMask(data.value));
    onChange(data.formattedValue?.replace(/[^0-9+]/g, "") || null);
  };

  const onValueChange = useCallback(debounce(data => {
    processFormat(data);
  }, 600), []);

  useEffect(() => {
    if (other.value) {
      setFormat(getPhoneMask(other.value));
    }
  }, []);

  return (
    <NumberFormat
      {...other}
      getInputRef={ref}
      onBlur={stubFunction}
      onValueChange={onValueChange}
      format={format}
      type="tel"
    />
  );
});

const EditInPlacePhoneField: React.FunctionComponent<any> = props => {
  // prevent dispatch and type from spreading
  const {
    InputProps, dispatch, type, className, ...restProps
  } = props;

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
