/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import * as React from "react";
import FormField from "../../../../common/components/form/form-fields/FormField";

interface Props { }

const LeadNotes: React.FunctionComponent<Props> = () => (
  <div className="pl-3 saveButtonTableOffset">
    <div className="centeredFlex">
      <div className="heading pb-1">Notes</div>
    </div>
    <div>
      <FormField
        type="multilineText"
        name="studentNotes"
        disabled
        label="Student notes"
        fullWidth
      />
    </div>
    <FormField type="multilineText" name="privateNotes" label="Private notes" fullWidth />
  </div>
);

export default LeadNotes;
