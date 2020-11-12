/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import clsx from "clsx";
import React, { useCallback } from "react";
import Typography from "@material-ui/core/Typography";
import { connect } from "react-redux";
import { State } from "../../../reducers/state";
import { openInternalLink } from "../../utils/links";
import { formatCurrency } from "../../utils/numbers/numbersNormalizing";
import { LinkAdornment } from "./FieldAdornments";

interface UneditableProps {
  value: string | number;
  label?: string;
  labelAdornment?: any;
  url?: string;
  currencySymbol?: string;
  money?: boolean;
  multiline?: boolean;
  className?: string;
  format?: (value) => any;
}

const Uneditable = React.memo<UneditableProps>(props => {
  const {
   label, labelAdornment, value, url, money, currencySymbol, className, format, multiline
  } = props;

  const openLink = useCallback(() => {
    openInternalLink(url);
  }, [url]);

  return (
    <div className={className ? className + " textField" : "textField"}>
      <div>
        <Typography variant="caption" color="textSecondary" noWrap>
          {label}
          {url && <LinkAdornment link={url} className="pl-0-5" clickHandler={openLink} />}
          {labelAdornment && (
          <span className="pl-0-5">
            {labelAdornment}
            {' '}
          </span>
        )}
        </Typography>
        <Typography variant="body1" className={clsx(money && "money", multiline && "text-pre-wrap")}>
          {money ? (
            formatCurrency(value, currencySymbol)
          ) : value ? (
            format ? (
              format(value)
            ) : (
              value
            )
          ) : (
            <span className="placeholderContent">No Value</span>
          )}
        </Typography>
      </div>
    </div>
  );
});

const mapStateToProps = (state: State) => ({
  currencySymbol: state.currency && state.currency.shortCurrencySymbol
});

export default connect(mapStateToProps, null)(Uneditable);
