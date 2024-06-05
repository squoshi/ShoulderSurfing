plugins {
	id("java")
	id("idea")
	alias(libs.plugins.forgegradle)
	alias(libs.plugins.mixingradle)
	alias(libs.plugins.modpublishplugin)
}

repositories {
	maven("https://maven.minecraftforge.net/")
}

val modId: String by project
val modName: String by project
val modAuthor: String by project
val modVersion: String by project
val modDescription: String by project
val modUrl: String by project
val javaVersion: String by project
val forgeCompatibleMinecraftVersions: String by project
val jarName: String by project
val curseProjectId: String by project
val modrinthProjectId: String by project

base {
	archivesName.set("$jarName-Forge")
}

java {
	sourceCompatibility = JavaVersion.toVersion(javaVersion)
	targetCompatibility = JavaVersion.toVersion(javaVersion)
}

mixin {
	add(sourceSets.main.get(), "$modId.refmap.json")
	
	config("$modId.api.mixins.json")
	config("$modId.common.mixins.json")
	config("$modId.common.compat.mixins.json")
}

minecraft {
	mappings("official", libs.versions.minecraft.get())
	
	copyIdeResources = true
	
	runs {
		configureEach {
			workingDirectory(project.file("../run"))
			ideaModule("${rootProject.name}.${project.name}.main")
			
			mods {
				create(modId) {
					source(sourceSets.main.get())
					source(project(":api").sourceSets.main.get())
					source(project(":common").sourceSets.main.get())
				}
			}
		}
		
		create("client")
		
		create("server") {
			args("--nogui")
		}
	}
}

dependencies {
	compileOnly(project(":api"))
	compileOnly(project(":common"))
	
	minecraft(libs.minecraft.forge)
	annotationProcessor("org.spongepowered:mixin:${libs.versions.mixin.get()}:processor")
	implementation(libs.jetbrains.annotations)
}

tasks.named<JavaCompile>("compileJava").configure {
	source(project(":api").sourceSets.main.get().allSource)
	source(project(":common").sourceSets.main.get().allSource)
}

tasks.named<ProcessResources>("processResources").configure {
	from(project(":api").sourceSets.main.get().resources)
	from(project(":common").sourceSets.main.get().resources)
	
	val properties = mapOf(
		"modVersion" to modVersion,
		"modId" to modId,
		"modName" to modName,
		"modAuthor" to modAuthor,
		"modDescription" to modDescription,
		"modUrl" to modUrl,
		"minecraftVersion" to libs.versions.minecraft.get()
	)
	
	inputs.properties(properties)
	
	filesMatching(listOf("pack.mcmeta", "META-INF/mods.toml")) {
		expand(properties)
	}
}

tasks.register<Jar>("apiJar").configure {
	from(project(":api").sourceSets.main.get().output)
	from(project(":api").sourceSets.main.get().allSource)
	archiveClassifier = "API"
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.build {
	finalizedBy("apiJar")
}

tasks.jar {
	finalizedBy("reobfJar")
}

tasks.configureEach {
	when(name) {
		"configureReobfTaskForReobfJar" -> mustRunAfter(tasks.jar)
		"configureReobfTaskForReobfJarJar" -> mustRunAfter(tasks.jarJar)
	}
}

publishMods {
	displayName = "$jarName-Forge-${libs.versions.minecraft.get()}-$modVersion"
	file = tasks.named<Jar>("jar").get().archiveFile
	additionalFiles.from(tasks.named("apiJar").get())
	changelog = provider { file("../changelog.txt").readText() }
	modLoaders.add("forge")
	type = STABLE
	
	val compatibleVersions = forgeCompatibleMinecraftVersions.split(",")
	
	curseforge {
		projectId = curseProjectId
		accessToken = findProperty("curse_api_key").toString()
		minecraftVersions.set(compatibleVersions)
		javaVersions.add(JavaVersion.toVersion(javaVersion))
		clientRequired = true
		serverRequired = false
		incompatible("better-third-person", "nimble", "valkyrien-skies")
	}
	
	modrinth {
		projectId = modrinthProjectId
		accessToken = findProperty("modrinth_api_key").toString()
		minecraftVersions.set(compatibleVersions)
		incompatible("better-third-person", "nimble", "valkyrien-skies")
	}
}
