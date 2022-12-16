import React from "react";
import { FieldArray } from "redux-form";
import DocumentsRenderer from "../../../../common/components/form/documents/DocumentsRenderer";
import { EditViewProps } from "../../../../model/common/ListView";
import { Application } from "@api/model";

const ApplicationDocuments: React.FC<EditViewProps<Application>> = props => {
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