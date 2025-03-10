package org.greedy.ddarahang.db.travelCourse;

import org.springframework.data.jpa.repository.EntityGraph;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@EntityGraph(attributePaths = {"video"})
public @interface VideoEntityGraph {
}
