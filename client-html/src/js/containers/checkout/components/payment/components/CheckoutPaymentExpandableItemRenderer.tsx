/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import IconButton from "@material-ui/core/IconButton";
import Share from "@material-ui/icons/Share";
import React from "react";
import Accordion from "@material-ui/core/Accordion";
import AccordionDetails from "@material-ui/core/AccordionDetails";
import AccordionSummary from "@material-ui/core/AccordionSummary";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import { LinkAdornment } from "../../../../../common/components/form/FieldAdornments";
import { openInternalLink } from "../../../../../common/utils/links";
import { CheckoutItem } from "../../../../../model/checkout";
import { getInvoiceLineKey } from "../../../utils";

const ItemRow = React.memo<any>(props => {
  const {
    classes, item, invoiceLine
  } = props;

  const getItemLink = (item, line) => {
    switch (item.type) {
      case "course":
        return { type: "enrolment/", id: line.id };
      case "membership":
      case "voucher":
        return { type: "sale/", id: line.id };
      case "product":
        return { type: "sale", id: `?search=id in (${line.ids.join(",")})` };
      default:
        return null;
    }
  };

  const getShareLink = (item, line) => {
    switch (item.type) {
      case "course":
        return { type: "enrolment/", id: `?search=id is ${line.id}&openShare=true` };
      default:
        return null;
    }
  };

  const lineKey = getInvoiceLineKey(item.type);
  const id = item.type === "course" ? item.class.id : item.id;
  const line = invoiceLine.find(l => l[lineKey] && (l[lineKey].productId === id || l[lineKey].classId === id));

  const link = line && line[lineKey] ? getItemLink(item, line[lineKey]) : null;
  const shareLink = line && line[lineKey] ? getShareLink(item, line[lineKey]) : null;

  return (
    <Grid item xs={12} container alignItems="center" direction="row" className={classes.tableTab}>
      <Grid item xs={9}>
        <div className="centeredFlex">
          <Typography variant="body1">
            {item.name}
            {line && line[lineKey] && item.type === "voucher" && line[lineKey].code && ` (${line[lineKey].code})`}
            {link && (
              <LinkAdornment
                linkHandler={() => openInternalLink(`/${link.type}${link.id}`)}
                link={link.id}
                className="appHeaderFontSize ml-1"
              />
            )}
            {shareLink
              && (
              <span className="appHeaderFontSize ml-1">
                <IconButton
                  onClick={() => openInternalLink(`/${shareLink.type}${shareLink.id}`)}
                  onFocus={e => e.stopPropagation()}
                  color="secondary"
                  classes={{
                    root: "inputAdornmentButton"
                  }}
                >
                  <Share className="inputAdornmentIcon" color="inherit" />
                </IconButton>
              </span>
            )}
          </Typography>
        </div>
      </Grid>
    </Grid>
  );
});

interface Props {
  classes: any;
  children?: any;
  items?: CheckoutItem[];
  header: any,
  isPayer?: boolean,
  voucherItems?: any[];
  invoiceLine?: any;
}

const CheckoutPaymentExpandableItemRenderer = React.memo<Props>(props => {
  const {
    classes,
    children,
    items,
    header,
    isPayer,
    voucherItems,
    invoiceLine
  } = props;
  const [expanded, setExpanded] = React.useState(true);

  const handleChange = React.useCallback((event, expanded) => {
    setExpanded(expanded);
  }, []);

  return (
    <>
      <div className={classes.successExpansionPanelRoot}>
        <Accordion
          expanded={expanded}
          onChange={handleChange}
          className="box-shadow-none w-100"
          TransitionProps={{
            unmountOnExit: true,
            mountOnEnter: true
          }}
        >
          <AccordionSummary expandIcon={<ExpandMoreIcon />}>
            <Grid container className="centeredFlex">
              <div className="centeredFlex flex-fill">
                <div className="heading mr-2">{header}</div>
              </div>
            </Grid>
          </AccordionSummary>
          <AccordionDetails>
            {children || (
              <Grid container>
                {items && items.filter(item => item.checked).map((item, i) => (
                  <ItemRow
                    key={i}
                    item={item}
                    classes={classes}
                    invoiceLine={invoiceLine}
                  />
                ))}
                {isPayer && voucherItems && voucherItems.length > 0 && voucherItems.filter(item => item.checked).map((item, i) => (
                  <ItemRow
                    key={i}
                    item={item}
                    classes={classes}
                    invoiceLine={invoiceLine}
                  />
                ))}
              </Grid>
            )}
          </AccordionDetails>
        </Accordion>
      </div>
    </>
  );
});

export default CheckoutPaymentExpandableItemRenderer;
