/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useMemo } from "react";
import clsx from "clsx";
import { change } from "redux-form";
import { withStyles, createStyles } from "@material-ui/core/styles";
import Typography from "@material-ui/core/Typography";
import Grid from "@material-ui/core/Grid";
import debounce from "lodash.debounce";
import { Dispatch } from "redux";
import EditInPlaceField from "../../../../../common/components/form/form-fields/EditInPlaceField";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import Uneditable from "../../../../../common/components/form/Uneditable";
import { AppTheme } from "../../../../../model/common/Theme";
import { normalizeNumberToZero, preventDecimalEnter } from "../../../../../common/utils/numbers/numbersNormalizing";
import { stubFunction } from "../../../../../common/utils/common";
import { CourseClassExtended, CourseClassRoom } from "../../../../../model/entities/CourseClass";
import { validateNonNegative } from "../../../../../common/utils/validation";
import WarningMessage from "../../../../../common/components/form/fieldMessage/WarningMessage";

const styles = (theme: AppTheme) => createStyles({
  root: {
    background: theme.table.contrastRow.light,
    borderRadius: theme.shape.borderRadius,
    paddingTop: theme.spacing(1.5),
    paddingLeft: theme.spacing(4),
    paddingRight: theme.spacing(4)
  },
  rowItemCol12: {
    paddingRight: 20
  },
  rowItemCol3: {
    paddingRight: 21
  },
  rowItemCol4: {
    paddingRight: 26
  }
});

interface Props {
  values: CourseClassExtended;
  classRooms: CourseClassRoom[];
  classes: any;
  enrolmentsCount: number;
  form: string;
  dispatch: Dispatch;
}

const BudgetEnrolmentsFields = React.memo<Props>(({
 values, enrolmentsCount, classes, form, dispatch, classRooms
}) => {
  const seatedCapacityWarnings = useMemo(() => {
    const warnings = {
      max: null,
      min: null
    };

    if (!values.id) {
      return warnings;
    }

    classRooms.forEach(r => {
      if (Number(r.seatedCapacity) < values.minimumPlaces) {
        warnings.min = `Exceeds ${r.name} capacity`;
      }
      if (Number(r.seatedCapacity) < values.maximumPlaces) {
        warnings.max = `Exceeds ${r.name} capacity`;
      }
    });

    return warnings;
  }, [classRooms, values.maximumPlaces, values.minimumPlaces, values.id]);

  const debounceChange = debounce((field, value) => {
    dispatch(change(form, field, normalizeNumberToZero(value)));
  }, 500);

  const errors = useMemo(
    () => ({
      minimum: validateNonNegative(values.minimumPlaces),
      maximum: validateNonNegative(values.maximumPlaces),
      budgeted: validateNonNegative(values.budgetedPlaces)
    }),
    [values.minimumPlaces, values.maximumPlaces, values.budgetedPlaces]
  );

  return (
    <Grid container direction="row" className={classes.root}>
      <Grid item xs={3} className="centeredFlex">
        <Typography variant="body1">Enrolments</Typography>
      </Grid>
      <Grid item xs={2} className={clsx(classes.rowItemCol12, "text-end")}>
        <FormField type="stub" name="minimumPlaces" validate={validateNonNegative} />
        <FormField type="stub" name="maximumPlaces" validate={validateNonNegative} />
        <FormField type="stub" name="budgetedPlaces" validate={validateNonNegative} />

        <EditInPlaceField
          type="number"
          label="Minimum"
          defaultValue={values.minimumPlaces || "0"}
          meta={{
            error: errors.minimum,
            invalid: Boolean(errors.minimum)
          }}
          input={{
            onChange: e => debounceChange("minimumPlaces", e.target.value),
            onFocus: stubFunction,
            onBlur: stubFunction
          }}
          classes={{ textField: "text-end", fitWidth: "flex-fill" }}
          onKeyPress={preventDecimalEnter}
        />
        {seatedCapacityWarnings.min && <WarningMessage warning={seatedCapacityWarnings.min} />}
      </Grid>
      <Grid item xs={2} className={clsx(classes.rowItemCol12, "text-end")}>
        <EditInPlaceField
          type="number"
          label="Maximum"
          defaultValue={values.maximumPlaces || "0"}
          meta={{
            error: errors.maximum,
            invalid: Boolean(errors.maximum)
          }}
          input={{
            onChange: e => debounceChange("maximumPlaces", e.target.value),
            onFocus: stubFunction,
            onBlur: stubFunction
          }}
          classes={{ textField: "text-end", fitWidth: "flex-fill" }}
          onKeyPress={preventDecimalEnter}
        />
        {seatedCapacityWarnings.max && <WarningMessage warning={seatedCapacityWarnings.max} />}
      </Grid>
      <Grid item xs={2} className={clsx(classes.rowItemCol3, "text-end")}>
        <EditInPlaceField
          name="budgetedPlaces"
          type="number"
          label="Projected"
          defaultValue={values.budgetedPlaces || "0"}
          meta={{
            error: errors.budgeted,
            invalid: Boolean(errors.budgeted)
          }}
          input={{
            onChange: e => debounceChange("budgetedPlaces", e.target.value),
            onFocus: stubFunction,
            onBlur: stubFunction
          }}
          classes={{ textField: "text-end", fitWidth: "flex-fill" }}
          onKeyPress={preventDecimalEnter}
        />
      </Grid>
      <Grid item xs={2} className={clsx("d-flex justify-content-end", classes.rowItemCol4)}>
        <Uneditable value={enrolmentsCount || "0"} label="Actual" className="text-end" />
      </Grid>
      <Grid item xs={1} />
    </Grid>
  );
});

export default withStyles(styles)(BudgetEnrolmentsFields);
