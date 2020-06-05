package com.seekerslabs.jenkins

class Kaniko {
  static String KANIKO_IMAGE
  static String AWSCLI_IMAGE
  static String CREDENTIALS_IMAGE

  static void kanikoTemplateGenesis(script, body) {
    String podYaml = """
apiVersion: v1
kind: Pod
spec:
  serviceAccountName: ecr
  containers:
  - name: kaniko
    image: $KANIKO_IMAGE
    command: ["cat"]
    tty: true
    volumeMounts:
    - name: docker-config
      mountPath: /kaniko/.docker
    - name: awsdir
      mountPath: /root/.aws
  initContainers:
  - name: awscli
    image: $AWSCLI_IMAGE
    command: ["/bin/sh"]
    args:
    - -c
    - sh /etc/config/get_aws_credentials.sh
    volumeMounts:
    - name: awsdir
      mountPath: /work-dir
    - name: config-volume
      mountPath: /etc/config
  volumes:
  - name: awsdir
    emptyDir: {}
  - name: config-volume
    configMap:
      name: get-aws-credentials-config
  - name: docker-config
    configMap:
      name: docker-config
"""

    script.podTemplate(yaml: podYaml) {
      body.call()
    }
  }

  static void kanikoTemplate(script, body) {
    String podYaml = """
apiVersion: v1
kind: Pod
spec:
  serviceAccountName: ecr
  containers:
  - name: kaniko
    image: $KANIKO_IMAGE
    command: ["cat"]
    tty: true
    volumeMounts:
    - name: docker-config
      mountPath: /kaniko/.docker
    - name: awsdir
      mountPath: /root/.aws
  initContainers:
  - name: credentials
    image: $CREDENTIALS_IMAGE
    volumeMounts:
    - name: awsdir
      mountPath: /work-dir
  volumes:
  - name: awsdir
    emptyDir: {}
  - name: docker-config
    configMap:
      name: docker-config
"""

    script.podTemplate(yaml: podYaml) {
      body.call()
    }
  }
}
