import { RepeatEndEnum, RepeatEnum } from "../../../../build/generated-sources/swagger-js/api";
export function mockHolidays() {
    this.saveHolidays = items => {
        this.holiday = items;
    };
    this.removeHoliday = id => {
        this.holiday = this.holiday.filter(it => it.id !== id);
    };
    return [
        {
            id: "32435",
            description: "Holidays 1",
            startDateTime: "2015-01-24T10:30:00.000Z",
            endDateTime: "2018-05-24T10:40:00.000Z",
            repeat: RepeatEnum.day,
            repeatEnd: RepeatEndEnum.onDate,
            repeatEndAfter: 0
        },
        {
            id: "886543",
            description: "Holidays 2",
            startDateTime: "2018-04-24T10:30:00.000Z",
            endDateTime: "2018-05-24T10:35:00.000Z",
            repeat: RepeatEnum.none,
            repeatEnd: RepeatEndEnum.after,
            repeatEndAfter: 2
        }
    ];
}
//# sourceMappingURL=holidays.js.map