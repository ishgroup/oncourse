import {AxiosResponse} from "axios";

export const getErrorMessage = (response: AxiosResponse): string => {
  if (!response) {
    return null;
  }
  switch (response.status) {
    case 400:
      return response.data.message;
    default:
      return null;
  }
};
