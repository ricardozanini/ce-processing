FROM adoptopenjdk:11-jre-hotspot

RUN mkdir /opt/app
COPY target/ce-processing.jar /opt/app
CMD ["java", "-jar", "/opt/app/ce-processing.jar"]