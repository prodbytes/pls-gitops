.PHONY: all native clean

all: native

native:
	./scripts/make.sh

clean:
	./scripts/make-clean.sh
