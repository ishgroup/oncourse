import { Tax } from "@api/model";
import { getEntityResponse } from "../mockUtils";

export function mockTaxTypes(): Tax[] {
  this.getPlainTaxes = () => {
    const rows = [
      {
        id: "1",
        values: ["1", "GST", "0.1000", "true"]
      },
      {
        id: "2",
        values: ["2", "N", "0.0000", "false"]
      }
    ];

    return getEntityResponse({
      entity: "Tax",
      rows,
      plain: true
    });
  };

  this.saveTaxType = items => {
    this.taxTypes = items;
  };

  this.removeTaxType = id => {
    this.taxTypes = this.taxTypes.filter(it => it.id !== id);
  };

  return [
    {
      id: 886543,
      code: "56r76387",
      editable: false,
      systemType: true,
      gst: true,
      rate: 0.5,
      payableAccountId: 1,
      receivableAccountId: 3,
      description: ""
    },
    {
      id: 5684452,
      code: "2345245 ",
      editable: true,
      systemType: false,
      gst: false,
      rate: 0.1,
      payableAccountId: 2,
      receivableAccountId: 4,
      description: "Australian GST"
    },
    {
      id: 32435,
      code: "7456241",
      editable: true,
      systemType: true,
      gst: true,
      rate: 0.4,
      payableAccountId: 1,
      receivableAccountId: 3,
      description: "GST exempt"
    }
  ];
}
