package com.argusapm.gradle.internal.utils

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project

fun getVariants(project: Project): DomainObjectSet<ApplicationVariant> {
    return project.extensions.getByType(AppExtension::class.java).applicationVariants
}