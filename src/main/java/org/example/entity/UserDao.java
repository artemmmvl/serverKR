package org.example.entity;
import lombok.*;

@Data
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDao {
    private Long id;
    private String firstname;
    private long birthday;
    private String gender;
    private String email;
    private String aboutMe;
    private String city;
    private String vk;
    private String tg;
    private String img;
}
