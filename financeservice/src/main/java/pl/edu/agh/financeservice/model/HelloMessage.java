package pl.edu.agh.financeservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HelloMessage {
    private String id;
    private Date timestamp;
    private double ask;
    private double bid;
    private double askVolume;
    private double bidVolume;
    private String from;
    private String to;
    private String exchange;
}
