/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useState } from "react";
import { Grid, withStyles } from "@material-ui/core";
import { Dispatch } from "redux";
import {
  arrayInsert, change, FieldArray
} from "redux-form";
import clsx from "clsx";
import IconButton from "@material-ui/core/IconButton";
import AddCircle from "@material-ui/icons/AddCircle";
import { Note } from "@api/model";
import { connect } from "react-redux";
import styles from "./styles";
import { showConfirm } from "../../../actions";
import NotesRenderer from "./components/NotesRenderer";
import { ShowConfirmCaller } from "../../../../model/common/Confirm";

interface Props {
  classes?: any;
  notesHeader?: string;
  showConfirm?: ShowConfirmCaller;
  twoColumn?: any;
  className?: string;
  form: string;
  dispatch: any;
  values: any;
  leftOffset?: boolean;
  isNew?: boolean;
  rootEntity?: string;
}

const NestedNotes = React.memo<Props>(
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

        updatedNotes.splice(index, 1);

        showConfirm(
          () => {
            dispatch(change(form, "notes", updatedNotes));
          },
          "This item will be removed from Notes list",
          "AGREE"
        );
      },
      [values.notes]
    );

    const addNote = useCallback(() => {
        const newNote: Note = { message: "" };
        dispatch(arrayInsert(form, "notes", 0, { ...newNote }));
      }, [isNew, form, rootEntity, values.notes, values.id]);

    return (
      <Grid container className={clsx(classes.notesSection, className)} alignContent="flex-start">
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

const mapDispatchToProps = (dispatch: Dispatch) => ({
  showConfirm: (...args) => dispatch(showConfirm(...args as [any]))
});

export default connect(null, mapDispatchToProps)(withStyles(styles)(NestedNotes));
