import { SSOproviders } from '@api/model';

export type ThirdPartyState = {
  [key in SSOproviders]?: {
    webClientId: string,
    androidClientId: string,
    iosClientId: string,
  }
};

export type ThirdPartyKeysResponse = {
  [key in SSOproviders]: { web: string, android: string, ios: string }
};
