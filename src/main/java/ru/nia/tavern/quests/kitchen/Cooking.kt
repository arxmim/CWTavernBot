package ru.nia.tavern.quests.kitchen

import ru.nia.tavern.model.Quest
import ru.nia.tavern.model.QuestEvent
import ru.nia.tavern.model.QuestFact
import ru.nia.tavern.model.QuestItem
import ru.nia.tavern.model.types.facts.EQuestFact
import ru.nia.tavern.model.types.items.EQuestItem
import ru.nia.tavern.quests.IQuestEvent
import ru.nia.tavern.quests.IQuestStep
import java.util.*
import java.util.stream.Collectors

/**
 * @author Иван, 06.04.2017.
 */
enum class Cooking : IQuestStep {
    BAY_LEAF("", "Ты принес лавровый лист. Куда дальше?", emptyList<IQuestStep>()) {
        override fun getCommand(formatParam: String): String {
            return EQuestItem.BAY_LEAF.desc
        }

        override fun doWork(questEvent: QuestEvent) {
            val questItems = QuestItem.getAll(questEvent.quest)
            val first = questItems.stream().filter { qi -> qi.questItem === EQuestItem.BAY_LEAF }.findFirst()
            if (!first.isPresent) {
                val questItem = QuestItem()
                questItem.itemCount = 1
                questItem.quest = questEvent.quest
                questItem.questItem = EQuestItem.BAY_LEAF
                questItem.save()
            }
        }

        override fun getNext(quest: Quest): List<IQuestStep> {
            return getNextFromStart(quest)
        }
    },
    OLIVE_SOUCE("", "Ты принес оливковый соус. Куда дальше?", emptyList<IQuestStep>()) {
        override fun getCommand(formatParam: String): String {
            return EQuestItem.OLIVE_SOUCE.desc
        }

        override fun doWork(questEvent: QuestEvent) {
            val questItems = QuestItem.getAll(questEvent.quest)
            val first = questItems.stream().filter { qi -> qi.questItem === EQuestItem.OLIVE_SOUCE }.findFirst()
            if (!first.isPresent) {
                val questItem = QuestItem()
                questItem.itemCount = 1
                questItem.quest = questEvent.quest
                questItem.questItem = EQuestItem.OLIVE_SOUCE
                questItem.save()
            }
        }

        override fun getNext(quest: Quest): List<IQuestStep> {
            return getNextFromStart(quest)
        }
    },
    BASIL_SOUCE("", "Ты принес базиликовый соус. Куда дальше?", emptyList<IQuestStep>()) {
        override fun getCommand(formatParam: String): String {
            return EQuestItem.BASIL_SOUCE.desc
        }

        override fun doWork(questEvent: QuestEvent) {
            val questItems = QuestItem.getAll(questEvent.quest)
            val first = questItems.stream().filter { qi -> qi.questItem === EQuestItem.BASIL_SOUCE }.findFirst()
            if (!first.isPresent) {
                val questItem = QuestItem()
                questItem.itemCount = 1
                questItem.quest = questEvent.quest
                questItem.questItem = EQuestItem.BASIL_SOUCE
                questItem.save()
            }
        }

        override fun getNext(quest: Quest): List<IQuestStep> {
            return getNextFromStart(quest)
        }
    },
    ONE("1", "Ты принес свежие продукты. Куда дальше?", emptyList<IQuestStep>()) {
        override fun getNext(quest: Quest): List<IQuestStep> {
            return getNextFromStart(quest)
        }

        override fun doWork(questEvent: QuestEvent) {
            addItemCount(questEvent.quest, 1)
        }
    },
    TWO("2", "Ты принес свежие продукты. Куда дальше?", emptyList<IQuestStep>()) {
        override fun getNext(quest: Quest): List<IQuestStep> {
            return getNextFromStart(quest)
        }

        override fun doWork(questEvent: QuestEvent) {
            addItemCount(questEvent.quest, 2)
        }
    },
    THREE("3", "Ты принес свежие продукты. Куда дальше?", emptyList<IQuestStep>()) {
        override fun getNext(quest: Quest): List<IQuestStep> {
            return getNextFromStart(quest)
        }

        override fun doWork(questEvent: QuestEvent) {
            addItemCount(questEvent.quest, 3)
        }
    },
    FOUR("4", "Ты принес свежие продукты. Куда дальше?", emptyList<IQuestStep>()) {
        override fun getNext(quest: Quest): List<IQuestStep> {
            return getNextFromStart(quest)
        }

        override fun doWork(questEvent: QuestEvent) {
            addItemCount(questEvent.quest, 4)
        }
    },
    APPLE("", "Сколько возьмёшь?", Arrays.asList<IQuestStep>(ONE, TWO, THREE, FOUR)) {
        override fun getCommand(formatParam: String): String {
            return EQuestItem.APPLE.desc
        }

        override fun doWork(questEvent: QuestEvent) {
            val questItem = QuestItem()
            questItem.itemCount = 0
            questItem.quest = questEvent.quest
            questItem.questItem = EQuestItem.APPLE
            questItem.save()
        }
    },
    PLUM("", "Сколько возьмёшь?", Arrays.asList<IQuestStep>(ONE, TWO, THREE, FOUR)) {
        override fun getCommand(formatParam: String): String {
            return EQuestItem.PLUM.desc
        }

        override fun doWork(questEvent: QuestEvent) {
            val questItem = QuestItem()
            questItem.itemCount = 0
            questItem.quest = questEvent.quest
            questItem.questItem = EQuestItem.PLUM
            questItem.save()
        }
    },
    PEAR("", "Сколько возьмёшь?", Arrays.asList<IQuestStep>(ONE, TWO, THREE, FOUR)) {
        override fun getCommand(formatParam: String): String {
            return EQuestItem.PEAR.desc
        }

        override fun doWork(questEvent: QuestEvent) {
            val questItem = QuestItem()
            questItem.itemCount = 0
            questItem.quest = questEvent.quest
            questItem.questItem = EQuestItem.PEAR
            questItem.save()
        }
    },
    REDIS("", "Сколько возьмёшь?", Arrays.asList<IQuestStep>(ONE, TWO, THREE, FOUR)) {
        override fun getCommand(formatParam: String): String {
            return EQuestItem.REDIS.desc
        }

        override fun doWork(questEvent: QuestEvent) {
            val questItem = QuestItem()
            questItem.itemCount = 0
            questItem.quest = questEvent.quest
            questItem.questItem = EQuestItem.REDIS
            questItem.save()
        }
    },
    TOMATO("", "Сколько возьмёшь?", Arrays.asList<IQuestStep>(ONE, TWO, THREE, FOUR)) {
        override fun getCommand(formatParam: String): String {
            return EQuestItem.TOMATO.desc
        }

        override fun doWork(questEvent: QuestEvent) {
            val questItem = QuestItem()
            questItem.itemCount = 0
            questItem.quest = questEvent.quest
            questItem.questItem = EQuestItem.TOMATO
            questItem.save()
        }
    },
    POTATO("", "Сколько возьмёшь?", Arrays.asList<IQuestStep>(ONE, TWO, THREE, FOUR)) {
        override fun getCommand(formatParam: String): String {
            return EQuestItem.POTATO.desc
        }

        override fun doWork(questEvent: QuestEvent) {
            val questItem = QuestItem()
            questItem.itemCount = 0
            questItem.quest = questEvent.quest
            questItem.questItem = EQuestItem.POTATO
            questItem.save()
        }
    },
    FULL("Полную головку сыра", "Ты принес сыр. Куда дальше?", emptyList<IQuestStep>()) {
        override fun getNext(quest: Quest): List<IQuestStep> {
            return getNextFromStart(quest)
        }

        override fun doWork(questEvent: QuestEvent) {
            addItemCount(questEvent.quest, 4)
        }
    },
    HALF("Половину головки сыра", "Ты принес сыр. Куда дальше?", emptyList<IQuestStep>()) {
        override fun getNext(quest: Quest): List<IQuestStep> {
            return getNextFromStart(quest)
        }

        override fun doWork(questEvent: QuestEvent) {
            addItemCount(questEvent.quest, 2)
        }
    },
    QUOTER("Четверть головки сыра", "Ты принес сыр. Куда дальше?", emptyList<IQuestStep>()) {
        override fun getNext(quest: Quest): List<IQuestStep> {
            return getNextFromStart(quest)
        }

        override fun doWork(questEvent: QuestEvent) {
            addItemCount(questEvent.quest, 1)
        }
    },
    BLUE_CHEESE("", "Сколько возьмёшь?", Arrays.asList<IQuestStep>(QUOTER, HALF, FULL)) {
        override fun getCommand(formatParam: String): String {
            return EQuestItem.BLUE_CHEESE.desc
        }

        override fun doWork(questEvent: QuestEvent) {
            val questItem = QuestItem()
            questItem.itemCount = 0
            questItem.quest = questEvent.quest
            questItem.questItem = EQuestItem.BLUE_CHEESE
            questItem.save()
        }
    },
    GOAT_CHEESE("", "Сколько возьмёшь?", Arrays.asList<IQuestStep>(QUOTER, HALF, FULL)) {
        override fun getCommand(formatParam: String): String {
            return EQuestItem.GOAT_CHEESE.desc
        }

        override fun doWork(questEvent: QuestEvent) {
            val questItem = QuestItem()
            questItem.itemCount = 0
            questItem.quest = questEvent.quest
            questItem.questItem = EQuestItem.GOAT_CHEESE
            questItem.save()
        }
    },
    STONE_CHEESE("", "Сколько возьмёшь?", Arrays.asList<IQuestStep>(QUOTER, HALF, FULL)) {
        override fun getCommand(formatParam: String): String {
            return EQuestItem.STONE_CHEESE.desc
        }

        override fun doWork(questEvent: QuestEvent) {
            val questItem = QuestItem()
            questItem.itemCount = 0
            questItem.quest = questEvent.quest
            questItem.questItem = EQuestItem.STONE_CHEESE
            questItem.save()
        }
    },
    GO_CHEESE("Сходить за сыром", "Ты спустился в подвал за сыром. Что возьмёшь?", Arrays.asList<IQuestStep>(STONE_CHEESE, GOAT_CHEESE, BLUE_CHEESE)),
    GO_VEDGETABLES("Сходить за овощами", "Ты подошел к лотку с овощами. Что возьмёшь?", Arrays.asList<IQuestStep>(POTATO, TOMATO, REDIS)),
    GO_FRUIT("Сходить за фруктами", "Ты подошел к лотку с фруктами. Что возьмёшь?", Arrays.asList<IQuestStep>(APPLE, PLUM, PEAR)),
    GO_SPICE("Сходить за соусами и специями", "Ты зашел в подсобку с травами, специями и соусами. Что возьмёшь?", Arrays.asList<IQuestStep>(BASIL_SOUCE, OLIVE_SOUCE, BAY_LEAF)),
    DO("Отдать продукты шеф-повару", "Ты набрал правильные продукты. Шеф-повару удалось приготовить отличное блюдо и тебе достались щедрые чаевые.", "Ты накосячил со списком продуктов! Шеф-повару пришлось самому набирать продукты заново, причем " +
            "делал он это чудовищно медленно, как будто назло. К моменту приготовления блюда клиент уже ушел из " +
            "таверны, так что блюдо пропало впустую. Может быть, вы с шефом виноваты в равной степени, но " +
            "штраф из жалованья удержали именно с тебя.") {
        override fun doWork(questEvent: QuestEvent) {
            val allItems = QuestItem.getAll(questEvent.quest)
            val allFacts = QuestFact.getAll(questEvent.quest)
            val questFact = allFacts[0]
            if (questFact.questFact == EQuestFact.KITCHEN_ELVEN_SHAURMA) {
                var tomatoOk = false
                var cheeseOk = false
                var oliveOk = false
                var err = false
                for (questItem in allItems) {
                    if (questItem.questItem === EQuestItem.TOMATO && questItem.itemCount == 2) {
                        tomatoOk = true
                    } else if (questItem.questItem === EQuestItem.GOAT_CHEESE && questItem.itemCount == 1) {
                        cheeseOk = true
                    } else if (questItem.questItem === EQuestItem.OLIVE_SOUCE) {
                        oliveOk = true
                    } else {
                        err = true
                    }
                }
                if (tomatoOk && cheeseOk && oliveOk && !err) {
                    questEvent.winChance = 100
                } else {
                    questEvent.winChance = 0
                }
            }
        }
    },
    LOOK("Посмотреть, что набрал", "", emptyList<IQuestStep>()) {
        override fun getText(quest: Quest): String {
            val all = QuestItem.getAll(quest)
            val sb = StringBuilder()
            sb.append("Ты набрал:\n\n")
            all.forEach { qi -> sb.append(qi.questItem.desc).append(" - ").append(qi.itemCount).append(" ").append(qi.questItem.measure).append("\n") }
            return sb.toString()
        }

        override fun getNext(quest: Quest): List<IQuestStep> {
            return getNextFromStart(quest)
        }
    },
    RETURN("Выложить все продукты и начать заново", "Ты выложил все продукты, теперь надо собирать заново.", emptyList<IQuestStep>()) {
        override fun doWork(questEvent: QuestEvent) {
            val all = QuestItem.getAll(questEvent.quest)
            all.forEach(Consumer<QuestItem> { it.delete() })
        }

        override fun getNext(quest: Quest): List<IQuestStep> {
            return getNextFromStart(quest)
        }
    },
    RECIPE("Напомнить рецепт", "", emptyList<IQuestStep>()) {
        override fun getText(quest: Quest): String {
            return INIT.getText(quest)
        }

        override fun getNext(quest: Quest): List<IQuestStep> {
            return getNextFromStart(quest)
        }
    },
    INIT("", "", emptyList<IQuestStep>()) {
        override fun getText(quest: Quest): String {
            val all = QuestFact.getAll(quest)
            val questFact = all[0]
            val eQuestFact = questFact.questFact
            var res = "Тебя попросили помочь на кухне с приготовлением особого блюда. "
            if (eQuestFact == EQuestFact.KITCHEN_ELVEN_SHAURMA) {
                res += "Сегодня это шаурма по-эльфийски с козьим сыром. В рецепт, помимо обычных ингредиентов, " + "входит 2 помидора, четверть сырной головки и фирменный оливковый соус. "
            }
            res += "Тебе нужно принести это всё шеф-повару."
            return res
        }

        override fun getNext(quest: Quest): List<IQuestStep> {
            return getNextFromStart(quest)
        }
    };

