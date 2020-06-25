import {AxiosResponse} from "axios";

export const getErrorMessage = (response: AxiosResponse): string => {
  switch (response.status) {
    case 400:
      return response.data.message;
    default:
      return "";
  }
};
