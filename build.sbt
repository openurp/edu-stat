import org.openurp.parent.Settings._
import org.openurp.parent.Dependencies._
import org.beangle.tools.sbt.Sas

ThisBuild / organization := "org.openurp.edu.stat"
ThisBuild / version := "0.0.1-SNAPSHOT"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/openurp/edu-stat"),
    "scm:git@github.com:openurp/edu-stat.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id    = "chaostone",
    name  = "Tihua Duan",
    email = "duantihua@gmail.com",
    url   = url("http://github.com/duantihua")
  )
)

ThisBuild / description := "OpenURP Edu Stat"
ThisBuild / homepage := Some(url("http://openurp.github.io/edu-stat/index.html"))

val apiVer = "0.23.4"
val starterVer = "0.0.14"
val baseVer = "0.1.23"
val openurp_edu_api = "org.openurp.edu" % "openurp-edu-api" % apiVer
val openurp_stater_web = "org.openurp.starter" % "openurp-starter-web" % starterVer
val openurp_base_tag = "org.openurp.base" % "openurp-base-tag" % baseVer

lazy val root = (project in file("."))
  .enablePlugins(WarPlugin)
  .settings(
    name := "openurp-edu-stat-webapp",
    common,
    libraryDependencies ++= Seq(openurp_stater_web,openurp_base_tag),
    libraryDependencies ++= Seq(openurp_edu_api,beangle_ems_app),
    libraryDependencies ++= Seq(Sas.Tomcat % "test")
  )
