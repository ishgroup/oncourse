package ish.oncourse.willow.editor.services.access

import org.apache.cxf.Bus
import org.apache.cxf.annotations.Provider
import org.apache.cxf.feature.AbstractFeature
import org.apache.cxf.interceptor.InterceptorProvider

class PostProcessFeature extends AbstractFeature {

    @Override
    protected void initializeProvider(InterceptorProvider provider, Bus bus) {
        PostProcessInterceptor interceptor = new PostProcessInterceptor()
        bus.inInterceptors << interceptor
        bus.inFaultInterceptors << interceptor
    }
}
