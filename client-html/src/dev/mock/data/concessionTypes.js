/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
export function mockConcessionTypes() {
    this.saveConcessionType = items => {
        this.concessionTypes = items;
    };
    this.removeConcessionType = id => {
        this.concessionTypes = this.concessionTypes.filter(it => it.id !== id);
    };
    return [
        {
            id: 886543,
            name: "Seniors card",
            requireExpary: false,
            requireNumber: true,
            allowOnWeb: true
        },
        {
            id: 5684452,
            name: "Student ",
            requireExpary: true,
            requireNumber: false,
            allowOnWeb: false
        },
        {
            id: 32435,
            name: "Pensioner",
            requireExpary: true,
            requireNumber: true,
            allowOnWeb: true
        }
    ];
}
//# sourceMappingURL=concessionTypes.js.map