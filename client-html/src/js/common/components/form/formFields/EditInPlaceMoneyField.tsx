/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback } from "react";
import NumberFormat from "react-number-format";
import InputAdornment from "@mui/material/InputAdornment";
import { connect } from "react-redux";
import clsx from "clsx";
import EditInPlaceField from "./EditInPlaceField";
import { State } from "../../../../reducers/state";
import { formatCurrency, normalizeNumber } from "../../../utils/numbers/numbersNormalizing";
import { EditInPlaceFieldProps } from "../../../../model/common/Fields";
import { Dispatch } from "redux";

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

interface Props extends EditInPlaceFieldProps {
  currencySymbol?: string;
  dispatch?: Dispatch;
}

const preformatDisplayValue = value => value || value === 0 ? formatCurrency(value, "") : value;

const EditInPlaceMoneyField = ({ currencySymbol, InputProps, dispatch, className, ...restProps }: Props) => (
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

const mapStateToProps = (state: State) => ({
  currencySymbol: state.currency && state.currency.shortCurrencySymbol
});

export default connect(mapStateToProps)(EditInPlaceMoneyField);