    private val next = ArrayList<IQuestStep>()
    private val text = ""
    private var command = ""
    private val goodText = ""
    private val badText = "Ты ничего не предпринял и всё пошло наперекосяк."

    override val name: String
        get() = name

    override val iQuest: IQuestEvent
        get() = KitchenQuest.KitchenEvent.KITCHEN_COOKING

    private constructor(command: String, goodText: String, badText: String) {
        this.command = command
        this.goodText = goodText
        this.badText = badText
    }

    private constructor(command: String, text: String, next: List<IQuestStep>) {
        this.command = command
        this.text = text
        this.next.addAll(next)
    }

    override fun getText(quest: Quest): String {
        return text
    }

    override fun getNext(quest: Quest): List<IQuestStep> {
        return next
    }

    override fun getCommand(formatParam: String): String {
        return command
    }

    override fun getGoodText(quest: Quest): String {
        return goodText
    }

    override fun getBadText(quest: Quest): String {
        return badText
    }

    fun getNextFromStart(quest: Quest): List<IQuestStep> {
        val cookings = ArrayList<IQuestStep>(Arrays.asList(DO, GO_CHEESE, GO_FRUIT, GO_VEDGETABLES, GO_SPICE, RECIPE))
        if (!QuestItem.getAll(quest).isEmpty()) {
            cookings.add(LOOK)
            cookings.add(RETURN)
        }
        return cookings
    }

