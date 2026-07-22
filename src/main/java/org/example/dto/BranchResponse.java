package org.example.dto;

import org.example.model.Branch;

import java.time.LocalTime;

public record BranchResponse(Long id,
                             String name,
                             String address,
                             LocalTime openTime,
                             LocalTime closeTime,
                             Integer slotMinutes) {

    public static BranchResponse from(Branch branch) {
        return new BranchResponse(
                branch.getId(),
                branch.getName(),
                branch.getAddress(),
                branch.getOpenTime(),
                branch.getCloseTime(),
                branch.getSlotMinutes()
        );
    }
}

