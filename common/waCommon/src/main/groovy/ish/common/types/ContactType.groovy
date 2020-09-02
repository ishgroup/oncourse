package ish.common.types

import ish.oncourse.API

@API
enum ContactType {
    @API
    PLAIN_CONTACT,
    @API
    STUDENT,
    @API
    TUTOR,
    @API
    TUTOR_STUDENT,
    @API
    COMPANY
}