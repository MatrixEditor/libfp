package io.github.libfp.benchmark;//@date 01.11.2023

import java.util.function.Predicate;

public interface Whitelist
        extends Predicate<TestResult>
{
    @Override
    boolean test(TestResult testResult);
}
