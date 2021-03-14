---
test_4:
  security_key: "securityCode"
  version: "\"{{ small }}\""
  server:
    max_users: 1
    port: "10002"
    minion: "colo.splash"
  db:
    pass: "bjmAhUKF7ypW"
  document:
    bucket: "s3bucketName"
    accessKeyId: "s3bucketName"
    accessSecretKey: "s3bucketName"
    region: "ap-southeast-2"
    limit: "1G"
  user:
    firstName: "s3bucketName"
    lastName: "s3bucketName"
    email: "s3bucketName"
  billing:
    code: "test_4"
    plan: "basic"
    paid_until:
      values:
      - "s3bucketName"
      strings:
      - "\""
      - "\""
      bytes: !!binary |-
        InMzYnVja2V0TmFtZSI=
      valueCount: 1
    web:
      plan: "WEB-6"
