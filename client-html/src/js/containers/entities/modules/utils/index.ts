import { openInternalLink } from "ish-ui";
import { ListState } from '../../../../model/common/ListView';

export const openModuleLink = (moduleId: number) => {
  openInternalLink("/module/" + moduleId);
};

export const deleteDisabledCondition = ({ selection, records }: ListState) => {
  const customColumn = records.columns.filter(c => c.system || c.visible).findIndex(column => column.attribute === 'isCustom');
  const record = records.rows.find(row => row.id === selection[0]);
  return record?.values[customColumn] === 'false';
};
