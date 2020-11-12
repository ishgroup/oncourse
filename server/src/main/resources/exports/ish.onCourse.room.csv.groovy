records.each { Room r ->
	csv << [
			'name'                         : r.name,
			'seated capacity'              : r.seatedCapacity,
			'directions'                   : r.directions,
			'facilities'                   : r.facilities,
			'notes'                        : r.notes,
			'created on'                   : r.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
			'modified on'                  : r.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
			'siteName'                     : r.site.name,
			'site is administration centre': r.site.isAdministrationCentre,
			'site Latitude'                : r.site.latitude,
			'site Longitude'               : r.site.longitude,
			'site Postcode'                : r.site.postcode,
			'site State'                   : r.site.state,
			'site Street'                  : r.site.street,
			'site Suburb'                  : r.site.suburb,
			'site Created on'              : r.site.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
			'site Modified on'             : r.site.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX")
	]
}
