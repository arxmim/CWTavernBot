package org.nia.logic.quests.kitchen;

import org.nia.logic.lists.facts.EQuestFact;
import org.nia.logic.lists.items.EQuestItem;
import org.nia.logic.quests.IQuestEvent;
import org.nia.logic.quests.IQuestStep;
import org.nia.model.Quest;
import org.nia.model.QuestEvent;
import org.nia.model.QuestFact;
import org.nia.model.QuestItem;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Иван, 06.04.2017.
 */
public enum Cooking implements IQuestStep {
    BAY_LEAF(""
            , "Ты принес лавровый лист. Куда дальше?"
            , Collections.emptyList()) {
        @Override
        public String getCommand(String formatParam) {
            return EQuestItem.BAY_LEAF.getDesc();
        }

        @Override
        public void doWork(QuestEvent questEvent) {
            List<QuestItem> questItems = QuestItem.getAll(questEvent.getQuest());
            Optional<QuestItem> first = questItems.stream().filter(qi -> qi.getQuestItem() == EQuestItem.BAY_LEAF).findFirst();
            if (!first.isPresent()) {
                QuestItem questItem = new QuestItem();
                questItem.setItemCount(1);
                questItem.setQuest(questEvent.getQuest());
                questItem.setQuestItem(EQuestItem.BAY_LEAF);
                questItem.save();
            }
        }

        @Override
        public List<IQuestStep> getNext(Quest quest) {
            return getNextFromStart(quest);
        }
    },
    OLIVE_SOUCE(""
            , "Ты принес оливковый соус. Куда дальше?"
            , Collections.emptyList()) {
        @Override
        public String getCommand(String formatParam) {
            return EQuestItem.OLIVE_SOUCE.getDesc();
        }

        @Override
        public void doWork(QuestEvent questEvent) {
            List<QuestItem> questItems = QuestItem.getAll(questEvent.getQuest());
            Optional<QuestItem> first = questItems.stream().filter(qi -> qi.getQuestItem() == EQuestItem.OLIVE_SOUCE).findFirst();
            if (!first.isPresent()) {
                QuestItem questItem = new QuestItem();
                questItem.setItemCount(1);
                questItem.setQuest(questEvent.getQuest());
                questItem.setQuestItem(EQuestItem.OLIVE_SOUCE);
                questItem.save();
            }
        }

        @Override
        public List<IQuestStep> getNext(Quest quest) {
            return getNextFromStart(quest);
        }
    },
    BASIL_SOUCE(""
            , "Ты принес базиликовый соус. Куда дальше?"
            , Collections.emptyList()) {
        @Override
        public String getCommand(String formatParam) {
            return EQuestItem.BASIL_SOUCE.getDesc();
        }

        @Override
        public void doWork(QuestEvent questEvent) {
            List<QuestItem> questItems = QuestItem.getAll(questEvent.getQuest());
            Optional<QuestItem> first = questItems.stream().filter(qi -> qi.getQuestItem() == EQuestItem.BASIL_SOUCE).findFirst();
            if (!first.isPresent()) {
                QuestItem questItem = new QuestItem();
                questItem.setItemCount(1);
                questItem.setQuest(questEvent.getQuest());
                questItem.setQuestItem(EQuestItem.BASIL_SOUCE);
                questItem.save();
            }
        }

        @Override
        public List<IQuestStep> getNext(Quest quest) {
            return getNextFromStart(quest);
        }
    },
    ONE("1"
            , "Ты принес свежие продукты. Куда дальше?"
            , Collections.emptyList()) {
        @Override
        public List<IQuestStep> getNext(Quest quest) {
            return getNextFromStart(quest);
        }

        @Override
        public void doWork(QuestEvent questEvent) {
            addItemCount(questEvent.getQuest(), 1);
        }
    },
    TWO("2"
            , "Ты принес свежие продукты. Куда дальше?"
            , Collections.emptyList()) {
        @Override
        public List<IQuestStep> getNext(Quest quest) {
            return getNextFromStart(quest);
        }

        @Override
        public void doWork(QuestEvent questEvent) {
            addItemCount(questEvent.getQuest(), 2);
        }
    },
    THREE("3"
            , "Ты принес свежие продукты. Куда дальше?"
            , Collections.emptyList()) {
        @Override
        public List<IQuestStep> getNext(Quest quest) {
            return getNextFromStart(quest);
        }

        @Override
        public void doWork(QuestEvent questEvent) {
            addItemCount(questEvent.getQuest(), 3);
        }
    },
    FOUR("4"
            , "Ты принес свежие продукты. Куда дальше?"
            , Collections.emptyList()) {
        @Override
        public List<IQuestStep> getNext(Quest quest) {
            return getNextFromStart(quest);
        }

        @Override
        public void doWork(QuestEvent questEvent) {
            addItemCount(questEvent.getQuest(), 4);
        }
    },
    APPLE(""
            , "Сколько возьмёшь?"
            , Arrays.asList(ONE, TWO, THREE, FOUR)) {
        @Override
        public String getCommand(String formatParam) {
            return EQuestItem.APPLE.getDesc();
        }

        @Override
        public void doWork(QuestEvent questEvent) {
            QuestItem questItem = new QuestItem();
            questItem.setItemCount(0);
            questItem.setQuest(questEvent.getQuest());
            questItem.setQuestItem(EQuestItem.APPLE);
            questItem.save();
        }
    },
    PLUM(""
            , "Сколько возьмёшь?"
            , Arrays.asList(ONE, TWO, THREE, FOUR)) {
        @Override
        public String getCommand(String formatParam) {
            return EQuestItem.PLUM.getDesc();
        }

        @Override
        public void doWork(QuestEvent questEvent) {
            QuestItem questItem = new QuestItem();
            questItem.setItemCount(0);
            questItem.setQuest(questEvent.getQuest());
            questItem.setQuestItem(EQuestItem.PLUM);
            questItem.save();
        }
    },
    PEAR(""
            , "Сколько возьмёшь?"
            , Arrays.asList(ONE, TWO, THREE, FOUR)) {
        @Override
        public String getCommand(String formatParam) {
            return EQuestItem.PEAR.getDesc();
        }

        @Override
        public void doWork(QuestEvent questEvent) {
            QuestItem questItem = new QuestItem();
            questItem.setItemCount(0);
            questItem.setQuest(questEvent.getQuest());
            questItem.setQuestItem(EQuestItem.PEAR);
            questItem.save();
        }
    },
    REDIS(""
            , "Сколько возьмёшь?"
            , Arrays.asList(ONE, TWO, THREE, FOUR)) {
        @Override
        public String getCommand(String formatParam) {
            return EQuestItem.REDIS.getDesc();
        }

        @Override
        public void doWork(QuestEvent questEvent) {
            QuestItem questItem = new QuestItem();
            questItem.setItemCount(0);
            questItem.setQuest(questEvent.getQuest());
            questItem.setQuestItem(EQuestItem.REDIS);
            questItem.save();
        }
    },
    TOMATO(""
            , "Сколько возьмёшь?"
            , Arrays.asList(ONE, TWO, THREE, FOUR)) {
        @Override
        public String getCommand(String formatParam) {
            return EQuestItem.TOMATO.getDesc();
        }

        @Override
        public void doWork(QuestEvent questEvent) {
            QuestItem questItem = new QuestItem();
            questItem.setItemCount(0);
            questItem.setQuest(questEvent.getQuest());
            questItem.setQuestItem(EQuestItem.TOMATO);
            questItem.save();
        }
    },
    POTATO(""
            , "Сколько возьмёшь?"
            , Arrays.asList(ONE, TWO, THREE, FOUR)) {
        @Override
        public String getCommand(String formatParam) {
            return EQuestItem.POTATO.getDesc();
        }

        @Override
        public void doWork(QuestEvent questEvent) {
            QuestItem questItem = new QuestItem();
            questItem.setItemCount(0);
            questItem.setQuest(questEvent.getQuest());
            questItem.setQuestItem(EQuestItem.POTATO);
            questItem.save();
        }
    },
    FULL("Полную головку сыра"
            , "Ты принес сыр. Куда дальше?"
            , Collections.emptyList()) {
        @Override
        public List<IQuestStep> getNext(Quest quest) {
            return getNextFromStart(quest);
        }

        @Override
        public void doWork(QuestEvent questEvent) {
            addItemCount(questEvent.getQuest(), 4);
        }
    },
    HALF("Половину головки сыра"
            , "Ты принес сыр. Куда дальше?"
            , Collections.emptyList()) {
        @Override
        public List<IQuestStep> getNext(Quest quest) {
            return getNextFromStart(quest);
        }

        @Override
        public void doWork(QuestEvent questEvent) {
            addItemCount(questEvent.getQuest(), 2);
        }
    },
    QUOTER("Четверть головки сыра"
            , "Ты принес сыр. Куда дальше?"
            , Collections.emptyList()) {
        @Override
        public List<IQuestStep> getNext(Quest quest) {
            return getNextFromStart(quest);
        }

        @Override
        public void doWork(QuestEvent questEvent) {
            addItemCount(questEvent.getQuest(), 1);
        }
    },
    BLUE_CHEESE(""
            , "Сколько возьмёшь?"
            , Arrays.asList(QUOTER, HALF, FULL)) {
        @Override
        public String getCommand(String formatParam) {
            return EQuestItem.BLUE_CHEESE.getDesc();
        }

        @Override
        public void doWork(QuestEvent questEvent) {
            QuestItem questItem = new QuestItem();
            questItem.setItemCount(0);
            questItem.setQuest(questEvent.getQuest());
            questItem.setQuestItem(EQuestItem.BLUE_CHEESE);
            questItem.save();
        }
    },
    GOAT_CHEESE(""
            , "Сколько возьмёшь?"
            , Arrays.asList(QUOTER, HALF, FULL)) {
        @Override
        public String getCommand(String formatParam) {
            return EQuestItem.GOAT_CHEESE.getDesc();
        }

        @Override
        public void doWork(QuestEvent questEvent) {
            QuestItem questItem = new QuestItem();
            questItem.setItemCount(0);
            questItem.setQuest(questEvent.getQuest());
            questItem.setQuestItem(EQuestItem.GOAT_CHEESE);
            questItem.save();
        }
    },
    STONE_CHEESE(""
            , "Сколько возьмёшь?"
            , Arrays.asList(QUOTER, HALF, FULL)) {
        @Override
        public String getCommand(String formatParam) {
            return EQuestItem.STONE_CHEESE.getDesc();
        }

        @Override
        public void doWork(QuestEvent questEvent) {
            QuestItem questItem = new QuestItem();
            questItem.setItemCount(0);
            questItem.setQuest(questEvent.getQuest());
            questItem.setQuestItem(EQuestItem.STONE_CHEESE);
            questItem.save();
        }
    },
    GO_CHEESE("Сходить за сыром"
            , "Ты спустился в подвал за сыром. Что возьмёшь?"
            , Arrays.asList(STONE_CHEESE, GOAT_CHEESE, BLUE_CHEESE)),
    GO_VEDGETABLES("Сходить за овощами"
            , "Ты подошел к лотку с овощами. Что возьмёшь?"
            , Arrays.asList(POTATO, TOMATO, REDIS)),
    GO_FRUIT("Сходить за фруктами"
            , "Ты подошел к лотку с фруктами. Что возьмёшь?"
            , Arrays.asList(APPLE, PLUM, PEAR)),
    GO_SPICE("Сходить за соусами и специями"
            , "Ты зашел в подсобку с травами, специями и соусами. Что возьмёшь?"
            , Arrays.asList(BASIL_SOUCE, OLIVE_SOUCE, BAY_LEAF)),
    DO("Отдать продукты шеф-повару"
            , "Ты набрал правильные продукты. Шеф-повару удалось приготовить отличное блюдо и тебе достались щедрые чаевые."
            , "Ты накосячил со списком продуктов! Шеф-повару пришлось самому набирать продукты заново, причем " +
            "делал он это чудовищно медленно, как будто назло. К моменту приготовления блюда клиент уже ушел из " +
            "таверны, так что блюдо пропало впустую. Может быть, вы с шефом виноваты в равной степени, но " +
            "штраф из жалованья удержали именно с тебя.") {
        @Override
        public void doWork(QuestEvent questEvent) {
            List<QuestItem> allItems = QuestItem.getAll(questEvent.getQuest());
            List<QuestFact> allFacts = QuestFact.getAll(questEvent.getQuest());
            QuestFact questFact = allFacts.get(0);
            if (questFact.getQuestFact() == EQuestFact.KITCHEN_ELVEN_SHAURMA) {
                boolean tomatoOk = false;
                boolean cheeseOk = false;
                boolean oliveOk = false;
                boolean err = false;
                for (QuestItem questItem : allItems) {
                    if (questItem.getQuestItem() == EQuestItem.TOMATO && questItem.getItemCount() == 2) {
                        tomatoOk = true;
                    } else if (questItem.getQuestItem() == EQuestItem.GOAT_CHEESE && questItem.getItemCount() == 1) {
                        cheeseOk = true;
                    } else if (questItem.getQuestItem() == EQuestItem.OLIVE_SOUCE) {
                        oliveOk = true;
                    } else {
                        err = true;
                    }
                }
                if (tomatoOk && cheeseOk && oliveOk && !err) {
                    questEvent.setWinChance(100);
                } else {
                    questEvent.setWinChance(0);
                }
            }
            allFacts.forEach(QuestFact::delete);
            allItems.forEach(QuestItem::delete);
        }
    },
    LOOK("Посмотреть, что набрал", "", Collections.emptyList()) {
        @Override
        public String getText(Quest quest) {
            List<QuestItem> all = QuestItem.getAll(quest);
            StringBuilder sb = new StringBuilder();
            sb.append("Ты набрал:\n\n");
            all.forEach(qi-> sb.append(qi.getQuestItem().getDesc()).append(" - ").append(qi.getItemCount()).append(" ").append(qi.getQuestItem().getMeasure()).append("\n"));
            return sb.toString();
        }

        @Override
        public List<IQuestStep> getNext(Quest quest) {
            return getNextFromStart(quest);
        }
    },
    RETURN("Выложить все продукты и начать заново", "Ты выложил все продукты, теперь надо собирать заново."
            , Collections.emptyList()){
        @Override
        public void doWork(QuestEvent questEvent) {
            List<QuestItem> all = QuestItem.getAll(questEvent.getQuest());
            all.forEach(QuestItem::delete);
        }

        @Override
        public List<IQuestStep> getNext(Quest quest) {
            return getNextFromStart(quest);
        }
    },
    RECIPE("Напомнить рецепт", "", Collections.emptyList()) {
        @Override
        public String getText(Quest quest) {
            return INIT.getText(quest);
        }

        @Override
        public List<IQuestStep> getNext(Quest quest) {
            return getNextFromStart(quest);
        }
    },
    INIT("", "", Collections.emptyList()) {
        @Override
        public String getText(Quest quest) {
            List<QuestFact> all = QuestFact.getAll(quest);
            QuestFact questFact = all.get(0);
            EQuestFact eQuestFact = questFact.getQuestFact();
            String res = "Тебя попросили помочь на кухне с приготовлением особого блюда. ";
            if (eQuestFact == EQuestFact.KITCHEN_ELVEN_SHAURMA) {
                res += "Сегодня это шаурма по-эльфийски с козьим сыром. В рецепт, помимо обычных ингредиентов, " +
                        "входит 2 помидора, четверть сырной головки и фирменный оливковый соус. ";
            }
            res += "Тебе нужно принести это всё шеф-повару.";
            return res;
        }

        @Override
        public List<IQuestStep> getNext(Quest quest) {
            return getNextFromStart(quest);
        }
    };
    private List<IQuestStep> next = new ArrayList<>();
    private String text = "";
    private String command = "";
    private String goodText = "";
    private String badText = "Ты ничего не предпринял и всё пошло наперекосяк.";

