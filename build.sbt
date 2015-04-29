name := "jvmmetricsws"

version := "1.0"

lazy val `jvmmetricsws` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"


val hazelcast = ModuleID("com.hazelcast", "hazelcast-all", "2.0.1", None)

libraryDependencies ++= Seq( jdbc , anorm , cache , ws, hazelcast)



unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  