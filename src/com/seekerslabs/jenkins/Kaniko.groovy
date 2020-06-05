package com.seekerslabs.jenkins

class Kaniko {
  static void kanikoTemplateGenesis(body) {
    string podYaml = """
apiVersion: v1
kind: Pod
spec:
  serviceAccountName: ecr
  containers:
  - name: kaniko
    image: ${env.KANIKO_IMAGE}
    command: ["cat"]
    tty: true
    volumeMounts:
    - name: docker-config
      mountPath: /kaniko/.docker
    - name: awsdir
      mountPath: /root/.aws
  initContainers:
  - name: awscli
    image: ${env.AWSCLI_IMAGE}
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

    podTemplate(yaml: podYaml) {
      body.call()
    }
  }

  static void kanikoTemplate(body) {
    string podYaml = """
apiVersion: v1
kind: Pod
spec:
  serviceAccountName: ecr
  containers:
  - name: kaniko
    image: ${env.KANIKO_IMAGE}
    command: ["cat"]
    tty: true
    volumeMounts:
    - name: docker-config
      mountPath: /kaniko/.docker
    - name: awsdir
      mountPath: /root/.aws
  initContainers:
  - name: credentials
    image: ${env.CREDENTIALS_IMAGE}
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

    podTemplate(yaml: podYaml) {
      body.call()
    }
  }
}
