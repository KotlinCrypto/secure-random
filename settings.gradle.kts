rootProject.name = "secure-random"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

includeBuild("build-logic")

@Suppress("PrivatePropertyName")
private val CHECK_PUBLICATION: String? by settings

if (CHECK_PUBLICATION != null) {
    include(":tools:check-publication")
} else {
    listOf(
        "secure-random",
    ).forEach { name ->
        // TODO: :library Issue #22
        include(":$name")
    }

    include(":benchmarks")
    include(":sample")
}
