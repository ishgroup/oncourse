/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useMemo, useRef, useState } from "react";
import {
  Card,
  Collapse,
  FormControl,
  FormHelperText,
  Grid,
  IconButton,
  Menu,
  MenuItem,
  Select,
  Typography
} from "@mui/material";
import clsx from "clsx";
import { differenceInMinutes, format } from "date-fns";
import { change, Field, WrappedFieldProps } from "redux-form";
import { ClashType, SessionWarning } from "@api/model";
import DeleteIcon from "@mui/icons-material/Delete";
import ExpandMore from "@mui/icons-material/ExpandMore";
import OpenInNew from "@mui/icons-material/OpenInNew";
import ChatIcon from '@mui/icons-material/Chat';
import { makeStyles } from "@mui/styles";
import { defaultContactName } from "../../../contacts/utils";
import ErrorMessage from "../../../../../common/components/form/fieldMessage/ErrorMessage";
import { TimetableSession } from "../../../../../model/timetable";
import { CourseClassTutorExtended } from "../../../../../model/entities/CourseClass";
import FormField from "../../../../../common/components/form/formFields/FormField";
import { formatDurationMinutes } from "../../../../../common/utils/dates/formatString";
import { NumberArgFunction } from "../../../../../model/common/CommonFunctions";
import { H_MMAAA } from "../../../../../common/utils/dates/format";
import { openInternalLink } from "../../../../../common/utils/links";
import { appendTimezone } from "../../../../../common/utils/dates/formatTimezone";
import { AppTheme } from "../../../../../model/common/Theme";
import AddButton from "../../../../../common/components/icons/AddButton";

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
    marginBottom: theme.spacing(1)
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
  }
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

interface TutorRoosterProps extends WrappedFieldProps {
  warningTypes: { [P in ClashType]: SessionWarning[] },
  session: TimetableSession;
  tutors: CourseClassTutorExtended[];
  onDeleteTutor: NumberArgFunction;
  onAddTutor: (tutor: CourseClassTutorExtended) => void;
  sessionDuration: number;
}

