records = query {
    entity "IntegrationConfiguration"
    query "name = \"${record.tag.name}\""
}
def integration = records.sort { it.createdOn }.reverse()[0]
if (integration && (NodeSpecialType.MAILING_LISTS.displayName.equals(record.tag?.parentTag?.name) || record.tag?.parentTag == null) && record.taggedContact.email) {
    mailchimp {
        name record.tag.name
        action integrationAction
        email record.taggedContact.email
    }
}
