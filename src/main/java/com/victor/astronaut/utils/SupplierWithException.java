package com.victor.astronaut.utils;

@FunctionalInterface
public interface SupplierWithException<T> {
    T supply() throws Exception;
}
