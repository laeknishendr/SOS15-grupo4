package sos;

class Consumos {
	String country;
	Double energy_production;
	Double energy_use;
	Double energy_import;
	Long year;

	public Consumos(String c, Double ep, Double eu, Double ei, Long y) {
		super();
		this.country = c;
		this.energy_production = ep;
		this.energy_use = eu;
		this.energy_import = ei;
		this.year = y;
	}

	public Consumos() {

	}
}