/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useState } from "react";
import { Grid, withStyles } from "@material-ui/core";
import {
 arrayInsert, change, FieldArray
} from "redux-form";
import clsx from "clsx";
import IconButton from "@material-ui/core/IconButton";
import AddCircle from "@material-ui/icons/AddCircle";
import { Note } from "@api/model";
import { connect } from "react-redux";
import styles from "./styles";
import { State } from "../../../../reducers/state";
import { addActionToQueue, removeActionsFromQueue } from "../../../actions";
import NotesRenderer from "./components/NotesRenderer";
import { ShowConfirmCaller } from "../../../../model/common/Confirm";
import { deleteNoteItem, postNoteItem } from "./actions";
import NotesService from "./services/NotesService";
import instantFetchErrorHandler from "../../../api/fetch-errors-handlers/InstantFetchErrorHandler";
import uniqid from "../../../utils/uniqid";

interface Props {
  classes?: any;
  notesHeader?: string;
  showConfirm: ShowConfirmCaller;
  twoColumn?: any;
  className?: string;
  form: string;
  dispatch: any;
  values: any;
  leftOffset?: boolean;
  isNew: boolean;
  rootEntity: string;
}

const OwnApiNotes = React.memo<Props>(
  ({
    classes,
    className,
    showConfirm,
    twoColumn,
    notesHeader = "Note",
    rootEntity,
    dispatch,
    form,
    values,
    leftOffset,
    isNew
  }) => {
    const [showMore, setShowMore] = useState(false);

    const handleShowMore = useCallback(() => {
      setShowMore(prev => !prev);
    }, []);

    const deleteNote = useCallback(
      (index: number) => {
        const updatedNotes = [...values.notes];

        const deletedNote = updatedNotes[index];

        updatedNotes.splice(index, 1);

        showConfirm(
          {
            onConfirm: () => {
              if (deletedNote.id) {
                NotesService.validateRemove(deletedNote.id)
                  .then(() => {
                    dispatch(change(form, "notes", updatedNotes));
                    dispatch(addActionToQueue(deleteNoteItem(deletedNote.id), "DELETE", "Note", deletedNote.id));
                  })
                  .catch(response => instantFetchErrorHandler(dispatch, response));
                return;
              }
              dispatch(removeActionsFromQueue([{ entity: "Note", id: deletedNote.temporaryId }]));
              dispatch(change(form, "notes", updatedNotes));
            },
            confirmMessage: "This note will be deleted permanently.",
            confirmButtonText: "DELETE"
          }
        );
      },
      [values.notes]
    );

    const addNote = useCallback(() => {
      if (isNew) {
        showConfirm({ confirmMessage: `Please save record before adding notes`, cancelButtonText: "OK" });
      } else {
        const temporaryId = uniqid();
        const newNote: Note = { message: "", entityName: rootEntity, entityId: values.id };
        dispatch(arrayInsert(form, "notes", 0, { ...newNote, temporaryId }));
        dispatch(addActionToQueue(postNoteItem(newNote), "POST", "Note", temporaryId));
      }
    }, [isNew, form, rootEntity, values.notes, values.id]);

    return (
      <Grid container className={clsx("h-100 justify-content-center", className)} alignContent="flex-start">
        <Grid item xs={12}>
          <div className={clsx("centeredFlex", { "pl-3": !leftOffset })}>
            <div className="heading">
              {values.notes && values.notes.length}
              {' '}
              {notesHeader}
              {values.notes && values.notes.length !== 1 ? "s" : ""}
            </div>
            <IconButton onClick={addNote}>
              <AddCircle className="addButtonColor" />
            </IconButton>
          </div>
        </Grid>

        <FieldArray
          messageName="message"
          name="notes"
          component={NotesRenderer}
          showMore={showMore}
          classes={classes}
          handleShowMore={handleShowMore}
          onDelete={deleteNote}
          twoColumn={twoColumn}
          leftOffset={leftOffset}
        />
      </Grid>
    );
  }
);
const mapStateToProps = (state: State) => ({
  queuedActions: state.actionsQueue.queuedActions
});

export default connect<any, any, any>(mapStateToProps)(withStyles(styles)(OwnApiNotes));
