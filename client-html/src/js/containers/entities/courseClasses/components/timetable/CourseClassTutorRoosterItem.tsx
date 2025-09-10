/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { ClashType, CourseClassTutor, SessionWarning, TutorAttendance } from '@api/model';
import ChatIcon from '@mui/icons-material/Chat';
import DeleteIcon from '@mui/icons-material/Delete';
import ExpandMore from '@mui/icons-material/ExpandMore';
import OpenInNew from '@mui/icons-material/OpenInNew';
import { Collapse, Grid, IconButton, MenuItem, Select, Typography } from '@mui/material';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import $t from '@t';
import clsx from 'clsx';
import { differenceInMinutes, format } from 'date-fns';
import {
  appendTimezone,
  AppTheme,
  ErrorMessage,
  formatDurationMinutes,
  H_MMAAA,
  NumberArgFunction,
  openInternalLink
} from 'ish-ui';
import React from 'react';
import { Dispatch } from 'redux';
import { change, Field, WrappedFieldProps } from 'redux-form';
import { makeStyles } from 'tss-react/mui';
import { IAction } from '../../../../../common/actions/IshAction';
import FormField from '../../../../../common/components/form/formFields/FormField';
import { ClassCostExtended, CourseClassTutorExtended } from '../../../../../model/entities/CourseClass';
import { TimetableSession } from '../../../../../model/timetable';

const useStyles = makeStyles<void, 'tutorItemActions' | 'addWage' | 'noPay'>()((theme: AppTheme, _params, classes) => ({
  root: {
    width: "100%",
  },
  tutorItem: {
    marginLeft: theme.spacing(-1),
    padding: theme.spacing(0, 1),
    position: 'relative',
    [`&:hover .${classes.tutorItemActions}`]: {
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
    [`&:hover .${classes.addWage}`]: {
      display: "flex"
    },
    [`&:hover .${classes.noPay}`]: {
      display: "none"
    },
    [`& .${classes.addWage}`]: {
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
        {$t('pay_confirmed')}
      </Typography>
    </MenuItem>
    <MenuItem value="Rejected for payroll">
      <Typography variant="button" display="block" color="error" noWrap>
        {$t('dont_pay')}
      </Typography>
    </MenuItem>
    <MenuItem value="Not confirmed for payroll">
      <Typography variant="button" display="block" color="textSecondary" noWrap>
        {$t('pay_not_confirmed')}
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
  dispatch: Dispatch<IAction>
  expanded: number;
  tutors: CourseClassTutorExtended[];
  budget: ClassCostExtended[];
  onDeleteTutor: NumberArgFunction;
  setExpanded: NumberArgFunction;
  addTutorWage: (tutor: CourseClassTutor, wage?: ClassCostExtended) => void;
  disableExpand: boolean;
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
    setExpanded,
    disableExpand
  }: Props
) => {
  const { classes, cx } = useStyles();
  
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
    dispatch(change(form, `${fieldsName}.actualPayableDurationMinutes`, differenceInMinutes(new Date(tutorAttendance.end), new Date(newValue))));

  };

  const onEndChange = newValue => {
    dispatch(change(form, `${fieldsName}.actualPayableDurationMinutes`, differenceInMinutes(new Date(newValue), new Date(tutorAttendance.start))));
  };

  const isExpanded = expanded === index;

  const wageIndex = budget.findIndex(b => b.flowType === "Wages"
    && (b.courseClassTutorId === tutor?.id
      || (b.temporaryTutorId && b.temporaryTutorId === tutor?.temporaryId)));

  const openTutorWage = () => addTutorWage ? addTutorWage(tutor, wageIndex !== -1 ? { ...budget[wageIndex], index: wageIndex } : null) : null;

  const hasWage = Boolean(budget[wageIndex]);

  return (
    (<Card elevation={isExpanded ? 3 : 0} className={classes.tutorItem}>
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
                  {$t('paid')}
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
                  className={cx('hoverIconContainer', classes.statusSelect)}
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
                    {$t('no_pay')}
                  </Button>
                  {Boolean(addTutorWage) && (
                    <Button
                      color="primary"
                      className={classes.addWage}
                      onClick={openTutorWage}
                    >
                      {$t('add_pay')}
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
              label={$t('roster_start')}
              onChange={onStartChange}
              timezone={session.siteTimezone}
              debounced={false}
            />
          </Grid>
          <Grid item xs={2}>
            <FormField
              name={`${fieldsName}.end`}
              type="time"
              label={$t('roster_end')}
              onChange={onEndChange}
              timezone={session.siteTimezone}
              debounced={false}
            />
          </Grid>
          <Grid item xs={2}>
            <FormField
              name={`${fieldsName}.actualPayableDurationMinutes`}
              type="duration"
              label={$t('payable_time')}
            />
          </Grid>
          <Grid item xs={6}>
            <FormField
              name={`${fieldsName}.note`}
              type="multilineText"
              label={$t('attendance_notes')}
            />
          </Grid>
        </Grid>
      </Collapse>
      {tutorWarning && <ErrorMessage message={tutorWarning.message} className="m-0 p-0" />}
      <div className={classes.tutorItemActions}>
        <IconButton size="small" disabled={tutorAttendance.hasPayslip} onClick={() => onDeleteTutor(index)}>
          <DeleteIcon fontSize="inherit" />
        </IconButton>
        {!disableExpand && (
          <IconButton size="small" disabled={tutorAttendance.hasPayslip} onClick={() => setExpanded(isExpanded ? null : index)}>
            <ExpandMore fontSize="inherit" className={cx(classes.expandIcon, isExpanded && classes.expanded)} />
          </IconButton>
        )}
      </div>
    </Card>)
  );
};

export default CourseClassTutorRoosterItem;
