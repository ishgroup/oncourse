/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useCallback } from "react";
import NumberFormat from "react-number-format";
import InputAdornment from "@mui/material/InputAdornment";
import { connect } from "react-redux";
import clsx from "clsx";
import EditInPlaceField from "./EditInPlaceField";
import { State } from "../../../reducers/state";
import { formatCurrency, normalizeNumber } from "../../utils/numbers/numbersNormalizing";
import { Dispatch } from "redux";
import { EditInPlaceFieldProps } from "../model/Fields";

interface NumberFormatCustomProps {
  onChange?: (event: { target: { value: string } }) => void;
  decimalScale: number;
  allowNegative: boolean;
}

const NumberFormatCustom = React.forwardRef<any, NumberFormatCustomProps>((props, ref) => {
  const { onChange, allowNegative = true, ...other } = props;

  const onValueChange = useCallback(values => {
    onChange(normalizeNumber(values.value));
  }, []);

  return (
    <NumberFormat
      {...other}
      getInputRef={ref}
      onValueChange={onValueChange}
      allowNegative={allowNegative}
      decimalScale={2}
      thousandSeparator
    />
);
});

interface Props<InputProps, MetaProps> extends EditInPlaceFieldProps<InputProps, MetaProps> {
  currencySymbol?: string;
  dispatch?: Dispatch;
}

const preformatDisplayValue = value => value || value === 0 ? formatCurrency(value, "") : value;

function EditInPlaceMoneyField<IP, MP>({ currencySymbol, InputProps, dispatch, className, ...restProps }: Props<IP, MP>) {
  return (
    <EditInPlaceField
      {...restProps}
      preformatDisplayValue={preformatDisplayValue}
      InputProps={{
        ...InputProps,
        startAdornment: <InputAdornment position="start">{currencySymbol}</InputAdornment>,
        inputComponent: NumberFormatCustom as any,
      }}
      className={clsx("money", className)}
    />
  );
}

const mapStateToProps = (state: State) => ({
  currencySymbol: state.currency && state.currency.shortCurrencySymbol
});

export default connect(mapStateToProps)(EditInPlaceMoneyField);