const CourseClassTutorRooster = (
  {
    meta: {
 invalid, error, dispatch, form 
},
    input: { name },
    warningTypes,
    session,
    tutors,
    onDeleteTutor,
    onAddTutor,
    sessionDuration
  }: TutorRoosterProps
) => {
  const [tutorsMenuOpened, setTutorsMenuOpened] = useState(false);
  const [expanded, setExpanded] = useState(null);

  const classes = useStyles();

  const tutorsRef = useRef(null);

  const filteredTutors = useMemo<CourseClassTutorExtended[]>(() => tutors
    .filter(t => t.contactId
      && t.roleName
      && !session.tutorAttendances?.some(ta => (ta.courseClassTutorId && t.id === ta.courseClassTutorId)
          || (ta.temporaryTutorId && t.temporaryId === ta.temporaryTutorId))),
    [tutors, session.tutorAttendances]);

  return (
    <FormControl error={invalid} id={name} className={classes.root}>
      <div className="centeredFlex">
        <div className={clsx("secondaryHeading mb-1 mt-1", (invalid || warningTypes?.Tutor.length) && "errorColor")}>
          TUTOR  TIME & ATTENDANCE
        </div>
        <div>
          {Boolean(filteredTutors.length) && (
          <AddButton className="p-1" ref={tutorsRef} onClick={() => setTutorsMenuOpened(true)} />
        )}
          <Menu
            anchorOrigin={{ vertical: "top", horizontal: "right" }}
            anchorEl={tutorsRef.current}
            open={tutorsMenuOpened}
            onClose={() => setTutorsMenuOpened(false)}
          >
            {filteredTutors.map(t => (
              <MenuItem
                key={t.id}
                onClick={() => {
                onAddTutor(t);
                setTutorsMenuOpened(false);
              }}
              >
                {`${defaultContactName(t.tutorName)} (${t.roleName})`}
              </MenuItem>
              ))}
          </Menu>
        </div>
      </div>

      <div>
        {session?.tutorAttendances?.map((t, index) => {
          const tutor = tutors.find(tu => (t.courseClassTutorId && tu.id === t.courseClassTutorId)
            || (t.temporaryTutorId && tu.temporaryId === t.temporaryTutorId));
          const tutorWarning = tutor ? warningTypes?.Tutor.find(w => w.referenceId === tutor.contactId) : null;
          const fieldsName = `${name}[${index}]`;

          const diffLabel = `${t.start && (t.start !== session.start || t.end !== session.end) 
            ? session.siteTimezone 
              ? `${format(appendTimezone(new Date(t.start), session.siteTimezone), H_MMAAA)}-${format(appendTimezone(new Date(t.end), session.siteTimezone), H_MMAAA)} `
              : `${format(new Date(t.start), H_MMAAA)}-${format(new Date(t.end), H_MMAAA)} ` 
            : ""}
            
          ${sessionDuration && t.actualPayableDurationMinutes && t.actualPayableDurationMinutes !== sessionDuration 
            ? `payable ${formatDurationMinutes(t.actualPayableDurationMinutes)}` 
            : ""}`;

          const onStartChange = newValue => {
            const minutesOffset = differenceInMinutes(new Date(t.end), new Date(newValue)) - differenceInMinutes(new Date(t.end), new Date(t.start));
            dispatch(change(form, `${fieldsName}.actualPayableDurationMinutes`, t.actualPayableDurationMinutes + minutesOffset));
          };

          const onEndChange = newValue => {
            const minutesOffset = differenceInMinutes(new Date(newValue), new Date(t.start)) - differenceInMinutes(new Date(t.end), new Date(t.start));
            dispatch(change(form, `${fieldsName}.actualPayableDurationMinutes`, t.actualPayableDurationMinutes + minutesOffset));
          };

          const isExpanded = expanded === index;

          return (
            <Card elevation={isExpanded ? 3 : 0} key={t.courseClassTutorId || t.temporaryTutorId} className={classes.tutorItem}>
              <Grid container columnSpacing={3}>
                <Grid item xs={6} className="centeredFlex">
                  <Typography variant="body1" className={classes.tutorItemLabel} noWrap>
                    {`${defaultContactName(t.contactName)}${tutor ? ` (${tutor.roleName})` : ""}`}
                  </Typography>
                </Grid>

                <Grid item xs={6} className="centeredFlex">
                  <div>
                    {
                        t.hasPayslip ? (
                          <Typography variant="button" display="block" className="successColor centeredFlex" noWrap>
                            Paid
                            <IconButton className="ml-05" size="small" onClick={() => openInternalLink(`/payslip?search=id in (${t.payslipIds.toString()})`)}>
                              <OpenInNew fontSize="inherit" color="secondary" />
                            </IconButton>
                          </Typography>
                        ) : (
                          <Field
                            name={`${fieldsName}.attendanceType`}
                            component={RoosterStatuses}
                            payableTime={formatDurationMinutes(t.actualPayableDurationMinutes || sessionDuration)}
                            className={clsx('hoverIconContainer', classes.statusSelect)}
                          />
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

                {!isExpanded && t.note && (
                <Grid item xs={6} className="centeredFlex">
                  <Typography variant="body2" color="textSecondary" noWrap>
                    <ChatIcon fontSize="inherit" className={classes.noteIcon} />
                    {t.note}
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
                    />
                  </Grid>
                  <Grid item xs={2}>
                    <FormField
                      name={`${fieldsName}.end`}
                      type="time"
                      label="Roster end"
                      onChange={onEndChange}
                      timezone={session.siteTimezone}
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
                <IconButton size="small" disabled={t.hasPayslip} onClick={() => onDeleteTutor(index)}>
                  <DeleteIcon fontSize="inherit" />
                </IconButton>
                <IconButton size="small" disabled={t.hasPayslip} onClick={() => setExpanded(isExpanded ? null : index)}>
                  <ExpandMore fontSize="inherit" className={clsx(classes.expandIcon, isExpanded && classes.expanded)} />
                </IconButton>
              </div>
            </Card>
          );
        })}
      </div>
      {invalid && <FormHelperText className="shakingError">{error}</FormHelperText>}
    </FormControl>
);
};

export default CourseClassTutorRooster;
