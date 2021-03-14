test_1:
  security_key: securityCode
  version: "{{ small }}"
  server:
    max_users: 1
    port: 6000
    minion: colo.splash
  db:
    pass: yjPZHbgBFMkW
  document:
    bucket: s3bucketName
    accessKeyId: s3bucketName
    accessSecretKey: s3bucketName
    region: ap-southeast-2
    limit: 1G
  user:
    firstName: s3bucketName
    lastName: s3bucketName
    email: s3bucketName
  billing:
    code: collegeKey
    plan: basic
    paid_until: "s3bucketName"
    web:
      plan: WEB-6