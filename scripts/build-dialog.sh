mvn clean install -f ../external/step-dp/src/pom.xml -am -pl spring
mvn clean package -f ../pom.xml

echo "Finished. Press enter to coninue."
read