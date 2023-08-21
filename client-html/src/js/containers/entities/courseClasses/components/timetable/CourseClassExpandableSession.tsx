/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { CourseClassTutor, SessionWarning } from "@api/model";
import DeleteIcon from "@mui/icons-material/Delete";
import Edit from "@mui/icons-material/Edit";
import FileCopyOutlined from "@mui/icons-material/FileCopyOutlined";
import Checkbox from "@mui/material/Checkbox";
import FormControlLabel from "@mui/material/FormControlLabel";
import IconButton from "@mui/material/IconButton";
import clsx from "clsx";
import React, { useMemo } from "react";
import { Dispatch } from "redux";
import ExpandableItem from "../../../../../common/components/layout/expandable/ExpandableItem";
import { ClassCostExtended } from "../../../../../model/entities/CourseClass";
import { TimetableSession } from "../../../../../model/timetable";
import CalendarSession from "../../../../timetable/components/calendar/components/session/CalendarSession";
import CourseClassSessionFields from "./CourseClassSessionFields";

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
  budget: ClassCostExtended[];
  addTutorWage: (tutor: CourseClassTutor, wage?: ClassCostExtended) => void;
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
    budget,
    addTutorWage
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
      expandButtonId={`course-class-session-${session.id || session.temporaryId}`}
      classes={{
        expansionPanelRoot: "w-100",
        expandIcon: "invisible",
        expansionPanelSummayRoot: clsx(
          classes.sessionExpansionPanelSummayRoot,
          { [classes.visibleActionButtons]: openCopyDialog.session.id === session.id }
        )
      }}
      collapsedContent={<CalendarSession key={session.id} clashes={clashes} {...session} disableLink />}
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
            warnings={warnings}
            budget={budget}
            addTutorWage={addTutorWage}
          />
        )
      }
    />
  );
});

export default CourseClassExpandableSession;