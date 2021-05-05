import { EntityRelationType } from "@api/model";

export function mockEntityRelationTypes(): EntityRelationType[] {
  this.getEntityRelationTypes = () => this.entityRelationTypes;

  this.removeEntityRelationType = id => {
    this.entityRelationTypes = this.entityRelationTypes.filter(type => Number(type.id) !== Number(id));
  };

  return [
    {
      "id": -1,
      "created": "2020-11-12T04:52:44.000Z",
      "modified": "2020-11-23T14:41:41.000Z",
      "name": "Similar courses",
      "description": "Other courses you might be interested in",
      "fromName": "similar",
      "toName": "similar",
      "isShownOnWeb": true,
      "discountId": null,
      "shoppingCart": "No action",
      "considerHistory": true
    },
    {
      "id": 1,
      "created": "2020-11-12T04:52:44.000Z",
      "modified": "2020-11-23T06:41:33.000Z",
      "name": "Level progression",
      "description": "Courses at other levels of difficulty",
      "fromName": "previous",
      "toName": "next",
      "isShownOnWeb": true,
      "discountId": null,
      "shoppingCart": "Suggestion",
      "considerHistory": true
    },
    {
      "id": 2,
      "created": "2020-11-12T04:52:44.000Z",
      "modified": "2020-11-12T04:52:44.000Z",
      "name": "Bundles",
      "description": "Courses are bundled into a discounted packagen",
      "fromName": "bundle",
      "toName": "component",
      "isShownOnWeb": true,
      "discountId": null,
      "shoppingCart": "Add but do not allow removal",
      "considerHistory": false
    },
    {
      "id": 3,
      "created": "2020-11-12T04:52:44.000Z",
      "modified": "2020-11-12T04:52:44.000Z",
      "name": "Application fee",
      "description": "A product fee which must be added to checkout",
      "fromName": "course application",
      "toName": "product fee",
      "isShownOnWeb": true,
      "discountId": null,
      "shoppingCart": "Add but do not allow removal",
      "considerHistory": false
    },
    {
      "id": 4,
      "created": "2020-11-12T04:52:44.000Z",
      "modified": "2020-11-12T04:52:44.000Z",
      "name": "Rolling intake",
      "description": "Flexible course delivery",
      "fromName": "rolling intake course",
      "toName": "rolling intake subject",
      "isShownOnWeb": true,
      "discountId": null,
      "shoppingCart": "Add but do not allow removal",
      "considerHistory": true
    },
    {
      "id": 5,
      "created": "2020-11-12T04:52:44.000Z",
      "modified": "2020-11-20T07:00:03.000Z",
      "name": "Course materials",
      "description": "Suggested course materials",
      "fromName": "course",
      "toName": "course material",
      "isShownOnWeb": true,
      "discountId": null,
      "shoppingCart": "Add and allow removal",
      "considerHistory": false
    }
  ];
}
