import { promiseReject, promiseResolve } from "../../MockAdapter";
export function holidayApiMock(mock) {
    /**
     * Holidays items
     **/
    this.returnError = false;
    this.api.onGet("/v1/holiday").reply(config => {
        return promiseResolve(config, this.db.holiday);
    });
    /**
     * Mock Holidays save success or error
     **/
    this.api.onPost("/v1/holiday").reply(config => {
        this.returnError = !this.returnError;
        if (this.returnError) {
            const errorObj = {
                id: "886543",
                propertyName: "startDate",
                errorMessage: "Start Date is invalid"
            };
            return promiseReject(config, errorObj);
        }
        const data = JSON.parse(config.data);
        data.forEach(i => {
            if (!i.id)
                i.id = Math.random().toString();
        });
        this.db.saveHolidays(data);
        return promiseResolve(config, this.db.holiday);
    });
    this.api.onDelete(new RegExp(`v1/holiday/\\d+`)).reply(config => {
        const id = config.url.split("/")[2];
        this.db.removeHoliday(id);
        return promiseResolve(config, this.db.holiday);
    });
}
//# sourceMappingURL=HolidayApiMock.js.map