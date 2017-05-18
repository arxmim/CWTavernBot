package org.nia.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.nia.bots.CWTavernBot;
import org.nia.db.HibernateConfig;
import org.nia.logic.ServingMessage;
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author Иван, 08.05.2017.
 */
@Entity
@Setter
@Getter
@Table(name = "cwt_Voting")
public class Voting {
    @Column
    @Id
    @GeneratedValue
    private Integer publicID;
    @Column
    private String text;
    @Column
    private Date startTime;

    @SuppressWarnings("unchecked")
    public static Voting getByID(Integer publicID) {
        Voting res = null;
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            res = session.get(Voting.class, publicID);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    public boolean save() {
        boolean res = false;
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.saveOrUpdate(this);
            tx.commit();
            session.refresh(this);
            res = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }

    public void start(User user) {
        SessionFactory factory = HibernateConfig.getSessionFactory();
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            this.startTime = new Date();
            session.saveOrUpdate(this);
            tx.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            List<VoteOption> voteOptions = VoteOption.getAll(this.getPublicID());
            if (voteOptions.isEmpty()) {
                CWTavernBot.INSTANCE.sendMessage(ServingMessage.getTimedMessage(user, "Нет ни одного варианта для голосования!"));
            } else {
//                SendMessage sendMessage = ServingMessage.getTournamentMessage(text);
                SendMessage sendMessage = ServingMessage.getTimedMessage(user, text);
                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
                for (VoteOption vo : voteOptions) {
                    List<InlineKeyboardButton> row = new ArrayList<>();
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setText(vo.getText() + ": 0");
                    button.setCallbackData("@votingBot" + String.valueOf(vo.getPublicID()));
                    row.add(button);
                    keyboard.add(row);
                }
                inlineKeyboardMarkup.setKeyboard(keyboard);
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);
                try {
                    CWTavernBot.INSTANCE.sendMessage(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void processVote(CallbackQuery callbackQuery, User user) {
        if  (user == null || user.getDrinkedTotal() < 60) {
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
            answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
            answerCallbackQuery.setText("Ты слишком мало пьешь в таверне и не имеешь права голоса!");
            try {
                CWTavernBot.INSTANCE.answerCallbackQuery(answerCallbackQuery);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return;
        }
        String strOption = StringUtils.substringAfter(callbackQuery.getData(), "@votingBot");
        VoteOption option = VoteOption.getByID(VoteOption.class, Integer.valueOf(strOption));
        List<VoteUser> byUser = VoteUser.getByUser(user.getUserID(), option.getVoting().publicID);
        if (byUser.size() > 1) {
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
            answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
            answerCallbackQuery.setText("Ты уже проголосовал дважды!");
            try {
                CWTavernBot.INSTANCE.answerCallbackQuery(answerCallbackQuery);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return;
        } else if (byUser.size() == 1 && byUser.get(0).getVoteOption().equals(option)) {
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
            answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
            answerCallbackQuery.setText("Ты уже проголосовал за этого кандидата, выбери другого!");
            try {
                CWTavernBot.INSTANCE.answerCallbackQuery(answerCallbackQuery);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return;
        }
        VoteUser nVU = new VoteUser();
        nVU.setUser(user);
        nVU.setVoteOption(option);
        nVU.setVoting(option.getVoting());
        nVU.save();
        List<VoteUser> voteUsers = VoteUser.getAll(option.getVoting().publicID);
        List<VoteOption> allOptions = VoteOption.getAll(option.getVoting().publicID);

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageText.setChatId(callbackQuery.getMessage().getChatId());
        editMessageText.setText(callbackQuery.getMessage().getText());

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        HashMap<VoteOption, Integer> map = new HashMap<>();
        allOptions.forEach(opt -> map.put(opt, 0));
        for (VoteUser vu : voteUsers) {
            map.put(vu.getVoteOption(), map.get(vu.getVoteOption()) + 1);
        }
        map.entrySet().forEach(e -> {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(e.getKey().getText() + ": " + e.getValue());
            button.setCallbackData("@votingBot" + String.valueOf(e.getKey().getPublicID()));
            row.add(button);
            keyboard.add(row);
        });
        inlineKeyboardMarkup.setKeyboard(keyboard);
        editMessageText.setReplyMarkup(inlineKeyboardMarkup);
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
        answerCallbackQuery.setText("Голос принят!");
            try {
                CWTavernBot.INSTANCE.answerCallbackQuery(answerCallbackQuery);
                CWTavernBot.INSTANCE.editMessageText(editMessageText);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
    }

    @Override
    public String toString() {
        return "Voting{" +
                "publicID=" + publicID +
                ", text='" + text + '\'' +
                '}';
    }
}
