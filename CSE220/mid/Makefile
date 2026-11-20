JAVAC = javac
JAVA = java
SRC_DIR = src
BIN_DIR = bin
MAIN_CLASS = com.study.app.Main

SRCS = $(shell find $(SRC_DIR) -name "*.java")

all: build

build:
	@mkdir -p $(BIN_DIR)
	$(JAVAC) -d $(BIN_DIR) -sourcepath $(SRC_DIR) $(SRCS)

run: build
	$(JAVA) -cp $(BIN_DIR) $(MAIN_CLASS)

clean:
	rm -rf $(BIN_DIR)

.PHONY: all build run clean
