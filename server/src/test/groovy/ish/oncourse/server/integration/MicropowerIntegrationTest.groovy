package ish.oncourse.server.integration

import groovy.transform.CompileDynamic

@CompileDynamic
class MicropowerIntegrationTest {

    
    static void main(String[] args) {

        MicropowerIntegration integration = new MicropowerIntegration('clubId': '50051')

        def members = (List) integration.members


        def status = 'MemberStatus'
        def date = 'FinancialToDate'


        members.each { Map member ->
            List fields = member?.get('fields')
            boolean result = false
            println()

            if (fields) {
                def all = fields.findAll { Map field ->
                    field.name == status || field.name == date

                }

                all.each { Map field ->
                    println field
                    println "${field.name} ${field.value} ${field.type}"
                }

                if (all.size() == 2) {
                    result = true
                }
            }

            println result
        }
    }
}
