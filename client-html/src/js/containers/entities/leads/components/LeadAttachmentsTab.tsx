/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React from "react";
import { FieldArray } from "redux-form";
import { Lead } from "@api/model";
import { EditViewProps } from "../../../../model/common/ListView";
import DocumentsRenderer from "../../../../common/components/form/documents/DocumentsRenderer";
import OwnApiNotes from "../../../../common/components/form/notes/OwnApiNotes";
import FormField from "../../../../common/components/form/formFields/FormField";

interface LeadDocumentsProps {
  classes?: any;
  twoColumn?: boolean;
  values?: Lead;
  dispatch?: any;
  showConfirm?: any;
  form?: string;
}

const LeadAttachmentsTab: React.FC<EditViewProps<LeadDocumentsProps>> = (props) => {
  const {
    twoColumn,
      dispatch,
      form,
      showConfirm
  } = props;

  return (
    <div className="pr-3 saveButtonTableOffset">
      <div className="mb-2 pl-3 pb-3 pr-3 pt-2">
        <FieldArray
          name="documents"
          label="Documents"
          entity="Lead"
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
      <div>
        <OwnApiNotes {...props} />
      </div>
      <div className="p-3">
        <FormField
          type="multilineText"
          name="studentNotes"
          disabled
          label="Student notes"
                  />
      </div>
    </div>
  );
};

export default LeadAttachmentsTab;
