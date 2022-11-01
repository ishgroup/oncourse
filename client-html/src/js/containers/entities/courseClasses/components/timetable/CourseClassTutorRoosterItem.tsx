/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import { differenceInMinutes, format } from "date-fns";
import { change, Field, WrappedFieldProps } from "redux-form";
import {
 Card, Collapse, Grid, IconButton, MenuItem, Select, Typography 
} from "@mui/material";
import OpenInNew from "@mui/icons-material/OpenInNew";
import clsx from "clsx";
import Button from "@mui/material/Button";
import ChatIcon from "@mui/icons-material/Chat";
import DeleteIcon from "@mui/icons-material/Delete";
import ExpandMore from "@mui/icons-material/ExpandMore";
import {
 ClashType, CourseClassTutor, SessionWarning, TutorAttendance 
} from "@api/model";
import { Dispatch } from "redux";
import { makeStyles } from "@mui/styles";
import { TimetableSession } from "../../../../../model/timetable";
import ErrorMessage from "../../../../../common/components/form/fieldMessage/ErrorMessage";
import FormField from "../../../../../common/components/form/formFields/FormField";
import { openInternalLink } from "../../../../../common/utils/links";
import { formatDurationMinutes } from "../../../../../common/utils/dates/formatString";
import { H_MMAAA } from "../../../../../common/utils/dates/format";
import { appendTimezone } from "../../../../../common/utils/dates/formatTimezone";
import { ClassCostExtended, CourseClassTutorExtended } from "../../../../../model/entities/CourseClass";
import { AppTheme } from "../../../../../model/common/Theme";
import { NumberArgFunction } from "../../../../../model/common/CommonFunctions";

const useStyles = makeStyles((theme: AppTheme) => ({
  root: {
    width: "100%",
  },
  tutorItem: {
    marginLeft: theme.spacing(-1),
    padding: theme.spacing(0, 1),
    position: 'relative',
    "&:hover $tutorItemActions": {
      visibility: 'visible'
    },
    marginBottom: theme.spacing(1),
    backgroundColor: "inherit"
  },
  tutorItemLabel: {
    fontWeight: 400
  },
  tutorItemActions: {
    position: "absolute",
    display: "flex",
    marginLeft: theme.spacing(1),
    visibility: "hidden",
    right: 0,
    top: 0
  },
  expanded: {
    transform: 'rotate(180deg)',
  },
  expandIcon: {
    transition: `transform ${theme.transitions.duration.shortest}ms ${theme.transitions.easing.easeInOut}`
  },
  statusSelect: {
    top: '2px'
  },
  noteIcon: {
    marginRight: theme.spacing(0.5),
    verticalAlign: "middle"
  },
  wageButtons: {
    marginLeft: theme.spacing(-1),
    "&:hover $addWage": {
      display: "flex"
    },
    "&:hover $noPay": {
      display: "none"
    },
    "& $addWage": {
      display: "none"
    }
  },
  addWage: {},
  noPay: {}
}));

interface StatusesProps extends WrappedFieldProps {
  payableTime: string;
  className: string;
}

const RoosterStatuses = ({
 input: { onChange, value },
 payableTime,
 className
}: StatusesProps) => (
  <Select
    variant="standard"
    value={value}
    onChange={onChange}
    className={className}
    IconComponent={props => (
      <span className="primaryColor">
        <ExpandMore
          {...props}
          className={clsx(props.className, 'hoverIcon invisible')}
        />
      </span>
    )}
  >
    <MenuItem value="Confirmed for payroll">
      <Typography variant="button" display="block" color="textPrimary" noWrap>
        PAY CONFIRMED
      </Typography>
    </MenuItem>
    <MenuItem value="Rejected for payroll">
      <Typography variant="button" display="block" color="error" noWrap>
        DON’T PAY
      </Typography>
    </MenuItem>
    <MenuItem value="Not confirmed for payroll">
      <Typography variant="button" display="block" color="textSecondary" noWrap>
        PAY NOT CONFIRMED
        {" "}
        {payableTime}
      </Typography>
    </MenuItem>
  </Select>
);

interface Props {
  tutorAttendance: TutorAttendance;
  warningTypes: { [P in ClashType]: SessionWarning[] };
  name: string;
  form: string;
  index: number;
  session: TimetableSession;
  sessionDuration: number;
  dispatch: Dispatch;
  expanded: number;
  tutors: CourseClassTutorExtended[];
  budget: ClassCostExtended[];
  onDeleteTutor: NumberArgFunction;
  setExpanded: NumberArgFunction;
  addTutorWage: (tutor: CourseClassTutor, wage?: ClassCostExtended) => void;
}

