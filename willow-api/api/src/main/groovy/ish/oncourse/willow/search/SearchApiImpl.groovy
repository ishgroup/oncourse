package ish.oncourse.willow.search

import com.google.inject.Inject
import ish.oncourse.model.Country
import ish.oncourse.model.Language
import ish.oncourse.willow.model.common.Item
import ish.oncourse.willow.service.SearchApi
import org.apache.cayenne.configuration.server.ServerRuntime
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy



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
        List<Item> result = []

        ObjectSelect.query(Country.class).
                where(Country.NAME.likeIgnoreCase("%" + text + "%")).
                cacheStrategy(QueryCacheStrategy.SHARED_CACHE).
                cacheGroups(Country.class.simpleName).
                select(cayenneRuntime.newContext()).each { c->
                    result << new Item(key: c.id.toString(), value: c.name)
                }
        result
    }

    @Override
    List<Item> getLanguages(String text) {
        List<Item> result = []

        ObjectSelect.query(Language.class).
                where(Language.NAME.likeIgnoreCase(text + "%")).
                cacheStrategy(QueryCacheStrategy.SHARED_CACHE).
                cacheGroups(Language.class.simpleName).
                select(cayenneRuntime.newContext()).each { l ->
            result << new Item(key: l.id.toString(), value: l.name)
        }
        result    }

    @Override
    List<Item> getPostcodes(String text) {
        searchService.searchSuburbsByPostcode(text)
    }

    @Override
    List<Item> getSuburbs(String text) {
        searchService.searchSuburbsByName(text)
    }
}
