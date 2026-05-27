# --- Stage 1: Build ---
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package assembly:single -DskipTests

# --- Stage 2: Runtime ---
FROM eclipse-temurin:17-jre
WORKDIR /app

# Install Google Chrome for Selenium
RUN apt-get update && apt-get install -y wget gnupg libxss1 \
    fonts-ipafont-gothic fonts-wqy-zenhei fonts-thai-tlwg fonts-kacst fonts-freefont-ttf --no-install-recommends \
    && wget -q https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb \
    && apt-get install -y ./google-chrome-stable_current_amd64.deb \
    && rm google-chrome-stable_current_amd64.deb \
    && rm -rf /var/lib/apt/lists/*

# Copy the built JAR from Stage 1
COPY --from=build /app/target/PriceTrackerEngine.jar app.jar
COPY selectors.json .

# Hugging Face environment setup
ENV PORT=7860
EXPOSE 7860

# Command to run the API
ENTRYPOINT ["java", "-jar", "app.jar"]
