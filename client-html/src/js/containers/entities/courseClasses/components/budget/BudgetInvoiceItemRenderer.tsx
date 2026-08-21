/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import Launch from '@mui/icons-material/Launch';
import { Grid, IconButton, Typography } from '@mui/material';
import Accordion from '@mui/material/Accordion';
import AccordionDetails from '@mui/material/AccordionDetails';
import AccordionSummary from '@mui/material/AccordionSummary';
import clsx from 'clsx';
import { formatCurrency, openInternalLink } from 'ish-ui';
import React, { useCallback, useMemo, useState } from 'react';
import { BudgetExpandableProps } from './BudgetExpandableItemRenderer';

const BudgetInvoiceItemRow = React.memo<{ value, currencySymbol, classes? }>(({ value, currencySymbol, classes }) => {
  const amountValue = useMemo(() => `${formatCurrency(value.perUnitAmountExTax, currencySymbol)}`, [
    value.perUnitAmountExTax,
    currencySymbol
  ]);

  const openEnrolment = useCallback(() => {
    openInternalLink(`/invoice/${value.invoiceId}`);
  }, [value.invoiceId]);

  return (
    <Grid
      size={12}
      container
      sx={{ alignItems: 'center' }}
      direction="row"
      className={classes.tableTab}
      onDoubleClick={openEnrolment}
    >
      <Grid size={3} className="centeredFlex">
        <Typography variant="body2">{value.description}</Typography>

        <IconButton className="lightGrayIconButton" onClick={openEnrolment}>
          <Launch color="secondary" fontSize="inherit" />
        </IconButton>
      </Grid>
      <Grid size={2} className={classes.rowItemCol1}>
        <Typography variant="body2" className="money text-end">{amountValue}</Typography>
      </Grid>
      <Grid size={2} className={classes.rowItemCol2}>
        <Typography variant="body2" className="money text-end">{amountValue}</Typography>
      </Grid>
      <Grid size={2} className={classes.rowItemCol3}>
        <Typography variant="body2" className="money text-end">{amountValue}</Typography>
      </Grid>
      <Grid size={2} container sx={{ alignItems: 'center' }} className={classes.rowItemCol4}>
        <div className="flex-fill" />
        <Typography variant="body2" className="money text-end">{amountValue}</Typography>
      </Grid>
      <Grid size={1} />
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
          slotProps={{ transition: {
            unmountOnExit: true,
            mountOnEnter: true
          } }}
        >
          <AccordionSummary expandIcon={<ExpandMoreIcon />}>
            <Grid container direction="row">
              <Grid size={5}>
                <div className="secondaryHeading">{header}</div>
              </Grid>
              {!expanded && (
                <>
                  <Grid size={2} className={classes.headerItem}>
                    <Typography variant="body2">{totalLabel}</Typography>
                  </Grid>
                  <Grid size={2} className={classes.headerItem}>
                    <Typography variant="body2">{totalLabel}</Typography>
                  </Grid>
                  <Grid size={2} className={classes.headerItem}>
                    <Typography variant="body2">{totalLabel}</Typography>
                  </Grid>
                </>
              )}
            </Grid>
          </AccordionSummary>
          <AccordionDetails>
            <Grid container >
              {rowsValues.items.map((item, i) => (
                <BudgetInvoiceItemRow key={i} value={item.value} currencySymbol={currencySymbol} classes={classes} />
              ))}
              <Grid size={12} container direction="row" className={classes.tableTab}>
                <Grid size={5} />
                <Grid size={2} className={clsx("pt-1 summaryTopBorder", classes.rowItemCol2)}>
                  <Typography variant="body2" className="money text-end">{totalLabel}</Typography>
                </Grid>
                <Grid size={2} className={clsx("pt-1 summaryTopBorder", classes.rowItemCol3)}>
                  <Typography variant="body2" className="money text-end">{totalLabel}</Typography>
                </Grid>
                <Grid size={2} className={clsx("pt-1 summaryTopBorder", classes.rowItemCol4)}>
                  <Typography variant="body2" className="money text-end">{totalLabel}</Typography>
                </Grid>
                <Grid size={1} className="pt-1 summaryTopBorder" />
              </Grid>
            </Grid>
          </AccordionDetails>
        </Accordion>
      </div>
    ) : null;
  }
);

export default BudgetInvoiceItemRenderer;
