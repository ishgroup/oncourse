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
      payerFormName: "Payer Details",
      parentFormName: "Parent Details",
      productFormName: "Default Field form (Product)",
      voucherFormName: "Default Voucher Form",
      membershipFormName: "Default Field form (Membership)",
      surveyForms: ["Survey Form 1", "Survey Form 2"],
      created: "2017-04-09T02:00:00.000Z",
      modified: "2021-11-02T00:52:12.000Z",
    },
    {
      id: "55667",
      name: "Accredited course",
      enrolmentFormName: "Enrolment Form",
      applicationFormName: "Fee for service",
      waitingListFormName: "Waiting list Form",
      payerFormName: "Payer Details",
      parentFormName: "Parent Details",
      productFormName: "Default Field form (Product)",
      voucherFormName: "Creative Kids Voucher Form",
      membershipFormName: "Default Field form (Membership)",
      surveyForms: ["Survey Form 2"],
      created: "2017-04-09T02:00:00.000Z",
      modified: "2021-07-05T11:52:07.000Z"
    }
  ];
}
