xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

def map = records.groupBy { Room r -> r.site }

xml.data() {
	map.each { Site s, List<Room> rooms ->
		site(id: s.id) {
			modifiedOn(s.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
			createdOn(s.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
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

			rooms.each { Room r ->
				room(id: r.id) {
					modifiedOn(r.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
					createdOn(r.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
					directions(r.directions)
					facilities(r.facilities)
					name(r.name)
					notes(r.notes)
					seatedCapacity(r.seatedCapacity)
				}
			}
		}
	}
}
