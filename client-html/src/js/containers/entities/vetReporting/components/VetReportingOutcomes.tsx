/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import React, { useMemo, useState } from "react";
import { Contact } from "@api/model";
import { EditViewProps } from "../../../../model/common/ListView";
import FullScreenStickyHeader from "../../../../common/components/list-view/components/full-screen-edit-view/FullScreenStickyHeader";
import ProfileHeading from "../../contacts/components/ProfileHeading";

const VetReportingEditView = (props: EditViewProps<Contact>) => {
  const {
    twoColumn,
    isNew,
    form,
    dispatch,
    showConfirm,
    values,
    syncErrors,
  } = props;

  const [usiUpdateLocked, setUsiUpdateLocked] = useState(true);

  const usiLocked = useMemo(
    () => values.student && values.student.usiStatus === "Verified" && usiUpdateLocked,
    [values.student && values.student.usiStatus, usiUpdateLocked]
  );

  return (
    <div className="p-3">

      <div style={{ height: 2000 }}/>
      <FullScreenStickyHeader
        isFixed={false}
        twoColumn={twoColumn}
        title="Enrolment"
        disableInteraction
      />
      <div style={{ height: 2000 }}/>
    </div>
  );
};

export default props => props.values
  ? <VetReportingEditView {...props}/>
  : null;
