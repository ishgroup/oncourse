/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Grid } from "@mui/material";
import Button from "@mui/material/Button";
import Collapse from "@mui/material/Collapse";
import * as React from "react";
import NoteItem from "./NoteItem";

const NotesRenderer = props => {
  const {
    fields, classes, showMore, handleShowMore, messageName, onDelete, twoColumn
  } = props;

  const renderedNotes = fields.map((item, index) => {
    const field = fields.get(index);

    return (
      <NoteItem
        index={index}
        messageName={item + "." + messageName}
        classes={classes}
        key={field.id || field.temporaryId}
        item={field}
        onDelete={onDelete}
        twoColumn={twoColumn}
      />
    );
  });

  return (
    <>
      <Grid item xs={12} className="overflow-hidden">
        <Grid
          container
          wrap="wrap"
          spacing={3}
          className={classes.notesContainer}
        >
          {renderedNotes.slice(0, 6)}
        </Grid>
        <Collapse in={showMore}>
          <Grid
            container
            wrap="wrap"
            spacing={3}
            className={classes.notesContainer}
          >
            {renderedNotes.slice(6)}
          </Grid>
        </Collapse>
      </Grid>

      <Grid item xs={12} container justifyContent="center">
        {fields.length > 6 && (
          <>
            <span className={classes.showMore}>
              <Button onClick={handleShowMore} color="primary">
                Show
                {' '}
                {showMore ? "less" : "more"}
              </Button>
            </span>
          </>
        )}
      </Grid>
    </>
  );
};

export default NotesRenderer;
