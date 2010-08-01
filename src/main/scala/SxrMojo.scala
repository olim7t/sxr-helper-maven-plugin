/* sxr-helper-maven-plugin
 * Copyright 2010 Olivier Michallat
 */
package com.github.olim7t

import java.io.{File, PrintStream, FileOutputStream}
import java.util.{List => JList}
import org.apache.maven.plugin._
import org.apache.maven.plugin.logging.Log
import org.apache.maven.project.MavenProject
import org.apache.maven.artifact.ArtifactUtils
import org.apache.maven.model.Dependency
import org.scala_tools.maven.mojo.annotations._
import scala.collection.JavaConversions._

@requiresProject
trait SxrMojo extends AbstractMojo {
  @parameter
  @expression("${project}")
  @readOnly
  var project : MavenProject = _;

  @parameter
  @expression("${project.build.directory}")
  var outputDirectory : File = _
  
  @parameter
  @expression("${project.collectedProjects}")
  var collectedProjects: JList[_] = _
  lazy val modules = for (m <- collectedProjects; module = m.asInstanceOf[MavenProject]) yield module

  var log: Log = _
  override def setLog(log: Log) { this.log = log }

  def hasScalaSources(p: MavenProject) = {
  	val scalaSourceDir = new File(p.getBuild.getSourceDirectory, "../scala")
  	scalaSourceDir.exists
  }

  def makeProjectId(p: MavenProject): String = ArtifactUtils.versionlessKey(p.getGroupId(), p.getArtifactId())
  
  def makeDependencyId(d: Dependency): String = ArtifactUtils.versionlessKey(d.getGroupId(), d.getArtifactId())

  def write(s: String, f: File) {
    val output = new PrintStream(new FileOutputStream(f))
    output.println(s)
    output.close()
  }
}
