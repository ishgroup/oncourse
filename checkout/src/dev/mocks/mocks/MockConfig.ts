import * as L from "lodash";
import {AxiosResponse} from "axios";
import faker from "faker";
import uuid from "uuid";
import {CreateMockDB, MockDB} from "./MockDB";
import {Injector} from "../../../js/injector";
import {Store} from "redux";
import {IshState} from "../../../js/services/IshState";
import {EnvironmentConstants} from "../../../js/config/EnvironmentConstants";
import {CartApiMock} from "../CartApiMock";
import {ContactApiMock} from "../ContactApiMock";
import {CourseClassesApiMock} from "../CourseClassesApiMock";
import {CorporatePassApiMock} from "../CorporatePassApiMock";
import {ProductsApiMock} from "../ProductsApiMock";
import {PromotionApiMock} from "../PromotionApiMock";
import {CheckoutApiMock} from "../CheckoutApiMock";
import {SearchApiMock} from "../SearchApiMock";
import {PreferenceApiMock} from "../PreferenceApiMock";
import {CreateStore, RestoreState} from "../../../js/CreateStore";
import {ValidationError} from "../../../js/model";
import { localForage } from '../../../js/constants/LocalForage';

export interface Props {
  commonError: boolean;
  plainTextError: boolean;
  validationError: boolean;
  contactApi: { contactFieldsIsEmpty: boolean };
  checkoutApi: {
    makePayment: {
      formError: boolean,
      modelError: boolean,
      result: {
        success: boolean,
        inProgress: boolean,
        failed: boolean,
        undefined: boolean,
        unhandled: boolean,
      },
    },
  };
}
export class MockConfig {
  private id: string = uuid();
  public injector: Injector;
  public store: Store<IshState>;
  public db: MockDB = CreateMockDB();
  public props: Props = {
    commonError: false,
    plainTextError: false,
    validationError: false,
    contactApi: {
      contactFieldsIsEmpty: false,
    },
    checkoutApi: {
      makePayment: {
        formError: false,
        modelError: false,
        result: {
          failed: false,
          success: true,
          undefined: false,
          inProgress: false,
          unhandled: false,
        },
      },
    },

  };

  createValidationError(formErrors: number = 3, field: string[]): ValidationError {
    return {
      formErrors: L.range(0, formErrors).map( i => faker.hacker.phrase()),
      fieldsErrors: field.map( f => {
        return {name: f, error: faker.hacker.phrase()};
      }),
    };
  }


  createResponse(response: any): Promise<any> {
    if (this.props.plainTextError) {
      return CreatePromiseReject(faker.hacker.phrase());
    } else if (this.props.commonError) {
      return CreatePromiseReject({
        code: 1000,
        message: faker.hacker.phrase(),
      });
    } else if (this.props.validationError) {
      return CreatePromiseReject(this.createValidationError(2, ["email", "street"]));
    } else {
      return Promise.resolve(response);
    }
  }

  public save(): void {
    localForage.setItem("MockConfig", this.props).catch(e => {
      console.error(e);
    });
  }

  public load(callback: () => void): void {
    localForage.getItem("MockConfig").then((props: Props) => {
      if (props) {
        this.props = props;
      }
      if (callback) {
        callback();
      }
    },
    ).catch(e => {
      console.error(e);
    });
  }

  public init(callback: (config: MockConfig) => void) {
    this.store = CreateStore();
    this.injector = Injector.of();
    if (process.env.NODE_ENV === EnvironmentConstants.development) {
      // Overwrite Services for development without real server
      this.injector.setService("cartApi", new CartApiMock(this.injector.http));
      this.injector.setService("contactApi", new ContactApiMock(this));
      this.injector.setService("courseClassesApi", new CourseClassesApiMock(this));
      this.injector.setService("productsApi", new ProductsApiMock(this));
      this.injector.setService("promotionApi", new PromotionApiMock(this));
      this.injector.setService("checkoutApi", new CheckoutApiMock(this));
      this.injector.setService("corporatePassApi", new CorporatePassApiMock(this));
      this.injector.setService("searchApi", new SearchApiMock(this));
      this.injector.setService("preferenceApi", new PreferenceApiMock(this));
    }

    RestoreState(this.store, (error, result) => {
      this.load(() => callback(this));
    });
  }


}

export const CreatePromiseReject = (data: any, code: number = 400): Promise<any> => {
  return Promise.reject({
    data,
    status: code,
  } as AxiosResponse);
};
