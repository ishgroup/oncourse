package ish.oncourse.willow.filters

import ish.oncourse.willow.model.common.CommonError
import ish.oncourse.willow.service.SearchText
import org.apache.commons.lang3.StringUtils
import org.apache.cxf.jaxrs.impl.MetadataMap

import javax.ws.rs.BadRequestException
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.core.Response

@SearchText
class SearchFilter implements ContainerRequestFilter {
    
    @Override
    void filter(ContainerRequestContext requestContext) throws IOException {
        MetadataMap params = requestContext.uriInfo.getPathParameters()

        if (params.size() != 1 || params['text'] == null) {
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'Wrong number of params')).build())
        }
        
        String searchText = params['text'][0]

        if (!StringUtils.trimToNull(searchText)) {
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'Search string is empty')).build())
        }
    }
}
