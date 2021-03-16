/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { Site } from "@api/model";
import { initialize } from "redux-form";
import { getNoteItems } from "../../../../common/components/form/notes/actions";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_SITE_ITEM, GET_SITE_ITEM_FULFILLED, UPDATE_SITE_ITEM } from "../actions/index";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions/index";
import { getEntityItemById } from "../../common/entityItemsService";
import GoogleApiService from "../../../../common/components/google-maps/services/GoogleApiService";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { clearActionsQueue, FETCH_FAIL } from "../../../../common/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: GET_SITE_ITEM,
  getData: (id: number) => getEntityItemById("Site", id).then((site: Site) => {
      if (!site.latitude && !site.longitude && site.street && site.suburb && site.country && site.country.name) {
        return GoogleApiService.getGeocodeDetails([site.street, site.suburb, site.country.name].toString()).then(
          (response: any) => {
            if (response.status === "OK") {
              const { location } = response.results[0].geometry;

              return { site: { ...site, latitude: location.lat, longitude: location.lng }, updateSite: true };
            }

            return { site: { ...site }, updateSite: false, error: true };
          }
        );
      }
      return { site: { ...site }, updateSite: false, error: false };
    }),
  processData: ({ site, updateSite, error }, s, id) => (updateSite
      ? [
          {
            type: UPDATE_SITE_ITEM,
            payload: { id: site.id, site, message: "Site coordinates was updated" }
          }
        ]
      : [
          {
            type: GET_SITE_ITEM_FULFILLED
          },
          {
            type: SET_LIST_EDIT_RECORD,
            payload: { editRecord: site, name: site.name }
          },
          initialize(LIST_EDIT_VIEW_FORM_NAME, site),
          getNoteItems("Site", id, LIST_EDIT_VIEW_FORM_NAME),
          ...(s.actionsQueue.queuedActions.length ? [clearActionsQueue()] : []),
          ...(error
              ? [
                  {
                    type: FETCH_FAIL,
                    payload: { message: "Google Api Error" }
                  }
                ]
              : [])
        ]),
  processError: e => FetchErrorHandler(e, "Error on getting site data")
};

export const EpicGetSite: Epic<any, any> = EpicUtils.Create(request);
