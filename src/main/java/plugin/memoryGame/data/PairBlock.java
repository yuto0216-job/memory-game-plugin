package plugin.memoryGame.data;


import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;


@Getter
@Setter

public class PairBlock {


  private Location location;
  private int pairId;
  private Material type;


public PairBlock(Location location,Material type,int pairId){
  this.location = location;
  this.type = type;
  this.pairId = pairId;
}


}
