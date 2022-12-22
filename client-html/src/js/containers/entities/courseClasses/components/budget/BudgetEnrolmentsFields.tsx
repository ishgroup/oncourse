/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useMemo } from "react";
import { change } from "redux-form";
import { withStyles, createStyles } from "@mui/styles";
import Typography from "@mui/material/Typography";
import Grid from "@mui/material/Grid";
import debounce from "lodash.debounce";
import { Dispatch } from "redux";
import EditInPlaceField from "../../../../../common/components/form/formFields/EditInPlaceField";
import FormField from "../../../../../common/components/form/formFields/FormField";
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
    paddingRight: theme.spacing(4)
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
      <Grid item xs={3} className="centeredFlex pl-2">
        <Typography variant="body1">Enrolments</Typography>
      </Grid>
      <Grid item xs={2} className="text-end">
        <FormField type="stub" name="minimumPlaces" validate={validateNonNegative} />
        <FormField type="stub" name="maximumPlaces" validate={validateNonNegative} />
        <FormField type="stub" name="budgetedPlaces" validate={validateNonNegative} />

        <EditInPlaceField
          type="number"
          label="Minimum"
          className="flex-fill"
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
          onKeyPress={preventDecimalEnter}
          rightAligned
        />
        {seatedCapacityWarnings.min && <WarningMessage warning={seatedCapacityWarnings.min} />}
      </Grid>
      <Grid item xs={2} className="text-end">
        <EditInPlaceField
          type="number"
          label="Maximum"
          className="flex-fill"
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
          onKeyPress={preventDecimalEnter}
          rightAligned
        />
        {seatedCapacityWarnings.max && <WarningMessage warning={seatedCapacityWarnings.max} />}
      </Grid>
      <Grid item xs={2} className="text-end">
        <EditInPlaceField
          type="number"
          label="Projected"
          className="flex-fill"
          defaultValue={values.budgetedPlaces || "0"}
          meta={{
            error: errors.budgeted,
            invalid: Boolean(errors.budgeted)
          }}
          input={{
            name: "budgetedPlaces",
            onChange: e => debounceChange("budgetedPlaces", e.target.value),
            onFocus: stubFunction,
            onBlur: stubFunction
          }}
          onKeyPress={preventDecimalEnter}
          rightAligned
        />
      </Grid>
      <Grid item xs={2} className="d-flex justify-content-end">
        <Uneditable value={enrolmentsCount || "0"} label="Actual" rightAligned />
      </Grid>
      <Grid item xs={1} />
    </Grid>
  );
});

export default withStyles(styles)(BudgetEnrolmentsFields);