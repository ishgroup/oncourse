/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback } from "react";
import Dialog from "@mui/material/Dialog";
import { AvetmissExportSettings } from "@api/model";
import AvetmissExportForm from "../../containers/AvetmissExportForm";
import { AnyArgFunction } from "../../../../model/common/CommonFunctions";
import { ShowConfirmCaller } from "../../../../model/common/Confirm";
import { openInternalLink } from "../../../../common/utils/links";
import { AvetmissExportSettingsReqired } from "../../../../model/preferences";

interface Props {
  opened: boolean;
  setDialogOpened: (values: string) => void;
  closeMenu?: any;
  enrolmentsCount: number;
  selection: any;
  entity: "CourseClass" | "Enrolment";
}

const AvetmissExportModalForm: React.FC<Props> = props => {
  const {
    opened, setDialogOpened, selection, closeMenu, enrolmentsCount, entity
  } = props;

  const onclose = useCallback(() => {
    setDialogOpened(null);
    closeMenu();
  }, []);

  const customAvetmissExportSettings = useCallback(
    ({ flavour, noAssessment }: AvetmissExportSettings): AvetmissExportSettingsReqired => ({
      flavour,
      noAssessment,
      [entity === "CourseClass" ? "classIds" : "enrolmentIds"]: selection
    }),
    [selection]
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
