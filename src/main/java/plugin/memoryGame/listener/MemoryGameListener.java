package plugin.memoryGame.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import plugin.memoryGame.service.MemoryGameService;

public class MemoryGameListener implements Listener {

  private final MemoryGameService service;

  public MemoryGameListener(MemoryGameService service) {
    this.service = service;
  }

  @EventHandler(ignoreCancelled = true)
  public void onBlockClick(PlayerInteractEvent e) {
    if (Action.RIGHT_CLICK_BLOCK == e.getAction()) {
      return;
    }
    service.handleBlockClick(e.getPlayer(), e.getClickedBlock().getLocation());
  }

  @EventHandler
  public void onBreakBlock(BlockBreakEvent b) {
    if (service.isProtectedBlock(b.getPlayer(), b.getBlock().getLocation())) {
      b.setCancelled(true);
      b.getPlayer().sendMessage("§4-このBlockは破壊できません！");
    }
  }

  @EventHandler
  public void onQuitPlayer(PlayerQuitEvent q) {
    service.quitGameEnd(q.getPlayer());
  }
}
