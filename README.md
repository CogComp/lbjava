# Learning Based Java 

[![Build Status](https://semaphoreci.com/api/v1/projects/02a1d3da-4dc5-41c0-963c-b5605e4abc67/605145/badge.svg)](https://semaphoreci.com/danyaljj/lbjava)

[![Build Status](https://travis-ci.com/IllinoisCogComp/lbjava.svg?token=Mq5qBcoy2RUfPTEqyxUb)](https://travis-ci.com/IllinoisCogComp/lbjava)

- [LBJava core](lbjava/README.md) 
- [LBJava examples](lbjava-examples/README.md) 
- [LBJava maven plugin](lbjava-mvn-plugin/README.md)

## Compiling the whole package 
Try the following steps: 
 - Build the lbjava core: `mvn -pl lbjava package -Djar.finalName=lbjavaCore`
 - Generate java files from lbjava definitions: `cd lbjava-examples; sh compileLBJ.sh; cd ..`
 - Test the whole project: `mvn test`
 
## External Links 
[Here](http://cogcomp.cs.illinois.edu/page/software_view/LBJ) is the homepage of LBJ.  
If you have any questions visit [here](http://cogcomp.cs.illinois.edu/).  


## Licensing
To see the full license for this software, see [LICENSE](LICENSE) or visit the download 
page for this software and press 'Download'. The next screen displays the license. 

