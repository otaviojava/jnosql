/*
 *
 *  Copyright (c) 2017 Otávio Santana and others
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   and Apache License v2.0 which accompanies this distribution.
 *   The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *   and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *   You may elect to redistribute this code under either of these licenses.
 *
 *   Contributors:
 *
 *   Otavio Santana
 *
 */
package org.jnosql.diana.api.writer;

import org.jnosql.diana.api.ValueWriter;

/**
 * Value writer to {@link Enum}.
 * This writer converts the enum to {@link String} using {@link Enum#name()}
 */
public class EnumValueWriter implements ValueWriter<Enum<?>, String> {

    @Override
    public <T> boolean isCompatible(Class<T> clazz) {
        return Enum.class.isAssignableFrom(clazz);
    }

    @Override
    public String write(Enum<?> object) {
        return object.name();
    }
}
