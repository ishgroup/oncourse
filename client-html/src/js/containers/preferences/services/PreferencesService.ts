import {
  PreferenceApi,
  HolidayApi,
  SystemPreference,
  DataCollectionApi,
  PaymentApi,
  TaxApi,
  ConcessionApi,
  ContactApi,
  EntityRelationTypeApi,
  CustomFieldApi,
  Country,
  ColumnWidth,
  PaymentMethod,
  TutorRoleApi,
  AccountApi,
  UserPreferenceApi, GradingApi,
  ContactRelationType,
  EntityRelationType,
  ConcessionType,
  DataCollectionForm,
  DataCollectionRule,
  FieldType,
  Tax,
  CustomFieldType,
  EnumItem,
  Currency,
  Holiday,
  DefinedTutorRole,
  Account, GradingType
} from "@api/model";
import { DefaultHttpService } from "../../../common/services/HttpService";
import { AccountStudentEnrolments, Categories } from "../../../model/preferences";
import { SearchService } from "../../../common/services/SearchService";
import * as ModelCollege from "../../../model/preferences/College";
import * as ModelLdap from "../../../model/preferences/Ldap";
import * as ModelLicences from "../../../model/preferences/Licences";
import * as ModelMessaging from "../../../model/preferences/Messaging";
import * as ModelClass from "../../../model/preferences/ClassDefaults";
import * as ModelMaintenance from "../../../model/preferences/Maintenance";
import * as ModelAvetmiss from "../../../model/preferences/Avetmiss";
import * as ModelFinancial from "../../../model/preferences/Financial";
import * as ModelSecurity from "../../../model/preferences/security";

class PreferencesService {
  readonly defaultApi = new DefaultHttpService();

  readonly preferenceApi = new PreferenceApi(this.defaultApi);

  readonly holidayApi = new HolidayApi(this.defaultApi);

  readonly dataCollectionApi = new DataCollectionApi(this.defaultApi);

  readonly PaymentApi = new PaymentApi(this.defaultApi);

  readonly TaxApi = new TaxApi(this.defaultApi);

  readonly ConcessionApi = new ConcessionApi(this.defaultApi);

  readonly ContactApi = new ContactApi(this.defaultApi);

  readonly EntityRelationTypeApi = new EntityRelationTypeApi(this.defaultApi);

  readonly CustomFieldApi = new CustomFieldApi(this.defaultApi);

  readonly tutorRoleApi = new TutorRoleApi(this.defaultApi);

  readonly accountApi = new AccountApi(this.defaultApi);

  readonly userPreferenceApi = new UserPreferenceApi(this.defaultApi);

  readonly gradingApi = new GradingApi(this.defaultApi);

  public getGradingTypes(): Promise<GradingType[]> {
    return this.gradingApi.get();
  }

  public updateGradingTypes(types: GradingType[]): Promise<any> {
    return this.gradingApi.update(types);
  }

  public deleteGradingType(id): Promise<any> {
    return this.gradingApi.remove(id);
  }

  public getDeafaultIncomeAccount(): Promise<Account> {
    return this.userPreferenceApi.get(AccountStudentEnrolments.uniqueKey).then(
      (prefs): Promise<Account> => this.accountApi.get(Number(prefs[AccountStudentEnrolments.uniqueKey]))
    );
  }

  public getCurrency(): Promise<Currency> {
    return this.preferenceApi.getCurrency();
  }

  public getUSISoftwareId(): Promise<SystemPreference[]> {
    return this.preferenceApi.get("usi.softwareid");
  }

  public updateCustomFields(customFields: CustomFieldType[]): Promise<CustomFieldType[]> {
    return this.CustomFieldApi.update(customFields);
  }

  public deleteCustomFields(id: string): Promise<CustomFieldType[]> {
    return this.CustomFieldApi.remove(id);
  }

  public getCustomFields(): Promise<CustomFieldType[]> {
    return this.CustomFieldApi.get();
  }

  public updateContactRelationTypes(contactRelationTypes: ContactRelationType[]): Promise<ContactRelationType[]> {
    return this.ContactApi.update(contactRelationTypes);
  }

  public deleteContactRelationType(id: string): Promise<ContactRelationType[]> {
    return this.ContactApi.remove(id);
  }

  public getContactRelationTypes(): Promise<ContactRelationType[]> {
    return this.ContactApi.get();
  }

  public updateEntityRelationTypes(entityRelationTypes: EntityRelationType[]): Promise<EntityRelationType[]> {
    return this.EntityRelationTypeApi.update(entityRelationTypes);
  }

  public deleteEntityRelationType(id: string): Promise<EntityRelationType[]> {
    return this.EntityRelationTypeApi.remove(id);
  }

  public getEntityRelationTypes(): Promise<EntityRelationType[]> {
    return this.EntityRelationTypeApi.get();
  }

  public updateConcessionTypes(concessionTypes: ConcessionType[]): Promise<ConcessionType[]> {
    return this.ConcessionApi.update(concessionTypes);
  }

  public deleteConcessionType(id: string): Promise<ConcessionType[]> {
    return this.ConcessionApi.remove(id);
  }

  public getConcessionTypes(): Promise<ConcessionType[]> {
    return this.ConcessionApi.get();
  }

  public updateTaxTypes(taxTypes: Tax[]): Promise<Tax[]> {
    return this.TaxApi.update(taxTypes);
  }

  public deleteTaxType(id: string): Promise<Tax[]> {
    return this.TaxApi.remove(id);
  }

  public getTaxTypes(): Promise<Tax[]> {
    return this.TaxApi.get();
  }

