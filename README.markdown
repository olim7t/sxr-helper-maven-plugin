A Maven plugin providing [sxr](http://github.com/harrah/browse) support for multi-module projects.

Sample output: [LiftWeb 2.0 HTML cross-reference](http://olim7t.github.com/liftweb-2.0-release-sxr/)

# Background

sxr is a Scala compiler plugin written by Mark Harrah. It generates an HTML cross-reference of Scala source files, which displays symbol types and links references to declarations.

I contributed extensions to provide the same functionality in the [Vim](http://www.vim.org/) text editor (an alternate output format used by a [Vim plugin](http://github.com/olim7t/scala_sxr_vim)).

This project helps running sxr on Maven multi-module projects:

* for the HTML format: all the sources are compiled together in a single compiler run, to get a compound HTML report ;

* for the Vim format: each module is compiled on its own, but it links to the modules it depends on. The idea is to have a local copy of the project that you can navigate (and also link from your own projects).

# Installing

## Installing scala-mojo-support

This project is a Maven plugin, written in Scala thanks to Josh Suereth's [maven-mojo-support](http://github.com/jsuereth/scala-mojo-support). 

It requires version 0.3, which has not yet reached its release version. You'll need to clone the maven-mojo-support repo and build it locally by running `mvn install` (make sure you use Maven 2.2.1 or higher).

## Building this project

Same thing: clone the repo, run `mvn install`.

## Building sxr

Again, the required version (0.2.7) is not yet released. Clone the sxr repo and run [sbt](http://code.google.com/p/simple-build-tool/) in its root directory. From the sbt prompt, run `update` and then `++<version> package`, where `<version>` is the Scala version you use to build the target project (for instance: `++2.7.7 package` in the case of Lift 2.0).

This will generate a jar in the `target` directory (in a subdirectory named after the Scala version you specified).

## Installing the Vim plugin

See the instructions [here](http://github.com/olim7t/scala_sxr_vim/blob/master/INSTALL.markdown). Note: for now, use [the head version](http://github.com/olim7t/scala_sxr_vim/blob/master/vim/ftplugin/scala_sxr.vim), not a pre-packaged distribution.

# Using

I've included a sample Maven POM in the `samples` directory (this is the POM I used for LiftWeb's `framework` module). The changes are marked with comments saying `begin sxr changes`. 

The path of the sxr jar is hard-coded, you'll need to adapt it (it must be absolute).

## HTML report

Run `mvn generate-sources` from the parent module, then cd to `target/compound`. It will contain a POM to compile all your Scala sources together. Again, you'll need to adapt the sxr jar path in this POM (I should really make this one configurable...)

Run `mvn compile` from this same directory, the HTML report will get generated in `target/classes.sxr`.

## Vim plugin

Run `mvn compile` from the parent module. This will generate the index files for Vim in each module's `target/classes.sxr` directory. Open a Scala source from Vim, the plugin should auto-detect `classes.sxr` and provide the features.

