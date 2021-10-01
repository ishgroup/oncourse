/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.service.impl

import com.google.inject.Inject
import groovy.transform.CompileDynamic
import ish.oncourse.aql.AqlService
import ish.oncourse.cayenne.Taggable
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.v1.model.*
import ish.oncourse.server.api.v1.service.EntityApi
import ish.oncourse.server.cayenne.glue.CayenneDataObject
import ish.oncourse.server.preference.UserPreferenceService
import ish.util.DateFormatter
import ish.util.EntityUtil
import org.apache.cayenne.Cayenne
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.PersistentObject
import org.apache.cayenne.exp.Property
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.Ordering
import org.apache.cayenne.query.PrefetchTreeNode
import org.apache.cayenne.query.SortOrder
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

import static ish.oncourse.server.api.function.EntityFunctions.addAqlExp
import static ish.oncourse.server.api.function.EntityFunctions.parseSearchQuery

@CompileDynamic
class EntityApiImpl implements EntityApi {

    private static Logger logger = LogManager.logger

    private static final BigDecimal DEF_OFFSET = 0
    private static final BigDecimal DEF_PAGE_SIZE = 50
    private static final String ID_FIELD = "id"

    @Inject
    private ICayenneService cayenneService
    @Inject
    private UserPreferenceService preference
    @Inject
    private AqlService aql


    @Override
    DataResponseDTO get(String entity, String search, BigDecimal pageSize, BigDecimal offset) {
        return getAll(entity, new SearchQueryDTO(search: search, pageSize: pageSize, offset: offset))
    }

    @Override
    DataResponseDTO getAll(String entity, SearchQueryDTO request) {
        DataResponseDTO response = createResponse(entity, request.search, request.pageSize, request.offset)
        ObjectContext context = cayenneService.newReadonlyContext

        Class<? extends CayenneDataObject> clzz = EntityUtil.entityClassForName(entity)
        ObjectSelect objectSelect = ObjectSelect.query(clzz)

        ObjectSelect<CayenneDataObject> query = parseSearchQuery(objectSelect, context, aql, entity, request.search, request.filter, request.tagGroups)

        if ( request.filter || request.search || (request.tagGroups && !request.tagGroups.empty)) {
            response.filteredCount = query.column(Property.create("id", Long)).select(context).toSet().size()
        }

        SortOrder sortOrder = null
        query.offset(response.offset.intValue())
        query.limit(response.pageSize.intValue())
        response.sort.each {
            sortOrder = it.ascending ? SortOrder.ASCENDING : SortOrder.DESCENDING
            if ((it.complexAttribute != null) && (it.complexAttribute.size() > 0)) {
                List<Ordering> orderings = new ArrayList<>()
                for (String attribute : it.complexAttribute) {
                    orderings.add(new Ordering(attribute, sortOrder))
                }
                query.orderBy(orderings)
            } else {
                query.orderBy(it.attribute, sortOrder)
            }
        }
        query.orderBy(ID_FIELD, sortOrder != null ? sortOrder : SortOrder.ASCENDING)

        response.columns
                .findAll { it -> it.visible }
                .collect { it -> it.prefetches }
                .flatten()
                .forEach(
                        {
                            prefetch -> query.prefetch(prefetch.toString(), PrefetchTreeNode.DISJOINT_BY_ID_PREFETCH_SEMANTICS)
                        }
                )

        List<PersistentObject> records = query.select(context)
        response.pageSize = new BigDecimal(Math.min(response.pageSize.intValue(), records.size()))
        populateResponce(records, response, response.columns, null)
        return response
    }

