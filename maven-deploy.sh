# Use this command to circumvent the problem with using mvn:deploy.
# caused by the generation of the extra source files (parser, sym, Yylex, SymbolNames).

VERSION=`mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | grep -Ev '(INFO|WARNING)'`
mvn install
mvn deploy:deploy-file -Durl=scp://bilbo.cs.uiuc.edu:/mounts/bilbo/disks/0/www/cogcomp/html/m2repo -DrepositoryId=CogcompSoftware -Dfile=target/LBJava-${VERSION}.jar -Dsources=target/LBJava-${VERSION}-sources.jar -DpomFile=pom.xml

