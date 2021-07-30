import { SSOproviders } from '@api/model';

export type ThirdPartyState = {
  [key in SSOproviders]?: {
    clientId: string
  }
};
