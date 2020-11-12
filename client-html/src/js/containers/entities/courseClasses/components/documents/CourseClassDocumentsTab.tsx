/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React from "react";
import { FieldArray } from "redux-form";
import { EditViewProps } from "../../../../../model/common/ListView";
import { CourseClassExtended } from "../../../../../model/entities/CourseClass";
import DocumentsRenderer from "../../../../../common/components/form/documents/DocumentsRenderer";

const CourseClassDocumentsTab: React.FC<EditViewProps<CourseClassExtended>> = ({
  twoColumn,
  dispatch,
  form,
  showConfirm
}) => (
  <div className="pl-3 pr-3 pb-2 mb-3">
    <FieldArray
      name="documents"
      label="Documents"
      entity="CourseClass"
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

export default CourseClassDocumentsTab;
