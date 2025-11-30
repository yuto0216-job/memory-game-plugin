package plugin.memoryGame.service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.ibatis.session.SqlSessionFactory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import plugin.memoryGame.Main;
import plugin.memoryGame.data.ExecutingPlayer;
import plugin.memoryGame.data.ExecutingPlayer.GameState;
import plugin.memoryGame.data.PairBlock;
import plugin.memoryGame.effect.MemoryGameEffect;
import plugin.memoryGame.mapper.data.PlayerScore;
import plugin.memoryGame.repository.ScoreRepository;

public class MemoryGameService {

  private final Main main;
  private final MemoryGameEffect effect;
  private final List<ExecutingPlayer> executingPlayerList = new ArrayList<>();
  private final int gameLimit;
  private final int blockSpace;
  private final int player;
  private final ScoreRepository scoreRepository;

  public MemoryGameService(Main main, MemoryGameEffect effect,
      SqlSessionFactory sqlSessionFactory) {
    this.main = main;
    this.effect = effect;
    this.scoreRepository = new ScoreRepository(sqlSessionFactory);
    this.gameLimit = main.getConfig().getInt("gameLimit");
    this.blockSpace = main.getConfig().getInt("blockSpace");
    this.player = main.getConfig().getInt("top");
  }


  /**
   * ゲームの開始処理
   * @param player 実行したプレイヤー
   * @param pairCount 出現するブロックのペア数
   */

  public void gameStart(Player player, int pairCount) {
    ExecutingPlayer xp = getExecutingPlayer(player);

    if(!xp.getPairBlockList().isEmpty()){
      player.sendMessage("§c既に実行中です。ゲーム終了までお待ちください。");
      return;
    }

    sendGameUsage(xp.getPlayer());
    List<Location> loc = blocksLocation(xp.getPlayer(), pairCount, blockSpace);
    setBlocks(loc, xp);

    BukkitRunnable countDownTask = new BukkitRunnable() {
      int i = 3;

      @Override
      public void run() {

        if (i <= 0) {
          xp.getPlayer().sendTitle("ゲームスタート！", "", 0, 20, 0);

          effect.startSound(xp);
          setTimeBar(xp);
          cancel();
          return;
        }
        xp.getPlayer().sendTitle("ゲームスタートまで", String.valueOf(i), 0, 20, 0);
        effect.countdownSound(xp);
        i--;
      }
    };
    xp.setCountDownTask(countDownTask);
    countDownTask.runTaskTimer(main, 0, 20);
  }


  /**
   * 制限時間をプレイヤー上部にバーとして表示します。
   *
   * @param xp 実行したプレイヤー情報
   */

  private void setTimeBar(ExecutingPlayer xp) {
    BossBar timeBar = Bukkit.createBossBar("残り時間：" + gameLimit + "秒", BarColor.GREEN,
        BarStyle.SOLID);
    timeBar.addPlayer(xp.getPlayer());
    timeBar.setProgress(1.0);
    xp.setGameTime(gameLimit);

    BukkitRunnable playTask = new BukkitRunnable() {

      @Override
      public void run() {

        int gameTime = xp.getGameTime();

        if (gameTime <= 0 || xp.getPairBlockList().size() / 2 == xp.getCount()) {
          timeBar.removeAll();
          gameEnd(xp);
          cancel();
        }

        timeBar.setTitle("残り時間：" + gameTime + "秒");
        timeBar.setProgress(gameTime / (double) gameLimit);
        xp.setGameTime(gameTime - 1);
      }
    };
    xp.setPlayTask(playTask);
    playTask.runTaskTimer(main, 0, 20);
  }

  /**
   * ゲームの終了処理
   * @param xp 実行したプレイヤー情報
   */
  private void gameEnd(ExecutingPlayer xp) {
    xp.getPairBlockList().forEach(
        pb -> xp.getPlayer().getWorld().getBlockAt(pb.getLocation()).setType(Material.AIR));

    effect.celebration(xp);

    scoreRepository.insertScore(new PlayerScore(xp.getPlayerName(), xp.getScore()));

    xp.getPairBlockList().clear();
    xp.setScore(0);
    xp.setCount(0);
    xp.setFirst(null);
    xp.setState(GameState.FIRST);
  }

