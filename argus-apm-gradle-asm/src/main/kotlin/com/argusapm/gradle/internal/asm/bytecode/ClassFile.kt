package com.argusapm.gradle.internal.asm.bytecode

import com.argusapm.gradle.internal.utils.log

class ClassFile {

    companion object {
        //子类与父类的对应关系
        val sRelationshipMap = HashMap<String, String>()

        fun classMapReset() {
            log("------  classMapSize is ${sRelationshipMap.size}")
            sRelationshipMap.clear()
        }

        fun classMap(className: String, superClassName: String) {
            sRelationshipMap[className] = superClassName
        }
    }

}