export const validateMinMaxDate = (
  value: string,
  minDate: string,
  maxDate: string,
  minErrMsg: string = "Date is beyond minimum limit",
  maxErrMsg: string = "Date is beyond maximum limit"
) => {
  let result;

  if (value) {
    const valueTime = new Date(value).getTime();

    if (minDate) {
      const minDateTime = new Date(minDate).getTime();

      result = valueTime >= minDateTime ? undefined : minErrMsg;
    }

    if (maxDate) {
      const maxDateTime = new Date(maxDate).getTime();

      result = valueTime <= maxDateTime ? undefined : maxErrMsg;
    }
  }

  return result;
};
