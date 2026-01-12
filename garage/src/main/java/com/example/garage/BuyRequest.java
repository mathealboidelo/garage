package com.example.garage;

import lombok.Data;

@Data
public record BuyRequest(Long userId, Long carId) {

}
