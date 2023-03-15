package servlets;


import com.fasterxml.jackson.databind.ObjectMapper;
import model.Category;
import repository.CategoryRepository;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet("/categories")
public class CategoryServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset utf-8");
        String id = req.getParameter("id");
        String id_u = req.getParameter("id_u");
        try (CategoryRepository categoryRepository = new CategoryRepository()) {
            ObjectMapper objectMapper = new ObjectMapper();
            if (id != null && id_u == null) {

                Category category = categoryRepository.getCategory(Integer.parseInt(id));
                if (category != null) {
                    resp.getWriter().println(objectMapper.writeValueAsString(category));

                } else {
                    resp.setStatus(400);
                    resp.getWriter().println("There is no category with this id!");
                }
            }
            if (id == null && id_u != null) {
                ArrayList<Category> categories = categoryRepository.getCategories(Integer.parseInt(id_u));
                if (categories.size() != 0) {
                    resp.getWriter().println(objectMapper.writeValueAsString(categories));
                } else {
                    resp.setStatus(400);
                    resp.getWriter().println("There is no category with this id!");
                }
            }
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset utf-8");
        try (BufferedInputStream bufferedInputStream
                     = new BufferedInputStream(req.getInputStream());
             CategoryRepository categoryRepository = new CategoryRepository()) {
            String reqDataUser = new String(bufferedInputStream.readAllBytes());

            ObjectMapper objectMapper = new ObjectMapper();
            Category category = objectMapper.readValue(reqDataUser, Category.class);

            if (categoryRepository.addCategory(category)) {
                resp.getWriter().println("Category added");
                resp.setStatus(200);
            } else {
                resp.getWriter().println("Unable to add category");
                resp.setStatus(400);
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=utf-8");
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(req.getInputStream());
             CategoryRepository categoryRepository = new CategoryRepository()) {
            String categoryData = new String(bufferedInputStream.readAllBytes());
            ObjectMapper objectMapper = new ObjectMapper();
            Category category = objectMapper.readValue(categoryData, Category.class);
            boolean upDateCategoryBool = categoryRepository.updateCategory(category);
            if (upDateCategoryBool) {
                resp.getWriter().println("Category updated");
                resp.setStatus(200);
            } else {
                resp.setStatus(400);
                resp.getWriter().println("Failed to update category");
            }
            resp.getWriter().println();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset utf-8");
        resp.setCharacterEncoding("UTF-8");
        String id = req.getParameter("id");
        try (CategoryRepository categoryRepository = new CategoryRepository()) {
            ObjectMapper objectMapper = new ObjectMapper();
            if (id != null) {
                boolean deleteCategoryBool = categoryRepository.deleteCategory(Integer.parseInt(id));
                if (deleteCategoryBool) {
                    resp.setStatus(200);
                    resp.getWriter().println("Category deleted");
                } else {
                    resp.setStatus(400);
                    resp.getWriter().println("Category with this id does not exist");
                }
            }
        }
    }
}