  /**
   * 途中退出した時のゲームの終了処理
   * @param player 退出したプレイヤー
   */
  public void quitGameEnd(Player player) {
    ExecutingPlayer xp = getExecutingPlayer(player);

    xp.getPairBlockList().forEach(
        pb -> xp.getPlayer().getWorld().getBlockAt(pb.getLocation()).setType(Material.AIR));

    if (xp.getCountDownTask() != null) {
      xp.getCountDownTask().cancel();
      xp.setCountDownTask(null);
    }
    if (xp.getPlayTask() != null) {
      xp.getPlayTask().cancel();
      xp.setPlayTask(null);
    }

    xp.getPairBlockList().clear();
    xp.setScore(0);
    xp.setCount(0);
    xp.setFirst(null);
    xp.setState(GameState.FIRST);
    executingPlayerList.remove(xp);
  }


  /**
   * 現在実行しているプレイヤーの情報を取得する
   *
   * @param player 実行したプレイヤー
   * @return 実行しているプレイヤーのスコア情報
   */
  private ExecutingPlayer getExecutingPlayer(Player player) {
    return executingPlayerList.stream().filter(p -> p.getPlayerName().equals(player.getName()))
        .findFirst().orElseGet(() -> {
          ExecutingPlayer xp = new ExecutingPlayer(player.getName(), player);
          executingPlayerList.add(xp);
          return xp;
        });
  }

  /**
   * ブロックを配置する
   *
   * @param locList ブロックの位置情報リスト
   * @param xp 実行したプレイヤー情報
   */

  private void setBlocks(List<Location> locList, ExecutingPlayer xp) {
    for (int i = 0; i < locList.size(); i += 2) {

      PairBlock pb1 = new PairBlock(locList.get(i), Material.GOLD_BLOCK, i / 2);
      PairBlock pb2 = new PairBlock(locList.get(i + 1), Material.GOLD_BLOCK, i / 2);
      xp.getPairBlockList().add(pb1);
      xp.getPairBlockList().add(pb2);
    }

    new BukkitRunnable() {
      int index = 0;

      @Override
      public void run() {
        if (index >= xp.getPairBlockList().size()) {
          cancel();
          return;
        }
        PairBlock pb = xp.getPairBlockList().get(index);
        xp.getPlayer().getWorld().getBlockAt(pb.getLocation()).setType(pb.getType());

        index++;
      }
    }.runTaskTimer(main, 0, 2);
  }

  /**
   * 配置するブロックの位置情報を設定する。
   *
   * @param player     実行したプレイヤー
   * @param pairCount  作りたいペア数
   * @param blockSpace ブロックの間隔
   * @return ブロックの位置情報のリスト
   */
  private List<Location> blocksLocation(Player player, int pairCount, int blockSpace) {
    List<Location> locList = new ArrayList<>();
    Location blockLocation = convertLocation(player.getLocation());

    int col = (int) Math.ceil(Math.sqrt(pairCount * 2));
    int index = 0;

    for (int x = 0; x < col && index < pairCount * 2; x++) {
      for (int z = 0; z < col && index < pairCount * 2; z++) {
        locList.add(
            blockLocation.clone()
                .add(x * blockSpace + 5, 0, z * blockSpace - (blockSpace * (col - 1)) / 2));
        index++;
      }
    }
    Collections.shuffle(locList);
    return locList;
  }

  /**
   * 取得した座標のを丸めて返す(小数点を切り捨てる)
   *
   * @param loc 取得した座標
   * @return 丸めた後の座標
   */
  private Location convertLocation(Location loc) {
    World world = loc.getWorld();
    return new Location(
        world, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()
    );
  }

  /**
   * プレイヤーがクリックしたブロックに対して ゲームの状態に応じた処理を行います。
   * @param player 実行したプレイヤー
   * @param location クリックしたブロックの位置情報
   */
  public void handleBlockClick(Player player,Location location) {
    ExecutingPlayer xp = getExecutingPlayer(player);
    Location clickedLocation = convertLocation(location);

    xp.getPairBlockList().stream().filter(c -> c.getLocation().equals(clickedLocation)).findFirst()
        .ifPresent(clickBlock -> {

          xp.getPlayer().sendMessage("数字は" + clickBlock.getPairId() + "です！");

          switch (xp.getState()) {
            case FIRST -> firstSelection(clickBlock, xp);
            case SECOND -> secondSelection(xp.getPlayer(), clickBlock, xp);
          }
        });
  }

