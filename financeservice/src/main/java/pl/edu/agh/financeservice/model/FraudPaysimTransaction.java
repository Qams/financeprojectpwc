package pl.edu.agh.financeservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FraudPaysimTransaction {
    private String id;
    private BigDecimal amount;
    private double probability;
}
