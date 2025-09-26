plugins {
    id("com.falsepattern.fpgradle-mc") version ("2.0.0")
}

group = "com.ventooth"

minecraft_fp {
    java {
        compatibility = modern
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
    }
}

tasks.processResources {
    from(file("LICENSE"))
    from(file(".idea/icon.png")) {
        rename { "beddium.png" }
    }
}

repositories {
    exclusive(mavenpattern(), "com.falsepattern")
    exclusive(maven("taumcMirror", "https://mvn.falsepattern.com/taumc"), "org.embeddedt.celeritas")
    exclusive(mega(), "mega")
    exclusive(venmaven(), "com.ventooth")
}

dependencies {
    apiSplit("com.falsepattern:falsepatternlib-mc1.7.10:1.9.0")

    compileOnlyApi("org.joml:joml:1.10.8")
    compileOnlyApi("it.unimi.dsi:fastutil:8.5.16")

    compileOnlyApi("org.embeddedt.celeritas:celeritas-common:2.4.0-dev.3")
    shadowImplementation("org.embeddedt.celeritas:celeritas-common:2.4.0-dev.3") {
        excludeDeps()
    }

    compileOnly("mega:fluidlogged-mc1.7.10:0.1.2:api")

    devOnlyNonPublishable("com.ventooth:swansong-mc1.7.10:1.1.2:dev")
}