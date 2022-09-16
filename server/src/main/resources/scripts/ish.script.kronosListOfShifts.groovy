records = query {
    entity "Session"
    query "startDatetime next week"
}

records.each { r ->
    kronos {
        scheduleName "Example Weekly Test Schedule"
        session r
    }
}