    Cooking(String command, String goodText, String badText) {
        this.command = command;
        this.goodText = goodText;
        this.badText = badText;
    }

    Cooking(String command, String text, List<IQuestStep> next) {
        this.command = command;
        this.text = text;
        this.next.addAll(next);
    }

    @Override
    public String getText(Quest quest) {
        return text;
    }

    @Override
    public List<IQuestStep> getNext(Quest quest) {
        return next;
    }

    @Override
    public String getCommand(String formatParam) {
        return command;
    }

    @Override
    public String getGoodText() {
        return goodText;
    }

    @Override
    public String getBadText() {
        return badText;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public IQuestEvent getIQuest() {
        return KitchenQuest.KitchenEvent.KITCHEN_COOKING;
    }

    public List<IQuestStep> getNextFromStart(Quest quest) {
        List<IQuestStep> cookings = new ArrayList<>(Arrays.asList(DO, GO_CHEESE, GO_FRUIT, GO_VEDGETABLES, GO_SPICE, RECIPE));
        if (!QuestItem.getAll(quest).isEmpty()) {
            cookings.add(LOOK);
            cookings.add(RETURN);
        }
        return cookings;
    }

    private static void addItemCount(Quest quest, int count) {
        List<QuestItem> questItems = QuestItem.getAll(quest);
        EQuestItem eQuestItem = questItems.stream().filter(qi -> qi.getItemCount() == 0).findFirst().get().getQuestItem();
        List<QuestItem> collect = questItems.stream().filter(qi -> qi.getQuestItem() == eQuestItem).collect(Collectors.toList());
        if (collect.size() == 1) {
            QuestItem questItem = collect.get(0);
            questItem.setItemCount(count);
            questItem.save();
        } else if (collect.size() == 2) {
            QuestItem questItem0 = collect.get(0);
            QuestItem questItem1 = collect.get(1);
            if (questItem0.getItemCount() == 0) {
                questItem0.delete();
                questItem1.setItemCount(questItem1.getItemCount() + count);
                questItem1.save();
            } else {
                questItem1.delete();
                questItem0.setItemCount(questItem0.getItemCount() + count);
                questItem0.save();
            }
        }
    }
}
