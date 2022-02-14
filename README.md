# owl2lpg-translator
A program to translate OWL 2 Ontologies to LPG graphs

# Instructions
## Clone and compile
git clone https://github.com/protegeproject/owl2lpg-translator.git
git checkout develop
mvn clean install
cd owl2lpg-translation-cli
mvn clean package

## Running the translator
cd target
unzip owl2lpg-translation-cli-1.0-SNAPSHOT-bin.zip
cd owl2lpg-translation-cli-1.0-SNAPSHOT
./run.sh translate -f csv -o "/output/directory" "/path/to/my/ontology.obo"
