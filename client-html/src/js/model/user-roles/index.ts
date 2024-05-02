import { AccessStatus } from "@api/model";

interface Permission {
  name: string;
  checkbox: boolean;
  alwaysAllowed?: AccessStatus[];
  neverAllowed?: AccessStatus[];
}

interface UserPermissionCategory {
  type: string;
  headers?: AccessStatus[];
  permissions: Permission[];
}

const Categories: UserPermissionCategory[] = [
  {
    type: "People and companies",
    headers: ["Hide", "View", "Print", "Edit", "Create", "Delete"],
    permissions: [
      {
        name: "Contact",
        checkbox: false,
        alwaysAllowed: ["View"]
      }
    ]
  },
  {
    type: "Course management",
    headers: ["Hide", "View", "Print", "Edit", "Create", "Delete"],
    permissions: [
      {name: "Course", checkbox: false, alwaysAllowed: ["View"]},
      {name: "Faculty", checkbox: false, alwaysAllowed: ["View"]},
      {name: "VET Course details", checkbox: true},
      {name: "Class", checkbox: false, alwaysAllowed: ["View"]},
      {name: "Enrolment outcomes", checkbox: true},
      {name: "Budget", checkbox: false},
      {
        name: "Session",
        checkbox: false,
        alwaysAllowed: ["View"],
        neverAllowed: ["Print"]
      },
      {name: "Waiting list", checkbox: false},
      {name: "Assessment", checkbox: false, alwaysAllowed: ["View"]},
    ]
  },
  {
    type: "Vet management",
    headers: ["Hide", "View", "Print", "Edit", "Create", "Delete"],
    permissions: [
      {
        name: "Qualification reference data",
        checkbox: false,
        alwaysAllowed: ["View", "Print"],
      },
      {name: "Certificate", checkbox: false},
      {name: "Print certificate without verified USI", checkbox: true},
      {name: "Print certificate without USI", checkbox: true}
    ]
  },
  {
    type: "Resources",
    headers: ["Hide", "View", "Print", "Edit", "Create", "Delete"],
    permissions: [
      {name: "Site", checkbox: false, alwaysAllowed: ["View"]},
      {name: "Room", checkbox: false, alwaysAllowed: ["View"]}
    ]
  },
  {
    type: "Financial",
    headers: ["Hide", "View", "Print", "Edit", "Create", "Delete"],
    permissions: [
      {name: "Enrolment", checkbox: false, neverAllowed: ["Delete"]},
      {name: "Custom enrolment discount", checkbox: true},
      {name: "Applications", checkbox: false},
      {name: "Discount", checkbox: false},
      {name: "Tutor pay", checkbox: false},
      {name: "Override tutor session payable time", checkbox: true},
      {name: "Bulk confirm tutor wages", checkbox: true},
      {name: "Invoice", checkbox: false, neverAllowed: ["Delete"]},
      {name: "Credit note", checkbox: true},
      {
        name: "Payment in",
        checkbox: false,
        alwaysAllowed: ["Edit"],
        neverAllowed: ["Delete"]
      },
      {
        name: "Payment out",
        checkbox: false,
        alwaysAllowed: ["Edit"],
        neverAllowed: ["Delete"]
      },
      {name: "Payment Method", checkbox: false},
      {name: "Account", checkbox: false},
      {
        name: "Transaction",
        checkbox: false,
        neverAllowed: ["Edit", "Delete"],
        alwaysAllowed: ["Create", "Print"]
      },
      {name: "Banking", checkbox: true},
      {name: "Reconciliation", checkbox: true},
      {name: "CorporatePass", checkbox: false},
      {name: "Payment plan", checkbox: false},
      {name: "Summary extracts", checkbox: true}
    ]
  },
  {
    type: "Special actions",
    permissions: [
      {name: "Class duplication/rollover", checkbox: true},
      {name: "Class cancellation", checkbox: true},
      {name: "Exporting to XML and CSV", checkbox: true},
      {name: "Creating certificate from class", checkbox: true},
      {name: "Contact merging", checkbox: true},
      {name: "Enrolment cancellation and transferring", checkbox: true},
      {name: "Export AVETMISS", checkbox: true},
      {name: "Data import", checkbox: true},
      {name: "Override tutor pay rate", checkbox: true},
      {name: "Edit/Delete Notes", checkbox: true}
    ]
  },
  {
    type: "Messaging",
    permissions: [
      {name: "Email up to 50 contacts", checkbox: true},
      {name: "Email over 50 contacts", checkbox: true},
      {name: "SMS up to 50 contacts", checkbox: true},
      {name: "SMS over 50 contacts", checkbox: true}
    ]
  },
  {
    type: "Web and content management",
    headers: ["Hide", "View", "Print", "Edit", "Create", "Delete"],
    permissions: [
      {name: "Documents", checkbox: false, alwaysAllowed: ["View", "Print"]},
      {
        name: "Private documents",
        checkbox: false,
        neverAllowed: ["Print", "Delete"]
      },
      {name: "Tag", checkbox: false, alwaysAllowed: ["View", "Print"]}
    ]
  },
  {
    type: "Products",
    headers: ["Hide", "View", "Print", "Edit", "Create", "Delete"],
    permissions: [
      {
        name: "Product", checkbox: false, alwaysAllowed: ["View"], neverAllowed: ["Delete"]
      },
      {
        name: "Memberships", checkbox: false, alwaysAllowed: ["View"], neverAllowed: ["Delete"]
      },
      {
        name: "Vouchers", checkbox: false, alwaysAllowed: ["View"], neverAllowed: ["Delete"]
      },
      {
        name: "Sales", checkbox: false, alwaysAllowed: ["View"], neverAllowed: ["Delete"]
      }
    ]
  },
  {
    type: "Other",
    headers: ["Hide", "View", "Print", "Edit", "Create", "Delete"],
    permissions: [
      {name: "Report", checkbox: false, alwaysAllowed: ["View", "Print"]},
      {name: "EmailTemplate", checkbox: false, alwaysAllowed: ["View"]},
      {name: "ExportTemplate", checkbox: false, alwaysAllowed: ["View"]},
      {name: "Scripts", checkbox: false, alwaysAllowed: ["View"]},
      {
        name: "Audit logging",
        checkbox: false,
        neverAllowed: ["Edit", "Create", "Delete"]
      },
      {name: "Change administration centre", checkbox: true},
      {name: "Require two factor authentication", checkbox: true}
    ]
  }
];

export default Categories;
