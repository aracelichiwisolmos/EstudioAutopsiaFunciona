/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hospital.filtro;

import hospital.modelo.usuario;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author dell
 */
public class filtro implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession();
        resp.setHeader("Cache-Control", "no-cache");
        usuario userInSession = (usuario) session.getAttribute("usuario");

        if (userInSession == null) {
            req.getRequestDispatcher("/faces/Views/login.xhtml").forward(request, response);
            return;
        } else {
            String[] path = req.getRequestURL().toString().split("/");
            if (userInSession.getTipo().equals("A")) {
                if (!(path[path.length - 1].equals("actualizar_u.xhtml") || path[path.length - 1].equals("usuario.xhtml") || path[path.length - 1].equals("cambiarPass.xhtml"))) {
                    req.getRequestDispatcher("/faces/Views/denegado.xhtml").forward(request, response);
                    return;
                }

            } else {
                if (userInSession.getTipo().equals("E")) {
                    if (!(path[path.length - 1].equals("actualizar_e.xhtml") || path[path.length - 1].equals("consultar_encuesta.xhtml") || path[path.length - 1].equals("encuesta.xhtml") || path[path.length - 1].equals("modelo.xhtml") || path[path.length - 1].equals("modeloEPM.xhtml") || path[path.length - 1].equals("modeloSG.xhtml") || path[path.length - 1].equals("modeloBayes.xhtml") || path[path.length - 1].equals("datasets.xhtml") || path[path.length - 1].equals("algorithms.xml") || path[path.length - 1].equals("graficas.xhtml"))) {
                        req.getRequestDispatcher("/faces/Views/denegado.xhtml").forward(request, response);
                        return;
                    }

                }

            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

}
