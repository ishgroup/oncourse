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
  expect('111111').toBe(courseClass.id);
  expect('2222222').toBe(courseClass.course.id);
  expect('COURSE').toBe(courseClass.course.code);
  expect('course name').toBe(courseClass.course.name);
  expect('course description').toBe(courseClass.course.description);
  expect('CODE').toBe(courseClass.code);
  expect(true).toBe(courseClass.hasAvailablePlaces);
  expect(10).toBe(courseClass.availableEnrolmentPlaces);
  expect(false).toBe(courseClass.isFinished);
  expect(false).toBe(courseClass.isCancelled);
  expect(true).toBe(courseClass.isAllowByApplication);
  expect(true).toBe(courseClass.isPaymentGatewayEnabled);

  console.log(normalize(courseClass, ClassesListSchema))

});