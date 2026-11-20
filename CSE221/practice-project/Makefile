# Java Project Makefile

SRC_DIR := src
BIN_DIR := bin
MAIN_CLASS := com.study.app.Main

# Automatically find all .java files under src/ (handles all packages & subpackages)
SRCS := $(shell find $(SRC_DIR) -name "*.java")

.PHONY: all build run clean

all: build

build: $(BIN_DIR)/.build

$(BIN_DIR)/.build: $(SRCS)
	@mkdir -p $(BIN_DIR)
	javac -d $(BIN_DIR) -sourcepath $(SRC_DIR) $(SRCS)
	@touch $(BIN_DIR)/.build

run: build
	java -cp $(BIN_DIR) $(MAIN_CLASS)

clean:
	rm -rf $(BIN_DIR)
