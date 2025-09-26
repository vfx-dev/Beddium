plugins {
    id("com.falsepattern.fpgradle-mc") version ("2.0.0")
}

group = "com.ventooth"

minecraft_fp {
    java {
        compatibility = jvmDowngrader
    }

    mod {
        modid = "beddium"
        name = "Beddium"
        rootPkg = "$group.beddium"
    }

    api {
        packages = listOf("api")
    }

    mixin {
        pkg = "mixin.mixins"
        pluginClass = "mixin.plugin.MixinPlugin"
    }

    core {
        coreModClass = "asm.CoreLoadingPlugin"
        accessTransformerFile = "beddium_at.cfg"
    }

    tokens {
        tokenClass = "Tags"
    }

    publish {
        maven {
            repoUrl = "https://mvn.ventooth.com/releases"
            repoName = "venmaven"
        }
        curseforge {
            projectId = "1065808"
            dependencies {
                required("fplib")
                incompatible("lwjgl3ify")
                optional("lumi")
                optional("rple")
                optional("mcpatcher")
                optional("falsetweaks")
                optional("swansong")
            }
        }
        modrinth {
            projectId = "kPtHpb7z"
            dependencies {
                required("fplib")
                incompatible("lwjgl3ify")
                optional("lumi1710")
                optional("rple")
                optional("mcpatcher")
                optional("falsetweaks")
                optional("swansong")
            }
        }
    }
}

tasks.processResources {
    from(file("LICENSE"))
    from(file(".idea/icon.png")) {
        rename { "beddium.png" }
    }
}

repositories {
    exclusive(mavenpattern(), "com.falsepattern", "org.embeddedt.celeritas")
    exclusive(mega(), "mega")
    exclusive(venmaven(), "com.ventooth")
}

dependencies {
    apiSplit("com.falsepattern:falsepatternlib-mc1.7.10:1.9.0")

    compileOnlyApi("org.joml:joml:1.10.8")
    compileOnlyApi("it.unimi.dsi:fastutil:8.5.16")

    compileOnlyApi("org.embeddedt.celeritas:celeritas-common:2.4.0-dev.4+beddium")
    shadowImplementation("org.embeddedt.celeritas:celeritas-common:2.4.0-dev.4+beddium") {
        excludeDeps()
    }

    compileOnly("mega:fluidlogged-mc1.7.10:0.1.2:api")

    devOnlyNonPublishable("com.ventooth:swansong-mc1.7.10:1.1.2:dev")
}