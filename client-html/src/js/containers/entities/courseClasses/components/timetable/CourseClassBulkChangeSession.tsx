/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useEffect, useMemo } from "react";
import clsx from "clsx";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import {
  arrayPush, arrayRemove, change, Field, getFormValues, initialize, reduxForm, submit
} from "redux-form";
import Dialog from "@material-ui/core/Dialog";
import Grid from "@material-ui/core/Grid/Grid";
import DialogContent from "@material-ui/core/DialogContent";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import FormGroup from "@material-ui/core/FormGroup";
import DialogActions from "@material-ui/core/DialogActions";
import MuiButton from "@material-ui/core/Button";
import FormControl from "@material-ui/core/FormControl";
import FormHelperText from "@material-ui/core/FormHelperText";
import Typography from "@material-ui/core/Typography";
import createStyles from "@material-ui/core/styles/createStyles";
import withStyles from "@material-ui/core/styles/withStyles";
import Collapse from "@material-ui/core/Collapse";
import { StyledCheckbox } from "../../../../../common/components/form/form-fields/CheckboxField";
import Button from "../../../../../common/components/buttons/Button";
import FormField from "../../../../../common/components/form/form-fields/FormField";
import { State } from "../../../../../reducers/state";
import { defaultContactName } from "../../../contacts/utils";
import { getPlainRooms } from "../../../rooms/actions";
import { CourseClassTutorExtended } from "../../../../../model/entities/CourseClass";
import { stubFunction } from "../../../../../common/utils/common";
import { greaterThanNullValidation } from "../../../../../common/utils/validation";
import EditInPlaceDurationField from "../../../../../common/components/form/form-fields/EditInPlaceDurationField";
import { courseClassCloseBulkUpdateModal } from "./actions";

const COURSE_CLASS_BULK_UPDATE_FORM: string = "CourseClassBulkUpdateForm";

const styles = theme => createStyles({
  paperDialog: {
    background: theme.palette.background.default
  },
  bulkChangeDialogContent: {
    "&:first-child": {
      paddingTop: theme.spacing(1)
    }
  },
  bulkChangeDaysInput: {
    maxWidth: theme.spacing(10)
  },
  bullkWrapperItem: {
    paddingLeft: theme.spacing(4)
  },
  disabledHeading: {
    color: `${theme.palette.text.secondary} !important`
  }
});

const validateDuration = value => (value !== "" && (value < 5 || value > 1440)
  ? "Each entry in the timetable cannot be shorter than 5 minutes or longer than 24 hours."
  : undefined);

const siteRoomLabel = site => site.name;

const normalizeStartDate = (value, prevValue) => value || prevValue;

const initialValues = {
  tutorsChecked: false,
  courseClassTutorIds: [],
  temporaryTutorIds: [],
  tutors: [],
  contactIds: [],
  locationChecked: false,
  siteId: null,
  roomId: null,
  room: "",
  site: "",
  siteTimezone: "",
  startChecked: false,
  start: "",
  durationChecked: false,
  duration: 0,
  payableDurationChecked: false,
  payableDuration: 0,
  moveForwardChecked: false,
  moveForward: "",
  moveBackwardChecked: false,
  moveBackward: ""
};

