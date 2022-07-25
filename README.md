# KumuluzEE NATS JetStream sample

> Develop REST services that produces and consumes messages using the NATS JetStream connective technology

The objective of this sample is to show how to produce and consume NATS JetStream messages with a simple REST service using KumuluzEE NATS JetStream extension.
The tutorial will guide you through all the necessary steps.
You will add KumuluzEE dependencies into pom.xml.
You will develop two simple REST services, which use KumuluzEE NATS JetStream extension for producing messages,
a few simple annotated methods, which use the KumuluzEE NATS JetStream extension for consuming messages pushed from the server
and a few methods that use the KumuluzEE NATS JetStream's injected subscriptions for pulling messages manualy from the server.

Required knowledge: basic familiarity with JAX-RS and REST; basic familiarity with NATS JetStream.

## Requirements

In order to run this example you will need the following:

1. Java 8 (or newer), you can use any implementation:
   * If you have installed Java, you can check the version by typing the following in a command line:

       ```
       java -version
       ```

2. Maven 3.2.1 (or newer):
   * If you have installed Maven, you can check the version by typing the following in a command line:

       ```
       mvn -version
       ```
3. Git:
   * If you have installed Git, you can check the version by typing the following in a command line:

       ```
       git --version
       ```

## Prerequisites

To run this sample you will need to start a few instances of JetStream enabled NATS servers.
You can do this easily with Docker Compose using the configuration file we provided in the `util/run-nats-server/` folder.

Simply run:
```
docker-compose -f util/run-nats-server/run-nats-servers.yaml up
```

## Usage

This example uses Docker Compose to set up NATS servers and maven to build and run the microservice.

1. Start the NATS server instances:
```
docker-compose -f util/run-nats-server/run-nats-servers.yaml up 
```
2. Build the sample using maven:
```
mvn clean package
```
3. Run the sample:
* Uber-jar:

    ```bash
    $ java -jar target/${project.build.finalName}.jar
    ```

  in Windows environemnt use the command
    ```batch
    java -jar target/${project.build.finalName}.jar
    ```

* Exploded:

    ```bash
    $ java -cp target/classes:target/dependency/* com.kumuluz.ee.EeApplication
    ```

  in Windows environment use the command
    ```batch
    java -cp target/classes;target/dependency/* com.kumuluz.ee.EeApplication
    ```
4. Producing messages:

   There are two REST endpoints available that produce messages.
   You can import the Postman collection located in the `util/postman` folder.
   Each of them produces a message that is sent to the NATS server and can be retrieved by consumers (next step).

5. Consuming messages:

   There are two REST endpoints available that ask the server for new messages and fetch them
   and four annotated methods that automatically receive new messages from the server.
   The consumed messages will be printed in the terminal. 

To shut down the example simply stop the processes in the foreground and all Docker containers started for this example.

## Tutorial

This tutorial will guide you through the steps required to create a NATS JetStream producers and consumers with the help of the KumuluzEE NATS JetStream extension.
We will develop two simple REST services for producing NATS JetStream messages, a few simple annotated methods which will be invoked when the message is consumed and two REST services that manually ask the server for new messages. 

We will follow these steps:
* Create a Maven project in the IDE of your choice (Eclipse, IntelliJ, etc.)
* Add Maven dependencies to KumuluzEE and include KumuluzEE components with the microProfile-3.3 dependency
* Add Maven dependency to KumuluzEE NATS JetStream extension
* Configure NATS connections, contexts, streams and consumers
* Implement the annotated methods and REST services
* Build the microservice
* Run it

