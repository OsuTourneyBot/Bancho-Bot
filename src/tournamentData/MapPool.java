/**
 * Need to make maps that uses the beatmap ID as the key
 * Need 2 maps, one that holds mods and the other one that holds the song/link to download
 * 
 * How to use hashmaps in java
 * https://www.w3schools.com/java/java_hashmap.asp 
 */
package tournamentData;

import java.util.HashMap;

/**
 * @author Vince
 *
 */
public class MapPool 
{
	// stores songs and mods
	private HashMap<Integer, Map> songPool;
	private HashMap<String, Map> tournamentName;
	
	// constructor
	MapPool()
	{
		this.songPool = new HashMap<Integer, Map>();
		this.tournamentName = new HashMap<String, Map>();
	}

	/**
	 * find if the map has the ID
	 * @param ID
	 * @return
	 */
	public boolean hasMap (Integer ID)
	{
		return this.songPool.containsKey(ID);
	}
	
	/**
	 * find if the map has the ID
	 * @param ID
	 * @return
	 */
	public boolean hasTournament (String ID)
	{
		return this.tournamentName.containsKey(ID);
	}
	
	/**
	 * Inserts map into pool
	 * @param name
	 * @param map
	 */
	public void insertMap(String name, Map map)
	{
		// insert into mod and song pool
		// the key itself is the songID which is shared between the mods and song pool
		this.tournamentName.put(name, map);
		this.songPool.put(map.getID(), map);
	}
	
	public Map getSong(Integer ID)
	{
		return this.songPool.get(ID);
	}
	
	public Map getTournament(String ID)
	{
		return this.tournamentName.get(ID);
	}
}
