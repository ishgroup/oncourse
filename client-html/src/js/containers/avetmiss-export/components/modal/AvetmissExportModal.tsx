/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { AvetmissExportSettings } from "@api/model";
import Dialog from "@mui/material/Dialog";
import React, { useCallback } from "react";
import { AvetmissExportSettingsReqired } from "../../../../model/preferences";
import AvetmissExportForm from "../../containers/AvetmissExportForm";

interface Props {
  opened: boolean;
  setDialogOpened: (values: string) => void;
  closeMenu?: any;
  enrolmentsCount: number;
  ids: string[];
  entity: "CourseClass" | "Enrolment";
}

const AvetmissExportModalForm: React.FC<Props> = props => {
  const {
    opened, setDialogOpened, ids, closeMenu, enrolmentsCount, entity
  } = props;

  const onclose = useCallback(() => {
    setDialogOpened(null);
    closeMenu();
  }, []);

  const customAvetmissExportSettings = useCallback(
    ({ flavour, noAssessment }: AvetmissExportSettings): AvetmissExportSettingsReqired => ({
      flavour,
      noAssessment,
      [entity === "CourseClass" ? "classIds" : "enrolmentIds"]: ids
    }),
    [ids]
  );

  return (
    <Dialog fullScreen open={opened} onClose={onclose}>
      <div className="relative w-100">
        <AvetmissExportForm
          formatSettings={customAvetmissExportSettings}
          onClose={onclose}
          enrolmentsCount={enrolmentsCount}
        />
      </div>
    </Dialog>
  );
};

export default AvetmissExportModalForm;
