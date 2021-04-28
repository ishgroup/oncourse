/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useMemo } from "react";
import clsx from "clsx";
import IconButton from "@material-ui/core/IconButton";
import FileCopyOutlined from "@material-ui/icons/FileCopyOutlined";
import DeleteIcon from "@material-ui/icons/Delete";
import { CourseClassTutor, SessionWarning } from "@api/model";
import { Dispatch } from "redux";
import Edit from "@material-ui/icons/Edit";
import Checkbox from "@material-ui/core/Checkbox";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import { TimetableSession } from "../../../../../model/timetable";
import CourseClassSessionFields from "./CourseClassSessionFields";
import CalendarSession from "../../../../timetable/components/calendar/components/session/CalendarSession";
import ExpandableItem from "../../../../../common/components/layout/expandable/ExpandableItem";

interface Props {
  expanded: number;
  session: TimetableSession;
  onChange: any;
  onDeleteHandler: any;
  onCopyHandler: any;
  classes: any;
  tutors: CourseClassTutor[];
  dispatch: Dispatch;
  form: string;
  triggerDebounseUpdate: any;
  selectSessionItem: (s: any) => void;
  sessionSelection: any[];
  warnings: SessionWarning[];
  setOpenCopyDialog?: ({ open, session }) => void;
  openCopyDialog?: { open?: boolean, session: { id: any } };
  prevTutorsState?: any;
}

const CourseClassExpandableSession = React.memo<Props>(props => {
  const {
    expanded,
    session,
    onChange,
    onDeleteHandler,
    onCopyHandler,
    classes,
    tutors,
    dispatch,
    triggerDebounseUpdate,
    form,
    selectSessionItem,
    sessionSelection,
    warnings,
    setOpenCopyDialog,
    openCopyDialog,
    prevTutorsState
  } = props;

  const onCopyClick = React.useCallback(e => {
    onCopyHandler(e, session);
    setOpenCopyDialog({ open: true, session: { id: session.id } });
  }, [session]);

  const onDeleteClick = e => onDeleteHandler(e, session.index, session.id);

  const isExpanded = useMemo(() => expanded !== null && [session.id, session.temporaryId].includes(expanded), [
    session.id,
    session.temporaryId,
    expanded
  ]);

  const clashes = useMemo(() => warnings.map(w => w.type), [warnings]);

  return (
    <ExpandableItem
      expanded={isExpanded}
      onChange={onChange}
      keepPaper={!session.id}
      classes={{
        expandIcon: "invisible",
        expansionPanelSummayRoot: clsx(
          classes.sessionExpansionPanelSummayRoot,
          { [classes.visibleActionButtons]: openCopyDialog.session.id === session.id }
        )
      }}
      collapsedContent={<CalendarSession key={session.id} clashes={clashes} {...session} disableLink disableTags inView />}
      buttonsContent={(
        <div className={clsx("d-flex align-items-baseline zIndex2 relative", classes.sessionActionButtonWrapper)}>
          <div className="centeredFlex">
            <FormControlLabel
              control={<Checkbox checked={sessionSelection.includes(session.id || session.temporaryId)} size="small" onClick={selectSessionItem} />}
              onClick={event => event.stopPropagation()}
              onFocus={event => event.stopPropagation()}
              label=""
              classes={{ root: classes.sessionItemFormControlRoot }}
              className={clsx(classes.sessionActionCheckBox, sessionSelection.includes(session.id) && "visible")}
            />
            <div className={clsx("align-items-baseline zIndex2", classes.sessionActionButton)}>
              <IconButton onClick={onCopyClick} size="small">
                <FileCopyOutlined fontSize="inherit" />
              </IconButton>
              <IconButton onClick={onDeleteClick} size="small" disabled={session.hasPaylines}>
                <DeleteIcon fontSize="inherit" />
              </IconButton>
              <IconButton size="small">
                <Edit fontSize="inherit" />
              </IconButton>
            </div>
          </div>
        </div>
      )}
      detailsContent={
        session && (
          <CourseClassSessionFields
            session={session}
            index={session.index}
            form={form}
            dispatch={dispatch}
            triggerDebounseUpdate={triggerDebounseUpdate}
            tutors={tutors}
            classes={classes}
            warnings={warnings}
            prevTutorsState={prevTutorsState}
          />
        )
      }
    />
  );
});

export default CourseClassExpandableSession;
