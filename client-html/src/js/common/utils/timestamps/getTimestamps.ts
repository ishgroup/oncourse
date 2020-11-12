import { PreferenceSchema } from "../../../model/preferences/PreferencesSchema";

const getTimestamps = (items: PreferenceSchema[]): Date[] => {
  const closestWithCreated = items.find(i => Boolean(i.created));
  let created = closestWithCreated ? new Date(closestWithCreated.created) : null;
  items.forEach(item => {
    const itemCreated = new Date(item.created);
    if (item.created && itemCreated < created) {
      created = itemCreated;
    }
  });

  const closestWithModified = items.find(i => Boolean(i.modified));
  let modified = closestWithModified ? new Date(closestWithModified.modified) : null;
  items.forEach(item => {
    const itemModified = new Date(item.modified);
    if (item.modified && itemModified > modified) {
      modified = itemModified;
    }
  });

  return [created, modified];
};

export default getTimestamps;
