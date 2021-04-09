/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useMemo } from "react";
import clsx from "clsx";
import Grid from "@material-ui/core/Grid";
import { change } from "redux-form";
import Button from "@material-ui/core/Button";
import { FormControlLabel } from "@material-ui/core";
import { Holiday, RepeatEndEnum, RepeatEnum } from "@api/model";
import Card from "@material-ui/core/Card";
import { Dispatch } from "redux";
import { format } from "date-fns";
import { normalizeNumberToPositive } from "../../../utils/numbers/numbersNormalizing";
import FormField from "../form-fields/FormField";
import { StyledCheckbox } from "../form-fields/CheckboxField";
import { repeatEndListItems, repeatListItems } from "../../../../containers/preferences/containers/holidays/ListItems";
import AvailabilityNextHint from "./AvailabilityNextHint";
import { validateMinMaxDate } from "../../../utils/validation";
import { YYYY_MM_DD_MINUSED } from "../../../utils/dates/format";

interface Props {
  fieldName: string;
  formName: string;
  index: number;
  threeColumn: boolean;
  classes: any;
  availabilityLayout: any;
  item: string;
  field: Holiday;
  onDelete: any;
  dispatch: Dispatch;
  timezone?: string;
}

const AvailabilityItem: React.FC<Props> = ({
  threeColumn,
  classes,
  availabilityLayout,
  item,
  field,
  index,
  onDelete,
  fieldName,
  formName,
  dispatch,
  timezone
}) => {
  const allDay = useMemo(() => Boolean(field.startDate && field.endDate), [field.startDate, field.endDate]);

  const validateMaxDate = useCallback(
    (value, allValues) => validateMinMaxDate(
        value,
        "",
        allValues[fieldName][index].endDate
          ? allValues[fieldName][index].endDate
          : allValues[fieldName][index].endDateTime
      ),
    [fieldName, index]
  );

  const validateMinDate = useCallback(
    (value, allValues) => validateMinMaxDate(
        value,
        allValues[fieldName][index].startDate
          ? allValues[fieldName][index].startDate
          : allValues[fieldName][index].startDateTime,
        ""
      ),
    [fieldName, index]
  );

  const onAllDayChange = useCallback(
    (e, v) => {
      if (v) {
        if (field.startDateTime) {
          dispatch(change(formName, `${item}.startDate`, format(new Date(field.startDateTime), YYYY_MM_DD_MINUSED)));
        }
        if (field.endDateTime) {
          dispatch(change(formName, `${item}.endDate`, format(new Date(field.endDateTime), YYYY_MM_DD_MINUSED)));
        }
        dispatch(change(formName, `${item}.startDateTime`, null));
        dispatch(change(formName, `${item}.endDateTime`, null));
      } else {
        if (field.startDate) {
          dispatch(change(formName, `${item}.startDateTime`, new Date(field.startDate).toISOString()));
        }
        if (field.endDate) {
          dispatch(change(formName, `${item}.endDateTime`, new Date(field.endDate).toISOString()));
        }
        dispatch(change(formName, `${item}.startDate`, null));
        dispatch(change(formName, `${item}.endDate`, null));
      }
    },
    [item, field.startDate, field.endDate, field.startDateTime, field.endDateTime]
  );

  return (
    <Card id={`holidays-item-${index}`} className={threeColumn ? classes.threeColumnCard : "card"}>
      <Grid container spacing={2}>
        <Grid item xs={availabilityLayout[1].xs}>
          <Grid container>
            <Grid
              item
              xs={availabilityLayout[2].xs}
              className={clsx({
                "d-flex-start justify-content-space-between": threeColumn
              })}
            >
              <FormField type="text" name={`${item}.description`} label="Description" fullWidth />
              {threeColumn && (
                <div className="d-flex errorColor">
                  <div className="flex-fill" />
                  <Button size="small" color="inherit" onClick={onDelete.bind(null, field, index)}>
                    Delete
                  </Button>
                </div>
              )}
            </Grid>

            <Grid item xs={availabilityLayout[3].xs}>
              <FormControlLabel
                control={<StyledCheckbox checked={allDay} onChange={onAllDayChange} />}
                label="All day"
              />
            </Grid>

            <Grid item xs={availabilityLayout[4].xs}>
              <FormField
                type={allDay ? "date" : "dateTime"}
                timezone={timezone}
                name={allDay ? `${item}.startDate` : `${item}.startDateTime`}
                label="Start"
                maxDate={field.endDate}
                validate={validateMaxDate}
                className="pr-2"
                required
              />
            </Grid>

            <Grid item xs={availabilityLayout[5].xs}>
              <FormField
                type={allDay ? "date" : "dateTime"}
                timezone={timezone}
                name={allDay ? `${item}.endDate` : `${item}.endDateTime`}
                label="End"
                minDate={field.startDate}
                validate={validateMinDate}
                className="pr-2"
                required
              />
            </Grid>

            <Grid item xs={availabilityLayout[6].xs} className={threeColumn ? "mt-2" : ""}>
              <FormField
                type="select"
                name={`${item}.repeat`}
                label="Repeat every"
                items={repeatListItems}
              />
            </Grid>

            {field.repeat !== RepeatEnum.none && (
              <Grid item xs={availabilityLayout[7].xs}>
                <FormField
                  type="select"
                  name={`${item}.repeatEnd`}
                  label="End repeat"
                  items={repeatEndListItems}
                  className="pr-2"
                />
              </Grid>
            )}

            {field.repeat !== RepeatEnum.none && field.repeatEnd === RepeatEndEnum.onDate && (
              <Grid item xs={availabilityLayout[8].xs}>
                <FormField
                  type="date"
                  timezone={timezone}
                  name={`${item}.repeatOn`}
                  label="On date"
                  minDate={field.startDate}
                  validate={validateMinDate}
                  className="pr-2"
                  required
                />
              </Grid>
            )}

            {field.repeat !== RepeatEnum.none && field.repeatEnd === RepeatEndEnum.after && (
              <Grid item xs={availabilityLayout[9].xs}>
                <FormField
                  name={`${item}.repeatEndAfter`}
                  normalize={normalizeNumberToPositive}
                  label="Times"
                  type="number"
                  min="0"
                  required
                />
              </Grid>
            )}
          </Grid>
        </Grid>
        <Grid item xs={availabilityLayout[10].xs}>
          {!threeColumn && (
            <div className="d-flex errorColor">
              <div className="flex-fill" />
              <Button size="small" color="inherit" onClick={onDelete.bind(null, field, index)}>
                Delete
              </Button>
            </div>
          )}

          <AvailabilityNextHint timezone={timezone} item={field} className={classes.hintContainer} />
        </Grid>
      </Grid>
    </Card>
  );
};

export default AvailabilityItem;
