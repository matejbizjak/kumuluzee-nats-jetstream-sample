# KumuluzEE NATS Core sample:

## TODO

Starting NATS servers: `docker-compose -f util/run-nats-server/run-nats-servers.yaml up`


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
       ###
       trust-store-path: ...\resources\certs\truststore.jks
       trust-store-password: password2
       # or
       certificate-path: ...\resources\certs\server-cert.pem
       ###
       key-store-path: ...\resources\certs\keystore.jks
       key-store-password: password
   ```
   - You can either use a truststore or server's certificate for server verification. 
   - Keystore is only needed at Mutual TLS (when also verifying clients). You enable this feature with `verify: true` in the TLS settings in the NATS server configuration file.

    