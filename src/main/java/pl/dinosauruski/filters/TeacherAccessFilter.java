package pl.dinosauruski.filters;

import pl.dinosauruski.teacher.login.TeacherDTO;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/teacher/*"})
public class TeacherAccessFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession();
        TeacherDTO teacher = (TeacherDTO) session.getAttribute("loggedTeacher");

        if (teacher == null) {
            res.sendRedirect("/");
            return;
        }
        chain.doFilter(request, response);

    }

    @Override
    public void destroy() {

    }
}
