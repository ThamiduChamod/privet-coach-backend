# ===============================
# Stage 1 - Build Spring Boot (Maven Project)
# ===============================
FROM eclipse-temurin:17-jdk AS builder

# Working directory
WORKDIR /app

# Copy Maven wrapper + pom.xml first (better caching)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Give execute permission
RUN chmod +x mvnw

# Download dependencies first
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src src

# Build jar file
RUN ./mvnw clean package -DskipTests


# ===============================
# Stage 2 - Run Application
# ===============================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy built jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Render provides PORT automatically
EXPOSE 8080

# Run Spring Boot app
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080}"]
