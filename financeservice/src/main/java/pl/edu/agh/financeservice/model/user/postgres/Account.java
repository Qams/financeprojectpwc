package pl.edu.agh.financeservice.model.user.postgres;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue
    private long id;

    private String number;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @ManyToOne
    @JsonIgnore
    private AppUser appUser;
}