    @Override
    DataResponseDTO getPlain(String entity, String search, BigDecimal pageSize, BigDecimal offset, String columnsString, String sortings, Boolean ascending) {
        List<String> columns
        if (StringUtils.trimToNull(columnsString)) {
            columns = columnsString.split(',').toList()
        } else {
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(null, null,"Columns list required.")).build())
        }

        List<String> cayenneSortings = new ArrayList<>()
        if (StringUtils.trimToNull(sortings)) {
            cayenneSortings.addAll(sortings.split(',').toList())
        }

        DataResponseDTO dataResponse = new DataResponseDTO()
        dataResponse.entity = entity.capitalize()

        Class<? extends CayenneDataObject> clzz = EntityUtil.entityClassForName(entity.capitalize())
        ObjectSelect<CayenneDataObject> query = ObjectSelect.query(clzz) as ObjectSelect<CayenneDataObject>
        ObjectContext context = cayenneService.newReadonlyContext
        query = addAqlExp(search, clzz, context, query, aql)

        dataResponse.offset = offset ? offset : DEF_OFFSET
        dataResponse.pageSize = pageSize ? pageSize : DEF_PAGE_SIZE
        query.offset(dataResponse.offset.intValue())
        query.limit(dataResponse.pageSize.intValue())

        if (cayenneSortings.size() != 0) {
            SortOrder sortOrder = ascending ? SortOrder.ASCENDING : SortOrder.DESCENDING
            List<Ordering> orderings = new ArrayList<>()
            cayenneSortings.each { sort ->
                orderings.add(new Ordering(sort, sortOrder))
            }
            query.orderBy(orderings)
        }

        List<PersistentObject> records = query.select(context)
        dataResponse.pageSize = new BigDecimal(Math.min(dataResponse.pageSize.intValue(), records.size()))

        populateResponce(records, dataResponse, null, columns)

        return dataResponse
    }

    @Override
    void updateTableModel(String entity, TableModelDTO tableModel) {
        preference.setTableModel(entity, tableModel)
    }

    private static void populateResponce(List<PersistentObject> records, DataResponseDTO response, List<ColumnDTO> columns = null, List<String> stringColumns = null) {

        List<String> attributes = stringColumns?:columns.findAll { it.visible || it.system }*.attribute
        records.subList(0, response.pageSize.intValue()).each { PersistentObject e ->
            response.rows << new DataRowDTO().with { r ->
                r.id = Cayenne.intPKForObject(e).toString()
                r.values = mapColumns(e, attributes)
                r
            }
        }
    }

    private static List<String> mapColumns(PersistentObject entity, List<String> attributes) {
        attributes.collect { a ->
            Object value = entity

            String[] path = a.split(/\./)
            path.each { part ->
                value = value?."$part"
                if ('byteSize' == part) {
                    value = formatFileSize(value as Long)
                }
            }
            formatValue(value)
        }
    }

    private DataResponseDTO createResponse(String entity, String search, BigDecimal pageSize, BigDecimal offset) {

        DataResponseDTO dataResponse = new DataResponseDTO()

        dataResponse.entity = entity.capitalize()
        TableModelDTO model = preference.getTableModel(dataResponse.entity)
        dataResponse.search = search
        dataResponse.pageSize = pageSize ? pageSize : DEF_PAGE_SIZE
        dataResponse.offset = offset ? offset : DEF_OFFSET
        dataResponse.sort = model.sortings
        dataResponse.columns = model.columns
        dataResponse.filterColumnWidth = model.filterColumnWidth
        dataResponse.layout = model.layout
        dataResponse.tagsOrder = model.tagsOrder

        return dataResponse
    }

    private static String formatValue(Object value) {
        if (value instanceof Boolean) {
            return value.toString()
        }

        if (value == null)
            return null

        if (value instanceof Date) {
            return DateFormatter.formatDateISO8601(value)
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal)value).toPlainString()
        }
        return value.toString()
    }

    private static String formatFileSize(Long size) {
        if (size < 1024) {
            return String.format("%d b", size)
        } else if (size < 1024 * 1024) {
            return String.format("%.2f kb", size / 1024)
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f Mb", size / (1024 * 1024))
        } else {
            return String.format("%.2f Gb", size / (1024 * 1024 * 1024))
        }
        null
    }
}
