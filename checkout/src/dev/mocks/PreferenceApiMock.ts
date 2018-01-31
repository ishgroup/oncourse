import {PreferenceApi} from "../../js/http/PreferenceApi";
import {MockConfig} from "./mocks/MockConfig";
import {Preferences} from "../../js/model";

export class PreferenceApiMock extends PreferenceApi {
  public config: MockConfig;

  constructor(config: MockConfig) {
    super(null);
    this.config = config;
  }

  getPreferences(): Promise<Preferences> {
    return this.config.createResponse({
      corporatePassEnabled: true,
      creditCardEnabled: true,
      successLink: '/courses',
      refundPolicyUrl: '/courses',
    });
  }
}
