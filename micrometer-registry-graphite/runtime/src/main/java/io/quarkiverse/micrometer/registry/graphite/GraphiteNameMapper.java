package io.quarkiverse.micrometer.registry.graphite;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Documented
@Qualifier
public @interface GraphiteNameMapper {
}