    private fun addItemCount(quest: Quest, count: Int) {
        val questItems = QuestItem.getAll(quest)
        val eQuestItem: EQuestItem
        val itemOptional = questItems.stream().filter { qi -> qi.itemCount == 0 }.findFirst()
        if (itemOptional.isPresent) {
            eQuestItem = itemOptional.get().questItem
        } else {
            return
        }
        val collect = questItems.stream().filter { qi -> qi.questItem === eQuestItem }.collect<List<QuestItem>, Any>(Collectors.toList())
        if (collect.size == 1) {
            val questItem = collect[0]
            questItem.itemCount = count
            questItem.save()
        } else if (collect.size == 2) {
            val questItem0 = collect[0]
            val questItem1 = collect[1]
            if (questItem0.itemCount == 0) {
                questItem0.delete()
                questItem1.itemCount = questItem1.itemCount + count
                questItem1.save()
            } else {
                questItem1.delete()
                questItem0.itemCount = questItem0.itemCount + count
                questItem0.save()
            }
        }
    }

    override fun doFinal(questEvent: QuestEvent) {
        val allItems = QuestItem.getAll(questEvent.quest)
        val allFacts = QuestFact.getAll(questEvent.quest)
        allFacts.forEach(Consumer<QuestFact> { it.delete() })
        allItems.forEach(Consumer<QuestItem> { it.delete() })
    }
}
