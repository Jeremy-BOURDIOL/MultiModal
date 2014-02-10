SHELL = /bin/sh

ICAR_JAR = icar-1.0.jar
IVY_JAR = ivy-java-1.2.6.jar

BIN_PATH = bin
SRC_PATH = src
DOC_PATH = javadoc

default: 
	rm -rf $(BIN_PATH)
	mkdir $(BIN_PATH)
	javac -sourcepath $(SRC_PATH) -classpath $(ICAR_JAR) -d $(BIN_PATH) $(SRC_PATH)/IcarClient.java 
	javac -sourcepath $(SRC_PATH) -classpath $(ICAR_JAR) -d $(BIN_PATH) $(SRC_PATH)/IcarAdmin.java
	javac -sourcepath $(SRC_PATH) -classpath "$(ICAR_JAR);$(IVY_JAR)" -d $(BIN_PATH) $(SRC_PATH)/IcarClientIvy.java

clean:
	rm -rf $(BIN_PATH)
	rm -rf $(DOC_PATH)

doc:
	rm -rf $(DOC_PATH)
	javadoc -classpath "$(ICAR_JAR);$(IVY_JAR)"  -d $(DOC_PATH) -author -use  -sourcepath $(SRC_PATH) $(SRC_PATH)/*.java  

rundemo:
	java -classpath "$(BIN_PATH);$(ICAR_JAR);$(IVY_JAR)"  IcarClientIvy dictionnaires/dictionnaire_graffiti.dat

runadmin:
	java -classpath "$(BIN_PATH);$(ICAR_JAR)" IcarAdmin

runclient:
	java -classpath "$(BIN_PATH);$(ICAR_JAR)" IcarClient dictionnaires/dictionnaire_graffiti.dat
