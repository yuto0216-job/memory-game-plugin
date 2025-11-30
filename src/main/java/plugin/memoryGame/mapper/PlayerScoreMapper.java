package plugin.memoryGame.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import plugin.memoryGame.mapper.data.PlayerScore;

public interface PlayerScoreMapper {

  @Select("select * from player_score order by score desc limit #{top}")
  List<PlayerScore> selectList(@Param("top") int top);

  @Insert("insert into player_score(player_name,score,registered_at)"
      + "values (#{playerName},#{score},now())")
  void insert(PlayerScore playerScore);

}
