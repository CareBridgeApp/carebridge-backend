package com.carebridge.carebridge.Filter;

import com.carebridge.carebridge.Repository.UserRepo;
import com.carebridge.carebridge.Utils.JWTutil;
import com.carebridge.carebridge.entity.UserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTutil jwtutil;
    private final UserRepo userrepo;

    public JwtAuthenticationFilter(
            JWTutil jwtutil,
            UserRepo userrepo) {

        this.jwtutil = jwtutil;
        this.userrepo = userrepo;
    }
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = null;
        String userId = null;
        // 1. Get cookies from the request
        Cookie[] cookies = request.getCookies();
        // 2. Find our JWT cookie
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }
        // 3. Make sure JWT exists
        if (jwt != null) {
            try {
                // 4. Validate JWT
                if (Boolean.TRUE.equals(
                        jwtutil.validateToken(jwt))) {

                    // 5. Extract userId from JWT
                    userId = jwtutil.extractUserId(jwt);

                    if (userId != null) {

                        // 6. Convert String -> ObjectId
                        ObjectId objectId =
                                new ObjectId(userId);

                        // 7. Find user in MongoDB
                        Optional<UserDetails> userDetails =
                                userrepo.findById(objectId);

                        // 8. Check user exists
                        if (userDetails.isPresent()) {

                            // 9. Get actual user
                            UserDetails user =
                                    userDetails.get();

                            // 10. Create Authentication
                            UsernamePasswordAuthenticationToken auth =
                                    new UsernamePasswordAuthenticationToken(
                                            user,
                                            null,
                                            user.getAuthorities()
                                    );

                            // 11. Add request details
                            auth.setDetails(
                                    new WebAuthenticationDetailsSource()
                                            .buildDetails(request)
                            );

                            // 12. Store authentication
                            SecurityContextHolder
                                    .getContext()
                                    .setAuthentication(auth);
                        }
                    }
                }

            } catch (Exception e) {

                // Invalid JWT / invalid ObjectId / etc.
                SecurityContextHolder
                        .clearContext();
            }
        }

        // 13. Continue request
        filterChain.doFilter(request, response);
    }
}