.PHONY: build test run clean docker-build docker-run help

help:
	@echo "Usage: make <target>"
	@echo ""
	@echo "Targets:"
	@echo "  build        Build the backend JAR"
	@echo "  test         Run backend tests"
	@echo "  run          Run the backend API server locally"
	@echo "  clean        Clean build artifacts"
	@echo "  docker-build Build the Docker image"
	@echo "  docker-run   Run the Docker container locally"

build:
	cd backend && mvn -B clean package assembly:single -DskipTests

test:
	cd backend && mvn -B test

run:
	cd backend && mvn -B exec:java -Dexec.mainClass="com.pricetracker.Main"

clean:
	cd backend && mvn clean
	rm -rf backend/target

docker-build:
	docker build -t price-scout-engine backend

docker-run:
	docker run -p 7860:7860 price-scout-engine
