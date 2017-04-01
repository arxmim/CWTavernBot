package org.nia.logic.commands;

import org.apache.commons.lang3.tuple.Pair;
import org.nia.bots.CWTavernBot;
import org.nia.logic.ServingMessage;
import org.nia.model.DrinkPrefs;
import org.nia.model.TournamentUsers;
import org.nia.model.User;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author IANazarov
 */
public enum ArenaCommands implements Commands {
    WEAPON("") {
        @Override
        public boolean isApplicable(Message message) {
            TournamentUsers currentByUserID = TournamentUsers.getCurrentByUserID(message.getFrom().getId());
            return currentByUserID != null && currentByUserID.InFight() && Weapon.getByName(message.getText()) != null;
        }

        @Override
        public String apply(Message message) {
            TournamentUsers currentByUserID = TournamentUsers.getCurrentByUserID(message.getFrom().getId());
            User user = currentByUserID.getUser();
            Weapon weapon = Weapon.getByName(message.getText());
            currentByUserID.setScore(weapon.getNumber());
            currentByUserID.save();

            try {
                CWTavernBot.INSTANCE.sendMessage(ServingMessage.getTournamentMessage(String.format(currentByUserID.getTournament().getType().getStartPhrase(), user)));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
//            Pair<TournamentUsers, TournamentUsers> twoUsers = TournamentUsers.getTwoUsers(currentByUserID.getTournament());
//            TournamentUsers left = twoUsers.getLeft();
//            TournamentUsers right = twoUsers.getRight();
//            Weapon leftWep = Weapon.getByNumber(left.getScore());
//            Weapon rightWep = Weapon.getByNumber(right.getScore());
//            if (leftWep != null && rightWep != null) {
//                try {
//                    CWTavernBot.INSTANCE.sendMessage(ServingMessage.getTournamentMessage(String.format(leftWep.getReadyPhrase(), left.getUser(), left.getUser().roundStatToString(leftWep.getStat(left.getUser())))));
//                    CWTavernBot.INSTANCE.sendMessage(ServingMessage.getTournamentMessage(String.format(rightWep.getReadyPhrase(), right.getUser(), right.getUser().roundStatToString(rightWep.getStat(right.getUser())))));
//                } catch (TelegramApiException e) {
//                    e.printStackTrace();
//                }
//            }
            return "Ты сделал хороший выбор. Теперь возвращайся в таверну и следи за результатом боя!";
        }
    };
    protected String text;

    ArenaCommands(String text) {
        this.text = text;
    }

    @Override
    public boolean isApplicable(Message message) {
        return message.getText().contains(this.text);
    }

    @Override
    public String apply(Message message) {
        return "";
    }

    public enum Weapon {
        CHAIR(1, "Ножка от стула") {
            @Override
            public void init() {
                addSameWeaponPhrase("Оба бойца решили драться ножками от стульев, но %s оказался #чутьлучше чем %s. Чистая победа!");
                addSameWeaponPhrase("Сражающиеся сегодня решили померяться своими ножками от стульев. Удача сегодня на стороне %s, он без труда расправился с %s.");
                readyPhrase = "Боец %s будет сражаться ножкой от стула.\nБонус знания таверны: %s";
            }
            @Override
            public int against(Weapon another) {
                if (another == KARATE) {
                    return halfWin;
                } else if (another == ARM) {
                    return fullWin;
                }
                return 0;
            }

            @Override
            public String getWinPhrase(Weapon another) {
                if (another == CHAIR) {
                    return getSameWeaponPhrase();
                } else if (another == KARATE) {
                    return "%s буквально разбил лицо %s своей ножкой от стула. Запомните, карате не работает против ножки от стула!";
                } else if (another == ARM) {
                    return "%s нанес противнику удар ножкой от стула, а в ответ отхватил удар кулаком. Так продолжалось " +
                            "до тех пор, пока %s не упал, не в силах больше сражаться. Ножка от стула тащит!";
                } else if (another == CAPOEIRA) {
                    return "Обычно капоэйра помогает против бродяг с ножками от стульев, но не в этот раз. %s " +
                            "оказался слишком опытным бойцом таверны, и без труда уложил незадачливого танцора %s.";
                } else if (another == MUG) {
                    return "%s знает каждый угол в этой таверне, и смог добежать до противника, уклонившись от всех " +
                            "бросков жбанами. Ну а в ближнем бою ножка от стула не оставила %s шанса.";
                }
                return "";
            }

            @Override
            public int getStat(User user) {
                return user.getKno();
            }
        },//kno
        KARATE(2, "Карате") {
            @Override
            public void init() {
                addSameWeaponPhrase("Оба бойца решили драться при помощи карате, но %s оказался #чутьлучше чем %s. Чистая победа!");
                addSameWeaponPhrase("Сражающиеся сегодня решили померяться своим карате. Удача сегодня на стороне %s, он без труда расправился с %s.");
                readyPhrase = "Боец %s будет драться при помощи карате.\nБонус ловкости: %s";
            }
            @Override
            public int against(Weapon another) {
                if (another == ARM) {
                    return halfWin;
                } else if (another == CAPOEIRA) {
                    return fullWin;
                }
                return 0;
            }

            @Override
            public String getWinPhrase(Weapon another) {
                if (another == CHAIR) {
                    return "Сейчас вы наблюдаете удивительно редкую картину - карате %s оказалось сильнее ножки от стула! Победа над бойцом %s.";
                } else if (another == KARATE) {
                    return getSameWeaponPhrase();
                } else if (another == ARM) {
                    return "Карате круче кулаков, это знает каждый. %s с легкостью нокаутировал %s.";
                } else if (another == CAPOEIRA) {
                    return "Карате против капоэйры, восток против запада, сила против обаяния! Увы, чуда не произошло и %s надрал задницу %s.";
                } else if (another == MUG) {
                    return "Наш каратист %s отбил жбан в соперника, и %s свалился, пораженный своим же снарядом. Неожиданный итог боя!";
                }
                return "";
            }

            @Override
            public int getStat(User user) {
                DrinkPrefs drinkPrefs = DrinkPrefs.getByUser(user);
                return user.getAgi(drinkPrefs);
            }
        },//agi
        ARM(3, "Кулаки") {
            @Override
            public void init() {
                addSameWeaponPhrase("Оба бойца решили драться кулаками, но %s оказался #чутьлучше чем %s. Чистая победа!");
                addSameWeaponPhrase("Сражающиеся сегодня решили померяться крепостью своих кулаков. Удача сегодня на стороне %s, он без труда расправился с %s.");
                readyPhrase = "Боец %s будет драться кулаками.\nБонус стойкости: %s";
            }
            @Override
            public int against(Weapon another) {
                if (another == CAPOEIRA) {
                    return halfWin;
                } else if (another == MUG) {
                    return fullWin;
                }
                return 0;
            }

            @Override
            public String getWinPhrase(Weapon another) {
                if (another == CHAIR) {
                    return "%s удалось доказать сопернику, что правильные кулаки работают даже против ножки от стула. %s побежден!";
                } else if (another == KARATE) {
                    return "%s отвесил такого смачного леща сопернику, что у %s искры из глаз посыпались. Сегодня мы наблюдаем тот редкий случай, когда карате оказалось бессильно против простых кулаков!";
                } else if (another == ARM) {
                    return getSameWeaponPhrase();
                } else if (another == CAPOEIRA) {
                    return "Наш любитель капоэйры долго танцевал вокруг %s, пока не был им пойман и жестоко избит! %s в другой раз выбирай серьезный способ борьбы!";
                } else if (another == MUG) {
                    return "%s поймал лицом пару жбанов, прежде чем дошел до соперника, но он все же дошел, и %s пришлось отскребать от стены.";
                }
                return "%s победил бойца %s. Текст еще не написан.";
            }

            @Override
            public int getStat(User user) {
                DrinkPrefs drinkPrefs = DrinkPrefs.getByUser(user);
                return user.getCon(drinkPrefs);
            }
        }, //con
        CAPOEIRA(4, "Капоэйра") {
            @Override
            public void init() {
                addSameWeaponPhrase("Оба бойца решили сражаться, используя искусство капоэйры, но %s оказался #чутьлучше чем %s. Чистая победа!");
                addSameWeaponPhrase("Сражающиеся сегодня решили померяться своей капоэйрой. Удача сегодня на стороне %s, он без труда расправился с %s.");
                readyPhrase = "Боец %s будет сражаться при помощи капоейры.\nБонус обаяния: %s";
            }
            @Override
            public int against(Weapon another) {
                if (another == MUG) {
                    return halfWin;
                } else if (another == CHAIR) {
                    return fullWin;
                }
                return 0;
            }

            @Override
            public String getWinPhrase(Weapon another) {
                if (another == CHAIR) {
                    return "Очаровательный %s своим танцем отвлек соперника, и пока тот вспоминал, зачем ему ножка от стула, решительно вырубил %s ударом пяткой в ухо";
                } else if (another == KARATE) {
                    return "%s плавными движениями задницы нарушил душевное равновесие каратиста %s и смог нокаутировать соперника!";
                } else if (another == ARM) {
                    return "Капоэйра %s оказалась #чутьсильнее кулаков %s. Неожиданный результат!";
                } else if (another == CAPOEIRA) {
                    return getSameWeaponPhrase();
                } else if (another == MUG) {
                    return "%s обещал показать искусство капоэйры, а вместо этого съел красную пилюлю и нечеловеческим образом уклонился от всех летевших в него жбанов. %s признал свое поражение!";
                }
                return "%s победил бойца %s. Текст еще не написан.";
            }

            @Override
            public int getStat(User user) {
                DrinkPrefs drinkPrefs = DrinkPrefs.getByUser(user);
                return user.getCha(drinkPrefs);
            }
        },// cha
        MUG(5, "Метать жбаны") {
            @Override
            public void init() {
                addSameWeaponPhrase("Оба бойца решили метать жбаны, но %s оказался #чутьлучше чем %s. Чистая победа!");
                addSameWeaponPhrase("Сражающиеся сегодня решили померяться точностью в метании жбанов. Удача сегодня на стороне %s, он без труда расправился с %s.");
                readyPhrase = "Боец %s будет метать жбаны в противника.\nБонус силы: %s";
            }

            @Override
            public int getStat(User user) {
                DrinkPrefs drinkPrefs = DrinkPrefs.getByUser(user);
                return user.getStr(drinkPrefs);
            }

            @Override
            public int against(Weapon another) {
                if (another == CHAIR) {
                    return halfWin;
                } else if (another == KARATE) {
                    return fullWin;
                }
                return 0;
            }

            @Override
            public String getWinPhrase(Weapon another) {
                if (another == CHAIR) {
                    return "Метатель жбанов %s в очередной раз доказал, что дальнобойное оружие лучше ножки от стула. %s повержен!";
                } else if (another == KARATE) {
                    return "Точный бросок жбаном в лицо принес %s победу над %s! И никакое карате не помогло!";
                } else if (another == ARM) {
                    return "Кулачные бойцы отличаются большой стойкостью, но что поделать, если в тебя прилетел жбан? %s нокаутировал противника, не дав %s сделать и шага!";
                } else if (another == CAPOEIRA) {
                    return "Что может быть прекраснее вида разбитого носа любителя капоэйры? У метателя жбанов %s сегодня счастливый день, его соперник %s повержен!";
                } else if (another == MUG) {
                    return getSameWeaponPhrase();
                }
                return "%s победил бойца %s. Текст еще не написан.";
            }
        };//str

        private List<String> sameWeaponPhrase = new ArrayList<>();
        int fullWin = 20;
        int halfWin = 10;
        String readyPhrase;
        int number;
        String name;

        public int getNumber() {
            return number;
        }

        Weapon(int number, String name) {
            this.number = number;
            this.name = name;
            init();
        }

        public static Weapon getByNumber(int num) {
            for (Weapon w : values()) {
                if (w.number == num) {
                    return w;
                }
            }
            return null;
        }
        public static Weapon getByName(String name) {
            for (Weapon w : values()) {
                if (w.name.equals(name)) {
                    return w;
                }
            }
            return null;
        }

        public void addSameWeaponPhrase(String text) {
            sameWeaponPhrase.add(text);
        }

        String getSameWeaponPhrase() {
            if (sameWeaponPhrase.isEmpty()) {
                return "";
            }
            return sameWeaponPhrase.get(new Random().nextInt(sameWeaponPhrase.size()));
        }

        public abstract String getWinPhrase(Weapon another);

        public abstract void init();

        public String getReadyPhrase() {
            return readyPhrase;
        }

        public abstract int against(Weapon loseWep);

        public abstract int getStat(User user);

        public String getName() {
            return name;
        }
    }
}
