records = query {
    entity "IntegrationConfiguration"
    query "name = \"${record.tag.name}\""
}
def integration = records.sort { it.createdOn }.reverse()[0]
if (integration && record.taggedContact.email) {
    mailchimp {
        name record.tag.name
        action integrationAction
        email record.taggedContact.email
    }
}
