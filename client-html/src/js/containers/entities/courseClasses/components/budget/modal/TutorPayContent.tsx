/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useMemo, useState } from "react";
import { change } from "redux-form";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography/Typography";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import { createStyles, withStyles } from "@material-ui/core/styles";
import { ClassCostRepetitionType } from "@api/model";
import { Collapse, Divider } from "@material-ui/core";
import IconButton from "@material-ui/core/IconButton/IconButton";
import LockOpen from "@material-ui/icons/LockOpen";
import Lock from "@material-ui/icons/Lock";
import FormField from "../../../../../../common/components/form/form-fields/FormField";
import Uneditable from "../../../../../../common/components/form/Uneditable";
import { BudgetCostModalContentProps } from "../../../../../../model/entities/CourseClass";
import { PayRateTypes } from "./BudgetCostModal";
import { greaterThanNullValidation, validateSingleMandatoryField } from "../../../../../../common/utils/validation";
import {
  formatCurrency,
  formatFieldPercent,
  normalizeNumberToZero,
  parseFieldPercent,
  preventNegativeOrLogEnter
} from "../../../../../../common/utils/numbers/numbersNormalizing";
import { decimalMinus, decimalMul, decimalPlus } from "../../../../../../common/utils/numbers/decimalCalculation";
import { COURSE_CLASS_COST_DIALOG_FORM } from "../../../constants";
import { DefinedTutorRoleExtended } from "../../../../../../model/preferences/TutorRole";
import WarningMessage from "../../../../../../common/components/form/fieldMessage/WarningMessage";
import { getClassCostFee } from "../utils";

const styles = theme => createStyles({
  divider: {
    borderBottom: `1px solid ${theme.palette.divider}`
  },
  textRight: {
    textAlign: "right"
  },
  textTopMargin: {
    marginTop: "20px"
  },
  modalAction: {
    paddingRight: "20px"
  },
  onCostRate: {
    position: "relative",
    left: "-5px"
  }
});

interface Props extends BudgetCostModalContentProps {
  tutorRoles?: DefinedTutorRoleExtended[];
  currencySymbol?: string;
  defaultOnCostRate: number;
}

