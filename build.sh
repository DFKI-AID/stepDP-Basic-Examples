mvn clean install -f external/step-dp/src/pom.xml -am -pl spring
mvn package
java -Dde.dfki.step.dialog.MyDialog10 -jar target/mydialog.jar


echo "Finished. Press enter to coninue."
read