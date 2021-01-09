
export function mockNotes() {
  this.getNotesByEntityName = entityName => {
    switch (entityName) {
      case "Application":
        return [];
      default:
        return [];
    }
  };
}
