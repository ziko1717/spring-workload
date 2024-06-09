package com.example.springworkload.Controller;


import com.example.springworkload.Model.WorkloadRequest;
import com.example.springworkload.Service.WorkloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/workloads")
public class WorkloadController {

    @Autowired
    private WorkloadService workloadService;

    @PostMapping
    public ResponseEntity<String> createWorkload(@RequestBody WorkloadRequest request) {
        try {
            String filePath = workloadService.generateWorkloadFile(request);
            return ResponseEntity.ok("Workload file created at: " + filePath);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error creating workload file: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/{name}")
    public ResponseEntity<String> updateWorkload(@PathVariable String name, @RequestBody WorkloadRequest request) {
        try {
            String filePath = workloadService.modifyWorkloadFile(name, request);
            return ResponseEntity.ok("Workload file updated at: " + filePath);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error updating workload file: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<String> deleteWorkload(@PathVariable String name) throws IOException, InterruptedException {
        try {
            String response = workloadService.deleteWorkloadFile(name);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error deleting workload file: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}