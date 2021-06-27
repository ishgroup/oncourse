
export function mockGradingTypes() {
  this.getGradingTypes = () => this.gradingTypes;

  this.removeGradingType = id => {
    this.gradingTypes = this.gradingTypes.filter(type => Number(type.id) !== Number(id));
  };

  return [
    {
      "id": 1,
      "created": "2021-04-13T04:52:44.000Z",
      "modified": "2021-04-13T04:52:44.000Z",
      "name": "Vocational",
      "minValue": 0.00,
      "maxValue": 100.00,
      "entryType": "choice list",
      "gradingItems": [
        {
          "id": 1,
          "created": "2021-04-13T04:52:44.000Z",
          "modified": "2021-04-13T04:52:44.000Z",
          "name": "Competent",
          "lowerBound": 100.00
        },
        {
          "id": 2,
          "created": "2021-04-13T04:52:44.000Z",
          "modified": "2021-04-13T04:52:44.000Z",
          "name": "Not competent",
          "lowerBound": 0.00
        }
      ]
    },
    {
      "id": 2,
      "created": "2021-04-13T04:52:44.000Z",
      "modified": "2021-04-13T04:52:44.000Z",
      "name": "University grades",
      "minValue": 0.00,
      "maxValue": 100.00,
      "entryType": "number",
      "gradingItems": [
        {
          "id": 3,
          "created": "2021-04-13T04:52:44.000Z",
          "modified": "2021-04-13T04:52:44.000Z",
          "name": "High Distinction",
          "lowerBound": 85.00
        },
        {
          "id": 4,
          "created": "2021-04-13T04:52:44.000Z",
          "modified": "2021-04-13T04:52:44.000Z",
          "name": "Distinction",
          "lowerBound": 75.00
        },
        {
          "id": 5,
          "created": "2021-04-13T04:52:44.000Z",
          "modified": "2021-04-13T04:52:44.000Z",
          "name": "Credit",
          "lowerBound": 65.00
        },
        {
          "id": 6,
          "created": "2021-04-13T04:52:44.000Z",
          "modified": "2021-04-13T04:52:44.000Z",
          "name": "Pass",
          "lowerBound": 50.00
        },
        {
          "id": 7,
          "created": "2021-04-13T04:52:44.000Z",
          "modified": "2021-04-13T04:52:44.000Z",
          "name": "Fail",
          "lowerBound": 0.00
        }
      ]
    },
    {
      "id": 3,
      "created": "2021-04-13T04:52:44.000Z",
      "modified": "2021-04-13T04:52:44.000Z",
      "name": "Percent",
      "minValue": 0.00,
      "maxValue": 100.00,
      "entryType": "number",
      "gradingItems": [

      ]
    },
    {
      "id": 4,
      "created": "2021-04-13T04:52:44.000Z",
      "modified": "2021-04-13T04:52:44.000Z",
      "name": "Ten",
      "minValue": 0.00,
      "maxValue": 10.00,
      "entryType": "number",
      "gradingItems": [

      ]
    },
    {
      "id": 5,
      "created": "2021-04-13T04:52:44.000Z",
      "modified": "2021-04-13T04:52:44.000Z",
      "name": "A-F",
      "minValue": 0.00,
      "maxValue": 100.00,
      "entryType": "choice list",
      "gradingItems": [
        {
          "id": 8,
          "created": "2021-04-13T04:52:44.000Z",
          "modified": "2021-04-13T04:52:44.000Z",
          "name": "A",
          "lowerBound": 100.00
        },
        {
          "id": 9,
          "created": "2021-04-13T04:52:44.000Z",
          "modified": "2021-04-13T04:52:44.000Z",
          "name": "B",
          "lowerBound": 85.00
        },
        {
          "id": 10,
          "created": "2021-04-13T04:52:44.000Z",
          "modified": "2021-04-13T04:52:44.000Z",
          "name": "C",
          "lowerBound": 70.00
        },
        {
          "id": 11,
          "created": "2021-04-13T04:52:44.000Z",
          "modified": "2021-04-13T04:52:44.000Z",
          "name": "D",
          "lowerBound": 60.00
        },
        {
          "id": 12,
          "created": "2021-04-13T04:52:44.000Z",
          "modified": "2021-04-13T04:52:44.000Z",
          "name": "E",
          "lowerBound": 50.00
        },
        {
          "id": 13,
          "created": "2021-04-13T04:52:44.000Z",
          "modified": "2021-04-13T04:52:44.000Z",
          "name": "F",
          "lowerBound": 0.00
        }
      ]
    }
  ];
}
