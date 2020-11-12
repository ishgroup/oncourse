import { LatestActivityItem, LatestActivityState } from "../../../model/dashboard";
import { DASHBOARD_ACTIVITY_STORAGE_NAME } from "../../../constants/Config";
import { Category } from "@api/model";

export const latestActivityStorageHandler = (item: LatestActivityItem, entity: Category) => {
  const today = new Date();

  let activitySate: LatestActivityState = localStorage.getItem(DASHBOARD_ACTIVITY_STORAGE_NAME) as LatestActivityState;

  if (activitySate) {
    activitySate = JSON.parse(activitySate as string);

    const activityStarted = new Date(activitySate.started);

    // checking if activity data gathering has started more then two days ago
    if (today.getTime() - 172800000 > activityStarted.getTime()) {
      activitySate = {};
    }
  } else {
    activitySate = {};
  }

  if (!activitySate.data) {
    activitySate.data = [];
  }

  if (!activitySate.started) {
    activitySate.started = today.toISOString();
  }

  const destination = activitySate.data.find(i => i.entity === entity);

  if (destination) {
    const destinationIndex = destination.items.findIndex(i => i.id === item.id);

    destinationIndex === -1
      ? (destination.items = [item, ...destination.items.slice(0, 9)])
      : (destination.items[destinationIndex] = item);

    activitySate.data = [destination, ...activitySate.data.filter(i => i.entity !== destination.entity)];
  } else {
    activitySate.data = [{ entity, items: [item] }, ...activitySate.data.slice(0, 19)];
  }

  localStorage.setItem(DASHBOARD_ACTIVITY_STORAGE_NAME, JSON.stringify(activitySate));
};
