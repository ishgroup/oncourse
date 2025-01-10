/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Outcome } from "@api/model";
import React, { useEffect } from "react";
import { change } from "redux-form";
import EntityService from "../../../../common/services/EntityService";
import { EditViewProps } from "../../../../model/common/ListView";
import { getContactFullName } from "../../contacts/utils";
import OutcomeEditFields from "./OutcomeEditFields";

const getFieldName = name => name;

const OutcomeEditView = React.memo<EditViewProps<Outcome>>(props => {
  const {
    values, form, dispatch, isNew
  } = props;

  useEffect(() => {
    if (isNew && window.location.search) {
      const searchParams = new URLSearchParams(window.location.search);
      const enrolmentSearch = searchParams.get("search");
      const enrolmentId = Number(new URLSearchParams(enrolmentSearch).get("enrolment.id"));

      if (enrolmentId && !isNaN(enrolmentId)) {
          EntityService.getPlainRecords(
            "Enrolment",
            // eslint-disable-next-line max-len
            "fundingSource,vetPurchasingContractID,vetFundingSourceStateID,courseClass.vetPurchasingContractScheduleID,courseClass.deliveryMode,courseClass.reportableHours,student.contact.id,student.contact.firstName,student.contact.lastName",
            `id is ${enrolmentId}`,
            1
          ).then(res => {
            if (res.rows.length > 0) {
              dispatch(change(form, "enrolmentId", res.rows[0].id));
              dispatch(change(form, "fundingSource", res.rows[0].values[0]));
              dispatch(change(form, "vetPurchasingContractID", res.rows[0].values[1]));
              dispatch(change(form, "vetFundingSourceStateID", res.rows[0].values[2]));
              dispatch(change(form, "vetPurchasingContractScheduleID", res.rows[0].values[3]));
              dispatch(change(form, "deliveryMode", res.rows[0].values[4]));
              dispatch(change(form, "reportableHours", Number(res.rows[0].values[5] || 0)));
              dispatch(change(form, "contactId", Number(res.rows[0].values[6] || 0)));
              dispatch(
                change(
                  form,
                  "studentName",
                  getContactFullName({ firstName: res.rows[0].values[7], lastName: res.rows[0].values[8] })
                )
              );
            }
          });
      }
    }
  }, [isNew]);

  return values ? (
    <OutcomeEditFields
      {...props}
      className="p-3"
      getFieldName={getFieldName}
      isPriorLearningBinded={values.isPriorLearning}
    />
  ) : null;
});

export default OutcomeEditView;
