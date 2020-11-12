/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
export function mockIntegrations() {
    this.updateIntegration = (id, item) => {
        const listItem = this.integrations.find(int => int.id === id);
        Object.assign(listItem, item);
        return this.integrations;
    };
    this.createIntegration = item => {
        item.id = item.name;
        this.integrations.push(item);
    };
    this.removeIntegration = id => {
        this.integrations = this.integrations.filter(item => item.id !== id);
    };
    return [
        {
            type: "moodle",
            id: "123",
            name: "Moodle Integration",
            props: [
                { key: "baseUrl", value: "http://localhost:8888/moodle29/" },
                { key: "username", value: "admin" },
                { key: "password", value: "Consulrisk_12" },
                { key: "serviceName", value: "newint" },
                { key: "courseTag", value: "Moodle" }
            ]
        },
        {
            type: "mailchimp",
            id: "23",
            name: "Mailchimp Integration",
            props: [
                { key: "apiKey", value: "09f3e95ea51a25d32aa1d2b3c3e766a3-us11" },
                { key: "listId", value: "557374a712" }
            ]
        },
        {
            type: "surveymonkey",
            id: "66",
            name: "SurveyMonkey Integration",
            props: [
                { key: "apiKey", value: "" },
                { key: "authToken", value: "Q2uwQCzlbR3fJih49siFKMUvulyIBKnSiKUrn2CujX7HKHxJH-bo7ZZrQM" },
                { key: "surveyName", value: "TestSurvey" },
                { key: "courseTag", value: "Moodle" },
                { key: "sendOnEnrolmentSuccess", value: "true" },
                { key: "sendOnEnrolmentCompletion", value: "true" }
            ]
        },
        {
            type: "myob",
            id: "9999",
            name: "MYOB Integration",
            props: [
                { key: "baseUrl", value: "http://localhost:8888/moodle29/" },
                { key: "user", value: "admin" },
                { key: "password", value: "Consulrisk_12" }
            ]
        },
        {
            type: "xero",
            id: "w5w45435",
            name: "Xero Integration",
            configured: false,
            props: []
        }
    ];
}
//# sourceMappingURL=integrations.js.map