package com.xbot.convetion

import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope

fun DependencyHandlerScope.implementation(name: Provider<MinimalExternalModuleDependency>) {
    add("implementation", name)
}

fun DependencyHandlerScope.debugImplementation(name: Provider<MinimalExternalModuleDependency>) {
    add("debugImplementation", name)
}

fun DependencyHandlerScope.androidTestImplementation(name: Provider<MinimalExternalModuleDependency>) {
    add("androidTestImplementation", name)
}

fun DependencyHandlerScope.ksp(name: Provider<MinimalExternalModuleDependency>) {
    add("ksp", name)
}

fun DependencyHandlerScope.coreLibraryDesugaring(name: Provider<MinimalExternalModuleDependency>) {
    add("coreLibraryDesugaring", name)
}

fun DependencyHandlerScope.detektPlugins(name: Provider<MinimalExternalModuleDependency>) {
    add("detektPlugins", name)
}