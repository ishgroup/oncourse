import { orderBy } from 'es-toolkit/compat';
import { initialize } from 'redux-form';
import { GET_TUTOR_ROLE_FULFILLED, getTutorRole } from '../../../js/containers/preferences/actions';
import { EpicGetTutorRole } from '../../../js/containers/preferences/containers/tutor-roles/epics/EpicGetTutorRole';
import {
  TUTOR_ROLES_FORM_NAME
} from '../../../js/containers/preferences/containers/tutor-roles/TutorRoleFormContainer';
import { DefaultEpic } from '../../common/Default.Epic';

describe("Get tutor role epic tests", () => {
  it("EpicGetTutorRole should returns correct values", () => DefaultEpic({
    action: getTutorRole("1"),
    epic: EpicGetTutorRole,
    processData: mockedApi => {
      const response = mockedApi.db.getTutorRole(1);
      const payRates = (response.payRates && response.payRates.length > 0 && orderBy(response.payRates, ["validFrom"], ["desc"]))
        || [];

      return [
        {
          type: GET_TUTOR_ROLE_FULFILLED
        },
        initialize(TUTOR_ROLES_FORM_NAME, { ...response, payRates })
      ];
    }
  }));
});
