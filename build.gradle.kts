plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("kapt") version "1.5.31"
    id("com.palantir.docker-compose") version "0.25.0"
    id("com.bmuschko.docker-remote-api") version "6.4.0"
    jacoco
}


group = "br.com.sankhya.bh.extexemplo"
description="Descrição da sua Ext"


// Responsavel por inserir as informações do identificador do pipeline e da branch dentro do git lab no momento do deploy.


version = "1.${System.getenv("CI_PIPELINE_ID")}-${System.getenv("CI_COMMIT_REF_NAME")}"


val lughVersion = "6.6.58" // verificar dentro do gitlab a versão mais recente
val skwVersion = "4.16b113" // verificar no local xxxxxx


//aplica o Plugin da LugLlb responsável por criar facilitadores para desenvolvimento e manutenção de extensões
apply<br.com.lughconsultoria.SankhyaPlugin>()


//Configura as principais configurações utilizadas pela LugLib no servidor do docker.
configure<br.com.lughconsultoria.SankhyaPluginExtension> {
    sankhyaWUrl.set("http://localhost:8080/")
    sankhyaWUrlProducao.set("http://localhost:8080/")
    usuario.set("SUP")
    usuarioProducao.set("SUP")
    senha.set("tecsis")
    senhaProducao.set("tecsis")
    plataformaMinima.set("3.10")
    parceiroId.set(System.getenv("PARCEIRO_ID"))
    parceiroNome.set(System.getenv("PARCEIRO_SANKHYA"))
    moduloPadrao.set("")
    branch.set(System.getenv("CI_COMMIT_REF_NAME"))
    chave.set(System.getenv("CHAVE_SANKHYA"))
    modelDD.set("Lugh")
}




//No Buildscript e gerado um script com as configurações necessárias para se conectar ao repositório do Maven onde algumas dependências estão armazenadas.


buildscript{
    repositories{
        mavenCentral()
        maven {
            url = uri("http://sankhyatec.mgcloud.net.br/api/v4/projects/173/packages/maven")
            name = "GitLab"
            metadataSources {
                artifact()
                mavenPom()
            }
            credentials(HttpHeaderCredentials::class.java) {
                name = "Private-Token"
                value = "YzDkSQZrWVnXYzG1RMQN" //Solicitar ao Adamo Principe Meirelles Gomes
            }
            authentication {
                create<HttpHeaderAuthentication>("header")
            }
        }
    }
    dependencies {
        classpath("br.com.lughconsultoria:sankhyaw-gradle:4.0.42")
        classpath("com.fasterxml.jackson.core", "jackson-databind", "2.11.3")
        classpath("org.apache.directory.studio", "org.apache.commons.io", "2.1")

    }
}


repositories {
    mavenCentral()
    maven("https://repository.jboss.org/nexus/content/groups/publisc-jboss")
    maven("https://repository.jboss.org/nexus/content/repositories")
    maven("https://repository.jboss.org/nexus/content/repositories/thirdparty-releases")
    maven {
        url = uri("http://sankhyatec.mgcloud.net.br/api/v4/projects/173/packages/maven")
        name = "GitLab"
        metadataSources {
            artifact()
            mavenPom()
        }
        credentials(HttpHeaderCredentials::class.java) {
            name = "Private-Token"
            value = "YzDkSQZrWVnXYzG1RMQN" //Solicitar ao Adamo Principe Meirelles Gomes
        }
        authentication {
            create<HttpHeaderAuthentication>("header")
        }
    }
}
configurations.implementation.get().isCanBeResolved = true
dependencies {
    // Processado de anotações e geradores de código
    implementation("br.com.lughconsultoria", "lugh-lib", lughVersion)
    implementation("br.com.lughconsultoria", "lugh-lib-annotation", lughVersion)
    kapt("br.com.lughconsultoria", "lugh-lib-processor", lughVersion)
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.13.0")
    // Nativo Sankhya
    implementation("br.com.sankhya", "mge-modelcore", skwVersion)
    implementation("br.com.sankhya", "jape", skwVersion)
    implementation("br.com.sankhya", "mge-param", skwVersion)
    implementation("br.com.sankhya","skw-environment", skwVersion)
    implementation("br.com.sankhya","sanutil", skwVersion)
    implementation("br.com.sankhya", "cuckoo", skwVersion)

    // Status HTTP / Apoio as Servlets
    implementation("org.apache.httpcomponents", "httpclient", "4.0.1")
    // Manipulador de JSON
    implementation("com.google.code.gson", "gson", "2.1")
    // EJB / Escrever no container wildfly
    implementation("org.wildfly:wildfly-spec-api:16.0.0.Final")
    implementation("org.jdom", "jdom", "1.1.3")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.4.2")
    testImplementation("br.com.lughconsultoria", "lugh-lib-test", lughVersion)
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.0")
    testImplementation("org.assertj:assertj-core:3.11.1")
    testImplementation("br.com.lughconsultoria", "lugh-lib-test", lughVersion)
    // Obsoletas
    //implementation(kotlin("stdlib-jdk8"))
    //implementation("jdom", "jdom", "1.0")
    //implementation("org.beanshell", "bsh", "1.3.0")
    //implementation("org.apache.directory.studio", "org.apache.commons.io", "2.1")
}


tasks {
    kapt{
        useBuildCache = false
    }
    test {
        useJUnitPlatform()
        reports {
            junitXml.isEnabled = true
            html.isEnabled = false
        }
    }
    jacocoTestReport {
        reports {
            xml.isEnabled = true
            csv.isEnabled = false
            html.isEnabled = true
            // xml.destination = file("${buildDir}/test-results")
        }
    }
    test {
        useJUnitPlatform()
        reports {
            junitXml.isEnabled = true
            html.isEnabled = false
        }
    }
}

tasks.withType(com.palantir.gradle.docker.DockerComposeUp::class.java) {
    dependsOn("configureExtension")
}
dockerCompose {
    this.setDockerComposeFile("docker-compose.yml")
}


tasks.register("atualizarDicionario", com.bmuschko.gradle.docker.tasks.container.DockerExecContainer::class.java) {
    dependsOn("copyMetadados", "convertMetadados", "convertScripts")
    group = "sankhyaw"
    this.containerId.set("${System.getProperty("COMPOSE_PROJECT_NAME")}_wildfly_1")
    this.commands.add(arrayOf("installExtension","/home/sankhya/wildfly/standalone/deployments/sankhyaw.ear/projeto/"))
}


// Dependencia entre os modulos
sourceSets {
    val main by getting
    val web by getting {
        java {
            compileClasspath += main.output
        }
    }
}
