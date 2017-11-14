package ish.oncourse.willow.search

import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.oncourse.api.cayenne.CayenneService
import ish.oncourse.model.Country
import ish.oncourse.model.Language
import ish.oncourse.willow.model.common.Item
import ish.oncourse.willow.service.SearchApi
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy


@CompileStatic
class SearchApiImpl implements SearchApi {


    CayenneService cayenneService
    SearchService searchService

    @Inject
    SearchApiImpl(CayenneService cayenneService, SearchService searchService) {
        this.cayenneService = cayenneService
        this.searchService = searchService
    }
    
    
    @Override
    List<Item> getCountries(String text) {
        List<Item> result = []

        ObjectSelect.query(Country.class).
                where(Country.NAME.likeIgnoreCase("%" + text + "%")).
                cacheStrategy(QueryCacheStrategy.SHARED_CACHE).
                cacheGroup(Country.class.simpleName).
                select(cayenneService.newContext()).each { c->
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
                cacheGroup(Language.class.simpleName).
                select(cayenneService.newContext()).each { l ->
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
