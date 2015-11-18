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
