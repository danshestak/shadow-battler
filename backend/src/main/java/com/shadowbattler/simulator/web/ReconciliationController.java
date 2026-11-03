package com.shadowbattler.simulator.web;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shadowbattler.simulator.service.EntityReconciliationService;

@RestController
@RequestMapping("/api/reconciliation")
public class ReconciliationController {
    private final EntityReconciliationService reconciliationService;

    public ReconciliationController(EntityReconciliationService reconciliationService) {
        this.reconciliationService = reconciliationService;
    }

    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> start() {
        try {
            if (!reconciliationService.startReconciliation()) {
                return ResponseEntity.status(409).body(reconciliationService.getReconciliationStatus());
            }
            reconciliationService.reconcileModified();
            return ResponseEntity.accepted().body(reconciliationService.getReconciliationStatus());
        } catch (IllegalStateException exception) {
            return ResponseEntity.status(409).body(reconciliationService.getReconciliationStatus());
        }
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        return reconciliationService.getReconciliationStatus();
    }
}
