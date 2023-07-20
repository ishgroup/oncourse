/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useRef, useState } from "react";
import IconButton from "@mui/material/IconButton";
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import Settings from "@mui/icons-material/Settings";
import { Dispatch } from "redux";
import { change } from "redux-form";
import { StyledCheckbox } from "../../../../../ish-ui/formFields/CheckboxField";
import { CheckoutSummary } from "../../../../model/checkout";
import {
  checkoutSetDisableDiscounts,
  checkoutUpdateSummaryClassesDiscounts,
  checkoutUpdateSummaryListItems
} from "../../actions/checkoutSummary";
import { CHECKOUT_SUMMARY_FORM as summmaryForm } from "./CheckoutSummaryList";

interface Props {
  summary: CheckoutSummary;
  disableDiscounts: boolean;
  dispatch: Dispatch;
}

export const CheckoutSummaryCogwheel: React.FC<Props> = ({
    summary,
    disableDiscounts,
    dispatch
  }) => {
  const [opened, setOpened] = useState(false);
  const ancor = useRef();

  const sendEmaiChecked = summary.list.some(li => li.sendEmail);

  const onSendEmailClick = () => {
    const items = summary.list
      .map((item, listIndex) => ({ listIndex, item: { ...item, sendEmail: !sendEmaiChecked } }))
      .filter(item => item.item.contact.email);
    dispatch(checkoutUpdateSummaryListItems(items));
  };

  const onNoDiscountClick = () => {
    dispatch(checkoutSetDisableDiscounts(!disableDiscounts));
    if (!disableDiscounts) {
      summary.list.forEach((l, ind) => {
        l.items.forEach(li => {
          if (li.type === "course") {
            dispatch(change(summmaryForm, `${li.id}_${ind}_discount`, null));
          }
        });
      });
    }
    dispatch(checkoutUpdateSummaryClassesDiscounts());
  };

  return (
    <>
      <IconButton color="primary" ref={ancor} onClick={() => setOpened(true)}>
        <Settings color="inherit" />
      </IconButton>

      <Menu open={opened} anchorEl={ancor.current} onClose={() => setOpened(false)}>
        <MenuItem className="centeredFlex" onClick={onNoDiscountClick}>
          <StyledCheckbox checked={disableDiscounts} className="m-0" />
          <span className="ml-1">
            No discount
          </span>
        </MenuItem>
        <MenuItem className="centeredFlex" onClick={onSendEmailClick}>
          <StyledCheckbox checked={sendEmaiChecked} className="m-0" />
          <span className="ml-1">
            Send confirmation email
          </span>
        </MenuItem>
      </Menu>
    </>
);
};

