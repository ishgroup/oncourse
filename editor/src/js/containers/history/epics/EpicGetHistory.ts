import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../epics/EpicUtils";
import {GET_VERSIONS_FULFILLED, GET_VERSIONS_REQUEST} from "../actions";
import PublishService from "../../../services/PublishService";
import {Version} from "../../../model";

const request: EpicUtils.Request<any, any> = {
  type: GET_VERSIONS_REQUEST,
  getData: (payload, state) => PublishService.getVersions(),
  processData: (versions: Version[], state: any) => {
    return [
      {
        type: GET_VERSIONS_FULFILLED,
        payload: versions,
      },
    ];
  },
};

export const EpicGetHistory: Epic<any, any> = EpicUtils.Create(request);