Add the KumuluzEE BOM module dependency to your `pom.xml` file:
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.kumuluz.ee</groupId>
            <artifactId>kumuluzee-bom</artifactId>
            <version>${kumuluzee.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Add the `kumuluzee-microProfile-3.3` and `kumuluzee-nats-jetstream` dependencies:
```xml
<dependencies>
    <dependency>
        <groupId>com.kumuluz.ee</groupId>
        <artifactId>kumuluzee-microProfile-3.3</artifactId>
    </dependency>
    <dependency>
        <groupId>com.kumuluz.ee.nats</groupId>
        <artifactId>kumuluzee-nats-jetstream</artifactId>
        <version>${kumuluzee-nats.version}</version>
    </dependency>
</dependencies>
```
We will use `kumuluzee-logs` for logging in this sample, so you need to include kumuluzee logs implementation dependency:
```xml
<dependency>
    <artifactId>kumuluzee-logs-log4j2</artifactId>
    <groupId>com.kumuluz.ee.logs</groupId>
    <version>${kumuluzee-logs.version}</version>
</dependency>
```
For more information about the KumuluzEE Logs visit the [KumuluzEE Logs Github page](https://github.com/kumuluz/kumuluzee-logs).
Currently, Log4j2 is supported implementation of `kumuluzee-logs`, so you need to include a sample Log4j2 configuration,
which should be in a file named `log4j2.xml` and located in `src/main/resources`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration name="config-name">
    <Appenders>
        <Console name="console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d %p %marker %m %X %ex %n"/>
        </Console>
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="console"/>
        </Root>
    </Loggers>
</Configuration>
```
If you would like to collect NATS related logs through the KumuluzEE Logs, you have to include the following `slf4j` implementation as dependency:
```xml
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-slf4j-impl</artifactId>
    <version>${log4j-slf4j.version}</version>
</dependency>
```

Add the `jackson-datatype-jsr310` dependency for our custom ObjectMapper provider, so it can work with Java 8 Date & Time API.

```xml
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
</dependency>

Add the `kumuluzee-maven-plugin` build plugin to package microservice as uber-jar:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.kumuluz.ee</groupId>
            <artifactId>kumuluzee-maven-plugin</artifactId>
            <version>${kumuluzee.version}</version>
            <executions>
                <execution>
                    <id>package</id>
                    <goals>
                        <goal>repackage</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

or exploded:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.kumuluz.ee</groupId>
            <artifactId>kumuluzee-maven-plugin</artifactId>
            <version>${kumuluzee.version}</version>
            <executions>
                <execution>
                    <id>package</id>
                    <goals>
                        <goal>copy-dependencies</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### Implement the servlet

Register your module as JAX-RS service and define the application path. You could do that in web.xml or
for example with `@ApplicationPath` annotation:

```java
@ApplicationPath("v1")
public class ProducerApplication extends Application {
}
```

#### Custom ObjectMapper

First create a custom ObjectMapper for de/serialization by implementing `NatsObjectMapperProvider`.
Here we register JavaTimeModule() which enables the usage of Java 8 Date & Time API.

```java
public class NatsMapperProvider implements NatsObjectMapperProvider {

    @Override
    public ObjectMapper provideObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
```

#### Producer

[//]: # (Create the interface SimpleClient, annotate it with `@RegisterRestClient` and specify the name of the NATS connection that the client will use.)

[//]: # ()
[//]: # (Annotate each producer method with `@Subject`. If you want to specify the subject dynamically, use parameter annotation &#40;as you can see at the method `sendSimpleDynamicSubjectResponse&#40;&#41;`&#41;.)

[//]: # ()
[//]: # (```java)

[//]: # (@RegisterNatsClient&#40;connection = "default"&#41;)

[//]: # (public interface SimpleClient {)

[//]: # ()
[//]: # (    @Subject&#40;value = "simple1"&#41;)

[//]: # (    void sendSimple&#40;String value&#41;;)

[//]: # ()
[//]: # (    String sendSimpleDynamicSubjectResponse&#40;@Subject String subject, String value&#41;;)

[//]: # ()
[//]: # (    @Subject&#40;value = "simple2"&#41;)

[//]: # (    String sendSimpleResponse&#40;String value&#41;;)

[//]: # ()
[//]: # (    @Subject&#40;value = "simple_async"&#41;)

[//]: # (    Future<String> sendSimpleResponseAsync&#40;String value&#41;;)

[//]: # ()
[//]: # (    @Subject&#40;"empty"&#41;)

[//]: # (    String sendEmptyPayload&#40;String value&#41;;)

[//]: # (})

[//]: # (```)

Implement the first JAX-RS resource `TextResource` with POST methods that initiate producing of the messages.
Inject the JetStream context using `@Inject` and `@JetStreamProducer` and specify the context name that we will later configure in the configurations. Let's leave the default connection for now.

Now we can use the injected JetStream reference to publish new messages using functions `publish()` and `publishAck()`. 

```java
@Path("/text/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class TextResource {
    
    @Inject
    @JetStreamProducer(context = "context1")
    private JetStream jetStream;

    @POST
    @Path("/subject1")
    public Response postSub1() {
        if (jetStream == null) {
            return Response.serverError().build();
        }
        try {
            String uniqueID = UUID.randomUUID().toString();
            Headers headers = new Headers().add("Nats-Msg-Id", uniqueID);

            Message message = NatsMessage.builder()
                    .subject("subject1")
                    .data(SerDes.serialize("simple message"))
                    .headers(headers)
                    .build();

            PublishAck publishAck = jetStream.publish(message);
            return Response.ok(String.format("Message has been sent to subject %s in stream %s", message.getSubject()
                    , publishAck.getStream())).build();
        } catch (IOException | JetStreamApiException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }

    @POST
    @Path("/subject2")
    public Response postSub2() {
        if (jetStream == null) {
            return Response.serverError().build();
        }
        try {
            Message message = NatsMessage.builder()
                    .subject("subject2")
                    .data(SerDes.serialize("simple message to be pulled manually"))
                    .build();

            CompletableFuture<PublishAck> futureAck = jetStream.publishAsync(message);
            return Response.ok(String.format("Message has been sent to subject %s in stream %s", message.getSubject()
                    , futureAck.get().getStream())).build();
        } catch (IOException | ExecutionException | InterruptedException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
        }
    }
}
```

#### Consumer

##### Pull consumer

To consume messages by pulling them from the server we need to create a JetStreamSubscription for each subject.
With KumuluzEE NATS JetStream we can use a `@JetStreamSubscriber` annotation to inject a JetStreamSubscription reference.

Let's create a service TextSubscriber as shown in the example below. Specify the connection details, the subject and durable name.
Let's override the consumer configuration `custom1` by changing its deliverPolicy to `new`. Now we will only receive the messages that were published after this subscription started. 

With the injected JetStreamSubscription use can now use functions like `fetch()`, `pull()`, `iterate()` etc. to receive new messages.

```java
@ApplicationScoped
public class TextSubscriber {

    @Inject
    @JetStreamSubscriber(context = "context1", stream = "stream1", subject = "subject2", durable = "somethingNew")
    @ConsumerConfig(name = "custom1", configOverrides = {@ConfigurationOverride(key = "deliver-policy", value = "new")})
    private JetStreamSubscription jetStreamSubscription;

    public void pullMsg() {
        if (jetStreamSubscription != null) {
            List<Message> messages = jetStreamSubscription.fetch(3, Duration.ofSeconds(1));
            for (Message message : messages) {
                try {
                    System.out.println(SerDes.deserialize(message.getData(), String.class));
                    message.ack();
                } catch (IOException e) {
                    message.nak();
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
```

Inject the previously created service to the TextResource and create a new REST endpoint for manually pulling new messages from the server.

```java
@Path("/simple/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class SimpleResource {

    @Inject
    private TextSubscriber textSubscriber;

    ...

    @GET
    @Path("/pull")
    public Response pullText() {
        textSubscriber.pullMsg();
        return Response.ok().build();
    }
}
```

##### Push consumer

For push based consumers we can annotate the methods with `@JetStreamListener` and the server will automatically push new messages to the consumers.

Create a new class `TextListener`, add a method and annotate it as shown in the example below.
The method will receive a message through the first method parameter. Make sure to match the type of the producer. 

```java
public class TextListener {

    @JetStreamListener(context = "context1", subject = "subject1")
    @ConsumerConfig(name = "custom1", configOverrides = {@ConfigurationOverride(key = "deliver-policy", value = "new")})
    public void receive(String value) {
        System.out.println(value);
    }
}
```

---

You can continue developing another REST endpoint and listener for producing and consuming streams: ProductResource, ProductListener, ProductSubscriber. 
The main difference is that the type of the messages is not String anymore, but a more complex class `Demo`.
The extension automatically de/serializes the data, just make sure to include the default constructor
```java
public Demo() {
}
```
in your class. And that the consumer's message type matches the producer's.

The complex variant is also using [wildcards](https://docs.nats.io/nats-concepts/subjects) for subjects and a different NATS connection that is secured with the TLS.
In the next section we learn how to configure the connections and how to use the TLS.

### Configuration

Let's configure 2 NATS connections we need for this application:
* default: unsecured connection on the default address
* secure: secured connection by Mutual TLS on localhost:4224

For each connection we also need to specify the streams (unless we want to do this manualy using NATS CLI or similar) that will be generated at the startup. 
We can also specify custom consumer configurations and jetStream context settings.  

```yaml
kumuluzee:
  nats:
    response-timeout: PT5S
    jetstream: true
    servers:
      - name: default
        addresses:
          - nats://localhost:4222
        streams:
          - name: stream1
            subjects:
              - subject1
              - subject2
            storageType: memory
        jetstream-contexts:
          - name: context1
#            domain:
#            prefix:
#            publish-no-ack:
#            request-timeout:
      - name: secure-unverified-client
        addresses:
          - tls://localhost:4223
          - opentls://localhost:4223
        tls:
          trust-store-path: certs\truststore.jks
          trust-store-password: password2
      - name: secure
        addresses:
          - tls://localhost:4224
        tls:
          trust-store-path: certs\truststore.jks
          trust-store-password: password2
          key-store-path: certs\keystore.jks
          key-store-password: password
        streams:
          - name: stream2
            subjects:
              - category.*
            storage-type: memory
    consumer-configuration:
      - name: custom1
        deliver-policy: all
```

See the next section to learn how to set up the TLS.

### Securing with TLS:
1. Generate self-singed CA, client certificates:

   [Easy way (using mkcert)](https://docs.nats.io/running-a-nats-service/configuration/securing_nats/tls#creating-self-signed-certificates-for-testing):
    ```
    mkcert -install
    mkcert -cert-file server-cert.pem -key-file server-key.pem localhost ::1
    mkcert -client -cert-file client-cert.pem -key-file client-key.pem localhost ::1 email@localhost
    ``` 
2. [Create a truststore and keystore](https://docs.nats.io/using-nats/developer/connecting/tls):

    ```
   (winpty) openssl pkcs12 -export -out keystore.p12 -inkey client-key.pem -in client-cert.pem -password pass:password
   keytool -importkeystore -srcstoretype PKCS12 -srckeystore keystore.p12 -srcstorepass password -destkeystore keystore.jks -deststorepass password
   
   keytool -importcert -trustcacerts -file rootCA.pem -storepass password2 -noprompt -keystore truststore.jks
    ```
   You can find the location of your rootCA with `mkcert -CAROOT`.

3. Add server's certificates to the configuration (examples are in *util/run-nats-server*)

4. Properly set the client's configuration file:

   ```
   tls:
       trust-store-path: certs\truststore.jks
       trust-store-password: password2
       key-store-path: certs\keystore.jks
       key-store-password: password
   ```
   - You can either specify a full path or a path from source root (resources directory).
   - Keystore is only needed at Mutual TLS (when also verifying clients). You enable this feature with `verify: true` in the TLS settings in the NATS server configuration file.

#### Examples

##### Default server connection with a custom response timeout
```yaml
kumuluzee:
  nats:
    response-timeout: PT5S
```

##### TLS with a single address

```yaml
kumuluzee:
  nats:
    response-timeout: PT5S
    servers:
      - name: secure-unverified-client
        addresses:
          - tls://localhost:4223
        tls:
          trust-store-path: certs\truststore.jks
          trust-store-password: password2
```

You can either specify the trust store and password, or the server's certificate path.

##### Mutual TLS with a single address

```yaml
kumuluzee:
  nats:
    servers:
      - name: secure
        addresses:
          - tls://localhost:4224
        tls:
          trust-store-path: certs\truststore.jks
          trust-store-password: password2
          key-store-path: certs\keystore.jks
          key-store-password: password
```

For Mutual TLS you also need to specify a key store.

### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.

## Using NATS CLI with secure connections

```text
nats context save secure --server nats://localhost:4224 --description 'Secure localhost' --tlscert=./client-cert.pem --tlskey=./private_key.pem --tlsca=rootCA.pem
nats context select secure
```

Now you can, for example, remove the stream `stream2` and all its messages and [much more](https://docs.nats.io/using-nats/nats-tools/nats_cli).

```text
nats stream rm stream2
```

Client's private key can be extracted from keystore.jks with:

```text
keytool -importkeystore -srckeystore keystore.jks -destkeystore keystore.p12 -srcstoretype jks -deststoretype pkcs12
(winpty) openssl pkcs12 -in keystore.p12 -nodes -nocerts -out private_key.pem
```