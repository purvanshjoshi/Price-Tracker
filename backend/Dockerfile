# --- Stage 1: Build ---
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package assembly:single -DskipTests

# --- Stage 2: Runtime ---
FROM eclipse-temurin:17-jre
WORKDIR /app

# Install Google Chrome for Selenium
RUN apt-get update && apt-get install -y wget gnupg \
    && wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable fonts-ipafont-gothic fonts-wqy-zenhei fonts-thai-tlwg fonts-kacst fonts-freefont-ttf libxss1 \
      --no-install-recommends \
    && rm -rf /var/lib/apt/lists/*

# Copy the built JAR from Stage 1
COPY --from=build /app/target/PriceTrackerEngine.jar app.jar
COPY selectors.json .

# Hugging Face environment setup
ENV PORT=7860
EXPOSE 7860

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:7860/api/health || exit 1

# Command to run the API
ENTRYPOINT ["java", "-jar", "app.jar"]
