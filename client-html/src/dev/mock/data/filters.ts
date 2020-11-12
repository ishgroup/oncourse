import { Filter } from "@api/model";

export function mockFilters(): any[] {
  this.getFilters = (entity): Filter[] => {
    return this.filters.filter(item => item.entity === entity).map(item => ({
      name: item.name,
      search: item.search,
      id: item.id
    }));
  };

  return [
    {
      name: "Available",
      id: "11",
      entity: "Audit"
    },
    {
      name: "Only Enrolments",
      id: "22",
      entity: "Audit"
    },
    {
      name: "Is Active",
      id: "33",
      entity: "Audit"
    },
    {
      name: "Custom filter",
      id: "44",
      entity: "Audit"
    },
    {
      name: "Students",
      id: "45",
      entity: "Contact",
      search: "(isCompany == false) and (student.id != null)"
    },
    {
      name: "Companies",
      id: "45",
      entity: "Contact",
      search: "(isCompany == true)",
      active: true
    }
  ];
}
