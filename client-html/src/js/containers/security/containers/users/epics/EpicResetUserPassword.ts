/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { User } from "@api/model";
import { initialize } from "redux-form";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import UserService from "../services/UsersService";
import { showMessage } from "../../../../../common/actions";
import { POST_USER_REQUEST_FULFILLED, RESET_USER_PASSWORD } from "../../../actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request<any, any, any> = {
    type: RESET_USER_PASSWORD,
    hideLoadIndicator: true,
    getData: (id: number) => UserService.resetPassword(id),
    retrieveData: () => UserService.getUsers(),
    processData: (users: User[], s, id) => [
        {
            type: POST_USER_REQUEST_FULFILLED,
            payload: { users }
        },
        showMessage({ message: "Invitation was successfully sent", success: true }),
        initialize("UsersForm", users.find(u => u.id === id))
    ],
    processError: response => FetchErrorHandler(response, "Invitation was not sent")
};

export const EpicResetUserPassword: Epic<any, any> = EpicUtils.Create(request);
