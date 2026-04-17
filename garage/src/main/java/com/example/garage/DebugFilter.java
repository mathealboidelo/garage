package com.example.garage;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * Filtre de debug temporaire — affiche CHAQUE requête HTTP reçue.
 * Permet d'identifier quel endpoint provoque le depth 501.
 * SUPPRIMER ce fichier une fois le bug identifié.
 */
@Component
public class DebugFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest  httpReq  = (HttpServletRequest)  req;
        HttpServletResponse httpRes  = (HttpServletResponse) res;

        String method = httpReq.getMethod();
        String uri    = httpReq.getRequestURI();
        System.out.println("[DEBUG-FILTER] --> " + method + " " + uri);

        chain.doFilter(req, res);

        System.out.println("[DEBUG-FILTER] <-- " + method + " " + uri + " status=" + httpRes.getStatus());
    }
}
