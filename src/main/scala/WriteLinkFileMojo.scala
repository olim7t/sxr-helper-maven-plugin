/* sxr-helper-maven-plugin
 * Copyright 2010 Olivier Michallat
 */
package com.github.olim7t

import java.io.{File, PrintStream, FileOutputStream}
import org.apache.maven.plugin._
import org.apache.maven.project.MavenProject
import org.apache.maven.plugin.reactor.SuperProjectSorter
import org.apache.maven.artifact.ArtifactUtils
import org.scala_tools.maven.mojo.annotations._
import scala.collection.JavaConversions._
import org.codehaus.plexus.util.dag.DAG

/**
 * For each module of the project, generate an sxr link file pointing to the modules it depends on.
 * This will enable cross-module when generating the Vim data files.
 *
 * Note: all the work is currently done at the parent project level, which means that the plugin
 * must not be inherited. This also caused a problem when the link file was generated in the target
 * directory and 'clean generate-sources' was executed, as the parent project would generate the
 * files and the 'clean' of each module would delete them. I've avoided this by not generating in
 * target.
 * A better approach would be to have each module handle its link file itself.
 */
@goal("write-link-file")
@phase("generate-sources")
class WriteLinkFileMojo extends AbstractMojo with SxrMojo {

  @throws(classOf[MojoExecutionException])
  override def execute() {
  	val sorter = new SuperProjectSorter(collectedProjects)
  	val dag = sorter.getDAG()

  	for (module <- modules) writeLinkFile(sxrIndex(module), moduleDependencies(module, dag))

    // Writes a global link file in the root project, to be used by external projects
    log.info(sxrIndex(project).getAbsolutePath)
    writeLinkFile(sxrIndex(project), modules)
  }

  private def sxrDir(module: MavenProject) = new File(module.getBuild.getOutputDirectory + ".sxr")

  private def sxrIndex(module: MavenProject) = new File(module.getBasedir, "sxr.links")

  // Finds which modules a module depends on 
  private def moduleDependencies(module: MavenProject, dag: DAG): Seq[MavenProject] = {
    val projectId = makeProjectId(module)
    require(dag.getVertex(projectId) != null)
    val depIds = dag.getChildLabels(projectId)
    for {
      i <- depIds ; depId = i.asInstanceOf[String]
      dep <- modules.find(makeProjectId(_) == depId)
      if hasScalaSources(dep)
    } yield dep
  }

  private def writeLinkFile(file: File, dependencies: Seq[MavenProject]) {
  	val linkList = dependencies.map(sxrDir(_).toURI + "/").mkString("\n")
  	write(linkList, file)
  }
}
