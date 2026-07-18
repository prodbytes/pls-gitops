.PHONY: all native release image clean

all: native

native:
	./scripts/make.sh

release:
	./scripts/make-release.sh

image:
	./scripts/make-image.sh

clean:
	./scripts/make-clean.sh
