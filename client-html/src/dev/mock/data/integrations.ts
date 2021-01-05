/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Integration, IntegrationProp } from "@api/model";

export function mockIntegrations(): Integration[] {
  this.getIntegrations = () => this.integrations;
  this.updateIntegration = (id, item) => {
    const listItem = this.integrations.find(int => int.id === id);

    Object.assign(listItem, item);
    return this.integrations;
  };

  this.createIntegration = item => {
    item.id = (this.integrations.length + 1).toString();
    item.verificationCode = null;
    item.created = new Date().toISOString();
    item.modified = new Date().toISOString();
    this.integrations.push(item);
  };

  this.removeIntegration = id => {
    this.integrations = this.integrations.filter(item => item.id !== id);
  };

  return [
    {
      type: 1,
      id: "123",
      name: "Moodle Integration",
      props: [
        { key: "baseUrl", value: "http://localhost:8888/moodle29/" } as IntegrationProp,
        { key: "username", value: "admin" } as IntegrationProp,
        { key: "password", value: "Consulrisk_12" } as IntegrationProp,
        { key: "serviceName", value: "newint" } as IntegrationProp,
        { key: "courseTag", value: "Moodle" } as IntegrationProp
      ],
      verificationCode: null,
      created: new Date().toISOString(),
      modified: new Date().toISOString()
    },
    {
      type: 2,
      id: "23",
      name: "Mailchimp Integration",

      props: [
        { key: "apiKey", value: "09f3e95ea51a25d32aa1d2b3c3e766a3-us11" } as IntegrationProp,
        { key: "listId", value: "557374a712" } as IntegrationProp
      ],
      verificationCode: null,
      created: new Date().toISOString(),
      modified: new Date().toISOString()
    },
    {
      type: 3,
      id: "66",
      name: "SurveyMonkey Integration",
      props: [
        { key: "apiKey", value: "" } as IntegrationProp,
        { key: "authToken", value: "Q2uwQCzlbR3fJih49siFKMUvulyIBKnSiKUrn2CujX7HKHxJH-bo7ZZrQM" } as IntegrationProp,
        { key: "surveyName", value: "TestSurvey" } as IntegrationProp,
        { key: "courseTag", value: "Moodle" } as IntegrationProp,
        { key: "sendOnEnrolmentSuccess", value: "true" } as IntegrationProp,
        { key: "sendOnEnrolmentCompletion", value: "true" } as IntegrationProp
      ],
      verificationCode: null,
      created: new Date().toISOString(),
      modified: new Date().toISOString()
    },
    {
      type: 6,
      id: "9999",
      name: "MYOB Integration",
      props: [
        { key: "baseUrl", value: "http://localhost:8888/moodle29/" } as IntegrationProp,
        { key: "user", value: "admin" } as IntegrationProp,
        { key: "password", value: "Consulrisk_12" } as IntegrationProp
      ],
      verificationCode: null,
      created: new Date().toISOString(),
      modified: new Date().toISOString()
    },
    {
      type: 5,
      id: "w5w45435",
      name: "Xero Integration",
      props: [],
      verificationCode: null,
      created: new Date().toISOString(),
      modified: new Date().toISOString()
    }
  ];
}
