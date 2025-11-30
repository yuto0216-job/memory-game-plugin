package plugin.memoryGame.repository;

import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import plugin.memoryGame.data.ExecutingPlayer;
import plugin.memoryGame.mapper.PlayerScoreMapper;
import plugin.memoryGame.mapper.data.PlayerScore;

public class ScoreRepository {

  private final SqlSessionFactory sqlSessionFactory;

  public ScoreRepository(SqlSessionFactory factory) {
    this.sqlSessionFactory = factory;
  }

  /**
   * プレイヤーのスコア情報をデータベースに登録する
   * @param playerScore　登録するプレイヤーのスコア情報
   */
  public void insertScore(PlayerScore playerScore) {
    try (SqlSession session = sqlSessionFactory.openSession(true)) {
      PlayerScoreMapper mapper = session.getMapper(PlayerScoreMapper.class);
      mapper.insert(playerScore);
    }
  }

  /**
   * スコアが高い順に指定された件数分のプレイヤー情報を取得する
   *
   * @param top　取得する件数（スコア上位から取得）
   * @return スコアが高い順に並んだプレイヤーのスコア情報のリスト
   */
  public List<PlayerScore> playerScoreList(int top) {
    try (SqlSession session = sqlSessionFactory.openSession()) {
      return session.getMapper(PlayerScoreMapper.class).selectList(top);
    }
  }

}