  /**
   * 1つ目のブロックをクリックしたときの処理
   */
  private void firstSelection(PairBlock clickBlock, ExecutingPlayer xp) {
    xp.setFirst(clickBlock);
    xp.setState(GameState.SECOND);
  }

  /**
   * 2つ目のブロックをクリックしたときの処理
   */
  private void secondSelection(Player player, PairBlock clickBlock, ExecutingPlayer xp) {
    if (clickBlock == xp.getFirst()) {
      player.sendMessage("§c違うブロックを選択して下さい!");
      return;
    }

    if (clickBlock.getPairId() == xp.getFirst().getPairId()) {
      removeBlock(player, clickBlock, xp);
      xp.setCount(xp.getCount() + 1);
    } else {
      player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1f, 1.0f);
      player.sendMessage("§c✖ 不正解！");
    }
    xp.setFirst(null);
    xp.setState(GameState.FIRST);
  }

  /**
   * 正解したペアを除去する処理
   */
  private void removeBlock(Player player, PairBlock clickBlock, ExecutingPlayer xp) {
    player.getWorld().getBlockAt(clickBlock.getLocation()).setType(Material.AIR);
    player.getWorld().getBlockAt(xp.getFirst().getLocation()).setType(Material.AIR);

    executingPlayerList.stream()
        .filter(p -> p.getPlayerName().equals(player.getName())).findFirst()
        .ifPresent(p -> {
          p.setScore(p.getScore() + 1);

          player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.2f);
          player.spawnParticle(Particle.HAPPY_VILLAGER, clickBlock.getLocation().add(0.5, 1, 0.5),
              20, 0.3, 0.3, 0.3);
          player.sendMessage("§a§l+1 点!");

        });
  }

  /**
   * DBから取得したscoreをplayerに表示する。
   *
   * @param args   引数
   * @param player コマンドを実行したプレイヤー
   */
  public void showRanking(String[] args, Player player) {
    if (args.length == 1) {
      player.sendMessage("§6===== MemoryGame ランキング TOP" + this.player + " =====");

      List<PlayerScore> playerScoreList = scoreRepository.playerScoreList(this.player);

      int rank = 1;
      for (PlayerScore ps : playerScoreList) {
        player.sendMessage(String.format(
            "§e%d位 §f%s §7- §b%d点 §7(%s)",
            rank,
            ps.getPlayerName(),
            ps.getScore(),
            ps.getRegisteredAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"))
        ));
        rank++;
      }
      player.sendMessage("§6======================================");
    }
  }

  /**
   * 出現させたブロックと破棄したブロックの真偽値を返す
   * @param player ブロックを破壊しようとしたプレイヤー
   * @param location 対象ブロックの位置情報
   *
   * @return MemoryGameのブロックであればtrue、そうでなければfalse
   */
  public boolean isProtectedBlock(Player player,Location location) {
    ExecutingPlayer xp = getExecutingPlayer(player);
    Location breakBlockLocation = convertLocation(location);

    return xp.getPairBlockList().stream().anyMatch(c -> c.getLocation().equals(breakBlockLocation));
  }

  /**
   * ゲーム実行の引数の説明
   * @param player 実行したプレイヤー
   */
  public void sendArgsUsage(Player player) {
    player.sendMessage("§e=== Game Command Help ===");
    player.sendMessage("§a/memorygame rank §7- Top" + this.player + " 表示");
    player.sendMessage("§a/memorygame play <1〜20> §7- 指定ペア数でゲーム開始");
  }

  /**
   * ゲームスタート時のゲーム説明
   * @param player 実行したプレイヤー
   */
  private void sendGameUsage(Player player) {
    player.sendMessage("§6=== MemoryGame(神経衰弱) ゲーム説明===");
    player.sendMessage("");
    player.sendMessage("§f1.出現したブロックを左クリックすると数字が表示されます。");
    player.sendMessage("");
    player.sendMessage("§f2.同じ数字のブロックを2つ揃えると1点獲得！");
    player.sendMessage("");
    player.sendMessage("§f3.制限時間は" + gameLimit + "秒。時間内にできるだけ多く揃えましょう！");
    player.sendMessage("");
    player.sendMessage("§6===================================");
  }

}
