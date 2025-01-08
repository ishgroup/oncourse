package ish.oncourse.server.integration

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Use this for static void method that accept single io.bootique.di.Binder argument
 * Is called on DI configuration phase  
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface OnConfigure {

}