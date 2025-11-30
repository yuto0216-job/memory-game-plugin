package plugin.memoryGame.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import plugin.memoryGame.service.MemoryGameService;

public class MemoryGameCommand implements CommandExecutor {

  private final MemoryGameService service;

  public MemoryGameCommand(MemoryGameService service) {
    this.service = service;
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
      @NotNull String label, @NotNull String[] args) {
    if (!(sender instanceof Player player)) {
      return false;
    }

    if (args.length == 0) {
      service.sendArgsUsage(player);
      return true;
    }

    switch (args[0].toLowerCase()) {
      case "rank" -> service.showRanking(args, player);
      case "play" -> gameplay(args, player);
      default -> service.sendArgsUsage(player);
    }
    return true;
  }

  /**
   * ゲーム実行の処理
   * @param args 引数。ペア数を指す
   * @param player 実行したプレイヤー
   */
  private void gameplay(String[] args, Player player) {
    if (args.length == 2 && args[1].matches("[1-9]|1\\d|20")) {
      int pairCount = Integer.parseInt(args[1]);
      service.gameStart(player, pairCount);
    } else {
      service.sendArgsUsage(player);
    }
  }
}
