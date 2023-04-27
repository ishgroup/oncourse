/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { Grid } from "@mui/material";
import { FormEditorField } from "../../../../common/components/markdown-editor/FormEditor";
import ExpandableContainer from "../../../../common/components/layout/expandable/ExpandableContainer";
import { EditViewProps } from "../../../../model/common/ListView";
import { Contact } from "@api/model";

const ContactsResume: React.FC<EditViewProps<Contact>> = ({
  tabIndex,
  twoColumn,
  expanded,
  setExpanded,
  syncErrors
}) => {

  return (
    <ExpandableContainer
      index={tabIndex}
      expanded={expanded}
      setExpanded={setExpanded}
      formErrors={syncErrors}
      className="pl-3 pr-3 pb-3"
      header="RESUME"
    >
      <Grid container columnSpacing={3}>
        <Grid item xs={12} className={twoColumn ? "pt-2 pb-2" : undefined}>
          <FormEditorField name="tutor.resume" label="Resume" />
        </Grid>
      </Grid>
    </ExpandableContainer>
  );
};

export default ContactsResume;
