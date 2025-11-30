package plugin.memoryGame;

import java.io.InputStream;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.memoryGame.command.MemoryGameCommand;
import plugin.memoryGame.effect.MemoryGameEffect;
import plugin.memoryGame.listener.MemoryGameListener;
import plugin.memoryGame.service.MemoryGameService;

public final class Main extends JavaPlugin {

  @Override
  public void onEnable() {

    saveDefaultConfig();

    try {
      InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
      SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

      MemoryGameService service = new MemoryGameService(this,new MemoryGameEffect(),sqlSessionFactory);
      getCommand("memorygame").setExecutor(new MemoryGameCommand(service));
      Bukkit.getPluginManager().registerEvents(new MemoryGameListener(service), this);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
