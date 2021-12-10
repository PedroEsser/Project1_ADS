FROM openjdk:8
RUN apt-get update
RUN apt-get install -y maven
RUN apt-get install -y git
COPY . /usr/src/project1
WORKDIR /usr/src/project1
RUN git clone https://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@github.com/ADSDummyUser/Knowledge_Base.git knowledge_base
RUN mvn package
CMD ["java","-jar","target/project1-1.0-SNAPSHOT/project1-1.0-SNAPSHOT.jar"]