const TutorPayContent: React.FC<Props> = ({
  values,
  classValues,
  tutorRoles,
  currencySymbol,
  costLabel,
  hasMinMaxFields,
  hasCountField,
  dispatch,
  defaultOnCostRate,
  classes
}) => {
  const [onCostLocked, setOnCostLocked] = useState(values.onCostRate === null);
  const onLockClick = () => {
    setOnCostLocked(prev => {
      dispatch(change(COURSE_CLASS_COST_DIALOG_FORM, "onCostRate", prev ? defaultOnCostRate : null));
      return !prev;
    });
  };

  const tutor = useMemo(
    () => classValues.tutors.find(
      t => (values.courseClassTutorId ? t.id === values.courseClassTutorId : t.temporaryId === values.temporaryTutorId)
    ),
    [values.courseClassTutorId, values.temporaryTutorId, classValues.tutors]
  );

  const role = useMemo(() => tutorRoles.find(r => r.id === tutor.roleId), [tutor, tutorRoles]);

  const rate = useMemo(() => ((role && role["currentPayrate.rate"]) ? parseFloat(role["currentPayrate.rate"]) : 0), [role]);
  const repetitionType = useMemo(() => ((role && role["currentPayrate.type"]) ? role["currentPayrate.type"] : ""), [role]);

  const budgetedCost = useMemo(
    () => getClassCostFee(
        values,
        classValues.maximumPlaces,
        classValues.budgetedPlaces,
        classValues.successAndQueuedEnrolmentsCount,
        classValues.sessions,
        classValues.tutorAttendance
      ).projected,
    [
      values,
      classValues.maximumPlaces,
      classValues.budgetedPlaces,
      classValues.successAndQueuedEnrolmentsCount,
      classValues.sessions,
      classValues.tutorAttendance
    ]
  );

  const budgetedCostLabel = useMemo(() => formatCurrency(budgetedCost, currencySymbol), [budgetedCost, currencySymbol]);

  const budgetedIncOnCost = useMemo(
    () => decimalMul(budgetedCost, decimalPlus(values.onCostRate === 0 ? 0 : values.onCostRate || defaultOnCostRate, 1)),
    [budgetedCost, values.onCostRate, defaultOnCostRate]
  );
  const budgetedIncOnCostLabel = useMemo(() => formatCurrency(budgetedIncOnCost, currencySymbol), [
    budgetedIncOnCost,
    currencySymbol
  ]);

  const onCostTotal = useMemo(() => decimalMinus(budgetedIncOnCost, budgetedCost), [budgetedCost, budgetedIncOnCost]);
  const onCostTotalLabel = useMemo(() => formatCurrency(onCostTotal, currencySymbol), [onCostTotal, currencySymbol]);

  const typeAndCostLabel = useMemo(
    () => `${formatCurrency(values.perUnitAmountExTax, currencySymbol)} ${values.repetitionType}`,
    [values.perUnitAmountExTax, values.repetitionType, currencySymbol]
  );

  const onRepetitionChange = useCallback<any>((val: ClassCostRepetitionType, v, prev: ClassCostRepetitionType) => {
    if (val === "Per unit") {
      dispatch(change(COURSE_CLASS_COST_DIALOG_FORM, "unitCount", 1));
    }
    if (prev === "Per unit") {
      dispatch(change(COURSE_CLASS_COST_DIALOG_FORM, "unitCount", null));
    }
  }, []);

  return (
    <Grid container>
      <Grid item xs={6}>
        <Uneditable label="Contact" value={values.contactName} url={`/contact/${values.contactId}`} />
      </Grid>
      <Grid item xs={6} className="pb-2">
        <FormControlLabel
          className="checkbox"
          control={<FormField type="checkbox" name="isSunk" color="secondary" fullWidth />}
          label="Sunk cost (not recoverable if class cancelled)"
        />
      </Grid>
      <Grid item xs={12} className="pb-2">
        <FormControlLabel
          className="checkbox"
          control={<FormField type="checkbox" name="isOverriden" disabled={isNaN(rate)} />}
          label="Override assigned role pay rate."
        />
      </Grid>
      <Grid item xs={12}>
        <Collapse in={values.isOverriden}>
          <Grid container>
            <Grid item xs={4}>
              <Uneditable label="Tutor Role" value={tutor.roleName} url={`/preferences/tutorRoles/${tutor.roleId}`} />
              {isNaN(rate) && <WarningMessage warning="The chosen role has no defined rate for class period" />}
            </Grid>
            <Grid item xs={4}>
              <FormField
                type="select"
                name="repetitionType"
                label="Type"
                items={PayRateTypes}
                onChange={onRepetitionChange}
              />

              {typeof repetitionType === "string" && repetitionType !== values.repetitionType && (
                <WarningMessage warning="The selected type does not match the defined role's name" />
              )}
            </Grid>
            {hasCountField && (
              <Grid item xs={2}>
                <FormField
                  type="number"
                  name="unitCount"
                  label="Count"
                  validate={[greaterThanNullValidation, validateSingleMandatoryField]}
                  normalize={normalizeNumberToZero}
                />
              </Grid>
            )}
            <Grid item xs={hasCountField ? 2 : 4}>
              <FormField
                type="money"
                name="perUnitAmountExTax"
                label={costLabel}
                validate={greaterThanNullValidation}
                normalize={normalizeNumberToZero}
              />
              {!isNaN(rate) && rate !== values.perUnitAmountExTax && (
                <WarningMessage warning="The rate/amount entered differs from, and will override what is defined for the chosen role" />
              )}
            </Grid>
          </Grid>
        </Collapse>
      </Grid>
      <Grid item xs={12}>
        <Divider />
      </Grid>
      <Grid item container xs={12} className="pt-2">
        <Grid item xs={6} container alignItems="center">
          <Typography variant="body1" className="text-nowrap money pt-1">
            {typeAndCostLabel}
          </Typography>
          {hasCountField && !values.isOverriden && (
            <Typography variant="body1" className="text-nowrap pl-3">
              <FormField
                type="number"
                name="unitCount"
                label="Count"
                validate={[greaterThanNullValidation, validateSingleMandatoryField]}
                normalize={normalizeNumberToZero}
              />
            </Typography>
          )}
        </Grid>
        <Grid item xs={6} container alignItems="center">
          <Typography variant="body1" className="money pt-1">
            {budgetedCostLabel}
          </Typography>
        </Grid>
        <Grid item xs={6} container alignItems="center">
          <Typography variant="body1" className={classes.onCostRate}>
            <FormField
              type="persent"
              name="onCostRate"
              format={formatFieldPercent}
              parse={parseFieldPercent}
              onKeyPress={preventNegativeOrLogEnter}
              defaultValue={`${defaultOnCostRate * 100}%`}
              disabled={onCostLocked}
              formatting="inline"
            />
            <span>oncost</span>
            <span className="pl-1">
              <IconButton className="inputAdornmentButton" onClick={onLockClick}>
                {!onCostLocked && <LockOpen className="inputAdornmentIcon" />}
                {onCostLocked && <Lock className="inputAdornmentIcon" />}
              </IconButton>
            </span>
          </Typography>
        </Grid>
        <Grid item xs={6} className="textField" alignItems="center" container>
          <Typography variant="body1" className="money pt-2">
            {onCostTotalLabel}
          </Typography>
        </Grid>
      </Grid>
      <Grid item xs={6} container>
        <Typography variant="body1">Total</Typography>
      </Grid>
      <Grid item xs={6} container>
        <Typography variant="body1" className="money">
          {budgetedIncOnCostLabel}
        </Typography>
      </Grid>
      <Grid item xs={12} className="pt-2 pb-2">
        <Divider />
      </Grid>
      <Grid item xs={12}>
        <Collapse in={hasMinMaxFields}>
          <Grid container>
            <Grid item xs={6}>
              <FormField type="money" name="minimumCost" label="Minimum pay for this class" />
            </Grid>
            <Grid item xs={6}>
              <FormField type="money" name="maximumCost" label="Maximum pay for this class" />
            </Grid>
          </Grid>
        </Collapse>
      </Grid>
    </Grid>
  );
};

export default withStyles(styles)(TutorPayContent);
