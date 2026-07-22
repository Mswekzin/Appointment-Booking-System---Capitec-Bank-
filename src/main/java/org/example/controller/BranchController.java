package org.example.controller;

import org.example.dto.AvailableSlotResponse;
import org.example.dto.BranchResponse;
import org.example.repository.BranchRepository;
import org.example.service.AppointmentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/branches")
public class BranchController {

    private final BranchRepository branchRepository;
    private final AppointmentService appointmentService;

    public BranchController(BranchRepository branchRepository, AppointmentService appointmentService) {
        this.branchRepository = branchRepository;
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public List<BranchResponse> getBranches() {
        return branchRepository.findAll()
                .stream()
                .map(BranchResponse::from)
                .toList();
    }

    @GetMapping("/{branchId}/slots")
    public List<AvailableSlotResponse> getAvailableSlots(@PathVariable Long branchId,
                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return appointmentService.getAvailableSlots(branchId, date);
    }
}

