package plugin.memoryGame.data;


import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
@Setter

public class ExecutingPlayer {
  public enum GameState {
    FIRST,
    SECOND
  }

  private List<PairBlock> pairBlockList = new ArrayList<>();
  private GameState state = GameState.FIRST;
  private Player player;
  private String playerName;
  private int score = 0;
  private PairBlock first = null;
  private int gameTime = 0;
  private int count = 0;
  private BukkitRunnable countDownTask;
  private BukkitRunnable playTask;


public ExecutingPlayer(String playerName,Player player){
 this.playerName = playerName;
 this.player = player;
}
}

