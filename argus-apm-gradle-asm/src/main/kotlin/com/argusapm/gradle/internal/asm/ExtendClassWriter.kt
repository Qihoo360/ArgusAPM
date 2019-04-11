package com.argusapm.gradle.internal.asm

import org.objectweb.asm.ClassWriter


class ExtendClassWriter( flags: Int) : ClassWriter(flags) {

    private val DIR_SEPARATOR_UNIX = '/'
    private val EXTENSION_SEPARATOR = '.';


    override fun getCommonSuperClass(type1: String, type2: String): String? {
        try {
            var c = Class.forName(type1.replace(DIR_SEPARATOR_UNIX, EXTENSION_SEPARATOR), true, this.classLoader);
            val d = Class.forName(type2.replace(DIR_SEPARATOR_UNIX, EXTENSION_SEPARATOR), true, this.classLoader);
            if (c.isAssignableFrom(d)) {
                return type1;
            }
            if (d.isAssignableFrom(c)) {
                return type2;
            }
            if (c.isInterface || d.isInterface) {
                return "java/lang/Object";
            }
            do {
                c = c.superclass;
            } while (!c.isAssignableFrom(d));
            return c.name.replace(EXTENSION_SEPARATOR, DIR_SEPARATOR_UNIX);
        } catch (e: Exception) {
            throw  RuntimeException(e.toString());
        }

    }
}