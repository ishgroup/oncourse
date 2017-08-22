import {ATTR_DATA_PROP_PREFIX} from "../../../../js/common/services/HTMLMarker";
import * as HtmlUtils from "../../../../js/common/utils/HtmlUtils";

export const createEnrolButtonHTMLElement = () => {
  const container: HTMLElement = document.createElement("div");
  container.setAttribute(`${ATTR_DATA_PROP_PREFIX}${HtmlUtils.camelCase2DashCase('id')}`, "111111");
  container.setAttribute(`${ATTR_DATA_PROP_PREFIX}${HtmlUtils.camelCase2DashCase('courseId')}`, "2222222");
  container.setAttribute(`${ATTR_DATA_PROP_PREFIX}${HtmlUtils.camelCase2DashCase('courseCode')}`, "COURSE");
  container.setAttribute(`${ATTR_DATA_PROP_PREFIX}${HtmlUtils.camelCase2DashCase('courseName')}`, "course name");
  container.setAttribute(`${ATTR_DATA_PROP_PREFIX}${HtmlUtils.camelCase2DashCase('courseDescription')}`, "course description");
  container.setAttribute(`${ATTR_DATA_PROP_PREFIX}${HtmlUtils.camelCase2DashCase('code')}`, "CODE");
  container.setAttribute(`${ATTR_DATA_PROP_PREFIX}${HtmlUtils.camelCase2DashCase('hasAvailablePlaces')}`, "true");
  container.setAttribute(`${ATTR_DATA_PROP_PREFIX}${HtmlUtils.camelCase2DashCase('availableEnrolmentPlaces')}`, "10");
  container.setAttribute(`${ATTR_DATA_PROP_PREFIX}${HtmlUtils.camelCase2DashCase('isFinished')}`, "false");
  container.setAttribute(`${ATTR_DATA_PROP_PREFIX}${HtmlUtils.camelCase2DashCase('isCancelled')}`, "false");
  container.setAttribute(`${ATTR_DATA_PROP_PREFIX}${HtmlUtils.camelCase2DashCase('isAllowByApplication')}`, "true");
  container.setAttribute(`${ATTR_DATA_PROP_PREFIX}${HtmlUtils.camelCase2DashCase('isPaymentGatewayEnabled')}`, "true");

  return container;
};
