import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Represents the battle arena where two teams fight.
 */
public class BattleArena {
    private Team team1;
    private Team team2;
    private FileWriter battleLogWriter; // Renamed to avoid confusion
    private BattleLogEntry history; // Use BattleLogEntry
    private int turnCounter = 1;

    /**
     * Constructs a new BattleArena.
     * @param team1 The first team.
     * @param team2 The second team.
     * @throws IOException If an I/O error occurs.
     */
    public BattleArena(Team team1, Team team2) throws IOException {
        this.team1 = team1;
        this.team2 = team2;
        this.battleLogWriter = new FileWriter("BattleLog.txt");
        this.history = null; // Initialize empty history
    }

    /**
     * Starts the battle between the two teams.
     * @throws IOException If an I/O error occurs.
     */
    public void startBattle() throws IOException {
    Team currentAttacker = team1;
    Team currentDefender = team2;

    while (team1.hasLivingMembers() && team2.hasLivingMembers()) {
        fightTurn(currentAttacker, currentDefender);

        // Alternate attacker and defender each turn
        Team temp = currentAttacker;
        currentAttacker = currentDefender;
        currentDefender = temp;

        if (!team1.hasLivingMembers() || !team2.hasLivingMembers()) {
            break;
        }
    }

    declareWinner();
}


    /**
     * Simulates a single turn of the battle.
     * @param attackingTeam The team attacking.
     * @param defendingTeam The team defending.
     * @throws IOException If an I/O error occurs.
     */
    private void fightTurn(Team attackingTeam, Team defendingTeam) throws IOException {
    if (!attackingTeam.hasLivingMembers() || !defendingTeam.hasLivingMembers()) {
        return;
    }

    GameCharacter attacker = attackingTeam.getNextAliveCharacter();
    GameCharacter defender = defendingTeam.getNextAliveCharacter();
    if (attacker == null || defender == null) {
        return;
    }

    System.out.println("Turn " + turnCounter + ":");
    turnCounter++;

    Battle battle = new Battle(attacker, defender);
    String result = battle.startFight();

    System.out.println(result);
    System.out.println("HP " + defender.getName() + "'s HP: " + defender.getHealthPoints());
    System.out.println("--------------------------------------------------");

    battleLogWriter.write(result + "\n");

    if (history == null) {
        history = new BattleLogEntry(result);
    } else {
        history.addLog(result);
    }

    try {
        Thread.sleep(800); // Delay between turns
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }

}


    /**
     * Declares the winner of the battle and closes the battle log.
     * @throws IOException If an I/O error occurs.
     */
    private void declareWinner() throws IOException {
        try {
            if (!team1.hasLivingMembers()) {
                System.out.println(team2.getTeamName() + " wins!");
                battleLogWriter.write(team2.getTeamName() + " wins!\n");
            } else if (!team2.hasLivingMembers()) {
                System.out.println(team1.getTeamName() + " wins!");
                battleLogWriter.write(team1.getTeamName() + " wins!\n");
            } else {
                System.out.println("It's a draw!");
                battleLogWriter.write("It's a draw!\n");
            }
        } finally {
            battleLogWriter.close();
        }
    }

    /**
     * Prints the battle history to the console.
     */
    public void printBattleHistory() {
        if (history != null) {
            history.printHistory();
        } else {
            System.out.println("No battle history available.");
        }
    }

    /**
     * Gets the number of battle log entries.
     * @return The number of battle log entries.
     */
    public int getBattleLogCount() {
        if (history == null) {
            return 0;
        }
        return history.countLogs();
    }

    /**
     * Gets the full battle history as a string.
     * @return The full battle history as a string.
     */
    public String getFullBattleHistory() {
        if (history == null) {
            return "No battle history available.";
        }
        return history.getAllLogs();
    }

    /**
     * Clears the battle history.
     */
    public void clearBattleHistory() {
        if (history != null) {
            history.clearHistory();
            history = null;
            System.out.println("Battle history cleared.");
        } else {
            System.out.println("No battle history to clear.");
        }
    }

    /**
     * Saves the battle history to a file.
     * @param filename The name of the file to save the history to.
     */
    public void saveBattleHistory(String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            if (history != null) {
                writer.write(history.getAllLogs());
            } else {
                writer.write("No battle history available.");
            }
        } catch (IOException e) {
            System.err.println("Error saving battle history: " + e.getMessage());
        }
    }

    /**
     * Loads the battle history from a file.
     * @param filename The name of the file to load the history from.
     */
    public void loadBattleHistory(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            history = null; // Clear existing history
            while ((line = reader.readLine()) != null) {
                if (history == null) {
                    history = new BattleLogEntry(line);
                } else {
                    history.addLog(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading battle history: " + e.getMessage());
        }
    }

    /**
     * Saves the current game state to a file.
     * @param filename The name of the file to save the game state to.
     * @throws IOException If an I/O error occurs.
     */
    public void saveGameState(String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("Team 1: " + team1.getTeamName() + "\n");
            for (GameCharacter c : team1.getMembers()) {
                writer.write(c.getName() + " - HP: " + c.getHealthPoints() + "\n");
            }
            writer.write("Team 2: " + team2.getTeamName() + "\n");
            for (GameCharacter c : team2.getMembers()) {
                writer.write(c.getName() + " - HP: " + c.getHealthPoints() + "\n");
            }
        }
    }

    /**
     * Loads the game state from a file.
     * @param filename The name of the file to load the game state from.
     * @throws IOException If an I/O error occurs.
     */
    public void loadGameState(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String team1NameLine = reader.readLine();
            if (team1NameLine == null || !team1NameLine.startsWith("Team 1: ")) {
                throw new IOException("Invalid game state file format: Missing or invalid Team 1 line");
            }
            String team1Name = team1NameLine.substring(7);
            team1 = new Team(team1Name);

            String line;
            while ((line = reader.readLine()) != null && !line.startsWith("Team 2: ")) {
                String[] parts = line.split(" - HP: ");
                if (parts.length != 2) {
                    System.out.println("Skipping invalid line: " + line);
                    continue;
                }
                String name = parts[0].trim();
                try {
                    int health = Integer.parseInt(parts[1].trim());
                    GameCharacter character = new GameCharacter(name, health, 10, 5, "Human", "Warrior");
                    team1.addCharacter(character);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid health format: " + parts[1]);
                }
            }

            if (line == null || !line.startsWith("Team 2: ")) {
                throw new IOException("Invalid game state file format: Missing or invalid Team 2 line");
            }
            String team2Name = line.substring(7);
            team2 = new Team(team2Name);

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" - HP: ");
                if (parts.length != 2) {
                    System.out.println("Skipping invalid line: " + line);
                    continue;
                }
                String name = parts[0].trim();
                try {
                    int health = Integer.parseInt(parts[1].trim());
                    GameCharacter character = new GameCharacter(name, health, 10, 5, "Human", "Warrior");
                    team2.addCharacter(character);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid health format: " + parts[1]);
                }
            }

        } catch (IOException e) {
            System.err.println("Error loading game state: " + e.getMessage());
            throw e; // Re-throw the exception to be handled by the caller
        }
    }
}