records.each { Site s ->
	csv << [
			'name'                    : s.name,
			'is administration centre': s.isAdministrationCentre,
			'latitude'                : s.latitude,
			'longitude'               : s.longitude,
			'postcode'                : s.postcode,
			'state'                   : s.state,
			'street'                  : s.street,
			'suburb'                  : s.suburb,
			'created on'              : s.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
			'modified on'             : s.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX")
	]
}
