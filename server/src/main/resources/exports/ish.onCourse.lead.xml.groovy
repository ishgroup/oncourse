xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

def courses = []
def products = []
def sites = []
def contacts = []
def tags = []

xml.data() {
    records.each { Lead ld ->
        lead(id: ld.id) {
            modifiedOn(ld.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ss"))
            createdOn(ld.createdOn?.format("yyyy-MM-dd'T'HH:mm:ss"))
            studentCount(ld.studentCount)
            contacts << ld.customer
            contact(id: ld.customer.id)
            estimatedValue(ld.estimatedValue)
            nextActionOn(ld.nextActionOn?.format("yyyy-MM-dd'T'HH:mm:ss"))
            notification(ld.notify)
            status(ld.status)
            notes(ld.notes)
            ld.courses.each {Course c ->
                courses << c
                course(id: c.id)
            }
            ld.products.each { Product p ->
                products << p
                product(id: p.id)
            }
            ld.sites.each { Site s ->
                sites << s
                site(id: s.id)
            }
            assignedTo(ld.assignedTo?.email)
        }
    }

    courses.unique().each { Course co ->
        course(id: co.id) {
            modifiedOn(co.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ss"))
            createdOn(co.createdOn?.format("yyyy-MM-dd'T'HH:mm:ss"))
            allowWaitingLists(co.allowWaitingLists)
            code(co.code)
            currentlyOffered(co.currentlyOffered)
            isWebVisible(co.isShownOnWeb)
            isSufficientForQualification(co.isSufficientForQualification)
            isVET(co.isVET)
            name(co.name)
            printedBrochureDescription(co.printedBrochureDescription)
            reportableHours(co.reportableHours?.format("0.00"))
            webDescription(co.webDescription)
            co.tags.each { Tag t ->
                tags << t
                tag(id: t.id)
            }
        }
    }

    products.unique().each { Product pr ->
        product(id: pr.id) {
            modifiedOn(pr.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ss"))
            createdOn(pr.createdOn?.format("yyyy-MM-dd'T'HH:mm:ss"))
            isWebVisible(pr.isWebVisible)
            isOnSale(pr.isOnSale)
            name(pr.name)
            prive(pr.priceIncTax)
            description(pr.description)
        }
    }

    sites.unique().each { Site s ->
        site(id: s.id) {
            modifiedOn(s.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ss"))
            createdOn(s.createdOn?.format("yyyy-MM-dd'T'HH:mm:ss"))
            drivingDirections(s.drivingDirections)
            isAdministrationCentre(s.isAdministrationCentre)
            latitude(s.latitude?.toBigDecimal()?.setScale(8)?.toPlainString())
            longitude(s.longitude?.toBigDecimal()?.setScale(8)?.toPlainString())
            name(s.name)
            postcode(s.postcode)
            publicTransportDirections(s.publicTransportDirections)
            state(s.state)
            street(s.street)
            suburb(s.suburb)
        }
    }

    contacts.unique().each { Contact c ->
        contact(id: c.id) {
            modifiedOn(c.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ss"))
            createdOn(c.createdOn?.format("yyyy-MM-dd'T'HH:mm:ss"))
            allowEmail(c.allowEmail)
            allowPost(c.allowPost)
            allowSms(c.allowSms)
            deliveryStatusEmail(c.deliveryStatusEmail)
            deliveryStatusPost(c.deliveryStatusPost)
            deliveryStatusSms(c.deliveryStatusSms)
            email(c.email)
            fax(c.fax)
            firstName(c.firstName)
            homePhone(c.homePhone)
            company(c.isCompany)
            male(c.isMale)
            lastName(c.lastName)
            message(c.message)
            mobilePhone(c.mobilePhone)
            notes(c.notes)
            postcode(c.postcode)
            state(c.state)
            street(c.street)
            suburb(c.suburb)
            tfn(c.tfn)
            uniqueCode(c.uniqueCode)
            workPhone(c.workPhone)
            title(c.title)
            student() {
                disabilityType(c.student.disabilityType.displayName)
                englishProficiency(c.student.englishProficiency.displayName)
                highestSchoolLevel(c.student.highestSchoolLevel.displayName)
                indigenousStatus(c.student.indigenousStatus.displayName)
                isOverseasClient(c.student.isOverseasClient)
                isStillAtSchool(c.student.isStillAtSchool)
                labourForceStatus(c.student.labourForceStatus.displayName)
                priorEducationCode(c.student.priorEducationCode.displayName)
                studentNumber(c.student.studentNumber)
                yearSchoolCompleted(c.student.yearSchoolCompleted)
            }
            tutor() {
                dateStarted(c.tutor?.dateStarted?.format("yyyy-MM-dd"))
                dateFinished(c.tutor?.dateFinished?.format("yyyy-MM-dd"))
                resume(c.tutor?.resume)
                dateFinished(c.tutor?.payrollRef)
            }
        }
    }

    tags.unique().each { Tag t ->
        tag(id: t.id) {
            modifiedOn(t?.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ss"))
            createdOn(t?.createdOn?.format("yyyy-MM-dd'T'HH:mm:ss"))
            name(t?.name)
            shortName(t?.shortName)
            isWebVisible(t?.isWebVisible)
            description(t?.contents)
        }
    }
}
