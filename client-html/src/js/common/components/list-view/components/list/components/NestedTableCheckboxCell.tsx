/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import FormField from "../../../../form/formFields/FormField";

const NestedTableCheckboxCell = props => {
  const {
    classes, fieldName, column: {onChangeHandler, disabledHandler}, row
  } = props;

  return (
    <div>
      <FormField
        type="checkbox"
        name={fieldName}
        className={classes.checkbox}
        onChangeHandler={onChangeHandler ? (e, checked) => onChangeHandler(row, checked) : undefined}
        disabled={
          typeof disabledHandler === "function" ? disabledHandler(row) : disabledHandler || false
        }
      />
    </div>
  );
};

export default NestedTableCheckboxCell;