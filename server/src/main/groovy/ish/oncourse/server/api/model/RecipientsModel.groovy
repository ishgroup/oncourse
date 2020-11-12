/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.model

class RecipientsModel {
    RecipientGroupModel students
    RecipientGroupModel activeStudents
    RecipientGroupModel withdrawStudents
    RecipientGroupModel tutors
    RecipientGroupModel other

    RecipientsModel() {
        this.students = new RecipientGroupModel()
        this.activeStudents = new RecipientGroupModel()
        this.withdrawStudents = new RecipientGroupModel()
        this.tutors = new RecipientGroupModel()
        this.other = new RecipientGroupModel()
    }

    RecipientGroupModel getActiveStudents() {
        return activeStudents
    }

    void setActiveStudents(RecipientGroupModel activeStudents) {
        this.activeStudents = activeStudents
    }

    RecipientGroupModel getWithdrawStudents() {
        return withdrawStudents
    }

    void setWithdrawStudents(RecipientGroupModel withdrawStudents) {
        this.withdrawStudents = withdrawStudents
    }

    RecipientGroupModel getStudents() {
        return students
    }

    void setStudents(RecipientGroupModel students) {
        this.students = students
    }

    RecipientGroupModel getTutors() {
        return tutors
    }

    void setTutors(RecipientGroupModel tutors) {
        this.tutors = tutors
    }

    RecipientGroupModel getOther() {
        return other
    }

    void setOther(RecipientGroupModel other) {
        this.other = other
    }
}
