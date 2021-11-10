/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useEffect, useState } from "react";
import clsx from "clsx";
import { connect } from "react-redux";
import createStyles from "@mui/styles/createStyles";
import withStyles from "@mui/styles/withStyles";
import { alpha } from '@mui/material/styles';
import Typography from "@mui/material/Typography";
import IconButton from "@mui/material/IconButton";
import Delete from "@mui/icons-material/Delete";
import { AppTheme } from "../../../../../model/common/Theme";
import { State } from "../../../../../reducers/state";

const styles = (theme: AppTheme) => createStyles({
  root: {
    "&:hover": {
      "& $deleteIcon": {
        visibility: "visible"
      },
      "& $priceDeleteIcon": {
        transform: "translateX(0px)"
      }
    }
  },
  heading: {
    marginTop: 10,
    marginBottom: 5
  },
  deleteIcon: {
    color: alpha(theme.palette.text.primary, 0.2),
    padding: 5,
    fontSize: 16,
    visibility: "hidden",
  },
  priceDeleteIcon: {
    transform: "translateX(26px)"
  }
});

const SelectedVoucherRenderer: React.FC<any> = props => {
  const {
    openRow, item, onDelete, classes, isSelected, index, disabled
  } = props;

  return (
    <>
      <div className="centeredFlex flex-fill cursor-pointer" onClick={disabled ? undefined : () => openRow(item)}>
        <Typography
          variant="body2"
          className={clsx(
            "flex-fill text-truncate",
            disabled && "disabled",
            { "fontWeight600": isSelected(item) }
          )}
          noWrap
        >
          <span>{item.name}</span>
        </Typography>
      </div>
      <IconButton disabled={disabled} className={classes.deleteIcon} color="secondary" onClick={e => onDelete(e, index, item)}>
        <Delete fontSize="inherit" color="inherit" />
      </IconButton>
    </>
  );
};

const SelectedDiscountRenderer: React.FC<any> = props => {
  const {
    openRow, item, onDelete, classes, isSelected, index
  } = props;

  return (
    <>
      <div className="centeredFlex flex-fill cursor-pointer" onClick={() => openRow(item)}>
        <Typography
          variant="caption"
          className={clsx("flex-fill text-truncate", { "fontWeight600": isSelected(item) })}
          noWrap
        >
          <span>{item.name}</span>
        </Typography>
      </div>
      <div className={clsx("centeredFlex relative", classes.priceDeleteIcon)}>
        <IconButton className={classes.deleteIcon} color="secondary" onClick={e => onDelete(e, index, item)}>
          <Delete fontSize="inherit" color="inherit" />
        </IconButton>
      </div>
    </>
  );
};

const Item = props => {
  const {
    classes, isSelected, item, type
  } = props;

  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setTimeout(() => {
      setMounted(true);
    }, 500);
  }, []);

  return (
    <div
      className={clsx(
      "centeredFlex relative",
      `${mounted ? "" : "bounceInRight animated"}`,
      classes.root,
      isSelected(item) && "selectedItemArrow"
    )}
    >
      {type === "voucher" ? (
        <SelectedVoucherRenderer {...props} />
    ) : (
      <SelectedDiscountRenderer
        {...props}
      />
    )}
    </div>
  );
};

const SelectedPromoCodesRenderer = React.memo<any>(props => {
  const {
    items, selectedDiscount, className, ...rest
  } = props;

  const isSelected = React.useCallback(
    item => selectedDiscount && selectedDiscount.id === item.id && selectedDiscount.type === item.type,
    [selectedDiscount]
  );

  return (
    <div className={className}>
      {items.map((item, index) => <Item isSelected={isSelected} item={item} index={index} {...rest} />)}
    </div>
  );
});

const mapStateToProps = (state: State) => ({
  currencySymbol: state.currency && state.currency.shortCurrencySymbol,
  summaryList: state.checkout.summary.list
});

export default connect(mapStateToProps, null)(withStyles(styles)(SelectedPromoCodesRenderer));
