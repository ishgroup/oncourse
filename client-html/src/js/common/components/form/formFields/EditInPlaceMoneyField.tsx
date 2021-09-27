/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback } from "react";
import NumberFormat from "react-number-format";
import InputAdornment from "@material-ui/core/InputAdornment";
import { connect } from "react-redux";
import clsx from "clsx";
import EditInPlaceField from "./EditInPlaceField";
import { State } from "../../../../reducers/state";
import { formatCurrency, normalizeNumber } from "../../../utils/numbers/numbersNormalizing";

interface NumberFormatCustomProps {
  inputRef: (instance: NumberFormat | null) => void;
  onChange: (event: { target: { value: string } }) => void;
}

const NumberFormatCustom: React.FunctionComponent<NumberFormatCustomProps> = props => {
  const { inputRef, onChange, ...other } = props;

  const onValueChange = useCallback(values => {
    onChange(normalizeNumber(values.value));
  }, []);

  return <NumberFormat {...other} getInputRef={inputRef} onValueChange={onValueChange} thousandSeparator />;
};

const EditInPlaceMoneyField: React.FunctionComponent<any> = props => {
  // prevent dispatch and type from spreading
  const {
    currencySymbol, allowNegative = true, InputProps, dispatch, type, className, ...restProps
} = props;

  const inputComponent = useCallback(
    inputProps => <NumberFormatCustom {...inputProps} decimalScale={2} allowNegative={allowNegative} />,
    [allowNegative]
  );

  return (
    <EditInPlaceField
      {...restProps}
      preformatDisplayValue={value => formatCurrency(value, currencySymbol)}
      InputProps={{
        ...InputProps,
        startAdornment: <InputAdornment position="start">{currencySymbol}</InputAdornment>,
        inputComponent
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
