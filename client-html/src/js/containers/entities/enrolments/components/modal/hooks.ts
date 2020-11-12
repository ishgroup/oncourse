/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useState } from "react";
import instantFetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler";
import EntityService from "../../../../../common/services/EntityService";

export const useOutcomeWarnings = (enrolmentPlainRecord, dispatch, selection) => {
  const [exportOutcomesWarning, setExportOutcomesWarning] = useState<string>();
  const [certificateOutcomesWarning, setCertificateOutcomesWarning] = useState<string>();

  React.useEffect(() => {
    if (enrolmentPlainRecord.items.length && selection.length === 1) {
      EntityService.getPlainRecords(
        "Outcome",
        "status",
        `enrolment.id is ${enrolmentPlainRecord.items[0].id} and fundingUploadOutcomes.fundingUpload.status in ( EXPORTED, SUCCESS )`
      ).then(res => {
        if (res.rows.length) {
          setExportOutcomesWarning(
            `${res.rows.length} outcome${res.rows.length !== 1 ? "s" : ""} won't be deleted because ${res.rows.length !== 1 ? "they are" : "it is"} part of an AVETMISS report which was successfully lodged`
          );
        } else {
          setExportOutcomesWarning(null);
        }
      }).catch(res => instantFetchErrorHandler(dispatch, res));

      EntityService.getPlainRecords(
        "Outcome",
        "status",
        `enrolment.id is ${enrolmentPlainRecord.items[0].id} and certificateOutcomes.id not is null and certificateOutcomes.certificate.revokedOn is null`
      ).then(res => {
        if (res.rows.length) {
          setCertificateOutcomesWarning(
            `${res.rows.length} outcome${res.rows.length !== 1 ? "s" : ""} won't be deleted because ${res.rows.length !== 1 ? "they are" : "it is"} linked to one or more certificates`
          );
        } else {
          setCertificateOutcomesWarning(null);
        }
      }).catch(res => instantFetchErrorHandler(dispatch, res));
    }
  }, [enrolmentPlainRecord]);

  return [exportOutcomesWarning, certificateOutcomesWarning];
};
