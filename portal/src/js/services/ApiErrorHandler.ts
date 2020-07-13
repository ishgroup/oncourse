import {AxiosResponse} from "axios";

export const getErrorMessage = (response: AxiosResponse): string => {
  if (!response) {
    return null;
  }
  switch (response.status) {
    case 400: {
      let message = response.data.message;
      if (response.data.formErrors) {
        message = response.data.formErrors.join(",");
      }
      return response.data.message || response.data;
    }
    default:
      return null;
  }
};
