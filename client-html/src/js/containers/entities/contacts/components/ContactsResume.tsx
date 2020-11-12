/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { Grid } from "@material-ui/core";
import { FormEditorField } from "../../../../common/components/markdown-editor/FormEditor";

interface ContactsResumeProps {
  twoColumn?: boolean;
}

const ContactsResume: React.FC<ContactsResumeProps> = props => {
  const { twoColumn } = props;

  return (
    <Grid container className="p-3">
      <div className="heading mb-2">RESUME</div>
      <Grid item xs={12} className={twoColumn ? "pt-2 pb-2" : undefined}>
        <FormEditorField name="tutor.resume" label="Resume" />
      </Grid>
    </Grid>
  );
};

export default ContactsResume;