  public updatePaymentTypes(paymentTypes: PaymentMethod[]): Promise<PaymentMethod[]> {
    return this.PaymentApi.update(paymentTypes);
  }

  public deletePaymentType(id: string): Promise<PaymentMethod[]> {
    return this.PaymentApi.remove(id);
  }

  public getPaymentTypes(): Promise<PaymentMethod[]> {
    return this.PaymentApi.get();
  }

  public deleteDataCollectionForm(id: string): Promise<any> {
    return this.dataCollectionApi.removeForm(id);
  }

  public createDataCollectionForm(form: DataCollectionForm): Promise<any> {
    return this.dataCollectionApi.createForm(form);
  }

  public updateDataCollectionForm(id: string, form: DataCollectionForm): Promise<any> {
    return this.dataCollectionApi.updateForm(id, form);
  }

  public getDataCollectionFormFieldTypes(formType: string): Promise<FieldType[]> {
    return this.dataCollectionApi.getFieldTypes(formType);
  }

  public getDataCollectionForms(): Promise<DataCollectionForm[]> {
    return this.dataCollectionApi.getForms();
  }

  public createDataCollectionRule(rule: DataCollectionRule): Promise<DataCollectionRule[]> {
    return this.dataCollectionApi.createRule(rule);
  }

  public removeDataCollectionRule(id: string): Promise<DataCollectionRule[]> {
    return this.dataCollectionApi.removeRule(id);
  }

  public updateDataCollectionRule(id: string, rule: DataCollectionRule): Promise<DataCollectionRule[]> {
    return this.dataCollectionApi.updateRule(id, rule);
  }

  public getDataCollectionRules(): Promise<DataCollectionRule[]> {
    return this.dataCollectionApi.getRules();
  }

  public getPreferences(category): Promise<SystemPreference[]> {
    const keys: string[] = this.getCategoryKeys(category);
    return this.preferenceApi.get(SearchService.buildSearchString(keys));
  }

  public getPreferenceByKeys(keys: string[]): Promise<SystemPreference[]> {
    return this.preferenceApi.get(SearchService.buildSearchString(keys));
  }

  public savePreferences(fields): Promise<any> {
    const formatedFields: SystemPreference[] = Object.keys(fields).map(f => ({ uniqueKey: f, valueString: fields[f] }));
    return this.preferenceApi.update(formatedFields);
  }

  public getColumnsWidth(): Promise<ColumnWidth> {
    return this.preferenceApi.getColumnSettings();
  }

  public updateColumnsWidth(columnWidth: ColumnWidth): Promise<any> {
    return this.preferenceApi.updateColumnSettings(columnWidth);
  }

  public getTimezones(): Promise<string[]> {
    return this.preferenceApi.getTimezones();
  }

  public getCountries(): Promise<Country[]> {
    return this.preferenceApi.getCountries();
  }

  public getLanguages(): Promise<Country[]> {
    return this.preferenceApi.getLanguages();
  }

  public getHolidays(): Promise<Holiday[]> {
    return this.holidayApi.get();
  }

  public saveHolidays(items: Holiday[]): Promise<any> {
    return this.holidayApi.update(items);
  }

  public deleteHolidaysItem(id: string): Promise<any> {
    return this.holidayApi.remove(id);
  }

  public getEnum(name: string): Promise<EnumItem[]> {
    return this.preferenceApi.getEnum(name);
  }

  public checkConnection(host: string, port: string, isSsl: string, baseDn: string, user: string): Promise<boolean> {
    return this.preferenceApi.checkConnection(host, port, isSsl, baseDn, user);
  }

  public messageQueued(type: string): Promise<any> {
    return this.preferenceApi.messageQueued(type);
  }

  public getLockedDate(): Promise<any> {
    return this.preferenceApi.getLockedDate();
  }

  private getCategoryKeys(category: Categories) {
    switch (category) {
      case Categories.college: {
        return Object.keys(ModelCollege).map(item => ModelCollege[item].uniqueKey);
      }

      case Categories.licences: {
        return Object.keys(ModelLicences).map(item => ModelLicences[item].uniqueKey);
      }

      case Categories.messaging: {
        return Object.keys(ModelMessaging).map(item => ModelMessaging[item].uniqueKey);
      }

      case Categories.classDefaults: {
        return Object.keys(ModelClass).map(item => ModelClass[item].uniqueKey);
      }

      case Categories.ldap: {
        return Object.keys(ModelLdap).map(item => ModelLdap[item].uniqueKey);
      }

      case Categories.maintenance: {
        return Object.keys(ModelMaintenance).map(item => ModelMaintenance[item].uniqueKey);
      }

      case Categories.avetmiss: {
        return Object.keys(ModelAvetmiss).map(item => ModelAvetmiss[item].uniqueKey);
      }

      case Categories.financial: {
        return Object.keys(ModelFinancial).map(item => ModelFinancial[item].uniqueKey);
      }

      case Categories.security: {
        return Object.keys(ModelSecurity).map(item => ModelSecurity[item].uniqueKey);
      }

      default:
        return null;
    }
  }

  public getTutorRole(id: number): Promise<DefinedTutorRole> {
    return this.tutorRoleApi.get(id);
  }

  public createTutorRole(tutorRole: DefinedTutorRole): Promise<any> {
    return this.tutorRoleApi.create(tutorRole);
  }

  public removeTutorRole(id: number): Promise<any> {
    return this.tutorRoleApi.remove(id);
  }

  public updateTutorRole(id: number, tutorRole: DefinedTutorRole): Promise<any> {
    return this.tutorRoleApi.update(id, tutorRole);
  }
}

export default new PreferencesService();
