package org.example;

import org.example.skills.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        System.out.println("====== Game nhân vật đánh theo lượt ======");

        // Đội A: Nhân vật anh hùng
        Character heroCharacter = new Character("Hiệp Sĩ", 15, 10, 12, 8);
        heroCharacter.setUltimateSkill(new MightySlash());

        Character wizardCharacter = new Character("Pháp Sư", 8, 12, 7, 15);
        wizardCharacter.setUltimateSkill(new ThunderStrike());

        // Thêm các nhân vật mới cho đội A
        Character healerCharacter = new Character("Y Sư", 5, 10, 8, 18);
        healerCharacter.setUltimateSkill(new HealthRestore());

        Character supportCharacter = new Character("Hiền Giả", 6, 11, 7, 17);
        supportCharacter.setUltimateSkill(new ManaRestore());

        Character tankerCharacter = new Character("Kỵ Sĩ Khiên", 10, 8, 18, 5);
        tankerCharacter.setUltimateSkill(new ShieldBash());

        // Đội B: Quái vật
        Character monsterCharacter_1 = new Character("Quái Vật Lửa", 12, 8, 10, 12);
        monsterCharacter_1.setUltimateSkill(new FireballBarrage());

        Character monsterCharacter_2 = new Character("Quái Vật Độc", 11, 13, 8, 9);
        monsterCharacter_2.setUltimateSkill(new PoisonStrike());

        Character monsterCharacter_3 = new Character("Quái Vật Bóng Tối", 14, 11, 7, 10);
        monsterCharacter_3.setUltimateSkill(new ShadowStrike());

        System.out.println("\nThông tin nhân vật trước khi vào trận:");
        System.out.println("=== Đội A ===");
        System.out.println(heroCharacter);
        System.out.println(wizardCharacter);
        System.out.println(healerCharacter);
        System.out.println(supportCharacter);
        System.out.println(tankerCharacter);
        System.out.println("\n=== Đội B ===");
        System.out.println(monsterCharacter_1);
        System.out.println(monsterCharacter_2);
        System.out.println(monsterCharacter_3);

        // Tạo một trận đấu mới
        Battle battle = new Battle();

        // Thêm nhân vật vào trận đấu với phân chia đội
        // Đội A
        battle.addCharacter(heroCharacter, Team.TEAM_A);
        battle.addCharacter(wizardCharacter, Team.TEAM_A);
        battle.addCharacter(healerCharacter, Team.TEAM_A);
        battle.addCharacter(supportCharacter, Team.TEAM_A);
        battle.addCharacter(tankerCharacter, Team.TEAM_A);

        // Đội B
        battle.addCharacter(monsterCharacter_1, Team.TEAM_B);
        battle.addCharacter(monsterCharacter_2, Team.TEAM_B);
        battle.addCharacter(monsterCharacter_3, Team.TEAM_B);

        // Bắt đầu trận đấu (tự động xử lý các lượt đánh)
        battle.startBattle();

        System.out.println("\n===== Kết thúc chương trình demo =====\n");
    }
}