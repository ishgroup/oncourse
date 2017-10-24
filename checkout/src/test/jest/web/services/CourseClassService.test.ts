import * as CourseClassService from "../../../../js/web/services/CourseClassService";
import * as TestUtils from "../../common/services/TestUtils";
import {HTMLMarkers} from "../../../../js/common/services/HTMLMarker";
import * as HtmlUtils from "../../../../js/common/utils/HtmlUtils";
import {CourseClass} from "../../../../js/model";
import {normalize} from "normalizr";
import {ClassesListSchema} from "../../../../js/NormalizeSchema";


test('create CourseClass from html element properties', () => {
  const element:HTMLElement = TestUtils.createEnrolButtonHTMLElement();
  const props:{ [key: string]: any } = HtmlUtils.parse(element, HTMLMarkers.ENROL_BUTTON);
  const courseClass:CourseClass = CourseClassService.htmlProps2CourseClass(props);
  expect(courseClass.id).toBe('111111');
  console.log(normalize(courseClass, ClassesListSchema))

});