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

const EditInPlaceMoneyField: React.FunctionComponent<any> = props => {
  // prevent dispatch and type from spreading
  const {
    currencySymbol, InputProps, dispatch, type, className, ...restProps
} = props;

  return (
    <EditInPlaceField
      {...restProps}
      preformatDisplayValue={value => formatCurrency(value, currencySymbol)}
      InputProps={{
        ...InputProps,
        startAdornment: <InputAdornment position="start">{currencySymbol}</InputAdornment>,
        inputComponent: NumberFormatCustom
      }}
      className={clsx("money", className)}
      type="money"
    />
  );
};

const mapStateToProps = (state: State) => ({
  currencySymbol: state.currency && state.currency.shortCurrencySymbol
});

export default connect<any, any, any>(
  mapStateToProps,
  null
)(EditInPlaceMoneyField);
