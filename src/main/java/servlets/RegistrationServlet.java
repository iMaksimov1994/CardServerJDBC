package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.User;
import repository.UserRepository;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Date;


@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset utf-8");
        resp.setCharacterEncoding("UTF-8");
        try (BufferedInputStream bufferedInputStream
                     = new BufferedInputStream(req.getInputStream());
             UserRepository userRepository = new UserRepository()) {
            String reqDataUser = new String(bufferedInputStream.readAllBytes());

            ObjectMapper objectMapper = new ObjectMapper();
            User user = objectMapper.readValue(reqDataUser, User.class);
            user.setRegDate(new Date());
            if (userRepository.addUser(user)) {
                resp.getWriter().println(objectMapper.writeValueAsString(user));
            } else {
                resp.getWriter().println("User already added");
                resp.setStatus(400);
            }
        }
    }
}

