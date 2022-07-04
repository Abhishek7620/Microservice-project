package com.codedecode.microservices.CitizenService.Model;

import com.codedecode.microservices.CitizenService.Entity.Citizen;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequiredResponseCitizen {

	private Citizen citizen;
	private VaccinationCenter center;
	public Citizen getCitizen() {
		return citizen;
	}
	public void setCitizen(Citizen citizen) {
		this.citizen = citizen;
	}
	public VaccinationCenter getCenter() {
		return center;
	}
	public void setCenter(VaccinationCenter center) {
		this.center = center;
	}
	
	
	
}
