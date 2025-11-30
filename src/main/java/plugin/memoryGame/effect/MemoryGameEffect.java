package plugin.memoryGame.effect;

import org.bukkit.Particle;
import org.bukkit.Sound;
import plugin.memoryGame.data.ExecutingPlayer;

public class MemoryGameEffect {

  /**
   * カウントダウン時の演出
   */
  public void countdownSound(ExecutingPlayer xp) {
    xp.getPlayer().playSound(
        xp.getPlayer().getLocation(),
        Sound.BLOCK_NOTE_BLOCK_HAT,
        1f, 1.6f
    );
  }

  /**
   * ゲームスタート時の演出
   */
  public void startSound(ExecutingPlayer xp) {
    xp.getPlayer().playSound(
        xp.getPlayer().getLocation(),
        Sound.ENTITY_PLAYER_LEVELUP,
        1f, 1.1f
    );
    xp.getPlayer().spawnParticle(
        Particle.ELECTRIC_SPARK,
        xp.getPlayer().getLocation().add(0, 1, 0),
        30, 0.4, 0.4, 0.4, 0.05
    );
  }

  /**
   * ゲーム終了時の演出
   */
  public void celebration(ExecutingPlayer xp) {

    // タイトル演出
    xp.getPlayer().sendTitle(
        "§e§l ゲーム終了！ ",
        "§bスコア: §a" + xp.getScore() + "点",
        10, 70, 10
    );

    // サウンド
    xp.getPlayer().playSound(
        xp.getPlayer().getLocation(),
        org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE,
        1f, 1f
    );

    // パーティクル
    xp.getPlayer().getWorld().spawnParticle(
        Particle.CRIT,
        xp.getPlayer().getLocation().add(0, 1, 0),
        80, 1.0, 1.0, 1.0, 0.25
    );

    // 花火
    xp.getPlayer().getWorld().spawn(
        xp.getPlayer().getLocation(),
        org.bukkit.entity.Firework.class,
        f -> {
          var meta = f.getFireworkMeta();
          meta.addEffect(org.bukkit.FireworkEffect.builder()
              .withColor(org.bukkit.Color.AQUA)
              .withFade(org.bukkit.Color.WHITE)
              .with(org.bukkit.FireworkEffect.Type.BALL_LARGE)
              .flicker(true)
              .trail(true)
              .build());
          meta.setPower(1);
          f.setFireworkMeta(meta);
        });
  }
}
