export default {
  "entity": "Course",
  "search": "",
  "pageSize": 50,
  "offset": 0,
  "sort": [
    {
      "attribute": "name",
      "ascending": true,
      "complexAttribute": []
    }
  ],
  "columns": [
    {
      "title": "Tags",
      "attribute": "tagColors",
      "type": "Tags",
      "sortable": false,
      "visible": true,
      "system": null,
      "width": 100,
      "sortFields": [],
      "prefetches": []
    },
    {
      "title": "Checklists",
      "attribute": "checklistsColor",
      "type": null,
      "sortable": false,
      "visible": true,
      "system": null,
      "width": 100,
      "sortFields": [],
      "prefetches": []
    },
    {
      "title": "Name",
      "attribute": "name",
      "type": null,
      "sortable": true,
      "visible": true,
      "system": null,
      "width": 400,
      "sortFields": [],
      "prefetches": []
    },
    {
      "title": "Code",
      "attribute": "code",
      "type": null,
      "sortable": true,
      "visible": true,
      "system": null,
      "width": 200,
      "sortFields": [],
      "prefetches": []
    },
    {
      "title": "Field",
      "attribute": "fieldOfEducation",
      "type": null,
      "sortable": true,
      "visible": true,
      "system": null,
      "width": 200,
      "sortFields": [],
      "prefetches": []
    },
    {
      "title": "Qualification",
      "attribute": "qualification.nationalCode",
      "type": null,
      "sortable": true,
      "visible": true,
      "system": null,
      "width": 200,
      "sortFields": [
        "qualification+.nationalCode"
      ],
      "prefetches": []
    },
    {
      "title": "Current classes",
      "attribute": "currentClassesCount",
      "type": null,
      "sortable": false,
      "visible": true,
      "system": null,
      "width": 200,
      "sortFields": [],
      "prefetches": []
    },
    {
      "title": "Currently offered",
      "attribute": "currentlyOffered",
      "type": "Boolean",
      "sortable": true,
      "visible": false,
      "system": true,
      "width": 100,
      "sortFields": [],
      "prefetches": []
    },
    {
      "title": "Shown on web",
      "attribute": "isShownOnWeb",
      "type": "Boolean",
      "sortable": true,
      "visible": false,
      "system": true,
      "width": 100,
      "sortFields": [],
      "prefetches": []
    },
    {
      "title": "Enrolment type",
      "attribute": "displayableEnrolmentType",
      "type": null,
      "sortable": true,
      "visible": false,
      "system": false,
      "width": 100,
      "sortFields": [
        "enrolmentType"
      ],
      "prefetches": []
    },
    {
      "title": "Data collection",
      "attribute": "dataCollectionRuleName",
      "type": null,
      "sortable": false,
      "visible": false,
      "system": null,
      "width": 200,
      "sortFields": [],
      "prefetches": [
        "fieldConfigurationSchema"
      ]
    },
    {
      "title": "Total classes",
      "attribute": "totalClassesCount",
      "type": null,
      "sortable": false,
      "visible": false,
      "system": null,
      "width": 200,
      "sortFields": [],
      "prefetches": []
    },
    {
      "title": "Related Sellables",
      "attribute": "relatedSellables",
      "type": null,
      "sortable": false,
      "visible": false,
      "system": null,
      "width": 200,
      "sortFields": [],
      "prefetches": []
    }
  ],
  "rows": [
    {
      "id": "1321",
      "values": [
        "[53baed, ad5f00]",
        null,
        "000",
        "000",
        null,
        null,
        "0",
        "true",
        "true",
        JSON.stringify([
          {
            "id": 307,
            "name": "Free Cake",
            "code": "CIAL",
            "active": true,
            "type": "Product",
            "expiryDate": null,
            "entityFromId": 460,
            "entityToId": null,
            "relationId": -1
          },
          {
            "id": 308,
            "name": "100$ Course 1",
            "code": "100c11",
            "active": true,
            "type": "Course",
            "expiryDate": null,
            "entityFromId": 560,
            "entityToId": null,
            "relationId": 4
          },
          {
            "id": 230,
            "name": "producct test",
            "code": "prodtest",
            "active": true,
            "type": "Product",
            "expiryDate": null,
            "entityFromId": null,
            "entityToId": 201,
            "relationId": -1
          }
        ])
      ]
    },
    {
      "id": "240",
      "values": [
        "[c8d1e0, ed5393, dde3ed]",
        null,
        "04_08_test_Course",
        "tc0408",
        null,
        null,
        "0",
        "true",
        "false",
        JSON.stringify([
          {
            "id": 317,
            "name": "Free Cake 1",
            "code": "CIAL1",
            "active": true,
            "type": "Product",
            "expiryDate": null,
            "entityFromId": 461,
            "entityToId": null,
            "relationId": -1
          },
          {
            "id": 318,
            "name": "100$ Course 1 100c111",
            "code": "100c111",
            "active": true,
            "type": "Course",
            "expiryDate": null,
            "entityFromId": 561,
            "entityToId": null,
            "relationId": 4
          },
          {
            "id": 234,
            "name": "producct test 1",
            "code": "prodtest1",
            "active": true,
            "type": "Product",
            "expiryDate": null,
            "entityFromId": null,
            "entityToId": 202,
            "relationId": -1
          }
        ])
      ]
    },
    {
      "id": "560",
      "values": [
        "[53baed, c8d1e0, 08827a]",
        null,
        "100$ Course 1",
        "100c11",
        "1005",
        "40511SA",
        "0",
        "true",
        "false",
        JSON.stringify([
          {
            "id": 310,
            "name": "Free Cake 2",
            "code": "CIAL2",
            "active": true,
            "type": "Product",
            "expiryDate": null,
            "entityFromId": 462,
            "entityToId": null,
            "relationId": -1
          },
          {
            "id": 311,
            "name": "100$ Course 1 100c12",
            "code": "100c12",
            "active": true,
            "type": "Course",
            "expiryDate": null,
            "entityFromId": 562,
            "entityToId": null,
            "relationId": 4
          },
          {
            "id": 212,
            "name": "producct test 2",
            "code": "prodtest2",
            "active": true,
            "type": "Product",
            "expiryDate": null,
            "entityFromId": null,
            "entityToId": 202,
            "relationId": -1
          }
        ])
      ]
    },
    {
      "id": "1290",
      "values": [
        "[08827a, c8d1e0, d6246e]",
        null,
        "100$ Course 1",
        "100c12",
        "1005",
        "40511SA",
        "0",
        "true",
        "false",
        JSON.stringify([
          {
            "id": 261,
            "name": "Locked Course locked",
            "code": "locked",
            "active": true,
            "type": "Course",
            "expiryDate": null,
            "entityFromId": null,
            "entityToId": 1316,
            "relationId": 7
          }
        ])
      ]
    },
    {
      "id": "720",
      "values": [
        "[f78bb8, 0ea197, 0c7bb3]",
        null,
        "1000$ Course",
        "1000c",
        null,
        null,
        "0",
        "true",
        "true",
        JSON.stringify([
          {
            "id": 216,
            "name": "AB Course AB17",
            "code": "AB17",
            "active": true,
            "type": "Course",
            "expiryDate": null,
            "entityFromId": 1194,
            "entityToId": null,
            "relationId": -1
          }
        ])
      ]
    },
    {
      "id": "1327",
      "values": [
        "[]",
        null,
        "1001$ Course",
        "1000c1",
        null,
        null,
        "0",
        "true",
        "true",
        JSON.stringify([
          {
            "id": 303,
            "name": "Creative Kids $100 Voucher",
            "code": "CK21",
            "active": true,
            "type": "Voucher",
            "expiryDate": null,
            "entityFromId": null,
            "entityToId": 601,
            "relationId": 1
          }
        ])
      ]
    },
    {
      "id": "1342",
      "values": [
        "[]",
        null,
        "1002$ Course",
        "1002c",
        null,
        null,
        "0",
        "true",
        "true",
        JSON.stringify([
          {
            "id": 280,
            "name": "Install Basic Components",
            "code": "10",
            "active": true,
            "type": "Module",
            "expiryDate": null,
            "entityFromId": null,
            "entityToId": 49582,
            "relationId": 8
          },
          {
            "id": 281,
            "name": "Work Effectively with other to Maintain Production",
            "code": "11",
            "active": true,
            "type": "Module",
            "expiryDate": null,
            "entityFromId": null,
            "entityToId": 48344,
            "relationId": 8
          },
          {
            "id": 304,
            "name": "Creative Kids $100 Voucher",
            "code": "CK21",
            "active": true,
            "type": "Voucher",
            "expiryDate": null,
            "entityFromId": null,
            "entityToId": 601,
            "relationId": 1
          }
        ])
      ]
    },
    {
      "id": "1319",
      "values": [
        "[c79bf2, ad5f00, 53baed]",
        null,
        "111",
        "111",
        "1005",
        "40511SA",
        "0",
        "true",
        "false",
        JSON.stringify([
          {
            "id": 301,
            "name": "Creative Kids $100 Voucher",
            "code": "CK21",
            "active": true,
            "type": "Voucher",
            "expiryDate": null,
            "entityFromId": null,
            "entityToId": 601,
            "relationId": 1
          }
        ])
      ]
    },
    {
      "id": "1357",
      "values": [
        "[]",
        null,
        "123",
        "123",
        "123456",
        null,
        "0",
        "true",
        "false",
        JSON.stringify([
          {
            "id": 285,
            "name": "Biotics btc1",
            "code": "btc1",
            "active": true,
            "type": "Course",
            "expiryDate": null,
            "entityFromId": null,
            "entityToId": 260,
            "relationId": 7
          },
          {
            "id": 293,
            "name": "N7 Recruit",
            "code": "N7R",
            "active": true,
            "type": "Membership",
            "expiryDate": null,
            "entityFromId": null,
            "entityToId": 561,
            "relationId": 7
          },
          {
            "id": 294,
            "name": "N7 Voucher",
            "code": "N7V",
            "active": true,
            "type": "Voucher",
            "expiryDate": null,
            "entityFromId": null,
            "entityToId": 597,
            "relationId": 7
          },
          {
            "id": 295,
            "name": "The Book",
            "code": "THB1",
            "active": true,
            "type": "Product",
            "expiryDate": null,
            "entityFromId": null,
            "entityToId": 240,
            "relationId": 7
          },
          {
            "id": 299,
            "name": "UI Voucher",
            "code": "uivouch",
            "active": true,
            "type": "Voucher",
            "expiryDate": null,
            "entityFromId": null,
            "entityToId": 599,
            "relationId": 7
          },
          {
            "id": 302,
            "name": "Creative Kids $100 Voucher",
            "code": "CK21",
            "active": true,
            "type": "Voucher",
            "expiryDate": null,
            "entityFromId": null,
            "entityToId": 601,
            "relationId": 1
          }
        ])
      ]
    },
    {
      "id": "1205",
      "values": [
        "[c8d1e0, ed5393, c79bf2]",
        null,
        "2 Session Course",
        "2sesc",
        null,
        null,
        "0",
        "true",
        "false",
        JSON.stringify([
          {
            "id": 300,
            "name": "Creative Kids $100 Voucher",
            "code": "CK21",
            "active": true,
            "type": "Voucher",
            "expiryDate": null,
            "entityFromId": null,
            "entityToId": 601,
            "relationId": 1
          }
        ])
      ]
    },
    {
      "id": "500",
      "values": [
        "[c79bf2, 229ad6, 48799c]",
        null,
        "5S Course",
        "5sc",
        null,
        null,
        "0",
        "true",
        "true",
        JSON.stringify([
          {
            "id": 282,
            "name": "Nat Course natco",
            "code": "natco",
            "active": true,
            "type": "Course",
            "expiryDate": null,
            "entityFromId": null,
            "entityToId": 1353,
            "relationId": -1
          }
        ])
      ]
    },
    {
      "id": "1255",
      "values": [
        "[229ad6, 48799c, 9656d6]",
        null,
        "5S Course",
        "5sc1",
        null,
        null,
        "0",
        "true",
        "true",
        JSON.stringify([
          {
            "id": 218,
            "name": "AB Course AB17",
            "code": "AB17",
            "active": true,
            "type": "Course",
            "expiryDate": null,
            "entityFromId": 1194,
            "entityToId": null,
            "relationId": -1
          }
        ])
      ]
    },
    {
      "id": "1284",
      "values": [
        "[229ad6, 3737b3, c79bf2]",
        null,
        "5S Course",
        "5sc2",
        null,
        null,
        "0",
        "true",
        "true",
        JSON.stringify([
          {
            "id": 283,
            "name": "UoC Course 1 uoc1",
            "code": "uoc1",
            "active": true,
            "type": "Course",
            "expiryDate": null,
            "entityFromId": null,
            "entityToId": 1217,
            "relationId": 2
          }
        ])
      ]
    },
    {
      "id": "501",
      "values": [
        "[84b7db, f55353, 7a68b3]",
        null,
        "5S VET Course",
        "5svc",
        "0301",
        "AUM25101",
        "0",
        "true",
        "true",
        JSON.stringify([
          {
            "id": 232,
            "name": "UoC Course 6 uoc6",
            "code": "uoc6",
            "active": true,
            "type": "Course",
            "expiryDate": null,
            "entityFromId": null,
            "entityToId": 1233,
            "relationId": -1
          }
        ])
      ]
    },
    {
      "id": "1169",
      "values": [
        "[086962, de1b1b, 53baed]",
        null,
        "AB 15 Course",
        "AB15",
        null,
        null,
        "0",
        "true",
        "true",
        JSON.stringify([
          {
            "id": 207,
            "name": "Massive VET Course masvetc1",
            "code": "masvetc1",
            "active": true,
            "type": "Course",
            "expiryDate": null,
            "entityFromId": 1162,
            "entityToId": null,
            "relationId": -1
          },
          {
            "id": 208,
            "name": "Massive VET Course masvetc3",
            "code": "masvetc3",
            "active": true,
            "type": "Course",
            "expiryDate": null,
            "entityFromId": 1207,
            "entityToId": null,
            "relationId": -1
          },
          {
            "id": 201,
            "name": "The Book",
            "code": "THB1",
            "active": true,
            "type": "Product",
            "expiryDate": null,
            "entityFromId": null,
            "entityToId": 240,
            "relationId": -1
          }
        ])
      ]
    }
  ],
  "filteredCount": 223,
  "layout": "Three column",
  "filterColumnWidth": 265,
  "tagsOrder": []
};
