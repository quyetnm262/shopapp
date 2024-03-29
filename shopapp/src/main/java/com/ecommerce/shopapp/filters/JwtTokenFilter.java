package com.ecommerce.shopapp.filters;

import com.ecommerce.shopapp.models.User;
import com.ecommerce.shopapp.components.JwtTokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    @Value("${api.prefix}")
    private String prefix;

    private final UserDetailsService userDetailsService;

    private final JwtTokenUtils jwtTokenUtils;


    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            if (isBypassToken(request)){
                //enable bypass
                filterChain.doFilter(request, response);
                return;
            }


            final String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            }

            final String token = authHeader.substring(7);
            final String phoneNumber = jwtTokenUtils.extractPhoneNumber(token);
            if (phoneNumber != null
                    && SecurityContextHolder.getContext().getAuthentication() == null){

                User userDetails = (User) userDetailsService.loadUserByUsername(phoneNumber);
                if (jwtTokenUtils.validateToken(token, userDetails)){
                    UsernamePasswordAuthenticationToken authenticationToken
                            = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }

            }

            final List<Pair<String, String>> bypassTokens = Arrays.asList(
                    Pair.of(prefix+"/categories", "GET"),
//                    Pair.of(prefix+"/orders", "GET"),
                    Pair.of(prefix+"/products", "GET"),
//                    Pair.of(prefix+"/products", "POST"),
                    Pair.of(prefix+"/users/login", "POST"),
                    Pair.of(prefix+"/users/register", "POST"),
                    Pair.of(prefix+"/roles", "GET"),
                    Pair.of(prefix+"/products/generateFakerProducts", "POST")

            );
            for (Pair<String, String> bypassToken: bypassTokens){
                if (request.getServletPath().contains(bypassToken.getFirst())
                        && request.getMethod().equals(bypassToken.getSecond())){
                    filterChain.doFilter(request, response);
                }
            }

            filterChain.doFilter(request,response);

        }catch (Exception e){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }

    }

    private boolean isBypassToken(@NotNull HttpServletRequest request){
        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                Pair.of(prefix+"/categories", "GET"),
                Pair.of(prefix+"/products", "GET"),
//                Pair.of(prefix+"/orders", "GET"),
//                Pair.of(prefix+"/products", "POST"),
                Pair.of(prefix+"/users/login", "POST"),
                Pair.of(prefix+"/users/register", "POST"),
                Pair.of(prefix+"/roles", "GET"),
                Pair.of(prefix+"/products/generateFakerProducts", "POST")

        );
        String requestPath = request.getServletPath();
        String requestMethod = request.getMethod();

        if (requestPath.equals(String.format("%s/orders", prefix))
        && requestMethod.equals("GET")){
            return true;
        }
        for (Pair<String, String> bypassToken: bypassTokens){
            if (request.getServletPath().contains(bypassToken.getFirst())
                    && request.getMethod().equals(bypassToken.getSecond())){
                return true;

            }
        }
        return false;
    }
}
