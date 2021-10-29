/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, {
 useMemo, useRef, useState
} from "react";
import {
  Collapse,
  FormControl, FormGroup, FormHelperText, makeStyles, Typography, Select
} from "@material-ui/core";
import clsx from "clsx";
import IconButton from "@material-ui/core/IconButton";
import AddCircle from "@material-ui/icons/AddCircle";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import { isPast } from "date-fns";
import { change, Field, WrappedFieldProps } from "redux-form";
import { ClashType, SessionWarning } from "@api/model";
import DeleteIcon from "@material-ui/icons/Delete";
import ExpandMore from "@material-ui/icons/ExpandMore";
import ChatIcon from '@material-ui/icons/Chat';
import Grid from "@material-ui/core/Grid";
import { defaultContactName } from "../../../contacts/utils";
import ErrorMessage from "../../../../../common/components/form/fieldMessage/ErrorMessage";
import { TimetableSession } from "../../../../../model/timetable";
import { CourseClassTutorExtended } from "../../../../../model/entities/CourseClass";
import FormField from "../../../../../common/components/form/formFields/FormField";
import { formatDurationMinutes } from "../../../../../common/utils/dates/formatString";
import { NumberArgFunction } from "../../../../../model/common/CommonFunctions";

const useStyles = makeStyles(theme => ({
  tutorItem: {
    "&:hover $tutorItemActions": {
      visibility: 'visible'
    }
  },
  tutorItemLabel: {
    fontWeight: 400
  },
  tutorItemActions: {
    display: "flex",
    marginLeft: theme.spacing(1),
    visibility: "hidden"
  },
  expanded: {
    transform: 'rotate(180deg)',
    transition: 'transform 150ms cubic-bezier(0.4, 0, 0.2, 1) 0ms'
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
      <Typography variant="button" display="block" className="successColor" noWrap>
        Pay scheduled
      </Typography>
    </MenuItem>
    <MenuItem value="Rejected for payroll">
      <Typography variant="button" display="block" color="error" noWrap>
        Absent
      </Typography>
    </MenuItem>
    <MenuItem value="Not confirmed for payroll">
      <Typography variant="button" display="block" color="textSecondary" noWrap>
        Payable
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
}

const CourseClassTutorRooster = (
  {
    meta: { invalid, error },
    input: { name },
    warningTypes,
    session,
    tutors,
    onDeleteTutor,
    onAddTutor
  }: TutorRoosterProps
) => {
  const [tutorsMenuOpened, setTutorsMenuOpened] = useState(false);
  const [expanded, setExpanded] = useState(null);

  const classes = useStyles();

  const tutorsRef = useRef(null);

  const isStarted = useMemo(() => isPast(new Date(session.start)), [session.start]);

  const roosterLabel = useMemo(() => (isStarted ? 'tutor  TIME & ATTENDANCE' : 'tutor Roster'), [isStarted]);

  const filteredTutors = useMemo<CourseClassTutorExtended[]>(() => tutors
    .filter(t => t.contactId
      && t.roleName
      && !session.tutorAttendances.some(ta => ta.courseClassTutorId === t.id
        || ta.temporaryTutorId === t.temporaryId)),
    [tutors, session.tutorAttendances]);

  return (
    <FormControl error={invalid} id={name} className="w-100">
      <div className="centeredFlex">
        <div className={clsx("secondaryHeading mb-1 mt-1", (invalid || warningTypes?.Tutor.length) && "errorColor")}>
          {roosterLabel}
        </div>
        <div>
          {Boolean(filteredTutors.length) && (
          <IconButton className="p-1" ref={tutorsRef} onClick={() => setTutorsMenuOpened(true)}>
            <AddCircle className="addButtonColor" />
          </IconButton>
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
        {session.tutorAttendances.map((t, index) => {
          const tutor = tutors.find(tu => (t.courseClassTutorId && tu.id === t.courseClassTutorId)
            || (t.temporaryTutorId && tu.temporaryId === t.temporaryTutorId));
          const tutorWarning = warningTypes?.Tutor.find(w => w.referenceId === tutor.contactId);
          const fieldsName = `sessions[${session.index}].tutorAttendances[${index}]`;

          return (
            <div key={t.courseClassTutorId || t.temporaryTutorId} className={classes.tutorItem}>
              <Grid container>
                <Grid item xs={4} className="centeredFlex">
                  <Typography variant="body1" className={classes.tutorItemLabel} noWrap>
                    {`${defaultContactName(t.contactName)} (${tutor.roleName})`}
                  </Typography>
                  <div className={classes.tutorItemActions}>
                    <IconButton size="small" onClick={() => setExpanded(expanded === index ? null : index)}>
                      <ExpandMore fontSize="inherit" className={expanded === index && classes.expanded} />
                    </IconButton>
                    <IconButton size="small" onClick={() => onDeleteTutor(index)}>
                      <DeleteIcon fontSize="inherit" />
                    </IconButton>
                  </div>
                </Grid>
                <Grid item xs={2}>
                  <div>
                    {isStarted && (
                      <Field
                        name={`${fieldsName}.attendanceType`}
                        component={RoosterStatuses}
                        payableTime={formatDurationMinutes(session.tutorAttendances[index].actualPayableDurationMinutes)}
                        className={clsx('hoverIconContainer', classes.statusSelect)}
                      />
                    )}
                  </div>
                </Grid>
                <Grid item xs={6} className="centeredFlex">
                  {isStarted && expanded !== index && session.tutorAttendances[index].note && (
                    <Typography variant="body2" color="textSecondary">
                      <ChatIcon fontSize="inherit" className={classes.noteIcon} />
                      {session.tutorAttendances[index].note}
                    </Typography>
                  )}
                </Grid>
              </Grid>
              <Collapse in={expanded === index}>
                <Grid container className="mt-1">
                  <Grid item xs={2}>
                    <FormField
                      name={`${fieldsName}.start`}
                      type="time"
                      label="Roster start"
                    />
                  </Grid>
                  <Grid item xs={2}>
                    <FormField
                      name={`${fieldsName}.end`}
                      type="time"
                      label="Roster end"
                    />
                  </Grid>
                  <Grid item xs={2}>
                    <FormField
                      name={`${fieldsName}.actualPayableDurationMinutes`}
                      type="duration"
                      label="Payable time"
                    />
                  </Grid>
                  {isStarted && (
                    <Grid item xs={6}>
                      <FormField
                        name={`${fieldsName}.note`}
                        type="multilineText"
                        label="Attendance notes"
                      />
                    </Grid>
                  )}
                </Grid>
              </Collapse>
              {tutorWarning && <ErrorMessage message={tutorWarning.message} className="m-0 p-0" />}
            </div>
          );
        })}
      </div>
      {invalid && <FormHelperText className="shakingError">{error}</FormHelperText>}
    </FormControl>
);
};

export default CourseClassTutorRooster;
