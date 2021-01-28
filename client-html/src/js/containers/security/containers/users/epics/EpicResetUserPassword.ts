/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import UserService from "../services/UsersService";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import {POST_USER_REQUEST_FULFILLED, RESET_USER_PASSWORD, RESET_USER_PASSWORD_FULFILLED} from "../../../actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import {User} from "@api/model";

const request: EpicUtils.Request<any, any, any> = {
    type: RESET_USER_PASSWORD,
    hideLoadIndicator: true,
    getData: (id: number) => UserService.resetPassword(id),
    retrieveData: () => UserService.getUsers(),
    processData: (users: User[]) => [
            {
                type: POST_USER_REQUEST_FULFILLED,
                payload: { users }
            },
            {
                type: FETCH_SUCCESS,
                payload: { message: "Invitation was successfully sent" }
            }
        ],
    processError: response => FetchErrorHandler(response, "Invitation was not sent")
};

export const EpicResetUserPassword: Epic<any, any> = EpicUtils.Create(request);
