name := "foursquare-fhttp"

version := "0.1.4"

organization := "com.foursquare"

crossScalaVersions := Seq("2.9.1", "2.8.1")

libraryDependencies <++= (scalaVersion) { scalaVersion =>
  val specsVersion = scalaVersion match {
    case "2.9.1" => "1.6.9"
    case _       => "1.6.8"
  }
  val finagleSuffix = scalaVersion match {
    case "2.9.1" => "_" + scalaVersion
    case _ => ""
  }
  Seq(
    "com.twitter"                   %  ("finagle" + finagleSuffix)      % "1.9.12" intransitive(),
    "com.twitter"                   %  ("finagle-core" + finagleSuffix) % "1.9.12",
    "com.twitter"                   %  ("finagle-http" + finagleSuffix) % "1.9.12", 
    "commons-httpclient"            %  "commons-httpclient"             % "3.1",
    "junit"                         %  "junit"                          % "4.5"        % "test",
    "com.novocode"                  %  "junit-interface"                % "0.6"        % "test",
    "org.scala-tools.testing"       %% "specs"                          % specsVersion % "test"
  )
}

publishTo <<= (version) { v =>
  val nexus = "http://nexus.scala-tools.org/content/repositories/"
  if (v.endsWith("-SNAPSHOT"))
    Some("snapshots" at nexus+"snapshots/")
  else
    Some("releases" at nexus+"releases/")
}

resolvers += "twitter mvn" at "http://maven.twttr.com"

scalacOptions ++= Seq("-deprecation", "-unchecked")

testFrameworks += new TestFramework("com.novocode.junit.JUnitFrameworkNoMarker")

credentials ++= {
  val scalaTools = ("Sonatype Nexus Repository Manager", "nexus.scala-tools.org")
  def loadMavenCredentials(file: java.io.File) : Seq[Credentials] = {
    xml.XML.loadFile(file) \ "servers" \ "server" map (s => {
      val host = (s \ "id").text
      val realm = if (host == scalaTools._2) scalaTools._1 else "Unknown"
      Credentials(realm, host, (s \ "username").text, (s \ "password").text)
    })
  }
  val ivyCredentials   = Path.userHome / ".ivy2" / ".credentials"
  val mavenCredentials = Path.userHome / ".m2"   / "settings.xml"
  (ivyCredentials.asFile, mavenCredentials.asFile) match {
    case (ivy, _) if ivy.canRead => Credentials(ivy) :: Nil
    case (_, mvn) if mvn.canRead => loadMavenCredentials(mvn)
    case _ => Nil
  }
}
