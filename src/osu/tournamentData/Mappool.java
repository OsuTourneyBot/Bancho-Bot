/**
 * Need to make maps that uses the beatmap ID as the key
 * Need 2 maps, one that holds mods and the other one that holds the song/link to download
 * 
 * How to use hashmaps in java
 * https://www.w3schools.com/java/java_hashmap.asp 
 */
package osu.tournamentData;

import java.util.HashMap;

/**
 * @author Vince
 *
 */
public class Mappool {
	// stores songs and mods
	private HashMap<Integer, Beatmap> songPool;
	private HashMap<String, Beatmap> shortMapName;

	// constructor
	public Mappool() {
		this.songPool = new HashMap<Integer, Beatmap>();
		this.shortMapName = new HashMap<String, Beatmap>();
	}

	/**
	 * find if the map has the ID
	 * 
	 * @param ID
	 * @return
	 */
	public boolean hasMap(Integer ID) {
		return this.songPool.containsKey(ID);
	}

	/**
	 * find if the map has the ID
	 * 
	 * @param ID
	 * @return
	 */
	public boolean hasShortMapName(String ID) {
		return this.shortMapName.containsKey(ID);
	}

	/**
	 * Inserts map into pool
	 * 
	 * @param name
	 * @param map
	 */
	public void insertMap(String name, Beatmap map) {
		// insert into mod and song pool
		// the key itself is the songID which is shared between the mods and song pool
		this.shortMapName.put(name, map);
		this.songPool.put(map.getID(), map);
	}

	public Beatmap getSong(Integer ID) {
		return this.songPool.get(ID);
	}

	public Beatmap getShortMapName(String ID) {
		return this.shortMapName.get(ID);
	}

	public HashMap<String, Beatmap> getShortMapNames() {
		return this.shortMapName;
	}
}
