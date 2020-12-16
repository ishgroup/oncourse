/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useMemo, useState } from "react";
import clsx from "clsx";
import Accordion from "@material-ui/core/Accordion";
import AccordionDetails from "@material-ui/core/AccordionDetails";
import AccordionSummary from "@material-ui/core/AccordionSummary";
import Typography from "@material-ui/core/Typography";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import Launch from "@material-ui/icons/Launch";
import Grid from "@material-ui/core/Grid";
import { IconButton } from "@material-ui/core";
import { openInternalLink } from "../../../../../common/utils/links";
import { formatCurrency } from "../../../../../common/utils/numbers/numbersNormalizing";
import { BudgetExpandableProps } from "./BudgetExpandableItemRenderer";

const BudgetInvoiceItemRow = React.memo<any>(({ value, currencySymbol, classes }) => {
  const amountValue = useMemo(() => `${formatCurrency(value.perUnitAmountExTax, currencySymbol)}`, [
    value.perUnitAmountExTax,
    currencySymbol
  ]);

  const openEnrolment = useCallback(() => {
    openInternalLink(`/invoice/${value.invoiceId}`);
  }, [value.invoiceId]);

  return (
    <Grid
      item
      xs={12}
      container
      alignItems="center"
      direction="row"
      className={classes.tableTab}
      onDoubleClick={openEnrolment}
    >
      <Grid item xs={3} className="centeredFlex">
        <Typography variant="body2">{value.description}</Typography>

        <IconButton className="lightGrayIconButton" onClick={openEnrolment}>
          <Launch color="secondary" fontSize="inherit" />
        </IconButton>
      </Grid>
      <Grid item xs={2} className={classes.rowItemCol1}>
        <Typography variant="body2" className="money text-end">{amountValue}</Typography>
      </Grid>
      <Grid item xs={2} className={classes.rowItemCol2}>
        <Typography variant="body2" className="money text-end">{amountValue}</Typography>
      </Grid>
      <Grid item xs={2} className={classes.rowItemCol3}>
        <Typography variant="body2" className="money text-end">{amountValue}</Typography>
      </Grid>
      <Grid item xs={2} container alignItems="center" className={classes.rowItemCol4}>
        <div className="flex-fill" />
        <Typography variant="body2" className="money text-end">{amountValue}</Typography>
      </Grid>
      <Grid item xs={1} />
    </Grid>
  );
});

const BudgetInvoiceItemRenderer = React.memo<Partial<BudgetExpandableProps>>(
  ({
 header, rowsValues, currencySymbol, classes
}) => {
    const [expanded, setExpanded] = useState(false);

    const handleChange = useCallback((event, expanded) => {
      setExpanded(expanded);
    }, []);

    const totalLabel = useMemo(() => formatCurrency(rowsValues.max, currencySymbol), [rowsValues, currencySymbol]);

    return rowsValues.items.length ? (
      <div className={classes.root}>
        <Accordion
          expanded={expanded}
          onChange={handleChange}
          className={classes.panel}
          TransitionProps={{
            unmountOnExit: true,
            mountOnEnter: true
          }}
        >
          <AccordionSummary expandIcon={<ExpandMoreIcon />}>
            <Grid container direction="row">
              <Grid item xs={5}>
                <div className="secondaryHeading">{header}</div>
              </Grid>
              {!expanded && (
                <>
                  <Grid item xs={2} className={classes.headerItem}>
                    <Typography variant="body2">{totalLabel}</Typography>
                  </Grid>
                  <Grid item xs={2} className={classes.headerItem}>
                    <Typography variant="body2">{totalLabel}</Typography>
                  </Grid>
                  <Grid item xs={2} className={classes.headerItem}>
                    <Typography variant="body2">{totalLabel}</Typography>
                  </Grid>
                </>
              )}
            </Grid>
          </AccordionSummary>
          <AccordionDetails>
            <Grid container>
              {rowsValues.items.map((item, i) => (
                <BudgetInvoiceItemRow key={i} value={item.value} currencySymbol={currencySymbol} classes={classes} />
              ))}
              <Grid item xs={12} container direction="row" className={classes.tableTab}>
                <Grid item xs={5} />
                <Grid item xs={2} className={clsx("pt-1 summaryTopBorder", classes.rowItemCol2)}>
                  <Typography variant="body2" className="money text-end">{totalLabel}</Typography>
                </Grid>
                <Grid item xs={2} className={clsx("pt-1 summaryTopBorder", classes.rowItemCol3)}>
                  <Typography variant="body2" className="money text-end">{totalLabel}</Typography>
                </Grid>
                <Grid item xs={2} className={clsx("pt-1 summaryTopBorder", classes.rowItemCol4)}>
                  <Typography variant="body2" className="money text-end">{totalLabel}</Typography>
                </Grid>
                <Grid item xs={1} className="pt-1 summaryTopBorder" />
              </Grid>
            </Grid>
          </AccordionDetails>
        </Accordion>
      </div>
    ) : null;
  }
);

export default BudgetInvoiceItemRenderer;
