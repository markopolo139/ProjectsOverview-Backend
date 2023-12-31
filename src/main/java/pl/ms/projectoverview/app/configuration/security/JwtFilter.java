package pl.ms.projectoverview.app.configuration.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.ms.projectoverview.app.exceptions.UserNotFoundException;
import pl.ms.projectoverview.app.services.UserService;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {

    private final Logger mLogger = LogManager.getLogger();
    private final TokenService mTokenService;
    private final UserService mUserService;

    public JwtFilter(TokenService tokenService, UserService userService) {
        mTokenService = tokenService;
        mUserService = userService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        Integer id = mTokenService.extractIdFromToken();
        if (id != null) {
            try {
                authenticateFromId(id, request);
            } catch (UserNotFoundException e) {
                mLogger.warn("User not found in database, error in jwtFilter");
                throw new RuntimeException(e);
            }
        }
        filterChain.doFilter(request, response);
    }

    private void authenticateFromId(int userId, HttpServletRequest request) throws UserNotFoundException {
        UserDetails user = mUserService.loadUserById(userId);

        AbstractAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        auth.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
