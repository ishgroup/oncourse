/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useMemo } from "react";
import clsx from "clsx";
import Accordion from "@material-ui/core/Accordion";
import AccordionDetails from "@material-ui/core/AccordionDetails";
import AccordionSummary from "@material-ui/core/AccordionSummary";
import Typography from "@material-ui/core/Typography";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import Grid from "@material-ui/core/Grid";
import { ClassCost } from "@api/model";
import Decimal from "decimal.js-light";
import { formatCurrency } from "../../../../../common/utils/numbers/numbersNormalizing";
import { NumberArgFunction, StringArgFunction } from "../../../../../model/common/CommonFunctions";
import { ClassCostType } from "../../../../../model/entities/CourseClass";
import BudgetItemRow from "./BudgetItemRow";

export interface BudgetExpandableProps {
  header: any;
  rowsValues: ClassCostType;
  currencySymbol: string;
  classes: any;
  openEditModal: (data: ClassCost) => void;
  onDeleteClassCost?: NumberArgFunction;
  expanded: boolean;
  setExpanded: StringArgFunction;
  showEmpty?: boolean;
  headerComponent?: any;
  customRowsRenderer?: any;
}

const BudgetExpandableItemRenderer: React.FC<BudgetExpandableProps> = ({
  header,
  rowsValues,
  currencySymbol,
  classes,
  openEditModal,
  onDeleteClassCost,
  expanded,
  setExpanded,
  showEmpty,
  headerComponent,
  customRowsRenderer
}) => {
  const handleChange = useCallback(() => {
    setExpanded(header);
  }, [header]);

  const maxLabel = useMemo(() => formatCurrency(rowsValues.max, currencySymbol), [currencySymbol, rowsValues.max]);

  const projectedLabel = useMemo(() => formatCurrency(rowsValues.projected, currencySymbol), [
    currencySymbol,
    rowsValues.projected
  ]);

  const actualLabel = useMemo(() => formatCurrency(rowsValues.actual, currencySymbol), [
    currencySymbol,
    rowsValues.actual
  ]);

  const percentOfProjectedValue = useMemo(
    () =>
      (rowsValues.projected <= 0
        ? 0
        : new Decimal(rowsValues.actual)
            .div(rowsValues.projected || 1)
            .mul(100)
            .toDecimalPlaces(0)
            .toFixed(2)),
    [rowsValues.projected, rowsValues.actual]
  );

  return rowsValues.items.length || showEmpty ? (
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
        <AccordionSummary
          classes={{
            root: classes.panelSumRoot,
            focused: classes.panelSumFocus
          }}
          expandIcon={<ExpandMoreIcon />}
        >
          <Grid container direction="row">
            <Grid item xs={5}>
              {headerComponent || <div className="secondaryHeading">{header}</div>}
            </Grid>
            {!expanded && (
              <>
                <Grid item xs={2} className={classes.headerItem}>
                  <Typography variant="body2" className="money">
                    {maxLabel}
                  </Typography>
                </Grid>
                <Grid item xs={2} className={classes.headerItem}>
                  <Typography variant="body2" className="money">
                    {projectedLabel}
                  </Typography>
                </Grid>
                {/* <Grid item xs={1} /> */}
                <Grid item xs={2} className={classes.headerItem}>
                  <Typography variant="body2" className="money">
                    {actualLabel}
                  </Typography>
                </Grid>
              </>
            )}
          </Grid>
        </AccordionSummary>
        <AccordionDetails>
          <Grid container>
            {customRowsRenderer
              ? customRowsRenderer({
               rowsValues, openEditModal, onDeleteClassCost, currencySymbol, classes
              })
              : rowsValues.items.map((item, i) => (
                <BudgetItemRow
                  key={i}
                  openEditModal={openEditModal}
                  onDeleteClassCost={onDeleteClassCost}
                  value={item.value}
                  currencySymbol={currencySymbol}
                  classes={classes}
                  projectedBasedValue={item.projected}
                  actualBasedValue={item.actual}
                  maxBasedValue={item.max}
                />
            ))}
            <Grid item xs={12} container direction="row" className={classes.tableTab}>
              <Grid item xs={5} />
              <Grid item xs={2} className={clsx("pt-1 summaryTopBorder", classes.rowItemCol2)}>
                <Typography variant="body2" className="money text-end">
                  {maxLabel}
                </Typography>
              </Grid>
              <Grid item xs={2} className={clsx("pt-1 summaryTopBorder", classes.rowItemCol3)}>
                <Typography variant="body2" className="money text-end">
                  {projectedLabel}
                </Typography>
              </Grid>
              <Grid item xs={2} className={clsx("pt-1 summaryTopBorder", classes.rowItemCol4)}>
                <Typography variant="body2" className="disabled">
                  (
                  {percentOfProjectedValue}
                  %)&nbsp;
                </Typography>
                <Typography variant="body2">{actualLabel}</Typography>
              </Grid>
              <Grid item xs={1} className="pt-1 summaryTopBorder" />
            </Grid>
          </Grid>
        </AccordionDetails>
      </Accordion>
    </div>
  ) : null;
};

export default BudgetExpandableItemRenderer;
