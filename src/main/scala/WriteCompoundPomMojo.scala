/* sxr-helper-maven-plugin
 * Copyright 2010 Olivier Michallat
 */
package com.github.olim7t

import java.io.File

import org.apache.maven.plugin._
import org.apache.maven.model._
import org.apache.maven.project.MavenProject

import org.scala_tools.maven.mojo.annotations._
import scala.xml.Node

import scala.collection.JavaConversions._

/**
 * Writes a POM for a "compound project" that will process the sources of all the modules in a single compiler run, allowing sxr to
 * generate a single directory, cross-module linked HTML report.
 */
@goal("write-compound-pom")
@phase("generate-sources")
class WriteCompoundPomMojo extends AbstractMojo with SxrMojo {

  @parameter
  @expression("${project.build.directory}/compound/pom.xml")
  var outputFile: File = _

  @throws(classOf[MojoExecutionException])
  override def execute() {
  	outputFile.getParentFile.mkdirs

  	val scalaPlugin = project.getBuild.getPlugins.map(_.asInstanceOf[Plugin]).find(p => p.getArtifactId == "maven-scala-plugin").get
  	
  	val compoundPom =
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>{project.getGroupId}</groupId>
  <artifactId>{project.getArtifactId}-compound</artifactId>
  <version>{project.getVersion}</version>
  <packaging>jar</packaging>
  <dependencies>
  {for (d <- allDependencies ; if (d.getScope != "test")) yield
  	<dependency>
  	  <groupId>{d.getGroupId}</groupId>
  	  <artifactId>{d.getArtifactId}</artifactId>
  	  <version>{d.getVersion}</version>
  	</dependency>
  }
  </dependencies>
  <build>
    <plugins>
      <plugin>
        <groupId>org.scala-tools</groupId>
        <artifactId>maven-scala-plugin</artifactId>
        <version>{scalaPlugin.getVersion}</version>
        <configuration>
          <jvmArgs>
            <jvmArg>-Xmx1024m</jvmArg>
          </jvmArgs>
          <args>
            <arg>-Xplugin:/Users/oli/projects/sxr/src/browse/target/scala_2.7.7/sxr_2.7.7-0.2.7-SNAPSHOT.jar</arg>
            <arg>-P:sxr:base-directory:{allSourceDirsAsString}</arg>
            <arg>-P:sxr:output-formats:html</arg>
            <!-- Stop compilation right after sxr has executed -->
            <arg>-Ystop:superaccessors</arg>
          </args>
        </configuration>
        <executions>
          <execution>
            <goals><goal>compile</goal></goals>
          </execution>
        </executions>
      </plugin>
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>build-helper-maven-plugin</artifactId>
        <version>1.5</version>
        <executions>
          <execution>
            <id>add-source</id>
            <phase>generate-sources</phase>
            <goals><goal>add-source</goal></goals>
            <configuration>
              <sources>
              {for (s <- allSourceDirs) yield
                <source>{s}</source>
              }
              </sources>
            </configuration>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
</project>
  	  
  	write(compoundPom, outputFile)
  }

  private def allSourceDirs = modules.
    filter(hasScalaSources(_)).
    map(m => m.getBuild.getSourceDirectory)
  
  private def allSourceDirsAsString = allSourceDirs.mkString(File.pathSeparator)

  private def allDependencies = {
    val moduleIds = Set(modules.map(makeProjectId(_)) : _*)
    val nonModuleDeps = for {
    	m <- modules
      o <- m.getDependencies
      d = o.asInstanceOf[Dependency]
      depId = makeDependencyId(d)
      if (! moduleIds.contains(depId))
    } yield d
    // Remove duplicates:
    Set(nonModuleDeps : _*)
  }

  private def write(n: Node, targetFile: File) {
  	write(n.toString, targetFile)
  }
}
