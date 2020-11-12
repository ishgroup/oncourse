import { promiseReject, promiseResolve } from "../../MockAdapter";
export function ContactRelationTypesApiMock(mock) {
    /**
     * Contact Relation Type items
     **/
    this.returnError = false;
    this.api.onGet("v1/preference/contact/relation/type").reply(config => {
        return promiseResolve(config, this.db.contactRelationTypes);
    });
    /**
     * Mock Contact Relation Types save success or error
     **/
    this.api.onPost("v1/preference/contact/relation/type").reply(config => {
        this.returnError = !this.returnError;
        if (this.returnError) {
            const errorObj = {
                id: "32435",
                propertyName: "reverseRelationName",
                errorMessage: "Reverse Relation Name is invalid"
            };
            return promiseReject(config, errorObj);
        }
        const data = JSON.parse(config.data);
        data.forEach(i => {
            if (!i.id)
                i.id = Math.random().toString();
        });
        this.db.saveContactRelationTypes(data);
        return promiseResolve(config, this.db.contactRelationTypes);
    });
    /**
     * Mock Contact Relation Type delete
     **/
    this.api.onDelete(new RegExp(`v1/preference/contact/relation/type/\\d+`)).reply(config => {
        const id = config.url.split("/")[5];
        this.db.removeContactRelationType(id);
        return promiseResolve(config, this.db.contactRelationTypes);
    });
}
//# sourceMappingURL=ContactRelationTypesApiMock.js.map