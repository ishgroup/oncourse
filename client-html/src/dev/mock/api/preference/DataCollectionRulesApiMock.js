import { promiseReject, promiseResolve } from "../../MockAdapter";
export function DataCollectionRulesApiMock() {
    this.returnError = false;
    /**
     * Data Collection Rules items
     **/
    this.api.onGet("/v1/datacollection/rule").reply(config => {
        return promiseResolve(config, this.db.dataCollectionRules);
    });
    /**
     * Update Data Collection Rule with success or error
     **/
    this.api.onPut(new RegExp(`v1/datacollection/rule/\.+`)).reply(config => {
        this.returnError = !this.returnError;
        if (this.returnError) {
            const errorObj = {
                propertyName: "enrolmentFormName",
                errorMessage: "Enrolment Form Name is invalid"
            };
            return promiseReject(config, errorObj);
        }
        const id = config.url.split("/")[3];
        const data = JSON.parse(config.data);
        this.db.updateCollectionRule(id, data);
        return promiseResolve(config, JSON.parse(JSON.stringify(this.db.dataCollectionRules)));
    });
    /**
     * Create Data Collection Rule
     **/
    this.api.onPost("v1/datacollection/rule").reply(config => {
        const data = JSON.parse(config.data);
        this.db.createCollectionRule(data);
        return promiseResolve(config, JSON.parse(JSON.stringify(this.db.dataCollectionRules)));
    });
    /**
     * Delete Data Collection Rule
     **/
    this.api.onDelete(new RegExp(`v1/datacollection/rule/\.+`)).reply(config => {
        const id = config.url.split("/")[3];
        this.db.deleteCollectionRule(id);
        return promiseResolve(config, JSON.parse(JSON.stringify(this.db.dataCollectionRules)));
    });
}
//# sourceMappingURL=DataCollectionRulesApiMock.js.map