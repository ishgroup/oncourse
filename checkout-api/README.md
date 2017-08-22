1. ./gradlew build
2. build/distributions contains distribution 
3. application.yml example 

````
jetty:
  connector:
    port: 10080
cxf:
  urlPattern: '/*'
  welcomeText: 'Willow REST API: 0.0.1'
  
````
3. unpack this distribution 
4. Start application 