{{- define "deployment.envs" }}
- name: JAVA_OPTS
  value: '{{ tpl .Values.app.javaOpts . }}'
- name: SERVER_PORT
  value: '{{ include "hocs-app.port" . }}'
- name: SPRING_PROFILES_ACTIVE
  value: '{{ tpl .Values.app.springProfiles . }}'
- name: ELASTICSEARCH_INDEX_PREFIX
  value: '{{ tpl .Values.app.elasticPrefix . }}'
- name: SEARCH_SQS_QUEUE_NAME
  value: '{{ tpl .Values.app.searchQueueName . }}'
- name: ELASTICSEARCH_HOST
  valueFrom:
    secretKeyRef:
      name: {{ .Values.namespace }}-elasticsearch
      key: endpoint
- name: ELASTICSEARCH_ACCESS_KEY
  valueFrom:
    secretKeyRef:
      name: {{ .Values.namespace }}-elasticsearch
      key: access_key_id
- name: ELASTICSEARCH_SECRET_KEY
  valueFrom:
    secretKeyRef:
      name: {{ .Values.namespace }}-elasticsearch
      key: secret_access_key
- name: SEARCH_SQS_QUEUE_URL
  valueFrom:
    secretKeyRef:
      name: {{ .Values.namespace }}-search-sqs
      key: sqs_queue_url
- name: SEARCH_SQS_ACCESS_KEY
  valueFrom:
    secretKeyRef:
      name: {{ .Values.namespace }}-search-sqs
      key: access_key_id
- name: SEARCH_SQS_SECRET_KEY
  valueFrom:
    secretKeyRef:
      name: {{ .Values.namespace }}-search-sqs
      key: secret_access_key
{{- end -}}
