/*
 * Copyright 2014-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Michael Minella
 * @author Mahmoud Ben Hassine
 * @author Seonkyo Ok
 * @since 2.2.6
 */
class ReflectionUtilsTests {

	@Test
	void testFindAnnotatedMethod() {
		Set<Method> methods = ReflectionUtils.findMethod(AnnotatedClass.class, Transactional.class);
		assertEquals(1, methods.size());
		assertEquals("toString", methods.iterator().next().getName());
	}

	@Test
	void testFindNoAnnotatedMethod() {
		Set<Method> methods = ReflectionUtils.findMethod(AnnotatedClass.class, Autowired.class);
		assertEquals(0, methods.size());
	}

	@Test
	void testFindAnnotatedMethodHierarchy() {
		Set<Method> methods = ReflectionUtils.findMethod(AnnotatedSubClass.class, Transactional.class);
		assertEquals(2, methods.size());

		boolean toStringFound = false;
		boolean methodOneFound = false;

		Iterator<Method> iterator = methods.iterator();

		String name = iterator.next().getName();

		if (name.equals("toString")) {
			toStringFound = true;
		}
		else if (name.equals("methodOne")) {
			methodOneFound = true;
		}

		name = iterator.next().getName();

		if (name.equals("toString")) {
			toStringFound = true;
		}
		else if (name.equals("methodOne")) {
			methodOneFound = true;
		}

		assertTrue(toStringFound && methodOneFound);
	}

	@Test
	void testHasMethodWithAnyAnnotation() {
		assertTrue(ReflectionUtils.hasMethodWithAnyAnnotation(AnnotatedClass.class, List.of(Transactional.class)));
		assertTrue(ReflectionUtils.hasMethodWithAnyAnnotation(AnnotatedClass.class,
				List.of(Transactional.class, Autowired.class)));
		assertFalse(ReflectionUtils.hasMethodWithAnyAnnotation(AnnotatedClass.class, List.of(Autowired.class)));
		assertFalse(ReflectionUtils.hasMethodWithAnyAnnotation(AnnotatedClass.class,
				List.of(Autowired.class, Value.class)));

		assertTrue(ReflectionUtils.hasMethodWithAnyAnnotation(AnnotatedSubClass.class, List.of(Transactional.class)));
		assertFalse(ReflectionUtils.hasMethodWithAnyAnnotation(AnnotatedSubClass.class,
				List.of(Autowired.class, Value.class)));
	}

	public static class AnnotatedClass {

		public void methodOne() {
		}

		@Override
		@Transactional
		public String toString() {
			return "AnnotatedClass";
		}

	}

	public static class AnnotatedSubClass extends AnnotatedClass {

		@Override
		@Transactional
		public void methodOne() {
		}

	}

}
