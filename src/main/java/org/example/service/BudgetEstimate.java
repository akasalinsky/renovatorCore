package org.example.service;

import org.example.service.money.Money;

public record BudgetEstimate(
        Money wallPlasteringCost,
        Money wallPrimingCost,
        Money wallpaperingCost,
        Money floorPouringCost,
        Money floorCoveringCost,
        Money skirtingBoardCost,
        Money ceilingCost,
        Money totalCost
) {
}