package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.Card;
import repository.CardRepository;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;


@WebServlet("/cards")
public class CardServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset utf-8");
        String id = req.getParameter("id");
        String id_c = req.getParameter("id_c");
        String id_u = req.getParameter("id_u");
        try (CardRepository cardRepository = new CardRepository()) {
            ObjectMapper objectMapper = new ObjectMapper();
            if (id != null && id_c == null && id_u == null) {
                Card card = cardRepository.getCard(Integer.parseInt(id));
                if (card != null) {
                    resp.getWriter().println(objectMapper.writeValueAsString(card));
                    resp.setStatus(200);
                } else {
                    resp.setStatus(400);
                    resp.getWriter().println("There is no map with this id!");
                }
            }
            if (id_c != null && id == null && id_u == null) {
                ArrayList<Card> cards = cardRepository.getCards(Integer.parseInt(id_c));
                if (cards.size() != 0) {
                    resp.getWriter().println(objectMapper.writeValueAsString(cards));
                } else {
                    resp.setStatus(400);
                    resp.getWriter().println("There is no map with this id!");
                }
            }
            if (id_u != null && id == null && id_c == null) {
                ArrayList<Card> cardByUser = cardRepository.getCardByUser(Integer.parseInt(id_u));
                if (cardByUser.size() != 0) {
                    resp.getWriter().println(objectMapper.writeValueAsString(cardByUser));
                } else {
                    resp.setStatus(400);
                    resp.getWriter().println("There is no map with this id!");
                }
            }
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (BufferedInputStream bufferedInputStream
                     = new BufferedInputStream(req.getInputStream());
             CardRepository cardRepository = new CardRepository()) {
            String reqDataCard = new String(bufferedInputStream.readAllBytes());
            ObjectMapper objectMapper = new ObjectMapper();
            Card card = objectMapper.readValue(reqDataCard, Card.class);
            card.setCreationDate(new Date());
            if (cardRepository.addCard(card)) {
                resp.getWriter().println("Card added");
            } else {
                resp.getWriter().println("Unable to add card");
                resp.setStatus(400);
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset utf-8");
        resp.setCharacterEncoding("UTF-8");
        int id = Integer.parseInt(req.getParameter("id"));
        try (CardRepository cardRepository = new CardRepository()) {
            if (id > 0) {
                boolean deleteUserBool = cardRepository.deleteCard(id);
                if (deleteUserBool) {
                    resp.getWriter().println("Card removed");
                } else {
                    resp.setStatus(400);
                    resp.getWriter().println("Card cannot be removed");
                }
            } else {
                resp.getWriter().println("Card cannot be removed");
                resp.setStatus(400);
            }
        }
    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=utf-8");
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(req.getInputStream());
             CardRepository cardRepository = new CardRepository()) {
            String cardData = new String(bufferedInputStream.readAllBytes());
            ObjectMapper objectMapper = new ObjectMapper();
            Card card = objectMapper.readValue(cardData, Card.class);
            card.setCreationDate(new Date());
            boolean upDateCardBool = cardRepository.updateCard(card);
            if (upDateCardBool) {
                resp.getWriter().println("Card updated");
            } else {
                resp.setStatus(400);
                resp.getWriter().println("Failed to update card");
            }
            resp.getWriter().println();
        }
    }
}
