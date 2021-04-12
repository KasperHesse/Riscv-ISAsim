TESTNAME :=

.PHONY : all
all: comp

comp: src/app/App.java
	javac src/app/*.java -d out

run: comp
	java -cp out app.App

jar:
	javac src/app/*.java -d out
	jar cfm App.jar Manifest.MF -C out app

test: comp
	javac -cp lib/junit-4.13.jar:out:. src/test/$(TESTNAME).java -d out
	java -cp lib/junit-4.13.jar:lib/hamcrest-core-1.3.jar:out:. org.junit.runner.JUnitCore test.$(TESTNAME)

clean:
	rm -rf out
	rm -f App.jar
	rm -f regdump.res