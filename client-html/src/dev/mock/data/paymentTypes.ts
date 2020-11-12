import { PaymentMethod } from "@api/model";

export function mockPaymentTypes(): PaymentMethod[] {
  this.savePaymentType = items => {
    this.paymentTypes = items;
  };

  this.removePaymentType = id => {
    this.paymentTypes = this.paymentTypes.filter(it => it.id !== id);
  };

  return [
    {
      id: 886543,
      name: "Cheque",
      systemType: false,
      active: true,
      reconcilable: false,
      bankedAuto: false,
      accountId: 1,
      undepositAccountId: 2,
      type: "Credit card"
    },
    {
      id: 32435,
      name: "Credit card",
      systemType: true,
      active: true,
      reconcilable: false,
      bankedAuto: true
    },
    {
      id: 5684452,
      name: "B-Pay",
      systemType: false,
      active: false,
      reconcilable: false,
      bankedAuto: true,
      accountId: 2,
      undepositAccountId: 1,
      type: "Other"
    }
  ];
}
