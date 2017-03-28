package org.nia.logic.commands;

import org.nia.model.TournamentUsers;
import org.nia.model.User;
import org.telegram.telegrambots.api.objects.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author IANazarov
 */
public enum PostukCommands implements Commands {
    DRAKA("/DRAKA") {
        @Override
        public boolean isApplicable(Message message) {
            if (!super.isApplicable(message)) {
                return false;
            }
            TournamentUsers currentByUserID = TournamentUsers.getCurrentByUserID(message.getFrom().getId());
            return currentByUserID != null && currentByUserID.InFight() && currentByUserID.getScore() == 0;
        }

        @Override
        public String apply(Message message) {
            TournamentUsers currentByUserID = TournamentUsers.getCurrentByUserID(message.getFrom().getId());
            User user = currentByUserID.getUser();
            currentByUserID.setScore(user.getFightClubStatsSum());
            currentByUserID.save();

            return String.format(currentByUserID.getTournament().getType().getStartPhrase(), user);
        }
    };
    protected String text;

    PostukCommands(String text) {
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

    public enum Weapons {
        CHAIR {
            @Override
            public void init() {
                addSameWeaponPhrase("Оба бойца решили драться ножками от стульев, но %s оказался #чутьлучше чем %s. Чистая победа!");
                addSameWeaponPhrase("Сражающиеся сегодня решили померяться своими ножками от стульев. Удача сегодня на стороне %s, он без труда расправился с %s.");
                readyPhrase = "Боец %s будет сражаться ножкой от стула.\nБонус знания таверны: %d";
            }

            @Override
            public String getWinPhrase(Weapons another) {
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
        },//kno
        KARATE {
            @Override
            public void init() {
                addSameWeaponPhrase("Оба бойца решили драться при помощи карате, но %s оказался #чутьлучше чем %s. Чистая победа!");
                addSameWeaponPhrase("Сражающиеся сегодня решили померяться своим карате. Удача сегодня на стороне %s, он без труда расправился с %s.");
                readyPhrase = "Боец %s будет драться при помощи карате.\nБонус ловкости: %d";
            }

            @Override
            public String getWinPhrase(Weapons another) {
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
        },//agi
        ARM {
            @Override
            public void init() {
                addSameWeaponPhrase("Оба бойца решили драться кулаками, но %s оказался #чутьлучше чем %s. Чистая победа!");
                addSameWeaponPhrase("Сражающиеся сегодня решили померяться крепостью своих кулаков. Удача сегодня на стороне %s, он без труда расправился с %s.");
                readyPhrase = "Боец %s будет драться кулаками.\nБонус стойкости: %d";
            }

            @Override
            public String getWinPhrase(Weapons another) {
                if (another == CHAIR) {
                } else if (another == KARATE) {
                } else if (another == ARM) {
                    return getSameWeaponPhrase();
                } else if (another == CAPOEIRA) {
                } else if (another == MUG) {
                }
                return "";
            }
        }, //con
        CAPOEIRA {
            @Override
            public void init() {
                addSameWeaponPhrase("Оба бойца решили сражатсья, используя искусство капоэйры, но %s оказался #чутьлучше чем %s. Чистая победа!");
                addSameWeaponPhrase("Сражающиеся сегодня решили померяться своей капоэйрой. Удача сегодня на стороне %s, он без труда расправился с %s.");
                readyPhrase = "Боец %s будет сражаться при помощи капоейры.\nБонус обаяния: %d";
            }

            @Override
            public String getWinPhrase(Weapons another) {
                if (another == CHAIR) {
                } else if (another == KARATE) {
                } else if (another == ARM) {
                } else if (another == CAPOEIRA) {
                    return getSameWeaponPhrase();
                } else if (another == MUG) {
                }
                return "";
            }
        },// cha
        MUG {
            @Override
            public void init() {
                addSameWeaponPhrase("Оба бойца решили метать жбаны, но %s оказался #чутьлучше чем %s. Чистая победа!");
                addSameWeaponPhrase("Сражающиеся сегодня решили померяться точностью в метании жбанов. Удача сегодня на стороне %s, он без труда расправился с %s.");
                readyPhrase = "Боец %s будет метать жбаны в противника.\nБонус силы: %d";
            }

            @Override
            public String getWinPhrase(Weapons another) {
                if (another == CHAIR) {
                } else if (another == KARATE) {
                } else if (another == ARM) {
                } else if (another == CAPOEIRA) {
                } else if (another == MUG) {
                    return getSameWeaponPhrase();
                }
                return "";
            }
        };//str

        private List<String> sameWeaponPhrase = new ArrayList<>();
        String readyPhrase;

        public void addSameWeaponPhrase(String text) {
            sameWeaponPhrase.add(text);
        }

        String getSameWeaponPhrase() {
            if (sameWeaponPhrase.isEmpty()) {
                return "";
            }
            return sameWeaponPhrase.get(new Random().nextInt(sameWeaponPhrase.size()));
        }

        public abstract String getWinPhrase(Weapons another);

        public abstract void init();

        public String getReadyPhrase() {
            return readyPhrase;
        }
    }
}
