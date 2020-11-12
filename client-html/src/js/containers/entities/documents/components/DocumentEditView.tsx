import * as React from "react";

import DocumentGeneralTab from "./DocumentGeneralTab";

const DocumentEditView = React.memo<any>((props: any) => {
  const { values } = props;

  return values ? <DocumentGeneralTab {...props} /> : null;
});

export default DocumentEditView;
