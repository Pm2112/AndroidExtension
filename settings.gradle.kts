pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        maven("https://jitpack.io")
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Pm2112/AndroidExtension")
            credentials {
                val githubUser = providers.gradleProperty("gpr.user")
                    .orElse(providers.environmentVariable("GITHUB_USERNAME"))
                val githubToken = providers.gradleProperty("gpr.token")
                    .orElse(providers.environmentVariable("GITHUB_TOKEN"))

                if (githubUser.orNull.isNullOrBlank() || githubToken.orNull.isNullOrBlank()) {
                    throw GradleException(
                        "GitHub Packages credentials are missing! Please set " +
                            "'gpr.user' and 'gpr.token' in gradle.properties or environment variables."
                    )
                }

                username = githubUser.get()
                password = githubToken.get()
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(
        RepositoriesMode.FAIL_ON_PROJECT_REPOS
    )
    repositories {
        maven("https://jitpack.io")
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Pm2112/AndroidExtension")
            credentials {
                val githubUser = providers.gradleProperty("gpr.user")
                    .orElse(providers.environmentVariable("GITHUB_USERNAME"))
                val githubToken = providers.gradleProperty("gpr.token")
                    .orElse(providers.environmentVariable("GITHUB_TOKEN"))

                if (githubUser.orNull.isNullOrBlank() || githubToken.orNull.isNullOrBlank()) {
                    throw GradleException(
                        "GitHub Packages credentials are missing! Please set 'gpr.user' and 'gpr.token' " +
                            "in gradle.properties or environment variables."
                    )
                }

                username = githubUser.get()
                password = githubToken.get()
            }
        }
        maven {
            url = uri("https://maven.pkg.github.com/Pm2112/AndroidExtension")
            credentials {
                val githubUser = providers.gradleProperty("gpr.user")
                    .orElse(providers.environmentVariable("GITHUB_USERNAME"))
                val githubToken = providers.gradleProperty("gpr.token")
                    .orElse(providers.environmentVariable("GITHUB_TOKEN"))

                if (githubUser.orNull.isNullOrBlank() || githubToken.orNull.isNullOrBlank()) {
                    throw GradleException(
                        "GitHub Packages credentials are missing! Please set 'gpr.user' and 'gpr.token' " +
                                "in gradle.properties or environment variables."
                    )
                }

                username = githubUser.get()
                password = githubToken.get()
            }
        }
        google()
        mavenCentral()
    }
}

rootProject.name = "AndroidExtension"
include(":app")
include(":Permission")
include(":Network")
include(":Audio")
include(":File")
