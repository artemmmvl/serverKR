package org.example.entity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name="users")
public class User implements  UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String firstname;
    @Column
    private long birthday;
    @ManyToOne
    private City city;
    @Column
    private String gender;
    @Column
    private String email;
    @Column
    private String password;
    @Column
    private String aboutMe;
    @Column
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column
    private String vk;
    @Column
    private String imgPath;
    @Column
    private String tg;
    @ManyToMany
    private List<User> favorites;
    @ManyToMany
    private List<User> skip;


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                '}';
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
