# Willow Codegen Library

1. Write specification
2. Generate Java JAX-RS server
3. Generate TypeScript client
4. Generate Java and TypeScript model
5. Generate Java/TypeScript Stub

## Links

1. [Swagger Editor](http://editor.swagger.io/#!/)
2. [Swagger Specification](http://swagger.io/specification/)
3. [Mustache Template Variables](https://github.com/swagger-api/swagger-codegen/wiki/Mustache-Template-Variables)

## Run

To generate sources for `:willow:api`:

```bash
pwd # Should be: .../willow-api/codegen
./gradlew :jaxrs:run
```