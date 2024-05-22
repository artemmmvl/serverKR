FROM openjdk
COPY build/libs/auth_rscir7-0.0.1-SNAPSHOT.jar auth_rscir7-0.0.1-SNAPSHOT.jar
CMD ["java", "-jar","auth_rscir7-0.0.1-SNAPSHOT.jar"]