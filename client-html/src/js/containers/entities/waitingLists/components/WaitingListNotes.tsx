/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as React from "react";
import FormField from "../../../../common/components/form/form-fields/FormField";

interface Props { }

const WaitingListNotes: React.FunctionComponent<Props> = () => (
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

export default WaitingListNotes;
