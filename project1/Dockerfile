FROM openjdk:8
RUN apt-get update
RUN apt-get install -y git
COPY ./target/project1-1.0-SNAPSHOT /usr/src/project1
WORKDIR /usr/src/project1
RUN git clone https://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@github.com/ADSDummyUser/Knowledge_Base.git knowledge_base
CMD ["java","-jar","project1-1.0-SNAPSHOT.jar"]