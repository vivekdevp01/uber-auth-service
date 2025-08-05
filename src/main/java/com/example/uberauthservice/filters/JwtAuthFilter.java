package com.example.uberauthservice.filters;

import com.example.uberauthservice.services.JwtService;
import com.example.uberauthservice.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {


    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private final RequestMatcher uriMatcher=
            new AntPathRequestMatcher("/api/v1/auth/validate", HttpMethod.GET.name());

    private final JwtService jwtService;
    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // Bypass filter for public URLs
        if (path.startsWith("/api/v1/auth/signup") || path.startsWith("/api/v1/auth/signin")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token=null;
        if(request.getCookies()!=null){
            for(Cookie cookie : request.getCookies()){
                if(cookie.getName().equals("jwtToken")){
                    token=cookie.getValue();
                }
            }
         }
         if(token==null){
//             user has not provided any jwt token hence request should not go forward
         response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
         return;
         }
         System.out.println("incoming token"+token);
         String email=jwtService.extractEmail(token);
         System.out.println("email"+email);

         if(email!=null){
             UserDetails userDetails=userDetailsService.loadUserByUsername(email);
             if(jwtService.validateToken(token,userDetails.getUsername())){
                 UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities());
                 usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                 SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
             }
         }
         System.out.println("forwarding req");
         filterChain.doFilter(request,response);


    }
//         @Override
//        protected boolean shouldNotFilter(HttpServletRequest request){
//             RequestMatcher matcher=new NegatedRequestMatcher(uriMatcher);
//             return matcher.matches(request);
//        }
}
