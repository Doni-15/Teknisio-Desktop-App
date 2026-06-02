package com.teknisio.security;

import com.teknisio.model.entities.User;
import com.teknisio.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = findByUuidOrEmail(username);
    return CustomUserDetails.fromUser(user);
  }

  public CustomUserDetails loadByEmail(String email) {
    User user = userRepository.findByEmailIgnoreCaseAndDeletedAtIsNull(email)
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return CustomUserDetails.fromUser(user);
  }

  private User findByUuidOrEmail(String username) {
    try {
      UUID idUser = UUID.fromString(username);

      return userRepository.findByIdUserAndDeletedAtIsNull(idUser)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    } catch (IllegalArgumentException exception) {
      return userRepository.findByEmailIgnoreCaseAndDeletedAtIsNull(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
  }
}
