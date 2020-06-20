package com.chinomars.prony;

public class PronyParameter {
	
	private double amlitude;
	
	private double damping;
	
	private double phase;

	private double frequency;
	
	private int length;
	
	private double t;
	
	private double energy ;

	
	public PronyParameter(int length, double t) {
		super();
		this.length = length;
		this.t = t;
	}

	public double getAmlitude() {
		return amlitude;
	}

	public void setAmlitude(double amlitude) {
		this.amlitude = amlitude;
	}

	public double getDamping() {
		return damping;
	}

	public void setDamping(double damping) {
		this.damping = damping;
	}

	public double getPhase() {
		return phase;
	}

	public void setPhase(double phase) {
		this.phase = phase;
	}

	public double getFrequency() {
		return frequency;
	}

	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}
	
	public double getEnergy() {
		if (this.energy == 0) {
			for (int i = 0; i < length - 1; i++) {
				energy += Math.abs(getFitValue(i) + getFitValue(i + 1));
			}
			energy = energy * t / 2;
		}
		return energy;
	}

	public double getFitValue(int k) {
		return this.getAmlitude() * Math.cos(2 * Math.PI * this.getFrequency() * t * k + this.getPhase())
				* Math.exp(t * k * this.getDamping());
	}
	
	@Override
	public String toString() {
		return "[Amlitude]:" + String.format("%-24s", this.amlitude) + "[Frequency]:"
				+ String.format("%-24s", this.frequency) + "[Damping]:" + String.format("%-24s", this.damping) + "[Phase]"
				+ String.format("%-24s", this.phase) + "============= [energy]:" + this.getEnergy();
	}
}
