package com.teknisio.security;

import com.teknisio.model.entities.User;
import com.teknisio.model.enums.UserRole;
import com.teknisio.model.enums.UserStatus;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
public class CustomUserDetails implements UserDetails {

  private final UUID idUser;
  private final String nama;
  private final String email;
  private final String passwordHash;
  private final UserRole role;
  private final UserStatus statusAkun;

  private CustomUserDetails(
    UUID idUser,
    String nama,
    String email,
    String passwordHash,
    UserRole role,
    UserStatus statusAkun
  ) {
    this.idUser = idUser;
    this.nama = nama;
    this.email = email;
    this.passwordHash = passwordHash;
    this.role = role;
    this.statusAkun = statusAkun;
  }

  public static CustomUserDetails fromUser(User user) {
    return new CustomUserDetails(
      user.getIdUser(),
      user.getNama(),
      user.getEmail(),
      user.getPasswordHash(),
      user.getRole(),
      user.getStatusAkun()
    );
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
  }

  @Override
  public String getPassword() {
    return passwordHash;
  }

  @Override
  public String getUsername() {
    return idUser.toString();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return statusAkun != UserStatus.BANNED && statusAkun != UserStatus.SUSPENDED;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return statusAkun == UserStatus.ACTIVE;
  }
}
