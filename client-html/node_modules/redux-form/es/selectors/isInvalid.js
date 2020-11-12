import createIsValid from './isValid';
export default function createIsInvalid(structure) {
  return function (form, getFormState) {
    var isValid = createIsValid(structure)(form, getFormState);
    return function (state) {
      return !isValid(state);
    };
  };
}