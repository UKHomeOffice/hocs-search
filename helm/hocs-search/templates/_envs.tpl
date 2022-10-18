{{- define "deployment.envs" }}
- name: JAVA_OPTS
  value: '{{ tpl .Values.app.env.javaOpts . }}'
{{- if not .Values.proxy.enabled }}
- name: SERVER_SSL_KEY_STORE_TYPE
  value: 'PKCS12'
- name: SERVER_SSL_KEY_STORE_PASSWORD
  value: 'changeit'
- name: SERVER_SSL_KEY_STORE
  value: 'file:/etc/keystore/keystore.jks'
- name: SERVER_COMPRESSION_ENABLED
  value: 'true'
- name: SERVER_SSL_ENABLED
  value: 'true'
{{- end }}
- name: SERVER_PORT
  value: '{{ include "hocs-app.port" . }}'
- name: SPRING_PROFILES_ACTIVE
  value: '{{ tpl .Values.app.env.springProfiles . }}'
- name: ELASTICSEARCH_INDEX_PREFIX
  value: '{{ tpl .Values.app.env.elasticPrefix . }}'
- name: ELASTICSEARCH_MODE
  value: '{{ tpl .Values.app.env.elasticMode . }}'
- name: SEARCH_SQS_QUEUE_NAME
  value: '{{ tpl .Values.app.env.searchQueueName . }}'
- name: ELASTICSEARCH_HOST
  valueFrom:
    secretKeyRef:
      name: {{ .Release.Namespace }}-elasticsearch
      key: endpoint
- name: ELASTICSEARCH_ACCESS_KEY
  valueFrom:
    secretKeyRef:
      name: {{ .Release.Namespace }}-elasticsearch
      key: access_key_id
- name: ELASTICSEARCH_SECRET_KEY
  valueFrom:
    secretKeyRef:
      name: {{ .Release.Namespace }}-elasticsearch
      key: secret_access_key
- name: SEARCH_SQS_QUEUE_URL
  valueFrom:
    secretKeyRef:
      name: {{ .Release.Namespace }}-search-sqs
      key: sqs_queue_url
- name: SEARCH_SQS_ACCESS_KEY
  valueFrom:
    secretKeyRef:
      name: {{ .Release.Namespace }}-search-sqs
      key: access_key_id
- name: SEARCH_SQS_SECRET_KEY
  valueFrom:
    secretKeyRef:
      name: {{ .Release.Namespace }}-search-sqs
      key: secret_access_key
{{- end -}}
