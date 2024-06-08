package com.example.springworkload.Service;


import org.springframework.stereotype.Service;

import com.example.springworkload.Model.WorkloadRequest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class WorkloadService {

    private static final String DIRECTORY = "workloads";

    public String generateWorkloadFile(WorkloadRequest request) throws IOException {
        String fileName = request.getName() + ".yaml";
        File file = new File(DIRECTORY, fileName);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(generateYamlContent(request));
        }

        return file.getAbsolutePath();
    }

    private String generateYamlContent(WorkloadRequest request) {
        StringBuilder yamlContent = new StringBuilder();

        try {
            yamlContent.append("---\n");
            yamlContent.append("apiVersion: \"carto.run/v1alpha1\"\n");
            yamlContent.append("kind: \"Workload\"\n");
            yamlContent.append("metadata:\n");
            yamlContent.append("  name: \"").append(request.getName()).append("\"\n");
            yamlContent.append("  namespace: \"").append(request.getNamespace()).append("\"\n");
            yamlContent.append("  labels:\n");
            yamlContent.append("    apps.tanzu.vmware.com/workload-type: \"").append(request.getWorkloadType()).append("\"\n");
            yamlContent.append("    apps.tanzu.vmware.com/has-tests: \"").append(request.getHasTests()).append("\"\n");
            yamlContent.append("    apps.tanzu.vmware.com/part-of: \"").append(request.getPartOf()).append("\"\n");
            yamlContent.append("spec:\n");
            appendVariables(yamlContent, "env", request.getEnvVariables());

            yamlContent.append("  build:\n");
            appendSecondLevelVariables(yamlContent, "env", request.getBuildVariables());
            appendVariables(yamlContent, "params", request.getParams());

            appendServiceClaims(yamlContent, request.getServiceClaimsName(), request.getServiceClaimsRefApiVersion(), request.getServiceClaimsRefKind(), request.getServiceClaimsRefName());

            yamlContent.append("  resources:\n");
            yamlContent.append("    requests:\n");
            yamlContent.append("      memory: \"").append(request.getResourcesRequestsMemory()).append("\"\n");
            yamlContent.append("      cpu: \"").append(request.getResourcesRequestsCpu()).append("\"\n");
            yamlContent.append("    limits:\n");
            yamlContent.append("      memory: \"").append(request.getResourcesLimitsMemory()).append("\"\n");
            yamlContent.append("      cpu: \"").append(request.getResourcesLimitsCpu()).append("\"\n");


        }catch (Error er){
            System.out.println(er);

        }
        return yamlContent.toString();
    }

    private void appendSecondLevelVariables(StringBuilder yamlContent, String section, List<WorkloadRequest.Variables> variables) {
        if (variables != null && !variables.isEmpty()) {
            yamlContent.append("    ").append(section).append(":\n");

            for (WorkloadRequest.Variables variable : variables) {
                yamlContent.append("      - name: \"").append(variable.getName()).append("\"\n");
                if (variable.getValue() instanceof String) {
                    // Handle string value
                    String stringValue = (String) variable.getValue();
                    System.out.println("String value: " + stringValue);
                    yamlContent.append("        value: \"").append(stringValue).append("\"\n");
                } else if (variable.getValue() instanceof Map) {
                    // Handle JSON object value
                    Map<String, Object> jsonValue = (Map<String, Object>) variable.getValue();
                    System.out.println("JSON object value: " + jsonValue);
                    yamlContent.append("        value: \n");
                    for (Map.Entry<String, Object> entry : jsonValue.entrySet()) {
                        if (entry.getValue() instanceof String) {
                            yamlContent.append("          ").append(entry.getKey()).append(": \"").append(entry.getValue()).append("\"\n");
                        }
                        else if (entry.getValue() instanceof Map) {
                            yamlContent.append("        "+entry.getKey()+": \n");
                            Map<String, Object> jsonValue2 = (Map<String, Object>) entry.getValue();
                            for (Map.Entry<String, Object> entry2 :jsonValue2.entrySet()) {
                                yamlContent.append("          ").append(entry2.getKey()).append(": \"").append(entry2.getValue()).append("\"\n");
                            }
                        }

                    }
                }

            }
        }
    }

    private void appendVariables(StringBuilder yamlContent, String section, List<WorkloadRequest.Variables> variables) {
        if (variables != null && !variables.isEmpty()) {
            yamlContent.append("  ").append(section).append(":\n");

            for (WorkloadRequest.Variables variable : variables) {
                yamlContent.append("    - name: \"").append(variable.getName()).append("\"\n");
                if (variable.getValue() instanceof String) {
                    // Handle string value
                    String stringValue = (String) variable.getValue();
                    System.out.println("String value: " + stringValue);
                    yamlContent.append("      value: \"").append(stringValue).append("\"\n");
                } else if (variable.getValue() instanceof Map) {
                    // Handle JSON object value
                    Map<String, Object> jsonValue = (Map<String, Object>) variable.getValue();
                    System.out.println("JSON object value: " + jsonValue);
                    yamlContent.append("      value: \n");
                    for (Map.Entry<String, Object> entry : jsonValue.entrySet()) {
                        if (entry.getValue() instanceof String) {
                            yamlContent.append("        ").append(entry.getKey()).append(": \"").append(entry.getValue()).append("\"\n");
                        }
                        else if (entry.getValue() instanceof Map) {
                            yamlContent.append("      "+entry.getKey()+": \n");
                            Map<String, Object> jsonValue2 = (Map<String, Object>) entry.getValue();
                            for (Map.Entry<String, Object> entry2 :jsonValue2.entrySet()) {
                                yamlContent.append("        ").append(entry2.getKey()).append(": \"").append(entry2.getValue()).append("\"\n");
                            }
                        }

                    }
                }

            }
        }
    }

    private void appendServiceClaims(StringBuilder yamlContent, String name, String refApiVersion, String refKind, String refName) {
        yamlContent.append("  serviceClaims:\n");
        yamlContent.append("  - name: \"").append(name).append("\"\n");
        yamlContent.append("    ref:\n");
        yamlContent.append("      apiVersion: \"").append(refApiVersion).append("\"\n");
        yamlContent.append("      kind: \"").append(refKind).append("\"\n");
        yamlContent.append("      name: \"").append(refName).append("\"\n");
    }

    public String modifyWorkloadFile(String name, WorkloadRequest request) throws IOException {
        String fileName = name + ".yaml";
        File file = new File(DIRECTORY, fileName);

        if (!file.exists()) {
            throw new IOException("File not found");
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(generateYamlContent(request));
        }

        return file.getAbsolutePath();
    }

    public boolean deleteWorkloadFile(String name) {
        String fileName = name + ".yaml";
        File file = new File(DIRECTORY, fileName);
        return file.delete();
    }
}
