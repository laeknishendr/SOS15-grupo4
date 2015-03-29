package sos;

class Emissions{
	String name;
	Double no_fossil;
	Double fossil;
	Double temperature;
	
	public Emissions(String n, Double nf, Double f, Double t){
		super();
		this.name = n;
		this.no_fossil = nf;
		this.fossil = f;
		this.temperature = t;
	}
	
	public Emissions(){
		
	}
}