import {ATTR_DATA_PROP_PREFIX, HTMLMarker, HTMLMarkers} from "../../../../js/common/services/HTMLMarker";
import * as HtmlUtils from "../../../../js/common/utils/HtmlUtils";
import * as TestUtils from "./TestUtils";

test('test camelCase2DashCase function', () => {
  const container: HTMLElement = document.createElement("div");
  Object.keys(HTMLMarkers.ENROL_BUTTON.props).forEach((k) => {
    container.setAttribute(`${ATTR_DATA_PROP_PREFIX}${HtmlUtils.camelCase2DashCase(k)}`, k)
  });

  Object.keys(HTMLMarkers.ENROL_BUTTON.props).forEach((k) => {
    expect(k).toBe(container.getAttribute(`${ATTR_DATA_PROP_PREFIX}${HtmlUtils.camelCase2DashCase(k)}`));
  });

});

test('test parse for enrol-button marker', () => {
  const marker: HTMLMarker = HTMLMarkers.ENROL_BUTTON;
  const container: HTMLElement =TestUtils.createEnrolButtonHTMLElement();

  const result: { [key: string]: any } = HtmlUtils.parse(container, marker);
  expect(true).toBe(typeof result.id == "string");
});


