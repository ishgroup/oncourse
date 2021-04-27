import React from "react";
import { FieldArray } from "redux-form";
import DocumentsRenderer from "../../../../common/components/form/documents/DocumentsRenderer";

interface ApplicationDocumentsProps {
  classes?: any;
  dispatch?: any;
  form?: string;
  showConfirm?: any;
  twoColumn?: boolean;
}

const ApplicationDocuments: React.FC<ApplicationDocumentsProps> = props => {
  const {
    classes, dispatch, form, showConfirm, twoColumn
  } = props;
  return (
    <div className={classes.documentsRoot}>
      <FieldArray
        name="documents"
        label="Documents"
        entity="Application"
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
  );
};

export default ApplicationDocuments;
