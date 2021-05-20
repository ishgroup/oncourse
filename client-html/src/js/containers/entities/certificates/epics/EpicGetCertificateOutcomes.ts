import { Epic } from "redux-observable";
import { CertificateOutcome, Outcome } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { GET_CERTIFICATE_OUTCOMES, GET_CERTIFICATE_OUTCOMES_FULFILLED } from "../actions/index";
import { formatToDateOnly } from "../../../../common/utils/dates/datesNormalizing";

const request: EpicUtils.Request<any, number> = {
  type: GET_CERTIFICATE_OUTCOMES,
  hideLoadIndicator: true,
  getData: (
    studentId,
    {
      certificates: {
        outcomes: { search }
      }
    }
  ) => {
    const searchString1 = `enrolment.student.contact.id == ${studentId} and module.id !== null`;
    const searchString2 = `priorLearning.student.contact.id == ${studentId} and module.id !== null`;

    let response: any = {};

    return EntityService.getPlainRecords(
      "Outcome",
      "createdOn,status,module.title,module.nationalCode",
      search ? `${search} and ${searchString1}` : searchString1
    ).then(result => {
      response = { ...result };

      return EntityService.getPlainRecords(
        "Outcome",
        "createdOn,status,module.title,module.nationalCode",
        search ? `${search} and ${searchString2}` : searchString2
      ).then(result => {
        response = response.rows.concat(result.rows);

        return Promise.resolve(response);
      });
    });
  },
  processData: rows => {
    const items: CertificateOutcome[] = rows.map(({ id, values }) => ({
      id: Number(id),
      issueDate: formatToDateOnly(values[0]),
      status: values[1],
      name: values[2],
      code: values[3]
    }));

    return [
      {
        type: GET_CERTIFICATE_OUTCOMES_FULFILLED,
        payload: { items, loading: false }
      }
    ];
  }
};

export const EpicGetCertificateOutcomes: Epic<any, any> = EpicUtils.Create(request);
