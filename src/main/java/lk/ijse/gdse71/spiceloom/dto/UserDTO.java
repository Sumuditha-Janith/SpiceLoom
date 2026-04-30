package lk.ijse.gdse71.spiceloom.dto;

import lk.ijse.gdse71.spiceloom.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDTO {
    private int userId;
    private String username;
    private String email;
    private String address;
    private Role role;
}