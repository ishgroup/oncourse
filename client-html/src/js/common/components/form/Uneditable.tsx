/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import clsx from "clsx";
import React, { useCallback } from "react";
import { connect } from "react-redux";
import {
 FormControl, FormHelperText, Input, InputLabel 
} from "@mui/material";
import InputAdornment from "@mui/material/InputAdornment";
import { makeStyles } from "@mui/styles";
import { State } from "../../../reducers/state";
import { openInternalLink } from "../../utils/links";
import { formatCurrency } from "../../utils/numbers/numbersNormalizing";
import { LinkAdornment } from "./FieldAdornments";

const useStyles = makeStyles(() => ({
  rightAlignedLabel: {
    left: "unset",
    right: 0,
    transformOrigin: '100% 0'
  }
}));

interface UneditableProps {
  value: string | number;
  label: string;
  labelAdornment?: any;
  url?: string;
  currencySymbol?: string;
  money?: boolean;
  multiline?: boolean;
  rightAligned?: boolean;
  className?: string;
  placeholder?: string;
  error?: string;
  format?: (value) => any;
}

const Uneditable = React.memo<UneditableProps>(props => {
  const {
   label, labelAdornment, placeholder = "No value", rightAligned, value, url, money, currencySymbol, className, format, multiline, error
  } = props;
  
  const classes = useStyles();
  const inputId = `input-${label.toLowerCase().replace(' ', '-')}`;

  const openLink = useCallback(() => {
    openInternalLink(url);
  }, [url]);

  return (
    <FormControl
      className={clsx(className, money && "money")}
      error={Boolean(error)}
      variant="standard"
      fullWidth
    >
      <InputLabel
        shrink
        classes={{
          root: rightAligned ? classes.rightAlignedLabel : null
        }}
        htmlFor={inputId}
      >
        {label}
        {url && <LinkAdornment link={url} className="pl-0-5" clickHandler={openLink} />}
        {labelAdornment && (
          <span className="pl-0-5">
            {labelAdornment}
            {' '}
          </span>
        )}
      </InputLabel>
      <Input
        id={inputId}
        startAdornment={money && <InputAdornment position="start">{currencySymbol}</InputAdornment>}
        value={
          money
            ? formatCurrency(value, "")
            : value
              ? (format
                  ? (format(value))
                  : (value)
            ) : ("")
          }
        multiline={multiline}
        placeholder={placeholder}
        inputProps={{
          className: rightAligned && "text-end"
        }}
        disabled
      />
      <FormHelperText>{error}</FormHelperText>
    </FormControl>
  );
});

const mapStateToProps = (state: State) => ({
  currencySymbol: state.currency && state.currency.shortCurrencySymbol
});

export default connect(mapStateToProps, null)(Uneditable);
