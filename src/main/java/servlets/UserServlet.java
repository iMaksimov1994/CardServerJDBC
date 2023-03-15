package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.User;
import repository.UserRepository;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;


@WebServlet("/users")
public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset utf-8");
        String id = req.getParameter("id");
        try (UserRepository userRepository = new UserRepository()) {
            ObjectMapper objectMapper = new ObjectMapper();
            if (id != null) {

                User user = userRepository.getUser(Integer.parseInt(id));
                if (user != null) {
                    resp.getWriter().println(objectMapper.writeValueAsString(user));

                } else {
                    resp.setStatus(400);
                    resp.getWriter().println("There is no user with this id!");
                }
            } else {
                ArrayList<User> users = userRepository.getUsers();
                if (users.size() == 0) {
                    resp.setStatus(400);
                    resp.getWriter().println("User list is empty");
                } else {
                    resp.setStatus(200);
                    resp.getWriter().println(objectMapper.writeValueAsString(users));
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset utf-8");
        resp.setCharacterEncoding("UTF-8");
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        try (UserRepository userRepository = new UserRepository()) {
            int idU = userRepository.checkUser(login, password);
            if (idU > 0) {
                resp.getWriter().print(idU);
                resp.setStatus(200);
            } else {
                resp.setStatus(400);
                resp.getWriter().println(0);
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset utf-8");
        resp.setCharacterEncoding("UTF-8");
        String id = req.getParameter("id");
        try (UserRepository userRepository = new UserRepository()) {
            if (id != null) {
                boolean deleteUserBool = userRepository.deleteUser(Integer.parseInt(id));
                if (deleteUserBool) {
                    resp.getWriter().println("User deleted");
                } else {
                    resp.setStatus(400);
                    resp.getWriter().println("User with this id does not exist");
                }
            }
        }
    }
}
