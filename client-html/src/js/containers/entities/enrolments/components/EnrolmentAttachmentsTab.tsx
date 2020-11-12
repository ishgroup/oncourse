/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import { FieldArray } from "redux-form";
import DocumentsRenderer from "../../../../common/components/form/documents/DocumentsRenderer";
import OwnApiNotes from "../../../../common/components/form/notes/OwnApiNotes";

const EnrolmentAttachmentsTab: React.FC<any> = props => {
  const {
 form, showConfirm, twoColumn, classes, dispatch
} = props;

  return (
    <div className="pr-3 saveButtonTableOffset">
      <div className="pb-3 pl-3">
        <FieldArray
          name="documents"
          label="Documents"
          entity="Enrolment"
          classes={classes}
          component={DocumentsRenderer}
          xsGrid={12}
          mdGrid={twoColumn ? 6 : 12}
          lgGrid={twoColumn ? 4 : 12}
          dispatch={dispatch}
          form={form}
          showConfirm={showConfirm}
          rerenderOnEveryChange
        />
      </div>

      <OwnApiNotes {...props} />
    </div>
  );
};

export default EnrolmentAttachmentsTab;
