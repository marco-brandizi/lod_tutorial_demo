cd "$(dirname $0)"
mvn clean package

cd ..
unzip data2_to_rdf/target/xml2rdf.zip
