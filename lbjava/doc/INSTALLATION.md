# 6. Installation and Command Line Usage

## 6.1 Installation

LBJ is written entirely in Java - almost. The Java Native Interface (JNI) is utilized to interface
with the GNU Linear Programming Kit (GLPK) which is used to perform inference (see
Section 5.3.2), requiring a small amount of C to complete the connection. This C code must be
compiled as a library so that it can be dynamically linked to the JVM at run-time in any application
that uses inference. Thus, the GNU Autotools became a natural choice for LBJ’s build
system. More information on building and installing LBJ from its source code is presented below.
On the other hand, some users’ applications may not require LBJ’s automated inference
capabilities. In this case, installation is as easy as downloading two jar files from the Cognitive
Computation Group’s website1 and adding them to your `CLASSPATH` environment variable.

`LBJ2.jar` contains the classes implementing the LBJ compiler. `LBJ2Library.jar` contains the
library classes. If this is your chosen method of installation, you may safely skip to the section
on command line usage below.

Alternatively, the source code for both the compiler and the library can be downloaded from
the same web site. Download the file `lbj-2.x.x.tar.gz` and unpack it with the following command:

```
tar zxf lbj-2.x.x.tar.gz
```

The `lbj-2.x.x` directory is created, and all files in the package are placed in that directory.
Of particular interest is the file `configure`. This is a shell script designed to automatically
detect pertinent parameters of your system and to create a set of makefiles that builds LBJ with
respect to those parameters. In particular, this script will detect whether or not you have GLPK
installed. If you do, LBJ will be compiled with inference enabled. 
(GLPK is a separate software package that must be downloaded, compiled, and installed before LBJ is configured
in order for LBJ to make use of it.) 
The `configure` script itself was built automatically by the GNU Autotools, 
but you will not need them installed on your
system to make use of it.

By default, the `configure` script will create makefiles that intend to install LBJ’s JNI libraries
and headers in system directories such as `/usr/local/lib` and `/usr/local/include`. If
you have root privileges on your system, this will work just fine. Otherwise, it will be necessary
to use `configure`’s `--prefix` command line option. For example, running configure
with `--prefix=$HOME` will create makefiles that install LBJ’s libraries and headers in similarly
named subdirectories of your user account’s root directory, such as `~/lib` and `~/include`. The
configure script has many other options as well. Use --help on the command line for more
information.
If you choose to use the `--prefix` command line option, then it is a reasonable assumption
that you also used it when building and installing GLPK. In that case, the following environment
variables must be set before running LBJ’s `configure` script. `CPPFLAGS` is used to supply
command line parameters to the C preprocessor. We will use it to add the directory where
the GLPK headers were installed to the include path. `LDFLAGS` is used to supply command
line parameters to the linker. We will use it to add the directory where the GLPK library was
installed to the list of paths that the linker will search in. For example, in the `bash` shell:
```
export CPPFLAGS=-I$HOME/include
export LDFLAGS=-L$HOME/lib
```
or in `csh`:
```
setenv CPPFLAGS -I${HOME}/include
setenv LDFLAGS -L${HOME}/lib
```
The last step in making sure that inference will be enabled is to ensure that the file `jni.h`
is on the include path for the C preprocessor. This file comes with your JVM distribution. It is
often installed in a standard location already, but if it isn’t, we must set `CPPFLAGS` in such a way
that it adds all the paths we are interested in to the include path. For example, in the `bash` shell:
```
export JVMHOME=/usr/lib/jvm/java-6-sun
export CPPFLAGS="$CPPFLAGS -I$JVMHOME/include"
export CPPFLAGS="$CPPFLAGS -I$JVMHOME/include/linux"
```
or in `csh`:
```
setenv JVMHOME /usr/lib/jvm/java-6-sun
setenv CPPFLAGS "${CPPFLAGS} -I${JVMHOME}/include"
setenv CPPFLAGS "${CPPFLAGS} -I${JVMHOME}/include/linux"
```
At long last, we are ready to build and install LBJ with the following command:
```
./configure --prefix=$HOME && make && make install
```
If all goes well, you will see a message informing you that a library has been installed and that
certain extra steps may be necessary to ensure that this library can be used by other programs.
Follows these instructions. Also, remember to add the `lbj-2.x.x` directory to your `CLASSPATH`
environment variable.
LBJ’s makefile also contains rules for creating the jars that are separately downloadable from
the website and for creating the Javadoc documentation for both compiler and library. To create

