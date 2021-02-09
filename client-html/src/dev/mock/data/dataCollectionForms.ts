import { DataCollectionForm } from "@api/model";

export function mockDataCollectionForms(): DataCollectionForm[] {
  this.updateCollectionForm = (id, form) => {
    const index = this.dataCollectionForms.findIndex(item => item.id === id);
    this.dataCollectionForms.splice(index, 1, form);
  };

  this.createCollectionForm = form => {
    this.dataCollectionForms.push(form);
  };

  this.deleteCollectionForm = id => {
    this.dataCollectionForms = this.dataCollectionForms.filter(form => String(form.id) !== String(id));
  };

  return [
    {
      id: "test.form.1",
      name: "Survey Form 1",
      type: "Survey",
      deliverySchedule: "At completion",
      headings: [
        {
          name: "Contact Details",
          description:
            "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Cum dicta est fugit neque, recusandae similique totam veritatis. Adipisci atque dolorum ea iusto laudantium maiores nemo non ut.",
          fields: [
            {
              type: {
                uniqueKey: "Businessphonenumber",
                label: "Business phone number"
              },
              mandatory: true,
              label: "Test Business phone number",
              helpText: "Test help text"
            },
            {
              type: {
                uniqueKey: "Mobilephonenumber",
                label: "Mobile phone number"
              },
              mandatory: true,
              label: "Test Mobile phone label",
              helpText: "Test Mobile phone help text"
            }
          ]
        },
        {
          name: "Enrolment",
          description:
            "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Cum dicta est fugit neque, recusandae similique totam veritatis. Adipisci atque dolorum ea iusto laudantium maiores nemo non ut.",
          fields: []
        }
      ],
      fields: [
        {
          type: {
            uniqueKey: "State",
            label: "State"
          },
          mandatory: false,
          label: "Test State label",
          helpText: "Test State help text"
        }
      ]
    },
    {
      id: "test.form.2",
      name: "Survey Form 2",
      type: "Survey",
      deliverySchedule: "At completion",
      headings: [
        {
          name: "Enrolment",
          description:
            "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Cum dicta est fugit neque, recusandae similique totam veritatis. Adipisci atque dolorum ea iusto laudantium maiores nemo non ut",
          fields: [
            {
              type: {
                uniqueKey: "Businessphonenumber",
                label: "Business phone number"
              },
              mandatory: true,
              label: "Test Business phone number label",
              helpText: "Test Business phone  help text"
            }
          ]
        }
      ],
      fields: [
        {
          type: {
            uniqueKey: "State",
            label: "State"
          },
          mandatory: false,
          label: "Test State label",
          helpText: "Test State help text"
        }
      ]
    },
    {
      id: "test.form.3",
      name: "Fee for service",
      type: "Application",
      headings: [
        {
          name: "Enrolment",
          description:
            "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Cum dicta est fugit neque, recusandae similique totam veritatis. Adipisci atque dolorum ea iusto laudantium maiores nemo non ut",
          fields: [
            {
              type: {
                uniqueKey: "Businessphonenumber",
                label: "Business phone number"
              },
              mandatory: true,
              label: "Test Business phone number label",
              helpText: "Test Business phone  help text"
            }
          ]
        }
      ],
      fields: [
        {
          type: {
            uniqueKey: "State",
            label: "State"
          },
          mandatory: false,
          label: "Test State label",
          helpText: "Test State help text"
        }
      ]
    },
    {
      id: "test.form.4",
      name: "Waiting list Form",
      type: "WaitingList",
      headings: [
        {
          name: "Enrolment",
          description:
            "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Cum dicta est fugit neque, recusandae similique totam veritatis. Adipisci atque dolorum ea iusto laudantium maiores nemo non ut",
          fields: [
            {
              type: {
                uniqueKey: "Businessphonenumber",
                label: "Business phone number"
              },
              mandatory: true,
              label: "Test Business phone number label",
              helpText: "Test Business phone  help text"
            }
          ]
        }
      ],
      fields: [
        {
          type: {
            uniqueKey: "State",
            label: "State"
          },
          mandatory: false,
          label: "Test State label",
          helpText: "Test State help text"
        }
      ]
    },
    {
      id: "test.form.5",
      name: "Enrolment Form",
      type: "Enrolment",
      headings: [
        {
          name: "Enrolment",
          description:
            "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Cum dicta est fugit neque, recusandae similique totam veritatis. Adipisci atque dolorum ea iusto laudantium maiores nemo non ut",
          fields: [
            {
              type: {
                uniqueKey: "Businessphonenumber",
                label: "Business phone number"
              },
              mandatory: true,
              label: "Test Business phone number label",
              helpText: "Test Business phone  help text"
            }
          ]
        }
      ],
      fields: [
        {
          type: {
            uniqueKey: "State",
            label: "State"
          },
          mandatory: false,
          label: "Test State label",
          helpText: "Test State help text"
        }
      ]
    }
  ];
}
