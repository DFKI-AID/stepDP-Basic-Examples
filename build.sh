mvn clean install -f external/step-dp/src/pom.xml
mvn package
java -Dde.dfki.step.dialog.MyDialog -jar target/mydialog.jar

echo "Finished. Press enter to coninue."
read