the jars, simply type `make jars`. To create the Javadoc documentation, you must first set the
environment variable `LBJ2 DOC` equal to the directory in which you would like the documentation
created. Then type make doc.
Finally, users of the VIM editor may be interested in lbj.vim, the LBJ syntax highlighting
file provided in the tar ball. If you have not done so already, create a directory named `.vim`
in your home directory. In that directory, create a file named `filetype.vim` containing the
following text:
```
if exists("did_load_filetypes")
  finish
endif
augroup filetypedetect
  au! BufRead,BufNewFile *.lbj      setf lbj
augroup END
```
Then create the subdirectory `.vim/syntax` and place the provided `lbj.vim` file in that subdirectory.
Now, whenever VIM edits a file whose extension is `.lbj`, LBJ syntax highlighting will be
enabled.


## 6.2 Command Line Usage
The LBJ compiler is itself written in Java. It calls `javac` both to compile classes that its source
file depends on and to compile the code it generates. Its command line usage is as follows:
```
java LBJ2.Main [options] <source file>
```
where `[options]` is zero or more of the following:

By default, all files generated by LBJ will be created in the same directory in which the
LBJ source file is found. To place generated Java sources in a different directory, use the `-gsp`
(or `-generatedsourcepath`) command line option. The lexicon and example files described in
Section 4.1.2.6 are also placed in the directory specified by this option. In addition, the generated
sources’ class files will be created in that directory unless the `-d` command line option is also
specified. This option places all generated class files in the specified directory, just like javac’s
-d option. The “learning classifier” file with extension `.lc` (also discussed in Section 4.1.2.6)
will also be placed in the directory specified by the `-d` option. Another option similar to javac
is the `-sourcepath` option for specifying extra directories in which Java source files are found.
Both the `-d` and `-sourcepath` options should be given directly to LBJ if they are given at all.
Do not specify them inside LBJ’s `-j` option. Finally, LBJ does not offer a `-classpath` option.
Simply give this parameter to the JVM instead.
For example, say an employee of the XYZ company is building a new software package called
ABC with the help of LBJ. This is a large project, and compiling the LBJ source file will generate
many new Java sources. She places her LBJ source file in a new working directory along
side three new subdirectories: `src`, `class`, and `lbj`.
```
$ ls
abc.lbj src/ class/ lbj/
```
Next, since all the source files in the ABC application will be part of the `com.xyz.abc` package,
she creates the directory structure `com/xyz/abc` as a subdirectory of the src directory. Application
source files are then placed in the `src/com/xyz/abc` directory. Next, at the top of her LBJ
source file she writes the line `package com.xyz.abc;`. Now she is ready to run the following
commands:
```
$ java -cp $CLASSPATH:class LBJ2.Main -sourcepath src -gsp lbj -d class abc.lbj
. . .
$ javac -classpath $CLASSPATH:class -sourcepath lbj:src -d class \
src/com/xyz/abc/*.java
$ jar cvf abc.jar -C class com
```
The first command creates the `com/xyz/abc` directory structure in both of the lbj and class
directories. LBJ then generates new Java sources in the `lbj/com/xyz/abc` directory and class
files in the `class/com/xyz/abc` directory. Now that the necessary classifiers’ implementations
exist, the second command compiles the rest of the application. Finally, the last command prepares
a jar file containing the entire ABC application. Users of ABC need only add `abc.jar` to
their `CLASSPATH`.
There are two other JVM command line parameters that will be of particular interest to
programmers working with large datasets. Both increase the amount of memory that Java is
willing to utilize while running. The first is `-Xmx<size>` which sets the maximum Java heap size.
It should be set as high as possible, but not so high that it causes page-faults for the JVM or
for some other application on the same computer. This value must be a multiple of `1024` greater
than 2MB and can be specified in kilobytes (`K`, `k`)`, megabytes (`M`, `m`), or gigabytes (`G`, `g`).
The second is `-XX:MaxPermSize=<size>` which sets the maximum size of the permanent generation. 
This is a special area of the heap which stores, among other things, canonical representations
for the `String`s in a Java application. Since a learned classifier can contain many `String`s, it
may be necessary to set it higher than the default of 64 MB. For more information about the heap
and garbage collection, see [here](http://java.sun.com/docs/hotspot/gc5.0/gc) tuning 5.html.
With these two command line parameters, a typical LBJ compiler command line might look
like:
```
java -Xmx512m -XX:MaxPermSize=512m LBJ2.Main Test.lbj
```
When it is necessary to run the compiler with these JVM settings, it will also be necessary to
run the application that uses the generated classifiers with the same or larger settings.


