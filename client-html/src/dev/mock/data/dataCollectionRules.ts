import { DataCollectionRule } from "@api/model";

export function mockDataCollectionRules(): DataCollectionRule[] {
  this.updateCollectionRule = (id, rule) => {
    const index = this.dataCollectionForms.findIndex(item => item.id === id);
    this.dataCollectionRules.splice(index, 1, rule);
  };

  this.createCollectionRule = rule => {
    rule.id = Math.random().toString();
    this.dataCollectionRules.push(rule);
  };

  this.deleteCollectionRule = id => {
    this.dataCollectionRules = this.dataCollectionRules.filter(rule => String(rule.id) !== String(id));
  };

  return [
    {
      id: "12345",
      name: "Non-accredited course",
      enrolmentFormName: "Enrolment Form",
      applicationFormName: "Fee for service",
      waitingListFormName: "Waiting list Form",
      surveyForms: ["Survey Form 1", "Survey Form 2"]
    },
    {
      id: "55667",
      name: "Accredited course",
      enrolmentFormName: "Enrolment Form",
      applicationFormName: "Fee for service",
      waitingListFormName: "Waiting list Form",
      surveyForms: ["Survey Form 2"]
    }
  ];
}
