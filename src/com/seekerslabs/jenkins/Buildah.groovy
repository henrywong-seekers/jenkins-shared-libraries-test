package com.seekerslabs.jenkins

class Buildah {
  static String BUILDAH_IMAGE
  static String AWSCLI_IMAGE

  static void buildahTemplate(script, body) {
    String podYaml = """
apiVersion: v1
kind: Pod
spec:
  serviceAccountName: ecr
  containers:
  - name: buildah
    image: $BUILDAH_IMAGE
    env:
    - name: STORAGE_DRIVER
      value: vfs
    command: ["cat"]
    tty: true
    volumeMounts:
    - name: workdir
      mountPath: /work-dir
  initContainers:
  - name: awscli
    image: $AWSCLI_IMAGE
    command: ["/bin/sh"]
    args:
    - -c
    - sh /etc/config/get_docker_password.sh
    volumeMounts:
    - name: workdir
      mountPath: /work-dir
    - name: config-volume
      mountPath: /etc/config
  volumes:
  - name: workdir
    emptyDir: {}
  - name: config-volume
    configMap:
      name: get-docker-password-config
"""

    script.podTemplate(yaml: podYaml) {
      body.call()
    }
  }
}
