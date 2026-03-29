# Bước 1: Sử dụng hình ảnh Maven để build dự án
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
# Copy file cấu hình và tải dependencies trước để cache (cho nhanh)
COPY pom.xml .
RUN mvn dependency:go-offline
# Copy code và build ra file jar
COPY src ./src
RUN mvn clean package -DskipTests

# Bước 2: Sử dụng JRE gọn nhẹ để chạy ứng dụng
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# Copy file jar từ bước build sang
COPY --from=build /app/target/*.jar app.jar
# Mở cổng 8080
EXPOSE 8080
# Lệnh khởi chạy
ENTRYPOINT ["java", "-jar", "app.jar"]