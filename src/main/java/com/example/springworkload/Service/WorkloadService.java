package com.example.springworkload.Service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.springworkload.Model.WorkloadRequest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class WorkloadService {

    // Git script variables
    // The SSH_PRIV_KEY variable must be set at as a global environment variable
    @Value("${ssh_priv_key}")
    private String SSH_PRIV_KEY;
    @Value("${repo_url}")
    private String REPO_URL;
    @Value("${repo_dir}")
    private String REPO_DIR;

    public String generateWorkloadFile(WorkloadRequest request) throws IOException, InterruptedException {
        String fileName = request.getName() + ".yaml";
        File file = new File(REPO_DIR, fileName);
        // Clone or pull the remote repository
        cloneOrPullRepository();

        // Write yaml content in the file in the repo
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(generateYamlContent(request));
        }

        // Commit and push changes to the remote repository
        commitAndPush("Genrate file "+REPO_DIR+"/"+fileName);

        // Return file absolute path in the disk
        return file.getAbsolutePath();
    }

    private String generateYamlContent(WorkloadRequest request) {
        StringBuilder yamlContent = new StringBuilder();

        try {
            // Construct the Yaml file using WorlkloadRequest
            // Getters and WorkloadService functions getBuildVariables, getEnvVariables & getParams
            // The appendVariables function is used to append env and params
            // which are direct child of the spec and appendSecondLevelVariables
            // is for build:env which is child of a child of env
            // The two functions can handle at the third level of
            // the child being an object. This can be ameliorated
            // maybe with recursivity and the functions merged.
            // appendServiceServiceClaims is to append serviceClaims block.
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
                    yamlContent.append("        value: \"").append(stringValue).append("\"\n");
                } else if (variable.getValue() instanceof Map) {
                    // Handle JSON object value
                    Map<String, Object> jsonValue = (Map<String, Object>) variable.getValue();
                    yamlContent.append("        value: \n");
                    for (Map.Entry<String, Object> entry : jsonValue.entrySet()) {
                        if (entry.getValue() instanceof String) {
                            yamlContent.append("          ").append(entry.getKey()).append(": \"").append(entry.getValue()).append("\"\n");
                        }
                        else if (entry.getValue() instanceof Map) {
                            yamlContent.append("          "+entry.getKey()+": \n");
                            Map<String, Object> jsonValue2 = (Map<String, Object>) entry.getValue();
                            for (Map.Entry<String, Object> entry2 :jsonValue2.entrySet()) {
                                yamlContent.append("            ").append(entry2.getKey()).append(": \"").append(entry2.getValue()).append("\"\n");
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
                    yamlContent.append("      value: \"").append(stringValue).append("\"\n");
                } else if (variable.getValue() instanceof Map) {
                    // Handle JSON object value
                    Map<String, Object> jsonValue = (Map<String, Object>) variable.getValue();
                    yamlContent.append("      value: \n");
                    for (Map.Entry<String, Object> entry : jsonValue.entrySet()) {
                        if (entry.getValue() instanceof String) {
                            yamlContent.append("        ").append(entry.getKey()).append(": \"").append(entry.getValue()).append("\"\n");
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

    private void appendServiceClaims(StringBuilder yamlContent, String name, String refApiVersion, String refKind, String refName) {
        yamlContent.append("  serviceClaims:\n");
        yamlContent.append("  - name: \"").append(name).append("\"\n");
        yamlContent.append("    ref:\n");
        yamlContent.append("      apiVersion: \"").append(refApiVersion).append("\"\n");
        yamlContent.append("      kind: \"").append(refKind).append("\"\n");
        yamlContent.append("      name: \"").append(refName).append("\"\n");
    }
    // Modify a file if it exists in the repo and push
    public String modifyWorkloadFile(String name, WorkloadRequest request) throws IOException, InterruptedException {
        String fileName = name + ".yaml";
        File file = new File(REPO_DIR, fileName);

        // Clone repo
        cloneOrPullRepository();
        System.out.println(file.getAbsolutePath());

        if (!file.exists()) {
            throw new IOException("File not found");
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(generateYamlContent(request));
        }

        // Commit and push changes
        commitAndPush("Updated file "+REPO_DIR+"/"+fileName);


        return file.getAbsolutePath();
    }
    // Delete a file if it exists and push
    public String deleteWorkloadFile(String name) throws IOException, InterruptedException {
        String fileName = name + ".yaml";
        File file = new File(REPO_DIR, fileName);
        cloneOrPullRepository();
        if (!file.exists()) {
            throw new IOException("File not found");
        }
        file.delete();
        commitAndPush("Deleted file "+REPO_DIR+"/"+fileName);
        return "File "+REPO_DIR+"/"+fileName+" Deleted";
    }


    // Make use of a Powershell script scripts/git-utility to clone or pull the repo if it exists
    private void cloneOrPullRepository() throws IOException, InterruptedException {
        // Execute shell command to clone the repository
        String cloneCommand = "powershell -ExecutionPolicy Bypass -command \"& { . "+System.getProperty("user.dir")+"\\scripts\\git-utility.ps1 "+ SSH_PRIV_KEY +"; CloneOrPull "+ REPO_URL +" "+ REPO_DIR + "}\"";
        executePowerShellCommand(cloneCommand);
    }

    // Make use of a Powershell script scripts/git-utility to commit and push
    private void commitAndPush(String commitMessage) throws IOException, InterruptedException {
        // Execute shell command to commit and push changes
        String commitCommand = "powershell -ExecutionPolicy Bypass -command \"& { . "+ System.getProperty("user.dir") +"\\scripts\\git-utility.ps1 "+ SSH_PRIV_KEY +"; CommitAndPush "+ REPO_DIR + " '" + commitMessage + "'}\"";
        executePowerShellCommand(commitCommand);
    }

    // Execute powershell script function
    private void executePowerShellCommand(String command) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(command);
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("PowerShell command failed with exit code " + exitCode);
        }
    }
}