const CourseClassTutorRoosterItem = (
  {
    tutorAttendance,
    warningTypes,
    name,
    index,
    expanded,
    session,
    sessionDuration,
    dispatch,
    form,
    tutors,
    budget,
    addTutorWage,
    onDeleteTutor,
    setExpanded
  }: Props
) => {
  const classes = useStyles();
  
  const tutor = tutors.find(tu => (tutorAttendance.courseClassTutorId && tu.id === tutorAttendance.courseClassTutorId)
    || (tutorAttendance.temporaryTutorId && tu.temporaryId === tutorAttendance.temporaryTutorId));
  const tutorWarning = tutor ? warningTypes?.Tutor.find(w => w.referenceId === tutor.contactId) : null;
  const fieldsName = `${name}[${index}]`;

  const diffLabel = `${tutorAttendance.start && (tutorAttendance.start !== session.start || tutorAttendance.end !== session.end)
    ? session.siteTimezone
      ? `${format(appendTimezone(new Date(tutorAttendance.start), session.siteTimezone), H_MMAAA)}-${format(appendTimezone(new Date(tutorAttendance.end), session.siteTimezone), H_MMAAA)} `
      : `${format(new Date(tutorAttendance.start), H_MMAAA)}-${format(new Date(tutorAttendance.end), H_MMAAA)} `
    : ""}
            
          ${sessionDuration && tutorAttendance.actualPayableDurationMinutes && tutorAttendance.actualPayableDurationMinutes !== sessionDuration
    ? `payable ${formatDurationMinutes(tutorAttendance.actualPayableDurationMinutes)}`
    : ""}`;

  const onStartChange = newValue => {
    const minutesOffset = differenceInMinutes(new Date(tutorAttendance.end), new Date(newValue)) - differenceInMinutes(new Date(tutorAttendance.end), new Date(tutorAttendance.start));
    dispatch(change(form, `${fieldsName}.actualPayableDurationMinutes`, tutorAttendance.actualPayableDurationMinutes + minutesOffset));
  };

  const onEndChange = newValue => {
    const minutesOffset = differenceInMinutes(new Date(newValue), new Date(tutorAttendance.start)) - differenceInMinutes(new Date(tutorAttendance.end), new Date(tutorAttendance.start));
    dispatch(change(form, `${fieldsName}.actualPayableDurationMinutes`, tutorAttendance.actualPayableDurationMinutes + minutesOffset));
  };

  const isExpanded = expanded === index;

  const wage = budget.find(b => b.flowType === "Wages"
    && (b.courseClassTutorId === tutor?.id
      || (b.temporaryTutorId && b.temporaryTutorId === tutor?.temporaryId)));

  const openTutorWage = () => addTutorWage ? addTutorWage(tutor, wage) : null;

  const hasWage = Boolean(wage);

  return (
    <Card elevation={isExpanded ? 3 : 0} className={classes.tutorItem}>
      <Grid container columnSpacing={3}>
        <Grid item xs={6} className="centeredFlex">
          <Typography variant="body1" className={classes.tutorItemLabel} noWrap>
            {`${tutorAttendance.contactName}${tutor ? ` (${tutor.roleName})` : ""}`}
          </Typography>
        </Grid>

        <Grid item xs={6} className="centeredFlex">
          <div>
            {
              tutorAttendance.hasPayslip && (
                <Typography variant="button" display="block" className="successColor centeredFlex" noWrap>
                  Paid
                  <IconButton className="ml-05" size="small" onClick={() => openInternalLink(`/payslip?search=id in (${tutorAttendance.payslipIds.toString()})`)}>
                    <OpenInNew fontSize="inherit" color="secondary" />
                  </IconButton>
                </Typography>
              )
            }
            {
              !tutorAttendance.hasPayslip && hasWage && (
                <Field
                  name={`${fieldsName}.attendanceType`}
                  component={RoosterStatuses}
                  payableTime={formatDurationMinutes(tutorAttendance.actualPayableDurationMinutes || sessionDuration)}
                  className={clsx('hoverIconContainer', classes.statusSelect)}
                />
              )
            }
            {
              !tutorAttendance.hasPayslip && !hasWage && (
                <div className={classes.wageButtons}>
                  <Button
                    className={classes.noPay}
                    disabled
                  >
                    No pay
                  </Button>
                  {Boolean(addTutorWage) && (
                    <Button
                      color="primary"
                      className={classes.addWage}
                      onClick={openTutorWage}
                    >
                      Add pay
                    </Button>
                  )}
                </div>
              )
            }
          </div>
        </Grid>

        { !isExpanded
        && (
          <Grid item xs={6} className="centeredFlex">
            <Typography variant="body2" color="textSecondary">
              {diffLabel}
            </Typography>
          </Grid>
        )}

        {!isExpanded && tutorAttendance.note && (
          <Grid item xs={6} className="centeredFlex">
            <Typography variant="body2" color="textSecondary" noWrap>
              <ChatIcon fontSize="inherit" className={classes.noteIcon} />
              {tutorAttendance.note}
            </Typography>
          </Grid>
        )}

      </Grid>
      <Collapse in={isExpanded}>
        <Grid container columnSpacing={3} className="mt-1">
          <Grid item xs={2}>
            <FormField
              name={`${fieldsName}.start`}
              type="time"
              label="Roster start"
              onChange={onStartChange}
              timezone={session.siteTimezone}
              debounced={false}
            />
          </Grid>
          <Grid item xs={2}>
            <FormField
              name={`${fieldsName}.end`}
              type="time"
              label="Roster end"
              onChange={onEndChange}
              timezone={session.siteTimezone}
              debounced={false}
            />
          </Grid>
          <Grid item xs={2}>
            <FormField
              name={`${fieldsName}.actualPayableDurationMinutes`}
              type="duration"
              label="Payable time"
            />
          </Grid>
          <Grid item xs={6}>
            <FormField
              name={`${fieldsName}.note`}
              type="multilineText"
              label="Attendance notes"
            />
          </Grid>
        </Grid>
      </Collapse>
      {tutorWarning && <ErrorMessage message={tutorWarning.message} className="m-0 p-0" />}
      <div className={classes.tutorItemActions}>
        <IconButton size="small" disabled={tutorAttendance.hasPayslip} onClick={() => onDeleteTutor(index)}>
          <DeleteIcon fontSize="inherit" />
        </IconButton>
        <IconButton size="small" disabled={tutorAttendance.hasPayslip} onClick={() => setExpanded(isExpanded ? null : index)}>
          <ExpandMore fontSize="inherit" className={clsx(classes.expandIcon, isExpanded && classes.expanded)} />
        </IconButton>
      </div>
    </Card>
  );
};

export default CourseClassTutorRoosterItem;
