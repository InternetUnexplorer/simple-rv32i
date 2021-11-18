OUT_DIR ?= target
SOURCES ?= $(shell find src)

.PHONY: default prog clean

default: $(OUT_DIR)/TopLevel.bin

$(OUT_DIR)/TopLevel.v: $(SOURCES)
	sbt "runMain TopLevel --target-dir $(OUT_DIR)"

$(OUT_DIR)/TopLevel.json: $(OUT_DIR)/TopLevel.v
	yosys -q -p "synth_ice40 -top TopLevel -json $@" $<

$(OUT_DIR)/TopLevel.asc: $(OUT_DIR)/TopLevel.json pinmap.pcf
	nextpnr-ice40 -q --json $< --asc $@ --pcf pinmap.pcf --hx1k --package tq144

$(OUT_DIR)/TopLevel.bin: $(OUT_DIR)/TopLevel.asc
	icepack $< $@

prog: $(OUT_DIR)/TopLevel.bin
	iceprog $<

clean:
	rm -rf $(OUT_DIR)
