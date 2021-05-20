/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback } from "react";
import Typography from "@material-ui/core/Typography";
import OutcomeEditFields from "../../outcomes/components/OutcomeEditFields";

const OutcomesHeaderBase: React.FunctionComponent<any> = React.memo((props: any) => {
  const { row } = props;

  return (
    <div className="w-100 d-grid gridTemplateColumns-1fr">
      <div>
        <Typography variant="subtitle2" noWrap>
          {row.moduleName}
        </Typography>
      </div>
    </div>
  );
});

export const OutcomesHeaderLine = OutcomesHeaderBase;

export const OutcomesContentLine: React.FunctionComponent<any> = React.memo((props: any) => {
  const { row, item, ...rest } = props;

  const getFieldName = useCallback(name => `${item}.${name}`, [item]);

  return <OutcomeEditFields {...rest} values={row} getFieldName={getFieldName} isPriorLearningBinded priorLearningEditView />;
});
