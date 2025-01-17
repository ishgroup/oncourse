/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Note } from '@api/model';
import { Grid } from '@mui/material';
import clsx from 'clsx';
import { AddButton, ShowConfirmCaller } from 'ish-ui';
import React, { useCallback, useState } from 'react';
import { connect } from 'react-redux';
import { arrayInsert, change, FieldArray } from 'redux-form';
import { withStyles } from 'tss-react/mui';
import { APP_BAR_HEIGHT } from '../../../../constants/Config';
import { State } from '../../../../reducers/state';
import { addActionToQueue, removeActionsFromQueue } from '../../../actions';
import instantFetchErrorHandler from '../../../api/fetch-errors-handlers/InstantFetchErrorHandler';
import uniqid from '../../../utils/uniqid';
import { deleteNoteItem, postNoteItem } from './actions';
import NotesRenderer from './components/NotesRenderer';
import NotesService from './services/NotesService';
import styles from './styles';

interface Props {
  classes?: any;
  notesHeader?: string;
  showConfirm: ShowConfirmCaller;
  twoColumn?: any;
  className?: string;
  form?: string;
  dispatch: any;
  values: any;
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
              dispatch(removeActionsFromQueue([{entity: "Note", id: deletedNote.temporaryId}]));
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
        showConfirm({
          title: null,
          confirmMessage: `Please save new record before adding notes`,
          cancelButtonText: "OK"
        });
      } else {
        const temporaryId = uniqid();
        const newNote: Note = {message: "", entityName: rootEntity, entityId: values.id};
        dispatch(arrayInsert(form, "notes", 0, {...newNote, temporaryId}));
        dispatch(addActionToQueue(postNoteItem(newNote), "POST", "Note", temporaryId));
        setTimeout(() => {
          const domNode = document.getElementById("notes[0].message");
          if (domNode) window.scrollTo({
            top: domNode.offsetTop - APP_BAR_HEIGHT,
            behavior: 'smooth'
          });
        }, 200);
      }
    }, [isNew, form, rootEntity, values.notes, values.id]);

    return (
      <Grid container columnSpacing={3} className={clsx("h-100 justify-content-center", className)}
            alignContent="flex-start">
        <Grid item xs={12}>
          <div className="centeredFlex">
            <div className="heading">
              {values.notes && values.notes.length > 0 && values.notes.length}
              {' '}
              {notesHeader}
              {values.notes && values.notes.length !== 1 ? "s" : ""}
            </div>
            <AddButton onClick={addNote}/>
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
        />
      </Grid>
    );
  }
);
const mapStateToProps = (state: State) => ({
  queuedActions: state.actionsQueue.queuedActions
});

export default connect(mapStateToProps)(withStyles(OwnApiNotes, styles));