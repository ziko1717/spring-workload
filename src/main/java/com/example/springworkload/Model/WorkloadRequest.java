package com.example.springworkload.Model;

import java.util.ArrayList;
import java.util.List;

public class WorkloadRequest {
    private String name;
    private String namespace;
    private String workloadType;
    private String hasTests;
    private String partOf;

    public String getHasTests() {
        return hasTests;
    }

    public String getPartOf() {
        return partOf;
    }

    private String gitUrl;
    private String gitopsTeam;
    private String language;
    private String serviceClaimsName;
    private String serviceClaimsRefApiVersion;
    private String serviceClaimsRefKind;
    private String serviceClaimsRefName;
    private ArrayList<Variables> envVariables;
    private ArrayList<Variables> buildVariables;
    private ArrayList<Variables> params;

    private String resourcesRequestsMemory;
    private String resourcesRequestsCpu;
    private String resourcesLimitsMemory;
    private String resourcesLimitsCpu;

    // Getters and setters

    public String getWorkloadType() {
        return workloadType;
    }

    public String getName() {
        return name;
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public String getGitopsTeam() {
        return gitopsTeam;
    }

    public String getLanguage() {
        return language;
    }

    public String getResourcesRequestsMemory() {
        return resourcesRequestsMemory;
    }

    public String getResourcesRequestsCpu() {
        return resourcesRequestsCpu;
    }

    public String getResourcesLimitsMemory() {
        return resourcesLimitsMemory;
    }

    public String getResourcesLimitsCpu() {
        return resourcesLimitsCpu;
    }

    /*public static class Resource {
        public String memory;
        public String cpu;

        // Getters and setters


        public String getMemory() {
            return memory;
        }

        public String getCpu() {
            return cpu;
        }
    }*/

    public static class Variables {
        public String name;
        public Object value;

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }

        // Getters and setters
    }

    public String getNamespace() {
        return namespace;
    }

    public String getServiceClaimsName() {
        return serviceClaimsName;
    }

    public String getServiceClaimsRefApiVersion() {
        return serviceClaimsRefApiVersion;
    }

    public String getServiceClaimsRefKind() {
        return serviceClaimsRefKind;
    }

    public String getServiceClaimsRefName() {
        return serviceClaimsRefName;
    }

    public List<Variables> getEnvVariables() {
        return envVariables;
    }

    public List<Variables> getBuildVariables() {
        return buildVariables;
    }

    public List<Variables> getParams() {
        return params;
    }
}
