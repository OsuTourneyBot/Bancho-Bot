package osu.tournamentData;

public enum Mod {
	NoFail(1, "NF"), Easy(2, "EZ"), Hidden(8, "HD"), HardRock(16, "HR"), SuddenDeath(32, "SD"), DoubleTime(64, "DT"),
	Relax(128, "RX"), HalfTime(256, "HT"), Nightcore(512, "NC"), FlashLight(1024, "FL"), SpunOut(4096, "SO"),
	AutoPilot(8192, "AP"), Perfect(16384, "PF");

	private int value;
	private String shortName;

	private Mod(int value, String shortName) {
		this.value = value;
		this.shortName = shortName;
	}

	public int getValue() {
		return value;
	}

	public static int getValue(Mod[] mods) {
		int res = 0;
		for (Mod mod : mods) {
			res |= mod.getValue();
		}
		return res;
	}

	public static String getNames(int value) {
		String res = "";
		for (Mod mod : Mod.values()) {
			if ((mod.getValue() & value) > 0) {
				res += " "+mod.shortName;
			}
		}
		return res;
	}
}
