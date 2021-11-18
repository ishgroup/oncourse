/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback, useState } from "react";
import { Grid } from "@mui/material";
import { withStyles } from "@mui/styles";
import { Dispatch } from "redux";
import {
  arrayInsert, change, FieldArray
} from "redux-form";
import clsx from "clsx";
import { Note } from "@api/model";
import { connect } from "react-redux";
import styles from "./styles";
import { showConfirm } from "../../../actions";
import NotesRenderer from "./components/NotesRenderer";
import { ShowConfirmCaller } from "../../../../model/common/Confirm";
import AddIcon from "../../icons/AddIcon";

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
          {
            onConfirm: () => {
              dispatch(change(form, "notes", updatedNotes));
            },
            confirmMessage: "This item will be removed from Notes list",
          }
        );
      },
      [values.notes]
    );

    const addNote = useCallback(() => {
        const newNote: Note = { message: "" };
        dispatch(arrayInsert(form, "notes", 0, { ...newNote }));
      }, [isNew, form, rootEntity, values.notes, values.id]);

    return (
      <Grid container columnSpacing={3} className={clsx(classes.notesSection, className)} alignContent="flex-start">
        <Grid item xs={12}>
          <div className={clsx("centeredFlex", { "pl-3": !leftOffset })}>
            <div className="heading">
              {values.notes && values.notes.length}
              {' '}
              {notesHeader}
              {values.notes && values.notes.length !== 1 ? "s" : ""}
            </div>
            <AddIcon onClick={addNote} />
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
  showConfirm: props => dispatch(showConfirm(props))
});

export default connect(null, mapDispatchToProps)(withStyles(styles)(NestedNotes));
