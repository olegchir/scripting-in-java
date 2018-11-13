package io.javawatch;

import org.joor.Reflect;

import java.util.function.Supplier;

public class Automatic {
    public static void main(String[] args) {

        Supplier<String> supplier = Reflect.compile(
                "io.javawatch.HelloHabr",
                `
                package io.javawatch;
                public class HelloHabr implements java.util.function.Supplier<String> {
                    public String get() {
                         return ``Hello Habr!``;
                    }
                };`
        ).create().get();

        System.out.println(supplier.get());
    }
}
