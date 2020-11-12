def integration = query {
    entity "IntegrationConfiguration"
    query "name = \"${record.tag.name}\""
    first true
}

if (integration && NodeSpecialType.MAILING_LISTS.equals(record.tag?.parentTag?.specialType) && record.taggedContact.email) {
    mailchimp {
        name record.tag.name
        action integrationAction
        email record.taggedContact.email
        firstName record.taggedContact.firstName
        lastName record.taggedContact.lastName
    }
}
