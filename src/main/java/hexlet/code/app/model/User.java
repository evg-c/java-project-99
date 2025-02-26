package hexlet.code.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import jakarta.validation.constraints.Email;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString(includeFieldNames = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails, BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    @EqualsAndHashCode.Include
    private Long id;

    @ToString.Include
    private String firstName;

    @ToString.Include
    private String lastName;

    @NotNull
    @ToString.Include
    @EqualsAndHashCode.Include
    @Column(unique = true)
    @Email
    private String email;

    @Size(min = 3)
    private String password;

    @CreatedDate
    private LocalDate createdAt;

    @LastModifiedDate
    private LocalDate updatedAt;

    /**
     * Геттер для получения списка ролей пользователя.
     * @return - возвращает список ролей пользователя (пустой)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<GrantedAuthority>();
    }

    /**
     * Геттер для получения пароля (хэшированного).
     * @return - возвращает пароль (точнее хэш пароля)
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Геттер для получения логина.
     * @return - возвращает логин (здесь логин это email).
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Метод для определения активности пользователя.
     * @return - возвращает true (что означает, что пользователь активен).
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Метод для определения непросроченности учетной записи пользователя.
     * @return - возвращает true (что означает, что учетная запись пользователь не просрочена).
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Метод для определения незаблокированности учетной записи пользователя.
     * @return - возвращает true (что означает, что учетная запись пользователь не заблокирована).
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Метод для определения непросроченности учетных данных пользователя.
     * @return - возвращает true (что означает, что учетные данные пользователь не истекли).
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
