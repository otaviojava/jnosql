/*
 *  Copyright (c) 2019 Otávio Santana and others
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
 */
package org.jnosql.artemis.reflection;


import org.jnosql.artemis.PreparedStatementAsync;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This instance has the information to run the JNoSQL native query at {@link org.jnosql.artemis.RepositoryAsync}
 * To create an instance use {@link DynamicAsyncQueryMethodReturn#builder()}
 */
public final class DynamicAsyncQueryMethodReturn<T> implements MethodDynamicExecutable {

    private final Method method;
    private final Object[] args;
    private final BiConsumer<String, Consumer<List<T>>> asyncConsumer;
    private final Function<String, PreparedStatementAsync> prepareConverter;

    private DynamicAsyncQueryMethodReturn(Method method, Object[] args, BiConsumer<String,
            Consumer<List<T>>> asyncConsumer, Function<String,
            PreparedStatementAsync> prepareConverter) {
        this.method = method;
        this.args = args;
        this.asyncConsumer = asyncConsumer;
        this.prepareConverter = prepareConverter;
    }

    Method getMethod() {
        return method;
    }

    Object[] getArgs() {
        return args;
    }


    @Override
    public Object execute() {
        String value = RepositoryReflectionUtils.INSTANCE.getQuery(method);
        Map<String, Object> params = RepositoryReflectionUtils.INSTANCE.getParams(method, args);
        Consumer<List<T>> consumer = getConsumer(args);
        if (params.isEmpty()) {
            asyncConsumer.accept(value, consumer);
        } else {
            PreparedStatementAsync prepare = prepareConverter.apply(value);
            params.forEach(prepare::bind);
            prepare.getResultList(consumer);
        }
        return Void.class;
    }

    private <T> Consumer<List<T>> getConsumer(Object[] args) {
        Consumer<List<T>> consumer;
        Object callBack = getCallback(args);
        if (callBack instanceof Consumer) {
            consumer = (Consumer) callBack;
        } else {
            consumer = l -> {
            };
        }
        return consumer;
    }

    private Object getCallback(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        return args[args.length - 1];
    }

    /**
     * Creates a builder class
     *
     * @param <T> the type
     * @return a builder instance
     */
    public static <T> DynamicAsyncQueryMethodReturnBuilder<T> builder() {
        return new DynamicAsyncQueryMethodReturnBuilder<>();
    }

    public static final class DynamicAsyncQueryMethodReturnBuilder<T> {

        private Method method;

        private Object[] args;

        private BiConsumer<String, Consumer<List<T>>> asyncConsumer;

        private Function<String, PreparedStatementAsync> prepareConverter;

        private DynamicAsyncQueryMethodReturnBuilder() {
        }

        public DynamicAsyncQueryMethodReturnBuilder<T> withMethod(Method method) {
            this.method = method;
            return this;
        }

        public DynamicAsyncQueryMethodReturnBuilder<T> withArgs(Object[] args) {
            if(args != null) {
                this.args = args.clone();
            }
            return this;
        }

        public DynamicAsyncQueryMethodReturnBuilder<T> withAsyncConsumer(BiConsumer<String, Consumer<List<T>>> asyncConsumer) {
            this.asyncConsumer = asyncConsumer;
            return this;
        }

        public DynamicAsyncQueryMethodReturnBuilder<T> withPrepareConverter(Function<String, PreparedStatementAsync> prepareConverter) {
            this.prepareConverter = prepareConverter;
            return this;
        }

        public DynamicAsyncQueryMethodReturn<T> build() {
            return new DynamicAsyncQueryMethodReturn<>(method, args, asyncConsumer, prepareConverter);
        }
    }
}
