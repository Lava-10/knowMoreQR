package com.knowMoreQR.server.config;

import com.knowMoreQR.server.auth.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtil.validateToken(jwt)) {
                Claims claims = Jwts.parserBuilder()
                                   .setSigningKey(jwtUtil.getSigningKey()) // Use getSigningKey() from JwtUtil
                                   .build()
                                   .parseClaimsJws(jwt)
                                   .getBody();
                
                String email = claims.getSubject();
                Long userId = claims.get("userId", Long.class);
                String userType = claims.get("userType", String.class);

                if (email != null && userId != null && userType != null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    
                    // Check if user details match token claims (optional but good practice)
                    if (userDetails != null) {
                         UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        
                        // Store userId and userType in authentication details
                        Map<String, Object> details = new HashMap<>();
                        details.put("userId", userId);
                        details.put("userType", userType);
                        WebAuthenticationDetailsSource detailsSource = new WebAuthenticationDetailsSource();
                        // Combine default details with our custom details (less standard, might override)
                        // A more standard way is creating a custom Authentication object or storing in Principal
                        // Let's try putting our map into the standard details object for simplicity here
                        authentication.setDetails(details); 
                        // Alternative: authentication.setDetails(new CustomAuthDetails(request, userId, userType));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        logger.debug("Set security context for user: {}, ID: {}, Type: {}", email, userId, userType);
                    }
                } else {
                    logger.warn("JWT token is missing required claims (userId, userType) for email: {}", email);
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
            // Optionally clear context if authentication failed
            // SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        // logger.trace("No JWT token found in Authorization header"); // Reduce noise
        return null;
    }
    
    // If using a custom details object (Alternative approach):
    /*
    public static class CustomAuthDetails extends WebAuthenticationDetails {
        private final Long userId;
        private final String userType;

        public CustomAuthDetails(HttpServletRequest request, Long userId, String userType) {
            super(request);
            this.userId = userId;
            this.userType = userType;
        }

        public Long getUserId() { return userId; }
        public String getUserType() { return userType; }
        // Add equals/hashCode if needed
    }
    */
    
    // Need access to the signing key from JwtUtil - add a getter there
} 