package ish.oncourse.api.cxf;

import com.google.inject.BindingAnnotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Target([ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD])
@Retention(RetentionPolicy.RUNTIME)
@BindingAnnotation
@interface CXFServlet {
}
