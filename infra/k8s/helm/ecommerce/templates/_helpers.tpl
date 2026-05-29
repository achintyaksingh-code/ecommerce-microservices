{{- define "ecommerce.name" -}}
{{- .Chart.Name -}}
{{- end -}}

{{- define "ecommerce.labels" -}}
app.kubernetes.io/name: {{ include "ecommerce.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end -}}
