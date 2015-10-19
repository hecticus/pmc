import play.PlayJava
import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._


name := """PMC"""

version := "1.0-SNAPSHOT"


lazy val root = (project in file(".")).enablePlugins(PlayJava).aggregate(jobCore).dependsOn(jobCore)

lazy val jobCore = project.in(file("modules/JobCore")).enablePlugins(PlayJava)

scalaVersion := "2.10.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "com.rabbitmq" % "amqp-client" % "3.0.1",
  "mysql" % "mysql-connector-java" % "5.1.26",
  "net.vz.mongodb.jackson" %% "play-mongo-jackson-mapper" % "1.1.0",
  "org.apache.jclouds" % "jclouds-allblobstore" % "1.7.2",
  "com.google.guava" % "guava" % "15.0",
  "com.jolbox" % "bonecp-spring" % "0.8.0.RELEASE",
  "org.apache.jclouds.driver" % "jclouds-slf4j" % "1.7.2",
  "org.apache.jclouds.driver" % "jclouds-sshj" % "1.7.2",
  "org.apache.jclouds.provider" % "rackspace-cloudservers-us" % "1.7.2",
  "org.apache.jclouds.provider" % "cloudfiles-us" % "1.7.2",
  "org.apache.jclouds.provider" % "rackspace-cloudblockstorage-us" % "1.7.2",
  "org.apache.jclouds.provider" % "rackspace-clouddns-us" % "1.7.2",
  "org.apache.jclouds.provider" % "rackspace-cloudloadbalancers-us" % "1.7.2",
  "org.apache.jclouds.provider" % "rackspace-cloudservers-uk" % "1.7.2",
  "org.apache.jclouds.provider" % "cloudfiles-uk" % "1.7.2",
  "org.apache.jclouds.provider" % "rackspace-cloudblockstorage-uk" % "1.7.2",
  "org.apache.jclouds.provider" % "rackspace-clouddns-uk" % "1.7.2",
  "org.apache.jclouds.provider" % "rackspace-cloudloadbalancers-uk" % "1.7.2",
  "org.jclouds" % "jclouds-compute" % "1.5.0-alpha.6",
  "org.reflections" % "reflections" % "0.9.7.RC1",
  "javax.activation" % "activation" % "1.1",
  "javax.mail" % "mail" % "1.4.7",
  "com.sun.xml.messaging.saaj" % "saaj-impl" % "1.3",
  "com.sun.jersey" % "jersey-client" % "1.18",
  "com.sun.jersey" % "jersey-core" % "1.18",
  "com.sun.jersey.contribs" % "jersey-multipart" % "1.18",
  "net.jodah" % "lyra" % "0.4.1",
  "bouncycastle" % "bcprov-jdk15" % "140",
  "com.github.fernandospr" % "javapns-jdk16" % "2.2.1",
  "be.objectify"  %% "deadbolt-java"     % "2.3.0-RC1",
  "com.feth"      %% "play-authenticate" % "0.6.5-SNAPSHOT",
  "org.apache.commons" % "commons-io" % "1.3.2",
  "org.jvnet.mimepull" % "mimepull" % "1.9.4"
)

//resolvers += "Maven1 Repository" at "http://repo1.maven.org/maven2/net/vz/mongodb/jackson/play-mongo-jackson-mapper_2.10/1.1.0/"

resolvers ++= Seq(
    "Maven1 Repository" at "http://repo1.maven.org/maven2/net/vz/mongodb/jackson/play-mongo-jackson-mapper_2.10/1.1.0/",
    "Apache" at "http://repo1.maven.org/maven2/",
    "jBCrypt Repository" at "http://repo1.maven.org/maven2/org/",
    "play-easymail (release)" at "http://joscha.github.io/play-easymail/repo/releases/",
    "play-easymail (snapshot)" at "http://joscha.github.io/play-easymail/repo/snapshots/",
    Resolver.url("Objectify Play Repository", url("http://schaloner.github.io/releases/"))(Resolver.ivyStylePatterns),
    "play-authenticate (release)" at "http://joscha.github.io/play-authenticate/repo/releases/",
    "play-authenticate (snapshot)" at "http://joscha.github.io/play-authenticate/repo/snapshots/"
)