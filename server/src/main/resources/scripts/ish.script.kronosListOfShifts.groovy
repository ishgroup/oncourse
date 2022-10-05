records = query {
    entity "Session"
    query "startDatetime next week"
}

records.each { r ->
    kronos {
        scheduleName scheduleNameValue
        session r
    }
}