/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useCallback } from "react";
import Dialog from "@material-ui/core/Dialog";
import { AvetmissExportSettings } from "@api/model";
import AvetmissExportForm from "../../containers/AvetmissExportForm";
import Content from "../../../../common/components/layout/Content";
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

export const manualAvetmisConfirm = (onConfirm: AnyArgFunction, showConfirm: ShowConfirmCaller) => {
  showConfirm(
    {
      title: null,
      onConfirm,
      confirmMessage: <span>
        Exporting AVETMISS in this way is not recommended and may no longer be supported with the new AVETMISS reporting standards due in 2022.
        <br />
        <br />
        If you have a specific use-case for this export, please ensure you log a ticket with ish.
      </span>,
      confirmButtonText: "Continue",
      onCancel: () => openInternalLink("/avetmiss-export"),
      cancelButtonText: "Use regular export"
    }
  );
};

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
      <div className="relative w-100 h-100 overflow-auto defaultBackgroundColor">
        <Content>
          <AvetmissExportForm
            formatSettings={customAvetmissExportSettings}
            onClose={onclose}
            enrolmentsCount={enrolmentsCount}
          />
        </Content>
      </div>
    </Dialog>
  );
};

export default AvetmissExportModalForm;
