import { AutomationConfigs, Integration } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../common/actions/ActionUtils";
import { AutomationEntity } from "../../../model/automation/common";
import { IntegrationSchema } from "../../../model/automation/integrations/IntegrationSchema";
import { CatalogItemType } from "../../../model/common/Catalog";

export const GET_INTEGRATIONS_REQUEST = _toRequestType("get/integrations");
export const GET_INTEGRATIONS_FULFILLED = FULFILLED(GET_INTEGRATIONS_REQUEST);

export const CREATE_INTEGRATION_ITEM_REQUEST = _toRequestType("post/integrations");

export const UPDATE_INTEGRATION_ITEM_REQUEST = _toRequestType("put/integrations");

export const DELETE_INTEGRATION_ITEM_REQUEST = _toRequestType("delete/integrations");

export const INSTALL_AUTOMATION = _toRequestType("install/automation");
export const UNINSTALL_AUTOMATION = _toRequestType("uninstall/automation");

export const IMPORT_AUTOMATION_CONFIG = _toRequestType("import/automation/config");
export const EXPORT_AUTOMATION_CONFIG = _toRequestType("export/automation/config");

export const importAutomationConfig = (entity: AutomationEntity, id: number, config: AutomationConfigs) => ({
  type: IMPORT_AUTOMATION_CONFIG,
  payload: {entity, id, config}
});

export const exportAutomationConfig = (entity: AutomationEntity, name: string, id: number) => ({
  type: EXPORT_AUTOMATION_CONFIG,
  payload: {entity, name, id}
});

export const getIntegrations = (nameToSelect?: string) => ({
  type: GET_INTEGRATIONS_REQUEST,
  payload: {nameToSelect}
});

export const installAutomation = (automation: CatalogItemType, entity: AutomationEntity) => ({
  type: INSTALL_AUTOMATION,
  payload: {automation, entity}
});

export const uninstallAutomation = (automation: CatalogItemType, entity: AutomationEntity) => ({
  type: UNINSTALL_AUTOMATION,
  payload: { automation, entity }
});

export const getIntegrationsFulfilled = (integrations: IntegrationSchema[]) => ({
  type: GET_INTEGRATIONS_FULFILLED,
  payload: { integrations }
});

export const updateIntegration = (id: string, item: Integration, form: string) => ({
  type: UPDATE_INTEGRATION_ITEM_REQUEST,
  payload: { id, item, form }
});

export const createIntegration = (item: Integration, form: string) => ({
  type: CREATE_INTEGRATION_ITEM_REQUEST,
  payload: { item, form }
});

export const deleteIntegrationItem = (id: string) => ({
  type: DELETE_INTEGRATION_ITEM_REQUEST,
  payload: { id }
});
