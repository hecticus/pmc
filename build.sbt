import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._


name := """PMC"""

version := "1.0-SNAPSHOT"


lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.10.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "com.typesafe.akka" %% "akka-actor" % "2.3.3",
  "com.typesafe.akka" %% "akka-contrib" % "2.3.3",
  "com.typesafe.akka" %% "akka-remote" % "2.3.4",
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
  "com.sun.jersey" % "jersey-client" % "1.8",
  "com.sun.jersey" % "jersey-core" % "1.8",
  "com.sun.jersey.contribs" % "jersey-multipart" % "1.8",
  "net.jodah" % "lyra" % "0.4.1",
  "bouncycastle" % "bcprov-jdk15" % "140",
  "com.github.fernandospr" % "javapns-jdk16" % "2.2.1"
)

resolvers += "Maven1 Repository" at "http://repo1.maven.org/maven2/net/vz/mongodb/jackson/play-mongo-jackson-mapper_2.10/1.1.0/"