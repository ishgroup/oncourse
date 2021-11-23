/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { Grid } from "@mui/material";
import { FormEditorField } from "../../../../common/components/markdown-editor/FormEditor";

interface ContactsResumeProps {
  twoColumn?: boolean;
}

const ContactsResume: React.FC<ContactsResumeProps> = props => {
  const { twoColumn } = props;

  return (
    <div className="p-3">
      <div className="heading mb-2">RESUME</div>
      <Grid container columnSpacing={3}>
        <Grid item xs={12} className={twoColumn ? "pt-2 pb-2" : undefined}>
          <FormEditorField name="tutor.resume" label="Resume" />
        </Grid>
      </Grid>
    </div>
  );
};

export default ContactsResume;
