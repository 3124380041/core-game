package org.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Manages a battle between characters.
 */
public class Battle {
    private List<BattleCharacter> participants;
    private boolean battleEnded;

    /**
     * Create a new battle.
     */
    public Battle() {
        participants = new ArrayList<>();
        battleEnded = false;
    }

    /**
     * Add a character to the battle.
     *
     * @param character The character to add
     * @param team The team this character belongs to
     * @return The battle character created for this battle
     */
    public BattleCharacter addCharacter(Character character, Team team) {
        BattleCharacter battleCharacter = new BattleCharacter(character, team);
        participants.add(battleCharacter);
        return battleCharacter;
    }

    /**
     * Start the battle.
     */
    public void startBattle() {
        if (participants.size() < 2) {
            System.out.println("Cần ít nhất 2 nhân vật để bắt đầu trận đấu!");
            return;
        }

        System.out.println("===== Trận đấu bắt đầu! =====");
        System.out.println("Các nhân vật tham gia:");
        for (BattleCharacter participant : participants) {
            System.out.println(participant);
        }

        // Main battle loop
        int round = 1;
        while (!battleEnded) {
            System.out.println("\n===== Vòng " + round + " =====");

            // Sort characters by speed for turn order
            participants.sort(Comparator.comparing(BattleCharacter::getCharacter, Comparator.comparing(Character::getAgility)).reversed());

            // Process each character's turn
            for (BattleCharacter participant : participants) {
                if (!participant.isDefeated()) {
                    processTurn(participant);

                    // Check if battle should end
                    if (checkBattleEnd()) {
                        battleEnded = true;
                        break;
                    }
                }
            }

            // After each round, restore some essence to all characters
            for (BattleCharacter participant : participants) {
                if (!participant.isDefeated()) {
                    // Tăng thêm một chút MP sau mỗi vòng
                    participant.increaseMp(25);
                    System.out.println(participant.getName() + "tăng 5 điểm Nộ Khí.");
                }
            }

            round++;
        }

        System.out.println("\n===== Trận đấu kết thúc! =====");
        // Determine winning team
        Team winningTeam = null;
        for (BattleCharacter participant : participants) {
            if (!participant.isDefeated()) {
                winningTeam = participant.getTeam();
                break;
            }
        }

        // Announce winning team
        if (winningTeam != null) {
            System.out.println(winningTeam + " là đội chiến thắng!");
            System.out.println("Các người chơi chiến thắng:");
            for (BattleCharacter participant : participants) {
                if (!participant.isDefeated() && participant.getTeam() == winningTeam) {
                    System.out.println("- " + participant.getName());
                }
            }
        }
    }

    /**
     * Process a single turn for a battle character.
     *
     * @param character The battle character whose turn it is
     */
    private void processTurn(BattleCharacter character) {
        System.out.println("\n=== Lượt của " + character.getName() + " ===");
        System.out.println(character);

        // Check if character can use ultimate skill
        if (character.canUseUltimateSkill()) {
            String skillName = character.getCharacter().getUltimateSkill().getName();

            // Xác định mục tiêu dựa vào loại kỹ năng
            BattleCharacter target;

            // Nếu là kỹ năng hồi phục, nhắm vào đồng minh yếu nhất
            if (skillName.equals("Hồi Xuân Thuật") || skillName.equals("Khí Nguyên Chú")) {
                target = getWeakestAlly(character);
                if (target == null) {
                    target = character; // Nếu không có đồng minh, hồi phục chính mình
                }

                int amount = character.useUltimateSkill(target);

                if (skillName.equals("Hồi Xuân Thuật")) {
                    System.out.println("⚡ " + character.getName() + " sử dụng kỹ năng [" + 
                            skillName + "] vào " + target.getName() + " và hồi phục " + amount + " điểm máu!");
                } else {
                    System.out.println("⚡ " + character.getName() + " sử dụng kỹ năng [" + 
                            skillName + "] vào " + target.getName() + " và hồi phục " + amount + " điểm nộ khí!");
                }

                System.out.println(target);
            } else {
                // Kỹ năng tấn công, nhắm vào kẻ địch
                target = getRandomTarget(character);
                if (target != null) {
                    int damage = character.useUltimateSkill(target);
                    System.out.println("⚡ " + character.getName() + " sử dụng kỹ năng [" + 
                            skillName + "] vào " + target.getName() + " và gây ra " + damage + " sát thương!");
                    System.out.println(target);

                    if (target.isDefeated()) {
                        System.out.println(target.getName() + " đã bị đánh bại!");
                    }
                } else {
                    System.out.println(character.getName() + " không thể tìm thấy mục tiêu để tấn công!");
                    return;
                }
            }
        } else {
            // Do a basic attack
            BattleCharacter target = getRandomTarget(character);
            if (target != null) {
                int damage = character.attack(target);
                System.out.println(character.getName() + " tấn công " + target.getName() + " và gây ra " + damage + " sát thương!");
                System.out.println(target);

                if (target.isDefeated()) {
                    System.out.println(target.getName() + " đã bị đánh bại!");
                }
            } else {
                System.out.println(character.getName() + " không thể tìm thấy mục tiêu để tấn công!");
            }
        }
    }

    /**
     * Gets a random target for the character to attack (from enemy team only).
     *
     * @param attacker The attacking battle character
     * @return A random target battle character that is not defeated and is from the opposite team
     */
    private BattleCharacter getRandomTarget(BattleCharacter attacker) {
        List<BattleCharacter> validTargets = new ArrayList<>();

        for (BattleCharacter participant : participants) {
            if (participant != attacker && 
                !participant.isDefeated() && 
                participant.getTeam() != attacker.getTeam()) {
                validTargets.add(participant);
            }
        }

        if (validTargets.isEmpty()) {
            return null;
        }

        // Select a random target
        int randomIndex = (int) (Math.random() * validTargets.size());
        return validTargets.get(randomIndex);
    }

    /**
     * Gets the weakest ally (lowest HP percentage) for healing.
     *
     * @param healer The healing battle character
     * @return The weakest ally that is not defeated and is from the same team
     */
    private BattleCharacter getWeakestAlly(BattleCharacter healer) {
        List<BattleCharacter> allies = new ArrayList<>();

        for (BattleCharacter participant : participants) {
            if (participant != healer && 
                !participant.isDefeated() && 
                participant.getTeam() == healer.getTeam()) {
                allies.add(participant);
            }
        }

        if (allies.isEmpty()) {
            return null;
        }

        // Find the ally with the lowest HP percentage
        BattleCharacter weakestAlly = allies.get(0);
        double lowestHpPercentage = (double) weakestAlly.getCurrentHealth() / weakestAlly.getMaxHealth();

        for (BattleCharacter ally : allies) {
            double hpPercentage = (double) ally.getCurrentHealth() / ally.getMaxHealth();
            if (hpPercentage < lowestHpPercentage) {
                lowestHpPercentage = hpPercentage;
                weakestAlly = ally;
            }
        }

        return weakestAlly;
    }

    /**
     * Checks if the battle should end (only one team remaining).
     *
     * @return true if the battle should end, false otherwise
     */
    private boolean checkBattleEnd() {
        boolean teamAAlive = false;
        boolean teamBAlive = false;

        for (BattleCharacter participant : participants) {
            if (!participant.isDefeated()) {
                if (participant.getTeam() == Team.TEAM_A) {
                    teamAAlive = true;
                } else if (participant.getTeam() == Team.TEAM_B) {
                    teamBAlive = true;
                }
            }
        }

        // Battle ends if either team is completely defeated
        return !teamAAlive || !teamBAlive;
    }
}