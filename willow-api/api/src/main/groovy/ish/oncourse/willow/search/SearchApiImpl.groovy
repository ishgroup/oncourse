package ish.oncourse.willow.search

import com.google.inject.Inject
import ish.oncourse.willow.model.common.CommonError
import ish.oncourse.willow.model.common.Item
import ish.oncourse.willow.service.SearchApi
import org.apache.cayenne.configuration.server.ServerRuntime
import org.apache.commons.lang3.StringUtils

import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response


class SearchApiImpl implements SearchApi {


    ServerRuntime cayenneRuntime
    SearchService searchService

    @Inject
    ContactApiServiceImpl(ServerRuntime cayenneRuntime, SearchService searchService) {
        this.cayenneRuntime = cayenneRuntime
        this.searchService = searchService
    }
    
    
    @Override
    List<Item> getCountries(String text) {
        return null
    }

    @Override
    List<Item> getLanguages(String text) {
        return null
    }

    @Override
    List<Item> getPostcodes(String text) {
        if (StringUtils.trimToNull(text)) {
            searchService.searchSuburbsByPostcode(text)
        } else {
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'Search string is empty')).build())
        }
    }

    @Override
    List<Item> getSuburbs(String text) {
        if (StringUtils.trimToNull(text)) {
            searchService.searchSuburbsByName(text)
        } else {
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'Search string is empty')).build())
        }    
    }
}
