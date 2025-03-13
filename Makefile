.PHONY := build


CS3380A3Q3.class: CS3380A3Q3.java 
	javac CS3380A3Q3.java

build: CS3380A3Q3.class 

run: build
	java -cp .:sqlite-jdbc-3.39.3.0.jar CS3380A3Q3
