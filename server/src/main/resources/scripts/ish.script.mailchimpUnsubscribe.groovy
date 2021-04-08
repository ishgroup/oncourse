def integrations = query {
    entity "IntegrationConfiguration"
    query "name = \"${record.tag.name}\""
}
def integration = integrations.sort { it.createdOn }.last()
if (integration && NodeSpecialType.MAILING_LISTS.equals(record.tag?.parentTag?.specialType) && record.taggedContact.email) {
    mailchimp {
        name record.tag.name
        action integrationAction
        email record.taggedContact.email
    }
}