const CourseClassBulkChangeSessionForm: React.FC<any> = props => {
  const {
    handleSubmit,
    form,
    dispatch,
    reset,
    asyncValidating,
    dirty,
    invalid,
    opened,
    classes,
    init,
    tutors,
    selection,
    getRooms,
    rooms,
    bulkValues,
    sessions
  } = props;

  const classTimezone = useMemo(() => {
    if (!sessions) {
      return null;
    }
    const sessionWithTimezone = sessions.find(s => s.siteTimezone);

    return sessionWithTimezone ? sessionWithTimezone.siteTimezone : null;
  }, [sessions]);

  const [initial, setInitial] = React.useState(bulkValues || initialValues);

  React.useEffect(() => {
    if (!bulkValues) {
      init();
    } else {
      setInitial(bulkValues);
    }
  }, [bulkValues]);

  useEffect(() => {
    if (initial.siteId && !initial.roomId && rooms.length) {
      dispatch(change(form, "roomId", rooms[0].value));
      dispatch(change(form, "room", rooms[0].label));
    }
  }, [rooms]);

  useEffect(() => {
    if (!rooms.length && initial.siteId) {
      getRooms(`site.id is ${initial.siteId}`);
    }
  }, []);

  const onTutorChange = useCallback(
    (checked, tutor: CourseClassTutorExtended) => {
      if (checked) {
        dispatch(arrayPush(form, "tutors", tutor.tutorName));
        if (tutor.id) {
          dispatch(arrayPush(form, "courseClassTutorIds", tutor.id));
        } else {
          dispatch(arrayPush(form, "temporaryTutorIds", tutor.temporaryId));
        }
        dispatch(
          arrayPush(form, "contactIds", tutor.contactId)
        );
      } else {
        const index = initial.tutors.findIndex(name => name === tutor.tutorName);
        dispatch(arrayRemove(form, "tutors", index));

        const contactIdIndex = initial.contactIds.findIndex(id => id === tutor.contactId);
        dispatch(arrayRemove(form, "contactIds", contactIdIndex));

        if (tutor.id) {
          dispatch(arrayRemove(form, "courseClassTutorIds", index));
        } else {
          dispatch(arrayRemove(form, "temporaryTutorIds", index));
        }
      }
    },
    [form, tutors, initial.courseClassTutorIds, initial.temporaryTutorIds]
  );

  const courseClassTutorIdsField = useCallback(
    ({ meta: { invalid, error } }) => tutors.length > 0 && (
      <FormControl error={invalid}>
        <FormGroup>
          {tutors.map(t => (
            <FormControlLabel
              key={t.id || t.temporaryId}
              className="checkbox"
              control={(
                <StyledCheckbox
                  checked={
                    initial.courseClassTutorIds.includes(t.id) || initial.temporaryTutorIds.includes(t.temporaryId)
                  }
                  onChange={(e, v) => onTutorChange(v, t)}
                  color="secondary"
                />
              )}
              label={`${defaultContactName(t.tutorName)} (${t.roleName})`}
            />
          ))}
        </FormGroup>
        {invalid && <FormHelperText>{error}</FormHelperText>}
      </FormControl>
    ),
    [tutors, initial.courseClassTutorIds, initial.temporaryTutorIds]
  );

  const onSiteIdChange = useCallback(
    site => {
      dispatch(change(form, "site", site.name));
      dispatch(change(form, "siteTimezone", site.localTimezone));
      dispatch(change(form, "room", null));
      dispatch(change(form, "roomId", null));
      getRooms(`site.id is ${site.id}`);
    },
    [form]
  );

  const onRoomIdChange = useCallback(
    room => {
      dispatch(change(form, "room", room.label));
    },
    []
  );

  const durationValue = useMemo(() => initial.duration, [initial.duration]);

  const payableDurationValue = useMemo(() => initial.payableDuration, [initial.payableDuration]);

  const onPayableDurationChange = useCallback(
    (durationMinutes: number) => {
      if (durationMinutes !== null) {
        dispatch(change(form, "payableDuration", durationMinutes));
      }
    },
    [form, initial]
  );

  const durationError = useMemo(() => greaterThanNullValidation(durationValue) || validateDuration(durationValue), [
    durationValue
  ]);

  const onDurationChange = useCallback(
    (durationMinutes: number) => {
      if (durationMinutes) {
        dispatch(change(form, "duration", durationMinutes));
      }
    },
    []
  );

  const onClose = React.useCallback(() => {
    dispatch(courseClassCloseBulkUpdateModal());
    reset();
  }, []);

  const submitChanges = () => {
    dispatch(submit(form));
  };

  return (
    <Dialog
      open={opened}
      onClose={onClose}
      disableAutoFocus
      disableEnforceFocus
      disableRestoreFocus
      maxWidth="sm"
      fullWidth
      classes={{
        paper: classes.paperDialog
      }}
    >
      <form autoComplete="off" noValidate onSubmit={handleSubmit}>
        <DialogContent
          classes={{
            root: classes.bulkChangeDialogContent
          }}
        >
          <Grid container>
            <Grid item xs={12}>
              <div className={clsx("centeredFlex")}>
                <div className="heading mt-2 mb-2">
                  Bulk change
                </div>
              </div>
              <div className="pb-2">
                {`Update ${selection.length} timetable event${selection.length > 1 ? "s" : ""}`}
              </div>
              {tutors.length > 0 && (
                <BulkItemWrapper classes={classes} title="Tutors" name="tutors">
                  <div className={classes.sessionTutors}>
                    <Field
                      name="courseClassTutorIds"
                      component={courseClassTutorIdsField}
                    />
                  </div>
                </BulkItemWrapper>
              )}
            </Grid>
            <Grid item xs={12}>
              <BulkItemWrapper classes={classes} title="Location" name="location">
                <Grid container>
                  <Grid item xs={6}>
                    <FormField
                      type="remoteDataSearchSelect"
                      name="siteId"
                      label="Site"
                      entity="Site"
                      selectValueMark="id"
                      selectLabelMark="name"
                      selectLabelCondition={siteRoomLabel}
                      defaultDisplayValue={initial.site}
                      onInnerValueChange={onSiteIdChange}
                      rowHeight={36}
                    />
                  </Grid>
                  <Grid item xs={6}>
                    <FormField
                      type="select"
                      name="roomId"
                      label="Room"
                      defaultValue={initial.room}
                      items={rooms || []}
                      disabled={!initial.siteId}
                      onInnerValueChange={onRoomIdChange}
                    />
                  </Grid>
                </Grid>
              </BulkItemWrapper>
            </Grid>
            <Grid item xs={12}>
              <BulkItemWrapper classes={classes} title="Payable duration" name="payableDuration">
                <EditInPlaceDurationField
                  label="Payable duration"
                  meta={{}}
                  input={{
                    value: payableDurationValue,
                    onChange: stubFunction,
                    onBlur: onPayableDurationChange,
                    onFocus: stubFunction
                  }}
                  hideLabel
                />
              </BulkItemWrapper>
            </Grid>
            <Grid item xs={12}>
              <BulkItemWrapper
                classes={classes}
                title={`Start time ${initial.siteTimezone ? `(${initial.siteTimezone})` : classTimezone ? `(${classTimezone})` : ""}`}
                name="start"
              >
                <FormField
                  type="time"
                  name="start"
                  label={`Start time ${initial.siteTimezone ? `(${initial.siteTimezone})` : classTimezone ? `(${classTimezone})` : ""}`}
                  normalize={normalizeStartDate}
                  timezone={initial.siteTimezone || classTimezone}
                  hideLabel
                />
              </BulkItemWrapper>
            </Grid>
            <Grid item xs={12}>
              <BulkItemWrapper classes={classes} title="Duration" name="duration">
                <EditInPlaceDurationField
                  label="Duration"
                  meta={{
                    error: durationError,
                    invalid: Boolean(durationError)
                  }}
                  input={{
                    value: durationValue,
                    onChange: stubFunction,
                    onBlur: onDurationChange,
                    onFocus: stubFunction
                  }}
                  hideLabel
                />
              </BulkItemWrapper>
            </Grid>
            <Grid item xs={12}>
              <BulkItemWrapper classes={classes} title="Move forward" name="moveForward">
                <FormField
                  type="number"
                  name="moveForward"
                  formatting="inline"
                  step="1"
                  className={classes.bulkChangeDaysInput}
                  hideArrows
                />
                {" "}
                days
              </BulkItemWrapper>
            </Grid>
            <Grid item xs={12}>
              <BulkItemWrapper classes={classes} title="Move backward" name="moveBackward">
                <FormField
                  name="moveBackward"
                  type="number"
                  formatting="inline"
                  step="1"
                  className={classes.bulkChangeDaysInput}
                  hideArrows
                />
                {" "}
                days
              </BulkItemWrapper>
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions className="p-3">
          <MuiButton color="primary" onClick={onClose}>
            Cancel
          </MuiButton>
          <Button
            variant="contained"
            color="primary"
            onClick={submitChanges}
            disabled={invalid || !dirty || Boolean(asyncValidating)}
          >
            Update
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
};

const BulkItemWrapper: React.FC<any> = props => {
  const {
 classes, title, name, children
} = props;
  const [opened, setOpened] = React.useState(false);

  const onChange = React.useCallback(checked => {
    setOpened(checked);
  }, [name]);

  return (
    <div className="mb-2">
      <FormControlLabel
        control={(
          <FormField
            type="checkbox"
            name={`${name}Checked`}
            color="secondary"
            checked={opened}
            onChange={onChange}
          />
        )}
        label={(
          <Typography variant="body2" className={clsx("secondaryHeading", { [classes.disabledHeading]: !opened })}>
            {title}
          </Typography>
        )}
      />
      <Collapse in={opened} className={classes.bullkWrapperItem}>{children}</Collapse>
    </div>
  );
};

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  init: () => {
    dispatch(initialize(COURSE_CLASS_BULK_UPDATE_FORM, initialValues));
  },
  getRooms: (search: string) => {
    dispatch(getPlainRooms(null, null, null, null, search));
  }
});

const mapStateToProps = (state: State) => ({
  bulkValues: getFormValues(COURSE_CLASS_BULK_UPDATE_FORM)(state),
  rooms: state.rooms.items,
  tutors: state.courseClassesBulkSession.tutors,
  selection: state.courseClassesBulkSession.selection
});

const CourseClassBulkChangeSession = reduxForm({
  form: COURSE_CLASS_BULK_UPDATE_FORM,
  initialValues
})(CourseClassBulkChangeSessionForm);

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(
  withStyles(styles)(CourseClassBulkChangeSession)
);
