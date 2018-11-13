package io.javawatch;

import jdk.jshell.JShell;
import org.jetbrains.annotations.Nullable;

public class Shell {
    public static void main(String[] args) {
        var jShell = JShell.create(); //Java 9
        jShell.eval("String result;");
        jShell.eval(`result = "Hello Habr!";`); //Java 12
        var result = jShell.variables() //Streams: Java 8, var: Java 10
            .filter((@Nullable var v) -> v.name().equals("result")) //var+lambda: Java 11
            .findAny()
            .get();
        System.out.println(jShell.varValue(result));
    }
}
