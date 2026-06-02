package com.teknisio.security;

import com.teknisio.common.exception.UnauthorizedException;
import com.teknisio.model.enums.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CurrentUserService {

  public CustomUserDetails getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails currentUser)) {
      throw new UnauthorizedException("Unauthorized");
    }

    return currentUser;
  }

  public UUID getCurrentUserId() {
    return getCurrentUser().getIdUser();
  }

  public UserRole getCurrentUserRole() {
    return getCurrentUser().getRole();
  }

  public boolean hasRole(UserRole role) {
    return getCurrentUserRole() == role;
  }
}
