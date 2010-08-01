A Maven plugin providing [sxr](http://github.com/harrah/browse) support for multi-module projects.

# Background

sxr is a Scala compiler plugin written by Mark Harrah. It generates an HTML cross-reference of Scala source files, which displays symbol types and links references to declarations.

I contributed extensions to provide the same functionality in the [Vim](http://www.vim.org/) text editor (an alternate output format used by a [Vim plugin](http://github.com/olim7t/scala_sxr_vim)).

This project helps running sxr on Maven multi-module projects:

* for the HTML format: all the sources are compiled together in a single compiler run, to get a single HTML report ;

* for the Vim format: each module is compiled on its own, but it links to the modules it depends on. The idea is to have a local copy of the project that you can navigate (and also link from your own project).

I wrote this plugin primarily for the [Lift](http://liftweb.net/) web framework. The HTML report for the 2.0 release of Lift is available [here](http://olim7t.github.com/liftweb-2.0-release-sxr/).

# Installing

## Installing scala-mojo-support

This project is a Maven plugin, written in Scala thanks to Josh Suereth's [maven-mojo-support](http://github.com/jsuereth/scala-mojo-support). 

It requires version 0.3, which has not yet reached its release version. So you'll need to clone maven-mojo-support and build it locally by running `mvn install` (make sure you use Maven 2.2.1 or higher).

## Building the project

Same thing: clone the project, run `mvn install`.

## Building sxr

Again, the required version (0.2.7) is not yet released. Clone the sxr repo, install [sbt](http://code.google.com/p/simple-build-tool/) if necessary, and run sbt. From the sbt prompt, run `update` and then `++<version> package`, where `<version>` is the Scala version you use to build the target project (for instance, for Lift 2.0: `++2.7.7 package`).

This will generate an sxr jar in the `target` directory (in a subdirectory named after the Scala version you specified).

## Installing the Vim plugin

See the instructions [here](http://github.com/olim7t/scala_sxr_vim/blob/master/INSTALL.markdown). Note: the 1.0.0 release won't work; for now, use [the head version](http://github.com/olim7t/scala_sxr_vim/blob/master/vim/ftplugin/scala_sxr.vim).

# Using

I've included a sample Maven POM in the `samples` directory (this is the POM I used for LiftWeb's `framework` module). The changes are marked with `begin sxr changes` comments. 

The path of the sxr jar is hard-coded, you'll need to adapt it (it must be absolute).

## HTML report

Run `mvn generate-sources` from the parent module, then cd to `target/compound`. It will contain a POM to compile all your Scala sources together. Again, you'll need to adapt the sxr jar path in this POM (I should really make this one configurable...)

Run `mvn compile` from this same directory, the HTML report will be generated in `target/classes.sxr`.

## Vim plugin

Run `mvn compile` from the parent module. This will generate the index files for Vim in each module's `target/classes.sxr` directory. Open a Scala source from Vim, the plugin should auto-detect `classes.sxr` and provide the features.

