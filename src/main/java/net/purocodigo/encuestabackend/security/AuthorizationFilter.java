package net.purocodigo.encuestabackend.security;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Jwts;

public class AuthorizationFilter extends BasicAuthenticationFilter {

    public AuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader(SecurityConstants.HEADER_STRING);

        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(request);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {

        String token = request.getHeader(SecurityConstants.HEADER_STRING);

        if (token != null) {
            token = token.replace(SecurityConstants.TOKEN_PREFIX, "");

            String subject = Jwts.parser().setSigningKey(SecurityConstants.getTokenSecret())
                    .parseClaimsJws(token).getBody().getSubject();
            String role = Jwts.parser().setSigningKey(SecurityConstants.getTokenSecret())
                    .parseClaimsJws(token).getBody().get("role", String.class);

            if (subject != null && role != null) {
                return new UsernamePasswordAuthenticationToken(subject, null, AuthorityUtils.createAuthorityList(role));
            }

            return null;
        }

        return null;
    }

}
