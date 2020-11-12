import { RepeatEndEnum, RepeatEnum } from "@api/model";

export const repeatListItems = [
  {
    value: RepeatEnum.day,
    label: "Day"
  },
  {
    value: RepeatEnum.hour,
    label: "Hour"
  },
  {
    value: RepeatEnum.month,
    label: "Month"
  },
  {
    value: RepeatEnum.none,
    label: "None"
  },
  {
    value: RepeatEnum.week,
    label: "Week"
  },

  {
    value: RepeatEnum.year,
    label: "Year"
  }
];

export const repeatEndListItems = [
  {
    value: RepeatEndEnum.after,
    label: "After"
  },
  {
    value: RepeatEndEnum.never,
    label: "Never"
  },
  {
    value: RepeatEndEnum.onDate,
    label: "On Date"
  }
];
